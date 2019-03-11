package com.hk.lang_data_manager.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.lang_data_manager.R;
import com.hk.lang_data_manager.base.AppBack;
import com.hk.lang_data_manager.base.BaseActivity;
import com.hk.lang_data_manager.base.Constant;
import com.hk.lang_data_manager.base.OKHttpUICallback;
import com.hk.lang_data_manager.base.OkHttpManger;
import com.hk.lang_data_manager.fragment.OrderFragment;
import com.hk.lang_data_manager.fragment.OrderFragment2;
import com.hk.lang_data_manager.utils.FlowLayoutManager;
import com.hk.lang_data_manager.utils.ImeUtils;
import com.hk.lang_data_manager.utils.MySharedPreference;
import com.hk.lang_data_manager.utils.MyViewHolder;
import com.hk.lang_data_manager.utils.print.PrintOrderUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * 作者  HK
 * 时间  2017/09/16 0016.
 * ░░░░░░░░░░░░░░░░░░░░░░░░▄░░
 * ░░░░░░░░░▐█░░░░░░░░░░░▄▀▒▌░
 * ░░░░░░░░▐▀▒█░░░░░░░░▄▀▒▒▒▐
 * ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐
 * ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐
 * ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌
 * ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒
 * ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐
 * ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄
 * ░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒
 * ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒
 * 单身狗就这样默默地看着你，一句话也不说。
 **/
