package com.hyphenate.easemob.im.officeautomation.ui;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.ui.EaseBaseFragment;
import com.hyphenate.easemob.im.officeautomation.adapter.SearchRecordAdapter;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.adapter.ViewPagerAdapter;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.im.officeautomation.domain.SearchConversation;
import com.hyphenate.easemob.im.officeautomation.domain.SearchMsgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.fragment.SearchAllFragmentV2;
import com.hyphenate.easemob.im.officeautomation.listener.MyTextWatcher;
import com.hyphenate.easemob.im.officeautomation.widget.CaterpillarIndicator;
import com.hyphenate.util.HanziToPinyin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qby on 2018/06/11.
 * 搜索页面
 */
public class SearchActivity extends BaseActivity {

    private static final String TAG = "SearchActivity";
    private String[] titles_zh = new String[]{"全部", "联系人", "群组", "聊天记录"};
    private String[] titles_en = new String[]{"All", "Contacts", "Groups", "Messages"};
    private TextView btnCancel;
    private EditText mSearch;
    private ImageButton mSearchClear;
    private CaterpillarIndicator mSearchTab;
    private ViewPager mSearchViewPager;
    private ViewPagerAdapter vpAdapter;
    private String searchText;
    private SearchAllFragmentV2 searchAllFragment;
    private SearchAllFragmentV2 searchContactsFragment;
    private SearchAllFragmentV2 searchGroupsFragment;
    //    private SearchAllFragment searchAllFragment;
//    private SearchContactsFragment searchContactsFragment;
//    private SearchGroupsFragment searchGroupsFragment;
//    private SearchMessagesFragment searchMessagesFragment;
    private SearchAllFragmentV2 searchMessagesFragment;
    private int currentItem;
    private int pageSize = 100;


