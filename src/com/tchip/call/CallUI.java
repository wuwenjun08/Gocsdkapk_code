package com.tchip.call;  
  
import com.goodocom.gocsdk.Config;
import com.goodocom.gocsdk.R;
import com.goodocom.gocsdk.service.GocsdkService;
import com.tchip.contact.CallLog;
import com.tchip.contact.ContactOperate;
import com.tchip.util.GocMessage;
import com.tchip.util.OperateCommand;

import android.app.Activity;  
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;  
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;  
import android.widget.Toast;
  
/**
 * 
 * 电话界面显示，来电界面，通话界面
 * @author wwj
 *
 */
public class CallUI extends Activity implements OnClickListener {  
	//来电界面
	private LinearLayout callIncomingLayout, voiceTransfer;
	private TextView callIncomingName;
	private ImageButton callIncomingAccept, callIncomingHangup;
	//通话界面
	private RelativeLayout callingLayout;
	private TextView callingName, callingNumber, callingTime;
	private Button callingHangUp;
	private ImageButton voiceTransferToBT, voiceTransferToPhone;
	
	private static boolean voiceOnPhone = false;
	
	AudioManager audioManager;
	

	//声明电源管理器
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	//声明键盘管理器
	KeyguardManager km = null; 
	//声明键盘锁
	private KeyguardLock kl = null; 
      
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
    	Log.d("callUI", "onCreate");
        setContentView(R.layout.call_ui);  

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        voiceOnPhone = false;
        initUI();
        
