package com.xiejiaye.pianweifen;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

import roboguice.util.RoboAsyncTask;
import roboguice.util.Strings;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusListAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Status> mStatuses;
	
	public StatusListAdapter(Context context, List<Status> statuses) {
		super();
		mContext = context;
		mStatuses = statuses;
	}

	@Override
	public int getCount() {
		return mStatuses.size();
	}

	@Override
	public Object getItem(int position) {
		return mStatuses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Status status = mStatuses.get(position);
		final View view = LayoutInflater.from(mContext)
				.inflate(R.layout.list_item_status, null);
		new RoboAsyncTask<Drawable>(mContext) {

			@Override
			public Drawable call() throws Exception {
				return loadImageFromUrl(status.user_profile_image_url);
			}

			@Override
			protected void onSuccess(Drawable profile) throws Exception {
				if (profile != null) {
					((ImageView) view.findViewById(R.id.profile_img))
						.setImageDrawable(profile);
				}
			}
			
		}.execute();
		
		((TextView) view.findViewById(R.id.user_screen_name))
			.setText(status.user_screen_name);
		((TextView) view.findViewById(R.id.created_at))
			.setText(new SimpleDateFormat("MM-dd").format(status.created_at));
		
		if (Strings.isEmpty(status.text) && Strings.isEmpty(status.thumbnail_pic)) {
			view.findViewById(R.id.layout_origin).setVisibility(View.GONE);
		} else {
			((TextView) view.findViewById(R.id.text))
				.setText(status.text);
		}
		
		if (Strings.isEmpty(status.retweeted_status_text) && 
				Strings.isEmpty(status.retweeted_status_thumbnail_pic)) {
			view.findViewById(R.id.layout_repost).setVisibility(View.GONE);
		} else {
			((TextView) view.findViewById(R.id.retweeted_text))
				.setText("@" + status.retweeted_status_user_screen_name + ": " +
						status.retweeted_status_text);
		}
		return view;
	}

	private Drawable loadImageFromUrl(String url) {
		try {
			InputStream input = new URL(url).openStream();
			return Drawable.createFromStream(input, url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
