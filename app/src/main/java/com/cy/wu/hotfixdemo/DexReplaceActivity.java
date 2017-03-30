package com.cy.wu.hotfixdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class DexReplaceActivity extends AppCompatActivity {

    public static final String TAG = "DexReplaceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileUtil.copyAssetsToFile(DexReplaceActivity.this, "user.dex");

        Button button = (Button) findViewById(R.id.btn_apply);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir(), "user.dex");
                DexLoadUtil.inject(DexReplaceActivity.this, file.getAbsolutePath());
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassStudent classStudent = new ClassStudent();
                classStudent.setName("Julia");
                Log.d(TAG, "getName" + classStudent.getName());
            }
        });
    }


    //直接从一个本地文件中加载某个类
    private void applyPatch() {
        ClassLoader classLoader = getClassLoader();
        try {
            File file = new File(getFilesDir(), "user.dex");
            File optimize = new File(file.getParent(), "optimizedDirectory");
            if(!optimize.exists()) {
                optimize.mkdirs();
            }

            DexClassLoader dexClassLoader = new DexClassLoader(file.getAbsolutePath(),
                    file.getParent() + "/optimizedDirectory/", "", classLoader);
            Class<?> aClass = dexClassLoader.loadClass("com.cy.wu.hotfixdemo.ClassStudent");//包名加类名加载
            Log.d(TAG, "ClassStudent = " + aClass);

            Object instance = aClass.newInstance();
            Method method = aClass.getMethod("setName", String.class);
            method.invoke(instance, "Sahadev");

            Method getNameMethod = aClass.getMethod("getName");
            Object invoke = getNameMethod.invoke(instance);

            Log.d(TAG, "invoke result = " + invoke);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




}
