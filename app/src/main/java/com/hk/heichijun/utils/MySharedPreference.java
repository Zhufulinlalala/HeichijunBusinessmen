package com.hk.heichijun.utils;

/**
 * Created by Administrator on 2016/10/31 0031.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.hk.heichijun.base.SaflyApplication;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class MySharedPreference {
    public static void saveUserId(Object s){
        save("userId",s);
    }
    public static void saveToken(Object s){
        save("token",s);
    }
    public static void savePhone(Object s){
        save("phone",s);
    }
    public static void saveCinemaId(Object s){
        save("shopId",s);
    }
    public static void saveUserName(Object s){
        save("username",s);
    }
    public static void saveLevel(Object s){
        save("level",s);
    }
    public static String getUserName(){
        return get1("username","", SaflyApplication.getInstance());
    }
    public static String getAdminId(){
        return get1("userId","", SaflyApplication.getInstance());
    }
    public static String getToken(){
        return get1("token","", SaflyApplication.getInstance());
    }
    public static String getPhone(){
        return get1("phone","", SaflyApplication.getInstance());
    }
    public static String getCinemaId(){
        return get1("shopId","", SaflyApplication.getInstance());
    }
    public static void save(String name, Object value) {
        SharedPreferences mySharedPreferences = SaflyApplication.getInstance().getSharedPreferences(
                "tzzhufang", Activity.MODE_PRIVATE);
        Editor editor = mySharedPreferences.edit();
        editor.putString(name, value.toString());
        editor.commit();
    }
    public static void save3(String name, Object value) {
        SharedPreferences mySharedPreferences = SaflyApplication.getInstance().getSharedPreferences(
                "tzzhufang3", Activity.MODE_PRIVATE);
        Editor editor = mySharedPreferences.edit();
        editor.putString(name, value.toString());
        editor.commit();
    }
    public static void save1(String name, String value, Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                "tzzhufang", Activity.MODE_PRIVATE);
        // ʵ����SharedPreferences.Editor���󣨵ڶ�����
        Editor editor = mySharedPreferences.edit();
        // ��putString�ķ�����������
        editor.putString(name, value);
        // �ύ��ǰ����
        editor.commit();
    }
    public static void remove3(String name) {
        SharedPreferences mySharedPreferences = SaflyApplication.getInstance().getSharedPreferences(
                "tzzhufang3", Activity.MODE_PRIVATE);
        // ʵ����SharedPreferences.Editor���󣨵ڶ�����
        Editor editor = mySharedPreferences.edit();
        // ��putString�ķ�����������
        editor.remove(name);
        // �ύ��ǰ����
        editor.commit();
    }
    public static void remove(String name) {
        SharedPreferences mySharedPreferences = SaflyApplication.getInstance().getSharedPreferences(
                "tzzhufang", Activity.MODE_PRIVATE);
        // ʵ����SharedPreferences.Editor���󣨵ڶ�����
        Editor editor = mySharedPreferences.edit();
        // ��putString�ķ�����������
        editor.remove(name);
        // �ύ��ǰ����
        editor.commit();
    }
    // ����ͼƬ�洢
    public static void saveBitmapToSharedPreferences(String key, Bitmap bitmap,
                                                     Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                "text", Activity.MODE_PRIVATE);
        // ��һ��:��Bitmapѹ�����ֽ����������ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 80, byteArrayOutputStream);
        // �ڶ���:����Base64���ֽ�����������е�����ת�����ַ���String
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageString = new String(Base64.encodeToString(byteArray,
                Base64.DEFAULT));
        // ������:��String������SharedPreferences
        Editor editor = mySharedPreferences.edit();
        editor.putString(key, imageString);
        editor.commit();
    }
    // ����ȡͼƬ
    public static Bitmap getBitmapFromSharedPreferences(String key, String def,
                                                        Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                "text", Activity.MODE_PRIVATE);
        // ��һ��:ȡ���ַ�����ʽ��Bitmap
        String imageString = mySharedPreferences.getString(key, def);
        // �ڶ���:����Base64���ַ���ת��ΪByteArrayInputStream
        byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArray);
        // ������:����ByteArrayInputStream����Bitmap
        Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
        return bitmap;
    }
    public static String get(String name) {
        SharedPreferences mySharedPreferences = SaflyApplication.getInstance().getSharedPreferences(
                "tzzhufang", Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(name, "");
    }
    public static String get(String name, String defvalue, Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                "tzzhufang", Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(name, defvalue);
    }
    public static String get1(String name, String defvalue, Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                "tzzhufang", Activity.MODE_PRIVATE);
        return mySharedPreferences.getString(name, defvalue);
    }
    public static void clear() {
        SharedPreferences mySharedPreferences = SaflyApplication.getInstance().getSharedPreferences(
                "tzzhufang", Activity.MODE_PRIVATE);
        Editor editor = mySharedPreferences.edit();
        editor.remove("id");
        editor.clear();
        editor.commit();
    }
}
