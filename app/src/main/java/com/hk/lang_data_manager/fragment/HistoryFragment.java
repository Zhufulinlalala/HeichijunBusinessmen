package com.hk.lang_data_manager.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hk.lang_data_manager.R;
import com.hk.lang_data_manager.base.AppBack;
import com.hk.lang_data_manager.base.BaseFragement;
import com.hk.lang_data_manager.base.Constant;
import com.hk.lang_data_manager.base.OKHttpUICallback;
import com.hk.lang_data_manager.base.OkHttpManger;
import com.hk.lang_data_manager.utils.MySharedPreference;
import com.hk.lang_data_manager.utils.MyViewHolder;
import com.hk.lang_data_manager.utils.PushPopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HistoryFragment extends BaseFragement {
    private List<Map<String, Object>> listItem = new ArrayList<>();
    @BindView(R.id.il_lv)
    RecyclerView ilLv;
    Unbinder unbinder;
    RecyclerView.Adapter myAdpter;
    private SwipeRefreshLayout mSwipeLayout;
    private boolean isRefresh = false;
    private int page = 1;
    private boolean hasNext = false;
    private boolean next = false;
    private PopCode popCode;
    private boolean isCode = false;
    private String code;
    private String level;

    @Override
    protected int getLayoutId() {
        return R.layout.item_list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        level=MySharedPreference.get("level");
        listItem.clear();
        initList();
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
                            loadData();
                            isRefresh = false;
                        }
                    }, 2000);
                }
            }
        });
        getCode();
        return root;
    }

    public void getCode(){
        if (level.equals("2")) {
            popCode = new PopCode(getActivity());
            popCode.show(getActivity());
        }
        else{
            return;
        }
    }
    @Override
    protected void setUpView() {

    }

    @Override
    public void onResume() {
        loadData();
        super.onResume();
    }

    private void loadData() {
        next = false;
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("Id", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("cinemaId", MySharedPreference.getCinemaId());
        parmsMap.put("page", page + "");
        OkHttpManger.getInstance().getAsync(Constant.URL + "goods/list", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    if (page == 1) {
                        listItem.clear();
                    }
                    Map<String, Object> items = (Map<String, Object>) appBack.getResult();
                    List<Map<String, Object>> list = (List<Map<String, Object>>) items.get("list");
                    listItem.addAll(list);
                    if (items.get("hasNextPage").equals(true))
                        hasNext = true;
                    else
                        hasNext = false;
                    next = true;
                    myAdpter.notifyDataSetChanged();
                }
            }
        }, parmsMap);
    }

    //下架

    void initList() {
        myAdpter = new MyAdapter(getActivity(), listItem);
        final LinearLayoutManager LayoutManager = new LinearLayoutManager(getActivity());
        ilLv.setLayoutManager(LayoutManager);
        ilLv.setAdapter(myAdpter);
        ilLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int last = LayoutManager.findLastCompletelyVisibleItemPosition();
                if (last > listItem.size() - 3 && hasNext) {
                    page++;
                    if (next) {
                        loadData();
                    }
                }
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {
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
            View itemView = inflater.inflate(R.layout.shop_item, parent, false);
            final MyViewHolder holder = new MyViewHolder(itemView);
            return holder;
        }

        //上架
        public void updateUp(String id, final int position) {
            Map<String, String> parmsMap = new HashMap<>();
            parmsMap.put("adminId", MySharedPreference.getAdminId());
            parmsMap.put("token", MySharedPreference.getToken());
            parmsMap.put("status", "0");
            parmsMap.put("id", id);
            OkHttpManger.getInstance().getAsync(Constant.URL + "goods/update", new OKHttpUICallback.ResultCallback<AppBack>() {
                @Override
                public void onSuccess(AppBack appBack) {
                    if (appBack.isSuccess()) {
                        listItem.get(position).put("status", 0);
                        myAdpter.notifyDataSetChanged();
                    }
                }
            }, parmsMap);
        }

        //下架
        public void updateuDown(String id, final int position) {
            Map<String, String> parmsMap = new HashMap<>();
            parmsMap.put("adminId", MySharedPreference.getAdminId());
            parmsMap.put("token", MySharedPreference.getToken());
            parmsMap.put("status", "-1");
            parmsMap.put("id", id);
            parmsMap.put("code", code);
            OkHttpManger.getInstance().getAsync(Constant.URL + "goods/update", new OKHttpUICallback.ResultCallback<AppBack>() {
                @Override
                public void onSuccess(AppBack appBack) {
                    if (appBack.isSuccess()) {
                        listItem.get(position).put("status", -1);
                        myAdpter.notifyDataSetChanged();
//
                    }
                }
            }, parmsMap);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            TextView tvShopName = (TextView) holder.getView(R.id.tv_shop_name);
            ImageView ivPic = (ImageView) holder.getView(R.id.iv_pic);
            TextView tvMoney = (TextView) holder.getView(R.id.tv_money);
            final TextView switchShop = (TextView) holder.getView(R.id.switch_shop);
            TextView tvCount = (TextView) holder.getView(R.id.tv_count);
            final Map<String, Object> item = data.get(position);
            loadImg(ivPic, item.get("facePhoto"));
            tvMoney.setText(notNullString("￥" + item.get("price")));
            tvShopName.setText(notNullString(item.get("title")));
//            tvCount.setText("库存" + notNullString(item.get("stockNum")));
            int status = Integer.parseInt(data.get(position).get("status").toString());
            if (status == 0) {
                switchShop.setBackgroundResource(R.drawable.btn_good);
                switchShop.setText("已上架");
                switchShop.setTextColor(Color.parseColor("#ffffff"));
            } else {
                switchShop.setBackgroundResource(R.drawable.btn_good2);
                switchShop.setText("已下架");
            }
            if (status == 0) {
                switchShop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (level.equals("2")){
                            getCode();
                        }
                        else{
                        updateuDown(data.get(position).get("id").toString(), holder.getAdapterPosition());
                        switchShop.setBackgroundResource(R.drawable.btn_good2);
                        switchShop.setText("已下架");}
                    }
                });
            } else {
                switchShop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (level.equals("2")){
                            getCode();
                        }
                        else{
                        updateUp(data.get(position).get("id").toString(), holder.getAdapterPosition());
                        switchShop.setBackgroundResource(R.drawable.btn_good);
                        switchShop.setText("已上架");
                        switchShop.setTextColor(Color.parseColor("#ffffff"));}
                    }
                });
            }

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

    //授权弹窗
    public class PopCode extends PushPopupWindow {

        private TextView tvSummit;
        private ImageView ivCannel;
        private EditText etCode;

        public PopCode(Context context) {
            super(context);
            initView();
            setOutsideTouchable(true);
        }

        @Override
        protected View generateCustomView() {
            View root = View.inflate(context, R.layout.pop_code, null);
            etCode=root.findViewById(R.id.et_code);
            tvSummit=root.findViewById(R.id.tv_btn_summit);
            ivCannel=root.findViewById(R.id.iv_close);
            tvSummit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                            code=etCode.getText().toString();
                            Map<String, String> parmsMap = new HashMap<>();
                            parmsMap.put("adminId", MySharedPreference.getAdminId());
                            parmsMap.put("token", MySharedPreference.getToken());
                            parmsMap.put("code", code);
                            OkHttpManger.getInstance().getAsync(Constant.URL + "cinemaverify/checkCode", new OKHttpUICallback.ResultCallback<AppBack>() {
                                @Override
                                public void onSuccess(AppBack appBack) {
                                    if (appBack.isSuccess()) {
                                        popCode.dismiss();
                                        level="1";
                                        TimerTask task=new TimerTask() {
                                                @Override
                                                 public void run() {
                                                 level="2";
                                                }
                                             };
                                        Timer timer=new Timer();
                                        timer.schedule(task,600000);
                                    }
                                }
                            }, parmsMap);
                        }
            });
            ivCannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            return root;
        }

        public void show(Activity activity) {
            showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }
}