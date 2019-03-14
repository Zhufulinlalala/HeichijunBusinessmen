package com.hk.heichijun.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

/**
 * 屏幕帮助类
 * 
 * @author zhaokaiqiang
 * 
 */
public class ScreenUtils {

	/*使整个屏幕界面变暗*/
    public static void screenDarken( Activity activity){
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha=0.7f;//设置整个屏幕的透明度为0.7
        window.setAttributes(attributes);
    }
    /*使整个屏幕界面非常暗*/
    public static void screenDeepDarken( Activity activity){
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha=0.2f;//设置整个屏幕的透明度为0.2
        window.setAttributes(attributes);
    }

    /*使整个屏幕界面变正常*/
    public static void screenLight( Activity activity){
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha=1f;//设置整个屏幕的透明度为1
        window.setAttributes(attributes);
    }

	/**
	 * 获取屏幕宽度
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getScreenWidth(Context context) {
		return ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getScreenHeight(Context context) {
		return ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getHeight();
	}

}
