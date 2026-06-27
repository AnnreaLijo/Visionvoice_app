package com.example.echovision;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class facial_expression_detection extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private PreviewView previewView;
    private TextView objectName;
    private ExecutorService cameraExecutor;
    private boolean isProcessing = false;

    private TextToSpeech tts;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_expression_detection);

        previewView = findViewById(R.id.previewViewf);
        objectName = findViewById(R.id.objectNamef);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // Initialize TextToSpeech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                tts.setSpeechRate(0.95f);
                tts.setPitch(1.1f);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            previewView.post(this::startCamera);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                    if (!isProcessing) {
                        isProcessing = true;

                        Bitmap bitmap = toBitmap(image);
                        if (bitmap != null) {
                            String base64Image = bitmapToBase64(bitmap);
                            detectBreedUsingGroq(base64Image);
                        } else {
                            runOnUiThread(() -> {
                                objectName.setText("Camera Error");
                                isProcessing = false;
                            });
                        }
                    }
                    image.close();
                });

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void detectBreedUsingGroq(String base64Image) {
        base64Image = base64Image.trim().replaceAll("\\s+", "");
        String apiUrl = "https://api.groq.com/openai/v1/chat/completions";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("model", config.yolomodel);
            requestBody.put("temperature", 1);
            requestBody.put("max_tokens", 1024);
            requestBody.put("top_p", 1);
            requestBody.put("stream", false);
            requestBody.put("stop", JSONObject.NULL);

            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");

            JSONArray contentArray = new JSONArray();
            JSONObject textPart = new JSONObject();
            textPart.put("type", "text");
            textPart.put("text", "Classify the facial expression in this image as one of: Happy, Sad, Angry, Surprised, Neutral, Fearful, or Disgusted. Respond with only the emotion name.");
            contentArray.put(textPart);

            JSONObject imagePart = new JSONObject();
            JSONObject imageUrl = new JSONObject();
            imageUrl.put("url", "data:image/jpeg;base64," + base64Image);
            imagePart.put("type", "image_url");
            imagePart.put("image_url", imageUrl);
            contentArray.put(imagePart);

            userMessage.put("content", contentArray);
            messages.put(userMessage);

            requestBody.put("messages", messages);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody,
                    response -> {
                        try {
                            JSONArray choices = response.getJSONArray("choices");
                            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                            String detected = message.getString("content").trim();
                            runOnUiThread(() -> {
                                objectName.setText("Detected: " + detected);
                                if (tts != null) {
                                    tts.speak(detected, TextToSpeech.QUEUE_FLUSH, null, null);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> objectName.setText("Error parsing result"));
                        } finally {
                            isProcessing = false;
                        }
                    },
                    error -> {
                        runOnUiThread(() -> objectName.setText(" Error: " + error.getMessage()));
                        Log.e("GroqError", error.toString());
                        isProcessing = false;
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + config.yolo);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() -> objectName.setText("JSON Error"));
            isProcessing = false;
        }
    }

    private Bitmap toBitmap(ImageProxy imageProxy) {
        @SuppressLint("UnsafeOptInUsageError") Image image = imageProxy.getImage();
        if (image == null) return null;

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21,
                image.getWidth(), image.getHeight(), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 60, out); // 60% quality

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream); // Compress more for API safety
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                // Stop TTS
                if (tts != null) tts.stop();

                // Navigate back to Home
                startActivity(new android.content.Intent(this, Home.class));
                finish(); // Finish Object Detection activity
                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:
                // Optional: handle volume up if needed
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    private void runYoloDetection(Uri imageUri) {

        try {
            String modelPath = "yolov5s.tflite";
            byte[] placeholderInput = new byte[0];

            String[] images = {"YOLO", "Error running YOLO detection"};
            int randomIndex = (int) (Math.random() * images.length);

            String result = "Predicted : " + images[randomIndex];
            Log.d("ML", result);


        } catch (Exception e) {
            Log.e("ML", "This placeholder ML code is not meant to run.", e);
        }
    }
}

