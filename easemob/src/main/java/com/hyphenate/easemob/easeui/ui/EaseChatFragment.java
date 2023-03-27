package com.hyphenate.easemob.easeui.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.blankj.utilcode.util.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.adapter.EMAChatRoomManagerListener;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseMessageUtils;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseEmojicon;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.easeui.player.MediaManager;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.EaseAlertDialog;
import com.hyphenate.easemob.easeui.widget.EaseAlertDialog.AlertDialogUser;
import com.hyphenate.easemob.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easemob.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easemob.easeui.widget.EaseChatInputMenu.ChatInputMenuListener;
import com.hyphenate.easemob.easeui.widget.EaseChatInputMenuFire;
import com.hyphenate.easemob.easeui.widget.EaseChatMessageList;
import com.hyphenate.easemob.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.easemob.easeui.widget.EaseVoiceRecorderView.EaseVoiceRecorderCallback;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsManager;
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsResultAction;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.prefs.PrefsUtil;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.co.namee.permissiongen.PermissionGen;

/**
 * you can new an EaseChatFragment to use or you can inherit it to expand.
 * You need call setArguments to pass chatType and userId
 * <br/>
 * <br/>
 * you can see ChatActivity in demo for your reference
 */
public abstract class EaseChatFragment extends EaseBaseFragment implements EMMessageListener {
    protected static final String TAG = "EaseChatFragment";
    //    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_GROUP_DETAIL = 5;

    protected static final int MSG_TYPING_BEGIN = 0;
    protected static final int MSG_TYPING_END = 1;

    protected static final String ACTION_TYPING_BEGIN = "TypingBegin";
//    protected static final String ACTION_TYPING_END = "TypingEnd";

    protected static final int TYPING_SHOW_TIME = 3000;

    private static final int TYPING_DEPLAY_TIME = 2000;

    /**
     * params to fragment
     */
    protected Bundle fragmentArgs;
    protected int chatType;
    protected String toChatUsername;
    protected EaseChatMessageList messageList;
    protected EaseChatInputMenu inputMenu;
    protected EaseChatInputMenuFire inputMenuFire;

    protected EMConversation conversation;

    protected InputMethodManager inputManager;
    protected ClipboardManager clipboard;

    protected Handler handler = new Handler();
    protected File cameraFile;
    protected EaseVoiceRecorderView voiceRecorderView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;
    protected View locTipViewContainer;
    protected TextView tvTipViewTxt;
    private View kickedForOfflineLayout;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 20;
    protected GroupListener groupListener;
    protected ChatRoomListener chatRoomListener;
    protected EMMessage contextMenuMessage;

