package com.my.engine.common.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.my.engine.common.Adapter.FragmentAdapter;
import com.my.engine.common.Fragment.CallFragment;
import com.my.engine.common.Fragment.PhoneFragment;
import com.my.engine.common.R;
import com.netease.nim.uikit.common.activity.UI;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends UI implements View.OnClickListener {

    private ViewPager viewpager;
    private Button hh;
    private Button txl;
    private ArrayList<Fragment> list = new ArrayList<>();
    public PhoneFragment phoneFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
    }

    private void initView() {
        list.add(new CallFragment());
        phoneFragment = new PhoneFragment();
        list.add(phoneFragment);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), list);
        viewpager.setAdapter(fragmentAdapter);
        viewpager.setCurrentItem(0);
        hh = (Button) findViewById(R.id.hh);
        txl = (Button) findViewById(R.id.txl);

        hh.setOnClickListener(this);
        txl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.hh) {
            viewpager.setCurrentItem(0);
        } else if (v.getId() == R.id.txl) {
            viewpager.setCurrentItem(1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
