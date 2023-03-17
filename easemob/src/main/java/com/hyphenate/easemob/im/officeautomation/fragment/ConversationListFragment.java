package com.hyphenate.easemob.im.officeautomation.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.SizeUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.WaterMarkBg;
import com.hyphenate.easemob.easeui.widget.WaterMarkBgView;
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsManager;
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsResultAction;
import com.hyphenate.easemob.imlibs.mp.events.EventUserInfoRefresh;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.im.mp.cache.SessionCache;
import com.hyphenate.easemob.im.mp.cache.TenantOptionCache;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.events.EventEMMessageReceived;
import com.hyphenate.easemob.imlibs.mp.events.EventEMessageSent;
import com.hyphenate.easemob.imlibs.mp.events.EventTenantOptionChanged;
import com.hyphenate.easemob.imlibs.mp.events.MessageChanged;
import com.hyphenate.easemob.im.mp.impl.DraftImpl;
import com.hyphenate.easemob.im.mp.manager.DraftManager;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.db.InviteMessageDao;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPSessionEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.ChatActivity;
import com.hyphenate.easemob.im.officeautomation.ui.FileTransferActivity;
import com.hyphenate.easemob.im.officeautomation.ui.FindUserActivity;
import com.hyphenate.easemob.im.officeautomation.ui.SearchActivity;
import com.hyphenate.easemob.im.officeautomation.ui.SystemNotifyActivity;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupAddMemberActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.widget.TriangleDrawable;
import com.hyphenate.util.NetUtils;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ConversationListFragment extends EaseConversationListFragment {

    private static final String TAG = "ConversationListFragment";
    private TextView errorText;
    private PopupWindow selectPopupWindow;
    private EasyPopup mEasyPopup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MPEventBus.getDefault().register(this);
        // runtime permission for android 6.0, just require all permissions here for simple
//        requestAllPermissions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        MPEventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoRefresh(EventUserInfoRefresh event){
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageChanged(MessageChanged event) {
        refresh();
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageSent(EventEMessageSent event) {
        EMMessage message = event.getMessage();
        if (message == null || message.getType() == EMMessage.Type.CMD) {
            return;
        }
        SessionCache.getInstance().onMessageSent(message);

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageReceived(EventEMMessageReceived event) {
        EMMessage message = event.getMessage();
        if (message == null || message.getType() == EMMessage.Type.CMD) {
            return;
        }
        if (MPClient.get().isFileHelper(message.getTo())) {
            message.setTo(MPClient.get().getPcTarget());
            EMClient.getInstance().chatManager().updateMessage(message);
        }
        SessionCache.getInstance().onMessageReceived(message);
        getActivity().runOnUiThread(() -> {
            refresh();
        });
    }


    @Override
    protected void initView() {
        super.initView();
        View errorView = View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = errorView.findViewById(R.id.tv_connect_errormsg);
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        titleBar.setRightImageResource(R.drawable.mp_ic_add_blue);
        titleBar.setTitle(getString(R.string.session));
        mRlSearch.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), SearchActivity.class));
        });
        conversationList.addAll(loadConversationList());
        conversationListView.init(conversationList);
        // register context menu
//        registerForContextMenu(conversationListView);
        conversationListView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            showSelectPopWindow(view, i);
            return true;
        });

//        conversationListView.setOnItemLongClickListener((adapterView, view, i, l) -> {
//            final FloatMenu floatMenu = new FloatMenu(getContext(), view);
//            floatMenu.items("菜单1", "菜单2", "菜单3");
//            floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
//                @Override
//                public void onClick(View v, int position) {
//                    Toast.makeText(getActivity(), "菜单"+position, Toast.LENGTH_SHORT).show();
//                }
//            });
//            floatMenu.show();
//            return true;
//        });

        titleBar.setRightLayoutClickListener(view -> showMorePopWindow(titleBar.getRightLayout()));
