package com.hyphenate.easemob.im.mp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.im.mp.adapter.SelectedItem;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.zhouzhuo.zzletterssidebar.ZzLetterSideBar;
import me.zhouzhuo.zzletterssidebar.adapter.BaseSortRecyclerViewAdapter;
import me.zhouzhuo.zzletterssidebar.anotation.Letter;
import me.zhouzhuo.zzletterssidebar.entity.SortModel;
import me.zhouzhuo.zzletterssidebar.interf.OnLetterTouchListener;
import me.zhouzhuo.zzletterssidebar.viewholder.BaseRecyclerViewHolder;
import me.zhouzhuo.zzletterssidebar.widget.ZzRecyclerView;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 13/09/2018
 */
public class ForwardUsersActivity extends BaseActivity {

	private static final String TAG = "ForwardUsersActivity";
	private static final int REQUEST_CODE_FORWARD_GROUPS = 0x01;
	private ImageView ivBack;
	private TextView tvRight;
	private ZzRecyclerView rv;
	private UsersAdapter usersAdapter;
	private ZzLetterSideBar sideBar;
	private TextView tvDialog;
	private List<UserItem> userItems = new ArrayList<>();
//	private ArrayList<SelectedItem> selectedItems = new ArrayList<>();
	private HashMap<String, SelectedItem> selectedItemMap = new HashMap<>();
	private boolean isMultiSelect;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.em_activity_forward_users);
		setSwipeEnabled(false);
		initViews();
		initDatas();
	}

	private void initViews(){
		ivBack = findViewById(R.id.iv_back);
		tvRight = findViewById(R.id.tv_right);
		rv = findViewById(R.id.rv);
		sideBar = findViewById(R.id.sidebar);
		tvDialog = findViewById(R.id.tv_dialog);
	}

	private void notifyRightText(){
		if (selectedItemMap.isEmpty()){
			tvRight.setEnabled(false);
			tvRight.setTextColor(Color.GRAY);
			tvRight.setText(R.string.btn_ensure);
		}else{
			tvRight.setEnabled(true);
			tvRight.setTextColor(getResources().getColor(R.color.topbar_btn_textcolor));
			tvRight.setText(getString(R.string.btn_ensure_format, String.valueOf(selectedItemMap.size())));
		}
	}

	private ProgressDialog mProgressDialog;

	private void showDialog(){
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.tip_start_groupchat));
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	private void closeDialog(){
		if (mProgressDialog != null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
	}

	private void createGroup(){
		LoginUser loginUser = UserProvider.getInstance().getLoginUser();
		if (loginUser == null){
			return;
		}
		showDialog();
		StringBuilder sb = new StringBuilder();
		sb.append(loginUser.getNick()).append(",");
		List<Integer> memberList = new ArrayList<>();
		for (Map.Entry<String, SelectedItem> selectedItem: selectedItemMap.entrySet()){
			EaseUser user = EaseUserUtils.getUserInfo(selectedItem.getValue().conversationId);
			sb.append(user.getNick()).append(",");
			memberList.add(user.getId());
		}
		if (sb.length() > 0){
			sb.deleteCharAt(sb.length() - 1);
		}
		String groupNick = sb.toString();
		if (groupNick.length() > 20){
			groupNick = groupNick.substring(0, 20);
		}

		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("name", groupNick);
			jsonObj.put("description", "");
			jsonObj.put("maxusers", EaseConstant.MAX_GROUP_COUNT);
			jsonObj.put("newMemberCanreadHistory", true);
			jsonObj.put("allowInvites", false);
			jsonObj.put("membersOnly", false);
			jsonObj.put("isPublic", false);

			jsonObj.put("memberIdList", new JSONArray(memberList));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		EMAPIManager.getInstance().postCreateGroup(jsonObj.toString(), new EMDataCallBack<String>() {
			@Override
			public void onSuccess(String value) {
				if (isFinishing()) return;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeDialog();
						try {
							JSONObject jsonResult = new JSONObject(value);
							String status = jsonResult.optString("status");
							if ("OK".equalsIgnoreCase(status)) {
								JSONObject jsonEntity = jsonResult.optJSONObject("entity");
								MPGroupEntity groupEntity = MPGroupEntity.create(jsonEntity);
								if (groupEntity != null) {
									String imChatGroup = groupEntity.getImChatGroupId();
//									GroupBean groupBean = new GroupBean(groupEntity.getId(), imChatGroup, groupEntity.getName(), groupEntity.getAvatar(),
//											groupEntity.getCreateTime(), groupEntity.getType());
									AppHelper.getInstance().getModel().saveGroupInfo(groupEntity);
									HashMap<String, SelectedItem> tempMap = new HashMap<>();
									tempMap.put(imChatGroup, new SelectedItem(imChatGroup, true));
									setResult(RESULT_OK, new Intent().putExtra("selectedItems", tempMap));
									finish();
								} else {
									MPLog.e(TAG, " forward postCreateGroup entity is null");
								}
							} else {
								MPLog.e(TAG, " forward postCreateGroup status = " + status);
							}

						} catch (Exception e) {
							e.printStackTrace();
							MPLog.e(TAG, " forward postCreateGroup error");
						}

					}
				});

			}

			@Override
			public void onError(int error, String errorMsg) {
				if (isFinishing()) return;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeDialog();
						Toast.makeText(getApplicationContext(), "创建群聊失败，请检查网络", Toast.LENGTH_SHORT).show();
					}
				});

			}
		});
	}

	private void initDatas(){
		Intent intent = getIntent();
		if (intent != null){
			selectedItemMap = (HashMap<String, SelectedItem>) intent.getSerializableExtra("selectedItems");
			isMultiSelect = intent.getBooleanExtra("isMultiSelect", false);
		}
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tvRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isMultiSelect || selectedItemMap.size() <= 1){
					setResult(RESULT_OK, new Intent().putExtra("selectedItems", selectedItemMap));
					finish();
				}else{
					createGroup();
				}

			}
		});
		usersAdapter = new UsersAdapter(this, userItems);
		usersAdapter.setRecyclerViewClickListener(new BaseSortRecyclerViewAdapter.OnRecyclerViewClickListener() {
			@Override
			public void onClick(View itemView, int pos) {
				if (pos < usersAdapter.getHeadViewSize()){
					Intent intent = new Intent(activity, ForwardGroupActivity.class);
					if (isMultiSelect){
						intent.putExtra("selectedItems", selectedItemMap);
					}
					intent.putExtra("isMultiSelect", isMultiSelect);
					startActivityForResult(intent, REQUEST_CODE_FORWARD_GROUPS);
					return;
				}
				int position = pos - usersAdapter.getHeadViewSize();
				if (position >= userItems.size()){
					return;
				}
 				UserItem userItem = userItems.get(position);
				userItem.isChecked = !userItem.isChecked;
				SelectedItem selectedItem = new SelectedItem(userItem.username);
				if (userItem.isChecked){
					if (!selectedItemMap.containsKey(userItem.username)){
						selectedItemMap.put(selectedItem.conversationId, selectedItem);
					}
				}else{
					if (selectedItemMap.containsKey(selectedItem.conversationId)){
						selectedItemMap.remove(selectedItem.conversationId);
					}
				}
				usersAdapter.notifyDataSetChanged();
				notifyRightText();
			}
		});

