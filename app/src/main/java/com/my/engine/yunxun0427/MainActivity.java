package com.my.engine.yunxun0427;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button but;
    private static final String LOGINACTIVITY = "/Login/LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        but = (Button) findViewById(R.id.but);

        but.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but:
                ARouter.getInstance().build(LOGINACTIVITY).navigation();
                break;
        }
    }
}
