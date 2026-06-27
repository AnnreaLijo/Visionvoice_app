package com.example.echovision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class RecognizeActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView personNameText;

    private File photoFile;
    private HashMap<String, Bitmap> familyImages = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        imageView = findViewById(R.id.imageView);
        personNameText = findViewById(R.id.personNameText);

        // Load family images from server
        loadFamilyImages();
    }

    // 🔹 Capture image on Volume Up button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            photoFile = new File(getExternalFilesDir(null), "captured.jpg");
            Uri photoURI = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, 100);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 🔹 Handle captured photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap capturedImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            imageView.setImageBitmap(capturedImage);

            detectFace(capturedImage);
        }
    }

    // 🔹 Load family images from PHP server
    private void loadFamilyImages() {
        String url = config.baseurl+"fetch_family.php"; // change IP
        new Thread(() -> {
            try {
                URL u = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONArray arr = new JSONArray(sb.toString());
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String name = obj.getString("personname");
                    String photoUrl = config.imgurl + obj.getString("personimage");

                    Bitmap bmp = BitmapFactory.decodeStream(new URL(photoUrl).openStream());
                    familyImages.put(name, bmp);
                }

                runOnUiThread(() -> Toast.makeText(this, "Family images loaded", Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to load family images", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // 🔹 Detect face using ML Kit
    private void detectFace(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .build();

        FaceDetector detector = FaceDetection.getClient(options);

        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (faces.size() > 0) {
                        Face face = faces.get(0);
                        Bitmap faceBitmap = cropFace(bitmap, face);
                        matchWithFamily(faceBitmap);
                    } else {
                        personNameText.setText("No Face Detected");
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    personNameText.setText("Face Detection Failed");
                });
    }

    // 🔹 Crop face from image
    private Bitmap cropFace(Bitmap bitmap, Face face) {
        Rect box = face.getBoundingBox();

        int x = Math.max(box.left, 0);
        int y = Math.max(box.top, 0);
        int width = Math.min(box.width(), bitmap.getWidth() - x);
        int height = Math.min(box.height(), bitmap.getHeight() - y);

        return Bitmap.createBitmap(bitmap, x, y, width, height);
    }

    // 🔹 Match captured face with family images
    private void matchWithFamily(Bitmap captured) {
        for (String name : familyImages.keySet()) {
            Bitmap familyBmp = familyImages.get(name);

            if (compareHistograms(captured, familyBmp)) {
                personNameText.setText("Matched: " + name);
                return;
            }
        }
        personNameText.setText("No Match Found");
    }

    // 🔹 Histogram comparison
    private boolean compareHistograms(Bitmap bmp1, Bitmap bmp2) {
        if (bmp1 == null || bmp2 == null) return false;

        Bitmap resized1 = Bitmap.createScaledBitmap(bmp1, 100, 100, true);
        Bitmap resized2 = Bitmap.createScaledBitmap(bmp2, 100, 100, true);

        int[] hist1 = getHistogram(resized1);
        int[] hist2 = getHistogram(resized2);

        double score = 0;
        for (int i = 0; i < hist1.length; i++) {
            score += Math.min(hist1[i], hist2[i]);
        }

        double similarity = score * 1.0 / (resized1.getWidth() * resized1.getHeight());
        Log.d("HIST", "Similarity: " + similarity);

        return similarity > 0.6; // threshold (tune this)
    }

    // 🔹 Create histogram (grayscale)
    private int[] getHistogram(Bitmap bmp) {
        int[] hist = new int[256];

        for (int x = 0; x < bmp.getWidth(); x++) {
            for (int y = 0; y < bmp.getHeight(); y++) {
                int pixel = bmp.getPixel(x, y);
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                int gray = (r + g + b) / 3;
                hist[gray]++;
            }
        }
        return hist;
    }
}
