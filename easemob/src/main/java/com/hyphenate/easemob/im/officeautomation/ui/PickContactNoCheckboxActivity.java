/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easemob.im.officeautomation.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.hyphenate.easemob.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.widget.EaseSidebar;
import com.hyphenate.easemob.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("Registered")
public abstract class PickContactNoCheckboxActivity extends BaseActivity {

	protected EaseContactAdapter contactAdapter;
	protected List<EaseUser> contactList;
	protected ListView listView;
	private ImageView ivBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());

		initViews();
		initListeners();
		initData();
	}

	protected void initData(){
		contactList = new ArrayList<EaseUser>();
		// set adapter
		contactAdapter = new EaseContactAdapter(this, R.layout.ease_row_contact, contactList);
		listView.setAdapter(contactAdapter);
	};

	protected abstract int getLayout();

	protected void onListItemClick(int position) {
		setResult(RESULT_OK, new Intent().putExtra("username", contactAdapter.getItem(position)
				.getUsername()));
		finish();
	}

	protected void initListeners(){
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onListItemClick(position);
			}
		});
	}

	public void initViews(){
		ivBack = $(R.id.iv_back);
		listView = (ListView) findViewById(R.id.list);
		EaseSidebar sidebar = (EaseSidebar) findViewById(R.id.sidebar);
		sidebar.setListView(listView);
	}

}
