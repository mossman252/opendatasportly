package com.team.common;

public class Constants {

	public final static String HERO_BOLD = "fonts/hero-bold.ttf";
	public final static String FONTS_REGULAR_SANS = "fonts/regular_sans.ttf";
	public final static String MONT_REG = "fonts/Montserrat-Regular.otf";
	public final static String MONT_LIGHT = "fonts/Montserrat-Light.otf";
	public final static String MONT_BOLD = "fonts/Montserrat-Bold.otf";
	public final static String MONT_BLACK = "fonts/Montserrat-Black.otf";
	
	//shared pref
	public static final String SHARED_PREFERENCE_NAME = "UserInfo";
	public static final String USER_ID = "USER_ID";
	
	public static final String SETTING_VENDORID_INFO = "vendorid";
	
	public static final String PUBLISHER_ID = "ca-app-pub-9943604836792192/5252740460";
	
	
	public static String capitalizeFirst(String monb) {
		if (monb.length() <= 1) {
	        monb = monb.toLowerCase();
	    } else {
	        monb = monb.substring(0, 1).toUpperCase() + monb.substring(1,monb.length()).toLowerCase();
	    }
		return monb;
	}
	
	public static String capitalizeFirstLeaveRest(String monb) {
		if (monb.length() <= 1) {
	        monb = monb.toLowerCase();
	    } else {
	        monb = monb.substring(0, 1).toUpperCase() + monb.substring(1,monb.length());
	    }
		return monb;
	}
	
	
}