/* search 类 与 newtaskfragment 这个类 的适配器差不多 */
public class SearchActivity2 extends BaseActivity {
    @BindView(R.id.rv_list_history)
    RecyclerView rvListHistory;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.fl_history)
    FrameLayout fl_history;
    @BindView(R.id.rv_list_result)
    RecyclerView rvListResult;
    @BindView(R.id.fl_res)
    FrameLayout flRes;
    TextView tv_name;
    List<String> listItem = new ArrayList<>();
    List<Map<String, Object>> list = new ArrayList<>();
    int page = 1;
    boolean hasNext = false;
    List<Map<String, String>> listItemHistory = new ArrayList<>();
    RecyclerView.Adapter myAdpterHistory;
    MyAdapter myAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void setUpView() {
        flRes.setVisibility(View.GONE);
        fl_history.setVisibility(View.GONE);
        rvListHistory.setVisibility(View.GONE);
        initList();
//        searchListener();
        loadData2();
        initLists();
        etSearch.addTextChangedListener(new MyTextWatcher());
    }

    public void back(View v) {
        finish();
    }

    private void loadData(String title) {
        flRes.setVisibility(View.VISIBLE);
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("cinemaId", MySharedPreference.getCinemaId());
        parmsMap.put("orderCode",title);
        parmsMap.put("showDetail","1");
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/list", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    list.clear();
                    List<Map<String, Object>> lists = appBack.getList();
                    for(int i=0;i<lists.size();i++){
                    list.add(lists.get(i));
                    myAdapter.notifyDataSetChanged();}
                }
                    else
                        toast("搜索订单不存在");
            }
        }, parmsMap);
    }

    void initLists() {
        myAdapter = new MyAdapter(this, list);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(this);
        rvListResult.setLayoutManager(LayoutManager);
        rvListResult.setAdapter(myAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {
        private List<Map<String, Object>> data;
        private List<Map<String, Object>> items;
        private List<Map<String, Object>> giftsItem = new ArrayList<>();
        private MyAdapterSon myAdapters;
        private LayoutInflater inflater;
        private Context context;
        TextView tvSeeDetail;
        RecyclerView lvDetail;
        LinearLayout llStatus;
        ImageView ivStatus;
        TextView tvStatus;

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


        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            TextView tvGetNum = (TextView) holder.getView(R.id.tv_get_num);
            TextView tvLocation = (TextView) holder.getView(R.id.tv_location);
            TextView tvSeatChange = (TextView) holder.getView(R.id.tv_seat_change);
            RecyclerView lvDetail = (RecyclerView) holder.getView(R.id.lv_detail);
            RecyclerView rlZb = (RecyclerView) holder.getView(R.id.lv_detail_gift);
            ImageView ivStatus = (ImageView) holder.getView(R.id.iv_status);
            TextView tvAllMoney = (TextView) holder.getView(R.id.tv_all_money);
            TextView tvStatusDo = (TextView) holder.getView(R.id.tv_status_do);
            TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
            ImageView ivStatusBottom = (ImageView) holder.getView(R.id.iv_status_bottom);
            TextView tvStatusRight = (TextView) holder.getView(R.id.tv_status_right);
            LinearLayout llDetails= (LinearLayout) holder.getView(R.id.ll_details);
            LinearLayout llbG = (LinearLayout) holder.getView(R.id.ll_bg);
            LinearLayout llSeat = (LinearLayout) holder.getView(R.id.ll_seat);
            TextView tvSeat= (TextView) holder.getView(R.id.tv_seat);
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
            if ((notNullString(item.get("isUrge")).equals("1") || notNullString(item.get("isUrge")).equals("2"))&&(Integer.parseInt(item.get("cookStatus").toString())!=-1&&Integer.parseInt(item.get("cookStatus").toString())!=-2)) {
                tvStatusRight.setText("催单");
                ivStatusBottom.setVisibility(View.VISIBLE);
                tvStatusRight.setVisibility(View.VISIBLE);
            }else{

            }
            if((item.get("cookStatus").equals(2)||item.get("status").equals(-1))&&Integer.parseInt(item.get("isUrge").toString())!=0){
                ivStatusBottom.setVisibility(View.GONE);
                tvStatusRight.setVisibility(View.GONE);
            }
            else{

            }
            tvGetNum.setText(item.get("takeCode").toString());
            if (item.get("orderType").equals(2)) {
                tvAllMoney.setText(notNullString("实付: ￥" + item.get("totalPrice")));
                tvType.setText("取餐号：");
                llSerive.setVisibility(View.GONE);
                lvDetail.setVisibility(View.VISIBLE);
                rlZb.setVisibility(View.VISIBLE);
                tvAllMoney.setVisibility(View.VISIBLE);
                llbG.setBackgroundResource(R.drawable.bg_top);
                tvStatusDo.setText("送餐到位");
                llSeat.setVisibility(View.VISIBLE);
                ivld.setVisibility(View.GONE);
                tvSeat.setVisibility(View.VISIBLE);
                llNote.setVisibility(View.VISIBLE);
                tvNote2.setText(notNullString(item.get("remark")));
                tvLocation.setText(notNullString(item.get("seat")));
                tvStatusDo.setTextColor(Color.parseColor("#ffa100"));
                if (item.get("status").equals(1)) {
                    if(item.get("isChangeSeat").equals(1)){
                        tvSeatChange.setVisibility(View.VISIBLE);
                    }
                    else{
                        tvSeatChange.setVisibility(View.GONE);
                    }
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
                tvType.setText("取餐号：");
                tvAllMoney.setText(notNullString("实付: ￥" + item.get("totalPrice")));
                llSerive.setVisibility(View.GONE);
                lvDetail.setVisibility(View.VISIBLE);
                rlZb.setVisibility(View.VISIBLE);
                tvAllMoney.setVisibility(View.VISIBLE);
                llbG.setBackgroundResource(R.drawable.bg_top2);
                tvSeat.setVisibility(View.GONE);
                tvStatusDo.setText("自助取餐");
                ivld.setVisibility(View.VISIBLE);
                llNote.setVisibility(View.VISIBLE);
                tvNote2.setText(notNullString(item.get("remark")));
                tvStatusDo.setTextColor(Color.parseColor("#74cd7a"));
                if (item.get("status").equals(1)) {
                    if (item.get("cookStatus").equals(-3)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn6);
                        tvStatus.setText("已取消");
                        ivld.setVisibility(View.GONE);
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.red);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("cookStatus").equals(-1)) {
                        ivStatus.setBackgroundResource(R.mipmap.mark);
                        tvStatusRight.setText("缺货");
                        tvStatus.setText("异常");
                        ivld.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.red);
                        ivStatusBottom.setVisibility(View.VISIBLE);
                        tvStatusRight.setVisibility(View.VISIBLE);
                    } else if (item.get("cookStatus").equals(0)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn3);
                        tvStatus.setText("未接单");
                        ivld.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.gray3);
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("cookStatus").equals(1)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn4);
                        tvStatus.setText("已接单");
                        ivld.setVisibility(View.VISIBLE);
                        tvStatus.setBackgroundResource(R.color.green2);
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("cookStatus").equals(2)) {
                        ivStatus.setBackgroundResource(R.mipmap.finish);
                        tvStatus.setText("已完成");
                        ivld.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.green2);
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                    }
                    else if (item.get("cookStatus").equals(3)) {
                        ivStatus.setBackgroundResource(R.mipmap.btn4);
                        tvStatus.setText("已接单");
                        ivld.setVisibility(View.VISIBLE);
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
            else{
            tvType.setText("服务号：");
            llSerive.setVisibility(View.VISIBLE);
            lvDetail.setVisibility(View.GONE);
            rlZb.setVisibility(View.GONE);
            llbG.setBackgroundResource(R.drawable.bg_top3);
            tvStatusDo.setText("影院服务");
            tvAllMoney.setVisibility(View.GONE);
            llSeat.setVisibility(View.VISIBLE);
            tvSeat.setVisibility(View.VISIBLE);
            tvLocation.setText(notNullString(item.get("seat")));
            ivld.setVisibility(View.GONE);
                llNote.setVisibility(View.GONE);
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
                if (item.get("cookStatus").equals(1)) {
                    ivStatus.setBackgroundResource(R.mipmap.btn3);
                    tvStatus.setText("未接单");
                    tvStatus.setBackgroundResource(R.color.gray3);
                    ivStatusBottom.setVisibility(View.GONE);
                    tvStatusRight.setVisibility(View.GONE);
                } else if (item.get("cookStatus").equals(2)) {
                    ivStatus.setBackgroundResource(R.mipmap.btn4);
                    tvStatus.setText("已接单");
                    tvStatus.setBackgroundResource(R.color.green2);
                    ivStatusBottom.setVisibility(View.GONE);
                    tvStatusRight.setVisibility(View.GONE);
                }
            }
            else{
            }
        }
