package com.goodocom.gocsdk.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.goodocom.gocsdk.Config;

import android.app.Service;
//import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
//import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class PlayerService extends Service implements OnCompletionListener{
	private static final String TAG = "goc";
	private static final int RESTART_DELAY = 2000; // ms
	private static final int MUSICDATA_SIZE = 122880;
	private static final int MUSICDATA_DELAYTIME = 2000;
	private static final int MUSICSTART_DELAYTIME = 500;
	private static final int MUSICVOLUME_DELAYTIME = 10;
	private static final int MSG_START_CONTROL = 1;
	private static final int MSG_START_DATA = 2;
	private static final int MSG_CONTROL_RECEIVED = 3;
	private static final int MSG_MUSICDATA_TIMEOUT = 4;
	private static final int MSG_MUSICSTART_TIMEOUT = 5;
	
	private volatile int sampleRate = 44100;
	private volatile int channels = 2;
	private volatile int sampleBits = 16;
	private volatile int MusicStartDelay = 0;
	private volatile float mCurrentVolume = 1.0f;
	
	private ControlThread controlThread = null;
	private DataThread dataThread = null;

	private volatile boolean running = true;
	private volatile boolean ringing = false;
	
	private volatile AudioTrack audioTrack = null;
	private volatile MediaPlayer ringPlayer = null;
	
	private StringBuilder controlBuilder = new StringBuilder();

	//private AudioManager mAudioManager = null;
	
	private void openAudioTrack(int rate,int ch,int bits){
		if(rate == sampleRate && ch == channels && bits == sampleBits && audioTrack != null){
			//audioTrack.play(); //根据状态进行播放和暂停
			return;
		}
		sampleRate = rate;
		channels = ch;
		sampleBits = bits;
		
		int minBufSize = AudioTrack.getMinBufferSize(
				sampleRate,
				channels==2?AudioFormat.CHANNEL_OUT_STEREO:AudioFormat.CHANNEL_OUT_MONO,
				sampleBits==16?AudioFormat.ENCODING_PCM_16BIT:AudioFormat.ENCODING_PCM_8BIT);
		if(audioTrack != null){
			audioTrack.stop();
			audioTrack = null;
		}
		
		minBufSize *= 10;
		Log.d("goc", "minBufSize:"+minBufSize);
		
		audioTrack = new AudioTrack(
				Config.STREAM_TYPE,
				sampleRate,
				channels==2?AudioFormat.CHANNEL_OUT_STEREO:AudioFormat.CHANNEL_OUT_MONO,
				sampleBits==16?AudioFormat.ENCODING_PCM_16BIT:AudioFormat.ENCODING_PCM_8BIT,
				minBufSize,
				AudioTrack.MODE_STREAM);
		int realRate = sampleRate;
		audioTrack.setPlaybackRate(realRate);
	}
	private void onControlCommand(String cmd){
		//Log.d(TAG, cmd);
		if(cmd.startsWith("open")){
			if(cmd.length() < 28){
				Log.e(TAG, "get error open:"+cmd);
				return;
			}
			int rate = Integer.valueOf(cmd.substring(4, 12), 16);
			int ch = Integer.valueOf(cmd.substring(12, 20), 16);
			int bits =  Integer.valueOf(cmd.substring(20, 28), 16);
			Log.d(TAG, "open rate:"+rate + " channels:"+ch+" bits:"+bits);
			openAudioTrack(rate, ch, bits);
		}else if(cmd.startsWith("play")){
			Log.d(TAG,"Music Play");
			if(audioTrack != null){
				if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
					audioTrack.play();
				}
			}else {
				openAudioTrack(sampleRate, channels, sampleBits);
				audioTrack.play();
			}
		}
		else if(cmd.startsWith("pause")){
			Log.d(TAG,"Music pause");
			if(audioTrack != null){
				audioTrack.pause(); ////根据状态进行播放和暂停
				MusicStartDelay = 0;
			}
		}
		else if(cmd.startsWith("stop")){
			Log.d(TAG,"Music stop");
			if(audioTrack != null){
				audioTrack.pause();
				audioTrack.flush();
				MusicStartDelay = 0;
			}
		}else if(cmd.startsWith("ring start")){
			Log.d(TAG, "PlayerService ring start");
			ringing = true;
			ringStart();
		}else if(cmd.startsWith("ring stop")){
			Log.d(TAG, "PlayerService ring stop");
			ringing = false;
			ringStop();
		}else if(cmd.startsWith("mute")){
			Log.d(TAG, "PlayerService mute");
			if(audioTrack != null)audioTrack.setStereoVolume(0f, 0f);
			mCurrentVolume = 0f;
		}else if(cmd.startsWith("unmute")){
			Log.d(TAG, "PlayerService unmute");
			if(audioTrack != null)audioTrack.setStereoVolume(1.0f, 1.0f);
			mCurrentVolume = 1.0f;
		}else if(cmd.startsWith("vol half")){
			Log.d(TAG, "PlayerService vol half");
			if(audioTrack != null)audioTrack.setStereoVolume(0.5f, 0.5f);
			mCurrentVolume = 0.5f;
		}else if(cmd.startsWith("vol normal")){
			Log.d(TAG, "PlayerService vol normal");
			if(audioTrack != null)audioTrack.setStereoVolume(1.0f, 1.0f);
			mCurrentVolume = 1.0f;
		}else if(cmd.startsWith("vol")){
			int ivol= Integer.parseInt(cmd.substring(4));
			float vol = ((float)ivol) / 100;
			Log.d(TAG, "PlayerService"+ivol+" "+vol);
			if(audioTrack != null)audioTrack.setStereoVolume(vol, vol);
			mCurrentVolume = vol;
		}
	}
	
	//private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
		
	//	@Override
	//	public void onAudioFocusChange(int focusChange) {
	//		if(null == audioTrack)return;
	//		switch (focusChange) {
	//		case AudioManager.AUDIOFOCUS_GAIN:
	//			audioTrack.setStereoVolume(1.0f, 1.0f);
	//			break;
	//		case AudioManager.AUDIOFOCUS_LOSS:
	//			//audioTrack.setStereoVolume(0, 0);
	//			break;
	//		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
	//			audioTrack.setStereoVolume(0, 0);
	//			break;
	//		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
	//			audioTrack.setStereoVolume(0.5f, 0.5f);
	//			break;
	//		default:
	//			break;
	//		}
	//	}
	//};
	
	private void onControlByte(byte b){
		if(b != '\n'){
			controlBuilder.append((char)b);
			return;
		}
		
		onControlCommand(controlBuilder.toString());
		controlBuilder.delete(0, controlBuilder.length());
	}
	
	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MSG_START_CONTROL) {
				controlThread = new ControlThread();
				controlThread.start();
				if(audioTrack != null)audioTrack.pause();
			} else if (msg.what == MSG_START_DATA) {
				dataThread = new DataThread();
				dataThread.start();
			}else if(msg.what == MSG_CONTROL_RECEIVED){
				byte[] data = (byte[]) msg.obj;
				for(byte b:data){
					onControlByte(b);
				}
			}else if (msg.what == MSG_MUSICDATA_TIMEOUT) {
				if(audioTrack != null)
					if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
						audioTrack.pause();
						MusicStartDelay = 0;
					}
			}else if (msg.what == MSG_MUSICSTART_TIMEOUT) {
				//Log.d(TAG, "MusicStartDelay = "+ MusicStartDelay + "mCurrentVolume = " + mCurrentVolume +"f");
				if (1 == MusicStartDelay) {
					if(audioTrack != null){
						audioTrack.setStereoVolume(0.0f, 0.0f);
						mCurrentVolume = 0.0f;
					}
					MusicStartDelay = 2;
					Log.d(TAG, "handle MusicStartDelay "+ MusicStartDelay);
					handler.sendEmptyMessageDelayed(MSG_MUSICSTART_TIMEOUT, MUSICVOLUME_DELAYTIME);
				}else if (2 == MusicStartDelay) {
					if(audioTrack != null){
						mCurrentVolume += 0.01f;
						if (mCurrentVolume < 1.0f) {
							handler.sendEmptyMessageDelayed(MSG_MUSICSTART_TIMEOUT, MUSICVOLUME_DELAYTIME);
						}else {
							mCurrentVolume = 1.0f;
						}
						audioTrack.setStereoVolume(mCurrentVolume, mCurrentVolume);
					}
				}
			}
		};
	};
	
	private void ringStart(){
		String path = null;
		
		if(null == ringPlayer)
			ringPlayer = new MediaPlayer();
		
		for(String p :Config.RING_PATH){
			if(new File(p).exists())path = p;
		}
		if(path == null){
			Log.e(TAG,"cannot find ring file");
			return;
		}
		try{
			ringPlayer.reset();             
			ringPlayer.setDataSource(path);
			ringPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
			ringPlayer.prepare();
			//ringPlayer.setVolume(0.2f, 0.2f); //FANGYITONG BC8 MUST SETVOLUME is 0.3f 
			ringPlayer.start();
			ringPlayer.setOnCompletionListener(this);
			Log.d(TAG, "playing ring "+path);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void ringStop(){
		if(ringPlayer != null)ringPlayer.stop();
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		handler.sendEmptyMessage(MSG_START_CONTROL);
		handler.sendEmptyMessage(MSG_START_DATA);
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	class ControlThread extends Thread {
		private LocalSocket client;
		private LocalSocketAddress address;
		private InputStream inputStream;
		private byte[] buffer = new byte[1024];
		public ControlThread() {
			client = new LocalSocket();
			address = new LocalSocketAddress(Config.CONTROL_SOCKET_NAME,
					LocalSocketAddress.Namespace.RESERVED);
		}

		@Override
		public void run() {
			int n;
			try {
				client.connect(address);
				inputStream = client.getInputStream();
				while(running){
					n = inputStream.read(buffer);
					if(n < 0)throw new IOException("n==-1");
					byte[] data = new byte[n];
					System.arraycopy(buffer, 0, data, 0, n);
					handler.sendMessage(handler.obtainMessage(MSG_CONTROL_RECEIVED, data));
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					client.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				handler.sendEmptyMessageDelayed(MSG_START_CONTROL,
						RESTART_DELAY);
				return;
			}
		}
	}

	class DataThread extends Thread {
		private LocalSocket client;
		private LocalSocketAddress address;
		private InputStream inputStream;
		private byte[] buffer = new byte[MUSICDATA_SIZE];
		public DataThread() {
			client = new LocalSocket();
			address = new LocalSocketAddress(Config.DATA_SOCKET_NAME,
					LocalSocketAddress.Namespace.RESERVED);
		}

		@Override
		public void run() {
			int n;
			try {
				client.connect(address);
				inputStream = client.getInputStream();
				while(running){
					handler.sendEmptyMessageDelayed(MSG_MUSICDATA_TIMEOUT, MUSICDATA_DELAYTIME);
					n = inputStream.read(buffer);
					handler.removeMessages(MSG_MUSICDATA_TIMEOUT);
					
					if(n < 0)throw new IOException("n==-1");
					if(audioTrack != null){
						if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
						{
							if (0 == MusicStartDelay) {
								MusicStartDelay = 1;
								Log.d(TAG, "run1 MusicStartDelay "+ MusicStartDelay);
								audioTrack.setStereoVolume(0.0f, 0.0f);
								mCurrentVolume = 0f;
								handler.sendEmptyMessageDelayed(MSG_MUSICSTART_TIMEOUT, MUSICSTART_DELAYTIME);
							} else {
								if (2 == MusicStartDelay) {
									audioTrack.write(buffer, 0, n);
								}
							}
						}
						else {
							MusicStartDelay = 1;
							Log.d(TAG, "run2 MusicStartDelay "+ MusicStartDelay);
							audioTrack.setStereoVolume(0.0f, 0.0f);
							mCurrentVolume = 0f;
							handler.sendEmptyMessageDelayed(MSG_MUSICSTART_TIMEOUT, MUSICSTART_DELAYTIME);
							audioTrack.play();
						}
					}else {
						openAudioTrack(sampleRate, channels, sampleBits);
						if (audioTrack != null){
							MusicStartDelay = 1;
							Log.d(TAG, "run3 MusicStartDelay "+ MusicStartDelay);
							audioTrack.setStereoVolume(0.0f, 0.0f);
							mCurrentVolume = 0f;
							audioTrack.play();
							handler.sendEmptyMessageDelayed(MSG_MUSICSTART_TIMEOUT, MUSICSTART_DELAYTIME);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					client.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				handler.sendEmptyMessageDelayed(MSG_START_DATA, RESTART_DELAY);
				return;
			}
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if(ringing)ringStart();		
	}
}
