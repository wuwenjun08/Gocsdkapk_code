package com.goodocom.gocsdk;

import com.tchip.contact.CallLog;
import android.media.AudioManager;

public class Config {
	public static final boolean DEBUG = true;
	
	public static final boolean JAVA_SDK = true;
	public static final boolean JAVA_PLAYER = false;

	public static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;

	public static final String CONTROL_SOCKET_NAME = "goc_control";
	public static final String DATA_SOCKET_NAME = "goc_data";
	public static final String SERIAL_SOCKET_NAME = "goc_serial";

	public static final String[] RING_PATH = new String[] { 
		"/system/ring.mp3",
		"/mtc/ring.mp3" 
	};
	
	//当前音量
	public static int currentVolume = 18;
	
	//通话界面是否显示
	public static boolean callUIShow = false;
	
	//蓝牙名称
	public static String BT_NAME = "TianQi_BT";
	//蓝牙配对密钥
	public static String BT_PIN_CODE = "0000";
	//蓝牙配对手机名称
	public static String BT_PARI_NAME = "";
	//蓝牙配对手机MAC地址
	public static String BT_PARI_MAC = null;
	

	//联系人变化
	public static boolean contactChanged = false;
	public static boolean contactSyncing = false;
	public static boolean contactDeleting = false;
	//通话状态
	public static boolean callLogChanged = false;
	public static CallLog cl = new CallLog();
	
	//通话接听状态
	public static String INCOMING_TYPE = "呼入";
	public static String OUTGOING_TYPE = "呼出";
	public static String MISSED_TYPE = "未接通";
	
	//蓝牙播放音乐
	public static boolean btMusic = false;
	public static boolean tqBtMusic = false;

	/**
	 * 根据号码查询联系人名字
	 * @param name
	 * @param phoneNum
	 * @return
	public static void setCallNumber(Context context, String phoneNum) {
		// 获取用来操作数据的类的对象，对联系人的基本操作都是使用这个对象
		ContentResolver cr = context.getContentResolver();
		// 查询contacts表的所有记录
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		// 如果记录不为空
		if (cursor.getCount() > 0) {
			// 游标初始指向查询结果的第一条记录的上方，执行moveToNext函数会判断
			// 下一条记录是否存在，如果存在，指向下一条记录。否则，返回false。
			while (cursor.moveToNext()) {
				String rawContactId = "";
				// 从Contacts表当中取得ContactId
				String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				// 获取RawContacts表的游标
				Cursor rawContactCur = cr.query(RawContacts.CONTENT_URI, null, RawContacts._ID + "=?", new String[] { id }, null);
				// 该查询结果一般只返回一条记录，所以我们直接让游标指向第一条记录
				if (rawContactCur.moveToFirst()) {
					// 读取第一条记录的RawContacts._ID列的值
					rawContactId = rawContactCur.getString(rawContactCur
							.getColumnIndex(RawContacts._ID));
					Log.v("rawContactID", rawContactId);

				}
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); 
				// 关闭游标
				rawContactCur.close();
				
				//if(contactName.equals(name)){  
					// 读取号码
					if (Integer.parseInt(cursor.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						// 根据查询RAW_CONTACT_ID查询该联系人的号码
						Cursor phoneCur = cr
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
												+ "=?",
										new String[] { rawContactId }, null);
						// 上面的ContactsContract.CommonDataKinds.Phone.CONTENT_URI
						// 可以用下面的phoneUri代替
						// Uri
						// phoneUri=Uri.parse("content://com.android.contacts/data/phones");
	
						// 一个联系人可能有多个号码，需要遍历
						while (phoneCur.moveToNext()) {
							// 获取号码
							String number = phoneCur
									.getString(phoneCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							number = number.replace(" ", "");
							if(number.equals(phoneNum)){
								phoneCur.close();
								cursor.close();
								callNumber = contactName;
								return;
							}
							Log.v("number", number);
							// 获取号码类型
							String type = phoneCur
									.getString(phoneCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
							Log.v("type", type);
	
						}
						phoneCur.close();
					}
				//}
			}
			cursor.close();
		}
		callNumber = phoneNum;
		return;
	}*/
}
