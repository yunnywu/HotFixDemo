package com.cy.wu.hotfixdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ResReplaceActivity extends BaseActivity {

    Button mBtnReplace;

    Button mBtnGetString;

    TextView mTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_replace);

        FileUtil.copyAssetsToFile(ResReplaceActivity.this, "res1.apk");

        mBtnReplace = (Button) findViewById(R.id.btn_update_res);
        mBtnGetString = (Button) findViewById(R.id.btn_set_string);
        mTvContent = (TextView) findViewById(R.id.tv_content);

        mBtnReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir(), "res1.apk");
                if(file.exists()){
                    loadResources(file.getAbsolutePath());

                    DexLoadUtil.inject(ResReplaceActivity.this, file.getAbsolutePath());
                }

            }
        });

        mBtnGetString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Class uiClass = Class.forName("com.cy.wu.resapp1.UIUtil");
                    Method method = uiClass.getDeclaredMethod("getStringId");
                    int resId = (int) method.invoke(null);
                    Log.d("wcy" , "resid = " + resId);

                    mTvContent.setText(getResources().getText(resId));

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        });


    }
}
