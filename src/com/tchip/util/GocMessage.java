package com.tchip.util;


/**
 * 
 * 发送intent message定义
 * @author wwj
 *
 */
public class GocMessage{
	//打开蓝牙
	public static String BT_OPENED= "com.tchip.BT_OPENED";
	//关闭蓝牙
	public static String BT_CLOSED= "com.tchip.BT_CLOSED";
	//蓝牙名称获取成功
	public static String BT_NAME_GET= "com.tchip.BT_NAME_GET";
	//蓝牙连接成功
	public static String BT_CONNECTED= "com.tchip.BT_CONNECTED";
	//蓝牙连接失败
	public static String BT_DISCONNECTED = "com.tchip.BT_DISCONNECTED";
	//联系人同步完毕
	public static String CONTACT_SYNC_DONE = "com.tchip.CONTACT_SYNC_DONE";
	//联系人删除完毕
	public static String CONTACT_DELETE_DONE = "com.tchip.CONTACT_DELETE_DONE";
	//拨号成功
	public static String CALL_SUCCESS = "com.tchip.CALL_SUCCESS";
	//开始通话
	public static String CALL_ONTALKING = "com.tchip.CALL_ONTALKING_T";
	//电话挂断
	public static String CALL_HANG_UP = "com.tchip.CALL_HANG_UP";
	//来电
	public static String CALL_INCOMING = "com.tchip.CALL_INCOMING";
	//来电开始响铃
	public static String CALL_RING_START = "com.tchip.CALL_RING_START";
	//来电停止响铃
	public static String CALL_RING_STOP = "com.tchip.CALL_RING_STOP";

	//获取连接蓝牙名称成功
	public static String BT_CONNECTED_NAME= "com.tchip.BT_CONNECTED_NAME";
	
	//联系人读取到
	public static String CONTACT_RECEIVER = "com.tchip.CONTACT_RECEIVER";
	//联系人已读取完毕
	public static String CONTACT_READED = "com.tchip.CONTACT_READED";
	//同步联系人
	public static String SYNC_CONTACT = "com.tchip.SYNC_CONTACT";
	//删除联系人
	public static String DELETE_CONTACT = "com.tchip.DELETE_CONTACT";
	//设置蓝牙名称
	public static String SET_BT_NAME = "com.tchip.SET_BT_NAME";
	//设置蓝牙配对密钥
	public static String SET_BT_PIN_CODE = "com.tchip.SET_BT_PIN_CODE";
	//插入通话记录
	public static String SYNC_CALL_LOG = "com.tchip.SYNC_CALL_LOG";
	//蓝牙连接手机
	public static String CONNECT_PHONE = "com.tchip.CONNECT_PHONE";
	//蓝牙断开手机
	public static String DISCONNECT_PHONE = "com.tchip.DISCONNECT_PHONE";
	

	//蓝牙搜索设备
	public static String DISCOVERY_PHONE = "com.tchip.DISCOVERY_PHONE";
	//蓝牙搜索成功
	public static String DISCOVERY_SUCCESSED = "com.tchip.DISCOVERY_SUCCESSED";
	//蓝牙搜索完成
	public static String DISCOVERY_DONE = "com.tchip.DISCOVERY_DONE";
	
	//语音蓝牙搜索状态
	public static String GET_BLT_STATUS = "com.android.bt.getBLTStatus";
	//语音接听电话
	public static String SPEECH_ACCEPT_CALL = "action.intent.AIOS_ACCEPT";

	//声音切换到蓝牙
	public static String PHONE_TRANSFER_TO_BT= "com.tchip.PHONE_TRANSFER_TO_BT";
	//声音切换到手机
	public static String PHONE_TRANSFER_TO_MOBILE= "com.tchip.PHONE_TRANSFER_TO_MOBILE";
}