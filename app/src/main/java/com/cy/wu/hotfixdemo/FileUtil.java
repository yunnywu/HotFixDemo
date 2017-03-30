package com.cy.wu.hotfixdemo;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wcy8038 on 2017/3/30.
 */

public class FileUtil {


    private static final String TAG = "wcy";

    public static void copyAssetsToFile(final Context context, final String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                FileOutputStream fos = null;
                try {
                    inputStream = context.getResources().getAssets().open(fileName);
                    fos =  context.openFileOutput(fileName, Context.MODE_PRIVATE);

                    byte[] buffer =new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1){
                        fos.write(buffer,0 ,len);
                    }
                    Log.d(TAG, "copy done!");
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(inputStream != null){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(fos != null){
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }).start();
    }
}
