package com.hk.lang_data_manager.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hk.lang_data_manager.R;
import com.hk.lang_data_manager.base.AppBack;
import com.hk.lang_data_manager.base.BaseActivity;
import com.hk.lang_data_manager.base.Constant;
import com.hk.lang_data_manager.base.OKHttpUICallback;
import com.hk.lang_data_manager.base.OkHttpManger;
import com.hk.lang_data_manager.utils.FlowLayoutManager;
import com.hk.lang_data_manager.utils.ImeUtils;
import com.hk.lang_data_manager.utils.MySharedPreference;
import com.hk.lang_data_manager.utils.MyViewHolder;

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

/*暂时无用   用不到这个 activity */

public class SearchActivity extends BaseActivity {
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
        initList();
        searchListener();
        loadData2();
        initLists();
    }

    public void back(View v) {
        finish();
    }


    public void searchListener() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (TextUtils.isEmpty(etSearch.getText().toString().trim())) {
                    ImeUtils.hideSoftKeyboard(etSearch);
                    etSearch.setText("");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new PromptDialog(SearchActivity.this).showError("请输入搜索关键词");
                        }
                    }, 100);
                    return true;
                } else {
                    ImeUtils.hideSoftKeyboard(rvListHistory);
                    String t_search = etSearch.getText().toString();
                    if (t_search != null) {
                    }
                }
                add2History(etSearch.getText().toString());
                loadData(etSearch.getText().toString());
                etSearch.clearFocus();
                return true;
            }
        });
    }

    private void loadData(String title) {
        flRes.setVisibility(View.VISIBLE);
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("cinemaId", MySharedPreference.getCinemaId());
        parmsMap.put("searchKey", title);
        OkHttpManger.getInstance().getAsync(Constant.URL + "goods/list", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    list.clear();
                    list.addAll(appBack.getList());
                    myAdapter.notifyDataSetChanged();
                }
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
            tvCount.setText("库存"+notNullString(item.get("stockNum")));
            int status = Integer.parseInt(item.get("status").toString());
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
                        updateuDown(data.get(position).get("id").toString(), holder.getAdapterPosition());
                        switchShop.setBackgroundResource(R.drawable.btn_good2);
                        switchShop.setText("已下架");
                    }
                });
            } else {
                switchShop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateUp(data.get(position).get("id").toString(), holder.getAdapterPosition());
                        switchShop.setBackgroundResource(R.drawable.btn_good);
                        switchShop.setText("已上架");
                        switchShop.setTextColor(Color.parseColor("#ffffff"));
                    }
                });
            }
        }

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
                        data.get(position).put("status", 0);
                        myAdapter.notifyDataSetChanged();
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
            OkHttpManger.getInstance().getAsync(Constant.URL + "goods/update", new OKHttpUICallback.ResultCallback<AppBack>() {
                @Override
                public void onSuccess(AppBack appBack) {
                    if (appBack.isSuccess()) {
                        data.get(position).put("status", -1);
                        myAdapter.notifyDataSetChanged();
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
        public void onClick(View v) {

        }
    }

    public void loadData2() {
        if (!TextUtils.isEmpty(MySharedPreference.get("TodaySearchSize"))) {
            int size = Integer.parseInt(MySharedPreference.get("TodaySearchSize"));
            for (int i = size; i >= 1; i--) {
                listItem.add(MySharedPreference.get("TodaySearch" + i));
            }
        }
//        if (listItem.size()>0)
//            fl_history.setVisibility(View.VISIBLE);
//        else
//            fl_history.setVisibility(View.GONE);
        myAdpterHistory.notifyDataSetChanged();
    }

    public void add2History(String s) {
        //第一次搜索
        if (TextUtils.isEmpty(MySharedPreference.get("TodaySearchSize"))) {
            MySharedPreference.save("TodaySearch1", s);
            MySharedPreference.save("TodaySearchSize", "1");
            toast(MySharedPreference.get("TodaySearch1"));
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
        MySharedPreference.remove3("TodaySearch");
        MySharedPreference.remove3("TodaySearchSize");
//        if (listItem.size()>0)
//            fl_history.setVisibility(View.VISIBLE);
//        else
//            fl_history.setVisibility(View.GONE);
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
            final TextView tv_name = (TextView) holder.getView(R.id.tv_name);
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
