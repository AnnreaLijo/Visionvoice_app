package com.example.echovision;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Locale;

public class chooseactivity extends AppCompatActivity {

    private CardView blind, family;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private String instructionMsg = "Please say Blind for blind mode, or Family for family login.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooseactivity);

        blind = findViewById(R.id.blind);
        family = findViewById(R.id.family);

        // Initialize TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                // Speak the instructions
                tts.speak(instructionMsg, TextToSpeech.QUEUE_FLUSH, null, "chooseMsg");

                // After TTS finishes, start listening
                tts.setOnUtteranceProgressListener(new android.speech.tts.UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {}

                    @Override
                    public void onDone(String utteranceId) {
                        runOnUiThread(() -> startVoiceListening());
                    }

                    @Override
                    public void onError(String utteranceId) {}
                });
            } else {
                Toast.makeText(this, "TTS Not Supported", Toast.LENGTH_SHORT).show();
            }
        });

        // Card click listeners
        blind.setOnClickListener(v -> navigateBlind());
        family.setOnClickListener(v -> navigateFamily());
    }

    // Navigate to Home (Blind)
    private void navigateBlind() {
        speak("Blind mode selected");
        Intent intent = new Intent(chooseactivity.this, Home.class);
        intent.putExtra("tts_message", "Welcome Blind User");
        startActivity(intent);
    }

    // Navigate to Family login
    private void navigateFamily() {
        speak("Family login selected");
        Intent intent = new Intent(chooseactivity.this, Family_member_login.class);
        intent.putExtra("tts_message", "Welcome Family User, please login");
        startActivity(intent);
    }

    // Speak using TTS
    private void speak(String message) {
        if (tts != null) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    // Voice recognition for Blind/Family
    private void startVoiceListening() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Blind or Family");

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onError(int error) { speakAndRetry(); }
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String choice = matches.get(0).toLowerCase();
                    if (choice.contains("blind")) {
                        navigateBlind();
                    } else if (choice.contains("family")) {
                        navigateFamily();
                    } else {
                        speakAndRetry();
                    }
                }
            }
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });

        speechRecognizer.startListening(intent);
    }

    // Retry if voice not recognized
    private void speakAndRetry() {
        tts.speak("I did not understand. " + instructionMsg, TextToSpeech.QUEUE_FLUSH, null, "retryMsg");
        startVoiceListening();
    }

    // Volume button repeats instructions
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            speak(instructionMsg);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
