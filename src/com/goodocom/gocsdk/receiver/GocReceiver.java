package com.goodocom.gocsdk.receiver;

import com.goodocom.gocsdk.Config;
import com.goodocom.gocsdk.service.GocsdkService;
import com.goodocom.gocsdk.service.PlayerService;
import com.tchip.call.CallUI;
import com.tchip.util.GocMessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GocReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("GocReceiver", "get action "+intent.getAction());
		String action = intent.getAction();
		if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
			startServices(context);
		}else if(action.equals("com.goodocom.gocsdk.INIT_SUCCEED")){
			startServices(context);
		}else if(GocMessage.CALL_SUCCESS.equals(action)){
			//拨号成功，启动通话界面
			startActivity(context);
		}
		
	}
	
	private void startServices(Context context){
		if(Config.JAVA_SDK)context.startService(new Intent(context,GocsdkService.class));
		if(Config.JAVA_PLAYER)context.startService(new Intent(context,PlayerService.class));
	}
	
	/**
	 * 启动通话界面
	 * @param context
	 */
	private void startActivity(Context context){
		Intent calUIIntent = new Intent(context, CallUI.class);
		//calUIIntent.putExtra("number", Config.phoneCallUnknow);
		calUIIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(calUIIntent);
	}
}
