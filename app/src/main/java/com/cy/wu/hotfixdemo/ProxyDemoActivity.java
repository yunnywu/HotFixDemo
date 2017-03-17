package com.cy.wu.hotfixdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyDemoActivity extends AppCompatActivity {

    BuyInterface buyInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy_demo);


        buyInterface = new BuyImplement();


        Button button = (Button) findViewById(R.id.btn_buy);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyInterface.buySomeThing();
            }
        });

        Button mBtnProxyBuy = (Button) findViewById(R.id.btn_proxy_buy);

        mBtnProxyBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyInterface proxyInstance = (BuyInterface) Proxy.newProxyInstance(
                        ProxyDemoActivity.class.getClassLoader(), new Class[]{BuyInterface.class},
                        new BuyInvokeHandler(buyInterface));

                proxyInstance.buySomeThing();

            }
        });





    }


    interface BuyInterface{
        void buySomeThing();
    }

    class BuyImplement implements BuyInterface{

        @Override
        public void buySomeThing() {
            Log.d("wcy", "buy a bread");
        }
    }


    class BuyInvokeHandler implements InvocationHandler {

        BuyInterface mBuyInterface;

        public BuyInvokeHandler(BuyInterface buyInterface) {
            mBuyInterface = buyInterface;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Log.d("wcy" , method.getName());

            Log.d("wcy" ,"buy a book");
            method.invoke(mBuyInterface, args);
            Log.d("wcy" , "buy a apple");
            return null;
        }
    }
}
