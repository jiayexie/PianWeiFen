package com.xiejiaye.pianweifen;

import java.util.Date;

public class Status {
	
	public Date created_at;
	public long id;
	
	public String text;
	public String thumbnail_pic;
	public String bmiddle_pic;
	public String original_pic;

	public long user_id;
	public String user_screen_name;
	public String user_profile_image_url;
	public String user_avatar_large;
	
	public String retweeted_status_text;
	public String retweeted_status_thumbnail_pic;
	public String retweeted_status_bmiddle_pic;
	public String retweeted_status_original_pic;
	
	public long retweeted_status_user_id;
	public String retweeted_status_user_screen_name;
	public String retweeted_status_user_profile_image_url;
	public String retweeted_status_user_avatar_large;
	
	public int reposts_count;
	public int comments_count;
}
