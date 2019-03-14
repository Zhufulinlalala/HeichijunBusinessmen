package com.hk.heichijun.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hk.heichijun.R;

/**
 * 作者 沈栋 on 2017/2/15 0015.
 * 邮箱：263808622@qq.com
 */

public class PopStyleIphonejiedan extends PushPopupWindow {
    public TextView tv_title;
    public TextView tv_text;
    public Button btn_sure;
    public Button btn_cancel;

    public PopStyleIphonejiedan(Context context) {
        super(context);
        initView();
        setOutsideTouchable(true);
//        setAnimationStyle();
    }


    @Override
    protected View generateCustomView() {
        View root = View.inflate(context, R.layout.pop_style_iphonejiedan, null);
        tv_title = (TextView) root.findViewById(R.id.tv_title);
        tv_text = (TextView) root.findViewById(R.id.tv_text);
        btn_sure = (Button) root.findViewById(R.id.btn_contact_us_confirm);
        btn_cancel = (Button) root.findViewById(R.id.btn_contact_us_cancel);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return root;
    }

    /**
     * 显示在界面的中间
     */
    public void show(Activity activity) {
        showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
}
