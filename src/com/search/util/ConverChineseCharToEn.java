package com.search.util;

import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 获取中文首字母类
 *
 */
public class ConverChineseCharToEn {
	static final String TAG = "ConverChineseCharToEn";

	/**
	 * 
	 * 替换特殊字符
	 * 
	 * @param string
	 * @param s
	 * @return
	 */
	public static boolean numberMatch(String string, String s) {
		// TODO Auto-generated method stub
		if (null == string)
			return false;
		String dealStr = string.replace("-", "");
		dealStr = dealStr.replace(" ", "");
		if (dealStr.contains(s))
			return true;
		return false;
	}

	/**
	 * 
	 * 判断是否数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 
	 * 小写字母转换成大写
	 * @param c
	 * @return
	 */
	public static char conversionHeadUppercase(char c) {
		switch (c) {
		case 'a':
			return 'A';
		case 'b':
			return 'B';
		case 'c':
			return 'C';
		case 'd':
			return 'D';
		case 'e':
			return 'E';
		case 'f':
			return 'F';
		case 'g':
			return 'G';
		case 'h':
			return 'H';
		case 'i':
			return 'I';
		case 'j':
			return 'J';
		case 'k':
			return 'K';
		case 'l':
			return 'L';
		case 'm':
			return 'M';
		case 'n':
			return 'N';
		case 'o':
			return 'O';
		case 'p':
			return 'P';
		case 'q':
			return 'Q';
		case 'r':
			return 'R';
		case 's':
			return 'S';
		case 't':
			return 'T';
		case 'u':
			return 'U';
		case 'v':
			return 'V';
		case 'w':
			return 'W';
		case 'x':
			return 'X';
		case 'y':
			return 'Y';
		case 'z':
			return 'Z';
		default:
			return c;
		}
	}

	/**
	 * 
	 * 将输入的拼音转成数字
	 * 
	 * @param str
	 * @return
	 */
	public static String converEnToNumber(String str) {
		char[] chars = str.toCharArray();
		StringBuffer sbf = new StringBuffer();
		for (char c : chars) {
			sbf.append(getOneNumFromAlpha(c));
		}
		return sbf.toString();
	}

	/**
	 * 
	 * 将字母转换成数字
	 * 
	 * @param firstAlpha
	 * @return
	 */
	public static char getOneNumFromAlpha(char firstAlpha) {
		// TODO Auto-generated method stub
		switch (firstAlpha) {
		case 'a':
		case 'b':
		case 'c':
		case 'A':
		case 'B':
		case 'C':
			return '2';
		case 'd':
		case 'e':
		case 'f':
		case 'D':
		case 'E':
		case 'F':
			return '3';
		case 'g':
		case 'h':
		case 'i':
		case 'G':
		case 'H':
		case 'I':
			return '4';
		case 'j':
		case 'k':
		case 'l':
		case 'J':
		case 'K':
		case 'L':
			return '5';
		case 'm':
		case 'n':
		case 'o':
		case 'M':
		case 'N':
		case 'O':
			return '6';
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
			return '7';
		case 't':
		case 'u':
		case 'v':
		case 'T':
		case 'U':
		case 'V':
			return '8';
		case 'w':
		case 'x':
		case 'y':
		case 'z':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
			return '9';
		default:
			return firstAlpha;
		}
	}

	/**
	 * 
	 * 替换掉中文标点
	 * 
	 * @param chines
	 * @return
	 */
	public static String replaceString(String chines) {
		return chines.replace("《", "").replace("》", "").replace("！", "")
				.replace("￥", "").replace("【", "").replace("】", "")
				.replace("（", "").replace("）", "").replace("－", "")
				.replace("；", "").replace("：", "").replace("”", "")
				.replace("“", "").replace("。", "").replace("，", "")
				.replace("、", "").replace("？", "").replace(" ", "")
				.replace("-", ""); 
	}

	/**
	 * 
	 * 转换输入汉字的全拼(小写) 多音字只反回第一个拼音,拼音首字母大写其它小写
	 * 
	 * @param chines
	 * @return
	 */
	public static String converterToPingYingHeadUppercase(String chines) {
		chines = replaceString(chines);
		StringBuffer pinyinName = new StringBuffer();
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					String[] pys = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
					String headPy = pys == null ? nameChar[i]+"" : pys[0];
					pinyinName.append(conversionHeadUppercase(headPy.charAt(0))+ headPy.substring(1, headPy.length()) + "-");
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName.append(nameChar[i]);
			}
		}
		return pinyinName.toString();
	}

	/**
	 * 
	 * 转换输入汉字的首字母，多个汉字是只反回第一个汉字的首字母
	 * 
	 * @param chinese
	 * @return
	 */
	public static String getFirstLetter(String chines) {
		chines = replaceString(chines);
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		// 如果传入的第一个不是汉字 则取第二个汉字的首字母 否则只反回第个汉字
		if (nameChar[0] > 128) {
			try {
				String[] pys = PinyinHelper.toHanyuPinyinStringArray(nameChar[0],defaultFormat);
				if(pys != null){
					char nchar = pys[0].charAt(0);
					pinyinName += nchar;
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		} else if (nameChar[1] > 128) {
			try {
				String[] pys = PinyinHelper.toHanyuPinyinStringArray(nameChar[1],defaultFormat);
				if(pys != null){
					char nchar = pys[0].charAt(0);
					pinyinName += nchar;
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		} else {
			pinyinName += "#";
		}
		return pinyinName;
	}

	/**
	 * 
	 * 反回输入字符串的所有首字母(大写)并去除非汉字
	 * 
	 * @param chines
	 * @return
	 */
	public static String converterToAllFirstSpellsUppercase(String chines) {
		chines = replaceString(chines);
		StringBuffer pinyinName = new StringBuffer();
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					String[] pys = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
					if(pys != null){
						char nchar = pys[0].charAt(0);
						pinyinName.append(nchar);
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName.append(nameChar[i]);
			}
		}
		return pinyinName.toString();
	}

	/**
	 * 
	 * 反回输入字符串的所有首字母(小写)并去除非汉字
	 * 
	 * @param chines
	 * @return
	 */
	public static String converterToAllFirstSpellsLowercase(String chines) {
		chines = replaceString(chines);
		StringBuffer pinyinName = new StringBuffer();
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					String[] pys = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
					if(pys != null){
						char nchar = pys[0].charAt(0);
						pinyinName.append(nchar);
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName.append(nameChar[i]);
			}
		}
		return pinyinName.toString();
	}

	/**
	 * 首字母是否有英文
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEng(String str) {
		if (str != null && str.length() > 0) {
			return str.charAt(0) >= 0x0000 && str.charAt(0) <= 0x00ff;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		System.out.println(ConverChineseCharToEn.getFirstLetter("婷"));
	}

}
