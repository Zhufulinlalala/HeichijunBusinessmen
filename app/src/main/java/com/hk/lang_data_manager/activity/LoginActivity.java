package com.hk.lang_data_manager.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.hk.lang_data_manager.MasterActivity;
import com.hk.lang_data_manager.R;
import com.hk.lang_data_manager.base.AppBack;
import com.hk.lang_data_manager.base.BaseActivity;
import com.hk.lang_data_manager.base.Constant;
import com.hk.lang_data_manager.base.OKHttpUICallback;
import com.hk.lang_data_manager.base.OkHttpManger;
import com.hk.lang_data_manager.utils.AppUtils;
import com.hk.lang_data_manager.utils.MySharedPreference;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import butterknife.BindView;

public class LoginActivity extends BaseActivity {
    public final String SYS_EMUI = "sys_emui";
    public final String SYS_MIUI = "sys_miui";
    public final String SYS_FLYME = "sys_flyme";
    private final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void setUpView() {
        SpeechUtility.createUtility(this,SpeechConstant.APPID+"=5a7e86c4");  //讯飞 语音
        MySharedPreference.save("androidId", Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID));
        //判断状态栏能否变成白字黑图标，是否为正式版
        if (TextUtils.equals(getSystem(), SYS_MIUI))
            MySharedPreference.save("canWhite", "trueMIUI");
        else if (!TextUtils.equals(getSystem(), SYS_FLYME) && Build.VERSION.SDK_INT <= 22 && Build.VERSION.SDK_INT >= 21)
            MySharedPreference.save("canWhite", "false");
        else
            MySharedPreference.save("canWhite", "true");




        if (AppUtils.isAppDebug(this))
            MySharedPreference.save("isDebug", "1");
        else
            MySharedPreference.save("isDebug", "0");
        /*登录*/
        if (!isEmpty(MySharedPreference.getAdminId())) {
            jump2Activity(MasterActivity.class);
            finish();
        }
    }

    public void Back(View v) {
        finish();
    }

    @Override
    protected void changeStatusBarColor() {
        transparencyBar(this);
    }

    public void login(View v) {
        if (TextUtils.isEmpty(etAccount.getText().toString()))
        {
            toast("请输入账号");
            return;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString()))
        {
            toast("请输入密码");
            return;
        }
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("username",etAccount.getText().toString().trim());
        parmsMap.put("password",etPassword.getText().toString());
        parmsMap.put("deviceCode",MySharedPreference.get("androidId"));
        OkHttpManger.getInstance().getAsync(Constant.URL + "cinemaadmin/login", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess())
                {
                    Map<String, Object> map = (Map<String, Object>) appBack.getResult();
                    MySharedPreference.saveUserId(map.get("id"));
                    MySharedPreference.saveToken(map.get("token"));
                    MySharedPreference.saveCinemaId(map.get("cinemaId"));
                    MySharedPreference.saveUserName(map.get("username"));
                    MySharedPreference.saveLevel(map.get("level"));
                    jump2Activity(MasterActivity.class);
                    finish();
                }
            }
        }, parmsMap);
    }
    public String getSystem() {
        String SYS = "";
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            if (prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null) {
                SYS = SYS_MIUI;//小米
            } else if (prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
                    || prop.getProperty(KEY_EMUI_VERSION, null) != null
                    || prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null) {
                SYS = SYS_EMUI;//华为
            } else if (getMeizuFlymeOSFlag().toLowerCase().contains("flyme")) {
                SYS = SYS_FLYME;//魅族
            }
            ;
        } catch (IOException e) {
            e.printStackTrace();
            return SYS;
        }
        return SYS;
    }

    public String getMeizuFlymeOSFlag() {
        return getSystemProperty("ro.build.display.id", "");
    }

    private String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }
}
