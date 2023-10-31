package com.example.blackandwhite;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static File uriToFile(Context context, Uri uri) {
        if (context == null || uri == null) {
            return null;
        }

        File file = null;
        InputStream input = null;
        OutputStream output = null;

        try {
            file = new File(context.getCacheDir(), "temp_file");
            input = context.getContentResolver().openInputStream(uri);
            output = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024];
            int read;

            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}
