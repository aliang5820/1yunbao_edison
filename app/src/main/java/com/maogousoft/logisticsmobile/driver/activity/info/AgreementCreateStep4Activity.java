package com.maogousoft.logisticsmobile.driver.activity.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.model.CarrierInfo;
import com.maogousoft.logisticsmobile.driver.widget.HeaderView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aliang on 2014/11/13.
 */
public class AgreementCreateStep4Activity extends BaseActivity {
    private TextView payTip;
    private EditText payPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_step4);
        HeaderView mHeaderView = (HeaderView) findViewById(R.id.headerView);
        mHeaderView.setTitle(R.string.agreement_edit_pay_tip);

        initView();
        initData();
    }

    private void initView() {
        payTip = (TextView) findViewById(R.id.payTip);
        payPassword = (EditText) findViewById(R.id.pay_password);
    }

    private void initData() {
        String money1 = getIntent().getStringExtra(Constants.COMMON_KEY_1);
        String money2 = getIntent().getStringExtra(Constants.COMMON_KEY_2);
        payTip.setText(getString(R.string.agreement_tip, money1, money2));
    }

    public void onNext(View view) {
        if(payPassword.length() == 0) {
            showMsg("请输入支付密码！");
            return;
        }
        getAgreement();
    }

    // 验证支付密码
    private void getAgreement() {
        try {
            showSpecialProgress();
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.VALIDATION_PAY_PASSWORD);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject()
                    .put("pay_password", payPassword.getText()).toString());

            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    CarrierInfo.class, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            Intent intent = new Intent(mContext, AgreementCreateStep3Activity.class);
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    intent.putExtra(Constants.COMMON_KEY, "支付密码验证成功,正在发送承运方!");
                                    intent.putExtra(Constants.PAY_RESULT, 0);
                                    break;
                                case ResultCode.RESULT_ERROR:
                                    intent.putExtra(Constants.COMMON_KEY, result.toString());
                                    intent.putExtra(Constants.PAY_RESULT, 1);
                                    break;
                                case ResultCode.RESULT_FAILED:
                                    intent.putExtra(Constants.COMMON_KEY, result.toString());
                                    intent.putExtra(Constants.PAY_RESULT, 1);
                                    break;
                                default:
                                    break;
                            }
                            startActivity(intent);
                            finish();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
