package com.hk.lang_data_manager.utils;

import android.content.Context;

public class ResourcesUtils {
    private static final String RES_ID = "id";  
    private static final String RES_DIMEN = "dimen";
    private static final String RES_STRING = "string";
    private static final String RES_MIPMAP = "mipmap";
      
      
    /** 
     * 获取资源文件的id 
     * @param context 
     * @param resName 
     * @return 
     */  
    public static int getId(Context context, String resName){
        return getResId(context,resName,RES_ID);  
    }  
      
    /** 
     * 获取资源文件string的id 
     * @param context 
     * @param resName 
     * @return 
     */  
    public static int getStringId(String resName,Context context){
        return getResId(context,resName,RES_STRING);  
    }
      
    /** 
     * 获取资源文件mipmap的id
     */  
    public static int getMipmap(String resName,Context context){
        return getResId(context,resName,RES_MIPMAP);
    }

    /**
     * 获取资源文件dimen的id
     */
    public static int getDimen(String resName,Context context){
        return getResId(context,resName,RES_DIMEN);
    }
      
    /** 
     * 获取资源文件ID 
     * @param context 
     * @param resName 
     * @param defType 
     * @return 
     */  
    public static int getResId(Context context,String resName,String defType){  
        return context.getResources().getIdentifier(resName, defType, context.getPackageName());  
    }
}