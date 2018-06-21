package com.my.engine.common.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.my.engine.common.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.team.TeamDataChangedObserver;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.business.contact.core.model.ContactGroupStrategy;
import com.netease.nim.uikit.business.contact.core.provider.ContactDataProvider;
import com.netease.nim.uikit.business.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.business.contact.core.viewholder.ContactHolder;
import com.netease.nim.uikit.business.contact.core.viewholder.LabelHolder;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

public class TeamListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String EXTRA_DATA_ITEM_TYPES = "EXTRA_DATA_ITEM_TYPES";

    private ContactDataAdapter adapter;

    private ListView lvContacts;

    private int itemType;
    private ImageView imageback;

    public static final void start(Context context, int teamItemTypes) {
        Intent intent = new Intent();
        intent.setClass(context, TeamListActivity.class);
        intent.putExtra(EXTRA_DATA_ITEM_TYPES, teamItemTypes);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemType = getIntent().getIntExtra(EXTRA_DATA_ITEM_TYPES, ItemTypes.TEAMS.ADVANCED_TEAM);
        setContentView(R.layout.activity_team_list);

        lvContacts = (ListView) findViewById(R.id.group_list);
        imageback = (ImageView) findViewById(R.id.imageback);
        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GroupStrategy groupStrategy = new GroupStrategy();
        IContactDataProvider dataProvider = new ContactDataProvider(itemType);
        adapter = new ContactDataAdapter(this, groupStrategy, dataProvider) {
            @Override
            protected List<AbsContactItem> onNonDataItems() {
                return null;
            }

            @Override
            protected void onPreReady() {
            }

            @Override
            protected void onPostLoad(boolean empty, String queryText, boolean all) {
            }
        };
        adapter.addViewHolder(ItemTypes.LABEL, LabelHolder.class);
        adapter.addViewHolder(ItemTypes.TEAM, ContactHolder.class);

        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(this);
        lvContacts.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                showKeyboard(false);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });


        // load data
        int count = NIMClient.getService(TeamService.class).queryTeamCountByTypeBlock(itemType == ItemTypes.TEAMS
                .ADVANCED_TEAM ? TeamTypeEnum.Advanced : TeamTypeEnum.Normal);
        if (count == 0) {
            if (itemType == ItemTypes.TEAMS.ADVANCED_TEAM) {
                Toast.makeText(TeamListActivity.this, "您还没有群", Toast.LENGTH_SHORT).show();
            } else if (itemType == ItemTypes.TEAMS.NORMAL_TEAM) {
                Toast.makeText(TeamListActivity.this, "您还没有讨论组", Toast.LENGTH_SHORT).show();
            }
        }
        adapter.load(true);

        registerTeamUpdateObserver(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        registerTeamUpdateObserver(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AbsContactItem item = (AbsContactItem) adapter.getItem(position);
        switch (item.getItemType()) {
            case ItemTypes.TEAM:
                NimUIKit.startTeamSession(this, ((ContactItem) item).getContact().getContactId());
                break;
        }
    }

    private void registerTeamUpdateObserver(boolean register) {
        NimUIKit.getTeamChangedObservable().registerTeamDataChangedObserver(teamDataChangedObserver, register);
    }

    TeamDataChangedObserver teamDataChangedObserver = new TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
            adapter.load(true);
        }

        @Override
        public void onRemoveTeam(Team team) {
            adapter.load(true);
        }
    };

    private static class GroupStrategy extends ContactGroupStrategy {
        GroupStrategy() {
            add(ContactGroupStrategy.GROUP_NULL, 0, ""); // 默认分组
        }

        @Override
        public String belongs(AbsContactItem item) {
            switch (item.getItemType()) {
                case ItemTypes.TEAM:
                    return GROUP_NULL;
                default:
                    return null;
            }
        }
    }

    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
