package com.cy.wu.hotfixdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ClassLoaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_loader);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassLoader loader = ClassLoaderActivity.class.getClassLoader();
                while (loader != null) {
                    System.out.println(loader.toString());
                    loader = loader.getParent();
                }
            }
        });
    }
}
