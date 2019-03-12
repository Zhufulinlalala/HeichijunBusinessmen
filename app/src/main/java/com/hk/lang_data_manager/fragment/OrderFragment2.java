package com.hk.lang_data_manager.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.lang_data_manager.R;
import com.hk.lang_data_manager.base.AppBack;
import com.hk.lang_data_manager.base.BaseActivity;
import com.hk.lang_data_manager.base.Constant;
import com.hk.lang_data_manager.base.OKHttpUICallback;
import com.hk.lang_data_manager.base.OkHttpManger;
import com.hk.lang_data_manager.utils.MySharedPreference;
import com.hk.lang_data_manager.utils.MyViewHolder;
import com.hk.lang_data_manager.utils.PopStyleIphone;
import com.hk.lang_data_manager.utils.print.PrintOrderUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*数据页面进入之后的fragment */
public class OrderFragment2 extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_get_num)
    TextView tvGetNum;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.lv_detail)
    RecyclerView lvDetail;
    @BindView(R.id.tv_get_name)
    TextView tvGetName;
    @BindView(R.id.tv_phone_num)
    TextView tvPhoneNum;
    @BindView(R.id.tv_location_num)
    TextView tvLocationNum;
    @BindView(R.id.tv_order_num)
    TextView tvOrderNum;
    @BindView(R.id.tv_date_order)
    TextView tvDateOrder;
    @BindView(R.id.tv_order_thing)
    TextView tv_order_thing;
    String orderId;
    @BindView(R.id.tv_all_money)
    TextView tvAllMoney;
    @BindView(R.id.tv_return_money)
    TextView tvReturnMoney;
    @BindView(R.id.tv_song_money)
    TextView tvSongMoney;
    @BindView(R.id.ll_song)
    LinearLayout llSong;
    @BindView(R.id.tv_red_money)
    TextView tvRedMoney;
    @BindView(R.id.ll_red_bag)
    LinearLayout llRedBag;
    @BindView(R.id.tv_date_get)
    TextView tvDateGet;
    @BindView(R.id.ll_get_goods)
    LinearLayout llGetGoods;
    @BindView(R.id.rl_zb)
    RecyclerView rlZb;
    @BindView(R.id.tv_finish)
    TextView tvFinish;
    @BindView(R.id.tv_error)
    TextView tvError;
    @BindView(R.id.ll_people_phone)
    LinearLayout llPeoplePhone;
    @BindView(R.id.ll_people_seat)
    LinearLayout llPeopleSeat;
    @BindView(R.id.iv_status_bottom)
    ImageView ivStatusBottom;
    @BindView(R.id.ll_ts)
    LinearLayout llTs;
    @BindView(R.id.ll_post_free)
    LinearLayout llPostFree;
    @BindView(R.id.tv_post_free)
    TextView tvPostFree;
    @BindView(R.id.ll_fill_free)
    LinearLayout llFillFree;
    @BindView(R.id.tv_fill_free)
    TextView tvFillFree;
    @BindView(R.id.tv_dazhe)
    TextView tvDazhe;
    @BindView(R.id.tv_pay)
    TextView tvPay;
    private List<Map<String, Object>> listItem = new ArrayList<>();
    private List<Map<String, Object>> giftsItem = new ArrayList<>();
    private String paytype;
    private MyAdapterSon myAdpter;
    private MyAdapterGoods goodmyAdpter;
    private PopStyleIphone exitPw;

    private Map<String, Object> map = new HashMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.order_detail;
    }

    @Override
    protected void setUpView() {
        Intent intent = getIntent();
        orderId = intent.getStringExtra("tag");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        this.setContentView(this.getLayoutId());//缺少这一行
        ButterKnife.bind(this);
        goodsList();
        inist();
    }

    private void inist() {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("id", orderId);
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/info", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    final Map<String, Object> item = appBack.getMap();
                    paytype = String.valueOf(item.get("payType"));
                    tvGetNum.setText(notNullString(item.get("takeCode")));

                    if (item.get("payType").equals(10)) {
                        tvAllMoney.setTextColor(getResources().getColor(R.color.red));
                        tvAllMoney.setText(notNullString("￥" + item.get("totalPrice") + "(会员价)"));
                    } else {
                        tvAllMoney.setTextColor(getResources().getColor(R.color.black));
                        tvAllMoney.setText(notNullString("￥" + item.get("totalPrice")));
                    }

                    tvGetName.setText(notNullString(item.get("userName")));
                    tvPhoneNum.setText(notNullString(item.get("phone")));
                    tvLocationNum.setText(notNullString(item.get("seat")));
                    tvOrderNum.setText(notNullString(item.get("orderCode")));
                    if (item.get("type").equals("weChat")){
                        tvPay.setText("微信支付");
                    }else if (item.get("type").equals("aliPay")){
                        tvPay.setText("支付宝支付");
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    map = item;
                    tvDateOrder.setText(simpleDateFormat.format(Long.parseLong(item.get("createTime").toString())));
                    tv_order_thing.setText(notNullString(item.get("remark")));
                    if (item.get("saleEventType").equals(0)) {
                        llFillFree.setVisibility(View.GONE);
                    } else if (item.get("saleEventType").equals(1)) {
                        if (Double.parseDouble(item.get("saleEventPrice").toString()) == 0.00) {
                            llFillFree.setVisibility(View.GONE);
                        } else {
                            llFillFree.setVisibility(View.VISIBLE);
                            tvDazhe.setText("满减优惠");
                            tvFillFree.setText("-￥" + item.get("saleEventPrice"));
                        }
                    } else if (item.get("saleEventType").equals(2)) {
                        if (Double.parseDouble(item.get("saleEventPrice").toString()) == 0.00) {
                            llFillFree.setVisibility(View.GONE);
                        } else {
                            llFillFree.setVisibility(View.VISIBLE);
                            tvDazhe.setText("打折优惠");
                            tvFillFree.setText("-￥" + item.get("saleEventPrice"));
                        }
                    }
                    if (item.get("orderType").equals(1)) {
                        llGetGoods.setVisibility(View.VISIBLE);
                    } else
                        llGetGoods.setVisibility(View.GONE);
                    tvDateGet.setText(simpleDateFormat.format(Long.parseLong(item.get("getTime").toString())));
                    if (notNullString(item.get("couponId")) == "") {
                        llRedBag.setVisibility(View.GONE);
                    } else {
                        llRedBag.setVisibility(View.VISIBLE);
                        tvRedMoney.setText(notNullString("-￥" + item.get("redPacketPrice")));
                    }
                    if (item.get("canRefund").equals(0)) {
                        tvReturnMoney.setBackgroundResource(R.color.gray3);
                        tvReturnMoney.setEnabled(false);
                        llTs.setVisibility(View.VISIBLE);
                    } else {
                        tvReturnMoney.setBackgroundResource(R.color.red);
                        tvReturnMoney.setEnabled(true);
                        llTs.setVisibility(View.GONE);
                    }
                    if (item.get("status").equals(1)) { // 0 代付款 1已付款 -10删除 -1已退款
                        if (item.get("orderType").equals(2)) {
                            llSong.setVisibility(View.VISIBLE);
                            tvSongMoney.setText("￥" + item.get("freight"));
                            if (item.get("freightFree").equals(1)) {
                                llPostFree.setVisibility(View.VISIBLE);
                                tvPostFree.setText("-￥" + item.get("freight"));
                            } else {
                                llPostFree.setVisibility(View.GONE);
                            }
                            if (item.get("cookStatus").equals(2)) {
                                tvStatus.setText("已完成");
                                tvStatus.setBackgroundResource(R.color.green2);
                                tvFinish.setVisibility(View.GONE);
                                tvReturnMoney.setVisibility(View.VISIBLE);
                            } else if (item.get("cookStatus").equals(-1)) {
                                tvFinish.setVisibility(View.GONE);
                                tvError.setVisibility(View.VISIBLE);
                                tvError.setText("缺货");
                                tvStatus.setText("异常");
                                ivStatusBottom.setVisibility(View.VISIBLE);
                                tvStatus.setBackgroundResource(R.color.red);
                                tvReturnMoney.setVisibility(View.VISIBLE);
                            } else if (item.get("cookStatus").equals(-2)) {
                                tvFinish.setVisibility(View.GONE);
                                tvError.setVisibility(View.VISIBLE);
                                tvError.setText("座位有误");
                                tvStatus.setText("异常");
                                ivStatusBottom.setVisibility(View.VISIBLE);
                                tvStatus.setBackgroundResource(R.color.red);
                                tvReturnMoney.setVisibility(View.VISIBLE);
                            }
                        } else {
                            llPostFree.setVisibility(View.GONE);
                            llSong.setVisibility(View.GONE);
                            llPeoplePhone.setVisibility(View.GONE);
                            llPeopleSeat.setVisibility(View.GONE);
                            if (item.get("cookStatus").equals(2)) {
                                tvStatus.setText("已完成");
                                tvStatus.setBackgroundResource(R.color.green2);
                                tvFinish.setVisibility(View.GONE);
                                tvReturnMoney.setVisibility(View.VISIBLE);
                            } else if (item.get("cookStatus").equals(-1)) {
                                tvFinish.setVisibility(View.GONE);
                                tvError.setVisibility(View.VISIBLE);
                                tvError.setText("缺货");
                                tvStatus.setText("异常");
                                ivStatusBottom.setVisibility(View.VISIBLE);
                                tvStatus.setBackgroundResource(R.color.red);
                                tvReturnMoney.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (item.get("status").equals(-1)) {
                        tvStatus.setText("已退款");
                        llTs.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.red);
                        tvFinish.setVisibility(View.GONE);
                        tvReturnMoney.setVisibility(View.GONE);
                        if (item.get("orderType").equals(2)) {
                            llSong.setVisibility(View.VISIBLE);
                            tvSongMoney.setText("￥" + item.get("freight"));
                            if (item.get("freightFree").equals(1)) {
                                llPostFree.setVisibility(View.VISIBLE);
                                tvPostFree.setText("-￥" + item.get("freight"));
                            } else {
                                llPostFree.setVisibility(View.GONE);
                            }
                        } else {
                            llPeoplePhone.setVisibility(View.GONE);
                            llPeopleSeat.setVisibility(View.GONE);
                        }
                    }
                    listItem = (List<Map<String, Object>>) item.get("attrList");
                    giftsItem = (List<Map<String, Object>>) item.get("giftList");
                    goodsList();
                    initList();
                    tvReturnMoney.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isNull(exitPw)) {
                                exitPw = new PopStyleIphone(OrderFragment2.this);
                                exitPw.tv_text.setText("是否确认退款");
                                exitPw.btn_sure.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelOrder(item.get("id").toString());
                                        exitPw.dismiss();
                                    }
                                });
                            }
                            exitPw.show(OrderFragment2.this);
                        }
                    });
                }
            }
        }, parmsMap);
    }

    private void cancelOrder(String orderCard) {
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("id", orderCard);
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/cancelOrder24", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    Intent intent = new Intent().putExtra("id", orderId);
                    setResult(2, intent);
                    EventBus.getDefault().post(new String("sum"));
                    finish();
                }
            }
        }, parmsMap);
    }

    void goodsList() {
        goodmyAdpter = new MyAdapterGoods(this, giftsItem);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(this);
        rlZb.setLayoutManager(LayoutManager);
        rlZb.setAdapter(goodmyAdpter);
    }

    void initList() {
        myAdpter = new MyAdapterSon(this, listItem);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(this);
        lvDetail.setLayoutManager(LayoutManager);
        lvDetail.setAdapter(myAdpter);
    }

    public class MyAdapterSon extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {
        private List<Map<String, Object>> data;
        private LayoutInflater inflater;
        private Context context;

        public MyAdapterSon(Context context, List<Map<String, Object>> data) {
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.item_goods, parent, false);
            ButterKnife.bind(this, itemView);
            final MyViewHolder holder = new MyViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            LinearLayout ll_vip = (LinearLayout) holder.getView(R.id.ll_vip);
            TextView tv_vipgoods_money = (TextView) holder.getView(R.id.tv_vipgoods_money);

            TextView tvGoodsName = (TextView) holder.getView(R.id.tv_goods_name);
            TextView tvGoodsNum = (TextView) holder.getView(R.id.tv_goods_num);
            TextView tvGoodsBig = (TextView) holder.getView(R.id.tv_goods_big);
            TextView tvGoodsWei = (TextView) holder.getView(R.id.tv_goods_wei);
            TextView tvMoney = (TextView) holder.getView(R.id.tv_goods_money);
            LinearLayout llMoney = (LinearLayout) holder.getView(R.id.ll_money);
            llMoney.setVisibility(View.VISIBLE);
            LinearLayout llGoods = (LinearLayout) holder.getView(R.id.ll_goods);
            llGoods.setVisibility(View.VISIBLE);
            Map<String, Object> item = data.get(position);
            tvGoodsName.setText(notNullString(item.get("goodsName")));
            tvGoodsNum.setText(notNullString("x" + item.get("count")));
            if (isEmpty(String.valueOf(item.get("attrName")))) {
                llGoods.setVisibility(View.GONE);
            } else {
                tvGoodsBig.setText(notNullString(item.get("attrName")));
                tvGoodsWei.setText(notNullString(item.get("attr2")));
            }

            tvMoney.setText(notNullString("￥" + item.get("sumPrice")));

            if (!paytype.equals("10")) {
                ll_vip.setVisibility(View.GONE);
            } else {
                ll_vip.setVisibility(View.VISIBLE);
                tv_vipgoods_money.setText(notNullString("￥" + item.get("sumVipPrice")));
            }

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


        public MyAdapterGoods(Context context, List<Map<String, Object>> data) {
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.item_goods, parent, false);
            ButterKnife.bind(this, itemView);
            final MyViewHolder holder = new MyViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            TextView tvGoodsName = (TextView) holder.getView(R.id.tv_goods_name);
            TextView tvGoodsNum = (TextView) holder.getView(R.id.tv_goods_num);
            TextView tvMoney = (TextView) holder.getView(R.id.tv_goods_money);
            LinearLayout llMoney = (LinearLayout) holder.getView(R.id.ll_money);
            llMoney.setVisibility(View.VISIBLE);
            LinearLayout llGoods = (LinearLayout) holder.getView(R.id.ll_goods);
            llGoods.setVisibility(View.GONE);
            Map<String, Object> item = data.get(position);
            tvGoodsName.setText(notNullString(item.get("giftName")));
            tvGoodsNum.setText("x1");
            tvMoney.setText(notNullString(item.get("price")));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public void onClick(View view) {

        }
    }

    @OnClick({R.id.back, R.id.tv_return_money, R.id.tv_dayin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
//            case R.id.tv_location_error:
//                break;
            case R.id.tv_return_money:
                break;
            case R.id.tv_dayin:
                PrintOrderUtils.print(map);
                break;
        }
    }
}