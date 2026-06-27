package com.example.echovision;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class Splashactivitynew extends AppCompatActivity {

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);

                // Welcome message
                String message = "Welcome to VisionVoice. " +
                        "This app helps visually impaired users. " +
                        "Features include object detection, sign board detection, facial expression recognition, and route guidance. " +
                        "Navigating to the chooseuser page.";

                // Speak message
                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "welcomeMsg");

                // After TTS finishes, navigate to HomeActivity
                tts.setOnUtteranceProgressListener(new android.speech.tts.UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {}

                    @Override
                    public void onDone(String utteranceId) {
                        runOnUiThread(() -> {
//                            startActivity(new Intent(Splashactivity.this, chooseactivity.class));
                            startActivity(new Intent(Splashactivitynew.this, chooseactivity.class));


                            finish();
                        });
                    }

                    @Override
                    public void onError(String utteranceId) {}
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
