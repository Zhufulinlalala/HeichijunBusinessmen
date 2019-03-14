package com.hk.lang_data_manager.utils.print;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hk.lang_data_manager.utils.DataFormatUtils;

import java.io.Console;
import java.util.List;
import java.util.Map;

/**
 * 作者  HK
 * 时间  2018/2/24.
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
public class PrintOrderUtils {
    private String name;
    //打印 浪 小票
    public static void print(Map<String,Object> orderMap)
    {
        //对齐模式  0--居左 , 1--居中, 2--居右

        AidlUtil.getInstance().printMyText(notNullString(orderMap.get("cinemaName")), 22, false, 1,1);
        AidlUtil.getInstance().printMyText("*****************************", 22, false, 1,1);
        if (orderMap.get("orderType").equals(2))//送餐到位
        {
            AidlUtil.getInstance().printMyText("送餐到位：" + notNullString(orderMap.get("takeCode")), 38, false,
                    1,1);
            AidlUtil.getInstance().printMyText("--------------------------------", 24, false,
                    1, 1);
            AidlUtil.getInstance().printMyText(notNullString(orderMap.get("phone"))+notNullString(orderMap.get("userName"))+"\n"+notNullString(orderMap.get("seat")), 30, false,
                    1, 1);
        }
        else if(orderMap.get("orderType").equals(1))//自提
        {
            AidlUtil.getInstance().printMyText("自助取餐 : " +notNullString(orderMap.get("takeCode")), 38, false, 1,1);
        }
        else if(orderMap.get("orderType").equals(4))//外卖
        {
            AidlUtil.getInstance().printMyText("外卖订单 : " +notNullString(orderMap.get("takeCode")), 38, false, 1,1);
        }
        else{
            AidlUtil.getInstance().printMyText("影院服务：" + notNullString(orderMap.get("takeCode")), 38, false,
                    1,1);
            AidlUtil.getInstance().printMyText("--------------------------------", 24, false,
                    1, 1);
            AidlUtil.getInstance().printMyText(notNullString(orderMap.get("phone"))+notNullString(orderMap.get("userName"))+"\n"+notNullString(orderMap.get("seat")), 30, false,
                    1, 1);
        }
        AidlUtil.getInstance().printMyText("--------------------------------", 24, false,
                1, 1);

        List<Map<String, Object>> goodsList = (List<Map<String, Object>>) orderMap.get("attrList");//商品列表
        List<Map<String, Object>> giftList = (List<Map<String, Object>>) orderMap.get("giftList");//周边列表
        if (!orderMap.get("orderType").equals(3)) {
            for (Map<String, Object> map : goodsList) {
                AidlUtil.getInstance().printMyText(notNullString(map.get("goodsName")) + "*" +
                        notNullString(map.get("count")), 38, false, 0, 1);
                if (!TextUtils.isEmpty(notNullString(map.get("attrName"))))
                    AidlUtil.getInstance().printMyText(notNullString(map.get("attrName")), 38, false, 0, 1);
                if (!TextUtils.isEmpty(notNullString(map.get("attr2"))))
                    AidlUtil.getInstance().printMyText(notNullString(map.get("attr2")), 38, false, 0, 1);
            }
            for (Map<String, Object> map : giftList) {
                AidlUtil.getInstance().printMyText(notNullString(map.get("giftName")) + "*1", 38, false, 0, 1);
            }
        }
        else{
            for (Map<String, Object> maps : goodsList) {
                AidlUtil.getInstance().printMyText(notNullString(maps.get("goodsName")), 38, false, 0, 1);
            }
        }
        AidlUtil.getInstance().printMyText("--------------------------------", 24, false,
                1, 1);
        AidlUtil.getInstance().printMyText("备注 : ", 38, false,
                0, 0);
        AidlUtil.getInstance().printMyText(notNullString(orderMap.get("remark")), 30, false,
                0, 1);
        AidlUtil.getInstance().printMyText("--------------------------------", 24, false,
                1, 1);
        if(orderMap.get("orderType").equals(4))//外卖
        {
            AidlUtil.getInstance().printMyText("运单号 : " +notNullString(orderMap.get("dadaWaybillCode")), 38, false, 0,1);
            AidlUtil.getInstance().printMyText("收货人 : " +notNullString(orderMap.get("dadaAddressUserName")), 38, false, 0,1);
            AidlUtil.getInstance().printMyText("联系方式 : " +notNullString(orderMap.get("dadaAddressPhone").toString().replaceAll("(\\d{3})\\d{4}(\\d)","$1****$2")), 38, false, 0,1);
            AidlUtil.getInstance().printMyText("收货地址 : " +notNullString(orderMap.get("dadaTakeawayAddress")), 38, false, 0,1);
        }
        AidlUtil.getInstance().printMyText("--------------------------------", 24, false,
                1, 1);
         if (!orderMap.get("orderType").equals(3)){
        AidlUtil.getInstance().printMyText("实付金额 : ", 22, false,
                2, 0);
        AidlUtil.getInstance().printMyText(notNullString(orderMap.get("totalPrice"))+"元", 30, false,
                2, 1);
         }
         else{
         }
        AidlUtil.getInstance().printMyText("下单时间 : "+ DataFormatUtils.formatTimeShort(
                Long.parseLong(notNullString(orderMap.get("createTime")))), 22, false,
                1, 1);
        AidlUtil.getInstance().printMyText("  ", 22, false,
                0,3);
    }
    // 规避空指针，对于生成的Model中的int需要手动批量替换成Integer
    public static String notNullString(Object o)
    {
        if (o != null)
            return o.toString();
        else
            return "";
    }
}
