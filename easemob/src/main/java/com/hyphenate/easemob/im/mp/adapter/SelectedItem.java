package com.hyphenate.easemob.im.mp.adapter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 13/09/2018
 */
public class SelectedItem implements Parcelable{

	public String conversationId;
	public boolean isGroupChat;

	public SelectedItem(){}
	public SelectedItem(String conversationId){
		this.conversationId = conversationId;
	}

	public SelectedItem(String conversationId, boolean isGroupChat){
		this.conversationId = conversationId;
		this.isGroupChat = isGroupChat;
	}

	protected SelectedItem(Parcel in) {
		conversationId = in.readString();
		isGroupChat = in.readByte() != 0;
	}

	public static final Creator<SelectedItem> CREATOR = new Creator<SelectedItem>() {
		@Override
		public SelectedItem createFromParcel(Parcel in) {
			return new SelectedItem(in);
		}

		@Override
		public SelectedItem[] newArray(int size) {
			return new SelectedItem[size];
		}
	};

	@Override
	public boolean equals(Object obj) {
		return conversationId.equals(obj);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(conversationId);
		dest.writeByte((byte) (isGroupChat ? 1 : 0));
	}
}
