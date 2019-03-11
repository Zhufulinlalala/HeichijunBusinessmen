package com.hk.lang_data_manager.utils;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

import java.lang.reflect.Field;

public class MyTabLayout5 extends TabLayout {
    private final int TabViewNumber = 5;
    private final String SCROLLABLE_TAB_MIN_WIDTH = "mScrollableTabMinWidth";
    public MyTabLayout5(Context context) {
        super(context);
        initTabMinWidth();
    }

    public MyTabLayout5(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTabMinWidth();
    }

    public MyTabLayout5(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);  
        initTabMinWidth();  
    }  
    private void initTabMinWidth() {  
        int screenWidth=getResources().getDisplayMetrics().widthPixels-dip2px(39);
        int tabMinWidth = screenWidth / TabViewNumber;  
  
        Field field;
        try {  
            field = TabLayout.class.getDeclaredField(SCROLLABLE_TAB_MIN_WIDTH);  
            field.setAccessible(true);  
            field.set(this, tabMinWidth);  
        } catch (NoSuchFieldException e) {  
            e.printStackTrace();  
        } catch (IllegalAccessException e) {  
            e.printStackTrace();  
        }  
    }
    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}  