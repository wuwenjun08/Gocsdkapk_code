package com.tchip.btsettings;  

import com.goodocom.gocsdk.Config;
import com.goodocom.gocsdk.R;
import com.goodocom.gocsdk.service.GocsdkService;
import com.tchip.call.MainActivity;
import com.tchip.contact.ContactCallLogStatus;
import com.tchip.contact.ContactOperate;
import com.tchip.util.BTFileOperater;
import com.tchip.util.GocMessage;
import com.tchip.util.OperateCommand;
import com.tchip.view.SwitchButton;

import android.app.Activity;  
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;  
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;
  
/**
 * 
 * 蓝牙设置界面
 * @author wwj
 *
 */
public class BTSettings extends Activity {  
	private SwitchButton switchButton;
	private EditText btName, btPinCode;
	private RelativeLayout btPair;
	private LinearLayout syncContact;
	private TextView btConnectOperate, btConnectStatus;

	/**
	 * 初始化消息
	 */
	private void initGocReceiver(){
		gocReceiver = new GocReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GocMessage.BT_OPENED);
		filter.addAction(GocMessage.BT_CLOSED);
		filter.addAction(GocMessage.BT_CONNECTED);
		filter.addAction(GocMessage.BT_DISCONNECTED);
		filter.addAction(GocMessage.CONTACT_SYNC_DONE);
		filter.addAction(GocMessage.CONTACT_DELETE_DONE);
		filter.addAction("com.tchip.ACC_OFF");
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
        	btPair.setEnabled(true);
        	btPair.setBackgroundResource(R.drawable.settings_list_bg);
        	btConnectStatus.setText(Config.BT_PARI_NAME);
        	
