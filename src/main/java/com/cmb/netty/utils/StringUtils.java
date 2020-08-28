package com.cmb.netty.utils;

public class StringUtils {
    public static boolean isBlank(CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isBlank(cs);
    }

    public static boolean isNotBlank(CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isNotBlank(cs);
    }

    public static String[] slit(String str, String separatorChars) {
        return org.apache.commons.lang3.StringUtils.split(str, separatorChars);
    }

    public static String[] slit(String str, char separatorChar) {
        return org.apache.commons.lang3.StringUtils.split(str, separatorChar);
    }
}
