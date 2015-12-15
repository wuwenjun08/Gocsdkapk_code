package com.goodocom.gocsdk.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.goodocom.gocsdk.Commands;
import com.goodocom.gocsdk.Config;
import com.goodocom.gocsdk.IGocsdkCallback;
import com.search.util.Dfine;
import com.tchip.call.CallUI;
import com.tchip.call.MainActivity;
import com.tchip.contact.CallLog;
import com.tchip.contact.Contact;
import com.tchip.contact.ContactCallLogStatus;
import com.tchip.contact.ContactOperate;
import com.tchip.database.ContactDB;
import com.tchip.util.BTFileOperater;
import com.tchip.util.BTStatus;
import com.tchip.util.CrashHandler;
import com.tchip.util.GocMessage;
import com.tchip.util.OperateCommand;
import com.tchip.util.PinyinComparator;
import com.tchip.view.TchipToast;
/**
 * 
 * 蓝牙后台服务
 * @author wwj
 *
 */
public class GocsdkService extends Service {
	private static boolean showToast = false;
	public static final String TAG = "GocsdkService";
	public static final int MSG_START_SERIAL = 1;
	public static final int MSG_SERIAL_RECEIVED = 2;
	private static final int RESTART_DELAY = 2000; // ms
	private CommandParser parser;
	
	private SerialThread serialThread = null;
	private volatile boolean running = true;
	private RemoteCallbackList<IGocsdkCallback> callbacks;
	private GocsdkServiceImp gocSI;
	private TchipToast tchipToast;
	//private AudioManager am;
	/**
	 * 初始化消息
	 */
	private void initGocReceiver(){
		gocReceiver = new GocReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.tchip.ACC_ON");
		filter.addAction("com.tchip.ACC_OFF");
		filter.addAction("com.tchip.BT_VOLUME_MUTE");
		filter.addAction("com.tchip.BT_VOLUME_UNMUTE");
		filter.addAction("com.tchip.DETAIL_PHONE");
		filter.addAction(GocMessage.DELETE_CONTACT);
		filter.addAction(GocMessage.SYNC_CONTACT);
		filter.addAction(GocMessage.SET_BT_NAME);
		filter.addAction(GocMessage.SET_BT_PIN_CODE);
		filter.addAction(GocMessage.CONNECT_PHONE);
		filter.addAction(GocMessage.DISCONNECT_PHONE);
		filter.addAction(GocMessage.DISCOVERY_PHONE);
		//speech语音
		filter.addAction(GocMessage.GET_BLT_STATUS);
		filter.addAction(GocMessage.SPEECH_ACCEPT_CALL);
		registerReceiver(gocReceiver, filter);
	}
	
