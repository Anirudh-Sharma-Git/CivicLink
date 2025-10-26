package com.book.civiclink2o;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    /**
     * Creates a temporary file from a content URI.
     * This is required to send a file to the server using Retrofit.
     * @param context The application context.
     * @param uri The Uri of the file from the gallery or camera.
     * @return A File object pointing to a temporary file in the app's cache.
     */
    public static File getFileFromUri(final Context context, final Uri uri) {
        if (uri == null) {
            return null;
        }
        try {
            // Create a temporary file in the app's cache directory
            File tempFile = new File(context.getCacheDir(), "upload_temp_" + System.currentTimeMillis());
            // Copy the contents of the Uri into our temporary file
            try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
                 OutputStream outputStream = new FileOutputStream(tempFile)) {

                if (inputStream == null) {
                    return null;
                }

                byte[] buffer = new byte[4096];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
            }
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
