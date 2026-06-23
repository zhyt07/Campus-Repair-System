package com.campus.repair.util;

/**
 * 手机号脱敏工具
 */
public class PhoneDesensitizeUtil {

    /**
     * 手机号脱敏：138****5678
     */
    public static String desensitize(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
