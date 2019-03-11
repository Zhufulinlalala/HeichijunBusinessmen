package com.hk.lang_data_manager.base;

import android.app.ActivityManager;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.hk.lang_data_manager.utils.AppUtils;
import com.hk.lang_data_manager.utils.print.AidlUtil;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

public class SaflyApplication extends MultiDexApplication {

    public interface PatchUpdateListener{
        void patchUpdate(int code);
    }

    public static PatchUpdateListener patchUpdateListener=null;
    private static SaflyApplication instance;
    private boolean isDebug = true;
    private static final String TAG = "SaflyApplication";
    private List<BaseActivity> oList;//用于存放所有启动的Activity的集合

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initSophix();
    }

    public void onCreate() {
        super.onCreate();
        this.instance = this;
        oList = new ArrayList<BaseActivity>();
        isDebug = AppUtils.isAppDebug(this);
        //初始化打印服务
        AidlUtil.getInstance().connectPrinterService(this);
        CrashReport.initCrashReport(getApplicationContext(), Constant.BUGLY_APPID, isDebug);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

    }

    /**
     * 阿里热修复
     *
     * @param
     */
    private void initSophix() {
        String appVersion = "0.0.0";
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData(null, null, null)
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            Log.i(TAG, "sophix load patch success!");
                            if (patchUpdateListener!=null){
                                patchUpdateListener.patchUpdate(code);
                            }
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            if (patchUpdateListener!=null){
                                patchUpdateListener.patchUpdate(code);
                            }
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。
                            Log.i(TAG, "sophix preload patch success. restart app to make effect.");
                        }
                    }
                }).initialize();
    }

    /**
     * 添加Activity
     */
    public void addActivity_(BaseActivity activity) {
// 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(BaseActivity activity) {
//判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.realFinish();//销毁当前Activity
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeOneActivity_(Class clz) {
//判断当前集合中存在该Activity
        for (int i = 0; i < oList.size(); i++) {
            if (oList.get(i).getClass().equals(clz)) {
                oList.get(i).finish();
                break;
            }
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (BaseActivity activity : oList) {
            activity.realFinish();
        }
    }

    /**
     * 判断当前界面是否是某一个界面
     */
    private boolean isChatUI() {
        boolean b = false;
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        if (!TextUtils.isEmpty(runningActivity) && TextUtils.equals(runningActivity.replace("com.hk.make_friends.activity.", ""), "ChatActivity"))
            b = true;
        return b;
    }

    public static SaflyApplication getInstance() {
        return instance;
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            //超时设置：连接、写、读的超时时间
            OkHttpClient okHttpClient = builder.connectTimeout(15, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //实现数据共享
}
