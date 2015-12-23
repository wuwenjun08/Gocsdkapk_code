package com.goodocom.gocsdk;

public class Commands {
	public static final String COMMAND_HEAD = "AT#";
	/**
	 * 配对:::DB[addr:12]
	 */
	public static final String START_PAIR = "DB";
	/**
	 * 进入配对模式:::CA
	 */
	public static final String PAIR_MODE = "CA";
	/**
	 * 取消配对模式:::CB
	 */
	public static final String CANCEL_PAIR_MOD = "CB";
	/**
	 * 连接到HFP:::SC[index:配对记录索引号:1]
	 */
	public static final String CONNECT_HFP = "SC";
	/**
	 * 断开HFP:::SE
	 */
	public static final String DISCONNECT_HFP = "SE";
	/**
	 * 连接设备:::CC[addr:12] hfp+a2dp
	 */
	public static final String CONNECT_DEVICE = "CC";
	/**
	 * 断开设备:::CD hfp+a2dp
	 */
	public static final String DISCONNECT_DEVICE = "CD";
	/**
	 * 接听来电:::CE
	 */
	public static final String ACCEPT_INCOMMING = "CE";
	/**
	 * 拒接来电:::CF
	 */
	public static final String REJECT_INCOMMMING = "CF";
	/**
	 * 结束通话:::CG
	 */
	public static final String FINISH_PHONE = "CG";
	/**
	 * 重拨:::CH
	 */
	public static final String REDIAL = "CH";
	/**
	 * 语音拨号:::CI
	 */
	public static final String VOICE_DIAL = "CI";
	/**
	 * 取消语音拨号:::CJ
	 */
	public static final String CANCEL_VOID_DIAL = "CJ";
	/**
	 * 音量增加:::CK
	 */
	public static final String VOLUME_UP = "CK";
	/**
	 * 音量减少:::CL
	 */
	public static final String VOLUME_DOWN = "CL";
	/**
	 * 麦克风打开 关闭:::CM
	 */
	public static final String MIC_OPEN_CLOSE = "CM";
	/**
	 * 语音切换到手机:::TF
	 */
	public static final String VOICE_TO_PHONE = "CN";
	/**
	 * 语音切换到蓝牙:::CP
	 */
	public static final String VOICE_TO_BLUE = "CP";
	/**
	 * 语音在蓝也和手机之间切换:::CO
	 */
	public static final String VOICE_TRANSFER = "CO";
	/**
	 * 挂断等待来电
	 */
	public static final String HANG_UP_WAIT_PHONE = "CQ";
	/**
	 * 挂断当前通话,接听等待来电
	 */
	public static final String HANG_UP_CURRENT_ACCEPT_WAIT = "CR";
	/**
	 * 保持当前通话接听等待来电
	 */
	public static final String HOLD_CURRENT_ACCEPT_WAIT = "CS";
	/**
	 * 会议电话
	 */
	public static final String MEETING_PHONE = "CT";
	/**
	 * 删除配对记录:::CV
	 */
	public static final String DELETE_PAIR_LIST = "CV";
	/**
	 * 拨打电话:::CZ[number]
	 */
	public static final String DIAL = "CW";
	/**
	 * 拨打分机号:::CX[DTMF:1]
	 */
	public static final String DTMF = "CX";
	/**
	 * 查询HFP状态:::CY
	 */
	public static final String INQUIRY_HFP_STATUS = "CY";
	/**
	 * 复位蓝牙模块:::CZ
	 */
	public static final String RESET_BLUE = "CZ";
	/**
	 * 连接A2Dp:::DC[index:配对记录索引号:1]
	 */
	public static final String CONNECT_A2DP = "DC";
	/**
	 * 断开A2DP:::DA
	 */
	public static final String DISCONNECT_A2DP = "DA";
	/**
	 * 播放,暂停音乐:::MA
	 */
	public static final String PLAY_PAUSE_MUSIC = "MA";
	/**
	 * 暂停音乐:::MB
	 */
	public static final String PAUSE_MUSIC = "MB";
	/**
	 * 停止音乐:::MC
	 */
	public static final String STOP_MUSIC = "MC";
	/**
	 * 下一曲:::MD
	 */
	public static final String NEXT_SOUND = "MD";
	/**
	 * 上一曲:::ME
	 */
	public static final String PREV_SOUND = "ME";
	/**
	 * 查询自动接听和上电自动连接配置:::MF
	 */
	public static final String INQUIRY_AUTO_CONNECT_ACCETP = "MF";
	/**
	 * 设置上电自动连接:::MG
	 */
	public static final String SET_AUTO_CONNECT_ON_POWER = "MG";
	/**
	 * 取消上电自动连接:::MH
	 */
	public static final String UNSET_AUTO_CONNECT_ON_POWER = "MH";
	/**
	 * 连接最后一个AV设备:::MI
	 */
	public static final String CONNECT_LAST_AV_DEVICE = "MI";
	/**
	 * 更改LOCAL Name:::MM[name]
	 */
	public static final String MODIFY_LOCAL_NAME = "MM";
	/**
	 * 更改PIN Code:::MN[code]
	 */
	public static final String MODIFY_PIN_CODE = "MN";
	/**
	 * 查询AVRCP状态:::MO
	 */
	public static final String INQRIRY_AVRCP_STATUS = "MO";
	/**
	 * 设定自动接听:::MP
	 */
	public static final String SET_AUTO_ANSWER = "MP";
	/**
	 * 取消自动接听:::MQ
	 */
	public static final String UNSET_AUTO_ANSWER = "MQ";
	/**
	 * 快进:::MQ
	 */
	public static final String FAST_FORWARD = "MR1";
	/**
	 * 停止快进:::MS
	 */
	public static final String STOP_FAST_FORWARD = "MR0";
	/**
	 * 快退:::MT
	 */
	public static final String FAST_BACK = "MT1";
	/**
	 * 停止快退:::MU
	 */
	public static final String STOP_FAST_BACK = "MT0";
	/**
	 * 查询A2DP状态:::MV
	 */
	public static final String INQUIRY_A2DP_STATUS = "MV";
	/**
	 * 查询配对记录:::MX
	 */
	public static final String INQUIRY_PAIR_RECORD = "MX";
	/**
	 * 查询版本日期:::MY
	 */
	public static final String INQUIRY_VERSION_DATE = "MY";
	/**
	 * 读取SIM电话本:::PA
	 */
	public static final String SET_SIM_PHONE_BOOK = "PA";
	/**
	 * 读取手机电话本:::PB
	 */
	public static final String SET_PHONE_PHONE_BOOK = "PB";
	/**
	 * 读取拨通话记录:::PH
	 */
	public static final String SET_OUT_GOING_CALLLOG = "PH";
	/**
	 * 读取已接通话记录:::PI
	 */
	public static final String SET_INCOMING_CALLLOG = "PI";
	/**
	 * 读取未接通话记录:::PJ
	 */
	public static final String SET_MISSED_CALLLOG = "PJ";
	/**
	 * 开始查找设备:::SD
	 */
	public static final String START_DISCOVERY = "SD";
	/**
	 * 停止查找设备:::ST
	 */
	public static final String STOP_DISCOVERY = "ST";
	/**
	 * 禁止蓝牙音乐:::VA
	 */
	public static final String MUSIC_MUTE = "VA";
	/**
	 * 启用蓝牙音乐:::VB
	 */
	public static final String MUSIC_UNMUTE = "VB";
	/**
	 * 蓝牙音乐作为背景音，音量减半:::VC
	 */
	public static final String MUSIC_BACKGROUND = "VC";
	/**
	 * 正常播放:::VD
	 */
	public static final String MUSIC_NORMAL = "VD";
	/**
	 * 本机蓝牙地址:::VE
	 */
	public static final String LOCAL_ADDRESS = "VE";
	/**
	 * 通过OPP发送文件给手机:::OS[path]
	 */
	public static final String OPP_SEND_FILE = "OS";
	/**
	 * 连接SPP:::SP[addr:12]
	 */
	public static final String CONNECT_SPP_ADDRESS = "SP";
	/**
	 * 发送spp数据:::SG[index:1][data]
	 */
	public static final String SPP_SEND_DATA = "SG";
	/**
	 * 断开spp:::SH[index:1]
	 */
	public static final String SPP_DISCONNECT = "SH";
	/**
	 * 查询a2dp播放状态:::VI
	 */
	public static final String INQUIRY_PLAY_STATUS = "VI";
	/**
	 * 连接hid:::HC[addr:12]
	 */
	public static final String CONNECT_HID = "HC";
	/**
	 * 连接最后一个设备的HID:::HE
	 */
	public static final String CONNECT_HID_LAST = "HE";
	/**
	 * 断开hid:::HD
	 */
	public static final String DISCONNECT_HID = "HD";
	/**
	 * 发送hid按键:::HK[key] key:HOME,MENU,BACK,A...Z
	 */
	public static final String SEND_KEY = "HK";
	/**
	 * hid按键按下:::HH[key]
	 */
	public static final String SEND_KEY_DOWN = "HKD";
	/**
	 * hid按键弹起:::HI[key]
	 */
	public static final String SEND_KEY_UP = "HKU";
	/**
	 * 发送鼠标移动:::HF[key:1][x:4][y:4] 0000,9999
	 */
	public static final String MOUSE_MOVE = "HF";
	/**
	 * 发送home键值:::HH
	 */
	public static final String MOUSE_HOME = "HH";
	/**
	 * 发送back键值:::HI
	 */
	public static final String MOUSE_BACK = "HI";
	/**
	 * 发送menu键值:::HG
	 */
	public static final String MOUSE_MENU = "HG";
	/**
	 * 发送鼠标点击:::HL
	 */
	public static final String SEND_TOUCH_DOWN = "HQ";
	/**
	 * 发送触摸屏弹起:::HR[x:5][y:5] +0000,+8195
	 */
	public static final String SEND_TOUCH_MOVE = "HR";
	/**
	 * 发送触摸屏移动:::HS[x:5][y:5] +0000,+8195
	 */
	public static final String SEND_TOUCH_UP = "HS";
	/**
	 * 查询A2dp信息指令:::MK
	 */
	public static final String INQUIRY_MUSIC_INFO = "MK";
	/**
	 * 向下读取N个条目(电话本):::PC
	 */
	public static final String READ_NEXT_PHONEBOOK_COUNT = "PC";
	/**
	 * 向下读取N个条目(电话本):::PD
	 */
	public static final String READ_LAST_PHONEBOOK_COUNT = "PD";
	/**
	 * 读取全部条目(电话本):::PX
	 */
	public static final String READ_ALL_PHONEBOOK = "PX";
	/**
	 * 停止下载(电话本):::PS
	 */
	public static final String STOP_PHONEBOOK_DOWN = "PS";
	/**
	 * 暂停下载(电话本):::PO
	 */
	public static final String PAUSE_PHONEBOOK_DOWN = "PO";
	/**
	 * 继续下载(电话本):::PQ
	 */
	public static final String PLAY_PHONEBOOK_DOWN = "PQ";
	/**
	 * 查询HID状态:::HY
	 */
	public static final String INQUIRY_HID_STATUS = "HY";
	/**
	 * 设置车机触摸屏分辨率:::HJ
	 */
	public static final String SET_TOUCH_RESOLUTION = "HJ";
	/**
	 * 触摸屏校屏指令:::HP
	 */
	public static final String HID_ADJUST = "HP";
	/**
	 * Pan连接:::NC
	 */
	public static final String PAN_CONNECT = "NC";
	/**
	 * PAN断开连接:::ND
	 */
	public static final String PAN_DISCONNECT = "ND";
	/**
	 * 查询PAN状态:::NY
	 */
	public static final String INQUIRY_PAN_STATUS = "NY";
	/**
	 * 查询本地蓝牙地址:::DF
	 */
	public static final String INQUIRY_DB_ADDR = "DF";
	/**
	 * 打开蓝牙:::P1
	 */
	public static final String OPEN_BT = "P1";
	/**
	 * 关闭蓝牙:::P0
	 */
	public static final String CLOSE_BT = "P0";
	/**
	 * 查询当前连接设备的蓝牙地址:::QA
	 */
	public static final String INQUIRY_CUR_BT_ADDR = "QA";
	/**
	 * 查询当前连接设备的蓝牙名字:::QB
	 */
	public static final String INQUIRY_CUR_BT_NAME = "QB";
	/**
	 * 查询SPK及MIC音量:::QC
	 */
	public static final String INQUIRY_SPK_MIC_VAL = "QC";
	/**
	 * 查询电池/信号量:::QD
	 */
	public static final String INQUIRY_SIGNEL_BATTERY_VAL = "QD";
	/**
	 * 查询SPP状态:::SY
	 */
	public static final String INQUIRY_SPP_STATUS = "SY";
	/**
	 * 设置蓝牙音乐音量:::VF
	 */
	public static final String MUSIC_VOL_SET = "VF";
	/**
	 * Pskey升级:::UP
	 */
	public static final String UPDATA_PSKEY = "UP";
	
	
	public static final String IND_HEAD = "\r\n";
	/**
	 * HFP已断开:::IA
	 */
	public static final String IND_HFP_DISCONNECTED = "IA";
	/**
	 * HFP已连接:::IB
	 */
	public static final String IND_HFP_CONNECTED = "IB";
	/**
	 * 去电:::IC[numberlen:2][number]
	 */
	public static final String IND_CALL_SUCCEED = "IC";
	/**
	 * 来电:::ID[numberlen:2][number]
	 */
	public static final String IND_INCOMING = "ID";
	/**
	 * 通话中的来电::IE[numberlen:2][number]
	 */
	public static final String IND_SECOND_INCOMING = "IE";
	/**
	 * 挂机:::IF[numberlen:2][number]
	 */
	public static final String IND_HANG_UP = "IF";
	/**
	 * 通话中:::IG[numberlen:2][number]
	 */
	public static final String IND_TALKING = "IG";
	/**
	 * 本地来电铃声开始:::VR1
	 */
	public static final String IND_RING_START = "VR1";
	/**
	 * 本地来电铃声结束:::VR0
	 */
	public static final String IND_RING_STOP = "VR0";
	/**
	 * 手机接听
	 */
	public static final String IND_HF_LOCAL = "T1";
	/**
	 * 蓝牙接听
	 */
	public static final String IND_HF_REMOTE = "T0";
	/**
	 * 进入配对模式:::II
	 */
	public static final String IND_IN_PAIR_MODE = "II";
	/**
	 * 退出配对模式:::IJ
	 */
	public static final String IND_EXIT_PAIR_MODE = "IJ";
	/**
	 * 呼叫等待
	 */
	public static final String IND_CALL_HOLD = "JK";
	/**
	 * 保持当前通话,接听等待中的电话
	 */
	public static final String IND_HOLD_CURRENT_ACCEPT_WAITING = "IL";
	/**
	 * 进入电话会议
	 */
	public static final String IND_IN_MEETING = "IM";
	/**
	 * 挂断保持中,等待中的电话
	 */
	public static final String IND_HANG_UP_HOLDING_WAITING = "IN";
	/**
	 * 来电名字显示
	 */
	public static final String IND_INCOMING_NAME = "IQ";
	/**
	 * 打出电话或通话中号码
	 */
	public static final String IND_OUTGOING_TALKING_NUMBER = "IR";
	/**
	 * 上电初始化成功:::IS
	 */
	public static final String IND_INIT_SUCCEED = "IS";
	/**
	 * 挂断当前通话,接听等待中的电话
	 */
	public static final String IND_HANG_UP_CURRENT_ACCEPT_WAITING = "IT";
	/**
	 * 连接中
	 */
	public static final String IND_CONNECTING = "IV";
	/**
	 * 音乐 播放中:::MB
	 */
	public static final String IND_MUSIC_PLAYING = "MB";
	/**
	 * 音乐停止
	 */
	public static final String IND_MUSIC_STOPPED = "MA";
	/**
	 * 语音连接建立
	 */
	public static final String IND_VOICE_CONNECTED = "MC";
	/**
	 * 语音连接断开
	 */
	public static final String IND_VOICE_DISCONNECTED = "MD";
	/**
	 * 开机自动连接,来电自动接听当前配置:::MF[auto_connect:1][auto_answer:1]
	 */
	public static final String IND_AUTO_CONNECT_ACCEPT = "MF";
	/**
	 * 当前连接设备地址:::JH[addr:12]
	 */
	public static final String IND_CURRENT_ADDR = "JH";
	/**
	 * 当前连接设备名称:::SA[name]
	 */
	public static final String IND_CURRENT_NAME = "SA";
	/**
	 * 当前HFP和a2dp状态:::S[hf_state:1][av_state:1] 1:未连接 3:已连接 4：电话拨出 5：电话打入 6：通话中
	 */
	public static final String IND_HFP_STATUS = "MG";
	/**
	 * 当前HFP和a2dp状态:::S[hf_state:1][av_state:1] 1:未连接 3:已连接
	 */
	public static final String IND_AV_STATUS = "MU";
	/**
	 * 当前版本号
	 */
	public static final String IND_VERSION_DATE = "MW";
	/**
	 * 当前AVRCP状态
	 */
	public static final String IND_AVRCP_STATUS = "ML";
	/**
	 * 当前设备名称:::MM[name]
	 */
	public static final String IND_CURRENT_DEVICE_NAME = "MM";
	/**
	 * 当前配对密码:::MN[code]
	 */
	public static final String IND_CURRENT_PIN_CODE = "MN";
	/**
	 * A2DP connected
	 */
	public static final String IND_A2DP_CONNECTED = "MA";
	/**
	 * 当前已连接设备地址及名字
	 */
	public static final String IND_CURRENT_ADDR_PAIR = "MX0";
	/**
	 * 当前设备名称和配对记录
	 */
	public static final String IND_CURRENT_AND_PAIR_LIST = "MX";
	/**
	 * A2DP已断开
	 */
	public static final String IND_A2DP_DISCONNECTED = "MY";
	/**
	 * 设定电话本状态
	 */
	public static final String IND_SET_PHONE_BOOK = "PA";
	/**
	 * 电话本记录显示:::PB[namelen:2][numlen:2][name][number]
	 */
	public static final String IND_PHONE_BOOK = "PB";
	/**
	 * 电话本记录显示:::PF[namelen:2][numlen:2][name][number]
	 */
	public static final String IND_SIM_BOOK = "PF";
	/**
	 * 下载电话本结束:::PC
	 */
	public static final String IND_PHONE_BOOK_DONE = "PC";
	/**
	 * SIM卡结束
	 */
	public static final String IND_SIM_DONE = "PE";
	/**
	 * 下载通话记录结束:::PE
	 */
	public static final String IND_CALLLOG_DONE = "PE";
	/**
	 * 通话记录显示:::PD[type:1][number]
	 */
	public static final String IND_CALLLOG = "PD";
	/**
	 * 查找到的设备:::SF[addr:12][name]
	 */
	public static final String IND_DISCOVERY = "SF";
	/**
	 * 查找结束:::SH
	 */
	public static final String IND_DISCOVERY_DONE = "SH";
	/**
	 * 本机蓝牙地址:::DB[addr:12]
	 */
	public static final String IND_LOCAL_ADDRESS = "DB";
	/**
	 * spp数据:::SI[index:1][data]
	 */
	public static final String IND_SPP_DATA = "SI";
	/**
	 * spp连接:::SV[index:1]
	 */
	public static final String IND_SPP_CONNECT = "SV";
	/**
	 * spp断开:::SS[index:1]
	 */
	public static final String IND_SPP_DISCONNECT = "SS";
	/**
	 * OPP收到文件
	 */
	public static final String IND_OPP_RECEIVED_FILE = "OR";
	/**
	 * OPP发送文件成功
	 */
	public static final String IND_OPP_PUSH_SUCCEED = "OC";
	/**
	 * OPP发送文件失败
	 */
	public static final String IND_OPP_PUSH_FAILED = "OF";
	/**
	 * hid连接成功
	 */
	public static final String IND_HID_CONNECTED = "HB";
	/**
	 * hid断开连接
	 */
	public static final String IND_HID_DISCONNECTED = "HA";
	/**
	 * hid状态
	 */
	public static final String IND_HID_STATUS = "HS";
	/**
	 * hid校屏
	 */
	public static final String IND_HID_ADJUST = "HP";
	/**
	 * Mic当前是否打开
	 */
	public static final String IND_MIC_STATUS = "IO";
	/**
	 * 当前SPK, MIC值
	 */
	public static final String IND_SPK_MIC_VAL = "KI";
	/**
	 * 当前播放歌曲信息
	 */
	public static final String IND_MUSIC_INFO = "MI";
	/**
	 * SPP状态
	 */
	public static final String IND_SPP_STATUS = "SR";	
	/**
	 * PAN已连接
	 */
	public static final String IND_PAN_CONNECT = "NC";
	/**
	 * PAN已断开
	 */
	public static final String IND_PAN_DISCONNECT = "NA";
	/**
	 * PAN状态
	 */
	public static final String IND_PAN_STATUS = "NS";
	/**
	 * 手机信号,电池电量值
	 */
	public static final String IND_SIGNAL_BATTERY_VAL = "PS";	
	/**
	 * 配对状态
	 */
	public static final String IND_PAIR_STATE = "P";
	/**
	 * PSKEY升级完成
	 */
	public static final String IND_UPDATA_SUCCESS = "US";
	/**
	 * 声音设置
	 */
	public static final String VOLUME_SETTING = "VS";
	/**
	 * 获取声音
	 */
	public static final String IND_CURRENT_VOLUME = "VO";

	/**
	 * 蓝牙不配发现，配对
	 */
	public static final String IND_OPEN_BT = "P1";
	/**
	 * 获取声音
	 */
	public static final String IND_CLOSE_BT = "P0";
	
	public static final String BT_STATUS = "BT";
	
}
