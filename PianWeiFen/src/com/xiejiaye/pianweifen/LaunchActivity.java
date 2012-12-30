package com.xiejiaye.pianweifen;

import com.google.inject.Inject;

import roboguice.activity.RoboActivity;
import android.content.Intent;
import android.os.Bundle;

public class LaunchActivity extends RoboActivity {

	@Inject
	private DataHelper mDataHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (mDataHelper.getToken() == null) {
			startActivity(new Intent(this, AuthActivity.class));
		} else {
			startActivity(new Intent(this, MainActivity.class));
		}
		
		finish();
	}

}