//		rv.setLinearLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		rv.setAdapter(usersAdapter);

		sideBar.setLetterTouchListener(rv, usersAdapter, tvDialog, new OnLetterTouchListener() {
			@Override
			public void onLetterTouch(String letter, int position) {

			}

			@Override
			public void onActionUp() {

			}
		});
		getAllUsersFromDB();
	}


	private void getAllUsersFromDB(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<EaseUser> easeUsers = AppHelper.getInstance().getModel().getExtUserList();
				userItems.clear();
				for (EaseUser easeUser : easeUsers) {
					if (easeUser.getUsername() == null || easeUser.getUsername().equals(EMClient.getInstance().getCurrentUser())){
						continue;
					}
					UserItem userItem = new UserItem();
					userItem.avatar = easeUser.getAvatar();
					userItem.nick = easeUser.getNick();
					userItem.username = easeUser.getUsername();
					if (selectedItemMap.containsKey(userItem.username)) {
						userItem.isChecked = true;
					} else {
						userItem.isChecked = false;
					}
					userItems.add(userItem);
				}
				if (isFinishing()) return;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						usersAdapter.updateRecyclerView(userItems);
						usersAdapter.notifyDataSetChanged();
						notifyRightText();
					}
				});
			}
		}).start();


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK){
			if (requestCode == REQUEST_CODE_FORWARD_GROUPS){
				if (data != null){
					selectedItemMap = (HashMap<String, SelectedItem>) data.getSerializableExtra("selectedItems");
					if (!isMultiSelect){
						setResult(RESULT_OK, new Intent().putExtra("selectedItems", selectedItemMap));
						finish();
					}else{
						getAllUsersFromDB();
					}


				}
			}
		}
	}

	class UsersAdapter extends BaseSortRecyclerViewAdapter<UserItem, BaseRecyclerViewHolder>{


		public UsersAdapter(Context ctx, List<UserItem> mDatas) {
			super(ctx, mDatas);
		}

		@Override
		public int getItemLayoutId() {
			return R.layout.em_row_item_forward;
		}

		@Override
		public int getHeaderLayoutId() {
			return R.layout.em_layout_header_activity_forward;
		}

		@Override
		public int getFooterLayoutId() {
			return 0;
		}

		@Override
		public BaseRecyclerViewHolder getViewHolder(View itemView, int type) {
			switch (type){
				case BaseSortRecyclerViewAdapter.TYPE_HEADER:
					return new HeaderHolder(itemView);

			}
			return new MyViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
			if (holder instanceof MyViewHolder){
				MyViewHolder myViewHolder = ((MyViewHolder)holder);
				final int pos = position - getHeadViewSize();
				if (pos < mDatas.size()){
					initLetter(holder, pos);
					UserItem userItem = mDatas.get(pos);
					myViewHolder.cb.setChecked(userItem.isChecked);
					myViewHolder.tvContent.setText(userItem.nick);
					AvatarUtils.setAvatarContent(activity, userItem.username, myViewHolder.ivAvatar);
					initClickListener(holder, position);
				}
			} else if (holder instanceof HeaderHolder){
				HeaderHolder headerHolder = ((HeaderHolder)holder);
				if (isMultiSelect){
					headerHolder.headerTitle.setText("选择群聊");
				}else{
					headerHolder.headerTitle.setText("选择一个群");
				}
				headerHolder.headerLabel.setText("选择联系人");
				headerHolder.headerLabel.setVisibility(View.GONE);
				initClickListener(holder, position);
			}
		}
	}

	public static class MyViewHolder extends BaseRecyclerViewHolder {
		TextView tvContent;
		AppCompatCheckBox cb;
		AvatarImageView ivAvatar;

		public MyViewHolder(View itemView) {
			super(itemView);
			cb = itemView.findViewById(R.id.cb);
			cb.setVisibility(View.VISIBLE);
			tvContent = itemView.findViewById(R.id.tv_content);
			ivAvatar = itemView.findViewById(R.id.iv_avatar);
		}
	}

	public static class HeaderHolder extends BaseRecyclerViewHolder {

		TextView headerTitle;
		TextView headerLabel;

		public HeaderHolder(View itemView) {
			super(itemView);
			headerTitle = itemView.findViewById(R.id.tv_title);
			headerLabel = itemView.findViewById(R.id.tv_label);
		}
	}


	class UserItem extends SortModel {
		String username;
		@Letter(isSortField = true)
		String nick;
		boolean isChecked;
		String avatar;
	}



}
