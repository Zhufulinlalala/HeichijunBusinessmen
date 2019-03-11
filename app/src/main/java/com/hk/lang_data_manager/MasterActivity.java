package com.hk.lang_data_manager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.lang_data_manager.activity.LoginActivity;
import com.hk.lang_data_manager.activity.SearchActivity;
import com.hk.lang_data_manager.activity.SearchActivity2;
import com.hk.lang_data_manager.activity.TongActivity;
import com.hk.lang_data_manager.base.AppBack;
import com.hk.lang_data_manager.base.BaseActivity;
import com.hk.lang_data_manager.base.Constant;
import com.hk.lang_data_manager.base.OKHttpUICallback;
import com.hk.lang_data_manager.base.OkHttpManger;
import com.hk.lang_data_manager.base.SaflyApplication;
import com.hk.lang_data_manager.fragment.HistoryFragment;
import com.hk.lang_data_manager.fragment.NewTaskFragment;
import com.hk.lang_data_manager.fragment.SumFragment;
import com.hk.lang_data_manager.utils.AppUtils;
import com.hk.lang_data_manager.utils.MySharedPreference;
import com.hk.lang_data_manager.utils.PopPatchUpdate;
import com.hk.lang_data_manager.utils.PopStyleIphone;
import com.taobao.sophix.SophixManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MasterActivity extends BaseActivity {
    NewTaskFragment homeFragment;
    HistoryFragment historyFragment;
    SumFragment sumFragment;
    public final String SYS_EMUI = "sys_emui";
    public final String SYS_MIUI = "sys_miui";
    public final String SYS_FLYME = "sys_flyme";
    private final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.viewer)
    FrameLayout viewer;
    @BindView(R.id.iv_order_Manager)
    ImageView ivOrderManager;
    @BindView(R.id.tv_order_Manager)
    TextView tvOrderManager;
    @BindView(R.id.order_Manager)
    LinearLayout orderManager;
    @BindView(R.id.iv_shop_Manage)
    ImageView ivShopManage;
    @BindView(R.id.tv_shop_Manage)
    TextView tvShopManage;
    @BindView(R.id.shop_Manager)
    LinearLayout shopManager;
    @BindView(R.id.iv_sum)
    ImageView ivSum;
    @BindView(R.id.tv_sum)
    TextView tvSum;
    @BindView(R.id.ll_sum)
    LinearLayout llSum;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.iv_search)
    LinearLayout ivSearch;
    @BindView(R.id.dl_admin)
    DrawerLayout dlAdmin;
    @BindView(R.id.iv_resert)
    LinearLayout ivResert;
    @BindView(R.id.iv_tong)
    LinearLayout ivTong;
    @BindView(R.id.widget)
    FrameLayout widget;
    private long time = 0;
    private PopStyleIphone clearCachePw, exitPw;
    private boolean change = false;
    private Set<String> s;
    public boolean c = false;
    private PopPatchUpdate ppu;

    private String versionCode;
    private String versionTitle;
    private String androidId;
    private int isNewVersion=0;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_master;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkVersion();
        androidId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        SaflyApplication.patchUpdateListener = new SaflyApplication.PatchUpdateListener() {
            @Override
            public void patchUpdate(final int code) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, String> par = new HashMap<>();
                        par.put("updateCinemaId", MySharedPreference.getCinemaId());
                        par.put("updateVersionNumber",versionTitle);
                        par.put("updateVersionCode",versionCode);
                        par.put("updateStatus","1");
                        par.put("updateDeviceCode", androidId);
                        OkHttpManger.getInstance().getAsync(Constant.URL + "versionUpdate/save", new OKHttpUICallback.ResultCallback<AppBack>() {
                            @Override
                            public void onSuccess(AppBack result) {
                                toast("上传成功");
                            }
                        }, par);
                        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 15, restartIntent);
                        SophixManager.getInstance().killProcessSafely();
                    }
                });
            }
        };
    }

    private void checkVersion() {
        List<String> contentList = new ArrayList<>();
        Map<String, String> parmsMap = new HashMap<>();
        OkHttpManger.getInstance().getAsync(Constant.URL + "version/getUpdates", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    Map<String, Object> map = (Map<String, Object>) appBack.getResult();
                    versionCode = (String) map.get("version_code");
                    versionTitle = (String) map.get("version_number");
//                    String versionCodethis = getVersionCode(MasterActivity.this);
// versionCodethis热更新不起作用，原因不明，解决方法：手动设置versionCodethis的值
                    //每次更新补丁+1
                    String versionCodethis = 7 + "";
                    String versionDescription = (String) map.get("version_description");
                    final List<String> ListContent = Arrays.asList(versionDescription.split("#"));
                    if (Integer.valueOf(versionCode) > Integer.valueOf(versionCodethis)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isNull(ppu)) {
                                    String content = "";
                                    ppu = new PopPatchUpdate(MasterActivity.this);
                                    ppu.tv_text.setText("最新版本" + versionTitle);
                                    for (int i = 0; i < ListContent.size(); i++) {
                                        content += String.valueOf(i + 1) + "." + ListContent.get(i) + "\n\n";
                                    }
                                    ppu.tv_context1.setText(content);
                                    ppu.btn_sure.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            SophixManager.getInstance().queryAndLoadNewPatch();
                                            widget.setVisibility(View.VISIBLE);
                                            ppu.dismiss();
                                        }
                                    });
                                    ppu.show(MasterActivity.this);
                                } else {
                                    ppu.show(MasterActivity.this);
                                }
                            }
                        });
                    } else {

                    }
                }
            }
        }, parmsMap);
    }

    @Override
    protected void setUpView() {
        widget.setVisibility(View.GONE);

        //每次进入首页判断状态栏是否能白底黑字、是否为正式版
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
        NewTask();
        initView();
        Intent intent = getIntent();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView tvGoOut = headerView.findViewById(R.id.tv_go_out);
        TextView tvName = headerView.findViewById(R.id.tv_admin);
        TextView tvBanben=headerView.findViewById(R.id.tv_banben);
        tvName.setText(MySharedPreference.getUserName());
        tvGoOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNull(exitPw)) {
                    exitPw = new PopStyleIphone(MasterActivity.this);
                    exitPw.tv_text.setText("确定要退出账号吗");
                    exitPw.btn_sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JPushInterface.deleteAlias(MasterActivity.this, 0);
                            JPushInterface.deleteTags(MasterActivity.this, 0, s);
                            SaflyApplication.getInstance().removeALLActivity_();
                            out();
                            exitPw.dismiss();
                        }
                    });
                }
                exitPw.show(MasterActivity.this);
            }
        });
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (change == false)
                    jump2Activity(SearchActivity.class);
                else
                    jump2Activity(SearchActivity2.class);
            }
        });
        ivResert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ivTong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump2Activity(TongActivity.class);
            }
        });
        JPushInterface.init(getApplicationContext());
        JPushInterface.setAlias(this, "adminId" + MySharedPreference.getAdminId(), new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });
        s = new LinkedHashSet<>();
        s.add("shopId" + MySharedPreference.getCinemaId());
        JPushInterface.setTags(this, 1, s);
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(MasterActivity.this);
        builder.notificationDefaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
        JPushInterface.setPushNotificationBuilder(1, builder);
    }



    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                }
            } catch (Exception e) {
            }
        }
    }

    @OnClick(R.id.order_Manager)
    public void NewTask() {
        if (homeFragment == null) {
            homeFragment = new NewTaskFragment();
        } else {
            EventBus.getDefault().post(new String("New"));
        }
        changeFragment(homeFragment);

        if (homeFragment.refresh == true) {
            homeFragment.onResume();
        }
        ivOrderManager.setImageResource(R.mipmap.order2);
        ivShopManage.setImageResource(R.mipmap.commodity1);
        ivSum.setImageResource(R.mipmap.deta1);
        tvOrderManager.setTextColor(this.getResources().getColor(R.color.black));
        tvShopManage.setTextColor(Color.parseColor("#d3d3d3"));
        tvSum.setTextColor(Color.parseColor("#d3d3d3"));
        tvTitle.setText("订单管理");
        ivSearch.setVisibility(View.GONE);
        ivResert.setVisibility(View.GONE);
        ivTong.setVisibility(View.GONE);
    }

    @OnClick(R.id.shop_Manager)
    public void History() {
        if (historyFragment == null) {
            historyFragment = new HistoryFragment();
        }
        changeFragment(historyFragment);
        change = false;
        ivOrderManager.setImageResource(R.mipmap.order1);
        ivShopManage.setImageResource(R.mipmap.commodit2);
        ivSum.setImageResource(R.mipmap.deta1);
        tvOrderManager.setTextColor(Color.parseColor("#d3d3d3"));
        tvShopManage.setTextColor(this.getResources().getColor(R.color.black));
        tvSum.setTextColor(Color.parseColor("#d3d3d3"));
        tvTitle.setText("商品");
        ivSearch.setVisibility(View.INVISIBLE);
        ivResert.setVisibility(View.INVISIBLE);
        ivTong.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.ll_sum)
    public void Sum() {
        if (sumFragment == null) {
            sumFragment = new SumFragment();
        } else {
            EventBus.getDefault().post(new String("sum"));
        }
        changeFragment(sumFragment);
        change = true;
        ivOrderManager.setImageResource(R.mipmap.order1);
        ivShopManage.setImageResource(R.mipmap.commodity1);
        ivSum.setImageResource(R.mipmap.deta2);
        tvOrderManager.setTextColor(Color.parseColor("#d3d3d3"));
        tvShopManage.setTextColor(Color.parseColor("#d3d3d3"));
        tvSum.setTextColor(this.getResources().getColor(R.color.black));
        tvTitle.setText("数据统计");
        ivSearch.setVisibility(View.VISIBLE);
        ivResert.setVisibility(View.VISIBLE);
        ivTong.setVisibility(View.VISIBLE);
    }

    private void initView() {
        dlAdmin.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        dlAdmin.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                dlAdmin.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @OnClick(R.id.ll_pc)
    public void pc() {
        if (!dlAdmin.isDrawerOpen(navView)) {
            dlAdmin.openDrawer(navView);
        }
    }

    private Fragment mCurrentFg;

    private void changeFragment(Fragment newFragment) {
        FragmentManager fm = getSupportFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        if (mCurrentFg == null) {
            if (newFragment.isAdded()) {
                transaction.show(newFragment);
            } else {
                transaction.add(R.id.viewer, newFragment);
            }
        } else {
            if (mCurrentFg == newFragment) {
                return;
            }
            if (newFragment.isAdded()) {
                transaction.show(newFragment).hide(mCurrentFg);
            } else {
                transaction.add(R.id.viewer, newFragment).hide(mCurrentFg);
            }
        }
        mCurrentFg = newFragment;
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        //退出方法
        finish();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private void out() {
        Map<String, String> par = new HashMap<>();
        par.put("cinemaId", MySharedPreference.getCinemaId());
        par.put("userId", MySharedPreference.getAdminId());
        par.put("deviceCode", MySharedPreference.get("androidId"));
        OkHttpManger.getInstance().getAsync(Constant.URL + "cinemaadmin/loginOut", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack result) {
                MySharedPreference.clear();
                jump2Activity(LoginActivity.class);
            }
        }, par);
    }

    /**
     * get App versionCode
     *
     * @param context
     * @return
     */
    public String getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

}
