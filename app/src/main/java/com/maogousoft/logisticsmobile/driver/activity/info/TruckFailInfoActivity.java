package com.maogousoft.logisticsmobile.driver.activity.info;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.model.TruckFailInfo;
import com.maogousoft.logisticsmobile.driver.widget.HeaderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aliang on 2015/5/24.
 */
public class TruckFailInfoActivity extends BaseActivity {

    private HeaderView mHeaderView;
    private TextView desc;
    private List<TruckFailInfo> truckFailInfoList;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_truck_loading_fail_info);

        initView();
        initData();
    }

    private void initView() {
        mHeaderView = (HeaderView) findViewById(R.id.headerView);
        mHeaderView.setTitle("装车不成功");
        desc = (TextView) findViewById(R.id.desc);
    }

    private void initData() {
        orderId = getIntent().getIntExtra(Constants.ORDER_ID, -1);
        getData(orderId);
    }

    private void getData(int orderId) {
        try {
            showDefaultProgress();
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.GET_TRUCK_LOADING_FAIL_INFO);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject().put("order_id", orderId).toString());

            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    TruckFailInfo.class, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result instanceof ArrayList) {
                                        truckFailInfoList = (List<TruckFailInfo>) result;
                                        if(truckFailInfoList.size() == 1) {
                                            //责任人 1托运方、2承运方
                                            String cause = "";
                                            TruckFailInfo truckFailInfo = truckFailInfoList.get(0);
                                            switch (truckFailInfo.getResponsible_people()) {
                                                case 1:
                                                    cause = "托运方";
                                                    break;
                                                case 2:
                                                    cause = "承运方";
                                                    break;
                                            }
                                            desc.setText(Html.fromHtml(getString(R.string.truck_loading_fail, cause)));
                                        } else {
                                            //大于2条不成功原因，认为是纠纷问题
                                            desc.setText(Html.fromHtml(getString(R.string.truck_loading_fail_other)));
                                            findViewById(R.id.error).setVisibility(View.GONE);
                                        }
                                        /*for(TruckFailInfo truckFailInfo:truckFailInfoList) {
                                            if (TextUtils.equals("Y", truckFailInfo.getIs_able_has_confirm())) {
                                                findViewById(R.id.confirm).setVisibility(View.VISIBLE);
                                                return;
                                            }
                                        }*/
                                    }
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

    public void onConfirm(View view) {
        try {
            showProgress("正在处理");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.LOADING_FAIL_CONTRACT_BALANCE);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject().put("order_id", orderId).toString());

            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    null, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    showMsg("装车失败已确认完结货单!");
                                    Intent intent = new Intent(Constants.ACTION_NOTIFICATION_ORDER_CONFIRM);
                                    intent.putExtra(Constants.ORDER_ID, orderId);
                                    sendBroadcast(intent);
                                    finish();
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

    public void onError(View view) {
        if(truckFailInfoList.size() == 1) {
            TruckFailInfo truckFailInfo = truckFailInfoList.get(0);
            Intent intent = new Intent(mContext, TruckFailedReasonActivity.class);
            intent.putExtra(Constants.ORDER_ID, Integer.parseInt(truckFailInfo.getOrder_id()));
            startActivity(intent);
            finish();
        }
    }
}
