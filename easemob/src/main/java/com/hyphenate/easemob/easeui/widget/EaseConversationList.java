package com.hyphenate.easemob.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.adapter.EaseConversationAdapter;
import com.hyphenate.easemob.easeui.delegates.DraftDelegate;
import com.hyphenate.util.EMLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class EaseConversationList extends ListView{
    
    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;
    

    protected static final int MSG_REFRESH_ADAPTER_DATA = 0;
    protected static final int MSG_REFRESH_TO_UNREAD = 1;
    
    protected Context context;
    protected EaseConversationAdapter adapter;
    protected List<EMConversation> conversations = new ArrayList<EMConversation>();
    protected List<EMConversation> passedListRef = null;
    
    
    public EaseConversationList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public EaseConversationList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setDraftListener(DraftDelegate delegate){
        adapter.setDraftListener(delegate);
    }

    
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseConversationList);
        primaryColor = ta.getColor(R.styleable.EaseConversationList_cvsListPrimaryTextColor, getResources().getColor(R.color.list_itease_primary_color));
        secondaryColor = ta.getColor(R.styleable.EaseConversationList_cvsListSecondaryTextColor, getResources().getColor(R.color.list_itease_secondary_color));
        timeColor = ta.getColor(R.styleable.EaseConversationList_cvsListTimeTextColor, getResources().getColor(R.color.list_itease_secondary_color));
        primarySize = ta.getDimensionPixelSize(R.styleable.EaseConversationList_cvsListPrimaryTextSize, 0);
        secondarySize = ta.getDimensionPixelSize(R.styleable.EaseConversationList_cvsListSecondaryTextSize, 0);
        timeSize = ta.getDimension(R.styleable.EaseConversationList_cvsListTimeTextSize, 0);
        
        ta.recycle();
        mWeakHandler = new WeakHandler(this);
    }

    public void init(List<EMConversation> conversationList){
        this.init(conversationList, null);
    }

    public void init(List<EMConversation> conversationList, EaseConversationListHelper helper){
        conversations = conversationList;
        if(helper != null){
            this.conversationListHelper = helper;
        }
        adapter = new EaseConversationAdapter(context, 0, conversationList);
        adapter.setCvsListHelper(conversationListHelper);
        adapter.setPrimaryColor(primaryColor);
        adapter.setPrimarySize(primarySize);
        adapter.setSecondaryColor(secondaryColor);
        adapter.setSecondarySize(secondarySize);
        adapter.setTimeColor(timeColor);
        adapter.setTimeSize(timeSize);
        setAdapter(adapter);
    }

    private WeakHandler mWeakHandler;

    private static class WeakHandler extends android.os.Handler {
        WeakReference<EaseConversationList> weakReference;
        WeakHandler(EaseConversationList conversationList){
            this.weakReference = new WeakReference<>(conversationList);
        }

        private void refreshList(){
            EaseConversationList convList = weakReference.get();
            if (null != convList){
                if (convList.adapter != null){
                    convList.adapter.notifyDataSetChanged();
                }
            }
        }

        private void refreshToUnread() {
            final EaseConversationList conversationList = weakReference.get();
            if (null != conversationList) {
                conversationList.jumpUnread();
            }
        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_REFRESH_ADAPTER_DATA:
                    refreshList();
                    break;
                case MSG_REFRESH_TO_UNREAD:
                    refreshToUnread();
                    break;
            }
        }
    }

    public EMConversation getItem(int position) {
        return adapter.getItem(position);
    }
    
    public void refresh() {
    	if(!mWeakHandler.hasMessages(MSG_REFRESH_ADAPTER_DATA)){
            mWeakHandler.sendEmptyMessage(MSG_REFRESH_ADAPTER_DATA);
    	}
    }

    public void refreshToUnreadConversation(){
        if (!mWeakHandler.hasMessages(MSG_REFRESH_TO_UNREAD)){
            mWeakHandler.sendEmptyMessage(MSG_REFRESH_TO_UNREAD);
        }

    }

    
    public void filter(CharSequence str) {
        adapter.getFilter().filter(str);
    }


    private EaseConversationListHelper conversationListHelper;


    public interface EaseConversationListHelper{
        /**
         * set content of second line
         * @param lastMessage
         * @return
         */
        String onSetItemSecondaryText(EMMessage lastMessage);
    }

    //============================ jump to unread position===================================
    public boolean isListViewReachBottomEdge(){
        boolean result = false;
        if (getLastVisiblePosition() == (getCount() - 1)){
            final View bottomChildView = getChildAt(getLastVisiblePosition() - getFirstVisiblePosition());
            result = (getHeight() >= bottomChildView.getBottom());
            EMLog.d("ConversationList", "" + result);
        }
        return result;
    }


    public void reJumpUnread() {
        for (int i = 0; i < adapter.getCount(); i++){
            if (adapter.getItem(i).getUnreadMsgCount() > 0) {
                // 未读处理
                setSelection(i);
                return;
            }
        }
    }

    public void jumpUnread() {
        int position = getFirstVisiblePosition();
        for (int i = position + 1; i < adapter.getCount(); i++){
            if (conversations.get(i).getUnreadMsgCount() > 0) {
                // 未读处理
                if (isListViewReachBottomEdge()){
                    reJumpUnread();
                } else {
                    setSelection(i);
                }
                return;
            }
        }
        reJumpUnread();
    }

    public void jumpUnread(boolean cycle) {
        int dataSize = conversations.size();

        int position = 0;
        if (cycle) {
            position = 0;
        } else {
            position = getFirstVisiblePosition();
        }

        for (int i = position; i < dataSize; i++) {
            position++;
            if (conversations.get(i).getUnreadMsgCount() > 0) {
                setSelection(position);
                return;
            }
        }

        if (position > 0) {
            jumpUnread(true);
        }
    }




}
