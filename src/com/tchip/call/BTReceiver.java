package com.tchip.call;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;

import com.goodocom.gocsdk.service.GocsdkService;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;


/**
 * 监听usb camera按键信号
 * 
 * @author wu
 *
 */
public class BTReceiver extends BroadcastReceiver {
	private String TAG = "BTReceiver";
	
	private Context context;
	private String action;
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	this.context = context;
    	action = intent.getAction();
		if(action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals("com.tchip.ACC_ON")){
			startBTService();
		}
    }
    
    /**
     * 启动蓝牙服务
     */
    private void startBTService(){
    	Intent intent = new Intent(context, GocsdkService.class);
    	context.startService(intent);
    }
}
