package com.hk.heichijun.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.heichijun.R;
import com.hk.heichijun.base.AppBack;
import com.hk.heichijun.base.BaseActivity;
import com.hk.heichijun.base.Constant;
import com.hk.heichijun.base.OKHttpUICallback;
import com.hk.heichijun.base.OkHttpManger;
import com.hk.heichijun.utils.MySharedPreference;
import com.hk.heichijun.utils.MyViewHolder;
import com.hk.heichijun.utils.PushPopup2Window;
import com.hk.heichijun.utils.PushPopupWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lanit on 2018/4/26.
 */
// 盘  点
public class TongActivity extends BaseActivity {
    @BindView(R.id.rc_list)
    RecyclerView rcList;
    @BindView(R.id.tv_time_sel)
    TextView tvTime;
    private List<Map<String,Object>> list=new ArrayList<>();
    private List<Map<String,Object>> listSon=new ArrayList<>();
    private Map<String,Object> maps=new HashMap<>();
    private MyAdapter myAdapter;
    private PopStyleIphoneDate popStyleIphoneDate;
    private String date = "";
    private String dateEnd = "";
    private int page = 1;
    private boolean hasNext = false;
    private boolean next = false;
    private boolean under=false;
    private boolean color=false;
    private boolean time=false;
    private PopTime popTime;
    private boolean flag = true ;  //是否 弹出来 了 上午下午的弹出框
    private boolean flag1 = true;   //日历的选择成功 之后  按了 确定按钮
    private TextView tvall;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_tong;
    }

    @Override
    protected void setUpView() {
        inits();
        getView("0");
    }
    private void inits(){
        myAdapter=new MyAdapter(this,list);
        final LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        rcList.setLayoutManager(layoutManager);
        rcList.setAdapter(myAdapter);
        rcList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int last= layoutManager.findLastVisibleItemPosition();
                if (last > list.size() - 3 && hasNext) {
                    page++;
                    Log.e("page",page+"");
                    if (next) {
                        getView("0");
                    }
                }
            }
        });
    }
    private void inits2(){
        myAdapter=new MyAdapter(this,list);
        final LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        rcList.setLayoutManager(layoutManager);
        rcList.setAdapter(myAdapter);
        rcList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int last= layoutManager.findLastVisibleItemPosition();
                if (last > list.size() - 3 && hasNext) {
                    page++;
                    Log.e("page",page+"");
                    if (next) {
                        getView2(date,dateEnd);
                    }
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.tv_dayin,R.id.tv_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_dayin:
//                if (popStyleIphoneDate==null){
//                    popStyleIphoneDate=new PopStyleIphoneDate(this);
//                }
                if (popStyleIphoneDate == null || !popStyleIphoneDate.isShowing()) {
                    popStyleIphoneDate = new PopStyleIphoneDate(this);
                    //    }
                }

                popStyleIphoneDate.show(this);
                break;
            case R.id.tv_time: //判断  右上角 全部
                 if (flag){
                    // list.clear();
                     //getView("0");
                        if (popTime==null){
                            popTime=new PopTime(this);
                        }
                        popTime.show(this);

                }else{
                     time = false;
                     list.clear();
                     myAdapter.notifyDataSetChanged();

                     getView("0");
                     if (!flag1){
                         tvTime.setText("全部");
                         flag=true;
                     }else{
                         list.clear();
                         return;
                     }
                 }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!flag1){
            tvTime.setText("全部");
        }
    }

    //今日
    private void getView(String type){
        next = false;
        Map<String,String> map=new HashMap<>();
        map.put("adminId", MySharedPreference.getAdminId());
        map.put("token", MySharedPreference.getToken());
        map.put("cinemaId", MySharedPreference.getCinemaId());
        map.put("page", page + "");
        map.put("timeType", type);
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/getViewAttr", new OKHttpUICallback.ResultCallback<AppBack>() {

            @Override
            public void onSuccess(AppBack result) {
                if (result.isSuccess()){
                    Map<String,Object> maps=result.getMap();
                    List<Map<String,Object>> lists= (List<Map<String, Object>>) maps.get("list");
                    if (maps.get("hasNextPage").equals(true))
                        hasNext = true;
                    else
                        hasNext = false;
                    next = true;
                    if (page==1){
                        list.clear();}
                    list.addAll(lists);
                    myAdapter.notifyDataSetChanged();
                }
            }
        },map);
    }
    /*点开 日历  选择日期 时候的 请求数据    timetype  是 1上2下3午 0全部   */
    private void getView2(String dates,String dateEnds){
        next = false;
        date=dates;
        dateEnd=dateEnds;
        Map<String,String> map=new HashMap<>();
        map.put("adminId", MySharedPreference.getAdminId());
        map.put("cinemaId", MySharedPreference.getCinemaId());
        map.put("token", MySharedPreference.getToken());
        map.put("page", page + "");
        map.put("startTime", dates + " 00:00:00");
        map.put("endTime", dateEnds + " 23:59:59");
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/getViewAttr", new OKHttpUICallback.ResultCallback<AppBack>() {

            @Override
            public void onSuccess(AppBack result) {
                if (result.isSuccess()){
                    Map<String,Object> maps=result.getMap();
                    List<Map<String,Object>> lists= (List<Map<String, Object>>) maps.get("list");
                    if (maps.get("hasNextPage").equals(true))
                        hasNext = true;
                    else
                        hasNext = false;
                    next = true;
                    if (page==1){
                        list.clear();}
                    list.addAll(lists);
                    myAdapter.notifyDataSetChanged();
                }
            }
        },map);
    }
    /*点 上午  下午  晚上  的时候的 方法*/
    private void getView3(String dates,String dateEnds,String timeType){
        next = false;
        date=dates;
        dateEnd=dateEnds;
        Map<String,String> map=new HashMap<>();
        map.put("adminId", MySharedPreference.getAdminId());
        map.put("cinemaId", MySharedPreference.getCinemaId());
        map.put("token", MySharedPreference.getToken());
        map.put("page", page + "");
        map.put("timeType", timeType);
        map.put("startTime", dates + " 00:00:00");
        map.put("endTime", dateEnds + " 23:59:59");
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/getViewAttr", new OKHttpUICallback.ResultCallback<AppBack>() {

            @Override
            public void onSuccess(AppBack result) {
                if (result.isSuccess()){
                    Map<String,Object> maps=result.getMap();
                    List<Map<String,Object>> lists= (List<Map<String, Object>>) maps.get("list");
                    if (maps.get("hasNextPage").equals(true))
                        hasNext = true;
                    else
                        hasNext = false;
                    next = true;
                    if (page==1){
                        list.clear();}
                    list.addAll(lists);
                    myAdapter.notifyDataSetChanged();
                }
            }
        },map);
    }
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private Context context;
        private List<Map<String,Object>> data;
        private LayoutInflater inflater;
        private MyAdapterSon myAdapterSon;
        private MyAdapter(Context context,List<Map<String,Object>> data){
            this.context=context;
            this.data=data;
            inflater=LayoutInflater.from(context);
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=inflater.inflate(R.layout.item_shoplist1,parent,false);
            MyViewHolder myViewHolder= new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            RecyclerView rcList2= (RecyclerView) holder.getView(R.id.rc_list2);
            TextView tvName= (TextView) holder.getView(R.id.tv_good_no);
            TextView tvNum= (TextView) holder.getView(R.id.tv_good_num);
            TextView tvMoney= (TextView) holder.getView(R.id.tv_good_money);
            View vLine=holder.getView(R.id.v_line);
            listSon= (List<Map<String, Object>>) data.get(position).get("attrList");
            if (color==false){
                vLine.setVisibility(View.GONE);
            }
            else{
                vLine.setVisibility(View.VISIBLE);
            }
            if (listSon.size()>0){
                color=true;
            }else{
                color=false;
            }
            if (position+1==data.size()){
                under=true;
            }else{
                under=false;
            }
            tvName.setText(data.get(position).get("goods_name").toString());
            tvNum.setText("("+data.get(position).get("t").toString()+")");
            tvMoney.setText("("+data.get(position).get("sum_price").toString()+")");
            myAdapterSon=new MyAdapterSon(context,listSon);
            rcList2.setLayoutManager(new LinearLayoutManager(context));
            rcList2.setAdapter(myAdapterSon);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
    private class MyAdapterSon extends RecyclerView.Adapter<MyViewHolder>{
        private Context context;
        private List<Map<String,Object>> data;
        private LayoutInflater inflater;
        private MyAdapterSon(Context context,List<Map<String,Object>> data){
            this.context=context;
            this.data=data;
            inflater=LayoutInflater.from(context);
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=inflater.inflate(R.layout.item_shoplist2,parent,false);
            MyViewHolder myViewHolder= new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            TextView tvName= (TextView) holder.getView(R.id.tv_goods_name);
            TextView tvNum= (TextView) holder.getView(R.id.tv_nums);
            TextView tvMoney= (TextView) holder.getView(R.id.tv_moneys);
            View vLine1=holder.getView(R.id.v_line1);
            View vLine2=holder.getView(R.id.v_line2);
            View vLine3=holder.getView(R.id.v_line3);
            View vUnder=holder.getView(R.id.v_under);
            if (position==0){
                vLine1.setVisibility(View.GONE);
                vLine2.setVisibility(View.GONE);
                vLine3.setVisibility(View.GONE);
            }else{
                vLine1.setVisibility(View.VISIBLE);
                vLine2.setVisibility(View.VISIBLE);
                vLine3.setVisibility(View.VISIBLE);
            }
            if (position+1==data.size()&&under==true){
                vUnder.setVisibility(View.VISIBLE);
            }else{
                vUnder.setVisibility(View.GONE);
            }
            tvName.setText(String.valueOf(data.get(position).get("attr_name")));
            tvNum.setText(data.get(position).get("t").toString());
            tvMoney.setText(data.get(position).get("sum_price").toString());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
    public class PopStyleIphoneDate extends PushPopupWindow {

        DatePicker cvDate;
        TextView tvStart;
        View vH;
        TextView tvEnd;
        TextView tvOne;
        TextView tvMore;
        TextView tvSummit;
        TextView tvDiss;

        private int DATE_SELECT = 0;
        private int DATE_DAY = 0;

        private String date;
        private String dateend, datestart;
        private int year;
        private int month;
        private int day;

        public PopStyleIphoneDate(Context context) {
            super(context);
            initView();
            setOutsideTouchable(true);
           // time=true;
        }


        @Override
        protected View generateCustomView() {
            View root = View.inflate(context, R.layout.activity_date_picker_sel, null);
            Calendar c =Calendar.getInstance();
            year =c.get(Calendar.YEAR);
            month=c.get(Calendar.MONTH);
            day=c.get(Calendar.DAY_OF_MONTH);
            cvDate=root.findViewById(R.id.cv_date);
            tvStart=root.findViewById(R.id.tv_start);
            tvEnd=root.findViewById(R.id.tv_end);
            tvOne=root.findViewById(R.id.tv_one);
            tvMore=root.findViewById(R.id.tv_more);
            tvSummit=root.findViewById(R.id.tv_summit);
            vH=root.findViewById(R.id.v_h);
            tvDiss=root.findViewById(R.id.tv_diss);
            tvDiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            tvOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getOne();
                }
            });
            tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getMore();
                }
            });
            showDate();
            tvSummit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (DATE_SELECT == 0) {
                        dateend = date;
                        datestart=date;
                        tvTime.setText("今日");
                        flag=false;
                        flag1 = false;
                        inits2();
                        page = 1;
                        getView2(datestart,dateend);
                        datestart=null;
                        dateend=null;
                        dismiss();
                    } else if (DATE_SELECT == 1) {
                        if (datestart!=null&&dateend!=null) {
                            inits2();
                            flag = false;
                            flag1 = false;
                            tvTime.setText("今日");
                            page = 1;
                            getView2(datestart, dateend);
                            datestart = null;
                            dateend = null;
                            dismiss();
                        }else {
                            toast("选择日期不能为空");
                        }
                    }
                }
            });
            return root;
        }

        private void showDate() {
            /**
             * 默认赋值
             */
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
//获取当前时间
            Date date1 = new Date(System.currentTimeMillis());
            date=simpleDateFormat.format(date1);
            tvStart.setText(date);
            tvEnd.setText("");
            datestart = date;

            cvDate.init(year, month, day, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                    date = i + "-" + (i1 + 1) + "-" + i2;
                    if (DATE_DAY == 0) {
                        tvStart.setText(date);
                        tvEnd.setText("");
                        datestart = date;
                        DATE_DAY = 1;
                    }
                    /*这块需要加上 判断 时间 如果早于 前一个  责 选择 提示 ，不能早于之前
                    * */
                    else if (DATE_DAY == 1) {
                        tvEnd.setText(date);
                        dateend = date;
                        DATE_DAY = 0;
                    }
                }
            });
        }
        /**
         * 显示在界面的中间
         */
        public void show(Activity activity) {
            showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        }

        @OnClick({R.id.tv_one, R.id.tv_more, R.id.tv_summit})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.tv_one:
                    getOne();
                    break;
                case R.id.tv_more:
                    getMore();
                    break;
            }
        }

        /*点开日历的单日*/
        private void getOne() {
            tvOne.setBackgroundResource(R.drawable.date_bg);
            tvMore.setBackgroundResource(R.drawable.date_bg4);
            tvStart.setVisibility(View.INVISIBLE);
            tvEnd.setVisibility(View.INVISIBLE);
            vH.setVisibility(View.INVISIBLE);
            DATE_SELECT = 0;
            DATE_DAY = 0;
        }