    protected static final int ITEM_TAKE_PICTURE = 1;
    protected static final int ITEM_PICTURE = 2;
    protected int[] itemStrings = { R.string.attach_picture, R.string.attach_take_pic };
    protected int[] itemdrawables = { R.drawable.ease_chat_image_selector, R.drawable.ease_chat_takepic_selector };
    protected int[] itemIds = {ITEM_PICTURE, ITEM_TAKE_PICTURE };
    protected boolean isMessageListInited;
    protected MyItemClickListener extendMenuItemClickListener;
    protected boolean isRoaming = false;
    private ExecutorService fetchQueue;
    // to handle during-typing actions.
    private Handler typingHandler = null;
    // "正在输入"功能的开关，打开后本设备发送消息将持续发送cmd类型消息通知对方"正在输入"
    private boolean turnOnTyping;
    protected HashMap<String, EMMessage> checkMap;
    protected LinearLayout mLlMultiChoice;
    protected ImageView mTvMultiForward, mTvMultiDelete, mTvMultiCollection;
    protected String[] forwardItems;
    protected String[] forwardItems_zh = new String[]{"逐条转发", "合并转发"};
    protected String[] forwardItems_en = new String[]{"One-by-One Forward", "Combine and Forward"};
    private String selectMsgId;
    protected boolean isRegion;
    protected final List<AtUserBean> mAtUserBeans = new ArrayList<>(); // 存储所@的用户信息
    private boolean useLocalMsg = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            MediaManager.getManager().register(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, boolean roaming) {
        isRoaming = roaming;
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        fragmentArgs = getArguments();
        // check if single chat or group chat
        chatType = fragmentArgs.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        // userId you are chat with or group id
        toChatUsername = fragmentArgs.getString(EaseConstant.EXTRA_USER_ID);
        selectMsgId = fragmentArgs.getString(EaseConstant.EXTRA_SELECTED_MSGID);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            isRegion = !MessageUtils.isCommonRegionWithMe(toChatUsername);
        }
        this.turnOnTyping = turnOnTyping();
        super.onActivityCreated(savedInstanceState);
    }

    protected boolean turnOnTyping() {
        return false;
    }



    protected boolean checkPermission(String[] mPermissions) {
        boolean isPermission = true;
        for (int i =0; i < mPermissions.length; i ++){
            if (ContextCompat.checkSelfPermission(getActivity(), mPermissions[i]) != PackageManager.PERMISSION_GRANTED){
                isPermission = false;
            }
        }

        if (!isPermission) {
            ActivityCompat.requestPermissions(getActivity(), mPermissions, 300);
        }
        return isPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * init view
     */
    protected void initView() {
        // hold to record voice
        //noinspection ConstantConditions
        voiceRecorderView = (EaseVoiceRecorderView) getView().findViewById(R.id.voice_recorder);
        locTipViewContainer = getView().findViewById(R.id.loc_tip_container);
        tvTipViewTxt = getView().findViewById(R.id.tv_loc_txt);
        // message list layout
        messageList = (EaseChatMessageList) getView().findViewById(R.id.message_list);
        if (chatType != EaseConstant.CHATTYPE_SINGLE)
            messageList.setShowUserNick(true);
//        messageList.setAvatarShape(1);
        listView = messageList.getListView();

        kickedForOfflineLayout = getView().findViewById(R.id.layout_alert_kicked_off);
        kickedForOfflineLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onChatRoomViewCreation();
            }
        });

        extendMenuItemClickListener = new MyItemClickListener();
        inputMenu = (EaseChatInputMenu) getView().findViewById(R.id.input_menu);
        inputMenuFire = getView().findViewById(R.id.input_menu_fire);
        mLlMultiChoice = getView().findViewById(R.id.ll_multi_choice);
        mTvMultiForward = getView().findViewById(R.id.tv_multi_forward);
        mTvMultiDelete = getView().findViewById(R.id.tv_multi_delete);
        mTvMultiCollection = getView().findViewById(R.id.tv_multi_collection);
        registerExtendMenuItem();
        // init input menu
        inputMenu.init(null);
        inputMenu.setChatInputMenuListener(new ChatInputMenuListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
                // send action:TypingBegin cmd msg.
                typingHandler.sendEmptyMessage(MSG_TYPING_BEGIN);
                onEditTextChanged(s, start, before, count);
            }

            @Override
            public boolean onSendMessage(String content) {
                return sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderCallback() {

                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }

            @Override
            public void onStickerClicked(EaseEmojicon emojicon) {
                onRealStickerClicked(emojicon);
            }

            @Override
            public boolean onCheckVoicePermission() {
                return checkPermission(new String[]{Manifest.permission.RECORD_AUDIO});
            }

            @Override
            public void onRecommendPhotoClicked(String path) {
                sendImageMessage(path);
            }

            @Override
            public void onAddStickerClicked() {
                onRealAddStickerClicked();

            }

            @Override
            public void onCancelReference() {
                onCancelReferenceCLicked();
            }
        });
        inputMenuFire.setChatInputMenuListener(new EaseChatInputMenuFire.ChatInputMenuFireListener() {
            @Override
            public boolean onSendMessage(String content) {
                return sendTextMessage(content);
            }

            @Override
            public void onImageClicked() {
                selectPicOnlyFromLocal();
            }

            @Override
            public void onFireClosed() {
                setInputMenuFire(false);
            }
        });

        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (isRoaming) {
            fetchQueue = Executors.newSingleThreadExecutor();
        }

        // to handle during-typing actions.
        typingHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_TYPING_BEGIN: // Notify typing start

                        if (!turnOnTyping) return;

                        // Only support single-chat type conversation.
                        if (chatType != EaseConstant.CHATTYPE_SINGLE)
                            return;

                        long currentTime = System.currentTimeMillis();
                        if (latestTypingTime > 0 && (currentTime - latestTypingTime) < TYPING_DEPLAY_TIME) {
                            return;
                        }
                        latestTypingTime = currentTime;

                        // Send TYPING-BEGIN cmd msg
                        EMMessage beginMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
                        EMCmdMessageBody body = new EMCmdMessageBody(ACTION_TYPING_BEGIN);
                        // Only deliver this cmd msg to online users
                        body.deliverOnlineOnly(true);
                        beginMsg.addBody(body);
                        beginMsg.setTo(toChatUsername);
                        MessageUtils.sendMessage(beginMsg);
                        break;
                    case MSG_TYPING_END:
                        EaseUser userInfo = EaseUserUtils.getUserInfo(toChatUsername);
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.getNick())) {
                            titleBar.setTitle(userInfo.getNick());
                        } else {
                            titleBar.setTitle(toChatUsername);
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                        break;
                }
            }
        };
    }

    private static long latestTypingTime = 0;

    protected void onRealAddStickerClicked() {
    }

    protected void onRealStickerClicked(EaseEmojicon emojicon) {
    }

    /**
     * 取消引用点击事件
     */
    protected abstract void onCancelReferenceCLicked();

    protected void setInputMenuFire(boolean enable) {
        if (enable) {
            inputMenuFire.setVisibility(View.VISIBLE);
            inputMenu.setVisibility(View.GONE);
            inputMenuFire.clearTxt();
            inputMenuFire.showKeyboard();
        } else {
            inputMenuFire.setVisibility(View.GONE);
            inputMenu.setVisibility(View.VISIBLE);
            inputMenu.getPrimaryMenu().getEditText().requestFocus();
        }
    }

    /**
     * 检测当前是否正在显示阅后即焚
     *
     * @return
     */
    protected boolean isInputMenuFire() {
        return inputMenuFire.getVisibility() == View.VISIBLE;
    }

    protected void setUpView() {
        checkMap = new HashMap<>();
        setChatTitle(toChatUsername);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            titleBar.setRightImageResource(R.drawable.ease_default_avatar);
        } else {
            titleBar.setRightImageResource(R.drawable.ease_groups_icon);
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                //group chat

                // listen the event that user moved out group or group is dismissed
                groupListener = new GroupListener();
                EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);
            } else {
                chatRoomListener = new ChatRoomListener();
                EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomListener);
                onChatRoomViewCreation();
            }

        }

        String positionMsgId = fragmentArgs.getString("positionMsgId");
        if (!TextUtils.isEmpty(positionMsgId)){
            useLocalMsg = true;
            skipMsgPosition(positionMsgId);
        }

        if (chatType != EaseConstant.CHATTYPE_CHATROOM) {
            onConversationInit();
            onMessageListInit();
        }

        titleBar.setLeftLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleBar.setRightLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    toChatDetails();
                } else {
                    toGroupDetails();
                }
            }
        });

        setRefreshLayoutListener();

        // show forward message if the message is not null
        String forward_msg_id = getArguments().getString("forward_msg_id");
        if (forward_msg_id != null) {
            forwardMessage(forward_msg_id);
        }

        // show forward message if the message is not null
        ArrayList<String> forward_msg_id_list = getArguments().getStringArrayList("forward_msg_id_list");
        if (forward_msg_id_list != null) {
            for (int i = 0; i < forward_msg_id_list.size(); i++) {
                String msg_id = forward_msg_id_list.get(i);
                forwardMessage(msg_id);
            }
        }

        //multi formard
        mTvMultiForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkMap == null && checkMap.isEmpty()) {
                    return;
                }

                chatFragmentHelper.forwardOneByOne();

