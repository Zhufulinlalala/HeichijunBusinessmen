package com.hk.heichijun.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.heichijun.R;
import com.hk.heichijun.base.AppBack;
import com.hk.heichijun.base.BaseFragement;
import com.hk.heichijun.base.Constant;
import com.hk.heichijun.base.OKHttpUICallback;
import com.hk.heichijun.base.OkHttpManger;
import com.hk.heichijun.utils.MySharedPreference;
import com.hk.heichijun.utils.MyViewHolder;
import com.hk.heichijun.utils.PushPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
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
import butterknife.Unbinder;

/*  数据 页面 */
public class SumFragment extends BaseFragement {
    @BindView(R.id.tv_num_money)
    TextView tvNumMoney;
    @BindView(R.id.tv_num_t)
    TextView tvNumT;
    @BindView(R.id.tv_num_order)
    TextView tvNumOrder;
    @BindView(R.id.tv_order_t)
    TextView tvOrderT;
    @BindView(R.id.tv_num_people)
    TextView tvNumPeople;
    @BindView(R.id.tv_people_t)
    TextView tvPeopleT;
    @BindView(R.id.ll_date)
    LinearLayout llDate;
    Unbinder unbinder1;
    private List<Map<String, Object>> listItem = new ArrayList<>();
    @BindView(R.id.lv_detail)
    RecyclerView lvDetail;
    MyAdapter myAdpter, myAdpter2;
    private List<Map<String, Object>> giftsItem = new ArrayList<>();
    public String dates;
    private int page = 0;
    private boolean hasNext = false;
    private boolean next = false;
    private SwipeRefreshLayout mSwipeLayout;
    private boolean isRefresh = false;
    private boolean refresh = false;
    private String date = "";
    private String dateEnd = "";
    private PopStyleIphoneDate popStyleIphoneDate;

    @Override
    protected int getLayoutId() {
        return R.layout.data_count;
    }

    @Override
    @Subscribe
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dates = simpleDateFormat.format(System.currentTimeMillis());
        initList();
        page=1;
        loadData();//服务
        loadData2();//金额

        mSwipeLayout = view.findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefresh) {
                    isRefresh = true;
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mSwipeLayout.setRefreshing(false);
                            page = 1;
                            hasNext = false;
                            initList();
                            loadData();
                            loadData2();
                            isRefresh = false;
                        }
                    }, 2000);
                }
            }
        });
        LinearLayout resert = (LinearLayout) getActivity().findViewById(R.id.iv_resert);
        resert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 1;
                hasNext = false;

                loadData();
                loadData2();
                initList();
            }
        });
        llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), DatePickerSelActivity.class);
