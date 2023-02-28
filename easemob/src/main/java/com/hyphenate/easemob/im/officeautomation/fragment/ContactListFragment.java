package com.hyphenate.easemob.im.officeautomation.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.ui.EaseBaseFragment;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.im.gray.GrayScaleConfig;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.events.EventFriendNotify;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupNotify;
import com.hyphenate.easemob.imlibs.mp.events.EventOrgsReady;
import com.hyphenate.easemob.imlibs.mp.events.EventOrgsRefresh;
import com.hyphenate.easemob.imlibs.mp.events.EventUsersReady;
import com.hyphenate.easemob.imlibs.mp.events.EventUsersRefresh;
import com.hyphenate.easemob.imlibs.mp.prefs.PrefsUtil;
import com.hyphenate.easemob.im.mp.rest.EMAllOrgRequest;
import com.hyphenate.easemob.im.mp.rest.EMMyOrgUsersRequest;
import com.hyphenate.easemob.im.mp.ui.group.GroupInviteActivity;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.db.InviteMessageDao;
import com.hyphenate.easemob.im.officeautomation.domain.InviteMessage;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.im.officeautomation.ui.FindUserActivity;
import com.hyphenate.easemob.im.officeautomation.ui.GroupsActivity;
import com.hyphenate.easemob.im.officeautomation.ui.MyFriendsActivity;
import com.hyphenate.easemob.im.officeautomation.ui.NewFriendsActivity;
import com.hyphenate.easemob.im.officeautomation.ui.OrgStructureActivity;
import com.hyphenate.easemob.im.officeautomation.ui.SearchActivity;
import com.hyphenate.easemob.im.officeautomation.ui.StarredFriendsActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.utils.PreferenceManager;
import com.hyphenate.easemob.im.officeautomation.widget.CompayOrgItemView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author qby
 * @time 2018/06/07 20:57
 * 通讯录列表  {host}/v1/tenant/{tenantid}/organizations
 */
public class ContactListFragment extends EaseBaseFragment {
    private static final String TAG = "ContactListFragment";
    protected boolean hidden;
    //    private TextView mTvMyOrg;
//    private TextView mTvMyCompany;
    private RelativeLayout mRlNotify;
    //    private RelativeLayout mRlOrgStructure;
    private RelativeLayout mRlGroupNotify;
    private ImageView groupNotifyprompt;
    private RelativeLayout mRlStarFriends;
    private RelativeLayout mRlGroups;
    //    private RelativeLayout mRlMyDepartment;
    private RelativeLayout mRlMyFriends;
    private ExecutorService cacheThreadPool = Executors.newCachedThreadPool();
    private View rlSearch;
    private LinearLayout llCompanyListView;

    private ImageView notifyPrompt;

    private ImageView addFriend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    protected void initView() {
        if (getView() == null) return;
        rlSearch = getView().findViewById(R.id.rl_search);
        mRlStarFriends = getView().findViewById(R.id.rl_star_friends);
        mRlGroups = getView().findViewById(R.id.rl_groups);
        mRlNotify = getView().findViewById(R.id.rl_notify);
        mRlGroupNotify = getView().findViewById(R.id.rl_group_notify);
        groupNotifyprompt = getView().findViewById(R.id.iv_group_notify_prompt);
        mRlMyFriends = getView().findViewById(R.id.rl_my_friends);
        llCompanyListView = getView().findViewById(R.id.ll_company_list);
        notifyPrompt = getView().findViewById(R.id.iv_notify_prompt);
        addFriend = getView().findViewById(R.id.iv_add_friend);
        MPEventBus.getDefault().register(this);
        mRlMyFriends.setVisibility(GrayScaleConfig.useContact ? View.VISIBLE : View.GONE);
    }

    private boolean checkLoginStatus() {
        return AppHelper.getInstance().isLoggedIn();
    }

    @Override
    protected void setUpView() {

        mRlNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyPrompt.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), NewFriendsActivity.class).putExtra("notifyType", "friend"));
            }
        });

        mRlGroupNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupNotifyprompt.setVisibility(View.GONE);
