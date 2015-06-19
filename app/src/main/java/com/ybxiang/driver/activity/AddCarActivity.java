package com.ybxiang.driver.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 添加车辆
 * Created by aliang on 2014/8/14.
 */
public class AddCarActivity extends BaseActivity {

    private EditText mCarNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_cars);
        initViews();
        initData();
    }

    // 初始化视图
    private void initViews() {
        ((TextView) findViewById(R.id.titlebar_id_content)).setText("添加车辆");
        findViewById(R.id.titlebar_id_more).setVisibility(View.GONE);
        mCarNum = (EditText) findViewById(R.id.mCarNum); //车牌号
    }

    private void initData() {

    }

    // 请求数据
    public void addToMyCar(View view) {
        try {
            if (TextUtils.isEmpty(mCarNum.getText())) {
                Toast.makeText(mContext, "请输入司机的车牌号码或者运宝账号", Toast.LENGTH_SHORT).show();
                return;
            }
            final JSONObject jsonObject = new JSONObject();
            JSONObject params = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.ADD_MY_FLEET_BY_ACCOUNT);
            jsonObject.put(Constants.TOKEN, application.getToken());
            //组装参数
            params.put("account", mCarNum.getText());
            //组装参数结束
            jsonObject.put(Constants.JSON, params.toString());
            showSpecialProgress();
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    null, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result instanceof String) {
                                        if(!TextUtils.isEmpty(result.toString())) {
                                            showMsg(result.toString());
                                        } else {
                                            showMsg("成功添加到我的车队");
                                        }
                                        finish();
                                    }
                                    break;
                                case ResultCode.RESULT_ERROR:
                                    if (result instanceof String)
                                        showMsg(result.toString());
                                    break;
                                case ResultCode.RESULT_FAILED:
                                    if (result instanceof String)
                                        showMsg(result.toString());
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
