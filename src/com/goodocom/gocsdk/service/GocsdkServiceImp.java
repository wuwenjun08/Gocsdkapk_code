package com.goodocom.gocsdk.service;

import android.R.integer;
import android.R.string;
import android.os.RemoteException;
import android.util.Log;

import com.goodocom.gocsdk.Commands;
import com.goodocom.gocsdk.Config;
import com.goodocom.gocsdk.IGocsdkCallback;
import com.goodocom.gocsdk.IGocsdkService;

public class GocsdkServiceImp extends IGocsdkService.Stub {
	private GocsdkService service;
	private static final String TAG = "goc";
	public  GocsdkServiceImp(GocsdkService service){
		this.service = service;
	}
	public void write(String str){
		Log.d(TAG, str);
		service.write(str);
	}
	
	public void GOCSDK_getLocalName() throws RemoteException {
		write(Commands.MODIFY_LOCAL_NAME);
	}

	public void GOCSDK_setLocalName(String name) throws RemoteException {
		write(Commands.MODIFY_LOCAL_NAME+name);
	}

	public void GOCSDK_getPinCode() throws RemoteException {
		write(Commands.MODIFY_PIN_CODE);
	}

	public void GOCSDK_setPinCode(String pincode) throws RemoteException {
		write(Commands.MODIFY_PIN_CODE+pincode);
	}

	public void GOCSDK_connectA2dp(String addr) throws RemoteException {
		//TODO
		write(Commands.CONNECT_A2DP+addr);
	}

	public void GOCSDK_connectHFP(String addr) throws RemoteException {
		//TODO
		write(Commands.CONNECT_DEVICE+addr);
	}

	public void GOCSDK_disconnect() throws RemoteException {
		write(Commands.DISCONNECT_DEVICE);
	}

	public void GOCSDK_disconnectA2DP() throws RemoteException {
		write(Commands.DISCONNECT_A2DP);
	}

	public void GOCSDK_disconnectHFP() throws RemoteException {
		write(Commands.DISCONNECT_HFP);
	}

	public void GOCSDK_deletePair(String addr) throws RemoteException {
		write(Commands.DELETE_PAIR_LIST+addr);
	}

	public void GOCSDK_startDiscovery() throws RemoteException {
		write(Commands.START_DISCOVERY);
	}

	public void GOCSDK_getPairList() throws RemoteException {
		write(Commands.INQUIRY_PAIR_RECORD);
	}

	public void GOCSDK_stopDiscovery() throws RemoteException {
		write(Commands.STOP_DISCOVERY);
	}

	public void GOCSDK_phoneAnswer() throws RemoteException {
		write(Commands.ACCEPT_INCOMMING);
	}

	public void GOCSDK_phoneHangUp() throws RemoteException {
		write(Commands.REJECT_INCOMMMING);
	}

	public void GOCSDK_phoneDail(String phonenum) throws RemoteException {
		write(Commands.DIAL+phonenum);
	}

	public void GOCSDK_phoneTransmitDTMFCode(char code) throws RemoteException {
		write(Commands.DTMF+code);
	}
	
	
	
	public void GOCSDK_phoneTransfer() throws RemoteException {
		write(Commands.VOICE_TO_PHONE);
	}

	public void GOCSDK_phoneTransferBack() throws RemoteException {
		write(Commands.VOICE_TO_BLUE);
	}

	public void GOCSDK_phoneBookStartUpdate() throws RemoteException {
		write(Commands.SET_PHONE_PHONE_BOOK);
	}

	public void GOCSDK_callLogstartUpdate(int type) throws RemoteException {
		if(type == 2){
			write(Commands.SET_OUT_GOING_CALLLOG);
		}else if (type == 3) {
			write(Commands.SET_MISSED_CALLLOG);
		}else if (type == 1) {
			write(Commands.SET_INCOMING_CALLLOG);
		}
	}

	public void GOCSDK_musicPlayOrPause() throws RemoteException {
		write(Commands.PLAY_PAUSE_MUSIC);
	}

	public void GOCSDK_musicStop() throws RemoteException {
		write(Commands.STOP_MUSIC);
	}

	public void GOCSDK_musicPrevious() throws RemoteException {
		write(Commands.PREV_SOUND);
	}

