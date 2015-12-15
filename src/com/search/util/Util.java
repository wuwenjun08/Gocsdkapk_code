package com.search.util;

import com.tchip.database.ContactDB;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Spanned;

/**
 * 
 * 工具类
 */
public class Util {
	
	static final String array[] =  new String []{
	                               			"ABCabc",
	                               			"DEFdef",
	                               			"GHIghi",
	                               			"JKLjkl",
	                               			"MNOmno",
	                               			"PQRSpqrs",
	                               			"TUVtuv",
	                               			"WXYZwxyz"	
	                               			};
	
	/**
	 * 
	 * 匹配字符上色
	 * @param formatString
	 * @param input：用户输入的数字
	 * @param pyToNumber：
	 * @param index:搜索索引
	 * @return
	 */
	public static Spanned formatHtml(String formatString , String py,String input , String pyToNumber , int index){
		Spanned spanned = null;
		switch (index) {
		case 0:
		case 1:
			int i = pyToNumber.indexOf(input);
			int length = formatString.length();
			String newString = "";
			if((i + input.length()) < length && i >= 0)
				newString = formatString.substring(i , i + input.length());
			char[] chars = newString.toCharArray();
			int n = 0 , c = 0;
			StringBuffer sbf = new StringBuffer();
			char[] pyChar = py.toCharArray();
			for( ; n < py.length() ; n ++){
				if(c < chars.length && pyChar[n] == chars[c]){
					c ++ ;
					sbf.append("<b><font color=#0000ff>"+pyChar[n]+"</font></b>");
				}else{
					sbf.append(pyChar[n]);
				}
			}
			spanned = Html.fromHtml(sbf.toString());
			break;
		case 2: {
			StringBuffer regxString = new StringBuffer();
			try {
				for (int j = 0; j < input.length(); j++) {
					regxString.append("[" + array[input.charAt(j) - '2'] + "]");
				}
			} catch (Exception e) {
			}
			formatString = formatString.replaceFirst("(" + regxString + ")", "<b><font color=#0000ff>$1</font></b>");
			spanned = Html.fromHtml(formatString);
			break;
		}
		case 3:
			try{
				spanned = Html.fromHtml(formatString.replaceFirst("(" + input + ")","<font color=#0000ff>$1</font>"));
			}catch(Exception e){
				
			}
			break;
		}
		
		return spanned;
	}
	
	/**
	 * 
	 * 获取联系人
	 * @param mContext
	 */
	public static void getContacts(Context mContext, String address){
		//Cursor phoneCur = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null, null);
		ContactDB cDB = new ContactDB(mContext, address);
		Cursor phoneCur = cDB.query();
		Dfine.user.clear();
		while (phoneCur.moveToNext()) {
			ContactInfo contactInfo = new ContactInfo();
			contactInfo.personId = phoneCur.getLong(phoneCur.getColumnIndex(ContactDB.ID));
			contactInfo.number = ConverChineseCharToEn.replaceString(phoneCur.getString(phoneCur.getColumnIndex(ContactDB.NUMBER)));
			
			contactInfo.name = phoneCur.getString(phoneCur.getColumnIndex(ContactDB.NAME));
			if (contactInfo.name == null) {
				contactInfo.name = contactInfo.number;
			}
			
			contactInfo.lastNamePy = ConverChineseCharToEn.converterToAllFirstSpellsUppercase(contactInfo.name);
			contactInfo.namePy = ConverChineseCharToEn.converterToPingYingHeadUppercase(contactInfo.name).replace("-", "");
			
			contactInfo.lastNameToNumber = ConverChineseCharToEn.converEnToNumber(contactInfo.lastNamePy);
			contactInfo.nameToNumber = ConverChineseCharToEn.converEnToNumber(contactInfo.namePy).replace("-", "");
			
			/*
			System.out.println("名字："+contactInfo.name);
			System.out.println("号码："+contactInfo.number);
			System.out.println("全拼名字："+contactInfo.namePy);
			System.out.println("全拼数字："+contactInfo.nameToNumber);
			System.out.println("首拼名字："+contactInfo.lastNamePy);
			System.out.println("首拼数字："+contactInfo.lastNameToNumber);
			*/
			
			Dfine.user.add(contactInfo);
		}
		mContext.sendBroadcast(new Intent("reset.list"));
	}

}
