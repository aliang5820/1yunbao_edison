package com.maogousoft.logisticsmobile.driver.activity.info;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseListActivity;
import com.maogousoft.logisticsmobile.driver.activity.home.InvoiceActivity;
import com.maogousoft.logisticsmobile.driver.adapter.InvoiceAdapter;
import com.maogousoft.logisticsmobile.driver.adapter.PushByDriverAdapter;
import com.maogousoft.logisticsmobile.driver.adapter.PushByOrderAdapter;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.model.CarInfo;
import com.maogousoft.logisticsmobile.driver.model.CarInfoList;
import com.maogousoft.logisticsmobile.driver.model.NewSourceInfo;
import com.maogousoft.logisticsmobile.driver.utils.LogUtil;
import com.maogousoft.logisticsmobile.driver.utils.MyAlertDialog;
import com.ybxiang.driver.activity.MySourceActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EdisonZhao on 15/6/5.
 */
public class PushToDriverActivity extends BaseListActivity implements AbsListView.OnScrollListener {
    private static final String TAG = "PushToDriverFragment";
    private int orderId = 0;
    private int driverId = 0;
    // 底部更多
    private View mFootView;
    private ProgressBar mFootProgress;
    private TextView mFootMsg;
    // 当前模式
    private int state = WAIT;
    // 当前页码
    private int pageIndex = 1;
    // 滑动状态
    private boolean state_idle = false;
    // 已加载全部
    private boolean load_all = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        TextView mTitleBarContent = ((TextView) findViewById(R.id.titlebar_id_content));
        mTitleBarContent.setText("推送");
        // 更多按钮隐藏
        TextView mTitleBarMore = (Button) findViewById(R.id.titlebar_id_more);
        mTitleBarMore.setText("确定");
        mTitleBarMore.setOnClickListener(this);
        // 页脚信息
        mFootView = LayoutInflater.from(mContext).inflate(R.layout.listview_footview, null);
        mFootView.setClickable(false);
        mFootProgress = (ProgressBar) mFootView.findViewById(android.R.id.progress);
        mFootMsg = (TextView) mFootView.findViewById(android.R.id.text1);
        mListView.addFooterView(mFootView);
        mListView.setOnScrollListener(this);


    }

    private void initData() {
        orderId = getIntent().getIntExtra(Constants.ORDER_ID, 0);
        driverId = getIntent().getIntExtra(Constants.DRIVER_ID, 0);
        if (orderId != 0) {
            //选择推送的司机
            mAdapter = new PushByDriverAdapter(mContext);
            getData(pageIndex, CarInfoList.class);
        } else {
            //选择推送的货源
            mAdapter = new PushByOrderAdapter(mContext);
            getData(pageIndex, NewSourceInfo.class);
        }
        setListAdapter(mAdapter);
    }

    // 请求指定页数的数据
    private void getData(final int page, final Class clz) {
        try {
            state = ISREFRESHING;
            final JSONObject jsonObject = new JSONObject();
            JSONObject json = new JSONObject();
            if (orderId != 0) {
                jsonObject.put(Constants.ACTION, Constants.QUERY_MY_FLEET);
            } else {
                jsonObject.put(Constants.ACTION, Constants.QUERY_ORDER);
                json = new JSONObject();
                json.put("order_type", 1);//待定货单
            }
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, json.put("page", page).toString());
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    clz, new AjaxCallBack() {
                        @Override
                        public void receive(int code, Object result) {
                            setListShown(true);
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result instanceof CarInfoList) {
                                        CarInfoList carInfoList = (CarInfoList) result;
                                        List<CarInfo> mList = carInfoList.getList();
                                        if (mList == null || mList.isEmpty()) {
                                            load_all = true;
                                            mFootProgress.setVisibility(View.GONE);
                                            mFootMsg.setText("已加载全部");
                                        } else {
                                            if (mList.size() < 10) {
                                                load_all = true;
                                                mFootProgress.setVisibility(View.GONE);
                                                mFootMsg.setText("已加载全部");
                                            }
                                            mAdapter.addAll(mList);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    } else if (result instanceof ArrayList) {
                                        List<NewSourceInfo> mList = (List<NewSourceInfo>) result;
                                        if (mList.isEmpty()) {
                                            load_all = true;
                                            mFootProgress.setVisibility(View.GONE);
                                            mFootMsg.setText("已加载全部");
                                        } else {
                                            if (mList.size() < 10) {
                                                load_all = true;
                                                mFootProgress.setVisibility(View.GONE);
                                                mFootMsg.setText("已加载全部");
                                            }
                                            mAdapter.addAll(mList);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    break;
                                case ResultCode.RESULT_ERROR:
                                    if (result instanceof String)
                                        showMsg(result.toString());
                                    break;
                                case ResultCode.RESULT_FAILED:
                                    if (result instanceof String) {
                                        showMsg(result.toString());
                                    }
                                    break;
                                default:
                                    break;
                            }
                            if (mAdapter.isEmpty()) {
                                setEmptyText("没有找到数据哦");
                            }
                            state = WAIT;
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_id_more:
                if (orderId > 0) {
                    //选择车源
                    SparseArray<CarInfo> sparseArray = ((PushByDriverAdapter) mAdapter).getSelectedDriver();
                    if (sparseArray.size() == 0) {
                        showMsg("请选择需要推送的车源！");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < sparseArray.size(); i++) {
                            int key = sparseArray.keyAt(i);
                            CarInfo carInfo = sparseArray.get(key);
                            if (carInfo.getDriverInfo() != null) {
                                sb.append(carInfo.getDriverInfo().getId()).append(",");
                            }
                        }
                        String drivers = sb.toString();
                        if (drivers.length() > 0) {
                            drivers = drivers.substring(0, drivers.length() - 1);
                            doAction(orderId, drivers);
                        } else {
                            showMsg("抱歉，你选择的车源没有司机信息！");
                        }
                    }
                } else {
                    //选择货源
                    SparseArray<NewSourceInfo> sparseArray = ((PushByOrderAdapter) mAdapter).getSelectedOrder();
                    if (sparseArray.size() == 0) {
                        showMsg("请选择需要推送的货单！");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < sparseArray.size(); i++) {
                            int key = sparseArray.keyAt(i);
                            NewSourceInfo sourceInfo = sparseArray.get(key);
                            if (sourceInfo != null) {
                                sb.append(sourceInfo.getId()).append(",");
                            }
                        }
                        String orders = sb.toString();
                        if (orders.length() > 0) {
                            orders = orders.substring(0, orders.length() - 1);
                            doAction(orders, driverId);
                        }
                    }
                }
                break;
        }
    }

    private void doAction(Object orderId, Object driverId) {
        try {
            showProgress("正在处理");
            LogUtil.d(TAG, "push driver_id:" + driverId + ",order_id:" + orderId);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.PUSH_ORDER);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject().put("order_id", orderId).put("driver_id", driverId));

            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    null, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    final MyAlertDialog dialog = new MyAlertDialog(mContext, R.style.DialogTheme);
                                    dialog.show();
                                    dialog.setTitle("提示");
                                    dialog.setMessage("推送成功");
                                    dialog.setLeftButton("确定",
                                            new View.OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            });
                                    break;
                                default:
                                    showMsg(result.toString());
                                    break;
                            }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            return;
        }
        if (state != WAIT) {
            return;
        }
        this.state_idle = true;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (!this.state_idle) {
            return;
        }
        if (firstVisibleItem == 0
                || (firstVisibleItem + visibleItemCount) != totalItemCount) {
            return;
        }
        // 如果当前没有加载数据
        if (state != ISREFRESHING && !load_all) {
            if (orderId != 0) {
                getData(++pageIndex, CarInfoList.class);
            } else {
                getData(++pageIndex, NewSourceInfo.class);
            }
            mFootProgress.setVisibility(View.VISIBLE);
            mFootMsg.setText(R.string.tips_isloading);
        }
    }
}
