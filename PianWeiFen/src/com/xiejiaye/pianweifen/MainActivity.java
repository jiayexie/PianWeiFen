package com.xiejiaye.pianweifen;

import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;

import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Window;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends RoboSherlockFragmentActivity {

	@InjectView(R.id.switcher)
	private ViewSwitcher mSwitcher;
	@InjectView(R.id.home_timeline)
	private ListView mListHome;
	@InjectView(R.id.title)
	private TitlePageIndicator mIndicator;
	@InjectView(R.id.pager)
	private ViewPager mPager;
	
	@Inject
	private DataHelper mDataHelper;
	@Inject
	private ObjectMapper mMapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		
		setTitle(null);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				getSupportActionBar().getThemedContext(), 
				R.layout.sherlock_spinner_item, 
				getResources().getStringArray(R.array.nav_list));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		getSupportActionBar().setListNavigationCallbacks(
				adapter, 
				new OnNavigationListener() {
					
					@Override
					public boolean onNavigationItemSelected(int itemPosition, long itemId) {
						switch (itemPosition) {
						case 0:
							mSwitcher.setDisplayedChild(0);
							displayHomeTimeline();
							return true;
						case 1:
							mSwitcher.setDisplayedChild(1);
							return true;
						default:
							return false;
						}
					}
				});
	}

	private void displayHomeTimeline() {
		new RoboAsyncTask<String>(this) {

			@Override
			public String call() throws Exception {
				return Jsoup.connect("http://www.pkucada.org:8088/statuses/home_timeline.php")
					.data("access_token", mDataHelper.getToken())
					.method(Method.GET).execute().body();
			}

			@Override
			protected void onException(Exception e) throws RuntimeException {
				super.onException(e);
				Toast.makeText(getContext(), 
						getString(R.string.error_get_home_timeline_, e.toString()),
						Toast.LENGTH_LONG).show();
			}

			@Override
			protected void onFinally() throws RuntimeException {
				setSupportProgressBarIndeterminateVisibility(false);
			}

			@Override
			protected void onPreExecute() throws Exception {
				setSupportProgressBarIndeterminateVisibility(true);
			}

			@Override
			protected void onSuccess(String response) throws Exception {
				Map<String, List<Status>> map = mMapper.readValue(response, 
						new TypeReference<Map<String, List<Status>>>(){});
				setStatusList(mListHome, map.get("statuses"));
			}
			
		}.execute();
	}
	
	private void setStatusList(ListView listView, List<Status> statuses) {
		listView.setAdapter(new StatusListAdapter(this, statuses));
	}
}
