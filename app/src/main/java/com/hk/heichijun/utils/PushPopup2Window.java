package com.hk.heichijun.utils;

/**
 * 作者 沈栋 on 2017/2/11 0011.
 * 邮箱：263808622@qq.com
 */


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.hk.heichijun.base.SaflyApplication;

/**
 * 从下方弹出的PopupWindow,仿iphone效果，增加半透明蒙层。
 * <p>
 * 实现原理：<br>
 * 在弹出自定义的PopupWindow时，增加一个半透明蒙层view到窗口，并置于PopupWindow下方。
 * </p>
 * <p>
 * 使用方法：<br>
 * 继承BottomPushPopupWindow，编写generateCustomView添加自定义的view，调用show方法显示。
 * </p>
 *
 * @author y
 */
public abstract class PushPopup2Window extends PopupWindow {

    protected Context context;
    private WindowManager wm;
    private View maskView;

    @SuppressWarnings("deprecation")
    public PushPopup2Window(Context context) {
        super(context);
        //崩溃是因为空指针，未初始化属性
        // initView();
        // setFocusable(true);
        // setOutsideTouchable(true);
        this.context = context;
    }
    public void toast(String s)
    {
        ToastUtil.showToast(SaflyApplication.getInstance(),s);
    }
    public void toast(Object o)
    {
        ToastUtil.showToast(SaflyApplication.getInstance(),o.toString());
    }
    protected void initView(){
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initType();
        setContentView(generateCustomView());
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setTouchable(true);//这个控制PopupWindow内部控件的点击事件
        setFocusable(true);
        setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
        setOutsideTouchable(true);
    }

    protected void setAnimationStyle(){
//        setAnimationStyle(R.style.Animations_BottomPush);
    }
    protected abstract View generateCustomView();

    @TargetApi(23)
    private void initType() {
        // 解决华为手机在home建进入后台后，在进入应用，蒙层出现在popupWindow上层的bug。
        // android4.0及以上版本都有这个hide方法，根据jvm原理，可以直接调用，选择android6.0版本进行编译即可。
        setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if(parent==null){
            return;
        }
        if(parent.getWindowToken()==null){
            return;
        }
        addMaskView(parent.getWindowToken());
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        addMaskView(anchor.getWindowToken());
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void dismiss() {
        removeMaskView();
        super.dismiss();
    }

    /**
     * 显示在界面的底部
     */
    public void showBottom(Activity activity) {
        showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    public void show(Activity activity) {
        showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void addMaskView(IBinder token) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        p.height = WindowManager.LayoutParams.MATCH_PARENT;
        p.format = PixelFormat.TRANSLUCENT;
        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        p.token = token;
        p.windowAnimations = android.R.style.Animation_Toast;
        maskView = new View(context);
        maskView.setBackgroundColor(Color.alpha(0));
        //  maskView.setFitsSystemWindows(false);
        // 华为手机在home建进入后台后，在进入应用，蒙层出现在popupWindow上层，导致界面卡死，
        // 这里新增加按bug返回。
        // initType方法已经解决该问题，但是还是留着这个按back返回功能，防止其他手机出现华为手机类似问题。
        maskView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    removeMaskView();
                    return true;
                }
                return false;
            }
        });
        wm.addView(maskView, p);
    }

    private void removeMaskView() {
        if (maskView != null) {
            wm.removeViewImmediate(maskView);
            maskView = null;
        }
    }
}