	private static boolean syncContact = false;
	/**
	 * 接受GocsdkService发的message消息
	 */
	GocReceiver gocReceiver;
    private class GocReceiver extends BroadcastReceiver {    	
        @Override
        public void onReceive(Context context, Intent intent) {
        	String action = intent.getAction();

        	if("com.tchip.ACC_ON".equals(action)){
        		//startSpeak("ACC上电");
        		initBTStatus();
        	}else if("com.tchip.ACC_OFF".equals(action)){
        		//休眠
        		//startSpeak("休眠");
        		try {
					gocSI.GOCSDK_disconnect();
					gocSI.GOCSDK_SetBTUnfound();
					GocsdkService.this.sendBroadcast(new Intent(GocMessage.BT_DISCONNECTED));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(GocsdkService.this, "蓝牙断开手机失败", Toast.LENGTH_SHORT).show();
				}
        		//GocsdkService.this.sendBroadcast(new Intent("com.tchip.SLEEP_ON"));
        		BTFileOperater.writeBTFile("0");
        		GocsdkService.this.stopSelf();
        	}else if("com.tchip.BT_VOLUME_MUTE".equals(action)){
        		//蓝牙音乐播放暂停
				try {
					if(!Config.callUIShow)
						gocSI.GOCSDK_GetVolumeMute();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if("com.tchip.BT_VOLUME_UNMUTE".equals(action)){
				//蓝牙音乐强制停止
				try {
					if(!Config.callUIShow)
						gocSI.GOCSDK_GetVolumeUnmute();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}else if(GocMessage.SYNC_CONTACT.equals(action)){
        		//同步联系人
    			ContactCallLogStatus.ContactStatus = ContactCallLogStatus.contactSyncing;
    			syncContact = true;
        		try {
        			if(Config.btMusic){
        				gocSI.GOCSDK_musicStop();	//停止播放音乐
        				Config.tqBtMusic = true;
        			}
					gocSI.GOCSDK_phoneBookStartUpdate();
        			simContactCount = 0;
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
        			ContactCallLogStatus.ContactStatus = ContactCallLogStatus.contactSyncFailed;
        			syncContact = false;
					Toast.makeText(GocsdkService.this, "联系人同步失败", Toast.LENGTH_SHORT).show();
					reStartBtMusic();
				}
        	}else if(GocMessage.DELETE_CONTACT.equals(action)){
        		//删除联系人
    			ContactCallLogStatus.ContactStatus = ContactCallLogStatus.contactDeleting;
        		try {
        			clearContactDB(GocsdkService.this);
        			Dfine.user.clear();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	    			ContactCallLogStatus.ContactStatus = ContactCallLogStatus.contactDeleteFailed;
					Toast.makeText(GocsdkService.this, "删除联系人失败", Toast.LENGTH_SHORT).show();
				}
        		context.sendBroadcast(new Intent(GocMessage.CONTACT_DELETE_DONE));
        	}else if(GocMessage.SET_BT_NAME.equals(action)){
        		//设置蓝牙名称
        		try {
					gocSI.GOCSDK_setLocalName(Config.BT_NAME);
					editor.putString("name", Config.BT_NAME);
					editor.commit();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(GocsdkService.this, "设置蓝牙名称失败", Toast.LENGTH_SHORT).show();
				}
        	}else if(GocMessage.SET_BT_PIN_CODE.equals(action)){
        		//设置蓝牙配对密钥
        		try {
					gocSI.GOCSDK_setPinCode(Config.BT_PIN_CODE);
					editor.putString("pin", Config.BT_PIN_CODE);
					editor.commit();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(GocsdkService.this, "设置蓝牙配对密钥失败", Toast.LENGTH_SHORT).show();
				}
        	}else if(GocMessage.CONNECT_PHONE.equals(action)){
        		//蓝牙连接手机
        		try {
					gocSI.GOCSDK_connectLast();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(GocsdkService.this, "蓝牙连接手机失败", Toast.LENGTH_SHORT).show();
				}
        	}else if(GocMessage.DISCONNECT_PHONE.equals(action)){
        		//蓝牙断开手机
        		try {
					gocSI.GOCSDK_disconnect();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(GocsdkService.this, "蓝牙断开手机失败", Toast.LENGTH_SHORT).show();
				}
        	}else if("com.tchip.DETAIL_PHONE".equals(action)){
        		String number = intent.getStringExtra("number");
    			if(number != null && number.length() > 0){
					try {
						gocSI.GOCSDK_phoneDail(number);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
        	}else if(GocMessage.DISCOVERY_PHONE.equals(action)){
				try {
					gocSI.GOCSDK_stopDiscovery();
					gocSI.GOCSDK_startDiscovery();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("goc1", "onDiscoveryStart" + System.currentTimeMillis());
        	}
        	//speech语音
        	else if(GocMessage.GET_BLT_STATUS.equals(action)){
        		if(Config.BT_PARI_NAME.length() > 0)
        		GocsdkService.this.sendBroadcast(new Intent(GocMessage.BT_CONNECTED));
        	}
        	else if(GocMessage.SPEECH_ACCEPT_CALL.equals(action)){
				try {
					gocSI.GOCSDK_phoneAnswer();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
    }
    public GocsdkService(){
    	
    }
    
	private SharedPreferences preferences;
	private Editor editor;
    
	@Override
	public void onCreate() {
		Log.d("wwj_test", "gocsdk service oncreate...........");
		Settings.System.putString(getContentResolver(), "bt_connect", "0");		
		preferences = getSharedPreferences("bt_status", Context.MODE_PRIVATE);
		editor = preferences.edit();			
		Config.BT_NAME = preferences.getString("name", "TianQi_BT");
		Config.BT_PIN_CODE = preferences.getString("pin", "0000");

		if(BTFileOperater.getAccStatus()){	
			//am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			gocSI = new GocsdkServiceImp(this);
			callbacks = new RemoteCallbackList<IGocsdkCallback>();
			parser = new CommandParser(callbacks);
			handler.sendEmptyMessage(MSG_START_SERIAL);
			tchipToast = new TchipToast(this);
			registerCallback();
			initGocReceiver();

    		//startSpeak("蓝牙启动");
			initBTStatus();		
		}else{
    		//startSpeak("蓝牙启动2");
    		GocsdkService.this.stopSelf();
		}
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		running = false;
		if(callbacks != null){
			callbacks.kill();
			unregisterCallback();
		}
		if(gocReceiver != null)
			unregisterReceiver(gocReceiver);
		try{
			if(gocSI != null)
				gocSI.GOCSDK_CloseBt();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return gocSI;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		if(!ContactCallLogStatus.contactShow()){
			//同步联系人时蓝牙不进行其他操作
			Toast.makeText(GocsdkService.this, "正在同步联系人", Toast.LENGTH_SHORT).show();
			return super.onStartCommand(intent, flags, startId);
		}
		if(BTFileOperater.getAccStatus()){		
			String command = intent.getStringExtra("command");
			Log.d("wwj_test", "gocsdk service onStartCommand..........." + command);
			try {
				if(command != null){
					Log.d("goc", command);
					if(command.startsWith(OperateCommand.CALL_DEAIL)){
						//拨号
						gocSI.GOCSDK_phoneDail(command.substring(2));
					}else if(command.startsWith(OperateCommand.CALL_HANG_UP)){
						//挂断电话
						gocSI.GOCSDK_phoneHangUp();
					}else if(command.startsWith(OperateCommand.CALL_ANSWER)){
						//接听来电
						gocSI.GOCSDK_phoneAnswer();
					}else if(command.startsWith(OperateCommand.CALL_DTMF_CODE)){
						//通话按键
						gocSI.GOCSDK_phoneTransmitDTMFCode('1');
					}else if(command.startsWith(OperateCommand.OPEN_BT)){
						//打开蓝牙
						gocSI.GOCSDK_OpenBt();
						GocsdkService.this.sendBroadcast(new Intent(GocMessage.BT_OPENED));
					}else if(command.startsWith(OperateCommand.CLOSE_BT)){
						//关闭蓝牙
						gocSI.GOCSDK_CloseBt();
						GocsdkService.this.sendBroadcast(new Intent(GocMessage.BT_CLOSED));
					}else if(command.startsWith(OperateCommand.VOICE_TRANSFER_BT)){
						//声音切换到蓝牙
						gocSI.GOCSDK_phoneTransferBack();
					}else if(command.startsWith(OperateCommand.VOICE_TRANSFER_PHONE)){
						//声音切换到手机
						gocSI.GOCSDK_phoneTransfer();
					}else if(command.startsWith(OperateCommand.SET_VOICE_UP)){
						//设置声音
						gocSI.GOCSDK_VolumeUp();
						tchipToast.show(Config.currentVolume);
					}else if(command.startsWith(OperateCommand.SET_VOICE_DOWN)){
						//设置声音
						gocSI.GOCSDK_VolumeDown();
						tchipToast.show( Config.currentVolume);
					}else if(command.startsWith(OperateCommand.BT_DISCOVERY)){
						//设置声音
						gocSI.GOCSDK_stopDiscovery();
						gocSI.GOCSDK_startDiscovery();
						Log.d("goc1", "onDiscoveryStart" + System.currentTimeMillis());
					}else if(command.startsWith(OperateCommand.BT_MUSIC_MUTE)){
						//蓝牙音乐播放暂停
						gocSI.GOCSDK_GetVolumeMute();
					}else if(command.startsWith(OperateCommand.BT_MUSIC_UNMUTE)){
						//蓝牙音乐强制停止
						gocSI.GOCSDK_GetVolumeUnmute();
					}else if(command.startsWith(OperateCommand.BT_FOUND_CONNECT)){
						//蓝牙被发现被连接
						gocSI.GOCSDK_SetBTFound();
						//连接蓝牙
						new Handler().postDelayed(new Runnable(){						
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									gocSI.GOCSDK_connectLast();
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
							
						}, 1000);
					}else if(command.startsWith(OperateCommand.BT_UNFOUND_UNCONNECT)){
						//蓝牙不被发现不被连接
						gocSI.GOCSDK_SetBTUnfound();
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}


	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MSG_START_SERIAL) {
				serialThread = new SerialThread();
				serialThread.start();
			} else if (msg.what == MSG_SERIAL_RECEIVED) {
				byte[] data = (byte[]) msg.obj;
				parser.onBytes(data);
			}
		};
	};

	class SerialThread extends Thread {
		private LocalSocket client;
		private LocalSocketAddress address;
		private InputStream inputStream;
		private OutputStream outputStream = null;
		private byte[] buffer = new byte[1024];

		public void write(byte[] buf) {
			if (outputStream != null) {
				try {
					outputStream.write(buf);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public SerialThread() {
			client = new LocalSocket();
			address = new LocalSocketAddress(Config.SERIAL_SOCKET_NAME,
					LocalSocketAddress.Namespace.RESERVED);
		}

		@Override
		public void run() {
			int n;
			try {
				client.connect(address);
				inputStream = client.getInputStream();
				outputStream = client.getOutputStream();
				while (running) {
					n = inputStream.read(buffer);
					if (n < 0) throw new IOException("n==-1");
					byte[] data = new byte[n];
					System.arraycopy(buffer, 0, data, 0, n);
					handler.sendMessage(handler.obtainMessage(
							MSG_SERIAL_RECEIVED, data));
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					client.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				handler.sendEmptyMessageDelayed(MSG_START_SERIAL, RESTART_DELAY);
				return;
			}
		}
	}

	public void write(String str) {
		if (serialThread == null) return;
		serialThread.write((Commands.COMMAND_HEAD + str + "\r\n").getBytes());
	}

	public void registerCallback() {
		Log.d("goc", "GocsdkService registerCallback");
		callbacks.register(mCallback);
	}

	public void unregisterCallback() {
		Log.d("goc", "GocsdkService unregisterCallback");
		callbacks.unregister(mCallback);
	}
	
	
	/**
	 * 获取蓝牙状态
	 */
	private void initBTStatus(){
		String btStatus = Settings.System.getString(getContentResolver(), "bt_enable");
		Log.d("goc", "btStatus : " + btStatus);
		if(!BTFileOperater.getAccStatus())
			btStatus = "0";
		
		BTFileOperater.writeBTFile(btStatus);
		
		try{
			//查询蓝牙名称
			gocSI.GOCSDK_getLocalName();
			//上电蓝牙自动连接
			gocSI.GOCSDK_SetAutoConnect('1');
			if(btStatus.equals("1")){
				gocSI.GOCSDK_OpenBt();
				//Toast.makeText(GocsdkService.this, "蓝牙服务启动。。。。", Toast.LENGTH_LONG).show();
				new Handler().postDelayed(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						String status = Settings.System.getString(getContentResolver(), "bt_enable");
						try {
							if(status.equals("1"))
								gocSI.GOCSDK_SetBTFound();	//设置为发现模式
							else
								gocSI.GOCSDK_SetBTUnfound();	//设置为不被发现模式

							gocSI.GOCSDK_GetVolume();	//获取音量
							gocSI.GOCSDK_InquiryCurBtName();	//获取连接蓝牙名称
							gocSI.GOCSDK_InquiryCurBtAddr();	//获取连接蓝牙地址
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}						
					}
					
				}, 5000);
				//检测蓝牙是否连接
				if(Settings.System.getString(getContentResolver(), "bt_connect").equals("1")){
					BTFileOperater.openSpeaker();
				}else{
					BTFileOperater.closeSpeaker();
				}
//				new Handler().postDelayed(new Runnable(){
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						try {
//							//Toast.makeText(GocsdkService.this, "正在连接蓝牙", Toast.LENGTH_LONG).show();
//							gocSI.GOCSDK_connectLast();
//						} catch (RemoteException e) {
//							// TODO Auto-generated catch block
//							//Toast.makeText(GocsdkService.this, "蓝牙连接手机失败", Toast.LENGTH_SHORT).show();
//							e.printStackTrace();
//						}
//					}
//					
//				}, 10000);
			}else{
				gocSI.GOCSDK_CloseBt();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 蓝牙服务接口返回
	 */
	private IGocsdkCallback.Stub mCallback = new IGocsdkCallback.Stub() {

		@Override
		public void onHfpDisconnected() throws RemoteException {
			// TODO Auto-generated method stub
			//蓝牙已断开
			if(Settings.System.getString(getContentResolver(), "bt_connect").equals("1")){
				if(!BTStatus.btErrorReconnected){
					startSpeak("蓝牙已断开");
				}
			}
			Config.BT_PARI_NAME="未连接";
			Settings.System.putString(getContentResolver(), "bt_connect", "0");
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.BT_DISCONNECTED));
			
			gocSI.GOCSDK_MicSwitch((char)0);
			//关闭喇叭
			BTFileOperater.closeSpeaker();
			
			if(BTStatus.btErrorReconnected){
				gocSI.GOCSDK_connectLast();
				BTStatus.btErrorReconnected = false;
			}
		}

		@Override
		public void onHfpConnected() throws RemoteException {
			// TODO Auto-generated method stub
			//蓝牙已连接
			startSpeak("蓝牙已连接");
			Settings.System.putString(getContentResolver(), "bt_connect", "1");
			if(showToast)
				Toast.makeText(GocsdkService.this, "onHfpConnected", Toast.LENGTH_SHORT).show();
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.BT_CONNECTED));

			//打开喇叭
			BTFileOperater.openSpeaker();
			gocSI.GOCSDK_InquiryA2dpStatus();
		}

		@Override
		public void onCallSucceed(String number) throws RemoteException {
			// TODO Auto-generated method stub
			if(showToast)
				Toast.makeText(GocsdkService.this, "onCallSucceed", Toast.LENGTH_SHORT).show();

            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
			Config.cl.setType(Config.OUTGOING_TYPE);
			Config.cl.setTime(sfd.format(date));
			Config.cl.setNumber(number);
			Config.cl.setName(getContactName(number));
					
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.CALL_SUCCESS));

		}

		@Override
		public void onIncoming(String number) throws RemoteException {
			// TODO Auto-generated method stub
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
			Config.cl.setTime(sfd.format(date));
			Config.cl.setNumber(number);
			Config.cl.setType(Config.INCOMING_TYPE);
			Config.cl.setName(getContactName(number));
			
			Bundle bundle = new Bundle();
			bundle.putString("name", Config.cl.getName());
			bundle.putString("number", number);
			
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.CALL_INCOMING).putExtras(bundle));
			//启动电话界面
			Intent calUIIntent = new Intent(GocsdkService.this, CallUI.class);
			calUIIntent.putExtra("incoming", true);
			calUIIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			GocsdkService.this.startActivity(calUIIntent);
		}

		@Override
		public void onHangUp() throws RemoteException {
			// TODO Auto-generated method stub
	    	Config.callUIShow = false;
			if(showToast)
				Toast.makeText(GocsdkService.this, "onHangUp", Toast.LENGTH_SHORT).show();
			try {
				gocSI.GOCSDK_MicSwitch((char)0);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.CALL_HANG_UP));
		}

		@Override
		public void onTalking() throws RemoteException {
			// TODO Auto-generated method stub
			if(showToast)
				Toast.makeText(GocsdkService.this, "onTalking", Toast.LENGTH_SHORT).show();
			try {
				gocSI.GOCSDK_MicSwitch((char)0x01);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.CALL_ONTALKING));
		}

		@Override
		public void onRingStart() throws RemoteException {
			// TODO Auto-generated method stub
			if(showToast)
				Toast.makeText(GocsdkService.this, "onRingStart", Toast.LENGTH_SHORT).show();
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.CALL_RING_START));
		}

		@Override
		public void onRingStop() throws RemoteException {
			// TODO Auto-generated method stub
			if(showToast)
				Toast.makeText(GocsdkService.this, "onRingStop", Toast.LENGTH_SHORT).show();
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.CALL_RING_STOP));
		}

		@Override
		public void onHfpLocal() throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("wwj_bt", "onHfpLocal.....");
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.PHONE_TRANSFER_TO_MOBILE));
		}

