/*package com.tchip.database;   
  
import com.goodocom.gocsdk.Config;

import android.content.ContentProvider;   
import android.content.ContentValues;   
import android.content.UriMatcher;   
import android.database.Cursor;   
import android.net.Uri;   
  
public class ContactDBHelper extends ContentProvider {   
    private ContactDB dbOpenHelper;   
    //常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码   
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);   
    private static final int USERS = 1;   
    private static final int USER = 2;   
    static{   
        MATCHER.addURI("com.tchip.database.ContactDBHelper", "user", USERS);   
    }      
    @Override  
    public int delete(Uri uri, String selection, String[] selectionArgs) {   
    	return 0;
    }   
  
    *//**  
     * 该方法用于返回当前Url所代表数据的MIME类型。  
     * 如果操作的数据属于集合类型，那么MIME类型字符串应该以vnd.android.cursor.dir/开头  
     * 如果要操作的数据属于非集合类型数据，那么MIME类型字符串应该以vnd.android.cursor.item/开头  
     *//*  
    @Override  
    public String getType(Uri uri) {   
    	return null;   
    }   
  
    @Override  
    public Uri insert(Uri uri, ContentValues values) {   
        return null; 
    }   
  
    @Override  
    public boolean onCreate() {   
        this.dbOpenHelper = new ContactDB(this.getContext(), Config.BT_PARI_MAC);   
        return false;   
    }   
  
    @Override  
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,   
            String sortOrder) {   
        //SQLiteDatabase db = dbOpenHelper.getReadableDatabase();   
        switch (MATCHER.match(uri)) {   
        case USERS:   
            return dbOpenHelper.query();   
        default:   
            throw new IllegalArgumentException("Unkwon Uri:"+ uri.toString());   
        }   
    }   
  
    @Override  
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {   
        return -1;  
    }   
}  */