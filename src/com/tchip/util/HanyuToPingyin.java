package com.tchip.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 汉字转换位汉语拼音，英文字符不变
 * @author wu
 *
 */
public class HanyuToPingyin {
 
    /**
    * 汉字转换位汉语拼音首字母，英文字符不变
    * @param chines 汉字
    * @return 拼音
    */
    public static String converterToFirstSpell(String chines){  
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                	String[] result = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                	if(result == null)
                		break;
                    pinyinName += result[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
            	pinyinName += nameChar[i];
            }
        }
        pinyinName = pinyinName.toLowerCase();
        return pinyinName;
    }
 
    /**
    * 汉字转换位汉语拼音，英文字符不变
    * @param chines 汉字
    * @return 拼音
    */
    public static String converterToSpell(String chines){    	 
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
            	pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }
    
    public static void main(String[] args) {
		System.out.println(converterToFirstSpell("aaaaaaaaaaaaaaa"));
	}
}