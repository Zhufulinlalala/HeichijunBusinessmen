package com.hk.lang_data_manager.utils;

import android.view.View;

public abstract class NoDoubleClickListener implements View.OnClickListener {
    public final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public abstract void onNoDoubleClick(View v) ;
    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick( v);
        }
    }
}