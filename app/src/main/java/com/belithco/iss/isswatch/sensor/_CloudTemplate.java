package com.belithco.iss.isswatch.sensor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Expresses simple template.
 */
public abstract class _CloudTemplate {
    private static final String TAG = _CloudTemplate.class.getSimpleName();

    /**
     *
     * @return
     */
    public abstract String getJSonData();

    /**
     * Utility function to convert the stream to a string.
     *
     * @param is
     * @return the converted string.
     */
    protected static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "", e);
            }
        }
        return sb.toString();
    }
}