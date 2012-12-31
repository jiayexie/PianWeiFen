package com.xiejiaye.pianweifen;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DataHelper {
	
	private static final String AUTH = "auth";
	private static final String KEY_TOKEN = "token";

	private Context mContext;
	private SharedPreferences mPref;
	
	@Inject
	public DataHelper(Context context) {
		mContext = context;
		mPref = mContext.getSharedPreferences(AUTH, Context.MODE_PRIVATE);
	}
	
	public void saveToken(String token) {
		mPref.edit().putString(KEY_TOKEN, token).commit();
	}
	
	public String getToken() {
		return mPref.getString(KEY_TOKEN, null);
	}
	
	public void clear() {
		mPref.edit().clear().commit();
	}
}
