package com.hk.heichijun.utils;

import android.os.CountDownTimer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DataFormatUtils {
    //数据处理工具类
    private DataFormatUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /** 使用java正则表达式去掉多余的.与0
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s){
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }
    public static String twoNum(String str)
    {
        String s = String.format("%.3f",Float.parseFloat(str));
        return s.substring(0,s.length()-1);
    }

    /** 格式化时间戳
     * @param oldTime
     * @return newTime
     */
    public static String formatTime(Long oldTime)
    {
        String newTime = "";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        newTime = sdf.format(oldTime);
        return newTime;
    }
    /** 格式化时间戳  不需要秒
     * @param oldTime
     * @return newTime
     */
    public static String formatTimeShort(Long oldTime)
    {
        String newTime = "";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        newTime = sdf.format(oldTime).substring(0,16);
        return newTime;
    }
    //规避空指针，可对Model中int批量替换，小心多替换了
    public static String notNullString(Object o)
    {
        if (o != null)
            return o.toString();
        else
            return "null";
    }

    /** 对后台返回的时间差做界面上的更新（毫秒）
     */
    public void calLeftTime()
    {
        CountDownTimer cd = new CountDownTimer(60 * 1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {

            }
        };
        long leftTime = 0;//剩余时间 毫秒
        leftTime = 3600*1000;//假装调用接口拿到的剩余时间
        int days,hours,minutes,seconds;
        String daysStr = "",hoursStr = "",minutesStr = "",secondsStr = "";
        if (leftTime <= 0)
        {
            if (cd == null)
                cd.cancel();
            return;
        }
        long leftTimeSecond = leftTime / 1000;
        days = (int) leftTimeSecond / (3600 * 24);
        hours = (int) leftTimeSecond % (3600 * 24) / 3600;
        minutes = (int) leftTimeSecond % (3600 * 24) % 3600 / 60;
        seconds = (int) leftTimeSecond % (3600 * 24) % 3600 % 60;

        daysStr = days<10?"0"+String.valueOf(days):String.valueOf(days);
        hoursStr = hours<10?"0"+String.valueOf(hours):String.valueOf(hours);
        minutesStr = minutes<10?"0"+String.valueOf(minutes):String.valueOf(minutes);
        secondsStr = seconds<10?"0"+String.valueOf(seconds):String.valueOf(seconds);
//        tvDay.setText(daysStr);
//        tvHour.setText(hoursStr);
//        tvMinute.setText(minutesStr);
//        tvSecond.setText(secondsStr);
        leftTime -= 1000;
    }
    /** 计算时间差，多久之前
     * @param time
     * @return
     */
    public static String calTime(Long time)
    {
        Long nowTime = System.currentTimeMillis();
        Long timeDim = nowTime - time;
        //2分钟内
        if (timeDim< 2*60*1000)
            return "1分钟";
            //1小时内
        else if (timeDim< 60*60*1000)
            return (int) (timeDim/(60*1000))+"分钟";
            //24小时内
        else if (timeDim< 24*60*60*1000)
            return (int) (timeDim/(60*60*1000))+"小时";
            //4天内
        else if (timeDim< 4*24*60*60*1000)
            return (int) (timeDim/(24*60*60*1000))+"天";
            //4天或者更久前
        else
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(time);
            return simpleDateFormat.format(date);
        }
    }
    /** 小数点前移两位
     * @param oldPrice
     * @return newPrice
     */
    public static String dotFront2(int oldPrice)
    {
        String newPrice;
        String oldPriceStr = (oldPrice+"").replace("-","");
        if (oldPriceStr.length()<3)
            newPrice = "0.0".substring(0,4-oldPriceStr.length())+oldPriceStr;
        else
            newPrice = oldPriceStr.substring(0,oldPriceStr.length()-2)+"."+oldPriceStr.substring(oldPriceStr.length()-2,oldPriceStr.length());
        return (oldPrice<0)?"-"+newPrice:newPrice;
    }
    /** 返回时间差，多少天前 --- 年月日
     * @param oldTime
     * @return dateString
     */
    public static String formationDate(long oldTime) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        Date date= null;
        Date date1= null;
        Date now1= null;
        String s = sdf.format(oldTime);
        String dateString = "";
        // 获取系统当前时间
        Date now = new Date();
        String now2 = sdf.format(now);
        try {
            date = sdf.parse(s);
            date1 = sdf1.parse(s);
            now1 = sdf1.parse(now2);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        try {
            long endTime = now1.getTime();
            long currentTime= date1.getTime();
            // 计算两个时间点相差的秒数
            long seconds = (endTime - currentTime);
            if (seconds==0)
                dateString = s.substring(11,16);
            else if (seconds<=60*60*24*1000*7L&&seconds>0) {
                dateString =seconds/1000/60/60/24+ "天前";
            }else if (date.getYear()==now.getYear()) {//今年并且大于7天显示具体月日
                dateString = new SimpleDateFormat("MM-dd").format(date.getTime());
            }else if (date.getYear()!=now.getYear()) {//大于今年显示年月日
                dateString =  new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }
}