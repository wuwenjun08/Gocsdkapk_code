/*package com.tchip.contact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.goodocom.gocsdk.Config;
import com.goodocom.gocsdk.service.GocsdkService;
import com.search.util.ContactInfo;
import com.search.util.ConverChineseCharToEn;
import com.search.util.Dfine;
import com.tchip.database.ContactDB;
import com.tchip.util.GocMessage;
import com.tchip.util.PinyinComparator;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

*//**
 * 
 * 对联系人数据库进行操作
 * 
 * @author wwj
 * 
 *//*
public class ContactOperate_back {
	
	public static List<Contact> contactList = new ArrayList<Contact>();
	
	*//**
	 * 是否用本地存储
	 *//*
	public static boolean useTchipDB = true;
	
	*//**
	 * 是否正在同步联系人
	 *//*
	public static boolean tchipSyncingContact = false;
	
	*//**
	 * 是否正在删除联系人
	 *//*
	public static boolean tchipDeletingContact = false;
	
	
	*//**
	 * 获取数据库联系人，加载到contactList中
	 * @param context
	 *//*
	public static void getContact(Context context){
		contactList.clear();
		Dfine.user.clear();
		if(!(tchipSyncingContact || tchipDeletingContact)){
			if(useTchipDB){
				ContactDB cDB = new ContactDB(context, Config.BT_PARI_MAC);
				Cursor cursor = cDB.query();
				if(cursor != null && cursor.getCount() != 0){
					for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
						//获取联系人，放入contactList
						Contact con = new Contact();
						//con.setId(cursor.getInt(cursor.getColumnIndex(ContactDB.ID)));
						con.setName(cursor.getString(cursor.getColumnIndex(ContactDB.NAME)));
						con.setPhone(cursor.getString(cursor.getColumnIndex(ContactDB.NUMBER)));
						contactList.add(con);
						
						//获取联系人，放入Dfine.user供搜索使用
						ContactInfo contactInfo = new ContactInfo();
						contactInfo.personId = cursor.getLong(cursor.getColumnIndex(ContactDB.ID));
						contactInfo.number = ConverChineseCharToEn.replaceString(con.getPhone());						
						contactInfo.name = con.getName();
						if (contactInfo.name == null) {
							contactInfo.name = contactInfo.number;
						}						
						contactInfo.lastNamePy = ConverChineseCharToEn.converterToAllFirstSpellsUppercase(contactInfo.name);
						contactInfo.namePy = ConverChineseCharToEn.converterToPingYingHeadUppercase(contactInfo.name).replace("-", "");
						
						contactInfo.lastNameToNumber = ConverChineseCharToEn.converEnToNumber(contactInfo.lastNamePy);
						contactInfo.nameToNumber = ConverChineseCharToEn.converEnToNumber(contactInfo.namePy).replace("-", "");
						
						
						System.out.println("名字："+contactInfo.name);
						System.out.println("号码："+contactInfo.number);
						System.out.println("全拼名字："+contactInfo.namePy);
						System.out.println("全拼数字："+contactInfo.nameToNumber);
						System.out.println("首拼名字："+contactInfo.lastNamePy);
						System.out.println("首拼数字："+contactInfo.lastNameToNumber);
						
						
						Dfine.user.add(contactInfo);
					}
					cursor.close();
					context.sendBroadcast(new Intent("reset.list"));
				}
			}else{
				contactList = getContacts(context);
			}
		}
	}
	
	
	
	
	*//**
	 * 判断联系人是否存在
	 * @param name
	 * @param phoneNum
	 * @return
	 *//*
	public static synchronized boolean isExit(Context context, String name, String phoneNum) {
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
					rawContactId = rawContactCur.getString(rawContactCur.getColumnIndex(RawContacts._ID));
					Log.v("rawContactID", rawContactId);

				}
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); 
				// 关闭游标
				rawContactCur.close();
				
				if(contactName != null && contactName.equals(name)){  
					// 读取号码
					if (Integer.parseInt(cursor.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						// 根据查询RAW_CONTACT_ID查询该联系人的号码
						Cursor phoneCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
										ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + "=?",
										new String[] { rawContactId }, null);
	
						// 一个联系人可能有多个号码，需要遍历
						while (phoneCur.moveToNext()) {
							// 获取号码
							String number = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							if(number.equals(phoneNum)){
								phoneCur.close();
								cursor.close();
								return true;
							}
							Log.v("number", number);
							// 获取号码类型
							String type = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
							Log.v("type", type);
	
						}
						phoneCur.close();
					}
				}
			}
		}
		cursor.close();
		return false;
	}

	*//**
	 * 
	 * 更新联系人
	 * @param rawContactId
	 * @param phoneNum
	 *//*
	public static void updataCotact(Context context, long rawContactId, String phoneNum) {
		ContentValues values = new ContentValues();
		values.put(Phone.NUMBER, phoneNum);
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);
		String where = ContactsContract.Data.RAW_CONTACT_ID + "=? AND "
				+ ContactsContract.Data.MIMETYPE + "=?";
		String[] selectionArgs = new String[] { String.valueOf(rawContactId),
				Phone.CONTENT_ITEM_TYPE };
		context.getContentResolver().update(ContactsContract.Data.CONTENT_URI, values,
				where, selectionArgs);
	}
	
	*//**
	 * 删除联系人
	 * @param rawContactId
	 *//*
		public static void deleteContact(Context context, long rawContactId) {
			context.getContentResolver().delete(
					ContentUris.withAppendedId(RawContacts.CONTENT_URI,
							rawContactId), null, null);
		}


	*//**
	 * 获取联系人
	 *//*		
	private static final String[] PHONES_PROJECTION = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER};  
	*//**联系人显示名称**//*  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
      
    *//**电话号码**//*  
    private static final int PHONES_NUMBER_INDEX = 1;  
      
    *//**头像ID**//*  
    private static final int PHONES_PHOTO_ID_INDEX = 2;  
     
    *//**联系人的ID**//*  
    private static final int PHONES_CONTACT_ID_INDEX = 3;  
    private static ContentResolver resolver;
	public static List<Contact> getContacts(Context context) {
		if(resolver == null)
			resolver = context.getContentResolver();
		List<Contact> list = new ArrayList<Contact>();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				Contact con = new Contact();

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				//Log.d("goc",contactName + phoneNumber);

				
				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(),
							R.drawable.contact_photo);
				}

				con.setName(contactName);
				con.setPhone(phoneNumber);
				list.add(con);
			}

			phoneCursor.close();
		}
		return list;
	}

	*//**
	 * 获取联系人
	public static List<Contact> getContacts(Context context) {
		// 获取用来操作数据的类的对象，对联系人的基本操作都是使用这个对象
		ContentResolver cr = context.getContentResolver();
		// 查询contacts表的所有记录
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		List<Contact> list = new ArrayList<Contact>();
		// 如果记录不为空
		if (cursor.getCount() > 0) {
			// 游标初始指向查询结果的第一条记录的上方，执行moveToNext函数会判断
			// 下一条记录是否存在，如果存在，指向下一条记录。否则，返回false。
			while (cursor.moveToNext()) {
				String rawContactId = "";
				// 从Contacts表当中取得ContactId
				String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				Contact con = new Contact(id);
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
				con.setName(contactName);  
				// 关闭游标
				rawContactCur.close();
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
						con.setPhone(number);
						Log.v("number", number);
						// 获取号码类型
						String type = phoneCur
								.getString(phoneCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
						Log.v("type", type);

					}
					phoneCur.close();

				}
				if(con.getName() != null && con.getPhone() != null)
					list.add(con);
			}
			cursor.close();
		}
		return list;
	}
	 *//*
	
	*//**
	 * 清除通话记录
	 * @param context
	 *//*
	public static void clearCallHistory(Context context){
		if(resolver == null)
			resolver = context.getContentResolver();
        resolver.delete(CallLog.Calls.CONTENT_URI, null, null);  
        //Toast.makeText(context, "通话记录已清除", Toast.LENGTH_SHORT).show();
        Config.callLogChanged = true;
	}
	
	*//**
	 * 获取通话记录
	 * @param context
	 *//*
	public static List<CallHistory> getCallHistory(Context context){
		List<CallHistory> list = new ArrayList<CallHistory>();    
		if(!(tchipSyncingContact || tchipDeletingContact)){
			Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,                            
			        null, null, null, null);   
			if(cursor.moveToFirst()){                                                                                
			    do{                                           
					CallHistory ch = new CallHistory();                                                               
			        //CallLog calls =new CallLog();                                                                  
			        //号码                                                                                             
			        String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));                           
			        //呼叫类型                                                                                           
			        String type;                                                                                     
			        switch (Integer.parseInt(cursor.getString(cursor.getColumnIndex(Calls.TYPE)))) {                 
			        case Calls.INCOMING_TYPE:                                                                        
			            type = "呼入";                                                                                 
			            break;                                                                                       
			        case Calls.OUTGOING_TYPE:                                                                        
			            type = "呼出";                                                                                 
			            break;                                                                                       
			        case Calls.MISSED_TYPE:                                                                          
			            type = "未接通";                                                                                 
			            break;                                                                                       
			        default:                                                                                         
			            type = "未接通";//应该是挂断.根据我手机类型判断出的                                                              
			            break;                                                                                       
			        }                                                                                                
			        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");                              
			        Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DATE))));
			        //呼叫时间                                                                                           
			        String time = sfd.format(date);                                                                  
			        //联系人                                                                                            
			        String name = cursor.getString(cursor.getColumnIndexOrThrow(Calls.CACHED_NAME));                 
			        //通话时间,单位:s                                                                                      
			        String duration  = cursor.getString(cursor.getColumnIndexOrThrow(Calls.DURATION));
			        
			        int callTime = Integer.parseInt(duration);
			        int sec = callTime % 60;
					int min = callTime / 60;
					int hour = callTime / 3600;
					min = (min >= 60) ? (min % 60) : min;
					
					if(callTime == 0){
						duration = "";
					}else if(hour != 0){
						duration = hour + "小时" + min + "分" + sec + "秒";
					}else if(min == 0){
						duration = sec + "秒";
					}else if(sec == 0){
						duration = min + "分";
					}else{
						duration = min + "分" + sec + "秒";
					}
			        
			        ch.setNumber(number);
			        ch.setName(name);
			        ch.setType(type);
			        ch.setTime(time);
			        ch.setDuration(duration);
			        list.add(ch);
			    }while(cursor.moveToNext());   
			    cursor.close();		                                                                                                          
			}
		}
		return list;
	}
	
	*//**
	 * 把通话记录保存起来
	 * @param context
	 * @param ch
	 *//*
	public static void storeCallHistory(Context context, CallHistory ch){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        
		if(ch.getName() == null){
			String name = getContactName(context, ch.getNumber());
			if(name != null){				
		        values.put(CallLog.Calls.CACHED_NAME, name);
			}
		}else
        String name = ch.getName();
        if(name != null && (!"未知号码".equals(name))){			
	        values.put(CallLog.Calls.CACHED_NAME, name);
		}
        values.put(CallLog.Calls.NUMBER, ch.getNumber());
        values.put(CallLog.Calls.DATE, ch.getTime());
        values.put(CallLog.Calls.DURATION, ch.getDuration());
        //values.put(CallLog.Calls.IS_READ, ch.getIs_read());
        values.put(CallLog.Calls.TYPE, ch.getType());
        contentResolver.insert(CallLog.Calls.CONTENT_URI, values);
	}

	*//**
	 * 根据号码查询联系人名字
	 * @param name
	 * @param phoneNum
	 * @return
	 *//*
	public static String getContactName(Context context, String phoneNum) {
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
								return contactName;
							}
							Log.v("number", number);
	
						}
						phoneCur.close();
					}
				//}
			}
			cursor.close();
		}
		return null;
	}
}*/