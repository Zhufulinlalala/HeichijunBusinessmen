package com.hk.lang_data_manager.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者 沈栋 on 2016/12/29 0029.
 * 邮箱：263808622@qq.com
 */

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Map<Integer, View> mCacheView;

    public MyViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    OnItemClickListener mOnItemClickListener;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}