        //callingNumber.setText(Config.callNumber);
        //初始化锁屏和屏幕亮度控制
        km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);  
        kl = km.newKeyguardLock("");   
        pm=(PowerManager) getSystemService(Context.POWER_SERVICE);  
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright"); 
        if(!pm.isScreenOn()){ 
	        //解锁  
	        kl.disableKeyguard();  
            //点亮屏幕  
            wl.acquire();  
            //释放  
            wl.release();
		}
        
        //来电则显示接听按钮
        boolean incoming = getIntent().getBooleanExtra("incoming", false);
        if(incoming){
        	callIncomingLayout.setVisibility(View.VISIBLE);
        	callingLayout.setVisibility(View.GONE);
        }else{
        	callIncomingLayout.setVisibility(View.GONE);
        	callingLayout.setVisibility(View.VISIBLE);
        }
        //设置通话联系人，号码
    	String name = Config.cl.getName();
    	callIncomingName.setText("未知号码".equals(name) ? Config.cl.getNumber() : name);
    	callingName.setText(name);
    	callingNumber.setText(Config.cl.getNumber());
    	//设置蓝牙手机切换状态
		voiceTransferToBT.setEnabled(voiceOnPhone);
		voiceTransferToPhone.setEnabled(!voiceOnPhone);

    	Config.callUIShow = true;
    	
        //初始化BroadcastReceiver
        initGocReceiver();
    }  
    
    /**
     * 初始化UI界面
     */
    private void initUI(){
    	initCallIncomingUI();
    	initCallingUI();
    }
    /**
     * 初始化来电UI界面
     */
    private void initCallIncomingUI(){
    	callIncomingLayout = (LinearLayout) findViewById(R.id.call_incoming_layout);
    	callIncomingName = (TextView) findViewById(R.id.call_incoming_number);
    	callIncomingAccept = (ImageButton) findViewById(R.id.call_incoming_accept); 
    	callIncomingHangup = (ImageButton) findViewById(R.id.call_incoming_hangup); 
    	callIncomingAccept.setOnClickListener(this);
    	callIncomingHangup.setOnClickListener(this);
    }
    /**
     * 初始化通话UI界面
     */
    private void initCallingUI(){
    	callingLayout = (RelativeLayout) findViewById(R.id.calling_layout);
    	callingName = (TextView) findViewById(R.id.calling_name);
    	callingNumber = (TextView) findViewById(R.id.calling_number);
    	callingTime = (TextView) findViewById(R.id.calling_time);
    	callingHangUp = (Button) findViewById(R.id.calling_hangup);
    	voiceTransfer = (LinearLayout) findViewById(R.id.voice_transfer);
    	voiceTransferToBT = (ImageButton) findViewById(R.id.voice_transfer_to_bt);
    	voiceTransferToPhone = (ImageButton) findViewById(R.id.voice_transfer_to_phone);

    	if(Config.cl.getNumber().equals("112")){
    		voiceTransfer.setVisibility(View.VISIBLE);
    		handler.post(r);
    		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
    		audioManager.setSpeakerphoneOn(true);
    		audioManager.setMode(AudioManager.ROUTE_SPEAKER);
    		//startGocsdkService(OperateCommand.VOICE_TRANSFER_BT);
    	}else{
    		voiceTransfer.setVisibility(View.INVISIBLE);
    	}
    	callingHangUp.setOnClickListener(this);
    	voiceTransferToBT.setOnClickListener(this);
    	voiceTransferToPhone.setOnClickListener(this);
    }

    public void onResume(){
    	super.onResume();
    	Log.d("callUI", "onResume");
    }
    
    public void onPause(){
    	super.onPause();
    }
    
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        if(gocReceiver != null)
        	unregisterReceiver(gocReceiver);
	    //audioManager.setMicrophoneMute(false);
        audioManager.setSpeakerphoneOn(false);
		audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }
    
    public boolean dispatchKeyEvent(KeyEvent event){
    	switch(event.getKeyCode()){
    	case KeyEvent.KEYCODE_BACK:
    		try{
    			Intent intentLauncher = new Intent();
    			ComponentName comp = new ComponentName("com.tchip.carlauncher", "com.tchip.carlauncher.ui.activity.MainActivity");
    			intentLauncher.setComponent(comp);
    			intentLauncher.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			startActivity(intentLauncher);
    		}catch(Exception e){
    			//没有发现carlauncher
    		}
    		return true;
    	case KeyEvent.KEYCODE_VOLUME_UP:
    		if(event.getAction() == KeyEvent.ACTION_DOWN)
    			startGocsdkService(OperateCommand.SET_VOICE_UP);
    		return true;
    	case KeyEvent.KEYCODE_VOLUME_DOWN:
    		if(event.getAction() == KeyEvent.ACTION_DOWN)
    			startGocsdkService(OperateCommand.SET_VOICE_DOWN);
    		return true;
    	}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.call_incoming_accept:
			//接听来电：接听按钮gone，发送接听命令
			callIncomingAccept.setVisibility(View.GONE);
        	callIncomingLayout.setVisibility(View.GONE);
        	callingLayout.setVisibility(View.VISIBLE);
			startGocsdkService(OperateCommand.CALL_ANSWER);
			callIncomingAccept.setEnabled(false);
			callIncomingHangup.setEnabled(false);
			break;
		case R.id.call_incoming_hangup:
		case R.id.calling_hangup:
			//挂断电话
	    	Config.callUIShow = false;
			startGocsdkService(OperateCommand.CALL_HANG_UP);	
			callIncomingHangup.setEnabled(false);
			callingHangUp.setEnabled(false);
			break;
		case R.id.voice_transfer_to_bt:
			//声音切换到蓝牙
			startGocsdkService(OperateCommand.VOICE_TRANSFER_BT);
			//voiceTransferToBT.setEnabled(false);
			//voiceTransferToPhone.setEnabled(true);
			//voiceOnPhone = !voiceOnPhone;
			break;
		case R.id.voice_transfer_to_phone:
			//声音切换到手机
			startGocsdkService(OperateCommand.VOICE_TRANSFER_PHONE);
			//voiceTransferToBT.setEnabled(true);
			//voiceTransferToPhone.setEnabled(false);
			//voiceOnPhone = !voiceOnPhone;
			break;
		}
	}  
	
	/**
	 * 启动GocsdkService
	 * @param command
	 */
	private void startGocsdkService(String command){
	    Intent intent = new Intent(CallUI.this,GocsdkService.class);  
	    if(command != null)
	    	intent.putExtra("command", command);
        startService(intent);
	}
	
	/**
	 * 启动一线程用来更新通话时间
	 */
	int time = 0;
	Handler handler = new Handler();
	Runnable r = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int sec = time % 60;
			int min = time / 60;
			int hour = time / 3600;
			min = (min >= 60) ? (min % 60) : min;
			if(hour != 0){
				callingTime.setText(hour + ":" + ((min > 9) ? min : "0" + min) + ":" + ((sec > 9) ? sec : "0" + sec));
			}else{
				callingTime.setText(((min > 9) ? min : "0" + min) + ":" + ((sec > 9) ? sec : "0" + sec));
			}
			time ++;
			handler.postDelayed(r, 1000);
		}
		
	};

	/**
	 * 初始化消息
	 */
	private void initGocReceiver(){
		gocReceiver = new GocReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GocMessage.CALL_ONTALKING);
		filter.addAction(GocMessage.CALL_HANG_UP);
		filter.addAction(GocMessage.BT_DISCONNECTED);
		filter.addAction(GocMessage.CALL_RING_START);
		filter.addAction(GocMessage.CALL_RING_STOP);
		filter.addAction(GocMessage.PHONE_TRANSFER_TO_BT);
		filter.addAction(GocMessage.PHONE_TRANSFER_TO_MOBILE);
		registerReceiver(gocReceiver, filter);
	}
	
	/**
	 * 接受GocsdkService发的message消息
	 */
	GocReceiver gocReceiver;
    private class GocReceiver extends BroadcastReceiver {    	
        @Override
        public void onReceive(Context context, Intent intent) {
        	String action = intent.getAction();
        	Log.d("callUI", "action : " + action);
        	
        	if(GocMessage.CALL_HANG_UP.equals(action) || GocMessage.BT_DISCONNECTED.equals(action)){
        		//电话挂断
        		if(time == 0){
        			Config.cl.setType(Config.MISSED_TYPE);
        		}
        		Config.cl.setDuration(getCallLogTime(time));
    			
    			//插入通话记录
    			ContactOperate.storeCallHistory(CallUI.this, Config.cl);
    			Config.cl = new CallLog();
    			Config.callLogChanged = true;    
    			try{
    				handler.removeCallbacks(r);
    			}catch(Exception e){
    				
    			}
            	Log.d("callUI", "call ui finish...");
            	Config.callUIShow = false;
        		CallUI.this.finish();
        	}else if(GocMessage.CALL_RING_START.equals(action)){
        		Log.d("wwj_test", "CALL_RING_START");
        	}else if(GocMessage.CALL_RING_STOP.equals(action)){
        		//CallUI.this.finish();
        		Log.d("wwj_test", "CALL_RING_STOP");
        	}else if(GocMessage.CALL_ONTALKING.equals(action)){
        		//电话接通
        		Log.d("wwj_test", "CALL_ONTALKING");
        		//startGocsdkService(OperateCommand.VOICE_TRANSFER_BT);
        		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        		audioManager.setSpeakerphoneOn(true);
        		audioManager.setMode(AudioManager.ROUTE_SPEAKER);
        		
            	voiceTransfer.setVisibility(View.VISIBLE);
            	callIncomingLayout.setVisibility(View.GONE);
            	callingLayout.setVisibility(View.VISIBLE);
        		handler.post(r);
        	}else if(GocMessage.PHONE_TRANSFER_TO_BT.equals(action)){
    			voiceTransferToBT.setEnabled(false);
    			voiceTransferToPhone.setEnabled(true);
    			voiceOnPhone = false;
        	}else if(GocMessage.PHONE_TRANSFER_TO_MOBILE.equals(action)){
    			voiceTransferToBT.setEnabled(true);
    			voiceTransferToPhone.setEnabled(false);
    			voiceOnPhone = true;
        	}
        }
    }
    
    /**
     * 转化通话时间
     * @param callTime
     * @return
     */
    private String getCallLogTime(int callTime){
    	String callLogTime = "";
    	int sec = callTime % 60;
		int min = callTime / 60;
		int hour = callTime / 3600;
		min = (min >= 60) ? (min % 60) : min;
		
		if(callTime == 0){
			callLogTime = "";
		}else if(hour != 0){
			callLogTime = hour + "小时" + min + "分" + sec + "秒";
		}else if(min == 0){
			callLogTime = sec + "秒";
		}else if(sec == 0){
			callLogTime = min + "分";
		}else{
			callLogTime = min + "分" + sec + "秒";
		}
		return callLogTime;
    }
}  