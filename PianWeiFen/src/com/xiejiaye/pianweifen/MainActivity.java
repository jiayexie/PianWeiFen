package com.xiejiaye.pianweifen;

import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;

import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.extras.viewpager.PullToRefreshViewPager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends RoboSherlockFragmentActivity {

	@InjectView(R.id.home_timeline)
	private PullToRefreshListView mListHome;
	@InjectView(R.id.pwf_timeline)
	private LinearLayout mPwfTimeline;
	@InjectView(R.id.title)
	private TitlePageIndicator mIndicator;
	@InjectView(R.id.pager)
	private PullToRefreshViewPager mPager;
	
	@Inject
	private DataHelper mDataHelper;
	@Inject
	private ObjectMapper mMapper;
	@Inject
	private FragmentManager mFragManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		
		mListHome.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(
					PullToRefreshBase<ListView> refreshView) {
				displayHomeTimeline(true);
			}
		});
		mPager.setOnRefreshListener(new OnRefreshListener<ViewPager>() {

			@Override
			public void onRefresh(PullToRefreshBase<ViewPager> refreshView) {
				displayCategoriedTimeline(true);
			}
		});
		
		setTitle(null);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				getSupportActionBar().getThemedContext(), 
				R.layout.sherlock_spinner_item, 
				getResources().getStringArray(R.array.nav_list));
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		getSupportActionBar().setListNavigationCallbacks(
				adapter, 
				new OnNavigationListener() {
					
					@Override
					public boolean onNavigationItemSelected(int itemPosition, long itemId) {
						switch (itemPosition) {
						case 0:
							mListHome.setVisibility(View.VISIBLE);
							mPwfTimeline.setVisibility(View.GONE);
							displayHomeTimeline(false);
							return true;
						case 1:
							mListHome.setVisibility(View.GONE);
							mPwfTimeline.setVisibility(View.VISIBLE);
							displayCategoriedTimeline(false);
							return true;
						case 2:
							mListHome.setVisibility(View.GONE);
							mPwfTimeline.setVisibility(View.VISIBLE);
							displayCategoriedTimelineDemo(false);
						default:
							return false;
						}
					}
				});
	}

	private void displayHomeTimeline(final boolean fromPullToRefresh) {
		new RoboAsyncTask<String>(this) {

			@Override
			public String call() throws Exception {
				return Jsoup.connect("http://www.pkucada.org:8088/statuses/home_timeline.php")
					.data("access_token", mDataHelper.getToken())
					.method(Method.GET).execute().body();
			}

			@Override
			protected void onException(Exception e) throws RuntimeException {
				Toast.makeText(getContext(), 
						getString(R.string.error_get_home_timeline_, e.toString()),
						Toast.LENGTH_LONG).show();
			}

			@Override
			protected void onFinally() throws RuntimeException {
				setSupportProgressBarIndeterminateVisibility(false);
				if (fromPullToRefresh) {
					mListHome.onRefreshComplete();
				}
			}

			@Override
			protected void onPreExecute() throws Exception {
				setSupportProgressBarIndeterminateVisibility(true);
			}

			@Override
			protected void onSuccess(String response) throws Exception {
				Map<String, List<Status>> map = mMapper.readValue(response, 
						new TypeReference<Map<String, List<Status>>>(){});
				StatusListAdapter adapter = new StatusListAdapter(getContext(), 
						map.get("statuses"));
				mListHome.setAdapter(adapter);
				mListHome.setOnItemClickListener(adapter.getOnItemClickListener(mMapper, true));
			}
			
		}.execute();
	}
	
	private void displayCategoriedTimeline(final boolean fromPullToRefresh) {
		new RoboAsyncTask<String>(this) {

			@Override
			public String call() throws Exception {
				return Jsoup.connect("http://www.pkucada.org:8089/wdm/statuses/workspace/pwf_timeline.php")
						.data("access_token", mDataHelper.getToken())
						.method(Method.GET).execute().body();
			}

			@Override
			protected void onException(Exception e) throws RuntimeException {
				Toast.makeText(getContext(), 
						getString(R.string.error_get_pwf_timeline_, e.toString()),
						Toast.LENGTH_LONG).show();
			}

			@Override
			protected void onFinally() throws RuntimeException {
				setSupportProgressBarIndeterminateVisibility(false);
				if (fromPullToRefresh) {
					mPager.onRefreshComplete();
				}
			}

			@Override
			protected void onPreExecute() throws Exception {
				setSupportProgressBarIndeterminateVisibility(true);
			}

			@Override
			protected void onSuccess(String response) throws Exception {
				List<TopicSet> topicSets = mMapper.readValue(response, 
						new TypeReference<List<TopicSet>>() {});
				mPager.getRefreshableView().setAdapter(new TopicPagerAdapter(topicSets));
				mIndicator.setViewPager(mPager.getRefreshableView());
				mIndicator.notifyDataSetChanged();
			}
			
		}.execute();
	}
	
	private void displayCategoriedTimelineDemo(final boolean fromPullToRefresh) {
		new RoboAsyncTask<String>(this) {

			@Override
			public String call() throws Exception {
				return Jsoup.connect("http://www.pkucada.org:8089/wdm/statuses/pwf_timeline.php")
						.data("access_token", mDataHelper.getToken())
						.method(Method.GET).execute().body();
			}

			@Override
			protected void onException(Exception e) throws RuntimeException {
				Toast.makeText(getContext(), 
						getString(R.string.error_get_pwf_timeline_, e.toString()),
						Toast.LENGTH_LONG).show();
			}

			@Override
			protected void onFinally() throws RuntimeException {
				setSupportProgressBarIndeterminateVisibility(false);
				if (fromPullToRefresh) {
					mPager.onRefreshComplete();
				}
			}

			@Override
			protected void onPreExecute() throws Exception {
				setSupportProgressBarIndeterminateVisibility(true);
			}

			@Override
			protected void onSuccess(String response) throws Exception {
				List<TopicSet> topicSets = mMapper.readValue(response, 
						new TypeReference<List<TopicSet>>() {});
				mPager.getRefreshableView().setAdapter(new TopicPagerAdapter(topicSets));
				mIndicator.setViewPager(mPager.getRefreshableView());
				mIndicator.notifyDataSetChanged();
			}
			
		}.execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_init:
			init(mDataHelper.getToken());
			return true;
		case R.id.menu_logout:
			mDataHelper.clear();
			startActivity(new Intent(this, AuthActivity.class));
			finish();
			return true;
		default:
			return false;
		}
	}

	private class TopicPagerAdapter extends FragmentPagerAdapter {

		private List<TopicSet> mTopicSets;
		
		public TopicPagerAdapter(List<TopicSet> topicSets) {
			super(mFragManager);
			mTopicSets = topicSets;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mTopicSets.get(position).topic;
		}

		@Override
		public Fragment getItem(final int position) {
			return new ListFragment() {

				@Override
				public void onViewCreated(View view, Bundle savedInstanceState) {
					super.onViewCreated(view, savedInstanceState);
					StatusListAdapter adapter = new StatusListAdapter(getActivity(), 
							mTopicSets.get(position).statuses);
					setListAdapter(adapter);
					getListView().setOnItemClickListener(
							adapter.getOnItemClickListener(mMapper, false));
				}
			};
		}

		@Override
		public int getCount() {
			return mTopicSets.size();
		}
	}
	
	private void init(final String token) {
		new RoboAsyncTask<Void>(this) {

			private ProgressDialog progress;
			
			@Override
			public Void call() throws Exception {
				Jsoup.connect("http://www.pkucada.org:8089/wdm/statuses/workspace/init.php")
					.data("access_token", token)
					.timeout(60000)
					.method(Method.GET).execute();
				return null;
			}

			@Override
			protected void onFinally() throws RuntimeException {
				progress.dismiss();
			}

			@Override
			protected void onPreExecute() throws Exception {
				progress = new ProgressDialog(MainActivity.this);
				progress.setMessage(getString(R.string.initializing));
				progress.setIndeterminate(true);
				progress.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						cancel(true);
					}
				});
				progress.show();
			}

			@Override
			protected void onSuccess(Void t) throws Exception {
				mDataHelper.saveToken(token);
				startActivity(new Intent(MainActivity.this, MainActivity.class));
				finish();
			}
			
		}.execute();
	}
}
