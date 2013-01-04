package com.xiejiaye.pianweifen;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;

import roboguice.util.RoboAsyncTask;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.view.Window;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;

public class AuthActivity extends RoboSherlockActivity {
	
	public static final String SINA_WEIBO_AUTH = "https://api.weibo.com/oauth2/authorize" +
			"?display=mobile&client_id=4191846009&response_type=token" +
			"&redirect_uri=https://api.weibo.com/oauth2/default.html";
	public static final String TOKEN_KEY = "access_token=";

	private WebView mWebView;
	
	@Inject
	private DataHelper mDataHelper;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		mWebView = new WebView(this);
		mWebView.getSettings().setJavaScriptEnabled(true);

		mWebView.loadUrl(SINA_WEIBO_AUTH);
		
		mWebView.setWebViewClient(new WebViewClient() {
			
			@Override
			public void onPageStarted(WebView webview, String url, Bitmap favicon) {
				setSupportProgressBarIndeterminateVisibility(true);
				super.onPageStarted(webview, url, favicon);
			}
			
			@Override
			public void onPageFinished(WebView webview, String url) {
				setSupportProgressBarIndeterminateVisibility(false);
				if (url.contains(TOKEN_KEY)) {
					url = url.substring(url.indexOf(TOKEN_KEY)+TOKEN_KEY.length());
					url = url.substring(0, url.indexOf("&"));
					try {
						url = URLDecoder.decode(url, "UTF-8");
						init(url);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						finish();
					}
				}
			}
		});
		setContentView(mWebView);
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
				progress = new ProgressDialog(AuthActivity.this);
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
				startActivity(new Intent(AuthActivity.this, MainActivity.class));
				finish();
			}
			
		}.execute();
	}
}
