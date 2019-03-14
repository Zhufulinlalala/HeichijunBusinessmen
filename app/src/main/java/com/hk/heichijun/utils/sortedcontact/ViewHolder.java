package com.hk.heichijun.utils.sortedcontact;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者 沈栋 on 2016/12/29 0029.
 * 邮箱：263808622@qq.com
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    private Map<Integer, View> mCacheView;

    public ViewHolder(View itemView) {
        super(itemView);
        mCacheView = new HashMap<>();
    }

    public View getView(int resId) {
        View view;
        if (mCacheView.containsKey(resId)) {
            view = mCacheView.get(resId);
        } else {
            view = itemView.findViewById(resId);
            mCacheView.put(resId, view);
        }
        return view;
    }
}