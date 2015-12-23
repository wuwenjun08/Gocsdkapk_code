package com.tchip.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

/**
 * 
 * 对蓝牙节点文件进行操作
 * @author wwj
 *
 */
public class BTFileOperater{
	
	public static File btFile = new File("/sys/devices/platform/mt-i2c.1/i2c-1/1-007f/bt_car_status");
	public static File speakerFile = new File("/sys/devices/platform/mt-i2c.1/i2c-1/1-007f/bt_car_status");
	/**
	 * 把蓝牙状态写入文件，供kernel操作
	 * @param value
	 */
	public static void writeBTFile(String value) {
		if (btFile.exists()) {
			try {
				StringBuffer strbuf = new StringBuffer("");
				strbuf.append(value);
				OutputStream output = null;
				OutputStreamWriter outputWrite = null;
				PrintWriter print = null;

				try {
					output = new FileOutputStream(btFile);
					outputWrite = new OutputStreamWriter(output);
					print = new PrintWriter(outputWrite);
					print.print(strbuf.toString());
					print.flush();
					output.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Log.e("goc", "SaveFileToNode:output error");
				}
			} catch (IOException e) {
				Log.e("goc", "SaveFileToNode:IO Exception");
			}
		} else {
			Log.e("goc", "SaveFileToNode:File:" + btFile + "not exists");
		}
	}
	/**
	 * 把喇叭状态写入文件，供kernel操作
	 * 写2为打开speaker
	 * 写3为关闭speaker
	 * @param value
	 */
	public static void openSpeaker(){
		writeSpeakerFile("2");
	}
	public static void closeSpeaker(){
		writeSpeakerFile("3");
	}
	private static void writeSpeakerFile(String value) {
		if (btFile.exists()) {
			try {
				StringBuffer strbuf = new StringBuffer("");
				strbuf.append(value);
				OutputStream output = null;
				OutputStreamWriter outputWrite = null;
				PrintWriter print = null;

				try {
					output = new FileOutputStream(speakerFile);
					outputWrite = new OutputStreamWriter(output);
					print = new PrintWriter(outputWrite);
					print.print(strbuf.toString());
					print.flush();
					output.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Log.e("goc", "SaveFileToNode:output error");
				}
			} catch (IOException e) {
				Log.e("goc", "SaveFileToNode:IO Exception");
			}
		} else {
			Log.e("goc", "SaveFileToNode:File:" + btFile + "not exists");
		}
	}
	

	/**
	 * ACC状态节点
	 */
	public static File fileAccStatus = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-007f/acc_car_status");

	/**
	 * 获取ACC状态
	 * 
	 * @return 0:ACC下电
	 * 
	 *         1:ACC上电
	 */
	public static boolean getAccStatus() {
		return getFileInt(fileAccStatus) == 1;
		//return true;
	}
	
	public static void setAirplaneModeOn(Context context) {  
		try {
			if(Settings.System.getInt(context.getContentResolver() , Settings.System.AIRPLANE_MODE_ON) == 1)
				Settings.System.putInt(context.getContentResolver(),  Settings.System.AIRPLANE_MODE_ON, 0);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}  

	public static int getFileInt(File file) {

		if (file.exists()) {
			try {
				InputStream is = new FileInputStream(file);
				InputStreamReader fr = new InputStreamReader(is);
				int ch = 0;
				if ((ch = fr.read()) != -1)
					return Integer.parseInt(String.valueOf((char) ch));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
}