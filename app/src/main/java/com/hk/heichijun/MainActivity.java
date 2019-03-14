package com.hk.heichijun;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.hk.heichijun.base.AppBack;
import com.hk.heichijun.base.BaseActivity;
import com.hk.heichijun.base.Constant;
import com.hk.heichijun.base.OKHttpUICallback;
import com.hk.heichijun.base.OkHttpManger;
import com.tbruyelle.rxpermissions2.RxPermissions;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.HashMap;
import java.util.Map;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
    //获取版本名和版本号
    private String getVersionName() throws Exception
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String version = packInfo.versionName;
        int versionCode = packInfo.versionCode;
        Loge(versionCode);
        return version;
    }
    //接口格式代码示例
    public void testInterface()
    {
        Map<String, String> parmsMap = new HashMap<>();
//        parmsMap.put("token", MySharedPreference.getToken());
//        parmsMap.put("userId",MySharedPreference.getUserId());
        parmsMap.put("key","89860117836036476529");
        OkHttpManger.getInstance().getAsync(Constant.URL + "usermessage/list", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if(appBack.getStatus()==0)
                {
                //复杂接口或者无法吐司报错就用Model
                //TODO  防止无法打印， 把MasterActivity里debug版本判断代码移到启动界面
                //打印、吐司、保存、设置文字用notNullString()方法
                Map<String, String> map = (Map<String, String>) appBack.getResult();
                }
            }
        }, parmsMap);
    }
    //权限申请示例
    //requestRxPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
    public void requestRxPermissions(String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean granted) throws Exception {
                if (granted)
                {

                }
                else {
                    toast("为确保程序正常运行，请授予此权限");
                }
            }
        });
    }

    @Override
    protected void setUpView() {
        //水波纹效果 android:background="?android:attr/selectableItemBackground" 
        EventBus.getDefault().register(this);
        testInterface();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String[] s) {
        //类名+方法/事件名
        if (s[0].equals("MasterActivity|Test"))
        {
            toast(s[1]);
        }
    }
    //完整的EventBus示例
//    EventBus.getDefault().post(new String[]{"MasterActivity|Test","Test"});

    /** 异步耗时操作
     *
     */
    @SuppressLint("StaticFieldLeak")
    public void asyncLongTime()
    {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //做事
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //事情做完了的回调
            }
        }.execute();
    }
}
