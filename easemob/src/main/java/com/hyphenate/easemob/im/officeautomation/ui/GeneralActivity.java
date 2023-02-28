package com.hyphenate.easemob.im.officeautomation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.utils.ChangeLanguageHelper;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 31/05/2018
 * <p>
 * 设置--通用界面
 */
public class GeneralActivity extends BaseActivity {

	private ImageView mIvBack;
	private TextView mTvMultiLang;
	private RelativeLayout mRlMultiLang;
	private RelativeLayout mRlFontSize;
	private SwitchButton mSbEnterSendMsg;
	private SwitchButton mSbEarPiece;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_general);
		initViews();
		initListeners();

	}

	private void initViews() {
		mIvBack = findViewById(R.id.iv_back);
		mTvMultiLang = findViewById(R.id.tv_multi_language);
		mRlMultiLang = findViewById(R.id.rl_multi_lang);
		mTvMultiLang.setText(ChangeLanguageHelper.getLanguageName(this));
		mRlFontSize = findViewById(R.id.rl_font_size);
		mSbEnterSendMsg = findViewById(R.id.sb_enter_send_msg);
		mSbEnterSendMsg.setChecked(PreferenceUtils.getInstance().isEnterSendMsg());
		mSbEarPiece = findViewById(R.id.sb_earpiece);
		mSbEarPiece.setChecked(PreferenceUtils.getInstance().isEarpieceOn());
	}

	private void initListeners() {
		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
//				startActivity(new Intent(GeneralActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
			}
		});
		mRlMultiLang.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		mRlFontSize.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(GeneralActivity.this, SetTextSizeActivity.class));
			}
		});
		mSbEnterSendMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtils.getInstance().setEnterSendMsg(isChecked);
			}
		});

		mSbEarPiece.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtils.getInstance().setEarpieceVoice(isChecked);
			}
		});

	}

}
