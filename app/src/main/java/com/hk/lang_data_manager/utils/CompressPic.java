package com.hk.lang_data_manager.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lenovo on 2017/3/31.
 */

public class CompressPic {
    //压缩图片的尺寸大小
    public static Bitmap compressPicSize(String path, int newWidth, int newHight) {

        //先获取图片的尺寸大小
        //获取解析bitmap的选项参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        //仅仅解析图片的边框
        options.inJustDecodeBounds = true;
        //从路径或文件中获取图片bitmap对象
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        //解析后,bitmap为空,会将长度和宽度放到options中,
        //此时取得bitmap的宽高
        int oldWidth = (int) Math.ceil(options.outWidth);//向上取整
        int oldHight = (int) Math.ceil(options.outHeight);
        //获取宽高的比例
        int sizeWidth = oldWidth / newWidth;
        int sizeHight = oldHight / newHight;

        int sampleSize = sizeHight > sizeWidth ? sizeHight : sizeWidth;
        //如果超出指定的大小,就压缩图片
        if (sizeHight > 1 && sizeWidth > 1) {
            //如同前面的步骤,向选项参数中放入新的压缩比例
            options.inSampleSize = sampleSize;
        }
        //需要完整解析整张图片
        options.inJustDecodeBounds = false;
        //按照新的缩放比例重新解析
        Bitmap bp = BitmapFactory.decodeFile(path, options);
        return bp;
    }

    //压缩图片的内存大小,使文件占据更小的空间
    public static String compressPicFileSize(String path, String newFileName, int size) {//size单位kb
        //先获取图片bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        //将图片压缩到想要的内存以内
        //第一次,不压缩图片,先判断图片内存是否符合要求
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int compressQuality = 100;

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        while (baos.toByteArray().length / 1024 > size) {
            //清空已经写入的流
            baos.reset();
            //大于想要的尺寸继续压缩
            compressQuality -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, baos);
        }
        //跳出循环,表示图片尺寸已经小于size,往文件中写
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(new File(path, newFileName + ".jpg"));
            byte[] bytes = baos.toByteArray();
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            return path.replace(".jpg",newFileName+".jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return path;
    }
}
