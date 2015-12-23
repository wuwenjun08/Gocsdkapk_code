package com.tchip.database;

import java.util.ArrayList;
import java.util.List;

import com.tchip.contact.Contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *音乐数据库
 * @author wu
 *
 */
public class ContactDB extends SQLiteOpenHelper {
	public static String TABLE_NAME="contact_000000000000";
	public static final String ID="_id";
	public static final String NAME="name";
	public static final String NUMBER="number";
	
	
	public ContactDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public ContactDB(Context context, String address) {
		super(context, "tchip_contact_" + address + ".db", null, 1);
		TABLE_NAME = "contact_" + address;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		String sql="create table "+TABLE_NAME
				+"("
				+ID+" integer primary key not null,"
				+NAME+" varchar not null," 
				+NUMBER+" varchar not null" 
				+")" ;
		
		arg0.execSQL(sql);
	}
	
	public void inset(List<Contact> list){
		if(list != null){
			List<Contact> mList = list;
			Long time = System.currentTimeMillis();
			for(Contact con : mList){
				insert(con.getName(), con.getPhone());
			}
			Log.d("wwj_test", "time : " + (time - System.currentTimeMillis()));
		}
	}
	
	/**
	 *数据库插入
	 * @param name
	 * @param number
	 */
	private void insert(String name,String number){
		ContentValues c=new ContentValues();
		SQLiteDatabase s=ContactDB.this.getWritableDatabase();
		c.put(NAME, name);
		c.put(NUMBER, number);
		s.insert(TABLE_NAME, null, c);
		s.close();
		Log.d("goc", "inset :" + name + " " + number);
	}
	
	public void delete(int position){
		SQLiteDatabase s=ContactDB.this.getWritableDatabase();
		s.delete(TABLE_NAME, "_id=?", new String[]{position+""});
		s.close();
	}
	
	/**
	 * 清空数据库
	 */
	public void clearDB(){
		try{
			SQLiteDatabase s=ContactDB.this.getWritableDatabase();
			s.delete(TABLE_NAME, null, null);
			s.close();
		}catch(SQLiteException e){
			//表不存在
		}
	}

	public Cursor query(){
		SQLiteDatabase s=ContactDB.this.getReadableDatabase();
		Cursor c = null;
		try{
			c = s.query(TABLE_NAME,new String[]{ID,NAME,NUMBER}, null, null, null, null, null);
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
	public void updata(Contact con){
		update(con.getId(), 
				con.getName(), 
				con.getPhone());
	}
	/**
	 *  数据库更新
	 * @param position
	 * @param name
	 * @param number
	 */
	private void update(Long position,String name,String number){
		SQLiteDatabase s=ContactDB.this.getWritableDatabase();
		ContentValues c=new ContentValues();
		c.put(NAME, name);
		c.put(NUMBER, number);
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
