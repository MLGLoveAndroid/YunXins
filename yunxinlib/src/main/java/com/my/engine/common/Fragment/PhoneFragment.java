package com.my.engine.common.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.my.engine.common.Activity.FindActivity;
import com.my.engine.common.Activity.FindqunActivity;
import com.my.engine.common.Activity.SystemMessageActivity;
import com.my.engine.common.Activity.TeamListActivity;
import com.my.engine.common.Activity.UserProfileSettingActivity;
import com.my.engine.common.R;
import com.my.engine.common.clazz.DemoCache;
import com.my.engine.common.clazz.ReminderId;
import com.my.engine.common.clazz.ReminderItem;
import com.my.engine.common.clazz.ReminderManager;
import com.my.engine.common.clazz.SystemMessageUnreadManager;
import com.my.engine.common.clazz.TeamCreateHelper;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.contact.ContactsCustomization;
import com.netease.nim.uikit.business.contact.ContactsFragment;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.business.contact.core.viewholder.AbsContactViewHolder;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneFragment extends TFragment implements View.OnClickListener {
    //
    private ImageView tianjia;
    private View mPopView;
    private PopupWindow mPopupWindow;
    private TextView cjq;
    private TextView tjhy;
    private TextView findq;
    private ContactsFragment fragment;
    private static final int REQUEST_CODE_ADVANCED = 2;
    private TextView setting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_phone, container, false);
        initView(inflate);

        return inflate;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADVANCED) {
                ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                TeamCreateHelper.createAdvancedTeam(getActivity(), selected);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tianjia) {
            // 将布局文件转换成View对象，popupview 内容视图
            mPopView = getLayoutInflater().inflate(R.layout.popwindow_layout, null);
            // 将转换的View放置到 新建一个popuwindow对象中
            mPopupWindow = new PopupWindow(mPopView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            // 点击popuwindow外让其消失
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAsDropDown(tianjia);
            cjq = mPopView.findViewById(R.id.cjq);
            tjhy = mPopView.findViewById(R.id.tjhy);
            findq = mPopView.findViewById(R.id.findq);
            setting = mPopView.findViewById(R.id.setting);
            cjq.setOnClickListener(this);
            tjhy.setOnClickListener(this);
            findq.setOnClickListener(this);
            setting.setOnClickListener(this);
        } else if (v.getId() == R.id.cjq) {
            ContactSelectActivity.Option advancedOption = TeamHelper.getCreateContactSelectOption(null, 50);
            NimUIKit.startContactSelector(getActivity(), advancedOption, REQUEST_CODE_ADVANCED);
            mPopupWindow.dismiss();
        } else if (v.getId() == R.id.tjhy) {
            getActivity().startActivity(new Intent(getActivity(), FindActivity.class));
            mPopupWindow.dismiss();
        } else if (v.getId() == R.id.findq) {
            FindqunActivity.start(getActivity());
            mPopupWindow.dismiss();
        } else if (v.getId() == R.id.setting) {
            UserProfileSettingActivity.start(getActivity(), DemoCache.getAccount());
            mPopupWindow.dismiss();
        }

    }

    private void initView(View inflate) {
        tianjia = inflate.findViewById(R.id.tianjia);
        tianjia.setOnClickListener(this);
    }

    /**
     * ******************************** 功能项定制 ***********************************
     */

    final static class FuncItem extends AbsContactItem {
        static final FuncItem VERIFY = new FuncItem();
        static final FuncItem ADVANCED_TEAM = new FuncItem();

        @Override
        public int getItemType() {
            return ItemTypes.FUNC;
        }

        @Override
        public String belongsGroup() {
            return null;
        }

        public static final class FuncViewHolder extends AbsContactViewHolder<FuncItem> {
            private ImageView image;
            private TextView funcName;
            private TextView unreadNum;

            @Override
            public View inflate(LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.contacts_list, null);
                this.image = (ImageView) view.findViewById(R.id.img_head);
                this.funcName = (TextView) view.findViewById(R.id.tv_func_name);
                this.unreadNum = (TextView) view.findViewById(R.id.tab_new_msg_label);
                return view;
            }

            @Override
            public void refresh(ContactDataAdapter contactAdapter, int position, FuncItem item) {

                if (item == VERIFY) {
                    funcName.setText("验证提醒");
                    image.setImageResource(R.drawable.icon_verify_remind);
                    image.setScaleType(ImageView.ScaleType.FIT_XY);
                    int unreadCount = SystemMessageUnreadManager.getInstance().getSysMsgUnreadCount();
                    updateUnreadNum(unreadCount);

                    ReminderManager.getInstance().registerUnreadNumChangedCallback(new ReminderManager.UnreadNumChangedCallback() {
                        @Override
                        public void onUnreadNumChanged(ReminderItem item) {
                            if (item.getId() != ReminderId.CONTACT) {
                                return;
                            }
                            updateUnreadNum(item.getUnread());
                        }
                    });
                } else if (item == ADVANCED_TEAM) {
                    funcName.setText("高级群");//ic_advanced_team
                    image.setImageResource(R.drawable.ic_advanced_team);

                }
                if (item != VERIFY) {
                    image.setScaleType(ImageView.ScaleType.FIT_XY);
                    unreadNum.setVisibility(View.GONE);
                }
            }

            private void updateUnreadNum(int unreadCount) {
                // 2.*版本viewholder复用问题
                if (unreadCount > 0 && funcName.getText().toString().equals("验证提醒")) {
                    unreadNum.setVisibility(View.VISIBLE);
                    unreadNum.setText("" + unreadCount);
                } else {
                    unreadNum.setVisibility(View.GONE);
                }
            }
        }

        static List<AbsContactItem> provide() {
            List<AbsContactItem> items = new ArrayList<AbsContactItem>();
            items.add(VERIFY);
            items.add(ADVANCED_TEAM);
            return items;
        }

        static void handle(Context context, AbsContactItem item) {
            if (item == VERIFY) {
                SystemMessageActivity.start(context);
            } else if (item == ADVANCED_TEAM) {
                TeamListActivity.start(context, ItemTypes.TEAMS.ADVANCED_TEAM);
            }
        }
    }

    /**
     * ******************************** 生命周期 ***********************************
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerSystemMessageObservers(true);
        onInit(); // 触发onInit，提前加载
    }

    protected void onInit() {
        addContactFragment();  // 集成通讯录页面
    }

    private void addContactFragment() {
        fragment = new ContactsFragment();
        fragment.setContainerId(R.id.contact_fragment);

        UI activity = (UI) getActivity();

        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
        fragment = (ContactsFragment) activity.addFragment(fragment);

        // 功能项定制
        fragment.setContactsCustomization(new ContactsCustomization() {
            @Override
            public Class<? extends AbsContactViewHolder<? extends AbsContactItem>> onGetFuncViewHolderClass() {
                return FuncItem.FuncViewHolder.class;
            }

            @Override
            public List<AbsContactItem> onGetFuncItems() {
                return FuncItem.provide();
            }

            @Override
            public void onFuncItemClick(AbsContactItem item) {
                FuncItem.handle(getActivity(), item);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerSystemMessageObservers(false);
    }

    /**
     * 注册/注销系统消息未读数变化
     *
     * @param register
     */
    private void registerSystemMessageObservers(boolean register) {
        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(sysMsgUnreadCountChangedObserver,
                register);
    }

    private Observer<Integer> sysMsgUnreadCountChangedObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer unreadCount) {
            SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unreadCount);
            ReminderManager.getInstance().updateContactUnreadNum(unreadCount);
        }
    };

}
