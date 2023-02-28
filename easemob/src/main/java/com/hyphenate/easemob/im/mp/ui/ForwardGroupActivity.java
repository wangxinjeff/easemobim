package com.hyphenate.easemob.im.mp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.im.mp.adapter.SelectedItem;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.domain.GroupsListBean;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.ui.BaseActivity;
import com.hyphenate.easemob.im.officeautomation.utils.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import me.zhouzhuo.zzletterssidebar.ZzLetterSideBar;
import me.zhouzhuo.zzletterssidebar.adapter.BaseSortRecyclerViewAdapter;
import me.zhouzhuo.zzletterssidebar.anotation.Letter;
import me.zhouzhuo.zzletterssidebar.entity.SortModel;
import me.zhouzhuo.zzletterssidebar.interf.OnLetterTouchListener;
import me.zhouzhuo.zzletterssidebar.utils.PinyinComparator;
import me.zhouzhuo.zzletterssidebar.viewholder.BaseRecyclerViewHolder;
import me.zhouzhuo.zzletterssidebar.widget.ZzRecyclerView;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 14/09/2018
 */
public class ForwardGroupActivity extends BaseActivity {

	private static final String TAG = "ForwardGroupActivity";
	private ImageView ivBack;
	private TextView tvRight;
	private ZzRecyclerView rv;
	private ForwardGroupAdapter groupAdapter;
	private ZzLetterSideBar sideBar;
	private TextView tvDialog;
	private List<GroupItem> groupItems = new ArrayList<>();
	//	private ArrayList<SelectedItem> selectedItems = new ArrayList<>();
	private HashMap<String, SelectedItem> selectedItemMap = new HashMap<>();

	private TextView tvTitle;
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
		tvTitle = findViewById(R.id.tv_title);
		tvTitle.setText("选择群聊");
		rv = findViewById(R.id.rv);
		sideBar = findViewById(R.id.sidebar);
		tvDialog = findViewById(R.id.tv_dialog);
	}

	private void notifyRightText(){
		if (!isMultiSelect){
			tvRight.setVisibility(View.GONE);
			return;
		}
		if (selectedItemMap.isEmpty()){
			tvRight.setEnabled(false);
			tvRight.setTextColor(Color.GRAY);
			tvRight.setText("确定");
		}else{
			tvRight.setEnabled(true);
			tvRight.setTextColor(getResources().getColor(R.color.topbar_btn_textcolor));
			tvRight.setText("确定(" + selectedItemMap.size()+ ")");
		}
	}

	private void initDatas(){
		Intent intent = getIntent();
		if (intent != null){
			HashMap<String, SelectedItem> intentMap = (HashMap<String, SelectedItem>) intent.getSerializableExtra("selectedItems");
			if (intentMap != null){
				selectedItemMap = intentMap;
			}
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
				setResult(RESULT_OK, new Intent().putExtra("selectedItems", selectedItemMap));
				finish();
			}
		});

		groupAdapter = new ForwardGroupAdapter(this, groupItems);
		groupAdapter.setRecyclerViewClickListener(new BaseSortRecyclerViewAdapter.OnRecyclerViewClickListener() {
			@Override
			public void onClick(View itemView, int pos) {
				GroupItem groupItem = groupItems.get(pos);
				groupItem.isChecked = !groupItem.isChecked;
				SelectedItem selectedItem = new SelectedItem(groupItem.imGroupId, true);
				if (groupItem.isChecked){
					if (!selectedItemMap.containsKey(selectedItem.conversationId)){
						selectedItemMap.put(selectedItem.conversationId, selectedItem);
					}
				}else{
					if (selectedItemMap.containsKey(selectedItem.conversationId)){
						selectedItemMap.remove(selectedItem.conversationId);
					}
				}
				if (!isMultiSelect){
					setResult(RESULT_OK, new Intent().putExtra("selectedItems", selectedItemMap));
					finish();
					return;
				}
				groupAdapter.notifyDataSetChanged();
				notifyRightText();
			}
		});

