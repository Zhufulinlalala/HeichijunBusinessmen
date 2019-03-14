package com.hk.heichijun.base;

import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * Created by HK on 2017/2/20.
 */

//TODO App通用Model，可根据接口通用格式修改
public class AppBack {
    private String action;
    private String e_msg;
    private String msg;
    private Object result;
    private int status;
    private String statuss;

    /** 接口状态不正常（非0）
     * @return
     */
    public boolean unNormal()
    {
        if (status == 0)
            return false;
        else
        {
            if (msg != null && msg instanceof String)
            {
                Toast.makeText(SaflyApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }
    public boolean isSuccess(){
        if(status==0)
            return true;
        else
            return false;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getE_msg() {
        return e_msg;
    }
    public void setE_msg(String e_msg) {
        this.e_msg = e_msg;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Object getResult() {
        return (Map<String,Object>) result;
    }
    public void setResult(Object result) {
        this.result = result;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public Map<String,Object> getMap()
    {
        return (Map<String,Object>) result;
    }
    public List<Map<String,Object>> getList()
    {
        return (List<Map<String,Object>>) result;
    }
}