package com.my.engine.common.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.my.engine.common.R;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

public class FindqunActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageback;
    private TextView action_bar_right_clickable_textview;
    private ClearableEditTextWithIcon team_search_edittext;

    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FindqunActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findqun);
        initView();
        initActionbar();
    }

    private void initView() {
        imageback = (ImageView) findViewById(R.id.imageback);
        imageback.setOnClickListener(this);
        action_bar_right_clickable_textview = (TextView) findViewById(R.id.action_bar_right_clickable_textview);
        action_bar_right_clickable_textview.setText("搜索");
        team_search_edittext = (ClearableEditTextWithIcon) findViewById(R.id.team_search_edittext);
    }

    private void initActionbar() {
        action_bar_right_clickable_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(team_search_edittext.getText().toString())) {
                    Toast.makeText(FindqunActivity.this, R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                } else {
                    queryTeamById();
                }
            }
        });
    }

    private void queryTeamById() {
        NIMClient.getService(TeamService.class).searchTeam(team_search_edittext.getText().toString()).setCallback(new RequestCallback<Team>() {
            @Override
            public void onSuccess(Team team) {
                updateTeamInfo(team);
            }

            @Override
            public void onFailed(int code) {
                if (code == 803) {
                    Toast.makeText(FindqunActivity.this, "群号不存在", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(FindqunActivity.this, "search team failed: " + code, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(FindqunActivity.this, "search team exception：" + exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
       if (v.getId()== R.id.imageback)
    finish();
}

    /**
     * 搜索群组成功的回调
     *
     * @param team 群
     */
    private void updateTeamInfo(Team team) {
        if (team.getId().equals(team_search_edittext.getText().toString())) {
            AdvancedTeamJoinActivity.start(FindqunActivity.this, team.getId());
        }

    }
}
