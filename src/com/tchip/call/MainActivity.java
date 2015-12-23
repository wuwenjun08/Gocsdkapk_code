package com.tchip.call;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.goodocom.gocsdk.Config;
import com.goodocom.gocsdk.IGocsdkCallback;
import com.goodocom.gocsdk.IGocsdkService;
import com.goodocom.gocsdk.R;
import com.goodocom.gocsdk.service.GocsdkService;
import com.search.adapter.CallLogListAdapter;
import com.search.adapter.ContactListAdapter;
import com.search.adapter.SearchListAdapter;
import com.search.util.Dfine;
import com.search.util.Util;
import com.tchip.btsettings.BTSettings;
import com.tchip.contact.CallLog;
import com.tchip.contact.Contact;
import com.tchip.contact.ContactCallLogStatus;
import com.tchip.contact.MyService;
import com.tchip.contact.ContactOperate;
import com.tchip.indexablelistview.IndexableListView;
import com.tchip.indexablelistview.StringMatcher;
import com.tchip.util.GocMessage;
import com.tchip.util.HanyuToPingyin;
import com.tchip.util.OperateCommand;
import com.tchip.util.PinyinComparator;
import com.tchip.util.SortChineseName;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.provider.Settings;

public class MainActivity extends Activity implements OnClickListener{
	private static String TAG = "MainActivity";
	TextView tv;
	ImageButton keypadOne, keypadTwo, keypadThree, keypadFour, keypadFive, keypadSix, keypadSeven, keypadEight,
				keypadNine, keypadZero, keypadPound, keypadStar, keypadDelete, keypadCall;
	IndexableListView lv;
	ListView lvSearch, lvCallHistory;
	TextView emptyContactTv, listHeader, listCHHeader;
	//ImageButton mToggleButton;
	ImageButton callLogBtn, contactBtn;
	private static boolean isChecked = true;
	
	private ImageView btStatusPic;
	private TextView btStatusOpen;
	
    private static final int DTMF_DURATION_MS = 120; // 声音的播放时间
    private Object mToneGeneratorLock = new Object(); // 监视器对象锁
    private ToneGenerator mToneGenerator;             // 声音产生器
    private static boolean mDTMFToneEnabled;         // 系统参数“按键操作音”标志位
    