//            if ((item.get("isUrge").equals(1) || item.get("isUrge").equals(2))&&(Integer.parseInt(item.get("cookStatus").toString())!=-1&&Integer.parseInt(item.get("cookStatus").toString())!=-2)) {
//                tvStatusRight.setText("催单");
//                ivStatusBottom.setVisibility(View.VISIBLE);
//                tvStatusRight.setVisibility(View.VISIBLE);
//            }
//            if((item.get("cookStatus").equals(2)||item.get("status").equals(-1))&&Integer.parseInt(item.get("isUrge").toString())!=0){
//                ivStatusBottom.setVisibility(View.GONE);
//                tvStatusRight.setVisibility(View.GONE);
//            }
//            tvLocation.setText(notNullString(item.get("seat")));
//            tvGetNum.setText(notNullString(item.get("takeCode")));
//            tvAllMoney.setText("实付:￥" + item.get("totalPrice").toString());
            items = (List<Map<String, Object>>) item.get("attrList");
            giftsItem=(List<Map<String, Object>>) item.get("giftList");

            MyAdapterSon myAdapters = new MyAdapterSon(context, items,item);
            LinearLayoutManager LayoutManager = new LinearLayoutManager(context);
            lvDetail.setLayoutManager(LayoutManager);
            lvDetail.setAdapter(myAdapters);

            lvDetail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        startActivity(new Intent(context,OrderFragment.class)
                                .putExtra("tag",item.get("id").toString())
                                .putExtra("map",item.toString())
                        );
                    }
                    return false;
                }
            });

            MyAdapterGoods goodmyAdapter = new MyAdapterGoods(context, giftsItem,item);
            LinearLayoutManager LayoutManager2 = new LinearLayoutManager(context);
            rlZb.setLayoutManager(LayoutManager2);
            rlZb.setAdapter(goodmyAdapter);

            rlZb.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        startActivity(new Intent(context,OrderFragment.class)
                                .putExtra("tag",item.get("id").toString())
                                .putExtra("map",item.toString())
                        );
                    }
                    return false;
                }
            });

            ivStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.get("status").equals(1)) {
                        if(item.get("orderType").equals(2)){
                            if (item.get("cookStatus").equals(1)) {
                                cookDone(item.get("id").toString(),holder.getAdapterPosition());
                            } else if (item.get("cookStatus").equals(3)) {
                                confirm(item.get("id").toString(), holder.getAdapterPosition());
                            }else if (item.get("cookStatus").equals(0)) {
                                catchOrder(item.get("id").toString(), holder.getAdapterPosition());
                            }
                        }
                        else if(item.get("orderType").equals(1)){
                            if (item.get("cookStatus").equals(1)) {
                                confirm(item.get("id").toString(), holder.getAdapterPosition());
                            } else if (item.get("cookStatus").equals(3)) {
                                confirm(item.get("id").toString(), holder.getAdapterPosition());
                            }else if (item.get("cookStatus").equals(0)) {
                                catchOrder(item.get("id").toString(), holder.getAdapterPosition());
                            }
                        }
                        else{
                            if (item.get("cookStatus").equals(2)) {
                                Update(item.get("id").toString(), holder.getAdapterPosition(),"3");
                            }else if (item.get("cookStatus").equals(1)) {
                                Update(item.get("id").toString(), holder.getAdapterPosition(),"2");
                            }
                        }
                    }else{
                    }
                }
            });
            llDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context,OrderFragment.class)
                            .putExtra("tag",item.get("id").toString())
                            .putExtra("map",item.toString()));//订单整个model
                }
            });
        }

        //确定收货
        private void confirm(String orderCard, final int position) {
            Map<String, String> parmsMap = new HashMap<>();
            parmsMap.put("adminId", MySharedPreference.getAdminId());
            parmsMap.put("token", MySharedPreference.getToken());
            parmsMap.put("id", orderCard);
            OkHttpManger.getInstance().getAsync(Constant.URL + "order/confirm", new OKHttpUICallback.ResultCallback<AppBack>() {
                @Override
                public void onSuccess(AppBack appBack) {
                    if (appBack.isSuccess()) {
                        items.get(position).put("cookStatus", 2);
                        myAdapter.notifyDataSetChanged();
                        loadData(etSearch.getText().toString());
                    }
                }
            }, parmsMap);
        }
        //接单
        private void catchOrder(String orderCard, final int position) {
            Map<String, String> parmsMap = new HashMap<>();
            parmsMap.put("adminId", MySharedPreference.getAdminId());
            parmsMap.put("token", MySharedPreference.getToken());
            parmsMap.put("id", orderCard);
            OkHttpManger.getInstance().getAsync(Constant.URL + "order/catchOrder", new OKHttpUICallback.ResultCallback<AppBack>() {
                @Override
                public void onSuccess(AppBack appBack) {
                    if (appBack.isSuccess()) {
                        items.get(position).put("cookStatus", 1);
                        PrintOrderUtils.print(items.get(position));//打印某一订单
                        myAdapter.notifyDataSetChanged();
                        loadData(etSearch.getText().toString());
                    }
                }
            }, parmsMap);
        }
        //配餐完成
        private void cookDone(String orderCard, final int position) {
            Map<String, String> parmsMap = new HashMap<>();
            parmsMap.put("adminId", MySharedPreference.getAdminId());
            parmsMap.put("token", MySharedPreference.getToken());
            parmsMap.put("id", orderCard);
            OkHttpManger.getInstance().getAsync(Constant.URL + "order/cookDone", new OKHttpUICallback.ResultCallback<AppBack>() {
                @Override
                public void onSuccess(AppBack appBack) {
                    if (appBack.isSuccess()) {
                        items.get(position).put("cookStatus", 3);
                        myAdapter.notifyDataSetChanged();
                        loadData(etSearch.getText().toString());
                    }
                }
            }, parmsMap);
        }
        //服务号接单
        private void Update(String orderCard, final int position, final String orderType) {
            Map<String, String> parmsMap = new HashMap<>();
            parmsMap.put("orderHandleUserId", MySharedPreference.getAdminId());
            parmsMap.put("token", MySharedPreference.getToken());
            parmsMap.put("orderId", orderCard);
            parmsMap.put("orderStatus", orderType);
            parmsMap.put("orderHandleUserIdentity", MySharedPreference.get("level"));
            OkHttpManger.getInstance().getAsync(Constant.URL + "cinemaServiceOrder/update", new OKHttpUICallback.ResultCallback<AppBack>() {
                @Override
                public void onSuccess(AppBack appBack) {
                    if (appBack.isSuccess()) {
                        if (orderType=="2"){
                            items.get(position).put("cookStatus", 2);
                            PrintOrderUtils.print(items.get(position));//打印某一订单
                             }
                        else{
                            items.get(position).put("cookStatus", 3);
                        }
                        myAdapter.notifyDataSetChanged();
                        loadData(etSearch.getText().toString());
                    }
                }
            }, parmsMap);
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
        public void onClick(View view) {

        }
    }

    public class MyTextWatcher implements TextWatcher{
        private CharSequence temp;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp=charSequence;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(temp.length()>=4&&temp.length()<10) {
//                add2History(etSearch.getText().toString());
                loadData(etSearch.getText().toString());
                etSearch.clearFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (temp.length()>=10){
                toast("您输入的字数过长");
            }
        }
    }
    public class MyAdapterSon extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {
        private List<Map<String, Object>> data;
        private LayoutInflater inflater;
        private Context context;
        private Map<String,Object> item;

        public MyAdapterSon(Context context, List<Map<String, Object>> data,Map<String,Object> item) {
            this.context = context;
            this.data = data;
            this.item=item;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.item_goods, parent, false);
            ButterKnife.bind(this, itemView);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        startActivity(new Intent(context,OrderFragment.class)
                                .putExtra("tag",item.get("id").toString())
                                .putExtra("map",item.toString())
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
            TextView tvGoodsName= (TextView) holder.getView(R.id.tv_goods_name);
            TextView tvGoodsNum= (TextView) holder.getView(R.id.tv_goods_num);
            TextView tvGoodsBig= (TextView) holder.getView(R.id.tv_goods_big);
            TextView tvGoodsWei= (TextView) holder.getView(R.id.tv_goods_wei);
            LinearLayout llGoods= (LinearLayout) holder.getView(R.id.ll_goods);
            llGoods.setVisibility(View.VISIBLE);
            Map<String, Object> items = data.get(position);
            tvGoodsName.setText(notNullString(items.get("goodsName")));
            tvGoodsNum.setText(notNullString("x"+items.get("count")));
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
        private Map<String,Object> item;


        public MyAdapterGoods (Context context, List<Map<String, Object>> data,Map<String,Object> item) {
            this.context = context;
            this.data = data;
            this.item=item;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.item_goods, parent, false);
            ButterKnife.bind(this, itemView);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        startActivity(new Intent(context,OrderFragment.class)
                                .putExtra("tag",item.get("id").toString())
                                .putExtra("map",item.toString())
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
            TextView tvGoodsName = (TextView) holder.getView(R.id.tv_goods_name);
            TextView tvGoodsNum = (TextView) holder.getView(R.id.tv_goods_num);
            LinearLayout llGoods= (LinearLayout) holder.getView(R.id.ll_goods);
            llGoods.setVisibility(View.GONE);
            Map<String, Object> item = data.get(position);
            tvGoodsName.setText(item.get("giftName").toString());
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
    /*今日搜索数量*/
    public void loadData2() {
        if (!TextUtils.isEmpty(MySharedPreference.get("TodaySearchSize"))) {
            int size = Integer.parseInt(MySharedPreference.get("TodaySearchSize"));
            for (int i = size; i >= 1; i--) {
                listItem.add(MySharedPreference.get("TodaySearch" + i));
            }
        }
        myAdpterHistory.notifyDataSetChanged();
    }

    public void add2History(String s) {
        //第一次搜索
        if (TextUtils.isEmpty(MySharedPreference.get("TodaySearchSize"))) {
            MySharedPreference.save("TodaySearch1", s);
            MySharedPreference.save("TodaySearchSize", "1");
            listItem.add(s);
        }
        //之前就搜索过
        else {
            int i = Integer.parseInt(MySharedPreference.get("TodaySearchSize"));
            if (i == 12) {
                if (listItem.contains(s)) {
                    int index = listItem.indexOf(s);
                    listItem.remove(index);
                    Collections.reverse(listItem);
                    listItem.add(s);
                    for (int i1 = 1; i1 < 13; i1++) {
                        MySharedPreference.save("TodaySearch" + i1, listItem.get(i1 - 1));
                    }
                    MySharedPreference.save("TodaySearchSize", "12");
                } else {
                    MySharedPreference.save("TodaySearch12", s);
                    MySharedPreference.save("TodaySearchSize", "12");
                }
            } else {
                if (listItem.contains(s)) {
                    int index = listItem.indexOf(s);
                    listItem.remove(index);
                    Collections.reverse(listItem);
                    listItem.add(s);
                    for (int i1 = 1; i1 < i + 1; i1++) {
                        MySharedPreference.save("TodaySearch" + i1, listItem.get(i1 - 1));
                    }
                    MySharedPreference.save("TodaySearchSize", i + "");
                } else {
                    MySharedPreference.save("TodaySearch" + (i + 1), s);
                    MySharedPreference.save("TodaySearchSize", (i + 1) + "");
                }
            }
            int size = Integer.parseInt(MySharedPreference.get("TodaySearchSize"));
            listItem.clear();
            //倒序
            for (int i1 = size; i1 >= 1; i1--) {
                listItem.add(MySharedPreference.get("TodaySearch" + i1));
            }
        }
        myAdpterHistory.notifyDataSetChanged();
    }

    void initList() {
        myAdpterHistory = new MyAdapterHistory(this, listItem);
        final FlowLayoutManager flowLayoutManager1 = new FlowLayoutManager();
        rvListHistory.setLayoutManager(flowLayoutManager1);
        rvListHistory.setAdapter(myAdpterHistory);
    }

    public void clearHistory(View view) {
        listItem.clear();
        MySharedPreference.remove("TodaySearch");
        MySharedPreference.remove("TodaySearchSize");
        myAdpterHistory.notifyDataSetChanged();
    }

    class MyAdapterHistory extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {
        private List<String> data;
        private LayoutInflater inflater;

        public MyAdapterHistory(Context context, List<String> data) {
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = inflater.inflate(R.layout.item_search_history, parent, false);
            final MyViewHolder holder = new MyViewHolder(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            tv_name = (TextView) holder.getView(R.id.tv_name);
            tv_name.setText(data.get(position));
            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadData(tv_name.getText().toString());
                }
            });
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
}
