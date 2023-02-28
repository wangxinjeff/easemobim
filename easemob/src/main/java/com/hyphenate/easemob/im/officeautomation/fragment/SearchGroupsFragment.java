package com.hyphenate.easemob.im.officeautomation.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.ui.EaseBaseFragment;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupsChanged;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.adapter.SearchGroupsAdapter;
import com.hyphenate.easemob.im.officeautomation.ui.ChatActivity;
import com.hyphenate.easemob.im.officeautomation.ui.SearchActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by qby on 2018/06/11.
 * 搜索群组页面
 */
public class SearchGroupsFragment extends EaseBaseFragment {

    private static final String TAG = "SearchGroupsFragment";
    private RecyclerView rv;
    private ArrayList<GroupBean> searchGroupsList;
    private SearchGroupsAdapter refreshAdapter;
    private String searchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_groups, container, false);
    }

    @Override
    protected void initView() {
        if (getView() == null) return;
        rv = getView().findViewById(R.id.rv);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setUpView() {
        searchGroupsList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        registerGroupChangeReceiver();

        //适配
        refreshAdapter = new SearchGroupsAdapter(getActivity(), searchGroupsList, new SearchGroupsAdapter.SearchGroupsItemCallback() {
            @Override
            public void onGroupClick(int position) {
                //获取点击的群组信息
                GroupBean searchGroupBean = searchGroupsList.get(position);
                // enter group chat
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                // it is group chat
                intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                intent.putExtra("userId", searchGroupBean.getImGroupId());
                startActivity(intent);
            }
        });
        rv.setAdapter(refreshAdapter);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupChanged(EventGroupsChanged groupsChanged){
        if (getActivity() != null && EaseCommonUtils.getTopActivity(getActivity()).equals(SearchActivity.class.getName())) {
            refresh(searchText);
        }
    }


    void registerGroupChangeReceiver() {
        if (getActivity() == null) return;
        MPEventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MPEventBus.getDefault().unregister(this);
    }

    public void searchGroups() {
        refreshAdapter.setSearchText(searchText);
        ArrayList<GroupBean> extGroupList = AppHelper.getInstance().getModel().getExtGroupList();
        if (extGroupList != null) {
            synchronized (extGroupList) {
                for (GroupBean group : extGroupList) {
                    String nick = group.getNick();
                    if (!TextUtils.isEmpty(nick) && nick.contains(searchText)) {
                        searchGroupsList.add(group);
                    }
                }
                refreshAdapter.notifyDataSetChanged();
            }
        } else {
//            List<EMGroup> allGroups = EMClient.getInstance().groupManager().getAllGroups();
//            synchronized (allGroups) {
//                for (EMGroup emGroup : allGroups) {
//                    String groupName = emGroup.getGroupName();
//                    if (groupName.contains(searchText)) {
//                        String groupId = emGroup.getGroupId();
//                        GroupBean groupInfo = EaseUserUtils.getGroupInfo(groupId);
//                        if (groupInfo != null) {
//                            searchGroupsList.add(groupInfo);
//                        }
//                    }
//                }
//                refreshAdapter.notifyDataSetChanged();
//            }
        }
    }

    public void refresh(String searchText) {
        this.searchText = searchText;
        if (searchGroupsList != null)
            searchGroupsList.clear();
        if (refreshAdapter != null)
            refreshAdapter.notifyDataSetChanged();
        if (!TextUtils.isEmpty(searchText)) {
            searchGroups();
        }
    }

}
