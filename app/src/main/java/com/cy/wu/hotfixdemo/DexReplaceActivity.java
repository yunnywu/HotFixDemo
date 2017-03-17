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
        copyAssetsToFile();

        Button button = (Button) findViewById(R.id.btn_apply);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir(), "user.dex");
                inject(file.getAbsolutePath());
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

    public String inject(String apkPath) {
        boolean hasBaseDexClassLoader = true;

        File file = new File(apkPath);
        try {
            Class.forName("dalvik.system.BaseDexClassLoader");
        } catch (ClassNotFoundException e) {
            hasBaseDexClassLoader = false;
        }
        if (hasBaseDexClassLoader) {
            File optimize = new File(file.getParent(), "optimizedDirectory");
            if(!optimize.exists()) {
                optimize.mkdirs();
            }

            PathClassLoader pathClassLoader = (PathClassLoader) getClassLoader();
            DexClassLoader dexClassLoader = new DexClassLoader(apkPath, file.getParent() + "/optimizedDirectory/", "", pathClassLoader);
            try {
                Object dexElements = combineArray(getDexElements(getPathList(pathClassLoader)), getDexElements(getPathList(dexClassLoader)));
                Object pathList = getPathList(pathClassLoader);
                setField(pathList, pathList.getClass(), "dexElements", dexElements);
                return "SUCCESS";
            } catch (Throwable e) {
                e.printStackTrace();
                return android.util.Log.getStackTraceString(e);
            }
        }
        return "SUCCESS";
    }


    public Object getPathList(BaseDexClassLoader classLoader) {
        Class<? extends BaseDexClassLoader> aClass = classLoader.getClass();

        Class<?> superclass = aClass.getSuperclass();
        try {

            Field pathListField = superclass.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object object = pathListField.get(classLoader);

            return object;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getDexElements(Object object) {
        if (object == null)
            return null;

        Class<?> aClass = object.getClass();
        try {
            Field dexElements = aClass.getDeclaredField("dexElements");
            dexElements.setAccessible(true);
            return dexElements.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

    public Object combineArray(Object object, Object object2) {
        Class<?> aClass = Array.get(object, 0).getClass();

        Object obj = Array.newInstance(aClass, 2);

        Array.set(obj, 0, Array.get(object2, 0));
        Array.set(obj, 1, Array.get(object, 0));

        return obj;
    }

    public void setField(Object pathList, Class aClass, String fieldName, Object fieldValue) {

        try {
            Field declaredField = aClass.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(pathList, fieldValue);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void copyAssetsToFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                FileOutputStream fos = null;
                try {
                    inputStream = DexReplaceActivity.this.getResources().getAssets().open("user.dex");
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
