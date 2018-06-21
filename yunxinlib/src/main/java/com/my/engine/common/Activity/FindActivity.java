package com.my.engine.common.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.my.engine.common.R;
import com.my.engine.common.clazz.DemoCache;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

public class FindActivity extends AppCompatActivity {

    private TextView action_bar_right_clickable_textview;
    private ClearableEditTextWithIcon search_friend_edit;
    private Toolbar toolbar;
    private ImageView imageback;
    private AppBarLayout app_bar_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        initView();
    }

    @SuppressLint("NewApi")
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageback = (ImageView) findViewById(R.id.imageback);
        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        action_bar_right_clickable_textview = (TextView) findViewById(R.id.action_bar_right_clickable_textview);
        action_bar_right_clickable_textview.setText("搜索");
        search_friend_edit = (ClearableEditTextWithIcon) findViewById(R.id.search_friend_edit);
        action_bar_right_clickable_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_friend_edit.getText().toString())) {
                    Toast.makeText(FindActivity.this, R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                } else if (search_friend_edit.getText().toString().equals(DemoCache.getAccount())) {
                    Toast.makeText(FindActivity.this, "不能添加自己为好友", Toast.LENGTH_SHORT).show();
                } else {
                    query();
                }
            }
        });

    }

    private void query() {
        DialogMaker.showProgressDialog(this, null, false);
        final String account = search_friend_edit.getText().toString().toLowerCase();
        NimUIKit.getUserInfoProvider().getUserInfoAsync(account, new SimpleCallback<NimUserInfo>() {
            @Override
            public void onResult(boolean success, NimUserInfo result, int code) {
                DialogMaker.dismissProgressDialog();
                if (success) {
                    if (result == null) {
                        EasyAlertDialogHelper.showOneButtonDiolag(FindActivity.this, R.string.user_not_exsit,
                                R.string.user_tips, R.string.ok, false, null);
                    } else {
                        AddFriendActivity.start(FindActivity.this, account);
                    }
                } else if (code == 408) {
                    Toast.makeText(FindActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                } else if (code == ResponseCode.RES_EXCEPTION) {
                    Toast.makeText(FindActivity.this, "on exception", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FindActivity.this, "on failed:" + code, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