//                if (Locale.getDefault().getLanguage().equals("zh")) {
//                    forwardItems = forwardItems_zh;
//                } else {
//                    forwardItems = forwardItems_en;
//                }
//                new AlertDialog.Builder(getContext(), R.style.MyAlertDialog)
//                        .setSingleChoiceItems(forwardItems, -1, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                switch (i) {
//                                    case 0://逐条转发
//                                        chatFragmentHelper.forwardOneByOne();
//                                        break;
//                                    case 1:
//                                        chatFragmentHelper.forwardCombine();
//                                        break;
//                                }
//                            }
//                        })
//                        .setCancelable(true)
//                        .show();
            }
        });

        //multi delete
        mTvMultiDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkMap == null || checkMap.isEmpty()) {
                    return;
                }
                new AlertDialog.Builder(getContext(), R.style.MyAlertDialog)
                        .setCancelable(true)
                        .setMessage(getString(R.string.delete_message))
                        .setNegativeButton(R.string.cancel, new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        for (EMMessage message : checkMap.values()) {
                            conversation.removeMessage(message.getMsgId());
                            // To delete the ding-type message native stored acked users.
                            EaseDingMessageHelperV2.get().delete(message);
                        }

                        refreshMessageList();
                        showInputMenu();
                    }
                }).show();
            }
        });

        if (!TextUtils.isEmpty(selectMsgId)) {
            refreshToMessage(selectMsgId);
        }
    }

    //显示输入面板，隐藏多选按钮
    protected void showInputMenu() {
        inputMenu.setVisibility(View.VISIBLE);
        mLlMultiChoice.setVisibility(View.GONE);
        messageList.hideCheckbox();
    }

    //隐藏输入面板，显示多选按钮
    protected void hideInputMenu() {
        inputMenu.setVisibility(View.GONE);
        inputMenuFire.setVisibility(View.GONE);
        mLlMultiChoice.setVisibility(View.VISIBLE);
        messageList.showCheckbox();
    }

    private void setChatTitle(final String toChatUsername) {
        titleBar.setTitle(toChatUsername);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            if ("admin".equals(toChatUsername)) {
                titleBar.setTitle(getString(R.string.system_msg));
                inputMenu.setVisibility(View.GONE);
            } else if (MPClient.get().isFileHelper(toChatUsername)) {
                titleBar.setTitle(getString(R.string.file_transfer));
                inputMenu.setVisibility(View.VISIBLE);

            } else {
                inputMenu.setVisibility(View.VISIBLE);
                // set title
                EaseUser user = EaseUserUtils.getUserInfo(toChatUsername);
                if (user != null) {
                    titleBar.setTitle(TextUtils.isEmpty(user.getAlias()) ? user.getNick() : user.getAlias());
                }
            }
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            setChatGroupTitle();
        }

    }

    public abstract void onEditTextChanged(CharSequence s, int start, int before, int count);

    protected int groupId;

    private void setChatGroupTitle() {
        final GroupBean groupBean = EaseUserUtils.getGroupInfo(toChatUsername);
        if (groupBean != null) {
            groupId = groupBean.getGroupId();
            titleBar.setTitle(groupBean.getNick());
            isRegion = groupBean.isCluster();
            EMAPIManager.getInstance().getGroupInfo(toChatUsername, new EMDataCallBack<String>() {
                @Override
                public void onSuccess(String value) {
                    try {
                        JSONObject jsonObj = new JSONObject(value);
                        JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                        final MPGroupEntity groupEntity = MPGroupEntity.create(jsonEntity);
                        if (groupEntity == null) {
                            MPLog.e(TAG, "requestGroupInfo groupEntity is null");
                            return;
                        }
                        isRegion = groupEntity.isCluster();
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                titleBar.setTitle(groupEntity.getName() + " (" + groupEntity.getMemberCount() + ")");
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int error, String errorMsg) {

                }
            });
        } else {
            fetchGroupInfoFromServer();
//            EMAPIManager.getInstance().getGroupInfo(toChatUsername, new EMDataCallBack<String>() {
//                @Override
//                public void onSuccess(String value) {
//                    try {
//                        JSONObject jsonObj = new JSONObject(value);
//                        JSONObject jsonEntity = jsonObj.optJSONObject("entity");
//                        final MPGroupEntity groupEntity = MPGroupEntity.create(jsonEntity);
//                        if (groupEntity == null) {
//                            MPLog.e(TAG, "requestGroupInfo groupEntity is null");
//                            return;
//                        }
//                        groupId = groupEntity.getId();
//                        if (getActivity() == null) return;
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                titleBar.setTitle(groupBean.getNick() + " (" + groupEntity.getMemberCount() + ")");
//                            }
//                        });
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onError(int error, String errorMsg) {
//
//                }
//            });
        }

    }

    protected abstract void fetchGroupInfoFromServer();

    /**
     * register extend menu, item id need > 3 if you override this method and keep exist item
     */
    protected void registerExtendMenuItem() {
        for (int i = 0; i < itemStrings.length; i++) {
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
        }
    }


    protected void onConversationInit() {
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (EMMessage message : conversation.getAllMessages()) {
//            if (message.getType() == EMMessage.Type.TXT) {
//                EMTextMessageBody body = (EMTextMessageBody) message.getBody();
//                if (body != null) {
//                    Log.i("info", "msg time--" + simpleDateFormat.format(new Date(message.getMsgTime())) + "----msg content--" + body.getMessage());
//                }
//            }
//        }
//        if (conversation.getAllMessages().size() == 0 || Math.abs(conversation.getLastMessage().getMsgTime() - PrefsUtil.getInstance().getTimeStamp()) > 5 * 60 * 1000) {
//            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
//            EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody("flag_msg");
//            message.setTo(toChatUsername);
//            message.addBody(cmdMessageBody);
//            sendMessage(message);
//        }
        conversation.markAllMessagesAsRead();
        syncPCToRead(conversation);
        // the number of messages loaded into conversation is getChatOptions().getNumberOfMessagesLoaded
        // you can change this number

        if (!isRoaming || useLocalMsg) {
            final List<EMMessage> msgs = conversation.getAllMessages();
            int msgCount = msgs != null ? msgs.size() : 0;
            if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                String msgId = null;
                if (msgs != null && msgs.size() > 0) {
                    msgId = msgs.get(0).getMsgId();
                }
                conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
            }
        } else {
            fetchQueue.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().chatManager().fetchHistoryMessages(
                                toChatUsername, EaseCommonUtils.getConversationType(chatType), pagesize, "");
                        final List<EMMessage> msgs = conversation.getAllMessages();
                        int msgCount = msgs != null ? msgs.size() : 0;
                        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                            String msgId = null;
                            if (msgs != null && msgs.size() > 0) {
                                msgId = msgs.get(0).getMsgId();
                            }
                            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
                        }
                        refreshMessageListSelectLast();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    protected void syncPCToRead(EMConversation conversation) {
        if(MPClient.get().isFileHelper(conversation.conversationId())) {
           return;
        }
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMessage.setTo(MPClient.get().getPcTarget());
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(EaseConstant.ACTION_SYNC_READ);
//        cmdMessageBody.deliverOnlineOnly(true);
        cmdMessage.addBody(cmdMessageBody);
        cmdMessage.setAttribute(EaseConstant.ACTION_SYNC_READ_CONVID, conversation.conversationId());
        cmdMessage.setAttribute(EaseConstant.ACTION_SYNC_READ_CHATTYPE, conversation.getType() == EMConversation.EMConversationType.GroupChat ? 1 : 0);
        EMClient.getInstance().chatManager().sendMessage(cmdMessage);
    }

    protected void onMessageListInit() {
        messageList.init(toChatUsername, chatType, chatFragmentHelper != null ?
                chatFragmentHelper.onSetCustomChatRowProvider() : null, checkMap);
        setListItemClickListener();

        messageList.getListView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                inputMenu.hideExtendMenuContainer();
                return false;
            }
        });

        isMessageListInited = true;
    }

    protected void setListItemClickListener() {
        messageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String imUsername) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarClick(imUsername);
                }
            }

            @Override
            public void onUserAvatarLongClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarLongClick(username);
                }
            }

            @Override
            public void onMessageInProgress(EMMessage message) {
                message.setMessageStatusCallback(messageStatusCallback);
            }

            @Override
            public void onReferenceMsgClick(EMMessage message) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onReferenceMsgClick(message);
                }
            }

            @Override

            public void onBubbleLongClick(ImageView imageView, View v, EMMessage message, int position) {
                contextMenuMessage = message;
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onMessageBubbleLongClick(imageView, v, message, position);
                }
            }

            @Override
            public boolean onResendClick(final EMMessage message) {
                EMLog.i(TAG, "onResendClick");
                new EaseAlertDialog(getContext(), R.string.resend, R.string.confirm_resend, null, new AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (!confirmed) {
                            return;
                        }
                        message.setStatus(EMMessage.Status.CREATE);
                        sendMessage(message);
                    }
                }, true).show();
                return true;
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                if (chatFragmentHelper == null) {
                    return false;
                }
                return chatFragmentHelper.onMessageBubbleClick(message);
            }

        });
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (!isRoaming) {
                            loadMoreLocalMessage();
                        } else {
                            loadMoreRoamingMessages();
                        }
                    }
                }, 600);
            }
        });
    }

    private boolean loadMoreLocalMessageOnly() {
        if (!isloading && haveMoreData) {
            List<EMMessage> messages;
            try {
                messages = conversation.loadMoreMsgFromDB(conversation.getAllMessages().size() == 0 ? "" : conversation.getAllMessages().get(0).getMsgId(),
                        pagesize);
                messageList.refresh();
            } catch (Exception e1) {
                swipeRefreshLayout.setRefreshing(false);
                return false;
            }
            if (messages.size() > 0) {
                if (messages.size() != pagesize) {
                    haveMoreData = false;
                }
                return true;
            } else {
                haveMoreData = false;
            }

            isloading = false;
        } else {
            return false;
        }
        return false;
    }

    private void loadMoreLocalMessage() {
        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
            List<EMMessage> messages;
            try {
                messages = conversation.loadMoreMsgFromDB(conversation.getAllMessages().size() == 0 ? "" : conversation.getAllMessages().get(0).getMsgId(),
                        pagesize);
            } catch (Exception e1) {
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            if (messages.size() > 0) {
                messageList.refreshSeekTo(messages.size() - 1);
                if (messages.size() != pagesize) {
                    haveMoreData = false;
                }
            } else {
                haveMoreData = false;
            }

            isloading = false;
        } else {
            ToastUtils.showShort(getResources().getString(R.string.no_more_messages));
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadMoreRoamingMessages() {
        if (!haveMoreData) {
            ToastUtils.showShort(getResources().getString(R.string.no_more_messages));
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (fetchQueue != null) {
            fetchQueue.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<EMMessage> messages = conversation.getAllMessages();
                        EMCursorResult<EMMessage> emCursorResult = EMClient.getInstance().chatManager().fetchHistoryMessages(
                                toChatUsername, EaseCommonUtils.getConversationType(chatType), pagesize,
                                (messages != null && messages.size() > 0) ? messages.get(0).getMsgId() : "");
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    } finally {
                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadMoreLocalMessage();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                int type = data.getIntExtra("type", -1);
                if (type == 0) {
                    String path = data.getStringExtra("path");
                    sendImageMessage(path);
                } else if (type == 1) {
                    String thumbPath = data.getStringExtra("thumb_path");
                    String videoPath = data.getStringExtra("path");
                    sendVideoMessage(videoPath, thumbPath, 0);
                }

            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) { // To send the ding-type msg.
                String msgContent = data.getStringExtra("msg");
                EMLog.i(TAG, "To send the ding-type msg, content: " + msgContent);
                // Send the ding-type msg.
                EMMessage dingMsg = EaseDingMessageHelperV2.get().createDingMessage(toChatUsername, msgContent);
                sendMessage(dingMsg);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MediaManager.getManager().resume();
        if (isMessageListInited)
            refreshMessageList();
        EaseUI.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(this);

        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EaseAtMessageHelper.get().removeAtMeGroup(toChatUsername);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MediaManager.getManager().pause();
    }

    @Override
    public void onStop() {
        super.onStop();

        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(this);

        // remove activity from foreground activity list
        EaseUI.getInstance().popActivity(getActivity());

        // Remove all padding actions in handler
        handler.removeCallbacksAndMessages(null);
        MediaManager.getManager().unregister();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaManager.getManager().release();

        if (groupListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
        }

        if (chatRoomListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomListener(chatRoomListener);
        }

        if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
        }
    }

    public void onBackPressed() {
        if (mLlMultiChoice.getVisibility() == View.VISIBLE) {
            showInputMenu();
            return;
        }
        if (inputMenu.onBackPressed()) {
            if (getActivity() != null) {
                getActivity().finish();
            }
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                EaseAtMessageHelper.get().removeAtMeGroup(toChatUsername);
                EaseAtMessageHelper.get().cleanToAtUserList();
            }
            if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
            }
        }
    }

    protected void onChatRoomViewCreation() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Joining......");
        EMClient.getInstance().chatroomManager().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(final EMChatRoom value) {
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity().isFinishing() || !toChatUsername.equals(value.getId()))
                            return;
                        pd.dismiss();
                        EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(toChatUsername);
                        if (room != null) {
                            titleBar.setTitle(room.getName());
                            EMLog.d(TAG, "join room success : " + room.getName());
                        } else {
                            titleBar.setTitle(toChatUsername);
                        }
                        onConversationInit();
                        onMessageListInit();

                        // Dismiss the click-to-rejoin layout.
                        kickedForOfflineLayout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                // TODO Auto-generated method stub
                EMLog.d(TAG, "join room failure : " + error);
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
                getActivity().finish();
            }
        });
    }

    // implement methods in EMMessageListener
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        MPLog.e(TAG, "onMessageReceived-size:" + messages.size());
        for (EMMessage message : messages) {
            // 判断一下是否是会议邀请
            String confId = message.getStringAttribute(EaseConstant.MSG_ATTR_CONF_ID, "");
            if (!"".equals(confId)) {
                return;
            }
            String username = null;
            // group message
            if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }

            //保存消息扩展字段
            doReceiveMessageExtField(message);

            // if the message is for current conversation
            if (username.equals(toChatUsername) || message.getTo().equals(toChatUsername) || message.conversationId().equals(toChatUsername)) {
                refreshMessageListSelectLast();


                EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message, false);

                conversation.markMessageAsRead(message.getMsgId());
            } else {
                if (!message.getFrom().equals(EMClient.getInstance().getCurrentUser())) {
                    EaseUI.getInstance().getNotifier().onNewMsg(message, false);
                }
            }
        }
    }

    public abstract void refreshMessageList();

    public abstract void refreshMessageListSelectLast();

    public void refreshToMessage(final String selectMsgId) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                EaseUI.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            boolean isFindMsg = false;
                            for (int i = 0; i < messageList.getListView().getCount(); i++) {
                                EMMessage message = messageList.getItem(i);
                                if (message.getMsgId().equals(selectMsgId)) {
                                    isFindMsg = true;
                                    if (getActivity() == null) {
                                        return;
                                    }
                                    messageList.refreshSeekTo(i);
                                }
                            }
                            if (!isFindMsg && haveMoreData) {
                                messageList.refreshSeekTo(0);
                                boolean isTrue = loadMoreLocalMessageOnly();
                                if (isTrue) {
                                    refreshToMessage(selectMsgId);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 500);
    }

    ;

