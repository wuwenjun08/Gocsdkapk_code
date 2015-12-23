package com.tchip.database;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tchip.contact.CallLog;
import com.tchip.contact.Contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *音乐数据库
 * @author wu
 *
 */
public class CallLogDB extends SQLiteOpenHelper {
	public static String TABLE_NAME="calllog_000000000000";
	public static final String ID="_id";
	public static final String NAME="name";
	public static final String NUMBER="number";
	public static final String TYPE="type";
	public static final String DATE="date";
	public static final String DURATION="duration";
	
	
	public CallLogDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public CallLogDB(Context context, String address) {
		super(context, "tchip_calllog_" + address + ".db", null, 1);
		TABLE_NAME = "calllog_" + address;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		String sql="create table "+TABLE_NAME
				+"("
				+ID+" integer primary key not null,"
				+NAME+" varchar not null," 
				+NUMBER+" varchar not null," 
				+TYPE+" varchar not null," 
				+DATE+" varchar not null," 
				+DURATION+" varchar not null" 
				+")" ;
		
		arg0.execSQL(sql);
	}
	
	public void inset(List<CallLog> list){
		if(list != null){
			for(CallLog callLog : list){
				insert(callLog.getName(), callLog.getName(), callLog.getType(), callLog.getTime(), callLog.getDuration());
			}
		}
	}
	
	/**
	 * 插入通话记录
	 * @param callLog
	 */
	public void inset(CallLog callLog){
		insert(callLog.getName(), callLog.getNumber(), callLog.getType(), callLog.getTime(), callLog.getDuration());
	}
	
	/**
	 *数据库插入
	 * @param name
	 * @param number
	 */
	private void insert(String name,String number, String type, String date, String duration){
		if(isNumeric(number)){
			ContentValues c=new ContentValues();
			SQLiteDatabase s=CallLogDB.this.getWritableDatabase();
			c.put(NAME, name);
			c.put(NUMBER, number);
			c.put(TYPE, type);
			c.put(DATE, date);
			c.put(DURATION, duration);
			s.insert(TABLE_NAME, null, c);
			s.close();
			Log.d("goc", "inset :" + name + " " + number);
		}
	}

	/**
	 * 检查号码是否正确
	 * @param str
	 * @return
	 */
 	public boolean isNumeric(String str){ 
 		try{
 			Pattern pattern = Pattern.compile("^[+]?[0-9]*"); 
 			Matcher isNum = pattern.matcher(str);
 			if(!isNum.matches()){
 				return false; 
 			} 
 		}catch(Exception e){
 			//匹配错误
 			return false;
 		}
 	   return true; 
	}
	
	public void delete(int position){
		SQLiteDatabase s=CallLogDB.this.getWritableDatabase();
		s.delete(TABLE_NAME, "_id=?", new String[]{position+""});
		s.close();
	}
	
	/**
	 * 清空数据库
	 */
	public void clearDB(){
		try{
			SQLiteDatabase s=CallLogDB.this.getWritableDatabase();
			s.delete(TABLE_NAME, null, null);
			s.close();
		}catch(SQLiteException e){
			//表不存在
		}
	}

	public Cursor query(){
		SQLiteDatabase s=CallLogDB.this.getReadableDatabase();
		Cursor c = null;
		try{
			c = s.query(TABLE_NAME,new String[]{ID,NAME,NUMBER,TYPE,DATE,DURATION}, null, null, null, null, null);
		}catch(SQLiteException e){
			//表不存在
		}
		return c;
	}
	
	/**
	 *  数据库更新
	 *  对外接口
	 * @param music
	 */
	public void updata(CallLog calllog){
		update(calllog.getId(), 
				calllog.getName(), 
				calllog.getNumber(),
				calllog.getType(), 
				calllog.getTime(), 
				calllog.getDuration());
	}
	/**
	 *  数据库更新
	 * @param position
	 * @param name
	 * @param number
	 */
	private void update(Long position,String name,String number, String type, String date, String duration){
		SQLiteDatabase s=CallLogDB.this.getWritableDatabase();
		ContentValues c=new ContentValues();
		c.put(NAME, name);
		c.put(NUMBER, number);
		c.put(TYPE, type);
		c.put(DATE, date);
		c.put(DURATION, duration);
		s.update(TABLE_NAME, c, "_id=?", new String[]{position+""});
		s.close();
	}	
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 获取数据库大小
	 * @return
	 */
	public int getCount(){
		ArrayList<String> datas=new ArrayList<String>();
		Cursor c=query();
		if(c != null){
			for (c.moveToFirst();!c.isLast();c.moveToNext()) {
				String title=c.getString(c.getColumnIndex(NAME));
				datas.add(title);
			}
			c.close();
		}
		return datas.size();
	}
}
