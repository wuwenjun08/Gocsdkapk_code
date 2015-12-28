package com.goodocom.gocsdk.service;

import com.goodocom.gocsdk.Commands;
import com.goodocom.gocsdk.GocsdkCommon;
import com.goodocom.gocsdk.IGocsdkCallback;

import android.R.integer;
import android.R.string;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class CommandParser extends GocsdkCommon{
	private static final String TAG = CommandParser.class.getName();

	private RemoteCallbackList<IGocsdkCallback> callbacks;

	public CommandParser(RemoteCallbackList<IGocsdkCallback> callbacks) {
		this.callbacks = callbacks;
	}

	private byte[] serialBuffer = new byte[1024];
	private int count = 0;

	private void onSerialCommand(String cmd) {
		Log.d("goc","CommandParser:"+ cmd);
		int i = callbacks.beginBroadcast();
		while (i > 0) {
			i--;
			IGocsdkCallback cbk = callbacks.getBroadcastItem(i);
			try {
				if (cmd.startsWith(Commands.IND_HFP_CONNECTED)) {
					cbk.onHfpConnected();
				} else if (cmd.startsWith(Commands.IND_HFP_DISCONNECTED)) {
					cbk.onHfpDisconnected();
				} else if (cmd.startsWith(Commands.IND_CALL_SUCCEED)) {
					cbk.onCallSucceed(cmd.substring(2));
				} else if (cmd.startsWith(Commands.IND_INCOMING)) {
					cbk.onIncoming(cmd.substring(2));
				} else if (cmd.startsWith(Commands.IND_SECOND_INCOMING)) {
					//cbk.onSecondIncoming(cmd.substring(2));
				} else if (cmd.startsWith(Commands.IND_HANG_UP)) {
					cbk.onHangUp();
				} else if (cmd.startsWith(Commands.IND_TALKING)) {
					cbk.onTalking();
				} else if (cmd.startsWith(Commands.IND_RING_START)) {
					cbk.onRingStart();
				} else if (cmd.startsWith(Commands.IND_RING_STOP)) {
					cbk.onRingStop();
				} else if (cmd.startsWith(Commands.IND_HF_LOCAL)) {
					cbk.onHfpLocal();
				} else if (cmd.startsWith(Commands.IND_HF_REMOTE)) {
					cbk.onHfpRemote();
				} else if (cmd.startsWith(Commands.IND_IN_PAIR_MODE)) {
					cbk.onInPairMode();
				} else if (cmd.startsWith(Commands.IND_EXIT_PAIR_MODE)) {
					cbk.onExitPairMode();
				} else if (cmd.startsWith(Commands.IND_CALL_HOLD)) {
					//cbk.onCallHold();
				} else if (cmd.startsWith(Commands.IND_HOLD_CURRENT_ACCEPT_WAITING)) {
					//cbk.onHoldCurrentAcceptWaiting();
				} else if (cmd.startsWith(Commands.IND_IN_MEETING)) {
					//cbk.onInMeeting();
				} else if (cmd.startsWith(Commands.IND_HANG_UP_HOLDING_WAITING)) {
					//cbk.onHangUpHoldingWaiting();
				} else if (cmd.startsWith(Commands.IND_HANG_UP_CURRENT_ACCEPT_WAITING)) {
					//cbk.onHangUpCurrentAcceptWaiting();
				} else if (cmd.startsWith(Commands.IND_INCOMING_NAME)) {
					//cbk.onIncomingName(cmd.substring(2));
				} else if (cmd.startsWith(Commands.IND_OUTGOING_TALKING_NUMBER)) {
					cbk.onOutGoingOrTalkingNumber(cmd.substring(2));
				} else if (cmd.startsWith(Commands.IND_INIT_SUCCEED)) {
					cbk.onInitSucceed();
				} else if (cmd.startsWith(Commands.IND_CONNECTING)) {
					cbk.onConnecting();
				} else if (cmd.startsWith(Commands.IND_MUSIC_PLAYING)) {
					cbk.onMusicPlaying();
				} else if (cmd.startsWith(Commands.IND_MUSIC_STOPPED)) {
					cbk.onMusicStopped();
				} else if (cmd.startsWith(Commands.IND_VOICE_CONNECTED)) {
					//cbk.onVoiceConnected();
				} else if (cmd.startsWith(Commands.IND_VOICE_DISCONNECTED)) {
					//cbk.onVoiceDisconnected();
				} else if (cmd.startsWith(Commands.IND_AUTO_CONNECT_ACCEPT)) {
					if (cmd.length() < 4) {
						Log.e(TAG, cmd + "=====error command");
					} else {
						cbk.onAutoConnectAccept(cmd.charAt(2) == '1',
								cmd.charAt(3) != '0');
					}
				} else if (cmd.startsWith(Commands.IND_CURRENT_ADDR)) {
					if (cmd.length() < 3) {
						Log.e(TAG, cmd + "==== error command");
					} else {
						cbk.onCurrentAddr(cmd.substring(2));
					}
				}else if(cmd.startsWith(Commands.IND_CURRENT_NAME)){
					if (cmd.length() < 3) {
						Log.e(TAG, cmd + "==== error command");
					} else {
						cbk.onCurrentName(cmd.substring(2));
					}
				}else if(cmd.startsWith(Commands.IND_AV_STATUS)){
					if(cmd.length() < 3){
						Log.e(TAG, cmd + "=====error");
					}else{
						cbk.onAvStatus(Integer.parseInt(cmd.substring(2)));
					}
				}else if (cmd.startsWith(Commands.IND_HFP_STATUS)) {
					if(cmd.length() < 3){
						Log.e(TAG, cmd +" ==== error");
					}else{
						cbk.onHfpStatus(Integer.parseInt(cmd.substring(2)));
					}
				}else if (cmd.startsWith(Commands.IND_VERSION_DATE)) {
					if(cmd.length() < 3){
						Log.e(TAG, cmd + "====error");
					}else{
						cbk.onVersionDate(cmd.substring(2));
					}
				}else if (cmd.startsWith(Commands.IND_CURRENT_DEVICE_NAME)) {
					if(cmd.length() < 3){
						Log.e(TAG, cmd + "====error");
					}else{
						cbk.onCurrentDeviceName(cmd.substring(2));
					}
				}else if (cmd.startsWith(Commands.IND_CURRENT_PIN_CODE) ){
					if(cmd.length() < 3){
						Log.e(TAG, cmd + "====error");
					}else{
						cbk.onCurrentPinCode(cmd.substring(2));
					}
				}else if (cmd.startsWith(Commands.IND_A2DP_CONNECTED)) {
					cbk.onA2dpConnected ();
				}else if (cmd.startsWith(Commands.IND_A2DP_DISCONNECTED)) {
					cbk.onA2dpDisconnected ();
				}else if (cmd.startsWith(Commands.IND_CURRENT_AND_PAIR_LIST)) {
					if(cmd.length() < 15){
						Log.e(TAG, cmd + "====error");
					}else if (cmd.length() == 15) {
						cbk.onCurrentAndPairList(Integer.parseInt(cmd.substring(2, 3)),"",cmd.substring(3,15));
					}else{
						cbk.onCurrentAndPairList(Integer.parseInt(cmd.substring(2, 3)),cmd.substring(15),cmd.substring(3,15));
					}
				}else if (cmd.startsWith(Commands.IND_PHONE_BOOK)) {
					if (false) { /*[name][ff][num]*/
						if(cmd.length() < 4){
							Log.e(TAG, cmd + "====error");
						}else{
							String name;
							String num;
							int    length = cmd.length();
							byte[] bytes = cmd.getBytes();
							byte[] numbuffer = new byte[128];
							byte[] namebuffer = new byte[128];
							for (int j = 2; j < length; j++) {
								if (0xff == bytes[j]) {
									if (j+1 == length) {
										break;
									}
									System.arraycopy(bytes, j+1, numbuffer, 0, length-j-1);
									break;
								}else {
									namebuffer[j -2] = bytes[j];
								}
							}
							num = new String(numbuffer);
							name = new String(namebuffer);
							Log.d(TAG, "name:"+name+",numb:"+namebuffer);
							cbk.onPhoneBook(name, num);
						}
					}else/*[namelen:2][numberlen:2][name][number]*/ 
					{
						if(cmd.length() < 6){
							Log.e(TAG, cmd + "====error");
						}else{
							int nameLen = Integer.parseInt(cmd.substring(2,4));
							int numLen = Integer.parseInt(cmd.substring(4,6));
							String name;
							String num;
							byte[] bytes = cmd.getBytes();
							if(nameLen > 0 ){
							byte[] buffer = new byte[nameLen];
							System.arraycopy(bytes, 6, buffer, 0, nameLen);
							name = new String(buffer);
							}else{
								name = "";
							}
							if(numLen > 0){
								byte[] buffer = new byte[numLen];
								System.arraycopy(bytes,6+nameLen, buffer, 0, numLen);
								num = new String(buffer);
							}else{
								num = "";
							}	
							Log.d(TAG, "name:"+name+",num:"+num);
							cbk.onPhoneBook(name, num);
						}
					}
				}else if (cmd.startsWith(Commands.IND_SIM_BOOK)) {
					if (false/*[name][ff][num]*/) {
						if(cmd.length() < 4){
							Log.e(TAG, cmd + "====error");
						}else{
							String name;
							String num;
							int    length = cmd.length();
							byte[] bytes = cmd.getBytes();
							byte[] numbuffer = new byte[128];
							byte[] namebuffer = new byte[128];
							for (int j = 2; j < length; j++) {
								if (0xff == bytes[j]) {
									if (j+1 == length) {
										num = "";
										break;
									}
									System.arraycopy(bytes, j+1, numbuffer, 0, length-j-1);
									break;
								}else {
									namebuffer[j -2] = bytes[j];
								}
							}
							num = new String(numbuffer);
							name = new String(namebuffer);
							Log.d(TAG, "name:"+name+",bum:"+num);
							cbk.onPhoneBook(name, num);
						}
					}else/*[namelen:2][numberlen:2][name][number]*/ 
					{
						if(cmd.length() < 6){
							Log.e(TAG, cmd + "====error");
						}else{
							int nameLen = Integer.parseInt(cmd.substring(2,4));
							int numLen = Integer.parseInt(cmd.substring(4,6));
							String name;
							String num;
							byte[] bytes = cmd.getBytes();
							if(nameLen > 0 ){
							byte[] buffer = new byte[nameLen];
							System.arraycopy(bytes, 6, buffer, 0, nameLen);
							name = new String(buffer);
							}else{
								name = "";
							}
							if(numLen > 0){
								byte[] buffer = new byte[numLen];
								System.arraycopy(bytes,6+nameLen, buffer, 0, numLen);
								num = new String(buffer);
							}else{
								num = "";
							}	
							Log.d(TAG, "name:"+name+",num:"+num);
							cbk.onPhoneBook(name, num);
						}
					}
				}else if (cmd.startsWith(Commands.IND_PHONE_BOOK_DONE)) {
					cbk.onPhoneBookDone();
				}else if (cmd.startsWith(Commands.IND_SIM_DONE)) {
					cbk.onSimDone();
				}else if (cmd.startsWith(Commands.IND_CALLLOG_DONE)) {
					cbk.onCalllogDone();
				}else if (cmd.startsWith(Commands.IND_CALLLOG)) {
					if(cmd.length() < 4){
						Log.e(TAG, cmd + "====error");
					}else{
						cbk.onCalllog(Integer.parseInt(cmd.substring(2,3)), cmd.substring(3));
					}
				}else if (cmd.startsWith(Commands.IND_DISCOVERY)) {
					if(cmd.length() < 14){
						Log.e(TAG, cmd+"===error");
					}else if(cmd.length() == 14){
						cbk.onDiscovery("",cmd.substring(2));
					}else {
						cbk.onDiscovery(cmd.substring(14), cmd.substring(2,14));
					}
				}else if (cmd.startsWith(Commands.IND_DISCOVERY_DONE)) {
					cbk.onDiscoveryDone();
				}else if (cmd.startsWith(Commands.IND_LOCAL_ADDRESS)) {
					if(cmd.length() != 14){
						Log.e(TAG, cmd+"===error");
					}else {
						cbk.onLocalAddress(cmd.substring(2));	
					}
				}else if (cmd.startsWith(Commands.IND_SPP_DATA)) {
					if (cmd.length() < 3) {
						Log.e(TAG, cmd+"===error");
					}else {
						cbk.onSppData(Integer.parseInt(cmd.substring(2,3)),cmd.substring(3));	
					}
				}else if (cmd.startsWith(Commands.IND_SPP_CONNECT)) {
					if (cmd.length() < 3) {
						Log.e(TAG, cmd+"===error");
					}else {
						cbk.onSppConnect(Integer.parseInt(cmd.substring(2,3)));
					}
				}else if (cmd.startsWith(Commands.IND_SPP_DISCONNECT)) {
					if (cmd.length() < 3) {
						Log.e(TAG, cmd+"===error");
					}else {
						cbk.onSppDisconnect(Integer.parseInt(cmd.substring(2,3)));
					}
				}else if (cmd.startsWith(Commands.IND_SPP_STATUS)) {
					cbk.onSppStatus(Integer.parseInt(cmd.substring(2,3)));
				}else if (cmd.startsWith(Commands.IND_OPP_RECEIVED_FILE)) {
					cbk.onOppReceivedFile(cmd.substring(2));
				}else if (cmd.startsWith(Commands.IND_OPP_PUSH_SUCCEED)) {
					cbk.onOppPushSuccess();
				}else if (cmd.startsWith(Commands.IND_OPP_PUSH_FAILED)) {
					cbk.onOppPushFailed();
				}else if (cmd.startsWith(Commands.IND_HID_CONNECTED)) {
					cbk.onHidConnected();
				}else if (cmd.startsWith(Commands.IND_HID_DISCONNECTED)) {
					cbk.onHidDisconnected();
				}else if (cmd.startsWith(Commands.IND_HID_STATUS)) {
					cbk.onHidStatus(Integer.parseInt(cmd.substring(2,3)));
				}else if (cmd.startsWith(Commands.IND_MUSIC_INFO)) {
					if (cmd.length() < 3) {
						Log.e(TAG, cmd+"===error");
					}else {
						String musicName;
						String artist;
						int    j = 2;
						int    n = 2;
						int    length = cmd.length();
						byte[] bytes = cmd.getBytes();
						byte[] MusicNamebuffer = new byte[128];
						byte[] artistbuffer = new byte[128];
						for (; j < length; j++) {
							if (0xff == bytes[j]) {
								n = j+1;
								System.arraycopy(bytes, 2, MusicNamebuffer, 0, j-3);
								j++;
								break;
							}
						}
						
						for (; j < length; j++) {
							if (0xff == bytes[j]) {
								System.arraycopy(bytes, n, artistbuffer, 0, j-n);
								break;
							}
						}
						musicName = new String(MusicNamebuffer);
						artist = new String(artistbuffer);
						cbk.onMusicInfo(musicName, artist);
					}
				}else if (cmd.startsWith(Commands.IND_PAN_CONNECT)) {
					cbk.onPanConnect();
				}else if (cmd.startsWith(Commands.IND_PAN_DISCONNECT)) {
					cbk.onPanDisconnect();
				}else if (cmd.startsWith(Commands.IND_PAN_STATUS)) {
					cbk.onPanStatus(Integer.parseInt(cmd.substring(2,3)));
				}else if (cmd.startsWith(Commands.IND_SIGNAL_BATTERY_VAL)) {
					if (cmd.length() < 6) {
						Log.e(TAG, cmd+"===error");
					}else {
						//cbk.onSignalBatteryVal(Integer.parseInt(cmd.substring(2,4), Integer.parseInt(cmd.substring(4,6));
					}
				}else if(cmd.startsWith(Commands.READ_LAST_PHONEBOOK_COUNT)){
					cbk.onCalllog(Integer.parseInt(cmd.substring(2,3)), cmd.substring(3));
				}else if(cmd.startsWith(Commands.IND_CURRENT_VOLUME)){
					Log.d("goc", "cmd : " + cmd);
					cbk.onBTVolume(Integer.parseInt(cmd.substring(2)));
				}else if(cmd.startsWith(Commands.INQUIRY_PAIR_RECORD)){
					Log.d("goc", "cmd : " + cmd);
					cbk.onPairBtAddr(cmd.substring(2, 14));
				}
				
				else{
					Log.e(TAG, cmd+"===error");
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		callbacks.finishBroadcast();
	}

	private void onByte(byte b) {
		if ('\n' == b) return;
		if (count >= 1000) count = 0;
		if ('\r' == b) {
			if (count > 0) {
				byte[] buf = new byte[count];
				System.arraycopy(serialBuffer, 0, buf, 0, count);
				onSerialCommand(new String(buf));
				count = 0;
			}
			return;
		}
		serialBuffer[count++] = b;
	}

	public void onBytes(byte[] data) {
		for (byte b : data) {
			onByte(b);
		}
	}
}