	public void GOCSDK_musicNext() throws RemoteException {
		write(Commands.NEXT_SOUND);
	}
	public void GOCSDK_connectLast() throws RemoteException {
		write(Commands.CONNECT_DEVICE);
	}
	public void GOCSDK_PairMode(int type) throws RemoteException {
		if (1 == type) {
			write(Commands.PAIR_MODE);
		}else{
			write(Commands.CANCEL_PAIR_MOD);
		}
	}
	public void GOCSDK_ReDail() throws RemoteException {
		write(Commands.REDIAL);
	}
	//public void GOCSDK_VoiceDial() throws RemoteException {
	//	write(Commands.VOICE_DIAL);
	//}
	//public void CancelVoiceDial() throws RemoteException {
	//	write(Commands.CANCEL_VOID_DIAL);
	//}
	//public void GOCSDK_VolumeSet(string volume) throws RemoteException {}
	public void GOCSDK_MicSwitch(char type) throws RemoteException {
		write(Commands.MIC_OPEN_CLOSE+type);
	}
	public void GOCSDK_InquiryHfpStatus() throws RemoteException {
		write(Commands.INQUIRY_HFP_STATUS);
	}
	public void GOCSDK_ResetBluetooth() throws RemoteException {
		write(Commands.RESET_BLUE);
	}
	public void GOCSDK_InquiryAutoConnectAccept() throws RemoteException {
		write(Commands.INQUIRY_AUTO_CONNECT_ACCETP);
	}
	public void GOCSDK_SetAutoConnect(char type) throws RemoteException {
		if('1' == type){
			write(Commands.SET_AUTO_CONNECT_ON_POWER);
		}else {
			write(Commands.UNSET_AUTO_CONNECT_ON_POWER);
		}
	}
	public void GOCSDK_SetAutoAccept(char type) throws RemoteException {
		if('1' == type){
			write(Commands.SET_AUTO_ANSWER);
		}else {
			write(Commands.UNSET_AUTO_ANSWER);
		}
	}
	public void GOCSDK_InquiryA2dpStatus() throws RemoteException {
		write(Commands.INQUIRY_A2DP_STATUS);
	}
	public void GOCSDK_InquiryVersion() throws RemoteException {
		write(Commands.INQUIRY_VERSION_DATE);
	}
	public void GOCSDK_MuteOrUnmuteA2dp(char type) throws RemoteException {
		if ('1' == type) {
			write(Commands.MUSIC_MUTE);
		}else {
			write(Commands.MUSIC_UNMUTE);
		}
	}
	public void GOCSDK_UnmuteOrHalfA2dp(char type) throws RemoteException {
		if ('1' == type) {
			write(Commands.MUSIC_NORMAL);
		}else {
			write(Commands.MUSIC_BACKGROUND);
		}
	}
	public void GOCSDK_OppSendFile(string path) throws RemoteException {
		write(Commands.OPP_SEND_FILE+path);
	}
	public void GOCSDK_SppConnect(string addr) throws RemoteException {
		write(Commands.CONNECT_SPP_ADDRESS+addr);
	}
	public void GOCSDK_SppSendData(string data) throws RemoteException {
		write(Commands.SPP_SEND_DATA+data);
	}
	public void GOCSDK_SppDisConnect(char index) throws RemoteException {
		write(Commands.SPP_DISCONNECT+index);
	}
	public void GOCSDK_InquirySppStatus() throws RemoteException {
		write(Commands.INQUIRY_SPP_STATUS);
	}
	public void GOCSDK_HidConnect(string addr) throws RemoteException {
		write(Commands.CONNECT_HID+addr);
	}
	public void GOCSDK_HidMouseMove(string data) throws RemoteException {
		write(Commands.MOUSE_MOVE+data);
	}
	public void GOCSDK_HidHomeKey() throws RemoteException {
		write(Commands.MOUSE_HOME);
	}
	public void GOCSDK_HidBackKey() throws RemoteException {
		write(Commands.MOUSE_BACK);
	}
	public void GOCSDK_HidMenuKey() throws RemoteException {
		write(Commands.MOUSE_MENU);
	}
	public void GOCSDK_HidDisConnect() throws RemoteException {
		write(Commands.DISCONNECT_HID);
	}
	public void GOCSDK_InquiryHidStatus() throws RemoteException {
		write(Commands.INQUIRY_HID_STATUS);
	}
	public void GOCSDK_SetHidResolution(string data) throws RemoteException {
		write(Commands.SET_TOUCH_RESOLUTION+data);
	}
	public void GOCSDK_PauseMusic() throws RemoteException {
		write(Commands.PAUSE_MUSIC);
	}
	public void GOCSDK_PanConnect(string addr) throws RemoteException {
		write(Commands.PAN_CONNECT);
	}
	public void GOCSDK_PanDisConnect() throws RemoteException {
		write(Commands.PAN_DISCONNECT);
	}
	public void GOCSDK_InquiryPanStatus() throws RemoteException {
		write(Commands.INQUIRY_PAN_STATUS);
	}
	public void GOCSDK_InquiryLocalAddr() throws RemoteException {
		write(Commands.INQUIRY_DB_ADDR);
	}	
	public void GOCSDK_OpenBt() throws RemoteException {
		write(Commands.OPEN_BT);
	}
	public void GOCSDK_CloseBt() throws RemoteException {
		write(Commands.CLOSE_BT);
	}
	public void GOCSDK_InquiryCurBtAddr() throws RemoteException {
		write(Commands.INQUIRY_CUR_BT_ADDR);
	}
	public void GOCSDK_InquiryCurBtName() throws RemoteException {
		write(Commands.INQUIRY_CUR_BT_NAME);
	}	
	//public void GOCSDK_InquirySpkMicVal() throws RemoteException {
	//	write(Commands.INQUIRY_SPK_MIC_VAL);
	//}
	public void GOCSDK_InquirySignelBatteryVal() throws RemoteException {
		write(Commands.INQUIRY_SIGNEL_BATTERY_VAL);
	}
	public void GOCSDK_SetMusicVal() throws RemoteException {
		write(Commands.MUSIC_VOL_SET);
	}
	public void GOCSDK_InquiryMusicInfo() throws RemoteException {
		write(Commands.INQUIRY_MUSIC_INFO);
	}
	//public void GOCSDK_UpdataPskey() throws RemoteException {
	//	write(Commands.UPDATE_PSKEY);
	//}
	@Override
	public void GOCSDK_OppSendFile(String path) throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.OPP_SEND_FILE+path);
	}
	@Override
	public void GOCSDK_SppConnect(String addr) throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.CONNECT_SPP_ADDRESS+addr);
	}
	@Override
	public void GOCSDK_SppSendData(String data) throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.SPP_SEND_DATA+data);
	}
	@Override
	public void GOCSDK_HidConnect(String addr) throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.CONNECT_HID+addr);
	}
	@Override
	public void GOCSDK_HidMouseMove(String data) throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.MOUSE_MOVE+data);
	}
	@Override
	public void GOCSDK_SetHidResolution(String data) throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.SET_TOUCH_RESOLUTION+data);
	}
	@Override
	public void GOCSDK_PanConnect(String addr) throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.PAN_CONNECT+addr);
	}
	@Override
	public void GOCSDK_SetMusicVal(int val) throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.MUSIC_VOL_SET+val);
	}
	public void GOCSDK_registerCallback(IGocsdkCallback callback)
			throws RemoteException {
		//service.registerCallback(callback);
	}

	public void GOCSDK_unregisterCallback(IGocsdkCallback callback)
			throws RemoteException {
		//service.unregisterCallback(callback);
	}
	@Override
	public void GOCSDK_VolumeUp() throws RemoteException {
		// TODO Auto-generated method stub
		Config.currentVolume ++;
		if(Config.currentVolume > 18)
			Config.currentVolume = 18;
		write(Commands.VOLUME_SETTING + Config.currentVolume);		
	}
	@Override
	public void GOCSDK_VolumeDown() throws RemoteException {
		// TODO Auto-generated method stub
		Config.currentVolume --;
		if(Config.currentVolume < 0)
			Config.currentVolume = 0;
		write(Commands.VOLUME_SETTING + Config.currentVolume);		
	}
	@Override
	public void GOCSDK_GetVolume() throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.VOLUME_SETTING);		
	}
	@Override
	public void GOCSDK_GetVolumeMute() throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.VOLUME_SETTING + 0);		
	}
	@Override
	public void GOCSDK_GetVolumeUnmute() throws RemoteException {
		// TODO Auto-generated method stub
		if(Config.currentVolume == 0)
			Config.currentVolume = 18;
		write(Commands.VOLUME_SETTING + Config.currentVolume);		
	}
	@Override
	public void GOCSDK_SetBTFound() throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.IND_OPEN_BT);	
	}
	@Override
	public void GOCSDK_SetBTUnfound() throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.IND_CLOSE_BT);	
	}
	@Override
	public void GOCSDK_GetMusicStatus() throws RemoteException {
		// TODO Auto-generated method stub
		write(Commands.INQRIRY_AVRCP_STATUS);	
	}
}