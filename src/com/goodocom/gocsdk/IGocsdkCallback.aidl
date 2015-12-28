package com.goodocom.gocsdk;
interface IGocsdkCallback{
	void onHfpDisconnected ();
	void onHfpConnected ();
	void onCallSucceed (String number);
	void onIncoming (String number);
	//void onSecondIncoming(String number);
	void onHangUp ();
	void onTalking ();
	void onRingStart ();
	void onRingStop ();
	void onHfpLocal ();
	void onHfpRemote ();
	void onInPairMode ();
	void onExitPairMode ();
	//void onCallHold();
	//void onHoldCurrentAcceptWaiting();
	//void onInMeeting();
	//void onHangUpHoldingWaiting();
	//void onHangUpCurrentAcceptWaiting();
	//void onIncomingName();
	void onOutGoingOrTalkingNumber(String number);
	void onInitSucceed ();
	void onConnecting();
	void onMusicPlaying ();
	void onMusicStopped ();
	//void onVoiceConnected ();
	//void onVoiceDisconnected ();
	void onAutoConnectAccept (boolean autoConnect,boolean autoAccept);
	void onCurrentAddr (String addr);
	void onCurrentName (String name);
	void onHfpStatus (int status);
	void onAvStatus (int status);
	void onVersionDate (String version);
	void onCurrentDeviceName (String name);
	void onCurrentPinCode (String code);
	void onA2dpConnected ();
	void onCurrentAndPairList (int index,String name,String addr);
	void onA2dpDisconnected ();
	void onPhoneBook (String name,String number);
	void onSimBook (String name,String number);
	void onPhoneBookDone ();
	void onSimDone ();
	void onCalllogDone ();
	void onCalllog (int type,String number);
	void onDiscovery (String name,String addr);
	void onDiscoveryDone ();
	void onLocalAddress (String addr);
	void onSppData(int index, String data);
	void onSppConnect(int index);
	void onSppDisconnect(int index);
	void onSppStatus(int status);
	void onOppReceivedFile(String path);
	void onOppPushSuccess();
	void onOppPushFailed();
	void onHidConnected();
	void onHidDisconnected();
	void onHidStatus(int status);
	void onMusicInfo(String MusicName, String artist);
	void onPanConnect();
	void onPanDisconnect();
	void onPanStatus(int status);
	//void onSignalBatteryVal(int signal, int battery);
	void onBTVolume(int volume);
	void onPairBtAddr(String addr);
}