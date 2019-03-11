package com.hk.lang_data_manager.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;


public class DrawCircle {//画圆工具类
    public static Bitmap drawCircleOne(Bitmap bitmap){
        //画圆渲染法,绘图需要canvas,bitmap,paint
        //调整图片大小,使其充分展示
        Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        //构建一个位图对象,画布绘制出的图会绘制到此bitmap对象上
        Bitmap bp = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        //构建画布,构建反锯齿画笔
        Canvas canvas = new Canvas(bp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //先画一个圆,再将画笔重置一下
        canvas.drawCircle(100,100,100,paint);
        paint.reset();
        //设置图像合成模式,此模式为只在原图像和目标图像相交的地方绘制原图像
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap1,0,0,paint);
        return bp;
    }
}