package com.hyphenate.easemob.imlibs.easeui.token;

import android.text.TextUtils;

import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 26/07/2018
 */

public class TokenManager {

	private static TokenManager instance = new TokenManager();

	private Token token;

	private String m_session;

	public static TokenManager getInstance(){
		return instance;
	}


	public Token getToken() {
		if (token != null) {
			return token;
		}
		String strToken = PreferenceUtils.getInstance().getSession();
		if (TextUtils.isEmpty(strToken)) {
			return null;
		}
		try {
			JSONObject jsonObject = new JSONObject(strToken);
			Token token = new Token();
			token.expires = jsonObject.getInt("expires");
			token.value = jsonObject.optString("value");
			token.name = jsonObject.optString("name");
			return token;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setToken(Token token){
		this.token = token;
		PreferenceUtils.getInstance().setSession(token.toString());
	}


	public String getSession() {
		if (m_session == null) {
			m_session = PreferenceUtils.getInstance().getNewSession();
		}
		return m_session;
	}

	public void setSession(String session){
		this.m_session = session;
		PreferenceUtils.getInstance().setNewSession(session);
	}

	public static class Token {
		public String name;
		public String value;
		public int expires;

		@Override
		public String toString() {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("name", name);
				jsonObject.put("value", value);
				jsonObject.put("expires", expires);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonObject.toString();
		}
	}

}
