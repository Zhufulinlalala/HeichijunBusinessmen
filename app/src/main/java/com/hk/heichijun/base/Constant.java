package com.hk.heichijun.base;

/**
 * Created by Administrator on 2016/11/11 0011.
 */

public class Constant {
    //TODO  修改常量值
    public static final String URL="https://bp.weshine.vip:8080/";
   // public static final String URL="http://k17566x367.imwork.net:58209/";
    //public static final String URL="https://weshine.vip:8080/";
    public static final String BUGLY_APPID="7fe709fcdc";
    public static final String REG_MONEY="(^[1-9](\\d+)?(\\.\\d{1,2})?$)|(^(0){1}$)|(^\\d\\.\\d{1,2}?$)";  //金额正则
    public static final String REG_URL="[a-zA-z]+://[^\\s]*";  //网址正则
    public static final String REGEX_PHONE =  "^((13[0-9])|(14[5,7,9])|(15[^4])|(17[^4^9])|(18[0-9]))\\d{8}$";//手机号正则
    public static final String REG_EMAIL="[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";  //邮箱正则
}
