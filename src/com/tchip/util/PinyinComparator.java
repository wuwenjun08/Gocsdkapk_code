package com.tchip.util;

import java.util.Comparator;

import com.tchip.contact.Contact;

/**
 * 
 * 中英文混排
 * @author wwj
 *
 */
public class PinyinComparator implements Comparator<Contact> {

	public int compare(Contact s1, Contact s2) {
		String o1 = HanyuToPingyin.converterToFirstSpell(s1.getName());
		String o2 = HanyuToPingyin.converterToFirstSpell(s2.getName());

		for (int i = 0; i < o1.length() && i < o2.length(); i++) 
		{
			int codePoint1 = o1.charAt(i);
            int codePoint2 = o2.charAt(i);

			if (Character.isSupplementaryCodePoint(codePoint1)
					|| Character.isSupplementaryCodePoint(codePoint2)) {
				i++;
			}

			if (codePoint1 != codePoint2) {
				if (Character.isSupplementaryCodePoint(codePoint1)
						|| Character.isSupplementaryCodePoint(codePoint2))

				{
					return codePoint1 - codePoint2;
				}
				return codePoint1 - codePoint2;
			}
		}
		return o1.length() - o2.length();

	}
}