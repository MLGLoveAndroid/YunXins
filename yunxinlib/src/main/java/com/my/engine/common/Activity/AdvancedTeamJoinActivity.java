package com.my.engine.common.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.my.engine.common.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

public class AdvancedTeamJoinActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String EXTRA_ID = "EXTRA_ID";

    private String teamId;
    private Team team;

    private TextView teamNameText;
    private TextView memberCountText;
    private TextView teamTypeText;
    private Button applyJoinButton;
    private ImageView imageback;

    public static void start(Context context, String teamId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ID, teamId);
        intent.setClass(context, AdvancedTeamJoinActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_team_join);

        findViews();
        parseIntentData();
        requestTeamInfo();
    }

    private void findViews() {
        //421641347
        teamNameText = (TextView) findViewById(R.id.team_name);
        imageback = (ImageView) findViewById(R.id.imageback);
        memberCountText = (TextView) findViewById(R.id.member_count);
        applyJoinButton = (Button) findViewById(R.id.apply_join);
        teamTypeText = (TextView) findViewById(R.id.team_type);
        applyJoinButton.setOnClickListener(this);
        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void parseIntentData() {
        teamId = getIntent().getStringExtra(EXTRA_ID);
    }

    private void requestTeamInfo() {
        Team t = NimUIKit.getTeamProvider().getTeamById(teamId);
        if (t != null) {
            updateTeamInfo(t);
        } else {
            NimUIKit.getTeamProvider().fetchTeamById(teamId, new SimpleCallback<Team>() {
                @Override
                public void onResult(boolean success, Team result, int code) {
                    if (success && result != null) {
                        updateTeamInfo(result);
                    }
                }
            });
        }
    }

    /**
     * 更新群信息
     *
     * @param t 群
     */
    private void updateTeamInfo(final Team t) {
        if (t == null) {
            Toast.makeText(AdvancedTeamJoinActivity.this, R.string.team_not_exist, Toast.LENGTH_LONG).show();
            finish();
        } else {
            team = t;
            teamNameText.setText(team.getName());
            memberCountText.setText(team.getMemberCount() + "人");
            if (team.getType() == TeamTypeEnum.Advanced) {
                teamTypeText.setText(R.string.advanced_team);
            } else {
                teamTypeText.setText(R.string.normal_team);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (team != null) {
            NIMClient.getService(TeamService.class).applyJoinTeam(team.getId(), null).setCallback(new RequestCallback<Team>() {
                @Override
                public void onSuccess(Team team) {
                    applyJoinButton.setEnabled(false);
                    String toast = "成功加入群";
                    Toast.makeText(AdvancedTeamJoinActivity.this, toast, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(int code) {
                    if (code == 808) {
                        applyJoinButton.setEnabled(false);
                        Toast.makeText(AdvancedTeamJoinActivity.this, "申请已发出",
                                Toast.LENGTH_SHORT).show();
                    } else if (code == 809) {
                        applyJoinButton.setEnabled(false);
                        Toast.makeText(AdvancedTeamJoinActivity.this, "已经在群里",
                                Toast.LENGTH_SHORT).show();
                    } else if (code == 806) {
                        applyJoinButton.setEnabled(false);
                        Toast.makeText(AdvancedTeamJoinActivity.this, "群数量已达上限",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdvancedTeamJoinActivity.this, "failed, error code =" + code,
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