/*点开日历的  多日*/
        private void getMore() {
            tvStart.setVisibility(View.VISIBLE);
            tvEnd.setVisibility(View.VISIBLE);
            vH.setVisibility(View.VISIBLE);
            tvStart.setText("");
            tvEnd.setText("");
            DATE_SELECT = 1;
            DATE_DAY = 0;
            tvOne.setBackgroundResource(R.drawable.date_bg2);
            tvMore.setBackgroundResource(R.drawable.date_bg3);
        }
    }

    private class PopTime extends PushPopup2Window {

        public PopTime(Context context) {
            super(context);
            initView();
            setOutsideTouchable(true);
        }

        @Override
        protected View generateCustomView() {
            View view=View.inflate(context,R.layout.poptime,null);
            final LinearLayout llshangwu=view.findViewById(R.id.ll_shangwu);
            final LinearLayout llxiawu=view.findViewById(R.id.ll_xiawu);
            final LinearLayout llnight=view.findViewById(R.id.ll_night);
            final LinearLayout llall=view.findViewById(R.id.ll_all);
            final TextView tv_1 = view.findViewById(R.id.tv_1);
            final TextView tv_2 = view.findViewById(R.id.tv_2);
            final TextView tv_3 = view.findViewById(R.id.tv_3);
            final TextView tv_4 = view.findViewById(R.id.tv_4);
            final TextView tv_5 = view.findViewById(R.id.tv_5);
            final TextView tv_6 = view.findViewById(R.id.tv_6);
            tvall = view.findViewById(R.id.tv_all);
            llshangwu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv_1.setTextColor(getResources().getColor(R.color.white));
                    tv_2.setTextColor(getResources().getColor(R.color.white));
                    tv_3.setTextColor(getResources().getColor(R.color.black));
                    tv_4.setTextColor(getResources().getColor(R.color.black));
                    tv_5.setTextColor(getResources().getColor(R.color.black));
                    tv_6.setTextColor(getResources().getColor(R.color.black));
                    tvall.setTextColor(getResources().getColor(R.color.black));
                    llshangwu.setBackgroundColor(getResources().getColor(R.color.gray));
                    llxiawu.setBackgroundColor(getResources().getColor(R.color.moren1));
                    llnight.setBackgroundColor(getResources().getColor(R.color.moren1));
                    llall.setBackgroundColor(getResources().getColor(R.color.moren1));
                    if (time){
                        inits2();
                        page = 1;
                        getView3(date,dateEnd,"1");}
                    else{
                        inits();
                        page = 1;
                        getView("1");}
                    tvTime.setText("上午");
                    dismiss();
                }
            });
            llxiawu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv_3.setTextColor(getResources().getColor(R.color.white));
                    tv_4.setTextColor(getResources().getColor(R.color.white));
                    tv_1.setTextColor(getResources().getColor(R.color.black));
                    tv_2.setTextColor(getResources().getColor(R.color.black));
                    tv_5.setTextColor(getResources().getColor(R.color.black));
                    tv_6.setTextColor(getResources().getColor(R.color.black));
                    tvall.setTextColor(getResources().getColor(R.color.black));
                    llshangwu.setBackgroundColor(getResources().getColor(R.color.moren1));
                    llxiawu.setBackgroundColor(getResources().getColor(R.color.gray));
                    llnight.setBackgroundColor(getResources().getColor(R.color.moren1));
                    llall.setBackgroundColor(getResources().getColor(R.color.moren1));
                    if (time){inits2();
                        page = 1;
                        getView3(date,dateEnd,"2");}
                    else{inits();
                        page = 1;
                        getView("2");}
                    tvTime.setText("下午");
                    dismiss();
                }
            });
            llnight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv_5.setTextColor(getResources().getColor(R.color.white));
                    tv_6.setTextColor(getResources().getColor(R.color.white));
                    tv_1.setTextColor(getResources().getColor(R.color.black));
                    tv_2.setTextColor(getResources().getColor(R.color.black));
                    tv_3.setTextColor(getResources().getColor(R.color.black));
                    tv_4.setTextColor(getResources().getColor(R.color.black));
                    tvall.setTextColor(getResources().getColor(R.color.black));
                    llshangwu.setBackgroundColor(getResources().getColor(R.color.moren1));
                    llxiawu.setBackgroundColor(getResources().getColor(R.color.moren1));
                    llnight.setBackgroundColor(getResources().getColor(R.color.gray));
                    llall.setBackgroundColor(getResources().getColor(R.color.moren1));
                    if (time){inits2();
                        page = 1;
                        getView3(date,dateEnd,"3");}
                    else{inits();
                        page = 1;
                        getView("3");}
                    tvTime.setText("晚上");
                    dismiss();
                }
            });
            llall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv_1.setTextColor(getResources().getColor(R.color.black));
                    tv_2.setTextColor(getResources().getColor(R.color.black));
                    tv_3.setTextColor(getResources().getColor(R.color.black));
                    tv_4.setTextColor(getResources().getColor(R.color.black));
                    tv_5.setTextColor(getResources().getColor(R.color.black));
                    tv_6.setTextColor(getResources().getColor(R.color.black));
                    tvall.setTextColor(getResources().getColor(R.color.white));
                    llshangwu.setBackgroundColor(getResources().getColor(R.color.moren1));
                    llxiawu.setBackgroundColor(getResources().getColor(R.color.moren1));
                    llnight.setBackgroundColor(getResources().getColor(R.color.moren1));
                    llall.setBackgroundColor(getResources().getColor(R.color.gray));
                    if (time){inits2();
                        page = 1;
                        getView2(date,dateEnd);}
                    else{inits();
                        page = 1;
                        getView("0");}
                    tvTime.setText("全部");
                    dismiss();
                }
            });
            return view;
        }

        public void show(Activity activity){
            showAtLocation(activity.getWindow().getDecorView(), Gravity.TOP , 0, 0);
        }

    }
}
