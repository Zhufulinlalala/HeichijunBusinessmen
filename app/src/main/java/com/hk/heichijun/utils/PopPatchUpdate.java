package com.hk.heichijun.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hk.heichijun.R;

/**
 * 作者  WSG
 * 时间  2018/7/5.
 * ░░░░░░░░░░░░░░░░░░░░░░░░▄░░
 * ░░░░░░░░░▐█░░░░░░░░░░░▄▀▒▌░
 * ░░░░░░░░▐▀▒█░░░░░░░░▄▀▒▒▒▐
 * ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐
 * ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐
 * ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌
 * ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒
 * ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐
 * ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄
 * ░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒
 * ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒
 * 单身狗就这样默默地看着你，一句话也不说。
 **/
public class PopPatchUpdate extends PushPopupWindow {
    public TextView tv_title;
    public TextView tv_text;
    public Button btn_sure;
    public Button btn_cancel;
    public TextView tv_context1;
    public PopPatchUpdate(Context context) {
        super(context);
        initView();
        setOutsideTouchable(false);
        setFocusable(false);
//        setAnimationStyle();
    }


    @Override
    protected View generateCustomView() {
        View root = View.inflate(context, R.layout.pop_patch_update, null);
        tv_title = (TextView) root.findViewById(R.id.tv_title);
        tv_text = (TextView) root.findViewById(R.id.tv_text);
        btn_sure = (Button) root.findViewById(R.id.btn_contact_us_confirm);
        tv_context1=root.findViewById(R.id.tv_context1);
        return root;
    }

    /**
     * 显示在界面的中间
     */
    public void show(Activity activity) {
        showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
}
