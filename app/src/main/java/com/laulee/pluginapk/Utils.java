package com.laulee.pluginapk;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by laulee on 2020-03-04.
 */
public class Utils {

    public static String copyAssetAndWrite(Context context, String fileName) {

        try {
            File cacheDir = context.getCacheDir();
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File outFile = new File(cacheDir, fileName);
            if (!outFile.exists()) {
                boolean res = outFile.createNewFile();
                if (res) {
                    InputStream inputStream = context.getAssets().open(fileName);
                    FileOutputStream fileOutputStream = new FileOutputStream(outFile);

                    byte[] buffer = new byte[inputStream.available()];
                    int byteCount;

                    while ((byteCount = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteCount);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();

                    return outFile.getAbsolutePath();
                }
            }
            return outFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
