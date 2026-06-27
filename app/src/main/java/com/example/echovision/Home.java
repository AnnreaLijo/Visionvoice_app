package com.example.echovision;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private TextView voiceResultText;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize TextView for displaying spoken command
        voiceResultText = findViewById(R.id.voiceResultText);

        // Initialize TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                speak("Welcome to the VisionVoice Home Page. Press the volume up button to choose an option, or press the volume down button to exit. From any activity, pressing the volume down button will return you to the Home Page.");
            }
        });

        // Initialize Speech Recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onError(int error) {
                speak("I didn't catch that. Please press volume up and say object, sign, expression , map   or  face.");
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String command = matches.get(0).trim().toLowerCase();

                    // Show spoken word in TextView
                    voiceResultText.setText(command);

                    // Remove the text after 2 seconds
                    handler.postDelayed(() -> voiceResultText.setText(""), 2000);

                    switch (command) {
                        case "object":
                        case "object detection":
                            speak("Opening Object Detection");
                            startActivity(new Intent(Home.this, Speech_Cameraopen_objectdetection.class));
                            break;

                        case "sign":
                        case "signboard detection":
                            speak("Opening Signboard Detection");
                            startActivity(new Intent(Home.this, sign_board_detection.class));
                            break;

                        case "expression":
                        case "facial expression detection":
                            speak("Opening Facial Expression Detection");
                            startActivity(new Intent(Home.this, facial_expression_detection.class));
                            break;


                        case "map":
                        case "go route":
                            speak("Go route");
                            startActivity(new Intent(Home.this, MapActivity.class));
                            break;

                        case "face":
                        case "face detection":
                            speak("face detection");
                            startActivity(new Intent(Home.this, Person_DetectionActivity.class));
                            break;



                        default:
                            speak("Invalid option. Please say object for Object Detection, sign for Signboard Detection, face for Facial Expression Detection or Face Detection.");
                            break;
                    }
                }
            }

            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    // Handle Volume button clicks
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                speak("Please choose an option: object , sign , face, map,name .");
                startVoiceRecognition();
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                speak("Exiting VisionVoice. Goodbye.");
                Toast.makeText(this, "Exiting app...", Toast.LENGTH_SHORT).show();
                finishAffinity();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.startListening(intent);
    }

    private void speak(String message) {
        if (tts != null) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }
}
