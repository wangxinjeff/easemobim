package com.hyphenate.easemob.im.mp.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.EaseAlertDialog;
import com.hyphenate.easemob.imlibs.message.MessageUtils;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.im.mp.adapter.RecentAdapter;
import com.hyphenate.easemob.im.mp.adapter.RecentItem;
import com.hyphenate.easemob.im.mp.adapter.SelectedItem;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.im.mp.ui.ForwardUsersActivity;
import com.hyphenate.easemob.im.mp.utils.MPMessageUtils;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.ExtUserType;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.ui.ChatActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 04/12/2018
 */
public class PictureActivity extends BaseActivity {

	private static final int REQUEST_CODE_FORWARD_USERS = 0x01;
	private RecyclerView rvRecent;
	private ImageView ivBack;
	private RecentAdapter recentAdapter;
	private List<RecentItem> allConversations = new ArrayList<RecentItem>();
	private HashMap<String, SelectedItem> selectedItemMap = new HashMap<>();
	private ArrayList<String> forwardMsgIds;
	private TextView headerTitle;
	private String imagePath;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_picture);
		setSwipeEnabled(false);
		if (AppHelper.getInstance().isLoggedIn()){
			handleImage();
			initViews();
			initDatas();
		} else {
		}

	}

	private void initViews(){
		rvRecent = findViewById(R.id.rv_recent);
		ivBack = findViewById(R.id.iv_back);
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initDatas(){
		rvRecent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		rvRecent.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		rvRecent.setAdapter(recentAdapter = new RecentAdapter(R.layout.em_row_item_forward, allConversations));
		recentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
				RecentItem recentItem = (RecentItem) adapter.getItem(position);
				if (recentItem == null) return;
				String conversationId = recentItem.conversation.conversationId();
				EMConversation.EMConversationType conversationType = recentItem.conversation.getType();
				boolean isGroup = conversationType == EMConversation.EMConversationType.GroupChat;
				recentItem.isChecked = !recentItem.isChecked;
				SelectedItem selectedItem = new SelectedItem(conversationId, isGroup);
				if (recentItem.isChecked){
					if (!selectedItemMap.containsKey(conversationId)){
						selectedItemMap.put(conversationId, selectedItem);
					}
				}else{
					if (selectedItemMap.containsKey(conversationId)){
						selectedItemMap.remove(conversationId);
					}
				}
				String nick;
				if (isGroup){
					GroupBean groupInfo = EaseUserUtils.getGroupInfo(conversationId);
					nick  = groupInfo != null ? groupInfo.getNick() : conversationId;
				}else{
					EaseUser easeUser = EaseUserUtils.getUserInfo(conversationId);
					nick = easeUser != null ? easeUser.getNick() : conversationId;
				}


				new EaseAlertDialog(activity, null, "发送给：" + nick, null, new EaseAlertDialog.AlertDialogUser() {
					@Override
					public void onResult(boolean confirmed, Bundle bundle) {
						if (confirmed) {
							if (imagePath == null){
								return;
							}
							sendImageMessage(conversationId, isGroup, imagePath);
							if (ChatActivity.activityInstance != null){
								ChatActivity.activityInstance.finish();
							}

							Intent intent = new Intent(activity, ChatActivity.class);
							// it is single chat
							intent.putExtra("userId", conversationId);
							if (isGroup) {
								// it is group chat
								intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
							}
							startActivity(intent);
							finish();
						}else{
							selectedItemMap.clear();
							for (RecentItem recentItem1: allConversations){
								recentItem1.isChecked = false;
							}
							recentAdapter.notifyDataSetChanged();
						}

					}
				}, true).show();
			}
		});
		View headerView = LayoutInflater.from(this).inflate(R.layout.em_layout_header_activity_forward, null);
		tvHeaderView = headerView.findViewById(R.id.tv_title);
		tvHeaderView.setText(R.string.title_choose_contact);
		tvHeaderView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Toast.makeText(getApplicationContext(), "新聊天！", Toast.LENGTH_SHORT).show();
				startActivityForResult(new Intent(activity, ForwardUsersActivity.class)
						.putExtra("selectedItems", selectedItemMap)
						.putExtra("isMultiSelect", false), REQUEST_CODE_FORWARD_USERS);

			}
		});
		recentAdapter.addHeaderView(headerView);
		getAllConversationsFromDB();

	}
	private TextView tvHeaderView;

	private void getAllConversationsFromDB(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				allConversations.clear();
				Map<String, EMConversation> conversationMap = EMClient.getInstance().chatManager().getAllConversations();
				List<EMConversation> tempConversations = new ArrayList<>();
				synchronized (conversationMap) {
					for (EMConversation conversation : conversationMap.values()) {
						if (conversation.getAllMessages().size() > 0) {
							//判断是否置顶了
							String stickyTime = AppHelper.getInstance().getStickyTime(conversation.conversationId(), conversation.getType());
							conversation.setExtField(stickyTime);
							tempConversations.add(conversation);
						}
					}
				}
				Collections.sort(tempConversations, new Comparator<EMConversation>() {
					//		@Override
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
							return Long.compare(convRight.getLastMessage().getMsgTime(), convLeft.getLastMessage().getMsgTime());
						}
					}
				});

				for (EMConversation conversation : tempConversations) {
					RecentItem recentItem = new RecentItem(conversation);
					if (selectedItemMap.containsKey(conversation.conversationId())) {
						recentItem.isChecked = true;
					} else {
						recentItem.isChecked = false;
					}
					allConversations.add(recentItem);
				}
				if (isFinishing()) return;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						recentAdapter.setNewData(allConversations);
					}
				});
			}
		}).start();
	}

	private long getConversationSortTime(EMConversation conversation){
		boolean isSticky = !TextUtils.isEmpty(conversation.getExtField());
		long stickyTime = isSticky ? Long.parseLong(conversation.getExtField()) : 0l;
		long latestMsgTime = conversation.getLastMessage().getMsgTime();
		return Math.max(latestMsgTime, stickyTime);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK){
			if (requestCode == REQUEST_CODE_FORWARD_USERS){
				if (data != null){
					HashMap<String, SelectedItem> itemMaps = (HashMap<String, SelectedItem>) data.getSerializableExtra("selectedItems");
					if (itemMaps == null){
						selectedItemMap = new HashMap<>();
					}else{
						selectedItemMap = itemMaps;
					}
					for (Map.Entry<String, SelectedItem> selectedItemEntry : selectedItemMap.entrySet()){
						SelectedItem selectedItem = selectedItemEntry.getValue();
						boolean isGroup = selectedItem.isGroupChat;
						String conversationId = selectedItem.conversationId;
						String nick;
						if (isGroup){
							GroupBean groupInfo = EaseUserUtils.getGroupInfo(conversationId);
							nick  = groupInfo != null ? groupInfo.getNick() : conversationId;
							nick = "群(" + nick+ ")";
						}else{
							EaseUser easeUser = EaseUserUtils.getUserInfo(conversationId);
							nick = easeUser != null ? easeUser.getNick() : conversationId;
						}

						new EaseAlertDialog(activity, null, "发送给:" + nick, null, new EaseAlertDialog.AlertDialogUser() {
							@Override
							public void onResult(boolean confirmed, Bundle bundle) {
								if (confirmed) {
									if (imagePath == null){
										return;
									}
									sendImageMessage(conversationId, isGroup, imagePath);
									if (ChatActivity.activityInstance != null) {
										ChatActivity.activityInstance.finish();
									}
									Intent intent = new Intent(activity, ChatActivity.class);
									// it is single chat
									intent.putExtra("userId", conversationId);
									if (isGroup) {
										// it is group chat
										intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
									}
									startActivity(intent);
									finish();
								}
							}
						}, true).show();
					}
				}

			}
		}
	}

	private void handleImage(){
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_SEND.equals(action) && "image/*".equals(type)){
			Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
			imagePath = getRealPathFromUri(imageUri);
			// 接收多张图片
//			ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

		}
	}

	protected void sendMessage(EMMessage message) {
		if (message == null) {
			return;
		}
		// Send message.
		MPMessageUtils.sendMessage(message);
	}


	public String getRealPathFromUri(Uri contentUri){
		Cursor cursor = null;
		try {
			String[] proj = {MediaStore.Images.Media.DATA};
			cursor = getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}finally {
			if (cursor != null){
				cursor.close();
			}
		}
	}

	protected void sendImageMessage(String toChatUsername, boolean isGroup, String imagePath) {
		EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
		if (isGroup) {
			message.setChatType(EMMessage.ChatType.GroupChat);
		}
		sendMessage(message);
	}




}
