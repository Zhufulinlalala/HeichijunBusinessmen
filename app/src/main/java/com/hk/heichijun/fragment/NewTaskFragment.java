package com.hk.heichijun.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.heichijun.MyReceiver;
import com.hk.heichijun.R;
import com.hk.heichijun.base.AppBack;
import com.hk.heichijun.base.BaseFragement;
import com.hk.heichijun.base.Constant;
import com.hk.heichijun.base.OKHttpUICallback;
import com.hk.heichijun.base.OkHttpManger;
import com.hk.heichijun.base.SaflyApplication;
import com.hk.heichijun.utils.MySharedPreference;
import com.hk.heichijun.utils.MyViewHolder;
import com.hk.heichijun.utils.PopPatchUpdate;
import com.hk.heichijun.utils.PushPopupWindow;
import com.hk.heichijun.utils.print.PrintOrderUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/*   首  页   面  */
public class NewTaskFragment extends BaseFragement implements SwipeBackLayout.SwipeListener {
    private List<Map<String, Object>> listItem = new ArrayList<>();
    private List<Map<String, Object>> listItem2 = new ArrayList<>();
    @BindView(R.id.il_lv)
    RecyclerView ilLv;
    Unbinder unbinder;
    MyAdapter myAdpter;
    private int page = 1;
    private boolean hasNext = false;
    private boolean next = false;
    private SwipeRefreshLayout mSwipeLayout;
    private boolean isRefresh = false;
    private MyReceiver receiver;
    private int num = 0;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private PopLingdang popLingdang;
    private PopPatchUpdate ppu;
    //每1分钟检查一下是否有未接单订单
    private Runnable refreshLoad = new Runnable() {
        @Override
        public void run() {
            loadData2();
            handler2.postDelayed(this, 60000);
        }
    };
    public boolean refresh = false;

