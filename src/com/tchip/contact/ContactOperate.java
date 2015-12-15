package com.tchip.contact;

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
import com.tchip.database.CallLogDB;
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
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * 对联系人数据库进行操作
 * 
 * @author wwj
 * 
 */
public class ContactOperate {
	
	public static List<Contact> contactList = new ArrayList<Contact>();
	public static List<CallLog> callLogList = new ArrayList<CallLog>();
	
	
	/**
	 * 获取数据库联系人，加载到contactList中
	 * @param context
	 */
	public static void getContact(Context context){
		contactList.clear();
		Dfine.user.clear();
		if(ContactCallLogStatus.contactShow()){
			ContactDB cDB = new ContactDB(context, Config.BT_PARI_MAC);
			Cursor cursor = cDB.query();
			if(cursor != null && cursor.getCount() != 0){
				for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
					//获取联系人，放入contactList
					Contact con = new Contact();
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
					Dfine.user.add(contactInfo);
				}
				cursor.close();
				context.sendBroadcast(new Intent("reset.list"));
			}
		}
	}
	
	
	/**
	 * 清除通话记录
	 * @param context
	 */
	public static void clearCallLog(Context context){
		CallLogDB clDB = new CallLogDB(context, Config.BT_PARI_MAC);
		clDB.clearDB();
		callLogList.clear();  
        //Toast.makeText(context, "通话记录已清除", Toast.LENGTH_SHORT).show();
        Config.callLogChanged = true;
	}
	
	/**
	 * 获取通话记录
	 * @param context
	 */
	public static void getCallHistory(Context context){
		callLogList.clear();    
		CallLogDB clDB = new CallLogDB(context, Config.BT_PARI_MAC);
		Cursor cursor = clDB.query();
		if(cursor != null && cursor.getCount() != 0){
			for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
				//获取通话记录，放入list
				CallLog cl= new CallLog();
				//con.setId(cursor.getInt(cursor.getColumnIndex(ContactDB.ID)));
				cl.setName(cursor.getString(cursor.getColumnIndex(CallLogDB.NAME)));
				cl.setNumber(cursor.getString(cursor.getColumnIndex(CallLogDB.NUMBER)));                   
				cl.setType(cursor.getString(cursor.getColumnIndex(CallLogDB.TYPE)));     
				cl.setTime(cursor.getString(cursor.getColumnIndex(CallLogDB.DATE)));
				cl.setDuration(cursor.getString(cursor.getColumnIndex(CallLogDB.DURATION)));
				Log.d("wwj_test", cl.getName() + cl.getTime());
				callLogList.add(cl);
			}
			cursor.close();
		}
		Collections.reverse(ContactOperate.callLogList);
	}
	
	/**
	 * 把通话记录保存起来
	 * @param context
	 * @param ch
	 */
	public static void storeCallHistory(Context context, CallLog cl){
		CallLogDB clDB = new CallLogDB(context, Config.BT_PARI_MAC);		
        String name = cl.getName();
        if(name != null && ("未知号码".equals(name))){			
	        cl.setName(cl.getNumber());
		}
		callLogList.add(0, cl);
        clDB.inset(cl);
	}
}