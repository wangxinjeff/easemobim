package com.hyphenate.easemob.im.officeautomation.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.im.officeautomation.domain.VoteMsgEntity;
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsManager;
import com.hyphenate.easemob.im.officeautomation.runtimepermissions.PermissionsResultAction;
import com.hyphenate.easemob.im.officeautomation.ui.VoteCreateActivity;
import com.hyphenate.easemob.im.officeautomation.ui.VoteDetailActivity;
import com.hyphenate.easemob.im.officeautomation.widget.EaseChatVotePresenter;
import com.hyphenate.easemob.imlibs.cache.OnlineCache;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseMessageUtils;
import com.hyphenate.easemob.easeui.domain.DraftEntity;
import com.hyphenate.easemob.easeui.domain.EaseEmojicon;
import com.hyphenate.easemob.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.domain.StickerEntity;
import com.hyphenate.easemob.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.easeui.player.MediaManager;
import com.hyphenate.easemob.easeui.ui.EaseChatFragment;
import com.hyphenate.easemob.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;
import com.hyphenate.easemob.easeui.ui.EaseFilePreviewActivity;
import com.hyphenate.easemob.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easemob.easeui.ui.EaseShowVideoActivity;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.utils.FileHelper;
import com.hyphenate.easemob.easeui.widget.WaterMarkBgView;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easemob.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.im.mp.cache.TenantOptionCache;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.events.EventEMMessageReceived;
import com.hyphenate.easemob.imlibs.mp.events.EventEmojiconChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupDeleted;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupsChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventLocEnded;
import com.hyphenate.easemob.imlibs.mp.events.EventLocNotify;
import com.hyphenate.easemob.imlibs.mp.events.EventLocStarted;
import com.hyphenate.easemob.imlibs.mp.events.EventLocUserRemoved;
import com.hyphenate.easemob.imlibs.mp.events.EventOnLineOffLineQuery;
import com.hyphenate.easemob.imlibs.mp.events.EventTenantOptionChanged;
import com.hyphenate.easemob.imlibs.mp.events.MessageChanged;
import com.hyphenate.easemob.im.mp.location.LatLngManager;
import com.hyphenate.easemob.im.mp.location.LocServiceManager;
import com.hyphenate.easemob.im.mp.location.RTLocationManager;
import com.hyphenate.easemob.im.mp.manager.DraftManager;
import com.hyphenate.easemob.im.mp.manager.StickerManager;
import com.hyphenate.easemob.im.mp.ui.ForwardActivity;
import com.hyphenate.easemob.im.mp.ui.burn.BurnMsgPreviewActivity;
import com.hyphenate.easemob.im.mp.ui.location.ShareLocationActivity;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.MPMessageUtils;
import com.hyphenate.easemob.im.mp.utils.NetworkUtil;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.im.mp.view.contextmenu.CustomLayout;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.ExtUserType;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.http.BaseRequest;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.ui.CameraActivity;
import com.hyphenate.easemob.im.officeautomation.ui.ChatActivity;
import com.hyphenate.easemob.im.officeautomation.ui.ChatDetailsActivity;
import com.hyphenate.easemob.im.officeautomation.ui.ContactDetailsActivity;
import com.hyphenate.easemob.im.officeautomation.ui.EMBaiduMapActivity;
import com.hyphenate.easemob.im.officeautomation.ui.ImageGridActivity;
import com.hyphenate.easemob.im.officeautomation.ui.PickAtUserActivity;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupAddMemberActivity;
import com.hyphenate.easemob.im.officeautomation.ui.group.GroupDetailInfoActivity;
import com.hyphenate.easemob.im.officeautomation.utils.CommonUtils;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.hyphenate.easemob.im.officeautomation.widget.EaseChatBurnPresenter;
import com.hyphenate.easemob.im.officeautomation.widget.EaseChatCardPresenter;
import com.hyphenate.easemob.im.officeautomation.widget.EaseChatHsitoryPresenter;
import com.hyphenate.easemob.im.officeautomation.widget.EaseChatInvitePresenter;
import com.hyphenate.easemob.im.officeautomation.widget.EaseChatLocationPresenter;
import com.hyphenate.easemob.im.officeautomation.widget.EaseChatNoticePresenter;
import com.hyphenate.easemob.im.officeautomation.widget.EaseChatRTLocPresenter;
import com.hyphenate.easemob.im.officeautomation.widget.EaseChatRecallPresenter;
import com.hyphenate.easemob.im.officeautomation.widget.EaseChatStickerPresenter;
import com.hyphenate.util.DensityUtil;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.ImageUtils;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ChatFragment extends EaseChatFragment implements EaseChatFragmentHelper, RTLocationManager.LocationListener {

    // constant start from 11 to avoid conflict with constant in base class
    private static final int ITEM_VIDEO = 11;
    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOTE = 13;

    private static final int ITEM_LOCATION = 16;
    private static final int ITEM_BURN_AFTER_READING = 17;
    private static final int ITEM_NAME_CARD = 25;
    private static final int ITEM_NAME_SCHEDULE = 26;
    private static final int ITEM_NAME_TASK = 27;

    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;
    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    private static final int REQUEST_CODE_CHAT_DETAIL = 16;
    private static final int REQUEST_CODE_MAP = 17;
    private static final int REQUEST_CODE_SELECT_FILE_FROM_FRAGMENT = 20;
    private static final int REQUEST_CODE_VOTE = 21;


    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 5;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 6;
    private static final int MESSAGE_TYPE_CONFERENCE_INVITE = 7;
    private static final int MESSAGE_TYPE_RECALL = 9;
    private static final int MESSAGE_TYPE_INVITE = 10;
    private static final int MESSAGE_TYPE_SENT_CHAT_HISTORY = 11;
    private static final int MESSAGE_TYPE_RECV_CHAT_HISTORY = 12;
    private static final int MESSAGE_TYPE_NOTICE = 13;
    private static final int MESSAGE_TYPE_SENT_STICKER = 14;
    private static final int MESSAGE_TYPE_RECV_STICKER = 15;

    private static final int MESSAGE_TYPE_SENT_BURN = 16;
    private static final int MESSAGE_TYPE_RECV_BURN = 17;

    private static final int MESSAGE_TYPE_SENT_RT_LOC = 18;
    private static final int MESSAGE_TYPE_RECV_RT_LOC = 19;
    private static final int MESSAGE_TYPE_SENT_CARD = 20;
    private static final int MESSAGE_TYPE_RECV_CARD = 21;
    private static final int MESSAGE_TYPE_SENT_VOTE = 22;
    private static final int MESSAGE_TYPE_RECV_VOTE = 23;

    //    private QPopuWindow.Builder qPopup;
    private XPopup.Builder xPopup;
    private BasePopupView xPopupView;

    private float rawX, rawY;

    /**
     * 接收ChatActivity的Touch回调的对象，重写其中的onTouchEvent函数
     */
    ChatActivity.MyTouchListener myTouchListener = new ChatActivity.MyTouchListener() {
        @Override
        public void onTouchEvent(MotionEvent event) {
            //处理手势事件（根据个人需要去返回和逻辑的处理）
            rawX = event.getRawX();
            rawY = event.getRawY();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MPEventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RTLocationManager.getInstance().removeListener(this);
        LocServiceManager.getInstance().unBindService(getContext());
        LatLngManager.getInstance().unRegister();
        MPEventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState,
//                (chatType != EaseConstant.CHATTYPE_CHATROOM));
        return super.onCreateView(inflater, container, savedInstanceState,
                true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RTLocationManager.getInstance().addListener(this);
        LocServiceManager.getInstance().bindService(getContext());
        LatLngManager.getInstance().setUsernameAndChatType(toChatUsername, chatType);
        locTipViewContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShareLocationActivity.class)
                        .putExtra(Constant.EXTRA_USER_ID, toChatUsername));
                startRtLocation();
            }
        });
        refreshLocView();

        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            onUserStatusUpdated(null);
            ArrayList<String> mUserIds = new ArrayList<>();
            mUserIds.add(toChatUsername);
            EMClient.getInstance().chatManager().getUserStatusWithUserIds(mUserIds, new EMCallBack() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(int i, String s) {
                }

                @Override
                public void onProgress(int i, String s) {
                }
            });
        } else {
            titleBar.setStatusVisible(View.GONE);
        }
    }

//    private void setWaterMarkBg() {
//        if (messageList == null) {
//            return;
//        }
//        if (TenantOptionCache.getInstance().isShowWaterMark()) {
//            messageList.setBackground(WaterMarkBg.create(getContext()));
//        } else {
//            messageList.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//        }
//    }

    void refreshWaterMark(Activity activity) {
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return;
        }
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
            refreshWaterMark(getActivity());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupChanged(EventGroupsChanged groupsChanged) {
        refreshMessageList();
        if (chatType == Constant.CHATTYPE_GROUP) {
            fetchGroupInfoFromServer();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserStatusUpdated(EventOnLineOffLineQuery event) {
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            Boolean isOnline = OnlineCache.getInstance().get(toChatUsername);
            if (isOnline != null && isOnline) {
                titleBar.setStatusVisible(View.VISIBLE, R.drawable.ease_icon_status_online);
            } else {
                titleBar.setStatusVisible(View.VISIBLE, R.drawable.ease_icon_status_offline);
            }
        } else {
            titleBar.setStatusVisible(View.GONE);
        }
    }


    private void doTitleLeftClick() {
        onBackPressed();
    }

    @Override
    protected void setUpView() {
        setChatFragmentHelper(this);
        super.setUpView();
//        setWaterMarkBg();
        refreshWaterMark(getActivity());

        /** 触摸事件的注册 */
        if (getActivity() != null) {
            ((ChatActivity) getActivity()).registerMyTouchListener(myTouchListener);
        }
        titleBar.setLeftImageResource(R.drawable.mp_ic_back_black);
        // set click listener
        titleBar.setLeftLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!LocServiceManager.getInstance().isStarted()) {
                } else {
                    LatLngManager.getInstance().removeUser(toChatUsername, EMClient.getInstance().getCurrentUser());
                    LocServiceManager.getInstance().stopLocation();
                }
                doTitleLeftClick();
            }
        });
        addEmojiconToMenu();
        //添加自定义表情
