package io.github.jitinsharma.insplore.Utilities;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jitin on 29/06/16.
 */
public class Utils {

    public static String loadJSONFromAsset(Context context, String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
