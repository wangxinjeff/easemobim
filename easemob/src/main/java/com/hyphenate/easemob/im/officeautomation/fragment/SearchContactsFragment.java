package com.hyphenate.easemob.im.officeautomation.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.hyphenate.easemob.easeui.ui.EaseBaseFragment;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.adapter.SearchContactsAdapter;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.im.officeautomation.domain.SearchContactsBean;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest;
import com.hyphenate.easemob.im.officeautomation.ui.ContactDetailsActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qby on 2018/06/11.
 * 搜索联系人页面
 */
public class SearchContactsFragment extends EaseBaseFragment {
    private static final String TAG = "SearchContactsFragment";
    private RecyclerView rv;
    private int page;
    private boolean isLastPage;
    private String searchText;
    private SearchContactsAdapter refreshAdapter;
    private List<SearchContactsBean.EntitiesBean> searchContactsList;
    private int lastVisibleItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_contacts, container, false);
    }

    @Override
    protected void initView() {
        rv = getView().findViewById(R.id.rv);
    }

    @Override
    protected void setUpView() {
        searchContactsList = new ArrayList<SearchContactsBean.EntitiesBean>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //不是正在刷新、列表滑动为空闲状态、最下面显示的为最后一条、当前页不是最后一页
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == refreshAdapter.getItemCount() && !isLastPage) {
                    refreshAdapter.changeMoreStatus(SearchContactsAdapter.LOADING_MORE);
                    page++;
                    searchContacts();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取列表最下面一条的索引
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        refreshAdapter = new SearchContactsAdapter(getActivity(), searchContactsList, new SearchContactsAdapter.SearchContactsItemCallback() {
            @Override
            public void onItemClick(int position) {
                SearchContactsBean.EntitiesBean entitiesBean = searchContactsList.get(position);
                startActivity(new Intent(getActivity(), ContactDetailsActivity.class).putExtra("imUserId", entitiesBean.getEasemobName()));
            }
        });
        rv.setAdapter(refreshAdapter);
    }

    public void searchContacts() {
        EMAPIManager.getInstance().getSearchUser(BaseRequest.getTenantId(), searchText, 0, Constant.PAGE_SIZE, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(final String value) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SearchContactsBean searchContactsBean = new Gson().fromJson(value, SearchContactsBean.class);
                        if (searchContactsBean != null) {
                            String status = searchContactsBean.getStatus();
                            if ("OK".equalsIgnoreCase(status)) {
                                int elements = searchContactsBean.getNumberOfElements();
                                isLastPage = searchContactsBean.getLast();
                                if (elements > 0) {
                                    searchContactsList.addAll(searchContactsBean.getEntities());
                                }

                                if (isLastPage)
                                    refreshAdapter.changeMoreStatus(SearchContactsAdapter.LOADING_END);
                                else
                                    refreshAdapter.changeMoreStatus(SearchContactsAdapter.LOADING_MORE);
                            }
                        }
                    }
                });

            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    public void refresh(String searchText) {
        this.searchText = searchText;
        page = 0;
        if (searchContactsList != null)
            searchContactsList.clear();
        if (refreshAdapter != null){
            refreshAdapter.setSearchText(searchText);
            refreshAdapter.notifyDataSetChanged();
        }
        if (!TextUtils.isEmpty(searchText)) {
            List<MPUserEntity> usersList = AppHelper.getInstance().getModel().searchUsersByKeyword(searchText);
            if (usersList != null) {
                isLastPage = true;
                for (MPUserEntity user :
                        usersList) {
                    SearchContactsBean.EntitiesBean entitiesBean = new SearchContactsBean.EntitiesBean();
                    entitiesBean.setId(user.getId());
                    entitiesBean.setRealName(user.getRealName());
                    entitiesBean.setUsername(user.getUsername());
                    entitiesBean.setUserPinyin(user.getPinyin());
                    entitiesBean.setImage(user.getAvatar());
                    entitiesBean.setEasemobName(user.getImUserId());
                    searchContactsList.add(entitiesBean);
                }
                refreshAdapter.notifyDataSetChanged();
            } else {
                isLastPage = false;
                searchContacts();
            }
        }
    }

}
