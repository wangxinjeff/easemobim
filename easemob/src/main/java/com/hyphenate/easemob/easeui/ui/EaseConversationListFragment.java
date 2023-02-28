package com.hyphenate.easemob.easeui.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.widget.EaseConversationList;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * conversation list fragment
 */
public abstract class EaseConversationListFragment extends EaseBaseFragment {
    private static final String TAG = "EaseConversationListFragment";
    private final static int MSG_REFRESH = 2;
    //    protected EditText query;
//    protected ImageButton clearSearch;
    private boolean hidden;
    protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
    protected EaseConversationList conversationListView;
    protected LinearLayout layoutPcStatus;
    protected FrameLayout errorItemContainer;
    private Executor cacheExecutor = Executors.newCachedThreadPool();

    private boolean isConflict;

    protected EMConversationListener convListener = new EMConversationListener() {

        @Override
        public void onCoversationUpdate() {
            EMLog.d(TAG, "onCoversationUpdate");
            refresh();
        }

    };
    protected RelativeLayout mRlSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ease_fragment_conversation_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
        EMClient.getInstance().chatManager().addConversationListener(convListener);
        EMClient.getInstance().chatManager().addMessageListener(mEMMessageListener);
    }

    private EMMessageListener mEMMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            MPLog.e(TAG, "onMessageReceived-size:" + list.size());
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };

    @Override
    protected void initView() {
        if (getActivity() == null) return;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        conversationListView = getView().findViewById(R.id.list);
        errorItemContainer = getView().findViewById(R.id.fl_error_item);
        layoutPcStatus = getView().findViewById(R.id.ll_pc_status);
        mRlSearch = getView().findViewById(R.id.rl_search);
        titleBar.setRightImageResource(R.drawable.icon_more_white);
        conversationListView.setEmptyView(getView().findViewById(R.id.empty_view));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setUpView() {
        EMClient.getInstance().addConnectionListener(connectionListener);
        conversationListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });
    }


    private EMConnectionListener connectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE || error == EMError.SERVER_SERVICE_RESTRICTED
                    || error == EMError.USER_KICKED_BY_CHANGE_PASSWORD || error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                isConflict = true;
            } else {
                handler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };

    @SuppressLint("HandlerLeak")
    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    onConnectionDisconnected();
                    break;
                case 1:
                    onConnectionConnected();
                    break;

                case MSG_REFRESH: {
                    if (getActivity() == null) {
                        return;
                    }
                    conversationList.clear();
                    conversationList.addAll(loadConversationList());
                    if (conversationListView != null) {
                        conversationListView.refresh();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };


    private boolean connectedState;

    /**
     * connected to server
     */
    private void onConnectionConnected() {
        isConflict = false;
        connectedState = true;
        errorItemContainer.setVisibility(View.GONE);
    }

    /**
     * disconnected with server
     */
    protected void onConnectionDisconnected() {
        connectedState = false;
        checkNetwork(new EMCallBack() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorItemContainer.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onError(int i, String s) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorItemContainer.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }


    private final Object mutex = new Object();

    private void checkNetwork(final EMCallBack callBack) {
        cacheExecutor.execute(new Runnable() {
            @Override
            public void run() {
                EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
                EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody("check");
                cmdMessageBody.deliverOnlineOnly(true);
                message.addBody(cmdMessageBody);
                message.setTo("admin");
                message.setMessageStatusCallback(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        connectedState = true;
                        synchronized (mutex) {
                            mutex.notify();
                        }

                    }

                    @Override
                    public void onError(int i, String s) {
                        connectedState = false;
                        synchronized (mutex) {
                            mutex.notify();
                        }
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
                MessageUtils.sendMessage(message);
                synchronized (mutex) {
                    try {
                        mutex.wait(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (connectedState) {
                        if (callBack != null) {
                            callBack.onSuccess();
                        }
                    } else {
                        if (callBack != null) {
                            callBack.onError(EMError.GENERAL_ERROR, "send failed");
                        }
                    }
                }


            }
        });

    }

    /**
     * refresh ui
     */
    public void refresh() {
        if (!handler.hasMessages(MSG_REFRESH)) {
            handler.sendEmptyMessage(MSG_REFRESH);
        }
    }

    /**
     * load conversation list
     *
     * @return +
     */
    protected abstract List<EMConversation> loadConversationList();

    public void refreshToUnreadConversation() {
        if (conversationListView != null) {
            conversationListView.refreshToUnreadConversation();
        }
    }

    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden && !isConflict) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().removeConnectionListener(connectionListener);
        EMClient.getInstance().chatManager().removeConversationListener(convListener);
        EMClient.getInstance().chatManager().removeMessageListener(mEMMessageListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isConflict) {
            outState.putBoolean("isConflict", true);
        }
    }
}
