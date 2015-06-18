package com.maogousoft.logisticsmobile.driver.activity.info;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.activity.MainActivity;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.model.UserInfo;
import com.maogousoft.logisticsmobile.driver.utils.CheckUtils;
import com.maogousoft.logisticsmobile.driver.utils.MD5;
import com.maogousoft.logisticsmobile.driver.utils.MyAlertDialog;

/**
 * 货主注册
 *
 * @author ybxiang
 */
public class RegisterShipperActivity extends BaseActivity {

    // 短信限制时间
    private static final int MAX = 60;

    private int current = MAX;
    private Button mLogin, mRegister, mGetVerifyCode;

    private EditText mPhone, mVerifyCode, mPassword, mPassword2, mRecommender,
            mCompanyName;

    private CheckBox mCheckBox;
    // 点击查看协议
    private TextView mSkan;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_shipper_register);
        initViews();
        initHandler();
        // initUtils();
        initDatas();
        setIsShowAnonymousActivity(false);
    }

    // 初始化视图
    private void initViews() {
        ((TextView) findViewById(R.id.titlebar_id_content))
                .setText(R.string.string_title_register);

        mLogin = (Button) findViewById(R.id.titlebar_id_more);
        mRegister = (Button) findViewById(R.id.info_id_register_submit);

        mGetVerifyCode = (Button) findViewById(R.id.info_id_register_getverifycode);
        mPhone = (EditText) findViewById(R.id.info_id_register_phone);
        mCompanyName = (EditText) findViewById(R.id.company_nameView);
        mPassword = (EditText) findViewById(R.id.info_id_register_password);
        mPassword2 = (EditText) findViewById(R.id.info_id_register_password2);
        mVerifyCode = (EditText) findViewById(R.id.info_id_register_verifycode);
        mRecommender = (EditText) findViewById(R.id.info_id_register_recommender);

        mSkan = (TextView) findViewById(R.id.info_id_register_skan_remark);

        mSkan.setText(Html.fromHtml("<u>点击可进入查看协议</u>"));

        mCheckBox = (CheckBox) findViewById(R.id.info_id_login_autologin);

        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mGetVerifyCode.setOnClickListener(this);

        mSkan.setOnClickListener(this);

        mPhone.setOnFocusChangeListener(mOnFocusChangeListener);
        mVerifyCode.setOnFocusChangeListener(mOnFocusChangeListener);
        mPassword.setOnFocusChangeListener(mOnFocusChangeListener);
        mPassword2.setOnFocusChangeListener(mOnFocusChangeListener);
        mCompanyName.setOnFocusChangeListener(mOnFocusChangeListener);
        mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    // 初始化数据，获取车型
    private void initDatas() {
    }

    private void initHandler() {
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.what > 0) {
                    mGetVerifyCode.setText(String.format("重新发送(%s)", msg.what));
                    mGetVerifyCode.setClickable(false);
                } else {
                    mGetVerifyCode
                            .setText(R.string.string_register_getverifycode);
                    mGetVerifyCode.setClickable(true);
                }
            }
        };
    }

    // checkbox选中事件
    private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (isChecked) {
                mRegister.setEnabled(true);
            } else {
                showMsg("不同意使用协议，将不能注册");
                mRegister.setEnabled(false);
            }
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        final int id = v.getId();
        switch (id) {
            case R.id.info_id_register_getverifycode:
                getCode(mPhone.getText().toString());
                break;
            case R.id.info_id_register_submit:
                submit();
                break;

            case R.id.info_id_register_skan_remark:
                new AlertDialog.Builder(mContext)
                        .setTitle("易运宝平台服务使用协议")
                        .setMessage(R.string.tips_reg)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                break;

            default:
                break;
        }
    }

    // 输入框失去焦点事件监听
    private final OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v == mPhone && !hasFocus) {

            } else if (v == mVerifyCode && !hasFocus) {

            } else if (v == mPassword && !hasFocus) {

            } else if (v == mPassword2 && !hasFocus) {

            }
        }
    };

    // 提交注册
    private void submit() {
        if (!CheckUtils.checkIsEmpty(mPhone)) {
            showMsg("手机号码不能为空");
            mPhone.requestFocus();
            return;
        }
        if (!CheckUtils.checkPhone(mPhone.getText().toString())) {
            showMsg("手机号码格式不正确");
            mPhone.requestFocus();
            mPhone.selectAll();
            return;
        }

        if (!CheckUtils.checkIsEmpty(mVerifyCode)) {
            showMsg("请输入验证码");
            mVerifyCode.requestFocus();
            return;
        }
        if (!CheckUtils.checkIsEmpty(mPassword)) {
            showMsg("密码不能为空");
            mPassword.requestFocus();
            return;
        }
        if (!CheckUtils.checkIsEmpty(mPassword2)) {
            showMsg("确认密码不能为空");
            mPassword2.requestFocus();
            return;
        }
        if (!mPassword2.getText().toString()
                .equals(mPassword.getText().toString())) {
            showMsg("两次密码不一致");
            mPassword2.requestFocus();
            mPassword2.selectAll();
        }
        if (!CheckUtils.checkIsEmpty(mCompanyName)) {
            showMsg("公司名称不能为空");
            mCompanyName.requestFocus();
            return;
        }

        final JSONObject jsonObject = new JSONObject();
        final JSONObject params = new JSONObject();
        try {
            jsonObject.put(Constants.ACTION, Constants.SHIPPER_REG);
            jsonObject.put(Constants.TOKEN, null);
            params.put("phone", mPhone.getText().toString());
            // params.put("car_phone", mShuiChePhone.getText().toString());
            params.put("vcode", mVerifyCode.getText().toString());
            params.put("password", MD5.encode(mPassword.getText().toString()));
            params.put("name", "");
            params.put("recommender_phone", mRecommender.getText().toString());
            params.put("company", mCompanyName.getText().toString());
            params.put("device_type", Constants.DEVICE_TYPE);
            jsonObject.put(Constants.JSON, params);
            showSpecialProgress();
            ApiClient.doWithObject(Constants.SHIPPER_SERVER_URL, jsonObject,
                    UserInfo.class, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result instanceof UserInfo) {
                                        UserInfo mUserInfo = (UserInfo) result;
                                        application.setToken(mUserInfo.getToken());
                                        application.writeUserInfo(mPhone.getText()
                                                .toString(), mPassword.getText()
                                                .toString(), mUserInfo.getDriver_id(), mUserInfo.getId());
                                        application.startXMPPService();

                                        final MyAlertDialog dialog = new MyAlertDialog(mContext, R.style.DialogTheme);
                                        dialog.show();
                                        dialog.setTitle("提示");
                                        dialog.setMessage("注册成功");
                                        dialog.setLeftButton("完善资料",
                                                new OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(mContext, OptionalShipperActivity.class);
                                                        intent.putExtra("isFormRegisterActivity", true);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                        dialog.setRightButton("进入易运宝",
                                                new OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                        startActivity(new Intent(mContext, MainActivity.class));
                                                        // 第一次进入，获取 所有 新货源
                                                        // getNewSourceData(1);
                                                        finish();
                                                    }
                                                });
                                    }
                                    break;
                                case ResultCode.RESULT_ERROR:
                                    if (result != null)
                                        showMsg(result.toString());
                                    break;
                                case ResultCode.RESULT_FAILED:
                                    if (result != null)
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

    // 开启计时器
    private void startTimer() {
        current = MAX;
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                if (current > 0) {
                    current--;
                } else {
                    timer.cancel();
                }
                mHandler.sendEmptyMessage(current);
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    /**
     * 司机端获取验证码
     */
    private void getCode(final String phone) {
        if (!CheckUtils.checkPhone(phone)) {
            showMsg("手机号码格式不正确!");
            return;
        }
        final JSONObject params = new JSONObject();
        try {
            params.put(Constants.ACTION, Constants.SHIPPER_REG_GETCODE);
            params.put(Constants.TOKEN, null);
            params.put(Constants.JSON, new JSONObject().put("phone", phone)
                    .toString());
            ApiClient.doWithObject(Constants.SHIPPER_SERVER_URL, params, null,
                    new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    // 获取验证码成功，开始计时
                                    startTimer();
                                    break;
                                case ResultCode.RESULT_ERROR:
                                    showMsg(result.toString());
                                    break;
                                case ResultCode.RESULT_FAILED:
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