//                startActivityForResult(intent, 1);
//                if(popStyleIphoneDate==null){
//                    popStyleIphoneDate=new PopStyleIphoneDate(getActivity());
//                }
                if (popStyleIphoneDate == null || !popStyleIphoneDate.isShowing()) {
                    popStyleIphoneDate = new PopStyleIphoneDate(getActivity());
                    //    }
                }
                popStyleIphoneDate.show(getActivity());
            }
        });
        unbinder1 = ButterKnife.bind(this, root);
        return root;
    }

    @Subscribe
    public void setUpView() {

    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String s) {
        //类名+方法/事件名
        if (s.equals("sum")) {
            loadData2();
            page = 1;
            initList();
            loadData();
        }
    }
    //加载数据
    private void loadData() {
        next = false;
        refresh = true;
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("cinemaId", MySharedPreference.getCinemaId());
        parmsMap.put("showDetail", "1");
        parmsMap.put("page", page + "");
        parmsMap.put("rankType", "0");
        parmsMap.put("today", "1");
        parmsMap.put("statusOver","1");
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/list", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    Map<String, Object> items = (Map<String, Object>) appBack.getResult();
                    List<Map<String, Object>> list = (List<Map<String, Object>>) items.get("list");
                    if (page == 1){
                        listItem.clear();
                    }
                    for (Map<String, Object> item : list) {
                        listItem.add(item);
                    }
                    if (items.get("hasNextPage").equals(true))
                        hasNext = true;
                    else
                        hasNext = false;
                    next = true;
                    myAdpter.notifyDataSetChanged();
                    mSwipeLayout.setRefreshing(false);
                }
            }
        }, parmsMap);

    }
    //时间
    private void loadData4(String dates, String date2) {
        next = false;
        date=dates;
        dateEnd=date2;
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("cinemaId", MySharedPreference.getCinemaId());
        parmsMap.put("showDetail", "1");
        parmsMap.put("page", page + "");
        parmsMap.put("startTime", date + " 00:00:00");
        parmsMap.put("endTime", dateEnd + " 23:59:59");
        parmsMap.put("rankType", "0");
        parmsMap.put("statusOver","1");
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/list", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    Map<String, Object> items = (Map<String, Object>) appBack.getResult();
                    List<Map<String, Object>> list = (List<Map<String, Object>>) items.get("list");
                    if (page == 1) {
                        listItem.clear();
                    }
                    for (Map<String, Object> item : list) {
                        listItem.add(item);
                    }
                    if (items.get("hasNextPage").equals(true))
                        hasNext = true;
                    else
                        hasNext = false;
                    next = true;
                    myAdpter2.notifyDataSetChanged();

                }
            }
        }, parmsMap);

    }
    //金额
    private void loadData2() {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("cinemaId", MySharedPreference.getCinemaId());
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/getOverView", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    Map<String, Object> item = new HashMap<>();
                    Map<String, Object> items = new HashMap<>();
                    Map<String, Object> items2 = new HashMap<>();
                    item.clear();
                    items2.clear();
                    items.clear();
                    item = appBack.getMap();
                    items = (Map<String, Object>) item.get("today");
                    items2 = (Map<String, Object>) item.get("yestoday");
                    DecimalFormat df = new DecimalFormat("0.00");
                    DecimalFormat df2 = new DecimalFormat("0");
                    try{
                        tvNumMoney.setText(df.format(Double.parseDouble(notNullString(items.get("total_price")))).toString());
                        tvNumOrder.setText(df2.format(Double.parseDouble(items.get("count").toString())).toString());
                        tvNumPeople.setText(df.format(Double.parseDouble(items.get("unit_price").toString())).toString());
                        tvNumT.setText("昨日 " + df.format(Double.parseDouble(items2.get("total_price").toString())).toString());
                        tvOrderT.setText("昨日 " + df2.format(Double.parseDouble(items2.get("count").toString())).toString());
                        tvPeopleT.setText("昨日 " + df.format(Double.parseDouble(items2.get("unit_price").toString())).toString());}
                    catch (Exception e){

                    }
                }
            }
        }, parmsMap);

    }

    private void loadData3(String date, final String date2) {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("cinemaId", MySharedPreference.getCinemaId());
        parmsMap.put("startTime", date);
        parmsMap.put("endTime", date2);
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/getOverView", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    Map<String, Object> item = appBack.getMap();
                    Map<String, Object> items = (Map<String, Object>) item.get("data");
                    DecimalFormat df = new DecimalFormat("0.00");
                    DecimalFormat df2 = new DecimalFormat("0");
                    tvNumMoney.setText(df.format(Double.parseDouble(items.get("total_price").toString())).toString());
                    tvNumOrder.setText(df2.format(Double.parseDouble(items.get("count").toString())).toString());
                    tvNumPeople.setText(df.format(Double.parseDouble(items.get("unit_price").toString())).toString());
                    tvNumT.setText("平均 " + df.format(Double.parseDouble(items.get("avgTotalPrice").toString())).toString());
                    tvOrderT.setText("平均 " + df2.format(Double.parseDouble(items.get("avgCount").toString())).toString());
                    tvPeopleT.setText("平均 " + df.format(Double.parseDouble(items.get("avgTotalPrice").toString()) / Double.parseDouble(items.get("avgCount").toString())).toString());

                }
            }
        }, parmsMap);

    }

    void initList() {
        myAdpter = new MyAdapter(getActivity(), listItem);
        final LinearLayoutManager LayoutManager = new LinearLayoutManager(getActivity());
        lvDetail.setLayoutManager(LayoutManager);
        lvDetail.setAdapter(myAdpter);
        lvDetail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int last = LayoutManager.findLastVisibleItemPosition();
                if (last > listItem.size() - 3 && hasNext) {
                    page++;
                    loadData();
                    if (next) {

                    }
                }
            }
        });
    }

    void initList2() {
        myAdpter2 = new MyAdapter(getActivity(), listItem);
        final LinearLayoutManager LayoutManager = new LinearLayoutManager(getActivity());
        lvDetail.setLayoutManager(LayoutManager);
        lvDetail.setAdapter(myAdpter2);
        lvDetail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int last = LayoutManager.findLastVisibleItemPosition();
                if (last > listItem.size() - 3 && hasNext) {
                    Log.v("111111","2222222222");
                    page++;
                    loadData4(date, dateEnd);
                    if (next) {

                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }


    private void getReturn(int index) {
        Map map = myAdpter.data.get(index);
        map.put("status", -1);
        myAdpter.notifyItemChanged(index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            int i = 0;
            String id = data.getStringExtra("id");
            for (i = 0; ; i++) {
                String id2 = listItem.get(i).get("id").toString();
                if (id.equals(id2)) {
                    break;
                }
            }
            getReturn(i);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {
        public List<Map<String, Object>> items;
        private MyAdapterSon myAdapters;
        private MyAdapterGoods goodmyAdpter;
        RecyclerView lvDetail;
        RecyclerView rlZb;
        LinearLayout llStatus;
        ImageView ivStatus;
        TextView tvStatus;
        TextView tvStatusDo;
        private List<Map<String, Object>> data;
        private LayoutInflater inflater;
        private Context context;

        public MyAdapter(Context context, List<Map<String, Object>> data) {
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.item_order_detail, parent, false);
            final MyViewHolder holder = new MyViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.setIsRecyclable(false);

            TextView tv_vip = (TextView) holder.getView(R.id.tv_vip);
            //LinearLayout ll_tel = (LinearLayout) holder.getView(R.id.ll_tel);
            LinearLayout ll_tel1 = (LinearLayout) holder.getView(R.id.ll_tel1);
            TextView tvGetNum = (TextView) holder.getView(R.id.tv_get_num);
            TextView tvLocation = (TextView) holder.getView(R.id.tv_location);
            lvDetail = (RecyclerView) holder.getView(R.id.lv_detail);
            TextView tvSeatChange = (TextView) holder.getView(R.id.tv_seat_change);
            rlZb = (RecyclerView) holder.getView(R.id.lv_detail_gift);
            ivStatus = (ImageView) holder.getView(R.id.iv_status);
            TextView tvSeat = (TextView) holder.getView(R.id.tv_seat);
            TextView tvAllMoney = (TextView) holder.getView(R.id.tv_all_money);
            tvStatusDo = (TextView) holder.getView(R.id.tv_status_do);
            tvStatus = (TextView) holder.getView(R.id.tv_status);
            ImageView ivStatusBottom = (ImageView) holder.getView(R.id.iv_status_bottom);
            TextView tvStatusRight = (TextView) holder.getView(R.id.tv_status_right);
            LinearLayout llDetails = (LinearLayout) holder.getView(R.id.ll_details);
            LinearLayout llbG = (LinearLayout) holder.getView(R.id.ll_bg);
            llStatus = (LinearLayout) holder.getView(R.id.ll_status);
            LinearLayout llSeat = (LinearLayout) holder.getView(R.id.ll_seat);
            LinearLayout llSerive = (LinearLayout) holder.getView(R.id.ll_serive);
            TextView tvType = (TextView) holder.getView(R.id.tv_type);
            TextView tvThing = (TextView) holder.getView(R.id.tv_thing);
            TextView tvThing1 = (TextView) holder.getView(R.id.tv_thing1);
            TextView tvPhone = (TextView) holder.getView(R.id.tv_phone);
            TextView tvNote = (TextView) holder.getView(R.id.tv_note);
            ImageView ivld= (ImageView) holder.getView(R.id.iv_lingdang);
            LinearLayout llNote = (LinearLayout) holder.getView(R.id.ll_note);
            TextView tvNote2 = (TextView) holder.getView(R.id.tv_note2);
            final Map<String, Object> item = data.get(position);
            tvGetNum.setText(item.get("takeCode").toString());
            tvLocation.setText(notNullString(item.get("seat")));
            if (item.get("payType").equals(10)){
                tv_vip.setVisibility(View.VISIBLE);
            }
            tvAllMoney.setText(notNullString("实付:￥" + item.get("totalPrice")));
            if (item.get("orderType").equals(2)) {
                llbG.setBackgroundResource(R.drawable.bg_top);
                tvStatusDo.setText("送餐到位");
                llSeat.setVisibility(View.VISIBLE);
                lvDetail.setVisibility(View.VISIBLE);
                //llNote.setVisibility(View.VISIBLE);
                //start
                ivld.setVisibility(View.GONE);
                ll_tel1.setVisibility(View.GONE);
                //end
                try{
                    tvPhone.setText(notNullString(item.get("phone")));
                    tvNote.setText(notNullString(item.get("remark")));
                }catch (Exception e){

                }
                tvStatusDo.setTextColor(Color.parseColor("#ffa100"));
                if (item.get("status").equals(1)) {
                    if (item.get("cookStatus").equals(-3)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn6);
                        tvStatus.setText("已取消");
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.red);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("cookStatus").equals(-2)) {
                        ivStatus.setBackgroundResource(R.mipmap.mark);
                        tvStatusRight.setText("座位有误");
                        tvStatus.setText("异常");
                        tvStatus.setBackgroundResource(R.color.red);
                        ivStatusBottom.setVisibility(View.VISIBLE);
                        tvStatusRight.setVisibility(View.VISIBLE);
                    } else if (item.get("cookStatus").equals(-1)) {
                        ivStatus.setBackgroundResource(R.mipmap.mark);
                        tvStatusRight.setText("缺货");
                        tvStatus.setText("异常");
                        tvStatus.setBackgroundResource(R.color.red);
                        ivStatusBottom.setVisibility(View.VISIBLE);
                        tvStatusRight.setVisibility(View.VISIBLE);
                    } else if (item.get("cookStatus").equals(0)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn3);
                        tvStatus.setText("未接单");
                        tvStatus.setBackgroundResource(R.color.gray3);
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("cookStatus").equals(1)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn1);
                        tvStatus.setText("已接单");
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.green2);
                    } else if (item.get("cookStatus").equals(2)) {
                        ivStatus.setBackgroundResource(R.mipmap.finish);
                        tvStatus.setText("已完成");
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.green2);
                    } else if (item.get("cookStatus").equals(3)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn4);
                        tvStatus.setText("配送中");
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.green2);
                    }
                } else if (item.get("status").equals(-1)) {
                    ivStatus.setBackgroundResource(R.mipmap.btn5);
                    tvStatus.setText("已退款");
                    tvStatus.setBackgroundResource(R.color.red);
                    ivStatusBottom.setVisibility(View.GONE);
                    tvStatusRight.setVisibility(View.GONE);
                }
            } else if(item.get("orderType").equals(1)){
                llbG.setBackgroundResource(R.drawable.bg_top2);
                llSeat.setVisibility(View.INVISIBLE);
                tvStatusDo.setText("自助取餐");
                //ll_tel.setVisibility(View.GONE);
                lvDetail.setVisibility(View.VISIBLE);
                ll_tel1.setVisibility(View.GONE);
                // llNote.setVisibility(View.VISIBLE);
                tvNote.setText(notNullString(item.get("remark")));
                tvStatusDo.setTextColor(Color.parseColor("#74cd7a"));
                if (item.get("status").equals(1)) {
                    if (item.get("cookStatus").equals(-3)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn6);
                        tvStatus.setText("已取消");
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.red);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("cookStatus").equals(-1)) {
                        ivStatus.setBackgroundResource(R.mipmap.mark);
                        tvStatusRight.setText("缺货");
                        tvStatus.setText("异常");
                        tvStatus.setBackgroundResource(R.color.red);
                        ivStatusBottom.setVisibility(View.VISIBLE);
                        tvStatusRight.setVisibility(View.VISIBLE);
                    } else if (item.get("cookStatus").equals(0)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn3);
                        tvStatus.setText("未接单");
                        tvStatus.setBackgroundResource(R.color.gray3);
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("cookStatus").equals(1)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn4);
                        tvStatus.setText("已接单");
                        tvStatus.setBackgroundResource(R.color.green2);
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("cookStatus").equals(2)) {
                        ivStatus.setBackgroundResource(R.mipmap.finish);
                        tvStatus.setText("已完成");
                        tvStatus.setBackgroundResource(R.color.green2);
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("cookStatus").equals(3)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn4);
                        tvStatus.setText("已接单");
                        tvStatus.setBackgroundResource(R.color.green2);
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                    }
                } else if (item.get("status").equals(-1)) {
                    ivStatus.setBackgroundResource(R.mipmap.btn5);
                    tvStatus.setText("已退款");
                    tvStatus.setBackgroundResource(R.color.red);
                    ivStatusBottom.setVisibility(View.GONE);
                    tvStatusRight.setVisibility(View.GONE);
                }
            }
            else if (item.get("orderType").equals(4)) {  //外卖模式
                // "dada_status" int(11) DEFAULT '0' COMMENT '达达状态   未呼叫达达:0待接单＝1 待取货＝2 配送中＝3 已完成＝4 已取消＝5 已过期＝7 指派单=8 妥投异常之物品返回中=9 妥投异常之物品返回完成=10',
//                 "dada_cancel_reason" varchar(255) DEFAULT NULL COMMENT '达达取消原因',
//                "dada_cancel_from" int(11) DEFAULT '0' COMMENT '达达取消来源1:达达配送员取消；2:商家主动取消；3:系统或客服取消；0:默认值',
//                "dada_staff_id" varchar(32) DEFAULT NULL COMMENT '达达配送员员工',
//               "dada_staff_name" varchar(20) DEFAULT NULL COMMENT '达达配送员姓名',
                if (item.get("payType").equals(10)) {
                    tv_vip.setVisibility(View.VISIBLE);  //红色 （vip）
                }
                llSeat.setVisibility(View.GONE);         //座位号
                tvType.setText("取餐号：");// 取餐号
                tvAllMoney.setText(notNullString("实付: ￥" + item.get("totalPrice")));//   实付 ：
                llSerive.setVisibility(View.GONE);//  联系 方式 备注 那 一块
                lvDetail.setVisibility(View.VISIBLE);//  商品 recyclerview
                rlZb.setVisibility(View.VISIBLE);//   商品 recyclerview
                tvAllMoney.setVisibility(View.VISIBLE);//
                llbG.setBackgroundResource(R.drawable.bg_lanse_top);//  顶部 背景   蓝色  绿色 黄色
                tvSeat.setVisibility(View.GONE);//  座位号
                tvStatusDo.setText("外卖");//
                ivld.setVisibility(View.VISIBLE);//  座位号 旁边的小铃铛
                llNote.setVisibility(View.VISIBLE);//  真实 备注信息
                tvNote2.setText(notNullString(item.get("remark")));// 真实 备注信息 内容
                tvStatusDo.setTextColor(Color.parseColor("#1692ed"));//  外卖 字体颜色
                ivStatusBottom.setVisibility(View.GONE);
                if (item.get("status").equals(1)) { //已付款
                    if (item.get("isChangeSeat").equals(1)) {   //是否变更
                        tvSeatChange.setVisibility(View.VISIBLE);
                    } else {
                        tvSeatChange.setVisibility(View.GONE);
                    }
                    if (item.get("cookStatus").equals(0)) {  //尚未接单
                        ivStatus.setBackgroundResource(R.mipmap.btn3);
                        tvStatus.setText("未接单");
                        tvStatus.setBackgroundResource(R.color.gray3);
                        tvStatusRight.setVisibility(View.GONE);
                    }

                    if (item.get("dadaStatus").equals(7)) { //已过期订单
                        tvStatus.setText("异常");
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.red);
                        tvStatusRight.setVisibility(View.VISIBLE);
                        ivStatus.setBackgroundResource(R.mipmap.callqishou);
                        tvStatusRight.setText("! 呼叫超时");
                    }
                    else if (item.get("dadaStatus").equals(9)) {
                        tvStatus.setText("异常");
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.red);
                        tvStatusRight.setVisibility(View.VISIBLE);
                        ivStatus.setBackgroundResource(R.mipmap.fhsp);
                        tvStatusRight.setText("! 妥投异常");
                    } else if (item.get("dadaStatus").equals(10)) {

                        tvStatus.setText("异常");
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.red);
                        tvStatusRight.setVisibility(View.VISIBLE);
                        ivStatus.setBackgroundResource(R.mipmap.callqishou);
                        tvStatusRight.setText("! 妥投异常");
                    } else if (item.get("dadaStatus").equals(0)) {
                        //未呼叫达达:0
                        ivStatus.setBackgroundResource(R.mipmap.callqishou);
                        tvStatus.setText("已接单");
                        tvStatus.setBackgroundResource(R.color.green2);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("dadaStatus").equals(1)) { //待接单＝1 待取货＝2 配送中＝3 已完成＝4 已取消＝5
                        ivStatus.setBackgroundResource(R.mipmap.btn1);
                        ivStatus.setVisibility(View.GONE);
                        tvStatus.setText("呼叫中");
                        tvStatusRight.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.green2);
                    } else if (item.get("dadaStatus").equals(2)) {
                        ivStatus.setBackgroundResource(R.mipmap.finish);
                        tvStatus.setText("骑手已接单");
                        ivStatus.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.green2);
                    } else if (item.get("dadaStatus").equals(3)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn4);
                        tvStatus.setText("骑手已取货");
                        ivStatus.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.green2);
                    } else if (item.get("dadaStatus").equals(4)) {
                        ivStatus.setBackgroundResource(R.mipmap.finish2);
                        tvStatusRight.setText("");
                        tvStatus.setText("已完成");
                        tvStatus.setBackgroundResource(R.color.green2);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("dadaStatus").equals(5)) {
                        ivStatus.setBackgroundResource(R.mipmap.callqishou);
                        tvStatus.setText("异常");
                        tvStatus.setBackgroundResource(R.color.red);
                        tvStatusRight.setVisibility(View.VISIBLE);
                        if (item.get("dadaCancelFrom").equals(1)) {// 1:达达配送员取消；
                            ivStatusBottom.setVisibility(View.GONE);
                            tvStatusRight.setText("! 骑手取消订单");
                        } else if (item.get("dadaCancelFrom").equals(3)) {
                            ivStatusBottom.setVisibility(View.GONE);
                            tvStatusRight.setText("! 客服取消订单");
                        }
                    }

                } else if (item.get("status").equals(-1)) {
                    ivStatus.setBackgroundResource(R.mipmap.btn5);
                    tvStatus.setText("已退款");
                    tvStatus.setBackgroundResource(R.color.red);
                    tvStatusRight.setVisibility(View.GONE);
                }

            }
            else{
                tvType.setText("服务号：");
                llSerive.setVisibility(View.VISIBLE);
                lvDetail.setVisibility(View.GONE);
                rlZb.setVisibility(View.GONE);
                llbG.setBackgroundResource(R.drawable.bg_top3);
                tvStatusDo.setText("影院服务");
                tvAllMoney.setVisibility(View.GONE);
                llSeat.setVisibility(View.VISIBLE);
                llNote.setVisibility(View.GONE);
                tvLocation.setText(notNullString(item.get("seat")));
                ivld.setVisibility(View.GONE);
                tvStatusDo.setTextColor(Color.parseColor("#AE5CA4"));
                tvPhone.setText(notNullString(item.get("phone")));
                tvNote.setText(notNullString(item.get("remark")));
                List<Map<String,Object>> lists= (List<Map<String, Object>>) item.get("attrList");
                tvThing.setText(notNullString(lists.get(0).get("goodsName")));
                if (lists.size()>1){
                    tvThing1.setText(notNullString(lists.get(1).get("goodsName")));
                    tvThing1.setVisibility(View.VISIBLE);
                }else{
                    tvThing1.setVisibility(View.GONE);
                }
                ivStatusBottom.setVisibility(View.GONE);
                tvStatusRight.setVisibility(View.GONE);
                if (item.get("status").equals(1)) {
                    if (item.get("cookStatus").equals(3)) {
                        ivStatus.setBackgroundResource(R.mipmap.finish);
                        tvStatus.setText("已完成");
                        tvStatus.setBackgroundResource(R.color.green2);
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                    }
                }
                else{
                    ivStatus.setBackgroundResource(R.mipmap.btn5);
                    tvStatus.setText("已退款");
                    tvStatus.setBackgroundResource(R.color.red);
                    ivStatusBottom.setVisibility(View.GONE);
                    tvStatusRight.setVisibility(View.GONE);
                }
            }
            if ((item.get("isUrge").equals(1) || item.get("isUrge").equals(2))&&(Integer.parseInt(item.get("cookStatus").toString())!=-1&&Integer.parseInt(item.get("cookStatus").toString())!=-2)) {
                tvStatusRight.setText("催单");
                ivStatusBottom.setVisibility(View.VISIBLE);
                tvStatusRight.setVisibility(View.VISIBLE);
            }
            if((item.get("cookStatus").equals(2)||item.get("status").equals(-1))&&Integer.parseInt(item.get("isUrge").toString())!=0){
                ivStatusBottom.setVisibility(View.GONE);
                tvStatusRight.setVisibility(View.GONE);
            }
            items = (List<Map<String, Object>>) item.get("attrList");
            giftsItem = (List<Map<String, Object>>) item.get("giftList");

            goodmyAdpter = new MyAdapterGoods(getActivity(), giftsItem, item);
            LinearLayoutManager LayoutManager = new LinearLayoutManager(getActivity());
            rlZb.setLayoutManager(LayoutManager);
            rlZb.setAdapter(goodmyAdpter);
            myAdapters = new MyAdapterSon(getActivity(), items, item);
            LinearLayoutManager LayoutManager2 = new LinearLayoutManager(getActivity());
            lvDetail.setLayoutManager(LayoutManager2);
            lvDetail.setAdapter(myAdapters);

            lvDetail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (item.get("orderType").equals(3)){

                        }else{
                            startActivityForResult(new Intent(getActivity(), OrderFragment2.class)
                                    .putExtra("tag", item.get("id").toString())
                                    .putExtra("map", item.toString()), 1);}
                    }
                    return false;
                }
            });
            rlZb.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (item.get("orderType").equals(3)){

                        }else{
                            startActivityForResult(new Intent(getActivity(), OrderFragment2.class)
                                    .putExtra("tag", item.get("id").toString())
                                    .putExtra("map", item.toString()), 1
                            );}
                    }
                    return false;
                }
            });
            llDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.get("orderType").equals(3)){

                    }else{
                        startActivityForResult(new Intent(getActivity(), OrderFragment2.class)
                                .putExtra("tag", item.get("id").toString())
                                .putExtra("map", item.toString()), 1//订单整个model
                        );}
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            mRecyclerView = recyclerView;
        }

        RecyclerView mRecyclerView;

        @Override
        public void onClick(View v) {

        }
    }

    public class MyAdapterSon extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {
        private List<Map<String, Object>> data;
        private LayoutInflater inflater;
        private Context context;
        private Map<String, Object> item;

        public MyAdapterSon(Context context, List<Map<String, Object>> data, Map<String, Object> item) {
            this.context = context;
            this.data = data;
            this.item = item;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.item_goods, parent, false);
            ButterKnife.bind(this, itemView);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        startActivityForResult(new Intent(getActivity(), OrderFragment2.class)
                                .putExtra("tag", item.get("id").toString())
                                .putExtra("map", item.toString()), 1
                        );
                    }
                    return false;
                }
            });
            final MyViewHolder holder = new MyViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            TextView tvGoodsName = (TextView) holder.getView(R.id.tv_goods_name);
            TextView tvGoodsNum = (TextView) holder.getView(R.id.tv_goods_num);
            TextView tvGoodsBig = (TextView) holder.getView(R.id.tv_goods_big);
            TextView tvGoodsWei = (TextView) holder.getView(R.id.tv_goods_wei);
            LinearLayout llGoods = (LinearLayout) holder.getView(R.id.ll_goods);
            llGoods.setVisibility(View.VISIBLE);
            Map<String, Object> items = data.get(position);
            tvGoodsName.setText(notNullString(items.get("goodsName")));
            tvGoodsNum.setText(notNullString("x" + items.get("count")));
            tvGoodsBig.setText(notNullString(items.get("attrName")));
            tvGoodsWei.setText(notNullString(items.get("attr2")));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public void onClick(View view) {

        }
    }

    public class MyAdapterGoods extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {
        private List<Map<String, Object>> data;
        private LayoutInflater inflater;
        private Context context;
        private Map<String, Object> item;

        public MyAdapterGoods(Context context, List<Map<String, Object>> data, Map<String, Object> item) {
            this.context = context;
            this.data = data;
            this.item = item;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.item_goods, parent, false);
            ButterKnife.bind(this, itemView);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        startActivityForResult(new Intent(getActivity(), OrderFragment2.class)
                                .putExtra("tag", item.get("id").toString())
                                .putExtra("map", item.toString()), 1
                        );
                    }
                    return false;
                }
            });
            final MyViewHolder holder = new MyViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            TextView tvGoodsName = (TextView) holder.getView(R.id.tv_goods_name);
            TextView tvGoodsNum = (TextView) holder.getView(R.id.tv_goods_num);
            LinearLayout llGoods = (LinearLayout) holder.getView(R.id.ll_goods);
            llGoods.setVisibility(View.GONE);
            Map<String, Object> item = data.get(position);
            tvGoodsName.setText(notNullString(item.get("giftName")));
            tvGoodsNum.setText("x1");
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public void onClick(View view) {

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
//        setAnimationStyle();
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
                        loadData3(datestart, dateend);
                        page = 1;
                        hasNext = false;
                        initList2();
                        loadData4(datestart, dateend);
                        datestart=null;
                        dateend=null;
                        dismiss();
                    } else if (DATE_SELECT == 1) {
                        if (datestart!=null&&dateend!=null){
                            loadData3(datestart, dateend);
                            page = 1;
                            hasNext = false;
                            initList2();
                            loadData4(datestart, dateend);
                            datestart=null;
                            dateend=null;
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
                    } else if (DATE_DAY == 1) {
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

        private void getOne() {
            tvOne.setBackgroundResource(R.drawable.date_bg);
            tvMore.setBackgroundResource(R.drawable.date_bg4);
            tvStart.setVisibility(View.INVISIBLE);
            tvEnd.setVisibility(View.INVISIBLE);
            vH.setVisibility(View.INVISIBLE);
            DATE_SELECT = 0;
            DATE_DAY = 0;
        }

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
}