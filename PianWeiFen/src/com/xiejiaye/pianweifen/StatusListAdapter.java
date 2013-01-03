package com.xiejiaye.pianweifen;

import java.io.IOException;
import java.util.List;

import roboguice.util.RoboAsyncTask;
import roboguice.util.Strings;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
				return Utils.loadImageFromUrl(status.user_profile_image_url);
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
			.setText(Utils.formattedDate(status.created_at));
		
		if (Strings.isEmpty(status.text) && Strings.isEmpty(status.thumbnail_pic)) {
			view.findViewById(R.id.layout_origin).setVisibility(View.GONE);
		} else {
			((TextView) view.findViewById(R.id.text))
				.setText(status.text);
			new RoboAsyncTask<Drawable>(mContext) {

				@Override
				public Drawable call() throws Exception {
					return Utils.loadImageFromUrl(status.thumbnail_pic);
				}

				@Override
				protected void onSuccess(Drawable thumbnail) throws Exception {
					((ImageView) view.findViewById(R.id.text_image))
						.setImageDrawable(thumbnail);
				}
				
			}.execute();
		}
		
		if (Strings.isEmpty(status.retweeted_status_text) && 
				Strings.isEmpty(status.retweeted_status_thumbnail_pic)) {
			view.findViewById(R.id.layout_repost).setVisibility(View.GONE);
		} else {
			((TextView) view.findViewById(R.id.retweeted_text))
				.setText("@" + status.retweeted_status_user_screen_name + ": " +
						status.retweeted_status_text);
			new RoboAsyncTask<Drawable>(mContext) {

				@Override
				public Drawable call() throws Exception {
					return Utils.loadImageFromUrl(status.retweeted_status_thumbnail_pic);
				}

				@Override
				protected void onSuccess(Drawable thumbnail) throws Exception {
					((ImageView) view.findViewById(R.id.retweeted_text_image))
						.setImageDrawable(thumbnail);
				}
				
			}.execute();
		}
		return view;
	}
	
	public OnItemClickListener getOnItemClickListener(final ObjectMapper mapper,
			final boolean upperPullToRefresh) {
		return new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (upperPullToRefresh) position--;
				Intent intent = new Intent(mContext, StatusDetailActivity.class);
				String status;
				try {
					status = mapper.writeValueAsString(mStatuses.get(position));
					intent.putExtra(StatusDetailActivity.KEY_STATUS, status);
					mContext.startActivity(intent);
				} catch (JsonGenerationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
}
