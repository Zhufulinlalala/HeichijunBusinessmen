package com.hk.heichijun.utils;

/**
 * 作者  HK
 * 时间  2017/06/29 0029.
 */

public class DimenAdd {
    public static void main(String args [])
    {
        cal(0.1);
        cal(0.2);
        cal(0.3);
        cal(0.4);
        cal(0.5);
        cal(0.6);
        cal(0.7);
        cal(0.8);
        cal(0.9);
        for (int i = 1;i<361;i++)
        {
            System.out.println("<dimen name=\""+"m"+i+"\">"+0.888*i+"dp</dimen>");
        }
    }
    public static void cal(double x)
    {
        System.out.println("<dimen name=\""+"m"+x+"\">"+0.888*x+"dp</dimen>");
    }
}
