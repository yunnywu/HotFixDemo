package com.cy.wu.hotfixdemo;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by wcy8038 on 2017/3/30.
 */

public class DexLoadUtil {

    public static String inject(Context context, String apkPath) {
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

            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
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


    public static Object getPathList(BaseDexClassLoader classLoader) {
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

    public static Object getDexElements(Object object) {
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

    public static Object combineArray(Object object, Object object2) {
        Class<?> aClass = Array.get(object, 0).getClass();

        Object obj = Array.newInstance(aClass, 2);

        Array.set(obj, 0, Array.get(object2, 0));
        Array.set(obj, 1, Array.get(object, 0));

        return obj;
    }

    public static void setField(Object pathList, Class aClass, String fieldName, Object fieldValue) {

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
}
