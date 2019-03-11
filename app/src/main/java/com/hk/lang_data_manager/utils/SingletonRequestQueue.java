package com.hk.lang_data_manager.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.hk.lang_data_manager.base.SaflyApplication;

/**
 * 作者 沈栋 on 2016/12/15 0015.
 * 邮箱：263808622@qq.com
 */

public class SingletonRequestQueue {

    private LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(1024*1024*10) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    };

    static final String TAG="SaflyApplication";
    ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
        @Override
        public void putBitmap(String key, Bitmap value) {
            if(value!=null) {
                mCache.put(key, value);
            }
        }

        @Override
        public Bitmap getBitmap(String key) {
            return mCache.get(key);
        }
    };

    public void loadImageByVolley(ImageView imageView , String imageUrl){
        if( mCache.get(imageUrl)!=null){
            imageView.setImageBitmap(mCache.get(imageUrl));
            return;
        }
        ImageLoader imageLoader = new ImageLoader(mInstance.getRequestQueue(), imageCache);
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(
                imageView, 0,0);
        imageLoader.get(imageUrl, listener,800,800);
    }
    public void loadImageByVolleyRound(ImageView imageView , String imageUrl){

        if( mCache.get(imageUrl)!=null){
            imageView.setImageBitmap(DrawCircle.drawCircleOne(mCache.get(imageUrl)));
            return;
        }

        ImageLoader imageLoader = new ImageLoader(mInstance.getRequestQueue(), imageCache);
        ImageLoader.ImageListener listener =new Mylistener(imageView);

        imageLoader.get(imageUrl, listener,800,800);
    }





    private static SingletonRequestQueue mInstance;

    private RequestQueue mRequestQueue;



    private SingletonRequestQueue(Context context){

        mRequestQueue=getRequestQueue();

    }



    public static synchronized SingletonRequestQueue getInstance( ){

        if(mInstance == null){

            mInstance=new SingletonRequestQueue(SaflyApplication.getInstance());

        }

        return mInstance;

    }



    public RequestQueue getRequestQueue(){

        if(mRequestQueue==null){

            mRequestQueue= Volley.newRequestQueue(SaflyApplication.getInstance());

        }

        return mRequestQueue;

    }

}

class Mylistener implements    ImageLoader.ImageListener{

    ImageView imageView;
    Mylistener(ImageView imageView){
        this.imageView=imageView;
    }


    @Override
    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {


        if(imageContainer.getBitmap() != null) {
            imageView.setImageBitmap(DrawCircle.drawCircleOne(imageContainer.getBitmap()));
        }

    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }
}

