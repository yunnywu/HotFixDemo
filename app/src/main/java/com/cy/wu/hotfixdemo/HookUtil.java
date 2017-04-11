package com.cy.wu.hotfixdemo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wcy8038 on 2017/3/17.
 */

public class HookUtil {

    private static Object getActivityThread() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Method method = activityThreadClass.getDeclaredMethod("currentActivityThread");
            method.setAccessible(true);
            Object activityThread = method.invoke(null,new Object[0]);
            return activityThread;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Instrumentation getInstrumentation(){

        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = getActivityThread();
            Field field = activityThreadClass.getDeclaredField("mInstrumentation");
            field.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) field.get(activityThread);
            return instrumentation;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void hookInstrumentation(){
        Instrumentation realInstrumentation = getInstrumentation();
        MyInstrumentation myInstrumentation = new MyInstrumentation(realInstrumentation);
        Object activityThread = getActivityThread();
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field field = activityThreadClass.getDeclaredField("mInstrumentation");
            field.setAccessible(true);
            field.set(activityThread, myInstrumentation);
            Log.d("wcy", "hookInstrumentation success");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static class MyInstrumentation extends Instrumentation{

        Instrumentation mRealInstrumentation;

        public MyInstrumentation(Instrumentation realInstrumentation) {
            mRealInstrumentation = realInstrumentation;
        }

        public ActivityResult execStartActivity(
                Context who, IBinder contextThread, IBinder token, Activity target, Intent intent,
                int requestCode, Bundle options) {
            try {
                Class[] types = new Class[]{Context.class, IBinder.class, IBinder.class,
                        Activity.class, Intent.class, int.class, android.os.Bundle.class};
                Method method = Instrumentation.class.getMethod("execStartActivity", types);

                Log.d("wcy", "execStartActivity START!!!");
                if(intent.getComponent().getClassName().equals("com.cy.wu.resapp1.MainActivity")){
                    intent.setComponent(new ComponentName("com.cy.wu.hotfixdemo", "com.cy.wu.hotfixdemo.FakeActivity"));
                }

                return (ActivityResult) method.invoke(mRealInstrumentation, new Object[]{who,
                        contextThread, token, target, intent,requestCode,options});
            } catch (NoSuchMethodException e){
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public static void hookHCallBack(){
        Object thread = getActivityThread();
        Handler handler = (Handler) getDeclaredObject(thread,"mH");
        try {
            Field field = Handler.class.getDeclaredField("mCallback");
            field.setAccessible(true);
            field.set(handler, new MyHandlerCallback());
            Log.d("wcy", "hookHCallBack done");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    static class MyHandlerCallback implements Handler.Callback{

        public static final int LAUNCH_ACTIVITY         = 100;

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what){
                case LAUNCH_ACTIVITY:
                    Object object =  msg.obj;
                    Intent intent = (Intent) getDeclaredObject(object, "intent");
                    String className = "";
                    if(intent != null && intent.getComponent() != null){
                        className = intent.getComponent().getClassName();
                        if("com.cy.wu.hotfixdemo.FakeActivity".equals(className)) {
//                            ActivityInfo activityInfo = new ActivityInfo();
                            ActivityInfo originAi = (ActivityInfo) getDeclaredObject(object, "activityInfo");
                            originAi.targetActivity = "com.cy.wu.resapp1.MainActivity";
//                            activityInfo.applicationInfo = originAi
                            setDeclaredObject(object, "activityInfo", originAi);
                        }
                    }

                    Log.d("wcy", "startActivity : " +  className);
                    break;
                default:
                    break;
            }
            return false;
        }


    }



    public static Object getDeclaredObject(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void setDeclaredObject(Object object, String fieldName, Object newObj) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, newObj);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Object getDeclaredFiled(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
