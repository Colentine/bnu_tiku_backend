package com.ht.bnu_tiku_backend.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinYinTool {

    public static String getInitials(String chinese) {
        StringBuilder sb = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyins != null && pinyins.length > 0) {
                sb.append(pinyins[0].charAt(0));
            } else {
                sb.append(c); // 保留非汉字字符
            }
        }
        return sb.toString().toLowerCase();
    }

}
