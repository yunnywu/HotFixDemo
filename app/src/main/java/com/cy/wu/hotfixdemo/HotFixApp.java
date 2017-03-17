package com.cy.wu.hotfixdemo;

import android.app.Application;

/**
 * Created by wcy8038 on 2017/3/17.
 */

public class HotFixApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        HookUtil.hookInstrumentation();

        HookUtil.hookHCallBack();
    }
}