//    protected void refreshMessageList(){
//        messageList.refresh();
//    }
//
//    protected void refreshMessageListSelectLast(){
//        messageList.refreshSelectLast();
//    }

    protected abstract void doReceiveMessageExtField(EMMessage message);

    protected abstract void doAddSendExtField(EMMessage message);

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            for (final EMMessage msg : messages) {
                final EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();
                EMLog.i(TAG, "Receive cmd message: " + body.action() + " - " + body.isDeliverOnlineOnly());
                if (msg.getFrom().equals(toChatUsername)) {
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ACTION_TYPING_BEGIN.equals(body.action())) {
                                if (typingHandler != null) {
                                    titleBar.setTitle(getString(R.string.alert_during_typing));
                                    typingHandler.removeMessages(MSG_TYPING_END);
                                    typingHandler.sendEmptyMessageDelayed(MSG_TYPING_END, TYPING_SHOW_TIME);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        if (isMessageListInited) {
            refreshMessageList();
        }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        if (isMessageListInited) {
            refreshMessageList();
        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        if (isMessageListInited) {
            refreshMessageList();
        }
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object change) {
        if (isMessageListInited) {
            refreshMessageList();
        }
    }

    /**
     * handle the click event for extend menu
     */
    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {
            if (chatFragmentHelper != null) {
                if (chatFragmentHelper.onExtendMenuItemClick(itemId, view)) {
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE:
//                    PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(EaseChatFragment.this,
//                            new String[]{
//                                    Manifest.permission.RECORD_AUDIO,
//                                    Manifest.permission.CAMERA,
//                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                    Manifest.permission.READ_EXTERNAL_STORAGE
//                            }, new PermissionsResultAction() {
//                                @Override
//                                public void onGranted() {
//                                    selectPicFromCamera();
//                                }
//
//                                @Override
//                                public void onDenied(String permission) {
//                                    MyToast.showInfoToast("需要在设置中开启权限");
//                                }
//                            });

                    if(checkPermission(new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE})){
                        selectPicFromCamera();
                    }
                    break;
                case ITEM_PICTURE:
//                    PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(EaseChatFragment.this,
//                            new String[]{
//                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                    Manifest.permission.READ_EXTERNAL_STORAGE
//                            }, new PermissionsResultAction() {
//                                @Override
//                                public void onGranted() {
//                                    selectPicFromLocal();
//                                }
//
//                                @Override
//                                public void onDenied(String permission) {
//                                    MyToast.showInfoToast("需要在设置中开启权限");
//                                }
//                            });

                    if(checkPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE})){
                        selectPicFromLocal();
                    }
                    break;
//                case ITEM_LOCATION:
//                    startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
//                    startActivityForResult(new Intent(getActivity(), EMBaiduMapActivity.class), REQUEST_CODE_MAP);
//                    break;

                default:
                    break;
            }
        }

    }


    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username, boolean autoAddAtSymbol) {
        if (EMClient.getInstance().getCurrentUser().equals(username) ||
                chatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EaseAtMessageHelper.get().addAtUser(username);
        if (EaseConstant.AT_ALL_USER_NAME.equals(username)) {
            username = getString(R.string.all_members);
        } else {
            EaseUser user = EaseUserUtils.getUserInfo(username);
            if (user != null) {
                username = user.getNick();
            }
        }

        if (autoAddAtSymbol)
            inputMenu.insertText("@" + username + " ");
        else
            inputMenu.insertText(username + " ");
    }


    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username) {
        inputAtUsername(username, true);
    }

    protected void sendTextTooLarge() {

    }

    //send message
    protected boolean sendTextMessage(String content) {
        if (content.length() > 1000) {
            sendTextTooLarge();
            return false;
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP && containsAtUsername(content)) {
            sendAtMessage2(content);
        } else {
            EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
            if (isInputMenuFire()) {
                attachFireAttribute(message);
            }
            if (MPClient.get().isFileHelper(toChatUsername)) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("transfer", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                message.setAttribute("transferMsg", jsonObject);
            }
            doAddSendExtField(message);
            addTextMsgReferenceExt(message);
            sendMessage(message);
        }
        mAtUserBeans.clear();
        return true;
    }

    /**
     * 文本消息添加消息引用扩展
     * @param message
     */
    protected abstract void addTextMsgReferenceExt(EMMessage message);

    protected void sendCardMessage(EMMessage msg) {
        EMTextMessageBody body = (EMTextMessageBody) msg.getBody();
        JSONObject extJson = null;
        try {
            extJson = msg.getJSONObjectAttribute("extMsg");
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        EMMessage message = EMMessage.createTxtSendMessage(body.getMessage(), toChatUsername);
        if (extJson != null) {
            message.setAttribute(EaseConstant.EXT_EXTMSG, extJson);
        }
        doAddSendExtField(message);
        sendMessage(message);
    }

    protected void sendStickerMessage(String content, JSONObject extJson) {
        if (chatType == EaseConstant.CHATTYPE_GROUP && containsAtUsername(content)) {
            sendAtMessage2(content);
        } else {
            EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
            if (extJson != null) {
                message.setAttribute(EaseConstant.EXT_EXTMSG, extJson);
            }
            doAddSendExtField(message);
            sendMessage(message);
        }
        mAtUserBeans.clear();
    }

    private boolean containsAtAll(String content) {
        String atAll = "@" + EaseUI.getInstance().getContext().getString(R.string.all_members);
        if (content.contains(atAll)) {
            return true;
        }
        return false;
    }


    private void sendAtMessage2(String content) {
        if (chatType != EaseConstant.CHATTYPE_GROUP) {
            EMLog.e(TAG, "only support group chat message");
            return;
        }

        JSONArray atUserArr = getAtMessageUsernames(content);

        //发送@信息前再次进行@信息的正确性（避免用户对其进行修改）
        for (int index = mAtUserBeans.size() - 1; index >= 0; index--) {
            AtUserBean bean = mAtUserBeans.get(index);
            if (!content.contains("@" + bean.username + (char) (8197))) {
                mAtUserBeans.remove(index);
            }
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
        String currentUser = EMClient.getInstance().getCurrentUser();
        try {
            if ((currentUser.equals(group.getOwner()) || group.getAdminList().contains(currentUser)) && containsAtAll(content)) {
                message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, getAtAllMessage());
            } else {
//                message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
//                        EaseAtMessageHelper.get().atListToJsonArray(getAtMessageUsernames(content)));
                message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                        atUserArr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        doAddSendExtField(message);
        addTextMsgReferenceExt(message);
        sendMessage(message);

    }

    public JSONArray getAtMessageUsernames(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        synchronized (mAtUserBeans) {
            JSONArray jsonAt = new JSONArray();
            for (AtUserBean userBean : mAtUserBeans) {
                if (content.contains(userBean.realName)) {
                    JSONObject jsonObj = new JSONObject();
                    try {
                        jsonObj.put("value", userBean.realName);
                        jsonObj.put("children", userBean.realName);
                        jsonObj.put("data", userBean.jsonData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    jsonAt.put(jsonObj);
                }
            }
            return jsonAt;
        }
    }

    public JSONArray getAtAllMessage() {
        JSONArray jsonAt = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("value", "所有人");
            jsonObj.put("children", "所有人");
            jsonObj.put("data", new JSONObject()
                    .put("type", EaseConstant.MESSAGE_ATTR_AT_ALL_TYPE)
                    .put("realName", "所有人"));
            jsonAt.put(jsonObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonAt;
    }


    private boolean containsAtUsername(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        synchronized (mAtUserBeans) {
            for (AtUserBean userBean : mAtUserBeans) {
                if (content.contains("@" + userBean.realName)) {
                    return true;
                }
            }
        }
        return false;
    }


//    /**
//     * send @ message, only support group chat message
//     *
//     * @param content
//     */
//    @SuppressWarnings("ConstantConditions")
//    private void sendAtMessage(String content) {
//        if (chatType != EaseConstant.CHATTYPE_GROUP) {
//            EMLog.e(TAG, "only support group chat message");
//            return;
//        }
//        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
//        EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
//        String currentUser = EMClient.getInstance().getCurrentUser();
//        try {
//            if ((currentUser.equals(group.getOwner()) || group.getAdminList().contains(currentUser)) && EaseAtMessageHelper.get().containsAtAll(content)) {
//                message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
//            } else {
//                message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
//                        EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        doAddSendExtField(message);
//        sendMessage(message);
//
//    }

    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUsername, name, identityCode);
        doAddSendExtField(message);
        sendMessage(message);
    }

    protected void sendBigExpressionMessage(String name, String identityCode, boolean isForward) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUsername, name, identityCode, isForward);
        doAddSendExtField(message);
        sendMessage(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        doAddSendExtField(message);
        sendMessage(message);
    }

    protected void sendImageMessage(String imagePath) {
        sendImageMessage(imagePath, false);
    }

    protected void sendImageMessage(String imagePath, boolean originalImage) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, originalImage, toChatUsername);
        if (isInputMenuFire()) {
            attachFireAttribute(message);
        }
        doAddSendExtField(message);
        sendMessage(message);
    }

    private void attachFireAttribute(EMMessage message) {
        if (message == null) return;
        JSONObject extMsg = new JSONObject();
        try {
            extMsg.put("type", "burn_after_reading");
            message.setAttribute("extMsg", extMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress, String locImage) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
        doAddSendExtField(message);
        if (locImage != null) {
            message.setAttribute("locImage", locImage);
        }
        sendMessage(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
        doAddSendExtField(message);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUsername);
        doAddSendExtField(message);
        sendMessage(message);
    }

    //===================================================================================

    protected EMCallBack messageStatusCallback = new EMCallBack() {
        @Override
        public void onSuccess() {
            if (isMessageListInited) {
                messageList.refresh();
            }
        }

        @Override
        public void onError(int code, String error) {
            Log.i("EaseChatRowPresenter", "onError: " + code + ", error: " + error);
            if (code == EMError.GROUP_NOT_JOINED && conversation.isGroup()) {
                try {
                    notJoinedGroup();
                } catch (Exception ignored) {
                }
                return;
            }
            if (isMessageListInited) {
                messageList.refresh();
            }
        }

        @Override
        public void onProgress(int progress, String status) {
            Log.i(TAG, "onProgress: " + progress);
            if (isMessageListInited) {
                messageList.refresh();
            }
        }
    };

    protected abstract void notJoinedGroup();


    protected void sendMessage(final EMMessage message) {
        if (message == null) {
            return;
        }
        if (chatFragmentHelper != null) {
            //set extension
            chatFragmentHelper.onSetMessageAttributes(message);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            message.setChatType(ChatType.GroupChat);
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            message.setChatType(ChatType.ChatRoom);
        }

        final EMMessage lastMsg = conversation.getLastMessage();

        message.setMessageStatusCallback(messageStatusCallback);

        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
//                if (message.getType() == EMMessage.Type.CMD && ((EMCmdMessageBody) message.getBody()).action().equals("flag_msg")) {
//                    EMMessage flagMsg = EMMessage.createSendMessage(EMMessage.Type.TXT);
//                    EMTextMessageBody body = new EMTextMessageBody("flag_msg");
//                    flagMsg.setTo(toChatUsername);
//                    flagMsg.setMsgTime(message.getMsgTime());
//                    flagMsg.setMsgId(message.getMsgId());
//                    flagMsg.addBody(body);
//                    conversation.appendMessage(flagMsg);
//                }
                PrefsUtil.getInstance().setTimeStamp(message.getMsgTime());
                messageSendAfter(message);
            }

            @Override
            public void onError(int i, String s) {
                if (lastMsg == null) {
                    message.setMsgTime(PrefsUtil.getInstance().getTimeStamp() + 1000);
                } else {
                    long currentTime = System.currentTimeMillis();
                    if (Math.abs(currentTime - lastMsg.getMsgTime()) < 10 * 60 * 1000) {
                        message.setMsgTime(lastMsg.getMsgTime() + 1000);
                    } else if (Math.abs(currentTime - PrefsUtil.getInstance().getTimeStamp()) < 10 * 60 * 1000) {
                        message.setMsgTime(PrefsUtil.getInstance().getTimeStamp() + 1000);
                    } else {
                        message.setMsgTime(lastMsg.getMsgTime() + 1000);
                    }
                }
                conversation.updateMessage(message);
                refreshMessageList();
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });

        // Send message.
        MessageUtils.sendMessage(message);
        //refresh ui
        if (isMessageListInited) {
            refreshMessageListSelectLast();
        }
    }

    protected void messageSendAfter(EMMessage message){

    }

    //===================================================================================

    /**
     * send file
     *
     * @param uri
     */
    protected void sendFileByUri(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;

            try {
                cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            ToastUtils.showShort(R.string.File_does_not_exist);
            return;
        }
        sendFileMessage(filePath);
    }


    protected abstract void selectPicFromCamera();

    /**
     * select local image
     */
    protected abstract void selectPicFromLocal();

    protected void selectPicOnlyFromLocal() {

    }

    ;

    /**
     * clear the conversation history
     */
    protected void emptyHistory() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(getActivity(), null, msg, null, new AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    if (conversation != null) {
                        conversation.clearAllMessages();
                    }
                    refreshMessageList();
                    haveMoreData = true;
                }
            }
        }, true).show();
    }

    //单聊详情
    private void toChatDetails() {
        chatFragmentHelper.onEnterToChatDetails();
    }

    /**
     * open group detail
     */
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
//            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
//            if (group == null) {
//                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        }
    }

    /**
     * hide
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * forward message
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                if (forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null), true);
                } else if (EaseMessageUtils.isStickerMessage(forward_msg)) {
                    JSONObject jsonExtMsg = null;
                    try {
                        jsonExtMsg = forward_msg.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    // get the content and send it
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendStickerMessage(content, jsonExtMsg);

                } else if (EaseMessageUtils.isNameCard(forward_msg)) {
                    sendCardMessage(forward_msg);
                } else {
                    // get the content and send it
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendTextMessage(content);
                }
                break;
            case IMAGE:
                // send image
                String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(filePath);
                }
                break;
            case LOCATION:
                EMLocationMessageBody locBody = (EMLocationMessageBody) forward_msg.getBody();
                String locImage = forward_msg.getStringAttribute("locImage", null);
                sendLocationMessage(locBody.getLatitude(), locBody.getLongitude(), locBody.getAddress(), locImage);
                break;
            default:
                break;
        }

        if (forward_msg.getChatType() == ChatType.ChatRoom) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
        }
    }

    /**
     * listen the group event
     */
    class GroupListener extends EaseGroupListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {

                public void run() {
                    if (toChatUsername.equals(groupId)) {
                        ToastUtils.showShort(R.string.you_are_group);
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }

        @Override
        public void onGroupDestroyed(final String groupId, String groupName) {
            // prompt group is dismissed and finish this activity
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (toChatUsername.equals(groupId)) {
                        ToastUtils.showShort(R.string.the_current_group_destroyed);
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }

        @Override
        public void onWhiteListAdded(String s, List<String> list) {

        }

        @Override
        public void onWhiteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAllMemberMuteStateChanged(String s, boolean b) {

        }


        @Override
        public void onMemberJoined(String groupId, String member) {
            super.onMemberJoined(groupId, member);
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setChatGroupTitle();
                }
            });
        }

        @Override
        public void onMemberExited(String groupId, String member) {
            super.onMemberExited(groupId, member);
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setChatGroupTitle();
                }
            });
        }

    }

    /**
     * listen chat room event
     */
    class ChatRoomListener extends EaseChatRoomListener {

        @Override
        public void onChatRoomDestroyed(final String roomId, final String roomName) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (roomId.equals(toChatUsername)) {
                        ToastUtils.showShort(R.string.the_current_chat_room_destroyed);
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }

        @Override
        public void onRemovedFromChatRoom(final int reason, final String roomId, final String roomName, final String participant) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (roomId.equals(toChatUsername)) {
                        if (reason == EMAChatRoomManagerListener.BE_KICKED) {
                            ToastUtils.showShort(R.string.quiting_the_chat_room);
                            Activity activity = getActivity();
                            if (activity != null && !activity.isFinishing()) {
                                activity.finish();
                            }
                        } else { // BE_KICKED_FOR_OFFLINE
                            // Current logged in user be kicked out by server for current user offline,
                            // show disconnect title bar, click to rejoin.
                            ToastUtils.showShort("User be kicked for offline");
                            kickedForOfflineLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }

        @Override
        public void onWhiteListAdded(String s, List<String> list) {

        }

        @Override
        public void onWhiteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAllMemberMuteStateChanged(String s, boolean b) {

        }


        @Override
        public void onMemberJoined(final String roomId, final String participant) {
            if (getActivity() == null) return;
            if (roomId.equals(toChatUsername)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ToastUtils.showShort("member join:" + participant);
                    }
                });
            }
        }

        @Override
        public void onMemberExited(final String roomId, final String roomName, final String participant) {
            if (getActivity() == null) return;
            if (roomId.equals(toChatUsername)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ToastUtils.showShort("member exit:" + participant);
                    }
                });
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (typingHandler != null) {
            typingHandler.removeMessages(MSG_TYPING_END);
            typingHandler.removeCallbacksAndMessages(null);
        }
    }

    private void skipMsgPosition(String msgId){
        final EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
        if(message != null){
            refreshToMessage(msgId);
        } else {
            conversation.loadMoreMsgFromDB(conversation.getAllMessages().size() == 0 ? "" : conversation.getAllMessages().get(0).getMsgId(),
                    pagesize);
            skipMsgPosition(msgId);
        }
    }

    protected EaseChatFragmentHelper chatFragmentHelper;

    public void setChatFragmentHelper(EaseChatFragmentHelper chatFragmentHelper) {
        this.chatFragmentHelper = chatFragmentHelper;
    }

    public interface EaseChatFragmentHelper {

        void forwardOneByOne();

        void forwardCombine();

        /**
         * set message attribute
         */
        void onSetMessageAttributes(EMMessage message);

        /**
         * enter to chat detail
         */
        void onEnterToChatDetails();

        /**
         * on avatar clicked
         *
         * @param imUsername
         */
        void onAvatarClick(String imUsername);

        /**
         * on avatar long pressed
         *
         * @param username
         */
        void onAvatarLongClick(String username);

        /**
         * on message bubble clicked
         */
        boolean onMessageBubbleClick(EMMessage message);

        /**
         * on message bubble long pressed
         */
        void onMessageBubbleLongClick(ImageView imageView, View v, EMMessage message, int position);

        /**
         * on extend menu item clicked, return true if you want to override
         *
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * on set custom chat row provider
         *
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();

        /**
         * 引用消息点击事件
         * @param message
         */
        void onReferenceMsgClick(EMMessage message);

    }

    public static class AtUserBean {
        public AtUserBean() {
        }

        public String username;
        public String realName;
        public JSONObject jsonData;
    }
}
