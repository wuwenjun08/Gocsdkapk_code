package com.tchip.util;


/**
 * 
 * 操作命令
 * @author wwj
 *
 */
public class OperateCommand{
	//拨打电话
	public static String CALL_DEAIL = "CD";
	//挂断电话
	public static String CALL_HANG_UP = "CH";
	//接听电话
	public static String CALL_ANSWER = "CA";
	//通话按键
	public static String CALL_DTMF_CODE = "CC";

	//打开蓝牙
	public static String OPEN_BT = "OB";
	//关闭蓝牙
	public static String CLOSE_BT = "CB";
	

	//声音切换到蓝牙
	public static String VOICE_TRANSFER_BT = "VTB";
	//声音切换到手机
	public static String VOICE_TRANSFER_PHONE = "VTP";
	
	//设置音量
	public static String SET_VOICE_UP = "SVP";
	public static String SET_VOICE_DOWN = "SVD";
	
	public static String BT_DISCOVERY = "BD";
	
	//蓝牙音乐
	public static String BT_MUSIC_MUTE = "BMM";
	public static String BT_MUSIC_UNMUTE = "BMU";
	
	//蓝牙发现
	public static String BT_FOUND_CONNECT = "BFC";
	public static String BT_UNFOUND_UNCONNECT = "BUU";
}