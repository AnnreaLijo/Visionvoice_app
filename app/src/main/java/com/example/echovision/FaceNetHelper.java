package com.example.echovision;

import android.graphics.Bitmap;

public class FaceNetHelper {

    // This is a placeholder: You must load TFLite FaceNet model to generate embeddings
    public static float[] getFaceEmbedding(Bitmap faceBitmap) {
        // TODO: Use TensorFlow Lite model here
        // For demo, return a dummy vector
        float[] embedding = new float[128];
        for(int i=0;i<128;i++) embedding[i] = (float)Math.random();
        return embedding;
    }

    public static String floatArrayToString(float[] arr) {
        StringBuilder sb = new StringBuilder();
        for(float f: arr) sb.append(f).append(",");
        return sb.toString();
    }

    public static float[] stringToFloatArray(String str) {
        String[] parts = str.split(",");
        float[] arr = new float[parts.length];
        for(int i=0;i<parts.length;i++){
            arr[i] = Float.parseFloat(parts[i]);
        }
        return arr;
    }
}
