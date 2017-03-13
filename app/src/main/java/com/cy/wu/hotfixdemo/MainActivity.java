package com.cy.wu.hotfixdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        copyAssetsToFile();
    }

    private void copyAssetsToFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                FileOutputStream fos = null;
                try {
                    inputStream = MainActivity.this.getResources().getAssets().open("user.dex");
                    fos =  openFileOutput("user.dex", MODE_PRIVATE);

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
