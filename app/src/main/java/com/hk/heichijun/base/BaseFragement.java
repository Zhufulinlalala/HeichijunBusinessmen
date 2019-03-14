package com.hk.heichijun.base;

/**
 * Created by Administrator on 2016/10/31 0031.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.hk.heichijun.R;
import com.hk.heichijun.utils.ResourcesUtils;
import com.hk.heichijun.utils.ToastUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragement extends Fragment {
    private Unbinder mUnbinder;
    protected View view;
    
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
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(getLayoutId(), container, false);
        mUnbinder= ButterKnife.bind(this, view);
        setUpView();
        return view;
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
        Glide.with(getActivity()).load(newUrl).error(R.mipmap.head1).into(iv);
    }
    //返回适配后的字体大小(dp)
    public float getTextSize(int size)
    {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return size*(dm.widthPixels/(dm.density*360));
    }
    //从适配后的dimen中获取尺寸大小（px）
    public int getDimen(int dimen)
    {
        return getResources().getDimensionPixelSize(ResourcesUtils.getDimen("m"+dimen,getActivity()));
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
    public void jump2Activity(Class c)
    {
        startActivity(new Intent(getActivity(),c));
    }
    public String getTagString()
    {
        return getActivity().getIntent().getStringExtra("tag");
    }
    public void jump2Activity(String s,Class c)
    {
        Intent i = new Intent(getActivity(),c);
        i.putExtra("tag",s);
        startActivity(i);
    }
    public void Loge(String msg){
        Log.e("mm",msg);
    }
    public void Loge(Object msg){
        Log.e("mm",msg.toString());
    }
    public void toastCenter(String s)
    {
        Toast toast = Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public void toast(String s) {
        ToastUtil.showToast(SaflyApplication.getInstance(),s);
    }
    public void toast(Object s) {
        ToastUtil.showToast(SaflyApplication.getInstance(),s.toString());
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }
}