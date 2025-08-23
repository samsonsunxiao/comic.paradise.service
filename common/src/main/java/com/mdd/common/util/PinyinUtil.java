package com.mdd.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class PinyinUtil {
    
    // 转换中文为拼音的方法
    public static String convertToPinyin(String chinese) {
        // 配置拼音格式
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE); // 小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 无声调
        format.setVCharType(HanyuPinyinVCharType.WITH_V); // ü 使用 "v"

        StringBuilder pinyinBuilder = new StringBuilder();

        try {
            for (char c : chinese.toCharArray()) {
                // 判断是否为中文字符
                if (Character.toString(c).matches("[\\u4e00-\\u9fa5]")) {
                    // 获取单个汉字的拼音
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null) {
                        // 拼接第一个拼音
                        pinyinBuilder.append(pinyinArray[0]);
                    }
                } else {
                    // 非中文字符直接追加
                    pinyinBuilder.append(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pinyinBuilder.toString();
    }
}
