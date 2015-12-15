package com.tchip.contact;

/**
 * 
 * 联系人和通话记录状态处理
 * @author wwj
 *
 */
public class ContactCallLogStatus{
	
	/**
	 * 联系人状态
	 * 0  默认状态
	 * 1  同步中
	 * 2  同步完成
	 * 3  同步失败
	 * 4  删除中
	 * 5  删除完成
	 * 6  删除失败
	 */
	public static final int contactSyncing = 1;
	public static final int contactSyncSuccessed = 2;
	public static final int contactSyncFailed = 3;
	public static final int contactDeleting = 4;
	public static final int contactDeleteSuccessed = 5;
	public static final int contactDeleteFailed = 6;
	
	public static int ContactStatus = 0;
	
	/**
	 * 是否可以显示联系人
	 * @return
	 */
	public static boolean contactShow(){
		if(ContactStatus == 1 || ContactStatus == 4)
			return false;
		return true;
	}

	
	/**
	 * 是否正在删除联系人
	 * @return
	 */
	public static boolean contactDeleting(){
		if(ContactStatus == 4)
			return true;
		return false;
	}
	
	/**
	 * 是否正在同步联系人
	 * @return
	 */
	public static boolean contactSyncing(){
		if(ContactStatus == 1)
			return true;
		return false;
	}
	
}