//		rv.setLinearLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		rv.setAdapter(groupAdapter);

		sideBar.setLetterTouchListener(rv, groupAdapter, tvDialog, new OnLetterTouchListener() {
			@Override
			public void onLetterTouch(String letter, int position) {

			}

			@Override
			public void onActionUp() {

			}
		});


		long cacheTime = PreferenceManager.getInstance().getCacheGroupsTime();
		if (cacheTime > 0){
			getGroupsFromDB();
		}else{
			requestData();
		}
	}


	private void getGroupsFromDB(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<GroupBean> groupBeans = AppHelper.getInstance().getModel().getExtGroupList();
				groupItems.clear();
				for (GroupBean groupBean : groupBeans){
					GroupItem groupItem = new GroupItem();
					groupItem.groupId = groupBean.getGroupId();
					groupItem.imGroupId = groupBean.getImGroupId();
					groupItem.avatar = groupBean.getAvatar();
					groupItem.groupName = groupBean.getNick();
					if (selectedItemMap.containsKey(groupItem.imGroupId)){
						groupItem.isChecked = true;
					} else {
						groupItem.isChecked = false;
					}
					groupItems.add(groupItem);
				}
				if (isFinishing()) return;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						groupAdapter.updateRecyclerView(groupItems);
						notifyRightText();
					}
				});
			}
		}).start();
	}

	private void requestData() {
		EMAPIManager.getInstance().getCollectedGroups(0, 100, new EMDataCallBack<String>() {
			@Override
			public void onSuccess(final String value) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject jsonObj = new JSONObject(value);
							JSONArray jsonArrEntities = jsonObj.optJSONArray("entities");
							List<MPGroupEntity> groupEntities = MPGroupEntity.create(jsonArrEntities);
//							ArrayList<GroupBean> grouplist = new ArrayList<>();
//							for (int i = 0; i < groupEntities.size(); i++) {
//								MPGroupEntity groupEntity = groupEntities.get(i);
//								if (groupEntity != null) {
//									GroupBean groupBean = new GroupBean(groupEntity.getId(), groupEntity.getImChatGroupId(),
//											groupEntity.getName(), groupEntity.getAvatar(), groupEntity.getCreateTime(), groupEntity.getType());
//									grouplist.add(groupBean);
//								}
//							}
							AppHelper.getInstance().getModel().saveGroupEntities(groupEntities);
							if (!groupEntities.isEmpty()){
								PreferenceManager.getInstance().setCacheGroupsTime(System.currentTimeMillis());
								getGroupsFromDB();
							}

						} catch (JSONException e) {
							e.printStackTrace();
							MPLog.e(TAG, "getCollectedGroups json parse error");
						}
					}
				});

			}

			@Override
			public void onError(int error, String errorMsg) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
					}
				});

			}
		});
	}

	private void sortGroupList(List<GroupItem> grouplist){
		if (grouplist == null || grouplist.isEmpty()){
			return;
		}
		Collections.sort(grouplist, new PinyinComparator());
	}


	class ForwardGroupAdapter extends BaseSortRecyclerViewAdapter<GroupItem, BaseRecyclerViewHolder>{

		public ForwardGroupAdapter(Context ctx, List<GroupItem> mDatas) {
			super(ctx, mDatas);
		}

		@Override
		public int getItemLayoutId() {
			return R.layout.em_row_item_forward;
		}

		@Override
		public int getHeaderLayoutId() {
			return 0;
		}

		@Override
		public int getFooterLayoutId() {
			return 0;
		}

		@Override
		public BaseRecyclerViewHolder getViewHolder(View itemView, int type) {
			return new MyViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
			if (holder instanceof MyViewHolder){
				MyViewHolder myViewHolder = ((MyViewHolder)holder);
				initLetter(holder, position);
				GroupItem groupItem = mDatas.get(position);
				if (isMultiSelect){
					myViewHolder.cb.setVisibility(View.VISIBLE);
				}else{
					myViewHolder.cb.setVisibility(View.GONE);
				}
				myViewHolder.cb.setChecked(groupItem.isChecked);
				myViewHolder.tvContent.setText(groupItem.groupName);
				GlideUtils.loadFromRemote(activity, groupItem.avatar,R.drawable.ease_group_icon, myViewHolder.ivAvatar);
				initClickListener(holder, position);
			}

		}
	}

	public static class MyViewHolder extends BaseRecyclerViewHolder {
		TextView tvContent;
		AppCompatCheckBox cb;
		ImageView ivAvatar;

		public MyViewHolder(View itemView) {
			super(itemView);
			cb = itemView.findViewById(R.id.cb);
			tvContent = itemView.findViewById(R.id.tv_content);
			ivAvatar = itemView.findViewById(R.id.iv_avatar);
		}
	}


	class GroupItem extends SortModel {
		int groupId;
		String imGroupId;
		@Letter(isSortField = true)
		String groupName;
		boolean isChecked;
		String avatar;
	}

}
