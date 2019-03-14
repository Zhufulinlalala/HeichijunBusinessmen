package com.hk.heichijun.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WXAppBack {

    /**
     * 成功0
     * 错误-1
     */
    private Object result;
    private String openid; //是否有下一页

    public Map<String, String> getMap() {
        return getInMap(result);
    }

    private Map<String, String> getInMap(Object object) {
        Map<String, String> map;
        if (object == null) {
            return new HashMap<>();
        }
        try {
            return (Map<String, String>) result;
        } catch (Exception e) {
            try {
                Map<String, Object> map2 = (Map<String, Object>) result;
                map = new HashMap<>();
                for (String key : map2.keySet()) {
                    map.put(key, String.valueOf(map2.get(key)));
                }
                return map;
            } catch (Exception e1) {
                return new HashMap<>();
            }
        }

    }

    public List<Map<String, String>> getList() {
        List<Map<String, String>> list;
        if (result == null) {
            return Collections.emptyList();
        }
        try {

            return (List<Map<String, String>>) result;
        } catch (Exception e) {
            try {
                list = new ArrayList<>();
                for (Object o : (List<Object>) result) {
                    list.add(getInMap(o));
                }
                return list;
            } catch (Exception e1) {
                return Collections.emptyList();
            }
        }
    }


    public Map<String, Object> getMapObject() {
        return getMapInObject(result);
    }
    private Map<String, Object> getMapInObject(Object object) {
        Map<String, Object> map;
        if (object == null) {
            return new HashMap<>();
        }
        try {
            return (Map<String, Object>) result;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    public List<Map<String, Object>> getListObject() {
        List<Map<String, Object>> list;
        if (result == null) {
            return Collections.emptyList();
        }
        try {
            return (List<Map<String, Object>>) result;
        } catch (Exception e) {
            try {
                list = new ArrayList<>();
                for (Object o : (List<Object>) result) {
                    list.add(getMapInObject(o));
                }
                return list;
            } catch (Exception e1) {
                return Collections.emptyList();
            }
        }
    }


    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}