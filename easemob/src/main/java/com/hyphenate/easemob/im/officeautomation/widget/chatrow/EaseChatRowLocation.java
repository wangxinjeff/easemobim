package com.hyphenate.easemob.im.officeautomation.widget.chatrow;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.util.LatLng;

public class EaseChatRowLocation extends EaseChatRow{

	private TextView locationView;
	private EMLocationMessageBody locBody;
	private RelativeLayout mContainer;

	public EaseChatRowLocation(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
				R.layout.ease_row_received_location : R.layout.ease_row_sent_location, this);
	}

	@Override
	protected void onFindViewById() {
		locationView = (TextView) findViewById(R.id.tv_location);
		mContainer = findViewById(R.id.content_container);
	}


	@Override
	protected void onSetUpView() {
		locBody = (EMLocationMessageBody) message.getBody();
		locationView.setText(locBody.getAddress());
		String locImageUrl = message.getStringAttribute("locImage", null);
		try {
			if (locImageUrl != null) {
				GlideUtils.loadAsBitmap(activity, locImageUrl, mContainer);

//			    GlideUtils.load(activity, locImageUrl, new SimpleTarget<Bitmap>() {
//				    @Override
//				    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition transition) {
//					    Drawable drawable = new BitmapDrawable(resource);
//					    mContainer.setBackground(drawable);
//				    }
//			    });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onViewUpdate(EMMessage msg) {
		switch (msg.status()) {
			case CREATE:
				onMessageCreate();
				break;
			case SUCCESS:
				onMessageSuccess();
				break;
			case FAIL:
				onMessageError();
				break;
			case INPROGRESS:
				onMessageInProgress();
				break;
		}
	}

	private void onMessageCreate() {
		progressBar.setVisibility(View.VISIBLE);
		statusView.setVisibility(View.GONE);
	}

	private void onMessageSuccess() {
		progressBar.setVisibility(View.GONE);
		statusView.setVisibility(View.GONE);
	}

	private void onMessageError() {
		progressBar.setVisibility(View.GONE);
		statusView.setVisibility(View.VISIBLE);
	}

	private void onMessageInProgress() {
		progressBar.setVisibility(View.VISIBLE);
		statusView.setVisibility(View.GONE);
	}

	/*
		 * listener for map clicked
		 */
	protected class MapClickListener implements OnClickListener {

		LatLng location;
		String address;

		public MapClickListener(LatLng loc, String address) {
			location = loc;
			this.address = address;

		}

		@Override
		public void onClick(View v) {

		}
	}


	@Override
	protected boolean disableCheckBox() {
		return true;
	}
}