    private AudioManager am;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		//按键声音播放设置及初始化
        try {
            // 获取系统参数“按键操作音”是否开启
            mDTMFToneEnabled = Settings.System.getInt(getContentResolver(),
                    Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;
            if(!mDTMFToneEnabled){
            	Settings.System.putInt(getContentResolver(), Settings.System.DTMF_TONE_WHEN_DIALING, 1);
            	mDTMFToneEnabled = true;
            }
            synchronized (mToneGeneratorLock) {
                if (mDTMFToneEnabled && mToneGenerator == null) {
                    mToneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 100); // 设置声音的大小
                    setVolumeControlStream(AudioManager.STREAM_DTMF);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            mDTMFToneEnabled = false;
            mToneGenerator = null;
        }
        
        //初始化界面
        initUI();

		startGocsdkService(null);
	}
	
	public void onResume(){
		super.onResume();		
		initGocReceiver();
		
		//显示通话记录
		//if(Config.callLogChanged)
		{
			showCallLogList();
		}
		//显示联系人
		Log.d("wwj_bt","contactShow : " + ContactCallLogStatus.contactShow());
		showContactList(ContactCallLogStatus.contactShow());

		//初始化蓝牙状态，显示or不显示联系人，通话记录
		initBTStatus();
		isChecked = true;
		lvSearch.setVisibility(View.GONE);
		
		
		//当在通话状态是，进入通话界面
		if(Config.callUIShow){
			Intent intent = new Intent(this, CallUI.class);
			startActivity(intent);
		}
	}
	
	public void onPause(){
		super.onPause();
		//号码输入框制空
		tv.setText("");		
		if(gocReceiver != null)
			unregisterReceiver(gocReceiver);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.d("wwj_test","横屏");
		} else {
			Log.d("wwj_test","竖屏");
		}

	}
	/**
	 * 初始化界面
	 */
	private void initUI(){        
        lv = (IndexableListView) findViewById(R.id.lv);  
        lvSearch = (ListView) findViewById(R.id.lv_search);  
        lvCallHistory = (ListView) findViewById(R.id.lv_call_history);
		tv = (TextView)findViewById(R.id.tv);
		emptyContactTv = (TextView) findViewById(R.id.empty_contact);
		
		emptyContactTv.setVisibility(View.GONE);
		lvSearch.setVisibility(View.GONE);
		lv.setEmptyView(emptyContactTv);
		lv.setFastScrollEnabled(true);

		btStatusPic = (ImageView) findViewById(R.id.bt_status_pic);
		btStatusOpen = (TextView) findViewById(R.id.bt_status_open);
		
		keypadOne = (ImageButton)findViewById(R.id.keypad_one);
		keypadTwo = (ImageButton)findViewById(R.id.keypad_two);
		keypadThree = (ImageButton)findViewById(R.id.keypad_three);
		keypadFour = (ImageButton)findViewById(R.id.keypad_four);
		keypadFive = (ImageButton)findViewById(R.id.keypad_five);
		keypadSix = (ImageButton)findViewById(R.id.keypad_six);
		keypadSeven = (ImageButton)findViewById(R.id.keypad_seven);
		keypadEight = (ImageButton)findViewById(R.id.keypad_eight);
		keypadNine = (ImageButton)findViewById(R.id.keypad_nine);
		keypadStar = (ImageButton)findViewById(R.id.keypad_star);
		keypadZero = (ImageButton)findViewById(R.id.keypad_zero);
		keypadPound = (ImageButton)findViewById(R.id.keypad_pound);		
		keypadDelete = (ImageButton)findViewById(R.id.keypad_del);	
		keypadCall = (ImageButton)findViewById(R.id.keypad_call);
		keypadOne.setOnClickListener(this);
		keypadTwo.setOnClickListener(this);
		keypadThree.setOnClickListener(this);
		keypadFour.setOnClickListener(this);
		keypadFive.setOnClickListener(this);
		keypadSix.setOnClickListener(this);
		keypadSeven.setOnClickListener(this);
		keypadEight.setOnClickListener(this);
		keypadNine.setOnClickListener(this);
		keypadStar.setOnClickListener(this);
		keypadZero.setOnClickListener(this);
		keypadPound.setOnClickListener(this);		
		keypadDelete.setOnClickListener(this);	
		keypadCall.setOnClickListener(this);

		//设置长按删除键，触发删除全部
		keypadDelete.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				tv.setText("");
				return false;
			}
		});
		
		callLogBtn = (ImageButton) findViewById(R.id.show_call_log);
		contactBtn = (ImageButton) findViewById(R.id.show_contact);
		callLogBtn.setEnabled(false);
		contactBtn.setEnabled(true);
		callLogBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callLogBtn.setEnabled(false);
				contactBtn.setEnabled(true);
				if(Settings.System.getString(getContentResolver(), "bt_connect").equals("1")){
					lv.setVisibility(View.GONE);
					lvSearch.setVisibility(View.GONE);
					lvCallHistory.setVisibility(View.VISIBLE);
				}
			}
		});
		contactBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callLogBtn.setEnabled(true);
				contactBtn.setEnabled(false);
				if(Settings.System.getString(getContentResolver(), "bt_connect").equals("1")){
					lv.setVisibility(View.VISIBLE);
					lvSearch.setVisibility(View.GONE);
					lvCallHistory.setVisibility(View.GONE);
				}
			}
		});
		
		tv.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// H
				if(Settings.System.getString(getContentResolver(), "bt_connect").equals("1")){
					resetLvSearchGoneTime();
					 //文本变化中
					if(s.length() > 0){
						lvSearch.setVisibility(View.VISIBLE);
						searchAdapter.getFilter().filter(s);
					}else{
						Dfine.searchUser.clear();
						searchAdapter.notifyDataSetChanged();
						lvSearch.setVisibility(View.GONE);
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				//文本变化前
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// 文本变化后
				//contactList = MyService.findContacts(MainActivity.this, s.toString());
				//方法1：自己定义Adapter  每次都new貌似效率不好
		        //lv.setAdapter(new MyAdapter());  
			}
		});
		

		searchAdapter = new SearchListAdapter(this);
		lvSearch.setAdapter(searchAdapter);
		
		listHeader = new TextView(this);
		listCHHeader = new TextView(this);
		
		//获取联系人列表，并显示联系人listview
		//if(ContactOperate.contactList != null){
		//	contactAdapter = new ContactListAdapter(MainActivity.this, R.layout.contact_list, ContactOperate.contactList);
		//	lv.setAdapter(contactAdapter); 
		//}
		int count = (ContactOperate.contactList != null) ? ContactOperate.contactList.size() : 0;
		listHeader.setText("共" + count + "个联系人");
		listHeader.setTextSize(20);
		listHeader.setPadding(0, 10, 0, 5);
		if((lv.getHeaderViewsCount() == 0)){
			lv.addHeaderView(listHeader);
		}
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(arg2 > 0)
				{
					String number = ContactOperate.contactList.get(arg2-1).getPhone();
					String name = ContactOperate.contactList.get(arg2-1).getName();
					showLvSearch = false;
					startGocsdkService(OperateCommand.CALL_DEAIL+number);
					if(number.contains("+")){
						number = number.substring(3);
					}
					//tv.setText(number);
				}
			}
		});
		
		//获取通话记录列表，并显示通话记录listview
		//Collections.reverse(ContactOperate.callLogList);
		callLogAdapter = new CallLogListAdapter(MainActivity.this, ContactOperate.callLogList);
		lvCallHistory.setAdapter(callLogAdapter); 
		lvCallHistory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				//if(arg2 > 0)
				{
					Log.d("goc", "arg2 " + arg2 + " list size " + ContactOperate.callLogList.size());
					String number = ContactOperate.callLogList.get(arg2).getNumber();
					String name = ContactOperate.callLogList.get(arg2).getName();
					showLvSearch = false;
					startGocsdkService(OperateCommand.CALL_DEAIL+number);
					if(number.contains("+")){
						number = number.substring(3);
					}
				}
			}
		});
		
		
		lvSearch.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				resetLvSearchGoneTime();
				return false;
			}
		});
	}
	ContactListAdapter contactAdapter;
	SearchListAdapter searchAdapter;
	CallLogListAdapter callLogAdapter;

	/*
	 * 启动线程让T9键盘搜索到的联系人list不显示
	 * 15s的时间等待
	 */
	private void resetLvSearchGoneTime(){
		if(showLvSearch){
			lvSearch.setVisibility(View.VISIBLE);
			try{
				handler.removeCallbacks(r);
			}catch(Exception e){}
			handler.postDelayed(r, 10000);
		}
		showLvSearch = true;
	}
	private boolean showLvSearch = true;
	Handler handler = new Handler();
	Runnable r = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			lvSearch.setVisibility(View.GONE);
		}
		
	};
	
	/**
	 * 显示蓝牙状态
	 */
	private void initBTStatus(){
        boolean btEnable = Settings.System.getString(getContentResolver(), "bt_enable").equals("1");
        boolean btConnected = Settings.System.getString(getContentResolver(), "bt_connect").equals("1");
        
        btStatusOpen.setText(btEnable ? "已打开" : "已关闭");
        btStatusOpen.setText((btConnected && btEnable) ? Config.BT_PARI_NAME : "未连接");
        if(btConnected){
        	btStatusPic.setImageResource(R.drawable.bt_status_connected);
			if(!callLogBtn.isEnabled()){
				lvCallHistory.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
			}else{
				lv.setVisibility(View.VISIBLE);
				lvCallHistory.setVisibility(View.GONE);
			}
        }else{
        	btStatusPic.setImageResource(btEnable ? R.drawable.bt_status_opened : R.drawable.bt_status_closed);
			lvCallHistory.setVisibility(View.INVISIBLE);
			lv.setVisibility(View.INVISIBLE);
        }
        
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.keypad_one:
				playTone(ToneGenerator.TONE_DTMF_1);
				change("1");
				break;
			case R.id.keypad_two:
				playTone(ToneGenerator.TONE_DTMF_2);
				change("2");
				break;
			case R.id.keypad_three:
				playTone(ToneGenerator.TONE_DTMF_3);
				change("3");
				break;
			case R.id.keypad_four:
				playTone(ToneGenerator.TONE_DTMF_4);
				change("4");
				break;
			case R.id.keypad_five:
				playTone(ToneGenerator.TONE_DTMF_5);
				change("5");
				break;
			case R.id.keypad_six:
				playTone(ToneGenerator.TONE_DTMF_6);
				change("6");
				break;
			case R.id.keypad_seven:
				playTone(ToneGenerator.TONE_DTMF_7);
				change("7");
				break;
			case R.id.keypad_eight:
				playTone(ToneGenerator.TONE_DTMF_8);
				change("8");
				break;
			case R.id.keypad_nine:
				playTone(ToneGenerator.TONE_DTMF_9);
				change("9");
				break;
			case R.id.keypad_star:
				playTone(ToneGenerator.TONE_DTMF_S);
				change("*");
				break;
			case R.id.keypad_zero:
				playTone(ToneGenerator.TONE_DTMF_0);
				change("0");
				//startGocsdkService(OperateCommand.BT_MUSIC_MUTE);
				break;
			case R.id.keypad_pound:
				playTone(ToneGenerator.TONE_DTMF_P);
				change("#");
				//startGocsdkService(OperateCommand.BT_MUSIC_UNMUTE);
				break;
			case R.id.keypad_call:
				String number = tv.getText().toString();
				if(number != null && number.length() > 0)
					startGocsdkService(OperateCommand.CALL_DEAIL+number);
				break;
			case R.id.keypad_del:
				delete();
				break;
		}
	}
	
	/**
	 * 启动GocsdkService
	 * @param command
	 */
	private void startGocsdkService(String command){
	    Intent intent = new Intent(MainActivity.this,GocsdkService.class);  
	    if(command != null)
	    	intent.putExtra("command", command);
        startService(intent);
	}
	
 
    /**
     * 播放按键声音
     */
    private void playTone(int tone) {
        Log.w(TAG, "mDTMFToneEnabled : " + mDTMFToneEnabled);
        if (!mDTMFToneEnabled) {
            return;
        }
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if (ringerMode == AudioManager.RINGER_MODE_SILENT
                || ringerMode == AudioManager.RINGER_MODE_VIBRATE || Config.btMusic) {
            // 静音或者震动时不发出声音
            return;
        }
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log.w(TAG, "playTone: mToneGenerator == null, tone: " + tone);
                return;
            }
            mToneGenerator.startTone(tone, DTMF_DURATION_MS);   //发出声音
        }
    }
    
	private void change(String number){
		StringBuffer sb = new StringBuffer(tv.getText());
		tv.setText(sb.append(number));
	}
	
	
	/**
	 * 点击删除按钮删除操作
	 */
	private void delete(){
		if(tv.getText() != null && tv.getText().length() > 1){
			StringBuffer sb = new StringBuffer(tv.getText());
			tv.setText(sb.substring(0, sb.length()-1));
		}else if(tv.getText() != null && !"".equals(tv.getText())){
			tv.setText("");
		}
	}
	
	/**
	 * 初始化消息
	 */
	private void initGocReceiver(){
		gocReceiver = new GocReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GocMessage.CONTACT_SYNC_DONE);
		filter.addAction(GocMessage.BT_DISCONNECTED);
		filter.addAction(GocMessage.BT_CONNECTED);
		filter.addAction(GocMessage.BT_CONNECTED_NAME);
		filter.addAction(GocMessage.CONTACT_DELETE_DONE);
		filter.addAction(GocMessage.CONTACT_READED);
		registerReceiver(gocReceiver, filter);
	}
	
	/**
	 *  显示联系人
	 */
	private void showContactList(boolean show){
		Log.d("wwj_test", "VISIBLE : " + (lv.getVisibility() == View.VISIBLE));
		Config.contactChanged = false;

		int count = (ContactOperate.contactList != null) ? ContactOperate.contactList.size() : 0;
		listHeader.setText("共" + count + "个联系人");
		if(ContactCallLogStatus.contactSyncing()){
			emptyContactTv.setText("正在同步联系人...");
			listHeader.setText("");
		}else{
			emptyContactTv.setText("没有联系人");
		}		
		contactAdapter = new ContactListAdapter(MainActivity.this, R.layout.contact_list, show ? ContactOperate.contactList : null);
		lv.setAdapter(contactAdapter); 
		if((lv.getHeaderViewsCount() == 0)){
			lv.addHeaderView(listHeader);
		}
		Log.d("wwj_test", "VISIBLE2 : " + (lv.getVisibility() == View.VISIBLE));
	}
	
	/**
	 *  显示通话记录
	 */
	private void showCallLogList(){
		//获取通话记录列表，并显示通话记录listview
		Config.callLogChanged = false;
		//callLogAdapter.reSetAdapter(ContactOperate.callLogList);
		callLogAdapter = new CallLogListAdapter(MainActivity.this, ContactOperate.callLogList);
		lvCallHistory.setAdapter(callLogAdapter); 
	}
	
	/**
	 * 接受GocsdkService发的message消息
	 */
	GocReceiver gocReceiver;
    private class GocReceiver extends BroadcastReceiver {    	
        @Override
        public void onReceive(Context context, Intent intent) {
        	String action = intent.getAction();
        	Log.d("wwj_test", "action : " + action);
        	if(GocMessage.CONTACT_SYNC_DONE.equals(action)){
        		showContactList(true);
        	}else if(GocMessage.BT_DISCONNECTED.equals(action)){
        		showCallLogList();
        		showContactList(true);
        		initBTStatus();
        	}else if(GocMessage.BT_CONNECTED.equals(action)){
        		initBTStatus();
        	}else if(GocMessage.BT_CONNECTED_NAME.equals(action)){        		
        		initBTStatus();
        	}else if(GocMessage.CONTACT_DELETE_DONE.equals(action)){
        		showContactList(true);
        		showContactCallLog();
        	}else if(GocMessage.CONTACT_READED.equals(action)){
        		//联系人读取完毕，显示联系人
        		showContactList(true);
        		showCallLogList();
        		showContactCallLog();
        	}
        }
    }
    
    /**
     * 显示联系人，通话记录
     */
    private void showContactCallLog(){
		boolean btConnected = Settings.System.getString(getContentResolver(), "bt_connect").equals("1");
		if(btConnected){
			if(!callLogBtn.isEnabled()){
				lvCallHistory.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
			}else{
				lv.setVisibility(View.VISIBLE);
				lvCallHistory.setVisibility(View.GONE);
			}
		}else{
			lvCallHistory.setVisibility(View.INVISIBLE);
			lv.setVisibility(View.INVISIBLE);
		}
    }
	
    /**
     * 蓝牙设置入口
     * @param v
     */
    public void onStartSettings(View v){
    	Intent intent = new Intent(MainActivity.this, BTSettings.class);
		startActivity(intent);
    }
    /**
     * 蓝牙音量增大
     * @param v
     */
    public void onVolumeUp(View v){
    	startGocsdkService(OperateCommand.SET_VOICE_UP);
    }
    /**
     * 蓝牙音量减小
     * @param v
     */
    public void onVolumeDown(View v){
		startGocsdkService(OperateCommand.SET_VOICE_DOWN);
    }

    
    public boolean dispatchKeyEvent(KeyEvent event){
    	switch(event.getKeyCode()){
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
}
