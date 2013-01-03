package com.xiejiaye.pianweifen;

import java.io.IOException;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import roboguice.util.Strings;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;

@ContentView(R.layout.activity_status_detail)
public class StatusDetailActivity extends RoboSherlockActivity {
	
	@InjectView(R.id.profile_img)
	private ImageView mImageProfile;
	@InjectView(R.id.user_screen_name)
	private TextView mTextScreenName;
	@InjectView(R.id.created_at)
	private TextView mTextCreatedAt;
	@InjectView(R.id.layout_origin)
	private LinearLayout mLayoutOrigin;
	@InjectView(R.id.text)
	private TextView mTextStatus;
	@InjectView(R.id.text_image)
	private ImageView mImageStatus;
	@InjectView(R.id.layout_repost)
	private LinearLayout mLayoutRepost;
	@InjectView(R.id.retweeted_text)
	private TextView mTextRetweeted;
	@InjectView(R.id.retweeted_text_image)
	private ImageView mImageRetweeted;
	
	private Status mStatus = null;
	
	@Inject
	private ObjectMapper mMapper;
	
	public static final String KEY_STATUS = "status";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String status = getIntent().getStringExtra(KEY_STATUS);
		try {
			mStatus = mMapper.readValue(status, Status.class);
			display();
		} catch (JsonParseException e) {
			e.printStackTrace();
			finish();
		} catch (JsonMappingException e) {
			e.printStackTrace();
			finish();
		} catch (IOException e) {
			e.printStackTrace();
			finish();
		}
	}

	private void display() {
		new RoboAsyncTask<Drawable>(this) {

			@Override
			public Drawable call() throws Exception {
				return Utils.loadImageFromUrl(mStatus.user_profile_image_url);
			}

			@Override
			protected void onSuccess(Drawable profile) throws Exception {
				if (profile != null) {
					mImageProfile.setImageDrawable(profile);
				}
			}
			
		}.execute();
		
		mTextScreenName.setText(mStatus.user_screen_name);
		mTextCreatedAt.setText(Utils.formattedDate(mStatus.created_at));
		
		if (Strings.isEmpty(mStatus.text) && Strings.isEmpty(mStatus.bmiddle_pic)) {
			mLayoutOrigin.setVisibility(View.GONE);
		} else {
			mTextStatus.setText(mStatus.text);
			new RoboAsyncTask<Drawable>(this) {

				@Override
				public Drawable call() throws Exception {
					return Utils.loadImageFromUrl(mStatus.bmiddle_pic);
				}

				@Override
				protected void onSuccess(Drawable bmiddle) throws Exception {
					mImageStatus.setImageDrawable(bmiddle);
					mImageStatus.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(StatusDetailActivity.this, 
									ImageDetailActivity.class);
							intent.putExtra(ImageDetailActivity.KEY_IMG_URL,
									mStatus.original_pic);
							startActivity(intent);
						}
					});
				}
				
			}.execute();
		}
		
		if (Strings.isEmpty(mStatus.retweeted_status_text) && 
				Strings.isEmpty(mStatus.retweeted_status_bmiddle_pic)) {
			mLayoutRepost.setVisibility(View.GONE);
		} else {
			mTextRetweeted.setText("@" + mStatus.retweeted_status_user_screen_name + ": " +
						mStatus.retweeted_status_text);
			new RoboAsyncTask<Drawable>(this) {

				@Override
				public Drawable call() throws Exception {
					return Utils.loadImageFromUrl(mStatus.retweeted_status_bmiddle_pic);
				}

				@Override
				protected void onSuccess(Drawable bmiddle) throws Exception {
					mImageRetweeted.setImageDrawable(bmiddle);
					mImageRetweeted.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(StatusDetailActivity.this,
									ImageDetailActivity.class);
							intent.putExtra(ImageDetailActivity.KEY_IMG_URL, 
									mStatus.retweeted_status_original_pic);
							startActivity(intent);
						}
					});
				}
				
			}.execute();
		}
		
	}
}