    private List<MPUserEntity> searchContactsList = new ArrayList<>();
    private List<MPGroupEntity> searchGroupsList = new ArrayList<>();
    private List<SearchConversation> searchConversationList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ConstraintLayout searchRecordView;
    private ImageView ivRemoveRecord;
    private RecyclerView recyclerView;
    private SearchRecordAdapter adapter;
    private List<String> recordList;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_search);
        initViews();
        initListeners();
        initData();
    }

    private void initViews() {
        btnCancel = findViewById(R.id.tv_cancel);
        mSearch = findViewById(R.id.search);
        Drawable drawable = getResources().getDrawable(R.drawable.ease_icon_search);
        drawable.setBounds(0, 0, getResources().getDimensionPixelSize(R.dimen.dp_15), getResources().getDimensionPixelSize(R.dimen.dp_15));
        mSearch.setCompoundDrawables(drawable, null, null, null);
        mSearchClear = findViewById(R.id.search_clear);
        mSearchTab = findViewById(R.id.search_tab);
        mSearchViewPager = findViewById(R.id.search_view_pager);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        searchRecordView = findViewById(R.id.search_record_view);
        ivRemoveRecord = findViewById(R.id.iv_remove_record);
        recyclerView = findViewById(R.id.recycler_view);

        FlexboxLayoutManager manager = new FlexboxLayoutManager(this);
        manager.setFlexDirection(FlexDirection.ROW);
        manager.setFlexWrap(FlexWrap.WRAP);
        manager.setAlignItems(AlignItems.CENTER);
        manager.setJustifyContent(JustifyContent.FLEX_START);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new SearchRecordAdapter();
        recyclerView.setAdapter(adapter);
        recordList = PreferenceUtils.getInstance().getSearchRecord();
    }

    private void initListeners() {
        mSearchViewPager.setOffscreenPageLimit(4);
        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (getResources().getString(R.string.cancel).equals(btnCancel.getText().toString())) {
                        finish();
                    } else {
                        if (TextUtils.isEmpty(searchText)) {
                            Toast.makeText(SearchActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                        }
                        hideSoftKeyboard();
                        toSearch(searchText, true);
                    }
                    return false;
                }
                return false;
            }
        });
        btnCancel.setOnClickListener(view -> {
                    if (getResources().getString(R.string.cancel).equals(btnCancel.getText().toString())) {
                        finish();
                    } else {
                        if (TextUtils.isEmpty(searchText)) {
                            Toast.makeText(SearchActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        hideSoftKeyboard();
                        mSearch.clearFocus();
                        toSearch(searchText, true);
                    }
                }
        );
        mSearch.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                super.onTextChanged(charSequence, i, i1, i2);
                if (charSequence.length() > 0) {
                    mSearchClear.setVisibility(View.VISIBLE);
                    btnCancel.setText(getResources().getString(R.string.search));
                } else {
                    btnCancel.setText(getResources().getString(R.string.cancel));
                    mSearchClear.setVisibility(View.INVISIBLE);
                }
                searchText = charSequence.toString();
            }
        });
        mSearchClear.setOnClickListener(v -> {
            mSearch.getText().clear();
            searchText = "";
            hideSoftKeyboard();
        });
        mSearchViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                toSearch(searchText, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    recordList = PreferenceUtils.getInstance().getSearchRecord();
                    if(recordList.size() > 0){
                        adapter.setData(recordList);
                        searchRecordView.setVisibility(View.VISIBLE);
                    }
                } else {
                    searchRecordView.setVisibility(View.GONE);
                }

            }
        });

        adapter.setListener(new SearchRecordAdapter.OnItemSelectListener() {
            @Override
            public void onSelect(int position) {
                String searchText = recordList.get(position);
                mSearch.setText(searchText);
                mSearch.setSelection(searchText.length());
                hideSoftKeyboard();
                mSearch.clearFocus();
                toSearch(searchText, true);
            }
        });

        ivRemoveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.getInstance().clearSearchRecord();
                recordList.clear();
                searchRecordView.setVisibility(View.GONE);
            }
        });
    }

    private void initData() {
        String[] titles;
        if (Locale.getDefault().getLanguage().equals("zh")) {
            titles = titles_zh;
        } else {
            titles = titles_en;
        }
        //设置Tab和指示器
        List<CaterpillarIndicator.TitleInfo> titleInfos = new ArrayList<>();
        for (String title : titles) {
            titleInfos.add(new CaterpillarIndicator.TitleInfo(title));
        }

        searchAllFragment = new SearchAllFragmentV2();
        searchContactsFragment = new SearchAllFragmentV2();
        searchGroupsFragment = new SearchAllFragmentV2();
//        searchAllFragment = new SearchAllFragment();
//        searchContactsFragment = new SearchContactsFragment();
//        searchGroupsFragment = new SearchGroupsFragment();
//        searchMessagesFragment = new SearchMessagesFragment();
        searchMessagesFragment = new SearchAllFragmentV2();
        ArrayList<EaseBaseFragment> fragments = new ArrayList<>();
        fragments.add(searchAllFragment);
        fragments.add(searchContactsFragment);
        fragments.add(searchGroupsFragment);
        fragments.add(searchMessagesFragment);

        //设置下方ViewPager适配器
        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        mSearchViewPager.setAdapter(vpAdapter);

        mSearchTab.init(0, titleInfos, mSearchViewPager);
    }

    private void toSearch(String searchText, boolean forceRequest) {
        if(searchText != null){
            if(!TextUtils.isEmpty(searchText.trim())){
                if(!recordList.contains(searchText)){
                    recordList.add(0, searchText);
                    PreferenceUtils.getInstance().saveSearchRecord(recordList.size() > 10 ? recordList.subList(0, 10) : recordList);
                }
            }
        }
        switch (currentItem) {
            case 0:
                if (forceRequest) {
                    searchFromServer(searchText, pageSize, "all");
                } else {
                    searchAllFragment.refresh(searchText, searchGroupsList, searchContactsList, searchConversationList);

                }
                break;
            case 1:
                if (forceRequest) {
                    searchFromServer(searchText, pageSize, "user");
                } else {
                    searchContactsFragment.refresh(searchText, null, searchContactsList, null);
                }
                break;
            case 2:
                if (forceRequest) {
                    searchFromServer(searchText, pageSize, "chatgroup");
                } else {
                    searchGroupsFragment.refresh(searchText, searchGroupsList, null, null);
                }
                break;
            case 3:
                if (forceRequest) {
                    searchMsgStatistics("msg");
                } else {
                    searchMessagesFragment.refresh(searchText, null, null, searchConversationList);
                }
                break;
        }
    }


    private void searchFromServer(String queryContent, int size, String type) {
        if (!progressDialog.isShowing()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }
        searchContactsList.clear();
        searchGroupsList.clear();
        searchConversationList.clear();
        EMAPIManager.getInstance().getGlobalSearch(queryContent, size, type, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                JSONObject result;
                try {
                    result = new JSONObject(value);
                    JSONObject jsonEntity = result.optJSONObject("entity");

                    List<MPUserEntity> userEntities = MPUserEntity.create(jsonEntity.optJSONArray("userList"));
                    List<MPGroupEntity> groupEntities = MPGroupEntity.create(jsonEntity.optJSONArray("chatGroupList"));
                    if ("all".equals(type)) {
                        searchContactsList.addAll(userEntities);
                        if (containSearchText()) {
                            searchContactsList.add(fileTransfer());
                        }
                        searchGroupsList.addAll(groupEntities);

                        searchMsgStatistics("all");
                    } else if ("user".equals(type)) {
                        if (containSearchText()) {
                            searchContactsList.add(fileTransfer());
                        }
                        searchContactsList.addAll(userEntities);
                    } else {
                        searchGroupsList.addAll(groupEntities);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                runOnUiThread(() -> {
                    if(progressDialog != null && progressDialog.isShowing()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        });
                    }
                    if ("user".equals(type) || "chatgroup".equals(type)) {
                        toSearch(searchText, false);
                    }
                    if ("user".equals(type) && searchContactsList.size() == 0) {
                        Toast.makeText(SearchActivity.this, "未查到数据", Toast.LENGTH_SHORT).show();
                    }
                    if ("chatgroup".equals(type) && searchGroupsList.size() == 0) {
                        Toast.makeText(SearchActivity.this, "未查到数据", Toast.LENGTH_SHORT).show();
                    }

                });

            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(() -> {
                    if(progressDialog != null && progressDialog.isShowing()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
    }


    private MPUserEntity fileTransfer() {
        MPUserEntity ft = new MPUserEntity();
        ft.setImUserId(EMClient.getInstance().getCurrentUser() + MPClient.get().getPCResource());
        ft.setRealName(getResources().getString(R.string.file_transfer));
        return ft;
    }

    private boolean containSearchText() {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(searchText);
        if (m.find() && getResources().getString(R.string.file_transfer).contains(searchText)) {
            return true;
        } else {
            List<HanziToPinyin.Token> letters = HanziToPinyin.getInstance().get(getResources().getString(R.string.file_transfer));
            StringBuilder buffer = new StringBuilder();
            for (HanziToPinyin.Token token : letters) {
                buffer.append(token.target.toLowerCase());
            }
            return buffer.toString().contains(searchText);
        }
    }


    private void searchMsgStatistics(String type) {
        if (!progressDialog.isShowing()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }
        searchConversationList.clear();
        long beginTime = System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L);
        long endTime = System.currentTimeMillis();
        String fromId = EMClient.getInstance().getCurrentUser();
        String queryType = "all";
        String msgType = "txt";
        String words = searchText;
        EMAPIManager.getInstance().getSearchMsgStatistics(beginTime, endTime, fromId, queryType, msgType, words, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {

                try {
                    JSONObject result = new JSONObject(value);
                    JSONObject entity = result.getJSONObject("entity");
                    JSONObject entity1 = entity.getJSONObject("entity");
                    SearchMsgEntity msgEntity = new Gson().fromJson(entity1.toString(), SearchMsgEntity.class);
                    searchConversationList.addAll(msgEntity.getSearchMsgEntity());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                    if (searchConversationList.size() == 0 && "msg".equals(type))
                        Toast.makeText(SearchActivity.this, "未查到数据", Toast.LENGTH_SHORT).show();
                    if ("all".equals(type) && searchConversationList.size() == 0 && searchGroupsList.size() == 0 && searchContactsList.size() == 0) {
                        Toast.makeText(SearchActivity.this, "未查到数据", Toast.LENGTH_SHORT).show();
                    }
                    toSearch(searchText, false);
                });
            }

            @Override
            public void onError(int error, String errorMsg) {

                runOnUiThread(() -> {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                    Toast.makeText(SearchActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            hideSoftKeyboard();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //点击更多
    public void onMoreClick(int type) {
        mSearchViewPager.setCurrentItem(type + 1);
    }
}