    @Override
    protected int getLayoutId() {
        return R.layout.item_list;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (refresh==true){
//            listItem.clear();
//            initList();
//            page=1;
//            loadData();
//        }

        listItem.clear();
        initList();
        page = 1;
        loadData();

    }

    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String s) {
        //类名+方法/事件名
        if (s.equals("sss")) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    page = 1;
                    loadData();
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 500);
        }
        if (s.equals("New")) {
            initList();
            page = 1;
            loadData();
        }
    }

    //语音播报
    public void TextToVoice(String text) {
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(SaflyApplication.getInstance(), null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "50");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端,这些功能用到讯飞服务器,所以要有网络.
        mTts.startSpeaking(text, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        SpeechUtility.createUtility(getActivity(), SpeechConstant.APPID + "=5a7e86c4");
        EventBus.getDefault().register(this);
        initList();
        loadData();
        handler2.postDelayed(refreshLoad, 60000);
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
        return root;
    }

    @Override
    protected void setUpView() {
    }

    public void loadData() {
        refresh = true;
        next = false;
        num = 0;
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("cinemaId", MySharedPreference.getCinemaId());
        parmsMap.put("showDetail", "1");
        parmsMap.put("expNopay", "1");
        parmsMap.put("status", "1");
        parmsMap.put("expComplete", "1");
        parmsMap.put("page", page + "");
        parmsMap.put("rankType", "1");
        parmsMap.put("service_id", "1");
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/list", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    Map<String, Object> items = (Map<String, Object>) appBack.getResult();
                    List<Map<String, Object>> list = (List<Map<String, Object>>) items.get("list");
                    if (page == 1) {
                        listItem.clear();
                    }
                   // Log.d("NewTaskFragment", "listItem.get(0).get('dadaStutus'):" + list.get(0).get("dadaStatus"));
//                    "dada_status" int(11) DEFAULT '0' COMMENT '达达状态 未呼叫达达:0待接单＝1 待取货＝2 配送中＝3 已完成＝4 已取消＝5 已过期＝7 指派单=8 妥投异常之物品返回中=9 妥投异常之物品返回完成=10',
//                     "dada_cancel_reason" varchar(255) DEFAULT NULL COMMENT '达达取消原因',
//                      "dada_cancel_from" int(11) DEFAULT '0' COMMENT '达达取消来源1:达达配送员取消；2:商家主动取消；3:系统或客服取消；0:默认值',
//                      "dada_staff_id" varchar(32) DEFAULT NULL COMMENT '达达配送员员工',
//                     "dada_staff_name" varchar(20) DEFAULT NULL COMMENT '达达配送员姓名',
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

    /*每一分钟检查一遍 数据  加载数据*/
    public void loadData2() {
//        handler.removeCallbacks(noticeRunnable);
        num = 0;
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("cinemaId", MySharedPreference.getCinemaId());
        parmsMap.put("showDetail", "1");
        parmsMap.put("expNopay", "1");
        parmsMap.put("status", "1");
        parmsMap.put("expComplete", "1");
        parmsMap.put("rankType", "1");
        parmsMap.put("service_id", "1");
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/list", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    List<Map<String, Object>> items2 = appBack.getList();
                    listItem.clear();
                    listItem.addAll(items2);
                    myAdpter.notifyDataSetChanged();
                    for (int i = 0; i < items2.size(); i++) {
                        if (items2.get(i).get("cookStatus").equals(0)) {
                            num++;
                        }
                    }
                    if (num > 0) {
                        Loge(num + "");
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        if (notification == null) return;
                        Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
                        try {
                            r.play();
                        } catch (Exception e) {

                        }
                        TextToVoice("您还有订单未接单，请及时处理！");
                    }
//                    else{
//                        handler.removeCallbacks(noticeRunnable);
//                    }
                }
            }
        }, parmsMap);
    }

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

    @Override
    public void onScrollStateChange(int state, float scrollPercent) {

    }

    @Override
    public void onEdgeTouch(int edgeFlag) {

    }

    @Override
    public void onScrollOverThreshold() {

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {
        private List<Map<String, Object>> data;
        private List<Map<String, Object>> items = new ArrayList<>();
        private List<Map<String, Object>> giftsItem = new ArrayList<>();
        private LayoutInflater inflater;
        private Context context;

        public MyAdapter(Context context, List<Map<String, Object>> data) {
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
            View itemView = inflater.inflate(R.layout.item_order_detail, parent, false);
            final MyViewHolder holder = new MyViewHolder(itemView);
            return holder;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            //LinearLayout ll_tel = (LinearLayout) holder.getView(R.id.ll_tel);
            //TextView tv_tel = (TextView) holder.getView(R.id.tv_tel);
            TextView tv_vip = (TextView) holder.getView(R.id.tv_vip);

            TextView tvGetNum = (TextView) holder.getView(R.id.tv_get_num);
            TextView tvLocation = (TextView) holder.getView(R.id.tv_location);
            TextView tvSeatChange = (TextView) holder.getView(R.id.tv_seat_change);
            RecyclerView lvDetail = (RecyclerView) holder.getView(R.id.lv_detail);
            RecyclerView rlZb = (RecyclerView) holder.getView(R.id.lv_detail_gift);
            ImageView ivStatus = (ImageView) holder.getView(R.id.iv_status);
            TextView tvAllMoney = (TextView) holder.getView(R.id.tv_all_money);
            TextView tvStatusDo = (TextView) holder.getView(R.id.tv_status_do);
            TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
            TextView tvSeat = (TextView) holder.getView(R.id.tv_seat);
            ImageView ivStatusBottom = (ImageView) holder.getView(R.id.iv_status_bottom);
            TextView tvStatusRight = (TextView) holder.getView(R.id.tv_status_right);
            LinearLayout llDetails = (LinearLayout) holder.getView(R.id.ll_details);
            LinearLayout llbG = (LinearLayout) holder.getView(R.id.ll_bg);
            LinearLayout llSeat = (LinearLayout) holder.getView(R.id.ll_seat);
            LinearLayout llSerive = (LinearLayout) holder.getView(R.id.ll_serive);
            LinearLayout llNote = (LinearLayout) holder.getView(R.id.ll_note);
            TextView tvNote2 = (TextView) holder.getView(R.id.tv_note2);
            TextView tvType = (TextView) holder.getView(R.id.tv_type);
            TextView tvThing = (TextView) holder.getView(R.id.tv_thing);
            TextView tvThing1 = (TextView) holder.getView(R.id.tv_thing1);
            TextView tvPhone = (TextView) holder.getView(R.id.tv_phone);
            TextView tvNote = (TextView) holder.getView(R.id.tv_note);
            ImageView ivld = (ImageView) holder.getView(R.id.iv_lingdang);
            final Map<String, Object> item = data.get(position);
            if ((notNullString(item.get("isUrge")).equals("1") || notNullString(item.get("isUrge")).equals("2"))
                    && (Integer.parseInt(item.get("cookStatus").toString()) != -1 && Integer.parseInt(item.get("cookStatus").toString()) != -2)) {//1次催单 \\ 2次催单 &&（ 不缺货&&不有误）
                tvStatusRight.setText("催单");   //
                ivStatusBottom.setVisibility(View.VISIBLE);
                tvStatusRight.setVisibility(View.VISIBLE);
            } else {

            }
            if ((item.get("cookStatus").equals(2) || item.get("status").equals(-1)) && Integer.parseInt(item.get("isUrge").toString()) != 0) {  //已完成 \\ 已退款 && 催单
                ivStatusBottom.setVisibility(View.GONE);
                tvStatusRight.setVisibility(View.GONE);
            } else {

            }
            tvGetNum.setText(item.get("takeCode").toString());   //取货号
            if (item.get("orderType").equals(2)) {  //  送餐到位       cookStatus  3运送中（order_type=2时有）待取货（order_type=1 废弃）
                if (item.get("payType").equals(10)) {  //会员卡支付
                    tv_vip.setVisibility(View.VISIBLE);
                }
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

                //ll_tel.setVisibility(View.VISIBLE);
                //tv_tel.setText(notNullString(item.get("phone")));

                tvNote2.setText(notNullString(item.get("remark")));
                tvLocation.setText(notNullString(item.get("seat")));
                tvStatusDo.setTextColor(Color.parseColor("#ffa100"));
                if (item.get("status").equals(1)) {
                    if (item.get("isChangeSeat").equals(1)) {
                        tvSeatChange.setVisibility(View.VISIBLE);
                    } else {
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
                        if (notNullString(item.get("isUrge")).equals("1")) {
                            ivStatusBottom.setVisibility(View.VISIBLE);
                            tvStatusRight.setVisibility(View.VISIBLE);
                        }
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
                        if (notNullString(item.get("isUrge")).equals("1")) {
                            ivStatusBottom.setVisibility(View.VISIBLE);
                            tvStatusRight.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (item.get("status").equals(-1)) {
                    ivStatus.setBackgroundResource(R.mipmap.btn5);
                    tvStatus.setText("已退款");
                    tvStatus.setBackgroundResource(R.color.red);
                    ivStatusBottom.setVisibility(View.GONE);
                    tvStatusRight.setVisibility(View.GONE);
                }
            }
            else if (item.get("orderType").equals(1)) {  //自助取餐   3运送中（order_type=2时有）待取货（order_type=1 废弃）
                if (item.get("payType").equals(10)) {
                    tv_vip.setVisibility(View.VISIBLE);
                }
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
                        if (notNullString(item.get("isUrge")).equals("1")) {
                            ivStatusBottom.setVisibility(View.VISIBLE);
                            tvStatusRight.setVisibility(View.VISIBLE);
                        }
                    } else if (item.get("cookStatus").equals(2)) {
                        ivStatus.setBackgroundResource(R.mipmap.finish);
                        tvStatus.setText("已完成");
                        ivld.setVisibility(View.GONE);
                        tvStatus.setBackgroundResource(R.color.green2);
                        ivStatusBottom.setVisibility(View.GONE);
                        tvStatusRight.setVisibility(View.GONE);
                    } else if (item.get("cookStatus").equals(3)) {
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

            } else {
                //影院服务订单  ordertype  3
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
                List<Map<String, Object>> lists = (List<Map<String, Object>>) item.get("attrList");
                tvThing.setText(notNullString(lists.get(0).get("goodsName")));
                if (lists.size() > 1) {
                    tvThing1.setVisibility(View.VISIBLE);
                    tvThing1.setText(notNullString(lists.get(1).get("goodsName")));
                } else {
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
                } else {

                }
            }
            items = (List<Map<String, Object>>) item.get("attrList");
            giftsItem = (List<Map<String, Object>>) item.get("giftList");

            MyAdapterSon myAdapters = new MyAdapterSon(getActivity(), items, item);
            LinearLayoutManager LayoutManager = new LinearLayoutManager(getActivity());
            lvDetail.setLayoutManager(LayoutManager);
            lvDetail.setAdapter(myAdapters);

            lvDetail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (item.get("orderType").equals(3)) {

                        } else {
                            startActivityForResult(new Intent(getActivity(), OrderFragment.class)
                                    .putExtra("tag", item.get("id").toString())
                                    .putExtra("map", item.toString()), 1
                            );
                        }
                    }
                    return false;
                }
            });

            MyAdapterGoods goodmyAdapter = new MyAdapterGoods(getActivity(), giftsItem, item);
            LinearLayoutManager LayoutManager2 = new LinearLayoutManager(getActivity());
            rlZb.setLayoutManager(LayoutManager2);
            rlZb.setAdapter(goodmyAdapter);

            rlZb.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (item.get("orderType").equals(3)) {

                        } else {
                            startActivityForResult(new Intent(getActivity(), OrderFragment.class)
                                    .putExtra("tag", item.get("id").toString())
                                    .putExtra("map", item.toString()), 1
                            );
                        }
                    }
                    return false;
                }
            });

            ivStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.get("status").equals(1)) {   //已付款
                        if (item.get("orderType").equals(2)) {
                            if (item.get("cookStatus").equals(1)) {
                                cookDone(item.get("id").toString(), holder.getAdapterPosition());
                            } else if (item.get("cookStatus").equals(3)) {
                                confirm(item.get("id").toString(), holder.getAdapterPosition());
                            } else if (item.get("cookStatus").equals(0)) {
                                catchOrder(item.get("id").toString(), holder.getAdapterPosition());
                            }
                        } else if (item.get("orderType").equals(1)) {
                            if (item.get("cookStatus").equals(1)) {
                                confirm(item.get("id").toString(), holder.getAdapterPosition());
                            } else if (item.get("cookStatus").equals(3)) {
                                confirm(item.get("id").toString(), holder.getAdapterPosition());
                            } else if (item.get("cookStatus").equals(0)) {
                                catchOrder(item.get("id").toString(), holder.getAdapterPosition());
                            }
                        } else if (item.get("orderType").equals(4)) {   //外卖 点击
                            if (item.get("dadaStatus").equals(-1)){
                                catchOrder(item.get("id").toString(), holder.getAdapterPosition());
                            }
                            if (item.get("dadaStatus").equals(9)){  //返回餐品  到时候会走明伟接口
                                returnMeal(item.get("orderCode").toString(), holder.getAdapterPosition());

                            }
                            if (item.get("dadaStatus").equals(0)||item.get("dadaStatus").equals(10)||item.get("dadaStatus").equals(5)||item.get("dadaStatus").equals(7)) { //已接单（有呼叫骑手 ）
                                callqishou(item.get("id").toString(), holder.getAdapterPosition());
                            }
                            if (item.get("dadaStatus").equals(4)){
                                myAdpter.notifyDataSetChanged();
                                page = 1;
                                loadData();
                            }
                        } else {
                            if (item.get("cookStatus").equals(2)) {
                                Update(item.get("id").toString(), holder.getAdapterPosition(), "3");
                            } else if (item.get("cookStatus").equals(1)) {
                                Update(item.get("id").toString(), holder.getAdapterPosition(), "2");
                            }
                        }
                    } else {
                    }
                }
            });

            llDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.get("orderType").equals(3)) {

                    } else {
                        startActivityForResult(new Intent(getActivity(), OrderFragment.class)
                                .putExtra("tag", item.get("id").toString())
                                .putExtra("map", item.toString()), 1
                        );
                    }
                }
            });
            ivld.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (popLingdang==null){
//
//                    }
                    popLingdang = new PopLingdang(getContext(), item.get("id").toString(), holder.getAdapterPosition());
                    popLingdang.show(getActivity());
                }
            });
        }

        //通知用户
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
                    listItem.get(position).put("cookStatus", 2);
                    myAdpter.notifyDataSetChanged();
                    page = 1;
                    loadData();
                }
            }
        }, parmsMap);
    }
    //呼叫骑手
    private void callqishou(String orderCard, final int position) {
        /*{"action":null,"eMsg":"success","msg":"成功","result":null,"status":0}*/
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("id", orderCard);
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/callRider", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    if (listItem.get(position).get("orderType").equals(4)){
                        listItem.get(position).put("dadaStatus","1");
                    }
                    myAdpter.notifyDataSetChanged();
                    page = 1;
                    loadData();
                }
            }
        }, parmsMap);
    }

    //返还餐品
    private void returnMeal(String orderCard, final int position) {
        /*{"action":null,"eMsg":"success","msg":"成功","result":null,"status":0}*/
        Map<String, String> parmsMap = new HashMap<>();
        parmsMap.put("adminId", MySharedPreference.getAdminId());
        parmsMap.put("token", MySharedPreference.getToken());
        parmsMap.put("orderCode", orderCard);
        OkHttpManger.getInstance().getAsync(Constant.URL + "order/returnMeal", new OKHttpUICallback.ResultCallback<AppBack>() {
            @Override
            public void onSuccess(AppBack appBack) {
                if (appBack.isSuccess()) {
                    if (listItem.get(position).get("orderType").equals(4)){
                        listItem.get(position).put("dadaStatus","10");
                    }
                    myAdpter.notifyDataSetChanged();
                    page = 1;
                    loadData();
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
                    Loge(listItem.get(position));
                    PrintOrderUtils.print(listItem.get(position));//打印某一订单
                    listItem.get(position).put("cookStatus", 1);
                    if (listItem.get(position).get("orderType").equals(4)){
                        listItem.get(position).put("dadaStatus","0");
                    }
                    myAdpter.notifyDataSetChanged();
//                        handler.removeCallbacks(noticeRunnable);
                    page = 1;
                    loadData();

                }else {
                    toast(appBack.getE_msg());
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
                    listItem.get(position).put("cookStatus", 3);
                    myAdpter.notifyDataSetChanged();
                    page = 1;
                    loadData();
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
                    if (orderType == "2") {
                        listItem.get(position).put("cookStatus", 2);
                        PrintOrderUtils.print(listItem.get(position));//打印某一订单
                    } else {
                        listItem.get(position).put("cookStatus", 3);
                    }
                    myAdpter.notifyDataSetChanged();
                    page = 1;
                    loadData();
                }
            }
        }, parmsMap);
    }

    public class MyAdapterSon extends RecyclerView.Adapter<MyViewHolder> implements View.OnClickListener {
        private List<Map<String, Object>> data;
        private Map<String, Object> item;
        private LayoutInflater inflater;
        private Context context;

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
                        startActivityForResult(new Intent(getActivity(), OrderFragment.class)
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
                        startActivityForResult(new Intent(getActivity(), OrderFragment.class)
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

    private class PopLingdang extends PushPopupWindow {

        private String id = "";
        private int position;

        public PopLingdang(Context context, String id, int position) {
            super(context);
            initView();
            setTouchable(true);
            this.id = id;
            this.position = position;
        }

        @Override
        protected View generateCustomView() {
            View view = View.inflate(context, R.layout.pop_style_lingdan, null);
            TextView tvSummit = view.findViewById(R.id.btn_contact_us_confirm);
            TextView tvCancel = view.findViewById(R.id.btn_contact_us_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            tvSummit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cookDone(id, position);
                    dismiss();
                }
            });

            return view;
        }
    }
}