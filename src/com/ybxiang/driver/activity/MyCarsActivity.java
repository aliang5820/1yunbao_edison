package com.ybxiang.driver.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseListActivity;
import com.maogousoft.logisticsmobile.driver.adapter.MyCarInfoListAdapter;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.model.CarInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyCarsActivity extends BaseListActivity implements
        OnClickListener, OnScrollListener {
    private Context mContext;
    private Button mTitleBarBack;
    private Button mTitleBarMore;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MyCarsActivity.this;
        initViews();
    }

    private void initViews() {
        ((TextView) findViewById(R.id.titlebar_id_content)).setText("我的车队");
        // 返回按钮生效
        mTitleBarBack = (Button) findViewById(R.id.titlebar_id_back);
        mTitleBarBack.setOnClickListener(this);
        // 更多按钮隐藏
        mTitleBarMore = (Button) findViewById(R.id.titlebar_id_more);
        mTitleBarMore.setText("添加车辆");
        mTitleBarMore.setOnClickListener(this);

        // 数据加载中进度条
        mFootView = getLayoutInflater().inflate(R.layout.listview_footview,
                null);
        mFootView.setClickable(false);
        mFootProgress = (ProgressBar) mFootView
                .findViewById(android.R.id.progress);
        mFootMsg = (TextView) mFootView.findViewById(android.R.id.text1);
        mListView.addFooterView(mFootView);

        // 初始化MyCarInfoListAdapter的adapter
        mAdapter = new MyCarInfoListAdapter(mContext);
        setListAdapter(mAdapter);
        // list未加载数据不显示
        setListShown(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter.isEmpty()) {
            pageIndex = 1;
            getData(pageIndex);
        }
    }

    // 请求指定页数的数据
    private void getData(int page) {
        try {
            state = ISREFRESHING;
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.QUERY_MY_FLEET);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject().put("page", page).toString());
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    CarInfo.class, new AjaxCallBack() {
                        @Override
                        public void receive(int code, Object result) {
                            setListShown(true);
//                            switch (code) {
//                                case ResultCode.RESULT_OK:
//                                    if (result instanceof List) {
//                                        List<CarInfo> mList = (List<CarInfo>) result;
                            List<CarInfo> mList = new ArrayList<CarInfo>();
                                    CarInfo carInfo1 = new CarInfo();
                                        carInfo1.setOwer_name("司机1");
                            carInfo1.setPlate_number("川A12245");
                            carInfo1.setOwer_phone("15982034811");
                                        CarInfo carInfo2 = new CarInfo();
                                        carInfo2.setOwer_name("司机2");
                            carInfo2.setPlate_number("川A12245");
                            carInfo2.setOwer_phone("15982034811");
                                        CarInfo carInfo3 = new CarInfo();
                                        carInfo3.setOwer_name("司机3");
                            carInfo3.setPlate_number("川A12245");
                            carInfo3.setOwer_phone("15982034811");
                                        CarInfo carInfo4 = new CarInfo();
                                        carInfo4.setOwer_name("司机4");
                            carInfo4.setPlate_number("川A12245");
                            carInfo4.setOwer_phone("15982034811");
                                        mList.add(carInfo1);
                                        mList.add(carInfo2);
                                        mList.add(carInfo3);
                                        mList.add(carInfo4);
                                        if (mList == null || mList.isEmpty()) {
                                            load_all = true;
                                            mFootProgress.setVisibility(View.GONE);
                                            mFootMsg.setText("已加载全部");
                                        } else {
                                            if (mList.size() < 10) {
                                                load_all = true;
                                                mFootProgress.setVisibility(View.GONE);
                                                mFootMsg.setText("已加载全部");
                                            } else {
                                                load_all = false;
                                                mFootProgress.setVisibility(View.VISIBLE);
                                                mFootMsg.setText(R.string.tips_isloading);
                                            }
                                            mAdapter.addAll(mList);
                                            mAdapter.notifyDataSetChanged();
                                        }
//                                    }
//                                    break;
//                                case ResultCode.RESULT_ERROR:
//                                    if (result instanceof String)
//                                        showMsg(result.toString());
//                                    break;
//                                case ResultCode.RESULT_FAILED:
//                                    if (result instanceof String)
//                                        showMsg(result.toString());
//                                    break;
//
//                                default:
//                                    break;
//                            }
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
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titlebar_id_more:
                startActivity(new Intent(context, AddCarActivity.class));
                break;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(R.array.menu_friends_operation,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        switch (arg1) {
                            case 0:
                                Toast.makeText(mContext, "查看他的车源",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(mContext, "发送我的定位",
                                        Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                Toast.makeText(mContext, "发送我的定位=" + arg1,
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                });
        builder.create().show();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) {
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
            getData(++pageIndex);
        }
    }
}
