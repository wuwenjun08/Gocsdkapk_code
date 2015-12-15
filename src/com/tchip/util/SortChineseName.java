package com.tchip.util;

import java.text.Collator;
import java.util.Comparator;

import com.tchip.contact.Contact;

/**
 * @Title：             SortChineseName.java
 * @Description:    中文字符排序
 * @Function:       中文字符排序
 * @Copyright:      Copyright (c) 2012-11-19
 * @Version         0.1
 */
public class SortChineseName implements Comparator<Contact>{
	Collator cmp = Collator.getInstance(java.util.Locale.CHINA);
	@Override
	public int compare(Contact o1, Contact o2) {
		if (cmp.compare(o1.getName(), o2.getName())>0){
			return 1;
		}else if (cmp.compare(o1.getName(), o2.getName())<0){
			return -1;
		}
		return 0;
	}
}
