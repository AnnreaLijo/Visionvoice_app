package com.example.echovision;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    TextView resultTextView;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultTextView = findViewById(R.id.resultTextView);

        // Get detected text from Intent
        String detectedText = getIntent().getStringExtra("detectedText");
        if (detectedText != null && !detectedText.isEmpty()) {
            resultTextView.setText(detectedText);
        }

        // Initialize TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);

                // Set listener to detect when speech finishes
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) { }

                    @Override
                    public void onDone(String utteranceId) {
                        runOnUiThread(() -> {
                            if (tts != null) {
                                tts.stop();
                            }
                            // Navigate back to sign_board_detection
                            Intent intent = new Intent(ResultActivity.this, sign_board_detection.class);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String utteranceId) { }
                });

                // Speak detected text
                tts.speak(detectedText, TextToSpeech.QUEUE_FLUSH, null, "RESULT_DONE");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tts != null) {
            tts.stop();
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (tts != null) tts.stop();
                startActivity(new android.content.Intent(this, Home.class));
                finish();
                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}

