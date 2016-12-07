package com.andyidea.tabdemo.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharePreferenceUtil {

	private final static String PREFERENCE_NAME = "cn.teachcourse.login";
	private static SharedPreferences preferences;
	private static Editor editor;
	public static final String LOGIN_STR = "loginstr";
	public static final String DISTANCE = "updatedistance";
	public static final String FIRST_FLASH = "isFirstIn";
	public static final String DEVICETOKEN = "deviceToken";

	public static void initPreference(Context context) {
		preferences = context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		editor = preferences.edit();
	}

	public static void putInt(String key, int value) {
		editor.putInt(key, value).commit();
	}

	public static int getInt(String key, int defValue) {
		return preferences.getInt(key, defValue);
	}

	public static void putLong(String key, long value) {
		editor.putLong(key, value).commit();
	}

	public static long getLong(String key, long defValue) {
		return preferences.getLong(key, defValue);
	}

	public static void putString(String key, String value) {
		try {
			editor.putString(key, value).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getString(String key, String defValue) {

		try {
			return preferences.getString(key, defValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return preferences.getString(key, defValue);
	}

	public static void putBoolean(String key, boolean value) {
		editor.putBoolean(key, value).commit();
	}

	public static boolean getBoolean(String key, boolean defValue) {
		return preferences.getBoolean(key, defValue);
	}

	public static boolean remove(String key) {
		return editor.remove(key).commit();
	}

	public static boolean contains(String key) {
		return preferences.contains(key);
	}
}
