package com.my.engine.common.clazz;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.CreateTeamResult;
import com.netease.nimlib.sdk.team.model.Team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 未来属于自己
 * 作者：MLG
 * QQ:2264280742
 * 2018-4-12 17:01
 */


public class TeamCreateHelper {

    public static void createAdvancedTeam(final Context context, List<String> memberAccounts) {

        String teamName = "高级群";
        DialogMaker.showProgressDialog(context, context.getString(com.netease.nim.uikit.R.string.empty), true);
        // 创建群
        TeamTypeEnum type = TeamTypeEnum.Advanced;
        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<>();
        fields.put(TeamFieldEnum.Name, teamName);
        NIMClient.getService(TeamService.class).createTeam(fields, type, "",
                memberAccounts).setCallback(new RequestCallback<CreateTeamResult>() {
            @Override
            public void onSuccess(CreateTeamResult createTeamResult) {
                Toast.makeText(context, " 创建成功", Toast.LENGTH_SHORT).show();
                onCreateSuccess(context, createTeamResult);
            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }

    /**
     * 群创建成功回调
     */
    private static void onCreateSuccess(final Context context, CreateTeamResult result) {
        if (result == null) {
            Log.e("TAG", "onCreateSuccess exception: team is null");
            return;
        }
        final Team team = result.getTeam();
        if (team == null) {
            Log.e("TAG", "onCreateSuccess exception: team is null");
            return;
        }

        Log.i("TAG", "create and update team success");

        DialogMaker.dismissProgressDialog();
        // 检查有没有邀请失败的成员
        ArrayList<String> failedAccounts = result.getFailedInviteAccounts();
        if (failedAccounts != null && !failedAccounts.isEmpty()) {
            TeamHelper.onMemberTeamNumOverrun(failedAccounts, context);
        }
        // 向群里插入一条Tip消息，使得该群能立即出现在最近联系人列表（会话列表）中，满足部分开发者需求。
        Map<String, Object> content = new HashMap<>(1);
        content.put("content", "成功创建高级群");
// 创建tip消息，teamId需要开发者已经存在的team的teamId
        IMMessage msg = MessageBuilder.createTipMessage(team.getId(), SessionTypeEnum.Team);
        msg.setRemoteExtension(content);
// 自定义消息配置选项
        CustomMessageConfig config = new CustomMessageConfig();
    // 消息不计入未读
        config.enableUnreadCount = false;
        msg.setConfig(config);
    // 消息发送状态设置为success
        msg.setStatus(MsgStatusEnum.success);
    // 保存消息到本地数据库，但不发送到服务器
        NIMClient.getService(MsgService.class).saveMessageToLocal(msg, true);
        // 发送后，稍作延时后跳转
        new Handler(context.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                NimUIKit.startTeamSession(context, team.getId(), null);
            }
        }, 50);
    }
}