//        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                asyncGetSessions();
//            }
//        });
        conversationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.conversationId();
                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {

                    if (username.equals("admin")) {
                        Intent intent = new Intent(getActivity(), SystemNotifyActivity.class);
                        startActivity(intent);
                        return;
                    }
                    // start chat acitivity
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    if (conversation.isGroup()) {
                        if (conversation.getType() == EMConversationType.ChatRoom) {
                            // it's group chat
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
                        } else {
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
                        }

                    }
                    // it's single chat
                    intent.putExtra(Constant.EXTRA_USER_ID, username);
                    startActivityForResult(intent, 203);
                }
            }
        });
        super.setUpView();
        conversationListView.setDraftListener(new DraftImpl());
        refreshWaterMark();

        asyncGetSessions();
    }

    private void refreshWaterMark() {
//        if (getView() == null) return;
//        if (TenantOptionCache.getInstance().isShowWaterMark()) {
//            getView().findViewById(R.id.ll_layout).setBackground(WaterMarkBg.create(getContext()));
//        } else {
//            getView().findViewById(R.id.ll_layout).setBackgroundColor(Color.WHITE);
//        }

//        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
//        if (loginUser == null) {
//            return;
//        }
//        String text = "";
//        if (!TextUtils.isEmpty(loginUser.getAlias())) {
//            text = loginUser.getAlias();
//        } else if (!TextUtils.isEmpty(loginUser.getNickname())) {
//            text = loginUser.getNickname();
//        } else if (!TextUtils.isEmpty(loginUser.getEntityBean().getAlias())) {
//            text = loginUser.getEntityBean().getAlias();
//        } else if (!TextUtils.isEmpty(loginUser.getEntityBean().getRealName())) {
//            text = loginUser.getEntityBean().getRealName();
//        }
//        if (TenantOptionCache.getInstance().isShowWaterMark()) {
//            WaterMarkBgView.getInstance().setTextColor(0x33AEAEAE).show(getActivity(), text);
//        } else {
//            WaterMarkBgView.getInstance().setTextColor(0x00000000).show(getActivity(), text);
//        }
    }


    void refreshWaterMark(Activity activity) {
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        String text = "";
        if (!TextUtils.isEmpty(loginUser.getAlias())) {
            text = loginUser.getAlias();
        } else if (!TextUtils.isEmpty(loginUser.getNickname())) {
            text = loginUser.getNickname();
        } else if (!TextUtils.isEmpty(loginUser.getEntityBean().getAlias())) {
            text = loginUser.getEntityBean().getAlias();
        } else if (!TextUtils.isEmpty(loginUser.getEntityBean().getRealName())) {
            text = loginUser.getEntityBean().getRealName();
        }
        if (TenantOptionCache.getInstance().isShowWaterMark()) {
            WaterMarkBgView.getInstance().setTextColor(0x33AEAEAE).show(activity, text);
        } else {
            WaterMarkBgView.getInstance().setTextColor(0x00000000).show(activity, text);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTenantOptionsChanged(EventTenantOptionChanged event) {
        if (TenantOptionCache.OPTION_NAME_WATERMARK.equals(event.getOptionName())) {
//            refreshWaterMark(getActivity());
        }
    }


    //显示条目长按下拉列表
    private void showSelectPopWindow(View itemView, int i) {
        EMConversation conversation = conversationListView.getItem(i);
        String extField = conversation.getExtField();
        View contentView = View.inflate(getActivity(), R.layout.popup_select_conversation_list, null);
        TextView title = contentView.findViewById(R.id.conversation_title);
        String username = conversation.conversationId();
        try {
            if (conversation.getType() == EMConversationType.Chat) {
                if ("admin".equals(username)) {
                    title.setText(getString(R.string.system_msg));
                } else {
                    EaseUser userInfo = EaseUserUtils.getUserInfo(username);
                    title.setText(userInfo == null ? username : userInfo.getNickname());
                }
            } else {
                GroupBean groupInfo = EaseUserUtils.getGroupInfo(username);
                title.setText(groupInfo == null ? username : groupInfo.getNick());
            }
        } catch (Exception e) {
            title.setText(username);
        }
        TextView sticky_conversation = contentView.findViewById(R.id.sticky_conversation);
        TextView sticky_cancel = contentView.findViewById(R.id.sticky_cancel);
        TextView delete_conversation = contentView.findViewById(R.id.delete_conversation);
        TextView delete_message = contentView.findViewById(R.id.delete_message);
        if (TextUtils.isEmpty(extField)) {
            sticky_conversation.setVisibility(View.VISIBLE);
            sticky_cancel.setVisibility(View.GONE);
        } else {
            sticky_conversation.setVisibility(View.GONE);
            sticky_cancel.setVisibility(View.VISIBLE);
        }
        contentView.measure(0, 0);
        selectPopupWindow = new PopupWindow(getContext());
        selectPopupWindow.setContentView(contentView);
        selectPopupWindow.setFocusable(true);
        selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        selectPopupWindow.setOutsideTouchable(true);

        int[] location = new int[2];
        itemView.getLocationOnScreen(location);

        selectPopupWindow.showAtLocation(itemView, Gravity.NO_GRAVITY, location[0] + itemView.getMeasuredWidth() / 2 - contentView.getMeasuredWidth() / 2, (int) (location[1] + (float) itemView.getMeasuredHeight() / 2));

        sticky_conversation.setOnClickListener(view -> doSelectClick(R.id.sticky_conversation, conversation));
        sticky_cancel.setOnClickListener(view -> doSelectClick(R.id.sticky_cancel, conversation));
        delete_conversation.setOnClickListener(view -> doSelectClick(R.id.delete_conversation, conversation));
        delete_message.setOnClickListener(view -> doSelectClick(R.id.delete_message, conversation));
    }

    //显示更多下拉列表
    private void showMorePopWindow(RelativeLayout rightLayout) {
        ImageView ivRight = rightLayout.findViewById(R.id.right_image);

        mEasyPopup = EasyPopup.create().setContext(getContext()).setContentView(R.layout.popup_more_conversation_list)
                .setAnimationStyle(R.style.RightTop2PopAnim)
                .setOnViewListener(new EasyPopup.OnViewListener() {
                    @Override
                    public void initViews(View view) {
                        View arrowView = view.findViewById(R.id.v_arrow);
                        arrowView.setBackground(new TriangleDrawable(TriangleDrawable.TOP, Color.WHITE));
                        arrowView.setVisibility(View.GONE);
                    }
                })
                .setFocusAndOutsideEnable(true)
                .apply();
        TextView tvAddFriend = mEasyPopup.findViewById(R.id.tv_add_friend);
        tvAddFriend.setOnClickListener(view -> {
            mEasyPopup.dismiss();
            startActivity(new Intent(getActivity(), FindUserActivity.class));
        });
        TextView tvCreateGroup = mEasyPopup.findViewById(R.id.tv_start_group);
        tvCreateGroup.setOnClickListener(view -> {
            mEasyPopup.dismiss();
//            startActivity(new Intent(getActivity(), NewChatSelectActivity.class));
            startActivity(new Intent(getActivity(), GroupAddMemberActivity.class).putExtra("isCreate", true));
        });
        TextView tvScan = mEasyPopup.findViewById(R.id.tv_scan);
        tvScan.setVisibility(View.GONE);

        TextView tvTransfer = mEasyPopup.findViewById(R.id.tv_file_transfer);
        tvTransfer.setVisibility(View.GONE);
        tvTransfer.setOnClickListener(v -> {
            mEasyPopup.dismiss();
            startActivity(new Intent(getActivity(), FileTransferActivity.class));
        });

        int offsetX = SizeUtils.dp2px(20) - ivRight.getWidth() / 2;
        int offsetY = (titleBar.getHeight() - ivRight.getHeight()) / 2 - SizeUtils.dp2px(2);
        mEasyPopup.showAtAnchorView(ivRight, YGravity.BELOW, XGravity.ALIGN_RIGHT, offsetX, offsetY);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 202) {
                layoutPcStatus.setVisibility(View.GONE);
            } else if (requestCode == 203) {

                String id = data.getExtras().getString("tochatname", null);
                if (id != null) {
                    for (EMConversation conversation : conversationList) {
                        if (conversation.conversationId().equals(id)) {
                            conversationList.remove(conversation);
                            EMClient.getInstance().chatManager().deleteConversation(id, true);
                            refresh();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (NetUtils.hasNetwork(getActivity())) {
            errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
            errorText.setText(R.string.the_current_network);
        }
    }

    //选择点击条目
    private void doSelectClick(int id, EMConversation conversation) {
        //让popupWindow消失
        selectPopupWindow.dismiss();
        if (id == R.id.sticky_conversation) {
            SessionCache.getInstance().setSticky(conversation.conversationId(), conversation.getType());
            conversationList.clear();
            conversationList.addAll(loadConversationList());
            conversationListView.refresh();
        } else if (id == R.id.sticky_cancel) {
            SessionCache.getInstance().cancelSticky(conversation.conversationId(), conversation.getType());
            conversationList.clear();
            conversationList.addAll(loadConversationList());
            conversationListView.refresh();
        } else {
            boolean deleteMessage = false;
            if (id == R.id.delete_message) {
                deleteMessage = true;
            } else if (id == R.id.delete_conversation) {
                deleteMessage = false;
            }
            if (conversation == null) {
                return;
            }
            if (conversation.getType() == EMConversationType.GroupChat) {
                EaseAtMessageHelper.get().removeAtMeGroup(conversation.conversationId());
                EaseUI.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        SessionCache.getInstance().deleteSession(conversation.conversationId(), EMConversationType.GroupChat);
                    }
                });

            } else if (conversation.getType() == EMConversationType.Chat) {
                EaseUI.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        SessionCache.getInstance().deleteSession(conversation.conversationId(), EMConversationType.Chat);
                    }
                });
            }
            DraftManager.getInstance().removeDraft(conversation.conversationId());
            try {
                // delete conversation
                EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), deleteMessage);
                InviteMessageDao inviteMessageDao = new InviteMessageDao();
                inviteMessageDao.deleteMessage(conversation.conversationId());
                // To delete the native stored adked users in this conversation.
                if (deleteMessage) {
                    EaseDingMessageHelperV2.get().delete(conversation);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            refresh();
        }

    }

    @Override
    protected List<EMConversation> loadConversationList() {
        Map<String, EMConversation> conversationMap = EMClient.getInstance().chatManager().getAllConversations();
        List<EMConversation> allConversations = new ArrayList<>();
        synchronized (this) {
            for (EMConversation conversation : conversationMap.values()) {
                if (conversation.getType() == EMConversationType.GroupChat) {
                    GroupBean groupBean = EaseUI.getInstance().getUserProfileProvider().getGroupBean(conversation.conversationId());
                    if (groupBean == null) {
                        continue;
                    }
                }
                if (conversation.getAllMessages().size() > 0) {
                    //判断是否置顶了
                    String stickyTime = AppHelper.getInstance().getStickyTime(conversation.conversationId(), conversation.getType());
                    conversation.setExtField(stickyTime);
                    allConversations.add(conversation);
                } else {
                    conversation.markAllMessagesAsRead();
                }

            }
        }
        Collections.sort(allConversations, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation convLeft, EMConversation convRight) {

                boolean leftSticky = !TextUtils.isEmpty(convLeft.getExtField());
                boolean rightSticky = !TextUtils.isEmpty(convRight.getExtField());
                if (leftSticky && rightSticky) {
                    return Long.compare(getConversationSortTime(convRight), getConversationSortTime(convLeft));
                } else if (rightSticky) {
                    return 1;
                } else if (leftSticky) {
                    return -1;
                } else {
                    long rightTime = convRight.getLastMessage() != null ? convRight.getLastMessage().getMsgTime() : 0L;
                    long leftTime = convLeft.getLastMessage() != null ? convLeft.getLastMessage().getMsgTime() : 0L;
                    return Long.compare(rightTime, leftTime);
                }
            }
        });

        return allConversations;
    }

    private long getConversationSortTime(EMConversation conversation) {
        boolean isSticky = !TextUtils.isEmpty(conversation.getExtField());
        long stickyTime = isSticky ? Long.parseLong(conversation.getExtField()) : 0l;
        long latestMsgTime = conversation.getLastMessage() != null ? conversation.getLastMessage().getMsgTime() : 0;
        return Math.max(latestMsgTime, stickyTime);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mEasyPopup != null && mEasyPopup.isShowing())
            mEasyPopup.dismiss();
        if (selectPopupWindow != null && selectPopupWindow.isShowing())
            selectPopupWindow.dismiss();
        conversationListView.setDraftListener(null);
    }


    private void asyncGetSessions() {
//        int length = EMClient.getInstance().chatManager().getAllConversations().size();
//        if (length > 0) {
//            return;
//        }
        EaseUI.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SessionCache.getInstance().loadSessionCache();
            }
        });

        EMAPIManager.getInstance().getSessions(new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONArray jsonArr = jsonObject.optJSONArray("entities");
                    List<MPSessionEntity> sessionEntities = MPSessionEntity.create(jsonArr);
                    SessionCache.getInstance().saveSessions(sessionEntities);
                    for (MPSessionEntity item : sessionEntities) {
                        String chatType = item.getChatType();
                        if (TextUtils.isEmpty(chatType)) {
                            continue;
                        }
                        if (TextUtils.isEmpty(item.getImId())) {
                            continue;
                        }
                        if (chatType.equals(SessionCache.CHATTYPE_CHAT)) { // singleChat
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(item.getImId(), EMConversationType.Chat, true);
                            if (conversation == null || conversation.getAllMsgCount() == 0) {
                                try {
                                    EMClient.getInstance().chatManager().fetchHistoryMessages(
                                            item.getImId(), EMConversationType.Chat, 20, "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (chatType.equals(SessionCache.CHATTYPE_GROUPCHAT)) { // chatGroups
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(item.getImId(), EMConversationType.GroupChat, true);
                            if (conversation == null || conversation.getAllMsgCount() == 0) {
                                try {
                                    EMClient.getInstance().chatManager().fetchHistoryMessages(
                                            item.getImId(), EMConversationType.GroupChat, 20, "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MPLog.e(TAG, "getSession:" + MPLog.getStackTraceString(e));
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "getSesssions error:" + errorMsg);
            }
        });


    }

    private void requestAllPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(getActivity(), new PermissionsResultAction() {
            @Override
            public void onGranted() {
                MPLog.d(TAG, "All permissions have been granted");
            }

            @Override
            public void onDenied(String permission) {
                MPLog.e(TAG, "Permission:" + permission + "  has been denied");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
