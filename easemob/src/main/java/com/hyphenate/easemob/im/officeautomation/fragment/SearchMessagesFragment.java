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

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.ui.EaseBaseFragment;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.adapter.SearchMessagesAdapter;
import com.hyphenate.easemob.im.officeautomation.domain.SearchMessagesBean;
import com.hyphenate.easemob.im.officeautomation.ui.ChatActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.widget.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by qby on 2018/06/11.
 * 搜索聊天记录页面
 */
public class SearchMessagesFragment extends EaseBaseFragment {
    private RecyclerView rv;
    private SearchMessagesAdapter refreshAdapter;
    private ArrayList<SearchMessagesBean> searchMessagesList;
    private String searchText;
    private ExecutorService cacheThreadPool = Executors.newCachedThreadPool();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_messages, container, false);
    }

    @Override
    protected void initView() {
        rv = getView().findViewById(R.id.rv);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setUpView() {
        searchMessagesList = new ArrayList<>();
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
        refreshAdapter = new SearchMessagesAdapter(getActivity(), searchMessagesList, new SearchMessagesAdapter.SearchMessagesItemCallback() {
            @Override
            public void onMessageClick(int position) {
                SearchMessagesBean searchMessagesBean = searchMessagesList.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userId", searchMessagesBean.getEasemobName());
                if (searchMessagesBean.getType() != EMMessage.ChatType.Chat) {
                    intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                }
                if (searchMessagesBean.getMessageId() != null){
                    intent.putExtra(EaseConstant.EXTRA_SELECTED_MSGID, searchMessagesBean.getMessageId());
                }
                startActivity(intent);
            }
        });
        rv.setAdapter(refreshAdapter);
    }

    public void searchMessages() {
        final Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
//        ArrayList<SearchMessagesBean> searchRet = new ArrayList<>();
        searchMessagesList.clear();
        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (allConversations) {
                    for (EMConversation conversation : allConversations.values()) {
                        String easemobName = conversation.conversationId();
                        String avatar = "";
                        String userName = "";
                        String realName = "";
                        String content = "";
                        String selectedMsgId = null;
                        EMMessage.ChatType chatType;

                        EMConversation.EMConversationType type = conversation.getType();
                        if (type == EMConversation.EMConversationType.Chat){
                            EaseUser userInfo = EaseUserUtils.getUserInfo(easemobName);
                            if (userInfo != null){
                                realName = userInfo.getNick();
                                avatar = userInfo.getAvatar();
                            }
                            chatType = EMMessage.ChatType.Chat;
                        }else{
                            GroupBean groupBean = EaseUserUtils.getGroupInfo(easemobName);
                            if (groupBean != null){
                                realName = groupBean.getNick();
                                avatar = groupBean.getAvatar();
                            }
                            chatType = EMMessage.ChatType.GroupChat;
                        }
                        ArrayList<EMMessage> realMsgs = new ArrayList<>();
                        List<EMMessage> resultList = conversation.searchMsgFromDB(searchText, System.currentTimeMillis(), 500, null, EMConversation.EMSearchDirection.UP);
                        for (EMMessage message: resultList){
                            if (message.getType() != EMMessage.Type.TXT){
                                continue;
                            }
                            EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                            String text = body.getMessage();
                            if (text.contains(searchText)){
                                realMsgs.add(message);
                                content = text;
                                selectedMsgId = message.getMsgId();
                            }
                        }
                        if (!realMsgs.isEmpty()){
                            if (realMsgs.size() > 1){
                                content =  String.format(getString(R.string.search_has_message), realMsgs.size());
                            }
                            SearchMessagesBean searchMessagesBean = new SearchMessagesBean();
                            searchMessagesBean.setType(chatType);
                            searchMessagesBean.setAvatar(avatar);
                            searchMessagesBean.setUserName(userName);
                            searchMessagesBean.setRealName(realName);
                            searchMessagesBean.setContent(content);
                            searchMessagesBean.setEasemobName(easemobName);
                            searchMessagesBean.setMessageId(selectedMsgId);
                            searchMessagesList.add(searchMessagesBean);
                        }
                    }

                    if (getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
        refreshAdapter.notifyDataSetChanged();
    }

    public void refresh(String searchText) {
        this.searchText = searchText;
        if (searchMessagesList != null)
            searchMessagesList.clear();
        if (refreshAdapter != null)
            refreshAdapter.notifyDataSetChanged();
        if (!TextUtils.isEmpty(searchText)) {
            searchMessages();
        }
    }

}