//                startActivity(new Intent(getActivity(), NewFriendsActivity.class).putExtra("notifyType", "group"));
                startActivity(new Intent(getActivity(), GroupInviteActivity.class));
            }
        });

        mRlStarFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLoginStatus()) {
                    tryLogin();
                    return;
                }
                startActivity(new Intent(getActivity(), StarredFriendsActivity.class).putExtra("isStarred", true));
            }
        });
        mRlGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLoginStatus()) {
                    tryLogin();
                    return;
                }
                startActivity(new Intent(getActivity(), GroupsActivity.class));
            }
        });
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });

        mRlMyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyFriendsActivity.class));
            }
        });

        addCompanyListViewContent();
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FindUserActivity.class));
            }
        });

        cacheAll();
        registerCMDReceiver();
    }

    private void addCompanyListViewContent() {
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String jsonEnttyStr = PrefsUtil.getInstance().getLoginAllEntity();
                if (TextUtils.isEmpty(jsonEnttyStr)) {
                    return;
                }
                try {
                    JSONObject jsonEntity = new JSONObject(jsonEnttyStr);
                    JSONArray jsonArrCompay = jsonEntity.optJSONArray("companyList");
                    JSONArray jsonArrOrg = jsonEntity.optJSONArray("organizationList");
                    JSONArray jsonArrUserCompany = jsonEntity.optJSONArray("userCompanyRelationshipList");
                    List<MPOrgEntity> orgEntities = MPOrgEntity.create(jsonArrOrg, jsonArrCompay, jsonArrUserCompany);
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            realAddCompanyListView(orgEntities);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void realAddCompanyListView(List<MPOrgEntity> orgEntities) {
        for (MPOrgEntity item : orgEntities) {
            realAddCompanyViewItem(item);
        }
    }

    private void realAddCompanyViewItem(MPOrgEntity orgEntity) {
        CompayOrgItemView itemView = new CompayOrgItemView(getContext());
        itemView.setCompanyAndOrgInfo(orgEntity);
        itemView.setItemClickListener(new CompayOrgItemView.ICompanyOrgClickListener() {
            @Override
            public void onStructureClicked(int companyId, int orgId) {
                startActivity(new Intent(getActivity(), OrgStructureActivity.class)
                        .putExtra("orgName", orgEntity.getCompanyName())
                        .putExtra("orgId", orgId)
                        .putExtra("companyId", companyId)
                );
            }

            @Override
            public void onOrgClicked(int companyId, int orgId) {
                if (orgId >= 0) {
                    startActivity(new Intent(getActivity(), OrgStructureActivity.class)
                            .putExtra("orgName", orgEntity.getName())
                            .putExtra("myOrg", true)
                            .putExtra("companyId", companyId)
                            .putExtra("orgId", orgId));
                } else {
                    startActivity(new Intent(getActivity(), OrgStructureActivity.class)
                            .putExtra("orgName", orgEntity.getCompanyName())
                            .putExtra("myOrg", true)
                            .putExtra("companyId", companyId)
                            .putExtra("orgId", orgId));
                }

            }
        });
        llCompanyListView.addView(itemView);
    }


    private void tryLogin() {
        int status = PreferenceManager.getInstance().getLastCacheLoginStatus();
        if (status == 0) {
            return;
        }
        if (status == 2 || status == 1) {
            String password = PreferenceManager.getInstance().getLastCacheUsername();
            AppHelper.getInstance().login(PreferenceManager.getInstance().getLastCacheUsername(), password, null);
        }
    }

    private void registerCMDReceiver() {
//        MPEventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideDialog();
//        MPEventBus.getDefault().unregister(this);
    }

    private void cacheAll() {
        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (checkLoginStatus()) {
                    cacheUsers();
                    cacheOrgs();
                }
            }
        });

    }

    private ProgressDialog progressDialog;

    private void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.tip_loading));
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void forceCacheOrgs() {
        cacheOrgs(true);
    }

    private void cacheOrgs() {
        cacheOrgs(false);
    }


    private void cacheUsers() {
        cacheUsers(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUsersRefresh(EventUsersRefresh usersRefresh) {
        cacheUsers();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrgsRefresh(EventOrgsRefresh orgsRefresh) {
        cacheOrgs();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrgsReady(EventOrgsReady orgsReady) {
        hideDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUsersReady(EventUsersReady usersReady) {
        hideDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void groupNotify(EventGroupNotify eventGroupNotify) {
        groupNotifyprompt.setVisibility(View.VISIBLE);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void friendNotification(EventFriendNotify eventFriendNotify) {
        EMMessage message = eventFriendNotify.getMessage();
        EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
        final String action = cmdMsgBody.action();

        InviteMessageDao inviteMessageDao = new InviteMessageDao();
        InviteMessage inviteMessage = new InviteMessage();
        try {
            JSONObject extJson = message.getJSONObjectAttribute("content");
            inviteMessage.setUserId(extJson.getInt("userId"));
            inviteMessage.setFriendId(extJson.getInt("friendId"));
            inviteMessage.setAction(action);
            if (Constant.CMD_ACTION_INVITED_FRIEND.equals(action)) {
                notifyPrompt.setVisibility(View.VISIBLE);
                inviteMessage.setStatus(InviteMessage.InviteMessageStatus.BEINVITEED);
            } else if (Constant.CMD_ACTION_ACCEPT_FRIEND.equals(action)) {

            } else if (Constant.CMD_ACTION_DELETED_FRIEND.equals(action)) {
            }
            inviteMessageDao.saveMessage(inviteMessage);
        } catch (HyphenateException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //缓存组织信息
    private void cacheOrgs(boolean isForce) {
        if (!isForce) {
            long lastCacheTime = PreferenceManager.getInstance().getCachePreTime();
            if (lastCacheTime > 0 && System.currentTimeMillis() - lastCacheTime < 60 * 60 * 1000)
                return;
        }

        if (AppHelper.getInstance().getCompanyOrg() == null) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        showDialog();
                    }
                });
            }
        }
        new EMAllOrgRequest().request();
    }

    //缓存用户信息
    private void cacheUsers(boolean isForce) {
        if (!isForce) {
            long lastTime = PreferenceManager.getInstance().getLastCacheOrgUsersTime();
            if (lastTime > 0 && System.currentTimeMillis() - lastTime < 3 * 60 * 60 * 1000) return;
        }
        new EMMyOrgUsersRequest().request();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MPEventBus.getDefault().unregister(this);
    }
}
