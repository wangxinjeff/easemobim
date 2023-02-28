package com.hyphenate.easemob.im.officeautomation.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.ui.EaseGroupListener;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.im.mp.widget.ClearEditText;
import com.hyphenate.easemob.R;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.zhouzhuo.zzletterssidebar.ZzLetterSideBar;
import me.zhouzhuo.zzletterssidebar.adapter.BaseSortListViewAdapter;
import me.zhouzhuo.zzletterssidebar.anotation.Letter;
import me.zhouzhuo.zzletterssidebar.entity.SortModel;
import me.zhouzhuo.zzletterssidebar.interf.OnLetterTouchListener;
import me.zhouzhuo.zzletterssidebar.utils.CharacterParser;
import me.zhouzhuo.zzletterssidebar.utils.PinyinComparator;
import me.zhouzhuo.zzletterssidebar.viewholder.BaseViewHolder;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 01/09/2018
 */

public class GroupMembersActivity extends BaseActivity {

//	private static final String TAG = "GroupMembersActivity";
//
//	private TextView tvTitle;
//	private ListView mListView;
//	private List<MemberItem> members = Collections.synchronizedList(new ArrayList<MemberItem>());
//	private MemberAdapter mAdapter;
//	private String groupId;
//	private ProgressBar loadingPB;
//	private GroupChangeListener groupChangeListener;
//	private ZzLetterSideBar sideBar;
//	private TextView tvDialog;
//	private EMGroup emGroup;
//	private boolean isOwner;
//	private boolean isAdmin;
//	private ClearEditText mClearEditText;
//
//	private static final String ROLE_MEMBER = "member";
//	private static final String ROLE_ADMIN = "admin";
//	private static final String ROLE_OWNER = "owner";
//
//
//	@Override
//	protected void onCreate(Bundle arg0) {
//		super.onCreate(arg0);
//		setContentView(R.layout.activity_group_members);
//		initViews();
//		initDatas();
//	}
//
//	private void initViews(){
//		tvTitle = findViewById(R.id.tv_title);
//		loadingPB = (ProgressBar) findViewById(R.id.progressBar);
//		mListView = (ListView) findViewById(R.id.listview);
//		mListView.setAdapter(mAdapter = new MemberAdapter(this, members));
//		tvTitle.setText(R.string.all_members);
//		tvDialog = findViewById(R.id.tv_dialog);
//		sideBar = findViewById(R.id.sidebar);
//		mClearEditText = findViewById(R.id.filter_edit);
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if(resultCode == Activity.RESULT_OK){
//			if (requestCode == 200) {
//				updateGroup();
//			}
//		}
//
//	}
//
//	private void initDatas(){
//		groupId = getIntent().getStringExtra("groupId");
//		if (TextUtils.isEmpty(groupId)){
//			finish();
//		}
//		sideBar.setLetterTouchListener(mListView, mAdapter, tvDialog, new OnLetterTouchListener() {
//			@Override
//			public void onLetterTouch(String letter, int position) {
//
//			}
//
//			@Override
//			public void onActionUp() {
//
//			}
//		});
//
//		mClearEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				mClearEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//			}
//		});
//		// 根据输入框输入值的改变来过滤搜索
//		mClearEditText.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//			}
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
//				filterData(s.toString());
//			}
//		});
//
//		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
//				MemberItem memberItem = (MemberItem) parent.getItemAtPosition(position);
//				Intent intent = new Intent(activity, PersonalCardActivity.class);
//				intent.putExtra("username", memberItem.username);
//				if (isOwner || isAdmin) {
//					intent.putExtra("groupId", groupId);
//					intent.putExtra("role", memberItem.role);
//				}
//				startActivityForResult(intent, 200);
//			}
//		});
//
//		updateGroup();
//		groupChangeListener = new GroupChangeListener();
//		EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);
//
//		EMClient.getInstance().groupManager().asyncFetchGroupMuteList(groupId, 0, 200, new EMValueCallBack<Map<String, Long>>() {
//			@Override
//			public void onSuccess(Map<String, Long> stringLongMap) {
//				AppHelper.getInstance().getModel().updateGroupMutes(groupId, stringLongMap);
//			}
//
//			@Override
//			public void onError(int i, String s) {
//
//			}
//		});
//	}
//
//
//	private void asyncLoadGroupMembers(){
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				loadingPB.setVisibility(View.VISIBLE);
//			}
//		});
//
//		EaseUI.getInstance().execute(new Runnable() {
//			@Override
//			public void run() {
//
//				try {
//					try {
//						emGroup = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					String currentUser = EMClient.getInstance().getCurrentUser();
//					if (emGroup != null){
//						if (emGroup.getOwner() != null && emGroup.getOwner().equals(currentUser)){
//							isOwner = true;
//						}else{
//							isOwner = false;
//						}
//						if (emGroup.getAdminList() != null && emGroup.getAdminList().contains(currentUser)){
//							isAdmin = true;
//						}else{
//							isAdmin = false;
//						}
//					}
//					final ArrayList<String> allMembers = new ArrayList<>();
//
////					final EMGroup emGroup = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
//					EMCursorResult<String> result = null;
//					do {
//						// page size set to 20 is convenient for testing, should be applied to big value
//						result = EMClient.getInstance().groupManager().fetchGroupMembers(groupId,
//								result != null ? result.getCursor() : "",
//								100);
//						EMLog.d(TAG, "fetchGroupMembers result.size:" + result.getData().size());
//						synchronized (allMembers){
//							for (String item : result.getData()){
//								if (!allMembers.contains(item)){
//									allMembers.add(item);
//								}
//							}
//						}
//
//					} while (result.getCursor() != null && !result.getCursor().isEmpty());
//					members.clear();
//					EaseUser ownerUser = EaseUserUtils.getUserInfo(emGroup.getOwner());
//					if (ownerUser != null){
//						MemberItem memberItem = new MemberItem();
//						memberItem.username = ownerUser.getUsername();
//						memberItem.avatar = ownerUser.getAvatar();
//						memberItem.nick = ownerUser.getNick();
//						memberItem.role = ROLE_OWNER;
//						members.add(memberItem);
//					}
//					List<String> adminList = emGroup.getAdminList();
//					if (adminList != null && !adminList.isEmpty()){
//						for (String item: adminList){
//							EaseUser easeUser = EaseUserUtils.getUserInfo(item);
//							if (easeUser != null){
//								MemberItem memberItem = new MemberItem();
//								memberItem.username = item;
//								memberItem.avatar = easeUser.getAvatar();
//								memberItem.nick = easeUser.getNick();
//								memberItem.role = ROLE_ADMIN;
//								members.add(memberItem);
//							}
//						}
//					}
//
//					for (String item : allMembers){
//						EaseUser easeUser = EaseUserUtils.getUserInfo(item);
//						if (easeUser != null){
//							MemberItem memberItem = new MemberItem();
//							memberItem.username = item;
//							memberItem.avatar = easeUser.getAvatar();
//							memberItem.nick = easeUser.getNick();
//							memberItem.role = ROLE_MEMBER;
//							members.add(memberItem);
//						}
//					}
//
//					if (isFinishing()){
//						return;
//					}
//					runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							loadingPB.setVisibility(View.INVISIBLE);
//							synchronized (members){
//								mAdapter.updateListView(members);
//							}
//							mAdapter.notifyDataSetChanged();
//						}
//					});
//				} catch (HyphenateException e) {
//					e.printStackTrace();
//					if (isFinishing()){return;}
//					runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							loadingPB.setVisibility(View.INVISIBLE);
//							mAdapter.notifyDataSetChanged();
//						}
//					});
//				}
//
//			}
//		});
//	}
//
//	/**
//	 * 根据输入框中的值来过滤数据并更新ListView
//	 *
//	 * @param filterStr
//	 */
//	private void filterData(String filterStr){
//		List<MemberItem> filterMemberItems = new ArrayList<>();
//		if (TextUtils.isEmpty(filterStr)){
//			filterMemberItems = members;
//		} else {
//			filterMemberItems.clear();
//			synchronized (members){
//				for (MemberItem item : members){
//					String name = item.nick;
//					if (name.contains(filterStr) || CharacterParser.getInstance().getSelling(name).startsWith(filterStr)) {
//						filterMemberItems.add(item);
//					}
//				}
//			}
//		}
//		// 根据a-z进行排序
//		Collections.sort(filterMemberItems, new PinyinComparator());
//		mAdapter.updateListView(filterMemberItems);
//	}
//
//
//	class MemberAdapter extends BaseSortListViewAdapter<MemberItem, MemberAdapter.ViewHolder> {
//
//
//		public MemberAdapter(Context ctx, List<MemberItem> datas) {
//			super(ctx, datas);
//		}
//
//		@Override
//		public int getLayoutId() {
//			return R.layout.em_row_group_member;
//		}
//
//		@Override
//		public ViewHolder getViewHolder(View convertView) {
//			ViewHolder viewHolder = new ViewHolder();
//			viewHolder.ivAvatar = convertView.findViewById(R.id.iv_avatar);
//			viewHolder.tvName = convertView.findViewById(R.id.name);
//			viewHolder.tvStatus = convertView.findViewById(R.id.tv_status);
//			return viewHolder;
//		}
//
//		@Override
//		public void bindValues(ViewHolder viewHolder, int position) {
//			MemberItem memberItem = mDatas.get(position);
//			if (memberItem == null) return;
//			EaseUser easeUser = EaseUserUtils.getUserInfo(memberItem.username);
//			if (memberItem.role != null){
//				if (memberItem.role.equals(ROLE_ADMIN)){
//					viewHolder.tvStatus.setText(R.string.label_group_admin);
//					viewHolder.tvStatus.setVisibility(View.VISIBLE);
//					viewHolder.tvStatus.setBackgroundColor(Color.parseColor("#24bdb9"));
//				}else if (memberItem.role.equals(ROLE_OWNER)){
//					viewHolder.tvStatus.setText(R.string.label_group_owner);
//					viewHolder.tvStatus.setBackgroundColor(Color.parseColor("#ffac02"));
//					viewHolder.tvStatus.setVisibility(View.VISIBLE);
//				}else {
//					viewHolder.tvStatus.setVisibility(View.GONE);
//				}
//			}
//			AvatarUtils.setAvatarContent(GroupMembersActivity.this, easeUser, viewHolder.ivAvatar);
//			EaseUserUtils.setUserNick(memberItem.username, viewHolder.tvName);
//		}
//
//		class ViewHolder extends BaseViewHolder {
//			AvatarImageView ivAvatar;
//			TextView tvName;
//			TextView tvStatus;
//		}
//	}
//
//	public void back(View view){
//		setResult(RESULT_OK);
//		finish();
//	}
//
//	@Override
//	public void onBackPressed() {
//		setResult(RESULT_OK);
//		finish();
//		super.onBackPressed();
//	}
//
//	private class GroupChangeListener extends EaseGroupListener {
//
//		@Override
//		public void onInvitationAccepted(String groupId, String inviter, String reason) {
//		}
//
//		@Override
//		public void onUserRemoved(String groupId, String groupName) {
//			finish();
//		}
//
//		@Override
//		public void onGroupDestroyed(String groupId, String groupName) {
//			finish();
//		}
//
//		@Override
//		public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
//			updateGroup();
//		}
//
//		@Override
//		public void onMuteListRemoved(String groupId, final List<String> mutes) {
//
//		}
//
//		@Override
//		public void onAdminAdded(String groupId, String administrator) {
//			updateGroup();
//		}
//
//		@Override
//		public void onAdminRemoved(String groupId, String administrator) {
//			updateGroup();
//		}
//
//		@Override
//		public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
//			updateGroup();
//		}
//
//		@Override
//		public void onMemberJoined(String groupId, String member) {
//			updateGroup();
//		}
//
//		@Override
//		public void onMemberExited(String groupId, String member) {
//			updateGroup();
//		}
//
//		@Override
//		public void onAnnouncementChanged(String groupId, final String announcement) {
//		}
//
//		@Override
//		public void onSharedFileAdded(String groupId, final EMMucSharedFile sharedFile) {
//		}
//
//		@Override
//		public void onSharedFileDeleted(String groupId, String fileId) {
//		}
//	}
//
//	private void updateGroup(){
//		asyncLoadGroupMembers();
//	}
//
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		EMClient.getInstance().groupManager().removeGroupChangeListener(groupChangeListener);
//	}
//
//	class MemberItem extends SortModel {
//		String username;
//		@Letter(isSortField = true)
//		String nick;
//		String avatar;
//		String role;
//	}


}
