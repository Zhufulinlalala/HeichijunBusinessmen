package com.hk.lang_data_manager.base;

/**
 * Created by Administrator on 2016/10/31 0031.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.hk.lang_data_manager.R;
import com.hk.lang_data_manager.utils.ImeUtils;
import com.hk.lang_data_manager.utils.MySharedPreference;
import com.hk.lang_data_manager.utils.ResourcesUtils;
import com.hk.lang_data_manager.utils.ToastUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder mUnbinder;
    protected SaflyApplication application;

    //判断是否符合正则表达式
    public boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    //TODO  规避空指针，对于生成的Model中的int需要手动批量替换成Integer
    public String notNullString(Object o)
    {
        if (!isNull(o))
            return o.toString();
        else
            return "";
    }
    //从字符串中解析出Model，需要强转
    public Object getModelFromStr(String str,Class<?> cls)
    {
        JSONObject jsonObject = JSONObject.parseObject(str);
        return JSONObject.toJavaObject(jsonObject,cls);
    }
    //把Model转成字符串
    public String getJsonStrFromModel(Object model)
    {
        return JSON.toJSONString(model);
    }
    /**把object类型数据转为string 适用于Map，List等
     * @param object
     * @return
     */
    public String getStringFromObject(Object object){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos=null;
        try {
            oos=new ObjectOutputStream(baos);
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String newWord = new String(Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT));
        return newWord;
    }
    /**把string类型再转为object 适用于Map，List等
     * @param data
     * @return
     */
    public Object getObjectFromString(String data){
        byte[] bytes = Base64.decode(data, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    //返回适配后的字体大小(dp)
    public float getTextSize(int size)
    {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return size*(dm.widthPixels/(dm.density*360));
    }
    //加载图片，传入Imageview和图片网址，加载出错图为正方形破碎图像
    public void loadImg(ImageView iv,Object url)
    {
        String newUrl = "";
        if (url!=null && !TextUtils.isEmpty(url.toString()))
        {
            if (url.toString().indexOf("http")!=-1)//有http字符串
                newUrl = url.toString();
            else
                newUrl = Constant.URL+url.toString();
        }
        Glide.with(this).load(newUrl).error(R.mipmap.head1).into(iv);
    }
    //从适配后的dimen中获取尺寸大小（px）
    public int getDimen(int dimen)
    {
        return getResources().getDimensionPixelSize(ResourcesUtils.getDimen("m"+dimen,this));
    }
    public boolean isEmpty(String s)
    {
        if (TextUtils.isEmpty(s))
            return true;
        else
            return false;
    }
    public boolean isNull(Object o)
    {
        if (o == null)
            return true;
        else
            return false;
    }
    public int dp2px(Context context, int values) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (values * scale + 0.5f);
    }
    public int px2dp(Context context, float pxValue) {  
        float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }
    public void Loge(String msg){
        Log.e("mm",msg);
    }
    public void Loge(Object msg){
        Log.e("mm",msg.toString());
    }
    public void toastCenter(String s)
    {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public void toast(String s) {
        ToastUtil.showToast(SaflyApplication.getInstance(),s);
    }
    public void toast(Object s) {
        ToastUtil.showToast(SaflyApplication.getInstance(),s.toString());
    }
    public String getTagString()
    {
        return getIntent().getStringExtra("tag");
    }
    public void jump2Activity(Class c)
    {
        startActivity(new Intent(this,c));
    }
    public void jump2Activity(String s,Class c)
    {
        Intent i = new Intent(this,c);
        i.putExtra("tag",s);
        startActivity(i);
    }
    //TODO  自带透明状态栏
    @TargetApi(19)
    public void transparencyBar(Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window =activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeSetContentView();
        if (application == null) {
            // 得到Application对象
            application = (SaflyApplication) getApplication();
        }
        application.addActivity_(this);// 调用添加方法
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutId());
        changeStatusBarColor();
        mUnbinder = ButterKnife.bind(this);
        setUpView();
    }
    protected void changeStatusBarColor()
    {
        if (TextUtils.equals(MySharedPreference.get("canWhite"),"true"))
            StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.white),true);
        else if (TextUtils.equals(MySharedPreference.get("canWhite"),"trueMIUI"))
        {
            //MIUI9之前适配
            Class<? extends Window> clazz = getWindow().getClass();
            try {
                int darkModeFlag = 0;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(getWindow(), true ? darkModeFlag : 0, darkModeFlag);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //MIUI9适配
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        else if (TextUtils.equals(MySharedPreference.get("canWhite"),"false"))
            StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.black),false);
    }
    @Override
    public void finish() {
        Log.e("BaseActivity", "finish");
        application.removeActivity_(this);
    }

    public void realFinish() {
        super.finish();
    }

    protected void finishAll() {
        application.removeALLActivity_();
        System.exit(0);
    }

    /**
     * 在setContentView之前进行设置
     */
    protected void doBeforeSetContentView() {
        // ignore 子类需要时重写dd

    }

    /**
     * 应用被强杀 重启APP
     */
    protected void protectApp() {

    }

    /**
     * 获取布局id
     */
    protected abstract int getLayoutId();

    /**
     * 进行界面操作
     */
    protected abstract void setUpView();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }
    protected boolean checkPermission(String permissionName) {
        String permission = permissionName; //你要判断的权限名字
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    /**
     * 清除editText的焦点
     *
     * @param v   焦点所在View
     * @param ids 输入框
     */
    public void clearViewFocus(View v, int... ids) {
        if (null != v && null != ids && ids.length > 0) {
            for (int id : ids) {
                if (v.getId() == id) {
                    v.clearFocus();
                    break;
                }
            }
        }


    }

    /**
     * 隐藏键盘
     *
     * @param v   焦点所在View
     * @param ids 输入框
     * @return true代表焦点在edit上
     */
    public boolean isFocusEditText(View v, int... ids) {
        if (v instanceof EditText) {
            EditText tmp_et = (EditText) v;
            for (int id : ids) {
                if (tmp_et.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    //是否触摸在指定view上面,对某个控件过滤
    public boolean isTouchView(View[] views, MotionEvent ev) {
        if (views == null || views.length == 0) return false;
        int[] location = new int[2];
        for (View view : views) {
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            if (ev.getX() > x && ev.getX() < (x + view.getWidth())
                    && ev.getY() > y && ev.getY() < (y + view.getHeight())) {
                return true;
            }
        }
        return false;
    }

    //是否触摸在指定view上面,对某个控件过滤
    public boolean isTouchView(int[] ids, MotionEvent ev) {
        int[] location = new int[2];
        for (int id : ids) {
            View view = findViewById(id);
            if (view == null) continue;
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            if (ev.getX() > x && ev.getX() < (x + view.getWidth())
                    && ev.getY() > y && ev.getY() < (y + view.getHeight())) {
                return true;
            }
        }
        return false;
    }
    //endregion

    //region 右滑返回上级


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isTouchView(filterViewByIds(), ev)) return super.dispatchTouchEvent(ev);
            if (hideSoftByEditViewIds() == null || hideSoftByEditViewIds().length == 0)
                return super.dispatchTouchEvent(ev);
            View v = getCurrentFocus();
            if (isFocusEditText(v, hideSoftByEditViewIds())) {
                if (isTouchView(hideSoftByEditViewIds(), ev))
                    return super.dispatchTouchEvent(ev);
                //隐藏键盘
                ImeUtils.hideSoftKeyboard(this);
                clearViewFocus(v, hideSoftByEditViewIds());

            }
        }
        return super.dispatchTouchEvent(ev);

    }

    /**
     * 传入EditText的Id
     * 没有传入的EditText不做处理
     *
     * @return id 数组
     */
    public int[] hideSoftByEditViewIds() {
        return null;
    }

    /**
     * 传入要过滤的View
     * 过滤之后点击将不会有隐藏软键盘的操作
     *
     * @return id 数组
     */
    public View[] filterViewByIds() {
        return null;
    }
}