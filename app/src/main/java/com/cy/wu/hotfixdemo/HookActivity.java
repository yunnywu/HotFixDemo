package com.cy.wu.hotfixdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Field;

public class HookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook);


        Button button = (Button) findViewById(R.id.btn_start);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(HookActivity.this, ClassLoaderActivity.class);
//                startActivity(intent);

                Handler mHandler = new MyHandler();
                try {
                    Field field = Handler.class.getDeclaredField("mCallback");
                    field.setAccessible(true);
                    field.set(mHandler, new MyCallBack());
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(1);
            }
        });
    }


    class MyCallBack implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Log.d("wcy", "call Handle massage");break;
                default:
                    break;
            }
            return false;
        }
    }



    class MyHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Log.d("wcy", "handleMessage");
                    break;
                default:
                    break;

            }
        }
    }

}
