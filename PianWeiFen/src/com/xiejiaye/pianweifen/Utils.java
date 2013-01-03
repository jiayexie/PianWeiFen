package com.xiejiaye.pianweifen;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.graphics.drawable.Drawable;

public class Utils {
	
	public static Drawable loadImageFromUrl(String url) {
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
	
	public static final SimpleDateFormat FORMAT0 = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
	public static final SimpleDateFormat FORMAT1 = new SimpleDateFormat("MM月dd日 hh:mm");
	public static final SimpleDateFormat FORMAT2 = new SimpleDateFormat("hh:mm");
	
	public static String formattedDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		if (calendar.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
			return FORMAT0.format(date);
		} else if (calendar.get(Calendar.MONTH) != now.get(Calendar.MONTH)
				|| calendar.get(Calendar.DATE) != now.get(Calendar.DATE)) {
			return FORMAT1.format(date);
		} else {
			return FORMAT2.format(date);
		}
	}
}