//        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(new EmojiconExampleGroupData().getData());
//        emojiconGroupList.add(new EaseEmojiconGroupEntity(R.drawable.ee_1,  Arrays.asList(EaseDefaultEmojiconDatas.getData())));
//        ((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).replaceEmojiconGroup(2, new StickerGroupData().createData());
        titleBar.setRightImageResource(R.drawable.mp_ic_more);
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
//            inputMenu.getPrimaryMenu().getEditText().setFilters(new InputFilter[]{new MyInputFilter()});
            inputMenu.getPrimaryMenu().getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    atFunction(s, start, before, count);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        if (DraftManager.getInstance().hasDraft(toChatUsername)) {
            DraftEntity draftEntity = DraftManager.getInstance().getDraftEntity(toChatUsername);
            if (draftEntity != null) {
                String content = draftEntity.getContent();
                String referenceMsg = DraftManager.getInstance().getReferenceMsgId(draftEntity);
                if (!TextUtils.isEmpty(content)) {
                    EditText editText = inputMenu.getPrimaryMenu().getEditText();
                    editText.setText(EaseSmileUtils.getSmiledText(getContext(), content));
                    editText.setSelection(editText.getText().length());
                    String extra = draftEntity.getExtra();
                    List<AtUserBean> atUserBeans = getAtList(extra);
                    mAtUserBeans.addAll(atUserBeans);
                }
                if(!TextUtils.isEmpty(referenceMsg)){
                    inputMenu.showReference(EMClient.getInstance().chatManager().getMessage(referenceMsg));
                }
            }
        }

        boolean isTablet = CommonUtils.isTablet((BaseActivity) getActivity());
        if(isTablet){
            ((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).setEmojiconViewHeight((int)EaseCommonUtils.dip2px(getActivity(), 250f));
        } else {
            ((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).setEmojiconViewHeight((int)EaseCommonUtils.dip2px(getActivity(), 150f));
        }
    }

    /**
     * 添加自定义表情
     */
    private void addEmojiconToMenu() {
        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(new EaseEmojiconGroupEntity(R.drawable.ee_1, arrayToList(EaseDefaultEmojiconDatas.getData())));
//        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(new EmojiconGroupData().createData());
//        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(new StickerGroupData().createData());
    }

    private List<EaseEmojicon> arrayToList(EaseEmojicon[] easeEmojicons) {
        List<EaseEmojicon> easeEmojiconList = new ArrayList<>();
        for (EaseEmojicon easeEmojicon : easeEmojicons) {
            easeEmojiconList.add(easeEmojicon);
        }
        return easeEmojiconList;
    }

    private void resetEmojiconMenu() {
        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).reset();
        addEmojiconToMenu();
        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).selectedTo(0);
    }

    private List<AtUserBean> getAtList(String jsonExtra) {
        List<AtUserBean> atUserBeans = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonExtra);
            JSONArray jsonArray = jsonObject.getJSONArray(EaseConstant.DRAFT_EXT_AT_LIST);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItem = jsonArray.getJSONObject(i);
                AtUserBean atUserBean = new AtUserBean();
                atUserBean.username = jsonItem.optString("username");
                atUserBean.realName = jsonItem.optString("realName");
                atUserBean.jsonData = getUserDataFromUsername(atUserBean.username);
                atUserBeans.add(atUserBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return atUserBeans;
    }

    private JSONObject getUserDataFromUsername(String username) {
        EaseUser userEntity = UserProvider.getInstance().getEaseUser(username);
        if (userEntity == null) {
            return null;
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", userEntity.getId());
            jsonObj.put("imUsername", userEntity.getUsername());
            jsonObj.put("realName", userEntity.getNickname());
            jsonObj.put("userId", userEntity.getId());
            jsonObj.put("appkey", EMClient.getInstance().getOptions().getAppKey());
            jsonObj.put("createTime", System.currentTimeMillis());
            jsonObj.put("lastUpdateTime", System.currentTimeMillis());
            jsonObj.put("tenantId", BaseRequest.getTenantId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }


    private String getDraftExtra(DraftEntity draftEntity, List<AtUserBean> atUserBeans) {
        String extra = draftEntity.getExtra();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            if(!TextUtils.isEmpty(extra)){
                jsonObject = new JSONObject(extra);
            }
            for (AtUserBean atUserBean : atUserBeans) {
                JSONObject jsonItem = new JSONObject();
                jsonItem.put("username", atUserBean.username);
                jsonItem.put("realName", atUserBean.realName);
                jsonArray.put(jsonItem);
            }
            jsonObject.put(EaseConstant.DRAFT_EXT_AT_LIST, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    @Override
    public void onEditTextChanged(CharSequence s, int start, int before, int count) {
        DraftEntity draftEntity = DraftManager.getInstance().getDraftEntity(toChatUsername);
        if(draftEntity == null){
            draftEntity = new DraftEntity();
            draftEntity.setId(toChatUsername);
        }
        if (s instanceof SpannableStringBuilder) {
            draftEntity.setContent(s.toString());
            draftEntity.setExtra(getDraftExtra(draftEntity, mAtUserBeans));
        } else {
            draftEntity.setContent(s.toString());
        }

        DraftManager.getInstance().saveDraft(draftEntity);
    }

    @Override
    public void onPositionChanged(double lat, double lng, float radius, float direction) {
        // 添加自己的位置
        LatLngManager.getInstance().addUser(toChatUsername, EMClient.getInstance().getCurrentUser(), lat, lng, radius, direction);

        EMMessage refreshPosMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        refreshPosMsg.setTo(toChatUsername);
        if (chatType == Constant.CHATTYPE_GROUP) {
            refreshPosMsg.setChatType(EMMessage.ChatType.GroupChat);
        }
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(Constant.CMD_ACTION_REFRESH_LOCATION);
        cmdMessageBody.deliverOnlineOnly(true);
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(Constant.CMD_ACTION_REFRESH_LOCATION_LAT, lat);
            jsonObj.put(Constant.CMD_ACTION_REFRESH_LOCATION_LNG, lng);
            jsonObj.put(Constant.CMD_ACTION_REFRESH_LOCATION_RADIUS, radius);
            jsonObj.put(Constant.CMD_ACTION_REFRESH_LOCATION_DIRECTION, direction);

            refreshPosMsg.setAttribute(Constant.CMD_ACTION_REFRESH_LOCATION_LOC, jsonObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosMsg.addBody(cmdMessageBody);
        MessageUtils.sendMessage(refreshPosMsg);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocEvent(EventLocStarted event) {
//        if (!isSharingLoc) {
//            locTipViewContainer.setVisibility(View.VISIBLE);
//            tvTipViewTxt.setText("你正在共享位置");
//            isSharingLoc = true;
//        }
        sendLocMessageTip();
    }

    private void startRtLocation() {
        if (!LocServiceManager.getInstance().isStarted()) {
            LocServiceManager.getInstance().startLocation();
        }
    }

    private void sendLocMessageTip() {
        EMMessage message = EMMessage.createTxtSendMessage(Constant.MESSAGE_CONTENT_REALTIME_LOC_START, toChatUsername);
        if (chatType == Constant.CHATTYPE_GROUP) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Constant.EXT_LOCATION_STATE, Constant.EXT_LOCATION_STATE_START);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message.setAttribute(Constant.EXT_LOCATION, jsonObj);
        doAddSendExtField(message);
        MessageUtils.sendMessage(message);
    }

    private void endSharedLocation() {
        if (locTipViewContainer.getVisibility() == View.VISIBLE) {
            locTipViewContainer.setVisibility(View.GONE);
        }
        EMMessage message = EMMessage.createTxtSendMessage(Constant.MESSAGE_CONTENT_REALTIME_LOC_END, toChatUsername);
        if (chatType == Constant.CHATTYPE_GROUP) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        message.setMsgTime(System.currentTimeMillis());
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Constant.EXT_LOCATION_STATE, Constant.EXT_LOCATION_STATE_END);
        } catch (JSONException e) {
        }
        message.setMsgId(UUID.randomUUID().toString());
        message.setAttribute(Constant.EXT_LOCATION, jsonObj);
        message.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
        message.setStatus(EMMessage.Status.SUCCESS);
        EMClient.getInstance().chatManager().saveMessage(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocEndEvent(EventLocEnded event) {
        if (LatLngManager.getInstance().getSize() == 0) {
            endSharedLocation();
        }
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMessage.addBody(new EMCmdMessageBody(Constant.CMD_ACTION_END_LOCATION));
        cmdMessage.setTo(toChatUsername);
        if (chatType == Constant.CHATTYPE_GROUP) {
            cmdMessage.setChatType(EMMessage.ChatType.GroupChat);
        }
        JSONObject jsonExt = new JSONObject();
        try {
            jsonExt.put(Constant.EXT_LOCATION_STATE, "endLoc");
        } catch (JSONException e) {
        }
        cmdMessage.setAttribute(Constant.EXT_LOCATION, jsonExt);
        MessageUtils.sendMessage(cmdMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocUserRemoved(EventLocUserRemoved event) {
        Set<String> usernames = LatLngManager.getInstance().getUsernames();
        int size = usernames.size();
        if (size == 0) {
            endSharedLocation();
        } else if (size > 1) {
            tvTipViewTxt.setText(size + "人在共享位置");
        } else {
            for (String item : usernames) {
                if (EMClient.getInstance().getCurrentUser().equals(item)) {
                    tvTipViewTxt.setText("你在共享位置");
                } else {
                    EaseUser user = UserProvider.getInstance().getEaseUser(item);
                    tvTipViewTxt.setText(user.getNickname() + "在共享位置");
                }
            }
        }
    }

    private void refreshLocView() {
        Set<String> usernames = LatLngManager.getInstance().getUsernames();
        int size = usernames.size();
        if (size > 1) {
            locTipViewContainer.setVisibility(View.VISIBLE);
            tvTipViewTxt.setText(size + "人在共享位置");
        } else if (size == 1) {
            locTipViewContainer.setVisibility(View.VISIBLE);
            for (String item : usernames) {
                if (EMClient.getInstance().getCurrentUser().equals(item)) {
                    tvTipViewTxt.setText("你在共享位置");
                } else {
                    EaseUser user = UserProvider.getInstance().getEaseUser(item);
                    tvTipViewTxt.setText(user.getNickname() + "在共享位置");
                }
            }
        } else {
            locTipViewContainer.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocUserChanged(EventLocNotify event) {
        refreshLocView();
    }


    /**
     * 识别输入框的是不是@符号
     */
    private class MyInputFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.toString().equalsIgnoreCase("@")
                    || source.toString().equalsIgnoreCase("＠")) {
                startActivityForResult(new Intent(getActivity(), PickAtUserActivity.class).
                        putExtra("groupId", toChatUsername), REQUEST_CODE_SELECT_AT_USER);
            }
            return source;
        }
    }


    /**
     * 群@ 功能
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    private void atFunction(CharSequence s, int start, int before, int count) {
        if (chatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        String text = String.valueOf(s);
        // 增加符号
        if (before == 0) {
            String addText = text.substring(start, start + count);
            if (addText.equals("@")) {
                startActivityForResult(new Intent(getActivity(), PickAtUserActivity.class).
                        putExtra("groupId", toChatUsername), REQUEST_CODE_SELECT_AT_USER);
            }
        } else {
            // 减少符号
            String delText = strContent.substring(start, start + before);
            if (delText.equals((char) (8197) + "") && start != 0) {
                int index = text.lastIndexOf('@', start - 1);
                if (index != -1) {
                    text = text.substring(0, index) + text.substring(start, text.length());
                    inputMenu.getPrimaryMenu().getEditText().setText(text);
                    inputMenu.getPrimaryMenu().getEditText().setSelection(index);
                }
            }
        }
        strContent = text;
    }


    private String strContent = "";//存储当前消息

    @Override
    protected void registerExtendMenuItem() {
        //use the menu in base class
        super.registerExtendMenuItem();
        if (toChatUsername == null) {
            return;
        }
        boolean isFileHelper = MPClient.get().isFileHelper(toChatUsername);

        //extend menu items
//        inputMenu.registerExtendMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, ITEM_VIDEO, extendMenuItemClickListener);

        if (chatType == Constant.CHATTYPE_SINGLE) {
            if (!isFileHelper) {
                inputMenu.registerExtendMenuItem(R.string.attach_burn_after_read, R.drawable.mp_ic_ext_fire, ITEM_BURN_AFTER_READING, extendMenuItemClickListener);
            }
        } else if (chatType == Constant.CHATTYPE_GROUP) {
            inputMenu.registerExtendMenuItem(R.string.vote, R.drawable.mp_ic_ext_vote, ITEM_VOTE, extendMenuItemClickListener);
        }
        inputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.mp_ic_ext_file, ITEM_FILE, extendMenuItemClickListener);
        if (!isFileHelper) {
            inputMenu.registerExtendMenuItem(R.string.name_card, R.drawable.mp_ic_ext_card, ITEM_NAME_CARD, extendMenuItemClickListener);
//            inputMenu.registerExtendMenuItem(R.string.attach_location, R.drawable.mp_ic_ext_loc, ITEM_LOCATION, extendMenuItemClickListener);
        }
//        inputMenu.registerExtendMenuItem(R.string.attach_schedule, R.drawable.mp_ic_ext_schedule, ITEM_NAME_SCHEDULE, extendMenuItemClickListener);
//        inputMenu.registerExtendMenuItem(R.string.attach_task, R.drawable.mp_ic_ext_task, ITEM_NAME_TASK, extendMenuItemClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCAL && resultCode == PickerConfig.RESULT_CODE) {
            ArrayList<Media> retMedias = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            if (retMedias != null) {
                for (Media media : retMedias) {
                    if (media.mediaType == 0 || media.mediaType == 1) {
                        if (!TextUtils.isEmpty(media.path)) {
                            boolean orignalImage = data.getBooleanExtra(PickerConfig.EXTRA_RESULT_FULL_SIZE, false);
                            sendImageMessage(media.path, orignalImage);
                        }
                    } else if (media.mediaType == 3) {
                        try {
                            if (getActivity() != null) {
                                String thumbPath = ImageUtils.saveVideoThumb(new File(media.path), getResources().getDimensionPixelSize(R.dimen.dp_120), getResources().getDimensionPixelSize(R.dimen.dp_120), MediaStore.Images.Thumbnails.MINI_KIND);
                                sendVideoMessage(media.path, thumbPath, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_FILE_FROM_FRAGMENT:
                    //If it is a file selection mode, you need to get the path collection of all the files selected
                    //List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//Constant.RESULT_INFO == "paths"
                    List<String> list = data.getStringArrayListExtra("paths");
                    if (list != null && list.size() > 0) {
                        String filePath = list.get(list.size() - 1);
                        if (filePath != null) {
                            File file = new File(filePath);
                            if (!file.exists() || !file.canRead()) {
                                ToastUtils.showShort(getString(R.string.file_cannot_read));
                                return;
                            }

                            if (file.length() == 0) {
                                ToastUtils.showShort(getString(R.string.file_zero_send_failed));
                                return;
                            }
//                            if (EaseConstant.MSG_FILE_SEND_LIMIT > 0 && file.length() > (EaseConstant.MSG_FILE_SEND_LIMIT * 1024 * 1024)) {
//                                ToastUtils.showShort(getString(R.string.send_file_limited, String.valueOf(EaseConstant.MSG_FILE_SEND_LIMIT)));
//                                return;
//                            }

                            final FileHelper fileHelper = new FileHelper(getActivity());
                            final File destFile = new File(MPPathUtil.getInstance().getFilePath(), file.getName());
                            try {
                                boolean isCopySuccess = fileHelper.copyFileTo(file, destFile);
                                if (isCopySuccess) {
                                    sendFileMessage(destFile.getPath());
                                } else {
                                    ToastUtils.showShort(getString(R.string.send_fail));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                ToastUtils.showShort(getString(R.string.send_fail));
                            }
                        }

                        //If it is a folder selection mode, you need to get the folder path of your choice
//                        String path = data.getStringExtra("path");
                    }

                    break;
                case REQUEST_CODE_SELECT_VIDEO: //send the video
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        if (videoPath == null) {
                            return;
                        }
                        File videoFile = new File(videoPath);
                        if (!videoFile.exists()) {
                            return;
                        }
                        if (videoFile.length() > EaseConstant.MSG_FILE_SEND_LIMIT) {
                            ToastUtils.showShort(R.string.send_file_limited);
                            return;
                        }

                        File file = new File(MPPathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFileByUri(uri);
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String username = data.getStringExtra("username");
                        String nick = data.getStringExtra("nick");
                        AtUserBean atUserBean = new AtUserBean();
                        atUserBean.username = username;
                        atUserBean.realName = nick;
                        atUserBean.jsonData = getUserDataFromUsername(username);
                        mAtUserBeans.add(atUserBean);
                        EditText mEditSendMsg = inputMenu.getPrimaryMenu().getEditText();
                        // 将所@的名字添加到光标的后面
                        StringBuilder sb = new StringBuilder(mEditSendMsg.getText().toString());
                        int selectionEnd = mEditSendMsg.getSelectionEnd();
                        String strAt = nick + (char) (8197);
                        sb.insert(selectionEnd, strAt);
                        mEditSendMsg.setText(sb.toString());
                        mEditSendMsg.setSelection(selectionEnd + strAt.length());
                    }
                    break;
                case REQUEST_CODE_MAP:
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    String locationAddress = data.getStringExtra("address");
                    String locImage = data.getStringExtra("locImage");
                    if (locationAddress != null && !locationAddress.equals("")) {
                        sendLocationMessage(latitude, longitude, locationAddress, locImage);
                    } else {
                        ToastUtils.showShort(R.string.unable_to_get_loaction);
                    }
                    break;

                case 1000:
                    Log.i("info", "bean:" + data.getParcelableExtra("card"));
                    MPUserEntity entitiesBean = data.getParcelableExtra("card");

                    EMMessage emMessage = EMMessage.createSendMessage(EMMessage.Type.TXT);
                    emMessage.addBody(new EMTextMessageBody("名片消息"));
                    emMessage.setTo(toChatUsername);
                    if (chatType == Constant.CHATTYPE_GROUP) {
                        emMessage.setChatType(EMMessage.ChatType.GroupChat);
                    }

                    JSONObject extJson = new JSONObject();
                    JSONObject extMsgJson = new JSONObject();
                    JSONObject contentJson = new JSONObject();
                    try {
                        contentJson.put("user_id", entitiesBean.getId());
                        contentJson.put("im_user_id", entitiesBean.getImUserId());
                        contentJson.put("user_avatar", entitiesBean.getAvatar());
                        contentJson.put("realName", entitiesBean.getRealName());
                        extMsgJson.put("content", contentJson);
                        extMsgJson.put("type", "people_card");
                        extJson.put("extMsg", extMsgJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    emMessage.setAttribute(EaseConstant.EXT_EXTMSG, extMsgJson);
                    doAddSendExtField(emMessage);
                    MessageUtils.sendMessage(emMessage);
                    refreshMessageListSelectLast();
                    break;
                case REQUEST_CODE_VOTE:
                    String json = data.getStringExtra(EaseConstant.MSG_EXT_VOTE);
                    if(TextUtils.isEmpty(json)){
                        return;
                    }
                    JSONObject voteJson = null;
                    EMMessage message = EMMessage.createTxtSendMessage("投票消息", toChatUsername);
                    JSONObject extMsg = new JSONObject();
                    try {
                        voteJson = new JSONObject(json);
                        extMsg.put(EaseConstant.EXT_MSGTYPE, "vote");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    message.setAttribute(EaseConstant.EXT_EXTMSG, extMsg);
                    message.setAttribute(EaseConstant.MSG_EXT_VOTE, voteJson);
                    doAddSendExtField(message);
                    sendMessage(message);
                    break;
                default:
                    break;
            }
        }

        if (requestCode == REQUEST_CODE_CHAT_DETAIL) {
            //单聊详情返回
        }
    }

    private boolean checkContainVoiceOrFileMsg(ArrayList<String> forwardMsgIds) {
        if (forwardMsgIds != null && !forwardMsgIds.isEmpty()) {
            for (String msgId : forwardMsgIds) {
                EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
                if (message.getType() == EMMessage.Type.VOICE || message.getType() == EMMessage.Type.FILE) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void forwardOneByOne() {
        if (checkMap == null || checkMap.isEmpty()) {
            return;
        }
        //逐条转发
        ArrayList<String> list = getSortForwardList();
        boolean isContainVoice = checkContainVoiceOrFileMsg(list);
        if (!isContainVoice) {
            Intent intent = new Intent(getActivity(), ForwardActivity.class);
            intent.putExtra("forwardMsgIds", list);
            intent.putExtra("current_id", conversation.conversationId());
            startActivity(intent);
            showInputMenu();
        } else {
            new AlertDialog.Builder(getContext()).setMessage("你选择的消息中，语音或文件不能转发给朋友，是否继续？").setPositiveButton(R.string.button_send, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), ForwardActivity.class);
                    intent.putExtra("forwardMsgIds", list);
                    intent.putExtra("current_id", conversation.conversationId());
                    startActivity(intent);
                    showInputMenu();
                }
            }).setNegativeButton(R.string.cancel, null).show();
        }
    }

    //获取排序后的转发列表
    @Override
    public void forwardCombine() {
        if (checkMap == null || checkMap.isEmpty()) {
            return;
        }
        //合并转发
        ArrayList<String> list = getSortForwardList();
        Intent intent = new Intent(getActivity(), ForwardActivity.class);
        intent.putExtra("current_id", conversation.conversationId());
        intent.putExtra("forwardMsgIds", list);
        LoginUser loginUser = UserProvider.getInstance().getLoginUser();
        if (loginUser == null) {
            return;
        }
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            String loginRealName = loginUser.getNick();
            String me = TextUtils.isEmpty(loginRealName) ? loginUser.getNickname() : loginRealName;
            intent.putExtra("combine_title", String.format(getString(R.string.forward_combine_title), me, EaseUserUtils.getUserInfo(toChatUsername).getNickname()));
        } else {
            GroupBean groupInfo = EaseUserUtils.getGroupInfo(toChatUsername);
            intent.putExtra("combine_title", String.format(getString(R.string.forward_combine_group_title), groupInfo == null ? toChatUsername : groupInfo.getNick()));
        }
        startActivity(intent);
        showInputMenu();
    }

    @NonNull
    private ArrayList<String> getSortForwardList() {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<Pair<Long, EMMessage>> forwardList = new ArrayList<>();
        for (EMMessage message :
                checkMap.values()) {
            long msgTime = message.getMsgTime();
            Pair<Long, EMMessage> pair = new Pair<>(msgTime, message);
            forwardList.add(pair);
        }
        //按时间排序
        sortEMMessageByTime(forwardList);

        for (int i = 0; i < forwardList.size(); i++) {
            Pair<Long, EMMessage> messagePair = forwardList.get(i);
            EMMessage second = messagePair.second;
            list.add(second.getMsgId());
        }
        return list;
    }

    /**
     * sort forward list according time stamp of last message
     *
     * @param messageList
     */
    protected void sortEMMessageByTime(List<Pair<Long, EMMessage>> messageList) {
        Collections.sort(messageList, new Comparator<Pair<Long, EMMessage>>() {
            @Override
            public int compare(final Pair<Long, EMMessage> con1, final Pair<Long, EMMessage> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return -1;
                } else {
                    return 1;
                }
            }

        });
    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        message.setAttribute("msgType", message.getType().toString().toLowerCase(Locale.ROOT));
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return new CustomChatRowProvider();
    }

    @Override
    public void onReferenceMsgClick(EMMessage message) {
        if(message != null) {
            Intent intent = null;
            switch (message.getType()) {
                case TXT:
                    EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                    if (EaseMessageUtils.isBurnMessage(message)) {
                        boolean readed = message.getBooleanAttribute("readed", false);
                        if (readed) {
                            if (message.direct() == EMMessage.Direct.RECEIVE) {
                                MyToast.showToast("消息已焚毁");
                            } else {
                                MyToast.showToast("对方已阅，消息已焚毁");
                            }
                        } else {
                            try {
                                intent = new Intent();
                                intent.setClass(getContext(), BurnMsgPreviewActivity.class);
                                intent.putExtra("msgId", message.getMsgId());
                                startActivity(intent);
                            } catch (Exception ignored) {
                            }
                        }
                    } else if (EaseMessageUtils.isNameCard(message)) {
                        JSONObject extJson;
                        try {
                            extJson = new JSONObject(message.getJSONObjectAttribute(Constant.EXT_EXTMSG).toString());
                            JSONObject cardMsg = extJson.getJSONObject("content");

                            intent = new Intent(getContext(), ContactDetailsActivity.class);
                            intent.putExtra("imUserId", cardMsg.getString("im_user_id"));
                            startActivity(intent);
                        } catch (JSONException | HyphenateException e) {
                            e.printStackTrace();
                        }
                    } else {
                        TextDialogFragment.Companion.showDialog((BaseActivity) getContext(), txtBody.getMessage());
                    }
                    break;
                case IMAGE:
                    if (EaseMessageUtils.isBurnMessage(message)) {
                        boolean readed = message.getBooleanAttribute("readed", false);
                        if (readed) {
                            if (message.direct() == EMMessage.Direct.RECEIVE) {
                                MyToast.showToast("消息已焚毁");
                            } else {
                                MyToast.showToast("对方已阅，消息已焚毁");
                            }
                        } else {
                            intent = new Intent(getContext(), EaseShowBigImageActivity.class);
                            String msgId = message.getMsgId();
                            intent.putExtra("messageId", msgId);
                            intent.putExtra(Constant.BURN_AFTER_READING_DESTORY_MSGID, msgId);
                            startActivity(intent);
                        }
                    } else {
                        intent = new Intent(getContext(), EaseShowBigImageActivity.class);
                        intent.putExtra("messageId", message.getMsgId());
                        intent.putExtra("convId", message.conversationId());
                        startActivity(intent);
                    }
                    break;
                case VIDEO:
                    intent = new Intent(getContext(), EaseShowVideoActivity.class);
                    intent.putExtra("msgId", message.getMsgId());
                    startActivity(intent);
                    break;
                case FILE:
                    intent = new Intent(getContext(), EaseFilePreviewActivity.class);
                    intent.putExtra("msg", message);
                    startActivity(intent);
                    break;
                case LOCATION:
                    EMLocationMessageBody locBody = (EMLocationMessageBody) message.getBody();
                    intent = new Intent(getContext(), EMBaiduMapActivity.class);
                    intent.putExtra("latitude", locBody.getLatitude());
                    intent.putExtra("longitude", locBody.getLongitude());
                    intent.putExtra("address", locBody.getAddress());
                    startActivity(intent);
                    break;
                case VOICE:
                    EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
                    File file = new File(voiceBody.getLocalUrl());
                    if (!file.exists()) {
                        if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING || voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                            Toast.makeText(getContext(), R.string.is_down_please_wait, Toast.LENGTH_SHORT).show();
                        } else {
                            EMClient.getInstance().chatManager().downloadAttachment(message);
                        }
                        return;
                    }
                    MediaManager.getManager().release();
                    MediaManager.getManager().playSound(voiceBody.getLocalUrl(), 0, new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                        }
                    });
                    break;
            }
            if (message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            MyToast.showToast("引用内容不存在");
        }
    }

    @Override
    protected void onRealAddStickerClicked() {
        super.onRealAddStickerClicked();
    }

    @Override
    protected void onRealStickerClicked(EaseEmojicon emojicon) {
        super.onRealStickerClicked(emojicon);
        EMMessage emMessage = EMMessage.createSendMessage(EMMessage.Type.TXT);
        emMessage.addBody(new EMTextMessageBody("[动画表情]"));
        emMessage.setTo(toChatUsername);
        if (chatType == Constant.CHATTYPE_GROUP) {
            emMessage.setChatType(EMMessage.ChatType.GroupChat);
        }

        JSONObject msgContent = new JSONObject();
        JSONObject sizeJson = new JSONObject();
        try {
            msgContent.put("remote_url", emojicon.getRemoteUrl());
            msgContent.put("thumb_url", emojicon.getThumbnailUrl());
            if (emojicon.getWidth() > 0) {
                sizeJson.put("w", emojicon.getWidth());
                sizeJson.put("h", emojicon.getHeight());
                msgContent.put("size", sizeJson);
            }
            msgContent.put("md5", emojicon.getIdentityCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(EaseConstant.EXT_MSGTYPE, "sticker");
            jsonObj.put(EaseConstant.EXT_MSGCONTENT, msgContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        emMessage.setAttribute(EaseConstant.EXT_EXTMSG, jsonObj);
        doAddSendExtField(emMessage);
        MessageUtils.sendMessage(emMessage);
        refreshMessageListSelectLast();
    }

    @Override
    public void onEnterToChatDetails() {
        if (chatType == Constant.CHATTYPE_GROUP) {
            final GroupBean groupBean = EaseUserUtils.getGroupInfo(toChatUsername);
            if (groupBean == null) {
                return;
            }
            startActivityForResult(
                    (new Intent(getActivity(), GroupDetailInfoActivity.class)
                            .putExtra("isRegion", groupBean.isCluster())
                            .putExtra("groupId", groupBean.getGroupId())),
                    REQUEST_CODE_GROUP_DETAIL);
        } else if (chatType == Constant.CHATTYPE_SINGLE) {
            startActivityForResult(new Intent(getActivity(), ChatDetailsActivity.class).putExtra("conversationId", toChatUsername), REQUEST_CODE_CHAT_DETAIL);
        }
    }

    @Override
    public void onAvatarClick(String imUsername) {
        //handling when user click avatar
        Intent intent = new Intent(getActivity(), ContactDetailsActivity.class);
        intent.putExtra("imUserId", imUsername);
        startActivity(intent);
    }

    @Override
    public void onAvatarLongClick(String username) {
        if (chatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EaseUser easeUser = EaseUserUtils.getUserInfo(username);
        String nick = "@" + easeUser.getNickname() + " ";
        AtUserBean atUserBean = new AtUserBean();
        atUserBean.username = easeUser.getUsername();
        atUserBean.realName = easeUser.getNickname();
        atUserBean.jsonData = getUserDataFromUsername(easeUser.getUsername());
        mAtUserBeans.add(atUserBean);
        EditText mEditSendMsg = inputMenu.getPrimaryMenu().getEditText();
        // 将所@的名字添加到光标的后面
        StringBuilder sb = new StringBuilder(mEditSendMsg.getText().toString());
        int selectionEnd = mEditSendMsg.getSelectionEnd();
        String strAt = nick + (char) (8197);
        sb.insert(selectionEnd, strAt);
        mEditSendMsg.setText(sb.toString());
        mEditSendMsg.setSelection(selectionEnd + strAt.length());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupDelete(EventGroupDeleted event) {
        if (event.getImGroupId() != null && event.getImGroupId().equals(toChatUsername)) {
            if (getActivity() != null) {
                getActivity().finish();
            }
            return;
        }
        if (event.getGroupId() == groupId) {
            if (getActivity() != null) {
                getActivity().finish();
            }
            return;
        }
    }


    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //聊天记录,消息框点击事件，return true
        if(EaseMessageUtils.isVoteMessage(message)){
            try {
                JSONObject voteJson = message.getJSONObjectAttribute(EaseConstant.MSG_EXT_VOTE);
                VoteMsgEntity entity = new Gson().fromJson(voteJson.toString(), VoteMsgEntity.class);
                if(entity.getStatus() == 4){
                    MyToast.showInfoToast(getString(R.string.vote_has_been_deleted));
                } else {
                    startActivityForResult(new Intent(getActivity(), VoteDetailActivity.class).putExtra("voteId", entity.getId()), REQUEST_CODE_VOTE);
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        } else if (EaseMessageUtils.isNoticeMessage(message)) {
            try {
                JSONObject extMsgJson = message.getJSONObjectAttribute("extMsg");
                String type = extMsgJson.getString("type");
                if(type.equals("vote_notice")){
                    int voteId = extMsgJson.optInt("voteId");
                    AppHelper.getInstance().getModel().getMsgIdWithVoteId(String.valueOf(voteId), new EMValueCallBack<List<String>>() {
                        @Override
                        public void onSuccess(List<String> msgIds) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(msgIds.size() > 0){
                                        EMMessage msg = EMClient.getInstance().chatManager().getMessage(msgIds.get(msgIds.size() - 1));
                                        try {
                                            JSONObject ext = msg.getJSONObjectAttribute(EaseConstant.MSG_EXT_VOTE);
                                            VoteMsgEntity entity = new Gson().fromJson(ext.toString(), VoteMsgEntity.class);
                                            if(entity.getStatus() == 4){
                                                MyToast.showInfoToast(getString(R.string.vote_has_been_deleted));
                                            } else {
                                                startActivityForResult(new Intent(getActivity(), VoteDetailActivity.class).putExtra("voteId", String.valueOf(voteId)), REQUEST_CODE_VOTE);
                                            }
                                        } catch (HyphenateException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        startActivityForResult(new Intent(getActivity(), VoteDetailActivity.class).putExtra("voteId", String.valueOf(voteId)), REQUEST_CODE_VOTE);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }
            } catch (HyphenateException | JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        super.onCmdMessageReceived(messages);
    }

    @Override
    protected void selectPicFromCamera() {
        startActivityForResult(new Intent(getActivity(), CameraActivity.class), REQUEST_CODE_CAMERA);
    }

    @Override
    protected void selectPicFromLocal() {
//        startActivityForResult(new Intent(getActivity(), PhotoPickerActivity.class), REQUEST_CODE_LOCAL);
        Intent intent = new Intent(getContext(), PickerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);
        long maxSize = 10485760L;
        intent.putExtra(PickerConfig.MAX_SELECT_SIZE, maxSize); //default 10MB (Optional)
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 9);  //default 40 (Optional)
        intent.putExtra(PickerConfig.SHOW_FULL_SIZE, true);
//        intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST,defaultSelect); //(Optional)默认选中的照片
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    @Override
    protected void selectPicOnlyFromLocal() {
        super.selectPicOnlyFromLocal();
        Intent intent = new Intent(getContext(), PickerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE);
        long maxSize = 10485760L;
        intent.putExtra(PickerConfig.MAX_SELECT_SIZE, maxSize); //default 10MB (Optional)
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 9);  //default 40 (Optional)
        intent.putExtra(PickerConfig.SHOW_FULL_SIZE, true);
//        intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST,defaultSelect); //(Optional)默认选中的照片
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    @Override
    protected void notJoinedGroup() {
        if (getActivity() == null) {
            return;
        }
        AppHelper.getInstance().getModel().deleteGroupInfo(toChatUsername);
        EMClient.getInstance().chatManager().deleteConversation(toChatUsername, true);
        if (getActivity() == null) {
            return;
        }
        MPEventBus.getDefault().post(new EventGroupsChanged());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(R.string.not_joined_group);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onMessageBubbleLongClick(ImageView imageView, View v, EMMessage message, int position) {
//        qPopup = QPopuWindow.getInstance(getContext()).builder
//                .bindView(v, position)
//                .setPointers((int) rawX, (int) rawY)
//                .setDividerVisibility(true);
        xPopup = new XPopup.Builder(getContext()).hasShadowBg(false).atView(v);

        showPopupWindow(imageView, v, message, position);
    }

    private void scrollItemPosition(ImageView imageView, View v, int position, BasePopupView popupView){
        int[] location = new int[2];
        v.getLocationInWindow(location);
        int y = location[1];

        int topHeight = CommonUtils.getStatusBarHeight(getContext()) + DensityUtil.dip2px(getContext(), 44f);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;

        int[] locationIv = new int[2];
        imageView.getLocationInWindow(locationIv);
        int xIv = locationIv[0];

        xPopup = new XPopup.Builder(getContext()).hasShadowBg(false).atView(imageView);
        if(xIv < screenWidth/2){
            xPopup.offsetX(DensityUtil.dip2px(getContext(), 46f));
        } else {
            xPopup.offsetX(-DensityUtil.dip2px(getContext(), -46f));
        }
        if(y - topHeight < 0){
            messageList.getListView().smoothScrollBy(-(topHeight - y), 200);
        }
    }

    public boolean checkFloatPermission() {
        if(Build.VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(getActivity());
        }
        return true;
    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        boolean isInCalling = AppHelper.getInstance().isInCalling();
        switch (itemId) {
            case ITEM_VIDEO:
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
                break;
            case ITEM_FILE: //file
                selectFileFromLocal();
                break;
            case ITEM_LOCATION:
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        inputMenu.hideExtendMenuContainer();
                        final String[] items = {"发送位置", "共享实时位置"};
                        new ItemsDialogFragment.Builder((BaseActivity) getActivity()).setItems(items)
                                .setItemSelectListener(new ItemsDialogFragment.OnItemSelectListener() {
                                    @Override
                                    public void onSelect(int position) {
                                        if (position == 0) {
                                            startActivityForResult(new Intent(getActivity(), EMBaiduMapActivity.class), REQUEST_CODE_MAP);
                                        } else if (position == 1) {
                                            startActivity(new Intent(getActivity(), ShareLocationActivity.class)
                                                    .putExtra(Constant.EXTRA_USER_ID, toChatUsername));
                                            startRtLocation();
                                        }
                                    }
                                }).show();
                    }

                    @Override
                    public void onDenied(String permission) {
                        MyToast.showInfoToast("需要在设置中开启权限");
                    }
                });

                break;
            case ITEM_BURN_AFTER_READING:
                inputMenu.hideExtendMenuContainer();
                setInputMenuFire(true);
                break;

            case ITEM_NAME_CARD:

                startActivityForResult(new Intent(getActivity(), GroupAddMemberActivity.class).putExtra("isCard", true), 1000);
                break;
            case ITEM_VOTE:
                startActivityForResult(new Intent(getActivity(), VoteCreateActivity.class).putExtra("groupId", toChatUsername), REQUEST_CODE_VOTE);
                break;
            default:
                break;
        }
        //keep exist extend menu
        return false;
    }


    //显示长按popupWindow
    private void showPopupWindow(ImageView imageView, View v, EMMessage message, int position) {
        int type = message.getType().ordinal();
        if (type == EMMessage.Type.TXT.ordinal()) {
            if (EaseMessageUtils.isBurnMessage(message) || EaseMessageUtils.isVideoCallMessage(message) || EaseMessageUtils.isVoiceCallMessge(message) || EaseMessageUtils.isChatHistoryMessage(message)) {
                deleteAndRecallAndMultiChoice(imageView, v, message, position);
            } else if (EaseMessageUtils.isBigExprMessage(message)) {
                doDFRM(imageView, v, message, position);
            } else if (EaseMessageUtils.isInviteMessage(message) || EaseMessageUtils.isVoteMessage(message) || EaseMessageUtils.isNoticeMessage(message)) {

            } else if (EaseMessageUtils.isStickerMessage(message)) {
                doStickerMsg(imageView, v, message, position);
            } else if (EaseMessageUtils.isNameCard(message)) {
                doDFRM(imageView, v, message, position);
            } else {
                doCDFRM(imageView, v, message, position);
            }
        } else if (type == EMMessage.Type.LOCATION.ordinal()) {
            doDFRM(imageView, v, message, position);
        } else if (type == EMMessage.Type.IMAGE.ordinal()) {
            if (EaseMessageUtils.isBurnMessage(message)) {
                deleteAndRecallAndMultiChoice(imageView, v, message, position);
            } else {
                doDFRM(imageView, v, message, position);
            }
        } else if (type == EMMessage.Type.VOICE.ordinal()) {
            deleteAndRecallAndMultiChoice(imageView, v, message, position);
        } else if (type == EMMessage.Type.VIDEO.ordinal()) {
            deleteAndRecallAndMultiChoice(imageView, v, message, position);
        } else if (type == EMMessage.Type.FILE.ordinal()) {
            deleteAndRecallAndMultiChoice(imageView, v, message, position);
        }
    }

    //添加到表情，删除， 转发，撤回，多选
    private void doStickerMsg(ImageView imageView, View v, EMMessage message, int position) {
        List<String> itemList = new ArrayList<>();
        List<Integer> resList = new ArrayList<>();
        boolean isExist = StickerManager.get().containSticker(message);
        if (!isExist) {
            itemList.add(getString(R.string.addto_sticker));
            resList.add(R.drawable.ic_cm_collect);
        }
        itemList.add(getString(R.string.delete));
        resList.add(R.drawable.ic_cm_delete);
        itemList.add(getString(R.string.forward));
        resList.add(R.drawable.ic_cm_forward);
        boolean canMultiChoice = EaseMessageUtils.hasMultiChoices(message);
        if (canMultiChoice) {
            itemList.add(getString(R.string.multi_choice));
            resList.add(R.drawable.ic_cm_multiselect);
        }
        boolean canRecall = EaseMessageUtils.canRecall(message);
        if (canRecall) {
            itemList.add(getString(R.string.recall));
            resList.add(R.drawable.ic_cm_revocation);
        }
        CustomLayout customLayout = new CustomLayout(getContext(), itemList.toArray(new String[0]), resList.stream().mapToInt(Integer::intValue).toArray());
        customLayout.setOnSelectListener(new CustomLayout.OnSelectListener() {
            @Override
            public void onSelected(int position) {
                if (isExist) {
                    if (canMultiChoice) {
                        if (position == 0) {
                            doDelete(message);
                        } else if (position == 1) {
                            doForward(message);
                        } else if (position == 2) {
                            doMultiChoice(message);
                        } else if (position == 3) {
                            doRecall(message);
                        }
                    } else {
                        if (position == 0) {
                            doDelete(message);
                        } else if (position == 1) {
                            doForward(message);
                        } else if (position == 2) {
                            doRecall(message);
                        }
                    }
                } else {
                    if (canMultiChoice) {
                        if (position == 0) {
                            doAddSticker(message);
                        } else if (position == 1) {
                            doDelete(message);
                        } else if (position == 2) {
                            doForward(message);
                        } else if (position == 3) {
                            doMultiChoice(message);
                        } else if (position == 4) {
                            doRecall(message);
                        }
                    } else {
                        if (position == 0) {
                            doAddSticker(message);
                        } else if (position == 1) {
                            doDelete(message);
                        } else if (position == 2) {
                            doForward(message);
                        } else if (position == 3) {
                            doRecall(message);
                        }
                    }
                }
            }
        });
        scrollItemPosition(imageView, v, position, customLayout);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                xPopupView = xPopup.isDestroyOnDismiss(true).asCustom(customLayout).show();
            }
        }, 200);
    }

    //复制、删除、转发、撤回、多选
    private void doCDFRM(ImageView imageView, View v, EMMessage message, int position) {
        List<String> itemList = new ArrayList<>();
        List<Integer> resList = new ArrayList<>();
        itemList.add(getString(R.string.copy));
        resList.add(R.drawable.ic_cm_copy);
        itemList.add(getString(R.string.delete));
        resList.add(R.drawable.ic_cm_delete);
        itemList.add(getString(R.string.forward));
        resList.add(R.drawable.ic_cm_forward);
        boolean canMultiChoice = EaseMessageUtils.hasMultiChoices(message);
        if (canMultiChoice) {
            itemList.add(getString(R.string.multi_choice));
            resList.add(R.drawable.ic_cm_multiselect);
        }
        boolean canRecall = EaseMessageUtils.canRecall(message);
        if (canRecall) {
            itemList.add(getString(R.string.recall));
            resList.add(R.drawable.ic_cm_revocation);
        }
        boolean canReference = message.status() == EMMessage.Status.SUCCESS;
        if(canReference) {
            itemList.add(getString(R.string.reference));
            resList.add(R.drawable.ic_cm_reference);
        }
        CustomLayout customLayout = new CustomLayout(getContext(), itemList.toArray(new String[0]), resList.stream().mapToInt(Integer::intValue).toArray());
        customLayout.setOnSelectListener(new CustomLayout.OnSelectListener() {
            @Override
            public void onSelected(int position) {
                    if (position == 0) {
                        doCopy(message);
                    } else if (position == 1) {
                        doDelete(message);
                    } else if (position == 2) {
                        doForward(message);
                    } else if (position == 3) {
                        if (canMultiChoice) {
                            doMultiChoice(message);
                        } else if (canRecall) {
                            doRecall(message);
                        } else if (canReference) {
                            doReference(message);
                        }
                    } else if (position == 4) {
                        if(canMultiChoice){
                            if (canRecall) {
                                doRecall(message);
                            } else if (canReference) {
                                doReference(message);
                            }
                        } else {
                            if(canRecall){
                                if (canReference) {
                                    doReference(message);
                                }
                            }
                        }
                    } else if (position == 5) {
                        if (canReference) {
                            doReference(message);
                        }
                    }
            }
        });
        scrollItemPosition(imageView, v, position, customLayout);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                xPopupView = xPopup.isDestroyOnDismiss(true).asCustom(customLayout).show();
            }
        }, 200);
    }

    //删除、转发、撤回、多选
    private void doDFRM(ImageView imageView, View v, EMMessage message, int position) {
        boolean multiChoice = EaseMessageUtils.hasMultiChoices(message);
        List<String> itemLists = new ArrayList<>();
        List<Integer> resList = new ArrayList<>();
        itemLists.add(getString(R.string.delete));
        resList.add(R.drawable.ic_cm_delete);
        itemLists.add(getString(R.string.forward));
        resList.add(R.drawable.ic_cm_forward);
        boolean canRecall = EaseMessageUtils.canRecall(message);
        if (canRecall) {
            itemLists.add(getString(R.string.recall));
            resList.add(R.drawable.ic_cm_revocation);
        }
        if (multiChoice) {
            itemLists.add(getString(R.string.multi_choice));
            resList.add(R.drawable.ic_cm_multiselect);
        }

        boolean canReference = message.status() == EMMessage.Status.SUCCESS;
        if(canReference) {
            itemLists.add(getString(R.string.reference));
            resList.add(R.drawable.ic_cm_reference);
        }

        CustomLayout customLayout = new CustomLayout(getContext(), itemLists.toArray(new String[0]), resList.stream().mapToInt(Integer::intValue).toArray());
        customLayout.setOnSelectListener(new CustomLayout.OnSelectListener() {
            @Override
            public void onSelected(int position) {
                    if (position == 0) {
                        doDelete(message);
                    } else if (position == 1) {
                        doForward(message);
                    } else if (position == 2) {
                        if (canRecall) {
                            doRecall(message);
                        } else if (multiChoice) {
                            doMultiChoice(message);
                        } else if (canReference) {
                            doReference(message);
                        }
                    } else if (position == 3) {
                        if(canRecall){
                            if (multiChoice) {
                                doMultiChoice(message);
                            } else if (canReference) {
                                doReference(message);
                            }
                        } else {
                            if(multiChoice){
                                if (canReference) {
                                    doReference(message);
                                }
                            }
                        }
                    } else if (position == 4) {
                        if (canReference){
                            doReference(message);
                        }
                    }

            }
        });
        scrollItemPosition(imageView, v, position, customLayout);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                xPopupView = xPopup.isDestroyOnDismiss(true).asCustom(customLayout).show();
            }
        }, 200);
    }

    //删除、撤回、多选
    private void deleteAndRecallAndMultiChoice(ImageView imageView, View v, EMMessage message, int position) {
        List<String> itemList = new ArrayList<>();
        List<Integer> resList = new ArrayList<>();
        boolean canRecall = EaseMessageUtils.canRecall(message);
        itemList.add(getString(R.string.delete));
        resList.add(R.drawable.ic_cm_delete);
        boolean multiChoice = EaseMessageUtils.hasMultiChoices(message);
        if (multiChoice) {
            itemList.add(getString(R.string.multi_choice));
            resList.add(R.drawable.ic_cm_multiselect);
        }
        if (canRecall) {
            itemList.add(getString(R.string.recall));
            resList.add(R.drawable.ic_cm_revocation);
        }
        boolean canReference = message.status() == EMMessage.Status.SUCCESS;
        if(canReference) {
            itemList.add(getString(R.string.reference));
            resList.add(R.drawable.ic_cm_reference);
        }
        CustomLayout customLayout = new CustomLayout(getContext(), itemList.toArray(new String[0]), resList.stream().mapToInt(Integer::intValue).toArray());
        customLayout.setOnSelectListener(new CustomLayout.OnSelectListener() {
            @Override
            public void onSelected(int position) {
                    if (position == 0) {
                        doDelete(message);
                    } else if (position == 1) {
                        if (multiChoice) {
                            doMultiChoice(message);
                        } else if (canRecall) {
                            doRecall(message);
                        } else if (canReference) {
                            doReference(message);
                        }
                    } else if (position == 2) {
                        if(multiChoice){
                            if (canRecall) {
                                doRecall(message);
                            } else if (canReference) {
                                doReference(message);
                            }
                        } else {
                            if(canRecall){
                                if (canReference) {
                                    doReference(message);
                                }
                            }
                        }
                    } else if (position == 3) {
                        if (canReference) {
                            doReference(message);
                        }
                    }
            }
        });
        scrollItemPosition(imageView, v, position, customLayout);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                xPopupView = xPopup.positionByWindowCenter(true).isDestroyOnDismiss(true).asCustom(customLayout).show();
            }
        }, 200);
    }

    //删除、撤回
    private void deleteAndRecall(ImageView imageView, View v, EMMessage message, int position) {
        List<String> itemList = new ArrayList<>();
        List<Integer> resList = new ArrayList<>();
        boolean canRecall = EaseMessageUtils.canRecall(message);
        itemList.add(getString(R.string.delete));
        resList.add(R.drawable.ic_cm_delete);
        if (canRecall) {
            itemList.add(getString(R.string.recall));
            resList.add(R.drawable.ic_cm_revocation);
        }
        CustomLayout customLayout = new CustomLayout(getContext(), itemList.toArray(new String[0]), resList.stream().mapToInt(Integer::intValue).toArray());
        customLayout.setOnSelectListener(new CustomLayout.OnSelectListener() {
            @Override
            public void onSelected(int position) {
                if (position == 0) {
                    doDelete(message);
                } else if (position == 1) {
                    doRecall(message);
                }
            }
        });
        scrollItemPosition(imageView, v, position, customLayout);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                xPopupView = xPopup.isDestroyOnDismiss(true).asCustom(customLayout).show();
            }
        }, 200);
    }

    //复制消息
    private void doCopy(EMMessage message) {
        clipboard.setPrimaryClip(ClipData.newPlainText(null,
                ((EMTextMessageBody) message.getBody()).getMessage()));
    }

    //转发消息
    private void doForward(EMMessage message) {
        ArrayList<String> forwardMsgIds = new ArrayList<>();
        forwardMsgIds.add(message.getMsgId());
        Intent intent = new Intent(getActivity(), ForwardActivity.class);
        intent.putExtra("forwardMsgIds", forwardMsgIds);
        startActivity(intent);
    }

    //引用消息
    private void doReference(EMMessage message){
        if(isInputMenuFire()){
            setInputMenuFire(false);
        }
        DraftEntity entity = DraftManager.getInstance().getDraftEntity(toChatUsername);
        JSONObject jsonObject = new JSONObject();
        String extra = "";
        if(entity == null){
            entity = new DraftEntity();
            entity.setId(toChatUsername);
        } else {
            extra = entity.getExtra();
        }
        try {
            if(!TextUtils.isEmpty(extra)){
                jsonObject = new JSONObject(extra);
            }
            jsonObject.put(EaseConstant.DRAFT_EXT_REFERENCE_MSG_ID, message.getMsgId());
            entity.setExtra(jsonObject.toString());
        } catch(JSONException e){
            e.printStackTrace();
        }
        DraftManager.getInstance().saveDraft(entity);
        inputMenu.showReference(message);
    }

    //撤回消息
    private void doRecall(EMMessage message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMMessage msgNotification = EMMessage.createTxtSendMessage(getResources().getString(R.string.msg_recall_by_self), message.getTo());
                    msgNotification.setMsgTime(message.getMsgTime());
                    msgNotification.setLocalTime(message.getMsgTime());
                    msgNotification.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
                    msgNotification.setStatus(EMMessage.Status.SUCCESS);
                    EMClient.getInstance().chatManager().recallMessage(message);
                    EMClient.getInstance().chatManager().saveMessage(msgNotification);
                    AppHelper.getInstance().recallReferenceContent(message.getMsgId());
                    refreshMessageList();
                    hideReferenceWithRecall();
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            ToastUtils.showShort(getString(R.string.recall_failed));
                        }
                    });
                }
            }
        }).start();

        // Delete group-ack data according to this message.
        EaseDingMessageHelperV2.get().delete(message);
    }

    private JSONArray jsonCardArray = new JSONArray();
    private StringBuilder msgIdsBuilder = new StringBuilder();

    private void doCollect(EMMessage message, boolean has) {
        try {
            JSONArray array = message.getJSONObjectAttribute(Constant.EXT_EXTMSG).getJSONArray("contents");
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject extMsg = array.getJSONObject(i);
                    EMMessage.Type type = null;
                    EMMessageBody body = null;
                    String extContent = null;
                    if ("txt".equals(extMsg.getString("type"))) {
                        type = EMMessage.Type.TXT;
                        body = new EMTextMessageBody(extMsg.getString("msg"));
                    } else if ("image".equals(extMsg.getString("type"))) {
                        type = EMMessage.Type.IMAGE;
                        body = new EMImageMessageBody(new File(""), new File(""));
                    } else if ("video".equals(extMsg.getString("type"))) {
                        type = EMMessage.Type.VIDEO;
                        body = new EMVideoMessageBody();
                    } else if ("file".equals(extMsg.getString("type"))) {
                        type = EMMessage.Type.FILE;
                        body = new EMNormalFileMessageBody();
                    } else if ("voice".equals(extMsg.getString("type"))) {
                        type = EMMessage.Type.VOICE;
                        body = new EMVoiceMessageBody(new File(""), 0);
                    } else if ("location".equals(extMsg.getString("type"))) {
                        type = EMMessage.Type.LOCATION;
                        body = new EMLocationMessageBody("", 0, 0);
                    }
                    try {
                        extContent = extMsg.getString("extMsg");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    EMMessage emMessage = EMMessage.createSendMessage(type);
                    emMessage.addBody(body);
                    emMessage.setAttribute("extMsg", extContent);
                    emMessage.setChatType(EMMessage.ChatType.valueOf(extMsg.getString("chatType")));
                    emMessage.setMsgId(extMsg.getString("msgId"));
                    emMessage.setFrom(extMsg.getString("fromImId"));
                    emMessage.setTo(extMsg.getString("toImId"));
                    if (i < array.length() - 1) {
                        doCollect(emMessage, true);
                    } else if (i == array.length() - 1) {
                        doCollect(emMessage, false);
                        return;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        String queryType = null;
        String colType = null;
        int friendId;

        EMMessage.Type msgType = message.getType();
        EaseUser fromUser;
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            queryType = "groupchat";
        } else if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            queryType = "chat";
        }
        fromUser = EaseUserUtils.getUserInfo(message.getFrom());
        friendId = fromUser.getId();

        if (msgType == EMMessage.Type.TXT) {
            colType = "txt";
            EMTextMessageBody txtMsgBody = (EMTextMessageBody) message.getBody();
            String content = txtMsgBody.getMessage();
            try {
                if (Patterns.WEB_URL.matcher(content).matches() || URLUtil.isValidUrl(content)) {
                    colType = "link";
                } else if ("people_card".equals(message.getJSONObjectAttribute("extMsg").getString("type"))) {
                    colType = "card";
                    JSONObject jsonCardEntity = new JSONObject();
                    int cardUserId = message.getJSONObjectAttribute("extMsg").getJSONObject("content").getInt("user_id");
                    String avatar = message.getJSONObjectAttribute("extMsg").getJSONObject("content").getString("user_avatar");
                    String realName = message.getJSONObjectAttribute("extMsg").getJSONObject("content").getString("realName");
                    jsonCardEntity.put("cardUserId", cardUserId);
                    jsonCardEntity.put("avatar", avatar);
                    jsonCardEntity.put("realName", realName);
                    jsonCardEntity.put("msgId", message.getMsgId());
                    jsonCardArray.put(jsonCardEntity);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        } else if (msgType == EMMessage.Type.IMAGE) {
            colType = "img";
        } else if (msgType == EMMessage.Type.VOICE) {
            colType = "audio";
        } else if (msgType == EMMessage.Type.VIDEO) {
            colType = "video";
        } else if (msgType == EMMessage.Type.LOCATION) {
            colType = "loc";
        } else if (msgType == EMMessage.Type.FILE) {
            colType = "file";
        }

        msgIdsBuilder.append(message.getMsgId()).append(",");
        if (has) return;

        /* @param friendId  收藏消息的群聊/单聊(oa) friendId
         * @param imId      收藏消息的群聊/单聊 imId
         * @param queryType groupchat/chat (群聊/单聊)
         * @param colType   txt,img....
         * @param msgIds    收藏消息id集合
         * @param colExt    扩展信息*/
        String msgIds = msgIdsBuilder.substring(0, msgIdsBuilder.lastIndexOf(","));
        if (msgIds.contains(",")) {
            colType = "txt";
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("friendId", friendId);
            jsonBody.put("fromId", EMClient.getInstance().getCurrentUser());
            jsonBody.put("toId", message.getFrom());
            jsonBody.put("queryType", queryType);
            jsonBody.put("colType", colType);
            jsonBody.put("msgIds", msgIds);
            jsonBody.put("colExt", jsonCardArray.toString());
        } catch (Exception e) {

        }

        EMAPIManager.getInstance().postCollect(jsonBody.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                msgIdsBuilder.delete(0, msgIdsBuilder.length());
                jsonCardArray = new JSONArray();
                MPLog.d(TAG, "postCollect-onSuccess:" + value);
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showInfoToast(getString(R.string.tip_collect_success));
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                MPLog.e(TAG, "postCollect-onError:" + errorMsg);
                if (getActivity() == null) {
                    return;
                }
                msgIdsBuilder.delete(0, msgIdsBuilder.length());
                try {
                    JSONObject jsonRet = new JSONObject(errorMsg);
                    String errorDescription = jsonRet.getString("errorDescription");
                    if(TextUtils.isEmpty(errorDescription)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.showErrorToast(getString(R.string.tip_collect_failed));
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.showErrorToast(errorDescription);
                            }
                        });
                    }
                }catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showErrorToast(getString(R.string.tip_collect_failed));
                        }
                    });
                }

            }
        });
    }

    private void doAddSticker(EMMessage message) {
        try {
            JSONObject contentJson = message.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG).getJSONObject(EaseConstant.EXT_MSGCONTENT);
            String remoteUrl = contentJson.optString("remote_url");
            String thumbUrl = contentJson.optString("thumb_url");
            String md5Val = contentJson.optString("md5");
            JSONObject sizeJson = contentJson.optJSONObject("size");
            int[] widthAndHeight = new int[2];
            if (sizeJson != null) {
                widthAndHeight[0] = sizeJson.optInt("w");
                widthAndHeight[1] = sizeJson.optInt("h");
            }
            postSticker(remoteUrl, md5Val, widthAndHeight);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void postSticker(String remoteUrl, String md5Val, int[] widthAndHeight) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("url", remoteUrl);
            jsonObject.put("type", "image");
            try {
                jsonObject.put("width", widthAndHeight[0]);
                jsonObject.put("height", widthAndHeight[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            jsonObject.put("md5", md5Val);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EMAPIManager.getInstance().postSticker(jsonObject.toString(), new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                MPLog.d(TAG, "postSticker-onSuccess:" + value);
                final StickerEntity stickerEntity = StickerManager.get().getEntityFromResult(value);
                StickerManager.get().addBefore(stickerEntity);
                MPEventBus.getDefault().post(new EventEmojiconChanged());
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShort(R.string.add_sticker_success);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                Log.e(TAG, "postSticker-error:" + errorMsg);
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShort(R.string.add_sticker_failed);
                    }
                });
            }
        });
    }

    //多选消息
    private void doMultiChoice(EMMessage message) {
        hideInputMenu();
        checkMap.clear();

        //multi collection
        mTvMultiCollection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkMap == null || checkMap.isEmpty()) {
                    return;
                }
                new AlertDialog.Builder(getContext(), R.style.MyAlertDialog)
                        .setCancelable(true)
                        .setMessage(getString(R.string.collection))
                        .setNegativeButton(R.string.cancel, new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (checkMap.size() == 0) {
                            ToastUtils.showShort("请选择要收藏的消息");
                            return;
                        }
                        int size = 0;
                        for (EMMessage message : checkMap.values()) {
                            if (size == checkMap.size() - 1) {
                                doCollect(message, false);
                                size = 0;
                            } else {
                                size++;
                                doCollect(message, true);
                            }
                        }


                        showInputMenu();
                    }
                }).show();
            }
        });
    }

    public void refreshMessageList() {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.refresh();
//                if (qPopup != null) {
//                    qPopup.dismiss();
//                }
                if (xPopupView != null && xPopupView.isShow()) {
                    xPopupView.dismiss();
                }
            }
        });
    }

    public void refreshMessageListSelectLast() {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.refreshSelectLast();
                if (xPopupView != null && xPopupView.isShow()) {
                    xPopupView.dismiss();
                }
//                if (qPopup != null) {
//                    qPopup.dismiss();
//                }
            }
        });
    }

    //删除消息
    private void doDelete(EMMessage message) {
        conversation.removeMessage(message.getMsgId());
        refreshMessageList();
        // To delete the ding-type message native stored acked users.
        EaseDingMessageHelperV2.get().delete(message);
    }

    /**
     * select file
     */
    @SuppressLint("ResourceType")
    protected void selectFileFromLocal() {
        try {

//            new LFilePicker()
//                    .withSupportFragment(this)
//                    .withMutilyMode(false)
//                    .withStartPath(Environment.getExternalStorageDirectory().getPath())
//                    .start();

            String startPath = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    File dir = getActivity().getExternalFilesDir("");
                    startPath =  dir.getPath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                File sdPath = Environment.getExternalStorageDirectory();
                startPath = sdPath.getPath();
            }

            new LFilePicker()
                    .withSupportFragment(this)
                    .withMutilyMode(false)
                    .withRequestCode(REQUEST_CODE_SELECT_FILE_FROM_FRAGMENT)
                    .withBackgroundColor(getString(R.color.bg_top_bar_2))
                    .withBackIcon(R.drawable.mp_back_icon)
                    .withTitleColor(getString(R.color.topbar_title_color))
                    .withShowHidden(false)
//                    .withFileFilter(new String[]{".txt", ".pdf", ""})
                    //                .withStartPath("/storage/emulated/0")//指定初始显示路径
                    //                .withStartPath("/storage/emulated/0/Download")//指定初始显示路径
                    .withStartPath(startPath)
                    .withIsGreater(false)//过滤文件大小 小于指定大小的文件
                    .withFileSize(10240 * 1024 * 20)//指定文件大小为10M
                    .start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected boolean turnOnTyping() {
        return true;
    }

    private boolean networkIsConnected() {
        if (!NetworkUtil.isConnected()) {
            ToastUtils.showShort(R.string.network_unavailable);
            return false;
        }
        return true;
    }

    /**
     * chat row provider
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //here the number is the message type in EMMessage::Type
            //which is used to count the number of different chat row
            return 25;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if (EaseMessageUtils.isVoteMessage(message)){
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOTE : MESSAGE_TYPE_SENT_VOTE;
            }
            if (EaseMessageUtils.isBurnMessage(message)) {
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_BURN : MESSAGE_TYPE_SENT_BURN;
            }

            if (message.getType() == EMMessage.Type.TXT) {
                if (EaseMessageUtils.isRTLocMessage(message)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RT_LOC : MESSAGE_TYPE_SENT_RT_LOC;
                } else if (EaseMessageUtils.isStickerMessage(message)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_STICKER : MESSAGE_TYPE_SENT_STICKER;
                } else if (EaseMessageUtils.isNameCard(message)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_CARD : MESSAGE_TYPE_SENT_CARD;
                } else
                    //voice call
                    if (EaseMessageUtils.isVoiceCallMessge(message)) {
                        return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                    } else if (EaseMessageUtils.isVideoCallMessage(message)) {
                        //video call
                        return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                    } else if (EaseMessageUtils.isInviteMessage(message)) {
                        return MESSAGE_TYPE_INVITE;
                    } else if (EaseMessageUtils.isChatHistoryMessage(message)) {
                        return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_CHAT_HISTORY : MESSAGE_TYPE_SENT_CHAT_HISTORY;
                    } else if (EaseMessageUtils.isNoticeMessage(message)) {
                        return MESSAGE_TYPE_NOTICE;
                    }
                    //messagee recall
                    else if (EaseMessageUtils.isRecallMessage(message)) {
                        return MESSAGE_TYPE_RECALL;
                    } else if (!"".equals(message.getStringAttribute(EaseConstant.MSG_ATTR_CONF_ID, ""))) {
                        return MESSAGE_TYPE_CONFERENCE_INVITE;
                    }
            } else if (message.getType() == EMMessage.Type.LOCATION) {
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
            }
            return 0;
        }

        @Override
        public EaseChatRowPresenter getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
            if (message.getType() == EMMessage.Type.TXT) {
                // vote
                if(EaseMessageUtils.isVoteMessage(message)){
                    EaseChatRowPresenter presenter = new EaseChatVotePresenter(){
                        @Override
                        public void onChecked(EMMessage message, boolean b) {

                        }
                    };
                    return presenter;
                }

                // burn after reading
                if (EaseMessageUtils.isBurnMessage(message)) {
                    EaseChatRowPresenter presenter = new EaseChatBurnPresenter() {
                        @Override
                        public void onChecked(EMMessage message, boolean b) {
                            fillCheckList(message, b);
                        }
                    };
                    return presenter;
                }
                // name card
                if (EaseMessageUtils.isNameCard(message)) {
                    EaseChatRowPresenter presenter = new EaseChatCardPresenter() {
                        @Override
                        public void onChecked(EMMessage message, boolean b) {
                            fillCheckList(message, b);
                        }
                    };
                    return presenter;
                }
                // realtime location
                if (EaseMessageUtils.isRTLocMessage(message)) {
                    EaseChatRowPresenter presenter = new EaseChatRTLocPresenter() {

                        @Override
                        public void onChecked(EMMessage message, boolean b) {
                            fillCheckList(message, b);
                        }
                    };
                    return presenter;
                }


                //sticker
                if (EaseMessageUtils.isStickerMessage(message)) {
                    EaseChatRowPresenter presenter = new EaseChatStickerPresenter() {
                        @Override
                        public void onChecked(EMMessage message, boolean b) {
                            fillCheckList(message, b);
                        }
                    };
                    return presenter;
                } else if (EaseMessageUtils.isInviteMessage(message)) {
                        EaseChatRowPresenter presenter = new EaseChatInvitePresenter() {
                            @Override
                            public void onChecked(EMMessage message, boolean b) {

                            }
                        };
                        return presenter;
                    } else if (EaseMessageUtils.isChatHistoryMessage(message)) {
                        EaseChatRowPresenter presenter = new EaseChatHsitoryPresenter() {
                            @Override
                            public void onChecked(EMMessage message, boolean b) {

                            }
                        };
                        return presenter;
                    } else if (EaseMessageUtils.isNoticeMessage(message)) {
                        EaseChatRowPresenter presenter = new EaseChatNoticePresenter() {

                            @Override
                            public void onChecked(EMMessage message, boolean b) {

                            }
                        };
                        return presenter;
                    }
                    //recall message
                    else if (EaseMessageUtils.isRecallMessage(message)) {
                        EaseChatRowPresenter presenter = new EaseChatRecallPresenter() {
                            @Override
                            public void onChecked(EMMessage message, boolean b) {
                                fillCheckList(message, b);
                            }
                        };
                        return presenter;
                    } else if (!"".equals(message.getStringAttribute(EaseConstant.MSG_ATTR_CONF_ID, ""))) {
                        return null;
                    }
            } else if (message.getType() == EMMessage.Type.LOCATION) {
                EaseChatRowPresenter presenter = new EaseChatLocationPresenter() {
                    @Override
                    public void onChecked(EMMessage message, boolean b) {
                        fillCheckList(message, b);
                    }
                };
                return presenter;
            } else if (message.getType() == EMMessage.Type.IMAGE) {
                // burn after reading
                if (EaseMessageUtils.isBurnMessage(message)) {
                    EaseChatRowPresenter presenter = new EaseChatBurnPresenter() {
                        @Override
                        public void onChecked(EMMessage message, boolean b) {
                        }
                    };
                    return presenter;
                }
            }
            return null;
        }

    }

    //填充选择列表
    private void fillCheckList(EMMessage message, boolean b) {
        if (checkMap != null) {
            if (checkMap.containsKey(message.getMsgId()) && !b) {
                checkMap.remove(message.getMsgId());
            }
            if (!checkMap.containsKey(message.getMsgId()) && b) {
                checkMap.put(message.getMsgId(), message);
            }
        }
    }

    //对message添加扩展字段
    @Override
    protected void doAddSendExtField(EMMessage message) {
        message.setChatType(conversation.getType() == EMConversation.EMConversationType.Chat ? EMMessage.ChatType.Chat : EMMessage.ChatType.GroupChat);
        MPMessageUtils.doAddSendExtField(message);
    }

    @Override
    protected void addTextMsgReferenceExt(EMMessage message) {
        if(!isInputMenuFire()){
            DraftEntity entity = DraftManager.getInstance().getDraftEntity(toChatUsername);
            String msgId = DraftManager.getInstance().getReferenceMsgId(entity);
            EMMessage msg = EMClient.getInstance().chatManager().getMessage(msgId);
            if(msg != null){
                message.setAttribute(EaseConstant.MSG_EXT_REFERENCE_MSG, getReferenceMsgJson(msg));
            }
        }
    }

    @Override
    protected void messageSendAfter(EMMessage message) {
        AppHelper.getInstance().saveReferenceMsg(message);
        AppHelper.getInstance().saveVoteData(message);
    }

    private JSONObject getReferenceMsgJson(EMMessage referenceMsg){
        JSONObject object = new JSONObject();
        try {
            object.put(EaseConstant.MSG_EXT_REFERENCE_MSG_ID, referenceMsg.getMsgId());
            EaseUser user = EaseUserUtils.getUserInfo(referenceMsg.getFrom());
            object.put(EaseConstant.MSG_EXT_REFERENCE_MSG_NICK, (user != null ? user.getNickname() : referenceMsg.getFrom()));

            String content = "";
            String type = EaseConstant.REFERENCE_MSG_TYPE_TXT;
            if(EaseMessageUtils.isBurnMessage(referenceMsg)){
//                type = EaseConstant.REFERENCE_MSG_TYPE_READ_BURN;
                type = EaseConstant.REFERENCE_MSG_TYPE_TXT;
                content = getContext().getString(R.string.burn_message);
            } else {
                switch (referenceMsg.getType()){
                    case TXT:
                        EMTextMessageBody txtBody = (EMTextMessageBody) referenceMsg.getBody();
                        if (EaseMessageUtils.isNameCard(referenceMsg)) {
                            try {
                                JSONObject extMsg = new JSONObject(referenceMsg.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG).toString());
                                JSONObject cardMsg = extMsg.getJSONObject("content");
                                content = cardMsg.optString("realName");
                                type = EaseConstant.REFERENCE_MSG_TYPE_NAME_CARD;
                            } catch (JSONException | HyphenateException e) {
                            }
                        } else {
                            content = Html.fromHtml(txtBody.getMessage()).toString();
                            type = EaseConstant.REFERENCE_MSG_TYPE_TXT;
                        }
                        break;
                    case LOCATION:
                        EMLocationMessageBody localBody = (EMLocationMessageBody) referenceMsg.getBody();
                        content = localBody.getAddress();
                        type = EaseConstant.REFERENCE_MSG_TYPE_LOCATION;
                        break;
                    case FILE:
                        EMNormalFileMessageBody fileBody = (EMNormalFileMessageBody) referenceMsg.getBody();
                        content = fileBody.getFileName();
                        type = EaseConstant.REFERENCE_MSG_TYPE_FILE;
                        break;
                    case VOICE:
                        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) referenceMsg.getBody();
                        content = voiceBody.getLength() + "\"";
                        type = EaseConstant.REFERENCE_MSG_TYPE_VOICE;
                        break;
                    case IMAGE:
                        type = EaseConstant.REFERENCE_MSG_TYPE_IMAGE;
                        break;
                    case VIDEO:
                        type = EaseConstant.REFERENCE_MSG_TYPE_VIDEO;
                        EMVideoMessageBody videoMessageBody = (EMVideoMessageBody) referenceMsg.getBody();
                        content = videoMessageBody.getFileName();
                        break;
                }
            }
            object.put(EaseConstant.MSG_EXT_REFERENCE_MSG_TYPE, type);
            object.put(EaseConstant.MSG_EXT_REFERENCE_MSG_CONTENT, content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    //解析接收到的扩展字段，存入数据库
    @Override
    protected void doReceiveMessageExtField(EMMessage message) {
        try {
            Map<String, Object> ext = message.ext();
            if (ext.containsKey(EaseConstant.EXT_USER_TYPE)) {
                JSONObject userTypeJson = message.getJSONObjectAttribute(EaseConstant.EXT_USER_TYPE);
                if (userTypeJson != null) {
                    ExtUserType userType = new Gson().fromJson(userTypeJson.toString(), ExtUserType.class);
//                    AppHelper.getInstance().getModel().saveUserExtInfo(message.getFrom(), userType.userid, userType.nick, userType.avatar);
                }
            }

//            if (ext.containsKey(EaseConstant.EXT_GROUP_TYPE)) {
//                JSONObject extGroupTypeJSON = message.getJSONObjectAttribute(EaseConstant.EXT_GROUP_TYPE);
//                if (extGroupTypeJSON != null) {
//                    GroupBean groupBean = new Gson().fromJson(extGroupTypeJSON.toString(), GroupBean.class);
//                    AppHelper.getInstance().getModel().saveGroupInfo(groupBean);
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void fetchGroupInfoFromServer() {
        EMAPIManager.getInstance().getGroupInfo(toChatUsername, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                    final MPGroupEntity groupEntity = MPGroupEntity.create(jsonEntity);
                    if (groupEntity == null) {
                        MPLog.e(TAG, "requestGroupInfo groupEntity is null");
                        Intent intent = new Intent();
                        intent.putExtra("tochatname", toChatUsername);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                        return;
                    }
//                    GroupBean groupBean = new GroupBean(groupEntity.getId(), groupEntity.getImChatGroupId(),
//                            groupEntity.getName(),
//                            groupEntity.getAvatar(), groupEntity.getCreateTime(), groupEntity.getType());
                    AppHelper.getInstance().getModel().saveGroupInfo(groupEntity);
//                    MPEventBus.getDefault().post(new EventGroupsChanged());
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titleBar.setTitle(groupEntity.getName() + " (" + groupEntity.getMemberCount() + ")");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.e(TAG, "getGroupInfo error:" + errorMsg);

            }
        });
    }

    @Override
    protected void sendTextTooLarge() {
        super.sendTextTooLarge();
        MyToast.showWarning("发送的消息文本过长！");
    }

    @Override
    protected boolean sendTextMessage(String content) {
        super.sendTextMessage(content);
        DraftManager.getInstance().removeDraft(toChatUsername);
        inputMenu.hideReference();
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            /** 触摸事件的注销 */
            if (getActivity() instanceof ChatActivity) {
                ((ChatActivity) this.getActivity()).unRegisterMyTouchListener(myTouchListener);
            }
        }
        if (xPopupView != null && xPopupView.isShow()) {
            xPopupView.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageChanged(MessageChanged event) {
        if (messageList != null) {
            messageList.refreshSelectLast();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageReceived(EventEMMessageReceived event) {
        EMMessage message = event.getMessage();
        if (message != null) {
            String toChat = message.getChatType() == EMMessage.ChatType.GroupChat ? message.getTo() : message.getFrom();
            if (toChatUsername.equals(toChat)) {
                if (messageList != null) {
                    messageList.refreshSelectLast();
                }
            }
        } else {
            if (messageList != null) {
                messageList.refreshSelectLast();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEmojiconChanged(EventEmojiconChanged changed) {
        resetEmojiconMenu();
    }

    @Override
    protected void onCancelReferenceCLicked() {
        DraftEntity draftEntity = DraftManager.getInstance().getDraftEntity(toChatUsername);
        String extra = draftEntity.getExtra();
        try {
            JSONObject jsonObject = new JSONObject(extra);
            jsonObject.put(EaseConstant.DRAFT_EXT_REFERENCE_MSG_ID, "");
            draftEntity.setExtra(jsonObject.toString());
            DraftManager.getInstance().saveDraft(draftEntity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        super.onMessageRecalled(messages);
        hideReferenceWithRecall();
    }

    private void hideReferenceWithRecall(){
        // 撤回的消息是正在引用的消息，隐藏引用view
        handler.postDelayed(() -> {
            if(isMessageListInited){
                DraftEntity draftEntity = DraftManager.getInstance().getDraftEntity(toChatUsername);
                if(draftEntity != null){
                    String msgId = DraftManager.getInstance().getReferenceMsgId(draftEntity);
                    EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
                    if(message == null){
                        inputMenu.hideReference();
                    }
                } else {
                    inputMenu.hideReference();
                }
            }
        }, 300);
    }
}
