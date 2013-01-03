package com.xiejiaye.pianweifen;

import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.view.Window;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

public class ImageDetailActivity extends RoboSherlockActivity {

	@InjectView(R.id.image)
	private ImageView mImage;
	
	public static final String KEY_IMG_URL = "img_url";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_image);
		
		String url = getIntent().getStringExtra(KEY_IMG_URL);
		showImage(url);
	}
	
	private void showImage(final String url) {
		new RoboAsyncTask<Drawable>(this) {

			@Override
			public Drawable call() throws Exception {
				return Utils.loadImageFromUrl(url);
			}

			@Override
			protected void onException(Exception e) throws RuntimeException {
				Toast.makeText(ImageDetailActivity.this, 
						getString(R.string.error_get_image, e.toString()), 
						Toast.LENGTH_LONG).show();
				finish();
			}

			@Override
			protected void onFinally() throws RuntimeException {
				setSupportProgressBarIndeterminateVisibility(false);
			}

			@Override
			protected void onPreExecute() throws Exception {
				setSupportProgressBarIndeterminateVisibility(true);
			}

			//@SuppressWarnings("deprecation")
			@Override
			protected void onSuccess(Drawable drawable) throws Exception {
				//int width = getWindowManager().getDefaultDisplay().getWidth();
				//drawable.setBounds(0, 0, width, drawable.getIntrinsicHeight() * 
				//		width / drawable.getIntrinsicWidth());
				mImage.setAdjustViewBounds(true);
				mImage.setImageDrawable(drawable);
			}
			
		}.execute();
	}
}
