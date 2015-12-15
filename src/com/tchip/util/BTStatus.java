package com.tchip.util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

/**
 * 
 * 蓝牙状态
 * @author wwj
 *
 */
public class BTStatus{
	//蓝牙打开状态
	public static boolean btEnabled = false;
	//蓝牙连接状态
	public static boolean btConnected = false;
	
	/**
	 * 获取蓝牙状态
	 */
	public static void getBTStatus(Context context){
		ContentResolver  resolver = context.getContentResolver();
		btEnabled = Settings.System.getString(resolver, "bt_enable").equals("1");
		btConnected = Settings.System.getString(resolver, "bt_connect").equals("1");
	}
	
	
	//蓝牙错误，断开后重新连接
	public static boolean btErrorReconnected = false;
}