        	if(GocMessage.BT_CONNECTED.equals(action)){
        		//蓝牙连接
        		btConnectOperate.setText("断开连接");
        	}else if(GocMessage.BT_DISCONNECTED.equals(action)){
        		//蓝牙断开连接
        		btConnectOperate.setText("开始配对");
        	}else if(GocMessage.CONTACT_SYNC_DONE.equals(action)){
        		if(pdSC != null)
        			pdSC.dismiss();
        	}else if(GocMessage.CONTACT_DELETE_DONE.equals(action)){
        		if(pdCC != null)
        			pdCC.dismiss();
        	}else if("com.tchip.ACC_OFF".equals(action)){
        		BTSettings.this.finish();
        	}else if("com.tchip.ACC_ON".equals(action)){
        		String btStatus = Settings.System.getString(getContentResolver(), "bt_enable");
        		switchButton.setChecked(btStatus.equals("1"));
        		switchButton.setEnabled(true);
        	}else if(GocMessage.BT_OPENED.equals(action)){
        		//打开蓝牙
        		switchButton.setChecked(true);
        	}else if(GocMessage.BT_CLOSED.equals(action)){
        		//关闭蓝牙
        		switchButton.setChecked(false);
        	}
        }
    }
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.bt_settings);  
        
        syncContact = (LinearLayout) findViewById(R.id.sync_contact);
        
        //蓝牙名称
        btName = (EditText) findViewById(R.id.bt_name);
        btName.setText(Config.BT_NAME);
        btName.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				String name = btName.getText().toString();
				if(name == null || name.length() == 0){
					name = Config.BT_NAME;
					btName.setText(name);
				}
				if(!Config.BT_NAME.equals(name)){
					Log.d("goc", "input finish " + Config.BT_NAME + " " + name);
					Config.BT_NAME = name;
					if(isBTConnected()){
						sendBroadcast(new Intent(GocMessage.DISCONNECT_PHONE));
					}
					sendBroadcast(new Intent(GocMessage.SET_BT_NAME));
				}
				return false;
			}
		});
        //蓝牙密钥
        btPinCode = (EditText) findViewById(R.id.bt_pin_code);
        btPinCode.setText(Config.BT_PIN_CODE);
        btPinCode.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				Log.d("goc", "input finish");
				String pin = btPinCode.getText().toString();
				if(pin == null || pin.length() == 0){
					pin = Config.BT_PIN_CODE;
					btPinCode.setText(pin);
				}
				Config.BT_PIN_CODE = pin;
				sendBroadcast(new Intent(GocMessage.SET_BT_PIN_CODE));
				return false;
			}
		});
        //蓝牙连接
    	btPair = (RelativeLayout) findViewById(R.id.bt_pair);
    	btConnectOperate = (TextView) findViewById(R.id.bt_connect_operate);
    	btConnectStatus = (TextView) findViewById(R.id.bt_connect_status);
    	btConnectOperate.setText(isBTConnected() ? "断开连接" : "开始配对");
    	btConnectStatus.setText(Config.BT_PARI_NAME);
    	

        
        //蓝牙开关
        switchButton = (SwitchButton) findViewById(R.id.switchBT);
        String btStatus = Settings.System.getString(getContentResolver(), "bt_enable");
        if(BTFileOperater.getAccStatus()){
        	switchButton.setChecked(btStatus.equals("1"));
        	switchButton.setEnabled(true);
        }else{
        	switchButton.setEnabled(false);
        	btName.setEnabled(false);
        	btPinCode.setEnabled(false);
        	btPair.setEnabled(false);
        	btPair.setClickable(false);
        }
        switchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				String status = isChecked ? "1" : "0";
				Settings.System.putString(getContentResolver(), "bt_enable", status);
				if(!isChecked){
					btPair.setClickable(false);
					syncContact.setClickable(false);
					Settings.System.putString(getContentResolver(), "bt_connect", "0");
					startGocsdkService(OperateCommand.BT_UNFOUND_UNCONNECT);
				}else{
					btPair.setClickable(true);
					syncContact.setClickable(true);
			    	btConnectOperate.setText(isBTConnected() ? "断开连接" : "开始配对");
	        		startGocsdkService(OperateCommand.BT_FOUND_CONNECT);
				}
				//BTFileOperater.writeBTFile(status);
				//startGocsdkService(isChecked ? OperateCommand.OPEN_BT : OperateCommand.CLOSE_BT);
				switchButton.setEnabled(false);
				new Handler().postDelayed(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						switchButton.setEnabled(true);
					}
					
				}, 2000);
			}
		});
        
    	initGocReceiver();
    }  
    
    private boolean isBTConnected(){
    	String status = Settings.System.getString(getContentResolver(), "bt_connect");
    	return status.equals("1");
    }
    
    public void onRestart(){
    	super.onRestart();
    }
    public void onResume(){
    	super.onResume();
    	
    	if(ContactCallLogStatus.contactSyncing())
    		pdSC =  ProgressDialog.show(BTSettings.this, "", "联系人同步中，请稍等。。。", false, true);
    	if(ContactCallLogStatus.contactDeleting())
			pdCC =  ProgressDialog.show(BTSettings.this, "", "正在清空联系人，请稍等。。。", false, true);  
    }
    public void onPause(){
    	super.onPause();
    	unregisterReceiver(gocReceiver);
    	this.finish();
    }
    
    public boolean dispatchKeyEvent(KeyEvent event){
    	switch(event.getKeyCode()){
    	case KeyEvent.KEYCODE_BACK:
    		this.finish();
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
    protected void onDestroy() {   
        super.onDestroy();  
    }  
  
    /**
     * 删除通话记录
     * @param v
     */
    public void clearCallHistory(View v){
    	Log.d("goc", "clearCallHistory");
    	showDialog("请确认要清空通话记录！", CLEAR_CALL_HISTORY);
    }
    /**
     * 清空联系人
     * @param v
     */
    public void clearContact(View v){
    	Log.d("goc", "clearContact");
    	if(ContactCallLogStatus.contactSyncing()){
    		pdSC  =  ProgressDialog.show(BTSettings.this, "", "联系人同步中，请稍等。。。", false, true);
    	}else if(Config.contactDeleting){
			pdCC =  ProgressDialog.show(BTSettings.this, "", "正在清空联系人，请稍等。。。", false, true);  
    	}else{
    		showDialog("请确认要清空通讯录！", CLEAR_CONTACT);
    	}
    }
    /**
     * 同步联系人
     * @param v
     */
    public void syncContact(View v){
    	Log.d("goc", "syncContact");
    	if(BTFileOperater.getAccStatus())
        	if(ContactCallLogStatus.contactDeleting()){
        		pdSC  =  ProgressDialog.show(BTSettings.this, "", "联系人同步中，请稍等。。。", false, true);
        	}else if(Config.contactDeleting){
    			pdCC =  ProgressDialog.show(BTSettings.this, "", "正在清空联系人，请稍等。。。", false, true);  
        	}else{
        		showDialog("请确认要同步通讯录！", SYNC_CONTACT);
        	}
    }

    /**
     * 蓝牙连接断开
     * @param v
     */
    public void btConnect(View v){
    	Log.d("goc", "btConnect");
    	btPair.setEnabled(false);
    	btPair.setBackgroundColor(Color.GRAY);
    	sendBroadcast(new Intent(isBTConnected() ? GocMessage.DISCONNECT_PHONE : GocMessage.CONNECT_PHONE));
    }
    
    private final static int CLEAR_CALL_HISTORY = 0;
    private final static int CLEAR_CONTACT = 1;
    private final static int SYNC_CONTACT = 2;
    /**
     * 显示dialog
     * @param message
     * @param operate
     */
    private void showDialog(String message, final int operate){
    	AlertDialog.Builder builder = new Builder(BTSettings.this);
    	builder.setMessage("\n" + message + "\n\n");
    	builder.setTitle("提示");
    	builder.setPositiveButton("确认", new OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			switch(operate){
    			case CLEAR_CALL_HISTORY:
    				final ProgressDialog pdCCH =  ProgressDialog.show(BTSettings.this, "", "正在清空通话记录，请稍等。。。", false, false); 
    				new Thread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
		    				ContactOperate.clearCallLog(BTSettings.this);
		    				try{
		    					pdCCH.dismiss();
		    				}catch(Exception e){
		    					
		    				}
						}
    					
    				}).start();
    				break;
    			case CLEAR_CONTACT:
    				pdCC =  ProgressDialog.show(BTSettings.this, "", "正在清空联系人，请稍等。。。", false, true);  
    				sendBroadcast(new Intent(GocMessage.DELETE_CONTACT));
    				break;
    			case SYNC_CONTACT:
    				pdSC  =  ProgressDialog.show(BTSettings.this, "", "联系人同步中，请稍等。。。", false, true);
    				sendBroadcast(new Intent(GocMessage.SYNC_CONTACT));
    				break;
    			}
    			dialog.dismiss();
    		}
    	});
    	builder.setNegativeButton("取消", new OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			dialog.dismiss();
    		}
    		});
    	builder.create().show();
    }
    ProgressDialog pdSC, pdCC;

    private void showDialog2(String message){
    	AlertDialog.Builder builder = new Builder(BTSettings.this);
    	builder.setMessage("\n" + message + "\n\n");
    	builder.setTitle("提示");
    	builder.setPositiveButton("确认", new OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			dialog.dismiss();
    		}
    	});
    	builder.create().show();
    }
	
	/**
	 * 启动GocsdkService
	 * @param command
	 */
	private void startGocsdkService(String command){
	    Intent intent = new Intent(BTSettings.this,GocsdkService.class);  
	    if(command != null)
	    	intent.putExtra("command", command);
        startService(intent);
	}
}  