		@Override
		public void onHfpRemote() throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("wwj_bt", "onHfpRemote.....");
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.PHONE_TRANSFER_TO_BT));
		}

		@Override
		public void onInPairMode() throws RemoteException {
			// TODO Auto-generated method stub
			if(showToast)
				Toast.makeText(GocsdkService.this, "onInPairMode", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onExitPairMode() throws RemoteException {
			// TODO Auto-generated method stub
			if(showToast)
				Toast.makeText(GocsdkService.this, "onExitPairMode", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onOutGoingOrTalkingNumber(String number)
				throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("wwj_test", "onOutGoingOrTalkingNumber......................" + number);
		}

		@Override
		public void onInitSucceed() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnecting() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMusicPlaying() throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("wwj_test", "onMusicPlaying......................");
			//if(am == null)
			//	am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			//am.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
			//am.setStreamMute(AudioManager.STREAM_SYSTEM, true);
			//am.setStreamMute(AudioManager.STREAM_DTMF, true);
			Settings.System.putString(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, "0");
			Settings.System.putInt(getContentResolver(), Settings.System.DTMF_TONE_WHEN_DIALING, 0);
			GocsdkService.this.sendBroadcast(new Intent("com.tchip.BT_MUSIC_PLAYING"));
			Config.btMusic = true;
		}

		@Override
		public void onMusicStopped() throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("wwj_test", "onMusicStopped......................");
			//if(am == null)
			//	am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			//am.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
			//am.setStreamMute(AudioManager.STREAM_SYSTEM, false);
			//am.setStreamMute(AudioManager.STREAM_DTMF, false);
			Settings.System.putString(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, "1");
			Settings.System.putInt(getContentResolver(), Settings.System.DTMF_TONE_WHEN_DIALING, 1);
			GocsdkService.this.sendBroadcast(new Intent("com.tchip.BT_MUSIC_STOPED"));
			Config.btMusic = false;
		}

		@Override
		public void onAutoConnectAccept(boolean autoConnect, boolean autoAccept)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCurrentAddr(String addr) throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("wwj_test", "onCurrentAddr : " + addr);
			Config.BT_PARI_MAC = addr;
			ContactOperate.getContact(GocsdkService.this);
			ContactOperate.getCallHistory(GocsdkService.this);

			//获取连接蓝牙的mac地址后，读取联系人，发送联系人已读取的广播
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.CONTACT_READED));
		}

		@Override
		public void onCurrentName(String name) throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("wwj_test", "onCurrentName : " + name);
			Settings.System.putString(getContentResolver(), "bt_connect", "1");
			Config.BT_PARI_NAME = name;
			GocsdkService.this.sendBroadcast(new Intent(GocMessage.BT_CONNECTED_NAME));
		}

		@Override
		public void onHfpStatus(int status) throws RemoteException {
			// TODO Auto-generated method stub
		}

		@Override
		public void onAvStatus(int status) throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("wwj_bt", "btconnected : " + Settings.System.getString(getContentResolver(), "bt_connect"));
			Log.d("wwj_bt", "onAvStatus : " + status);
			
			boolean isConnected = Settings.System.getString(getContentResolver(), "bt_connect").equals("1");
			if(isConnected){
				if(status == 1){
					startSpeak("蓝牙媒体音频连接错误，重新连接");
					BTStatus.btErrorReconnected = true;
					gocSI.GOCSDK_disconnect();
				}else{
					BTStatus.btErrorReconnected = false;
				}
			}
		}

		@Override
		public void onVersionDate(String version) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCurrentDeviceName(String name) throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("wwj_test", "name : " + name);
			Config.BT_NAME = name;
		}

		@Override
		public void onCurrentPinCode(String code) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onA2dpConnected() throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("wwj_bt", "onA2dpConnected");
		}

		@Override
		public void onCurrentAndPairList(int index, String name, String addr)
				throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onA2dpDisconnected() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPhoneBook(final String name, final String number)
				throws RemoteException {
			// TODO Auto-generated method stub
			// 手机设备联系人
			if(name == null || number == null)
				return;
			if(name.length() ==0 || number.length() == 0)
				return;
			if(!isContactExit(name, number)){
				Contact con = new Contact();
				con.setName(name);
				con.setPhone(number);
				ContactOperate.contactList.add(con);
				addContact(GocsdkService.this, name, number);
				
				//读取到一个新的联系人，发送广播
				GocsdkService.this.sendBroadcast(new Intent(GocMessage.CONTACT_RECEIVER));
			}
		}

		@Override
		public void onSimBook(final String name, final String number)
				throws RemoteException {
			// TODO Auto-generated method stub
			// 手机SIM联系人
			if(name == null || number == null)
				return;
			if(name.length() ==0 || number.length() == 0)
				return;
			if(!isContactExit(name, number)){
				simContactCount ++;
				Contact con = new Contact();
				con.setName(name);
				con.setPhone(number);
				ContactOperate.contactList.add(con);
				addContact(GocsdkService.this, name, number);

				//读取到一个新的联系人，发送广播
				GocsdkService.this.sendBroadcast(new Intent(GocMessage.CONTACT_RECEIVER));
			}
		}

		@Override
		public void onPhoneBookDone() throws RemoteException {
			// TODO Auto-generated method stub
			//手机设备联系人同步完毕
			Log.d("goc1", "onPhoneBookDone");
			if(syncContact){
				createNewContactDB(GocsdkService.this);
				syncContact = false;
			}
		}

		@Override
		public void onSimDone() throws RemoteException {
			// TODO Auto-generated method stub
			//手机SIM联系人同步完毕
				
				//GocsdkService.this.sendBroadcast(new Intent(GocMessage.CONTACT_SYNC_DONE));
				if(simContactCount > 0){
			        new Handler().postDelayed(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							createNewContactDB(GocsdkService.this);
							Toast.makeText(GocsdkService.this, simContactCount + "个sim卡联系人同步成功", Toast.LENGTH_LONG).show();
					        Config.contactChanged = true;
					        Config.contactSyncing = false;
						}

			        }, 500);
				}
		}

		@Override
		public void onCalllogDone() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCalllog(int type, String number) throws RemoteException {
			// TODO Auto-generated method stub
			if(showToast)
				Toast.makeText(GocsdkService.this, type + " : " + number, Toast.LENGTH_SHORT).show();
			Log.d("goc", type + " : " + number);
		}

		@Override
		public void onDiscovery(String name, String addr) throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("goc1", name + " : " + addr);
			Bundle bundle = new Bundle();
			bundle.putString("name", name);
			bundle.putString("addr", addr);
			sendBroadcast(new Intent(GocMessage.DISCOVERY_SUCCESSED).putExtras(bundle));
		}

		@Override
		public void onDiscoveryDone() throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("goc1", "onDiscoveryDone" + System.currentTimeMillis());
			sendBroadcast(new Intent(GocMessage.DISCOVERY_DONE));
		}

		@Override
		public void onLocalAddress(String addr) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSppData(int index, String data) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSppConnect(int index) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSppDisconnect(int index) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSppStatus(int status) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onOppReceivedFile(String path) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onOppPushSuccess() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onOppPushFailed() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onHidConnected() throws RemoteException {
			// TODO Auto-generated method stub
			if(showToast)
				Toast.makeText(GocsdkService.this, "onHidConnected", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onHidDisconnected() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onHidStatus(int status) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMusicInfo(String MusicName, String artist)
				throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPanConnect() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPanDisconnect() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPanStatus(int status) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBTVolume(int volume) throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("goc", "volume " + volume);
			//Toast.makeText(GocsdkService.this, "" + volume, Toast.LENGTH_SHORT).show();
			Config.currentVolume = volume;
		}
	};

	/**
	 * 本地联系人数据库
	 * 根据号码获取联系人姓名
	 * @param number
	 * @return
	 */
	public String getContactName(String number){
		ContactDB cDB = new ContactDB(GocsdkService.this, Config.BT_PARI_MAC);
		Cursor cursor = cDB.query();
		if(cursor != null && cursor.getCount() != 0){
			for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
				String phone = cursor.getString(cursor.getColumnIndex(ContactDB.NUMBER));
				if(phone != null && phone.equals(number)){
					String name = cursor.getString(cursor.getColumnIndex(ContactDB.NAME));
					cursor.close();
					return name;
				}
			}
			cursor.close();
		}
		return "未知号码";
	}
	
	private int simContactCount = 0;
	/**
	 * 判断本地联系人是否存在
	 * @param number
	 * @param phoneNo
	 * @return
	 */
	private boolean isContactExit(String name, String number){
		for(Contact con : ContactOperate.contactList){
			if(name.equals(con.getName()) && number.equals(con.getPhone())){
				return true;
			}
		}
		return false;
	}
	

	private void startSpeak(String msg) {
		/*Intent speakIntent = new Intent("com.tchip.SPEAK_SERVICE");
		speakIntent.putExtra("content", msg);
		speakIntent.putExtra("return", false);
		startService(speakIntent);*/

		try{
			Intent intent = new Intent();
			ComponentName comp = new ComponentName("com.tchip.carlauncher", "com.tchip.carlauncher.service.SpeakService");
			intent.setComponent(comp);
			intent.putExtra("content", msg);
			startService(intent);
		}catch(Exception e){
			
		}
	}

	
	/**
	 * 同步联系人，排序后，重新插入数据库
	 * @param context
	 */
	public void createNewContactDB(Context context){
		 UpdateTextTask updateTextTask = new UpdateTextTask(GocsdkService.this);
		 updateTextTask.execute();  
	}

	
	/**
	 * 系统联系人数据库
	 * 清除通讯录
	 * @param context
	 */
	public void clearContact(Context context){
		ContentResolver cr = context.getContentResolver();
	    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
	    int i = 0;
	    while (cur.moveToNext()) {
  		  	Log.d("goc", "i " + i++);
	        try{
	            String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
	  		  	Log.d("goc", "lookupKey " + lookupKey);
	            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
	  		  	Log.d("goc", "The uri is " + uri.toString());
	            System.out.println("The uri is " + uri.toString());
	            cr.delete(uri, null, null);
	        }
		    catch(Exception e)
		    {
	  		  	Log.d("goc", "e.getStackTrace() " + e.getStackTrace());
		    }
		}		
	    cur.close();
        //Toast.makeText(context, "通讯录已清除", Toast.LENGTH_SHORT).show();
		Log.d("goc", "通讯录已清除");
        Config.contactChanged = true;
	}
	
	/**
	 * 本地联系人数据库
	 * 清除通讯录
	 * @param context
	 */
	public void clearContactDB(Context context){
		ContactDB cDB = new ContactDB(context, Config.BT_PARI_MAC);
		cDB.clearDB();
		Log.d("goc", "通讯录已清除");
		ContactOperate.contactList.clear();
		ContactCallLogStatus.ContactStatus = ContactCallLogStatus.contactDeleteSuccessed;
	}
	
	/**
	 * 系统联系人数据库
	 * 新增加联系人
	 * @param name
	 * @param phoneNum
	 */
	public void addContact(final Context context, final String name, final String phoneNum) {
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
			     /* 往 raw_contacts 中添加数据，并获取添加的id号*/  
				//先判断系统数据库联系人是否存在
				if(!contactIsExit(name, phoneNum)){
			        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");  
			        ContentResolver resolver = context.getContentResolver();
			        ContentValues values = new ContentValues();  
			        long contactId = ContentUris.parseId(resolver.insert(uri, values));  
			          
			        /* 往 data 中添加数据（要根据前面获取的id号） */  
			        // 添加姓名  
			        uri = Uri.parse("content://com.android.contacts/data");  
			        values.put("raw_contact_id", contactId);  
			        values.put("mimetype", "vnd.android.cursor.item/name");  
			        values.put("data2", name);  
			        resolver.insert(uri, values);  
			          
			        // 添加电话  
			        values.clear();  
			        values.put("raw_contact_id", contactId);  
			        values.put("mimetype", "vnd.android.cursor.item/phone_v2");  
			        values.put("data2", "2");  
			        values.put("data1", phoneNum);  
			        resolver.insert(uri, values);  
				}
			}			
		}).start();
	}
    
	/**
	 * 判断系统联系人是否存在
	 * @param name
	 * @param number
	 * @return
	 */
    public boolean contactIsExit(String name, String number){
    	try{
	        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + number);
	        ContentResolver resolver = getContentResolver();
	        Cursor cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
			for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
	            if(name.equals(cursor.getString(0))){
	    	        cursor.close();
	            	return true;
	            }
	        }
	        cursor.close();
    	}catch(Exception e){
    		
    	}
    	return false;
    }
	
	class UpdateTextTask extends AsyncTask<Void,Integer,Integer>{  
        private Context context;  
        UpdateTextTask(Context context) {  
            this.context = context;  
        }  
  
        /** 
         * 运行在UI线程中，在调用doInBackground()之前执行 
         */  
        @Override  
        protected void onPreExecute() {  
        	
        }  
        /** 
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法 
         */  
        @Override  
        protected Integer doInBackground(Void... params) {  
    		Collections.sort(ContactOperate.contactList, new PinyinComparator()); 
    		
    		ContactDB cDB = new ContactDB(context, Config.BT_PARI_MAC);
    		cDB.clearDB();
    		cDB.inset(ContactOperate.contactList);

			ContactCallLogStatus.ContactStatus = ContactCallLogStatus.contactSyncSuccessed;
    		ContactOperate.getContact(GocsdkService.this);
            return null;  
        }  
  
        /** 
         * 运行在ui线程中，在doInBackground()执行完毕后执行 
         */  
        @Override  
        protected void onPostExecute(Integer integer) {  
            int count = 0;
            if(ContactOperate.contactList != null)
            	count = ContactOperate.contactList.size();
    		Toast.makeText(GocsdkService.this, count + "个手机联系人同步成功", Toast.LENGTH_LONG).show();
    		sendBroadcast(new Intent(GocMessage.CONTACT_SYNC_DONE));
    		reStartBtMusic();
        }  
  
        /** 
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度 
         */  
        @Override  
        protected void onProgressUpdate(Integer... values) {  
        	Log.d("wwj_test", "values : " + values[0]);
        }  
    }  
	
	/**
	 * 联系人同步成功，继续播放音乐
	 */
	private void reStartBtMusic(){
		if(Config.tqBtMusic){
			try {
				gocSI.GOCSDK_musicPlayOrPause();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Config.tqBtMusic = false;
		}
	}
}
