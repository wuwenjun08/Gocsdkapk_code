package com.goodocom.gocsdk;

import com.goodocom.gocsdk.IGocsdkCallback;

interface IGocsdkService {
	/*注册回调*/
	void GOCSDK_registerCallback(IGocsdkCallback callback);
	/*注销回调*/
	void GOCSDK_unregisterCallback(IGocsdkCallback callback);
	/*取得本地蓝牙名字*/
	void GOCSDK_getLocalName();
	/*设置本地蓝牙名字*/
	void GOCSDK_setLocalName(String name);
	/*取得本地蓝牙密码*/
	void GOCSDK_getPinCode();
	/*设置本地蓝牙密码*/
	void GOCSDK_setPinCode(String pincode);
	/*连接最后个已配对设备*/
	void GOCSDK_connectLast();
	/*连接指定蓝牙地址a2dp设备*/
	void GOCSDK_connectA2dp(String addr);
	/*连接指定蓝牙地址hfp设备*/
	void GOCSDK_connectHFP(String addr);
	/*断开a2dp & hfp连接*/
	void GOCSDK_disconnect();
	/*断开a2dp连接*/
	void GOCSDK_disconnectA2DP();
	/*断开hfp连接*/
	void GOCSDK_disconnectHFP();
	/*删除指定已配对蓝牙设备, isEmpty删除所有蓝牙设备*/
	void GOCSDK_deletePair(String addr);
	/*开始搜索设备*/
	void GOCSDK_startDiscovery();
	/*取得配对列表*/
	void GOCSDK_getPairList();
	/*停止搜索设备*/
	void GOCSDK_stopDiscovery();
	/*接通电话*/
	void GOCSDK_phoneAnswer();
	/*挂断电话/拒接电话*/
	void GOCSDK_phoneHangUp();
	/*拨打电话*/
	void GOCSDK_phoneDail(String phonenum);
	/*拨打分机号*/
	void GOCSDK_phoneTransmitDTMFCode(char code);
	/*语音切换到蓝牙*/
	void GOCSDK_phoneTransfer();
	/*语音切换到手机*/
	void GOCSDK_phoneTransferBack();
	/*读取手机电话本*/
	void GOCSDK_phoneBookStartUpdate();
	/*
	       读取通话记录  
	  3 = 已拨号码
	  4 = 未接号码
	  5 = 已接号码
	*/
	void GOCSDK_callLogstartUpdate(int type);
	/*播放/暂停音乐*/
	void GOCSDK_musicPlayOrPause();
	/*停止音乐*/
	void GOCSDK_musicStop();
	/*上一曲*/
	void GOCSDK_musicPrevious();
	/*下一曲*/
	void GOCSDK_musicNext();
	/*
	1 = 进入配对
	2 = 取消配对
	*/
	void GOCSDK_PairMode(int type);
	/*重拨号码*/
	void GOCSDK_ReDail();
	/*语言拨号*/
	//void GOCSDK_VoiceDial();
	/*取消语音拨号*/
	//void CancelVoiceDial();
	/*
		SPK+MIC音量调试
		volume[0] = spk
		volume[1] = mic
	*/
	//void GOCSDK_VolumeSet(string volume);
	/*麦克打开(1)/关闭(0)*/
	void GOCSDK_MicSwitch(char type);
	/*查询hfp状态*/
	void GOCSDK_InquiryHfpStatus();
	/*复位蓝牙模块*/
	void GOCSDK_ResetBluetooth();
	/*查询自动接听和上电自动连接配置*/
	void GOCSDK_InquiryAutoConnectAccept();
	/*设置上电自动连接('1')/取消自动连接('0')*/
	void GOCSDK_SetAutoConnect(char type);
	/*设置自动接听('1')/取消自动接听('0')*/
	void GOCSDK_SetAutoAccept(char type);
	/*查询A2DP状态*/
	void GOCSDK_InquiryA2dpStatus();
	/*查询版本日期*/
	void GOCSDK_InquiryVersion();
	/*启动('1')/禁止('0')蓝牙音乐*/
	void GOCSDK_MuteOrUnmuteA2dp(char type);
	/*恢复('1')/减半('0')蓝牙音乐*/
	void GOCSDK_UnmuteOrHalfA2dp(char type);
	/*通过OPP发送文件给手机*/
	void GOCSDK_OppSendFile(String path);
	/*连接spp,isEmpty为连接当前spp*/
	void GOCSDK_SppConnect(String addr);
	/*发送spp数据
	  data[0] = spp index
	*/
	void GOCSDK_SppSendData(String data);
	/*断开spp*/
	void GOCSDK_SppDisConnect(char index);
	/*查询spp状态*/
	void GOCSDK_InquirySppStatus();
	/*连接Hid, isEmpty为连接当前的hid*/
	void GOCSDK_HidConnect(String addr);
	/*Hid 鼠标移动
	  data[0] = key(1 = down 0 = up)
	  data[1]-data[4] = x
	  data[5]-data[8] = y
	*/
	void GOCSDK_HidMouseMove(String data);
	/*HID home按键*/
	void GOCSDK_HidHomeKey();
	/*HID back按键*/
	void GOCSDK_HidBackKey();
	/*HID menu按键*/
	void GOCSDK_HidMenuKey();
	/*断开Hid*/
	void GOCSDK_HidDisConnect();
	/*查询hid状态*/
	void GOCSDK_InquiryHidStatus();
	/*设置车机可投射区域分辨率
	  data[0]-data[3] = x
	  data[4]-data[7] = y
	*/
	void GOCSDK_SetHidResolution(String data);
	/*强制暂停音乐*/
	void GOCSDK_PauseMusic();
	/*pan 连接, isEmpty为连接当前pan*/
	void GOCSDK_PanConnect(String addr);
	/*断开 pan*/
	void GOCSDK_PanDisConnect();
	/*查询pan状态*/
	void GOCSDK_InquiryPanStatus();
	/*查询本地蓝牙地址*/
	void GOCSDK_InquiryLocalAddr();
	/*打开蓝牙设备*/
	void GOCSDK_OpenBt();
	/*关闭蓝牙设备*/
	void GOCSDK_CloseBt();
	/*查询当前连接设备地址*/
	void GOCSDK_InquiryCurBtAddr();
	/*查询当前连接设备的蓝牙名*/
	void GOCSDK_InquiryCurBtName();
	/*查询spk/mic音量值*/
	//void GOCSDK_InquirySpkMicVal();
	/*查询电池/信号量*/
	void GOCSDK_InquirySignelBatteryVal();
	/*设置蓝牙音乐音量 0 - 20*/
	void GOCSDK_SetMusicVal(int val);
	/*查询蓝牙音乐信息*/
	void GOCSDK_InquiryMusicInfo();
	/*BC6 蓝牙pskey升级*/
	//void GOCSDK_UpdataPskey();
	
	
	/*蓝牙音量增加*/
	void GOCSDK_VolumeUp();
	/*蓝牙音量减小*/
	void GOCSDK_VolumeDown();
	/*获取蓝牙音量*/
	void GOCSDK_GetVolume();
	/*获取蓝牙音量静音*/
	void GOCSDK_GetVolumeMute();
	/*获取蓝牙音量非静音*/
	void GOCSDK_GetVolumeUnmute();
	/*蓝牙被发现被连接*/
	void GOCSDK_SetBTFound();
	/*蓝牙不被发现不被连接*/
	void GOCSDK_SetBTUnfound();
	
	/*查询音乐播放状态*/
	void GOCSDK_GetMusicStatus();
	
}	