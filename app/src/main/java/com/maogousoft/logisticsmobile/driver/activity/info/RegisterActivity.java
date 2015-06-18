package com.maogousoft.logisticsmobile.driver.activity.info;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.text.TextUtils;
import com.maogousoft.logisticsmobile.driver.utils.LocHelper;
import com.maogousoft.logisticsmobile.driver.utils.LogUtil;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.maogousoft.logisticsmobile.driver.activity.home.SourceDetailActivity;
import com.maogousoft.logisticsmobile.driver.adapter.CityListAdapter;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.db.CityDBUtils;
import com.maogousoft.logisticsmobile.driver.model.CityInfo;
import com.maogousoft.logisticsmobile.driver.model.NewSourceInfo;
import com.maogousoft.logisticsmobile.driver.model.UserInfo;
import com.maogousoft.logisticsmobile.driver.utils.CheckUtils;
import com.maogousoft.logisticsmobile.driver.utils.MD5;
import com.maogousoft.logisticsmobile.driver.utils.MyAlertDialog;
import com.maogousoft.logisticsmobile.driver.widget.MyGridView;

/**
 * 注册
 *
 * @author lenovo
 */
public class RegisterActivity extends BaseActivity {

    // 短信限制时间
    private static final int MAX = 60;

    private int current = MAX;
    private Button mLogin, mRegister, mGetVerifyCode;

    private EditText mPhone, mVerifyCode, mPassword, mPassword2, mRecommender;

    private CheckBox mCheckBox;
    // 点击查看协议
    private TextView mSkan;

    private Button mSelectProvinceFirst, mSelectCityFirst,
            mSelectEndProvinceFirst, mSelectEndCityFirst;

    private MyGridView mGridViewFirst, mGridViewFirst2;

    private Handler mHandler;

    private CityDBUtils mDBUtils;

    private CityListAdapter mAdapterFirst, mAdapterFirst2;

    // 当前选中线路1的一级城市，二级城市
    private CityInfo currentProvinceFirst, currentCityFirst,
            currentEndProvinceFirst, currentEndCityFirst;
    private double longitude;//经度
    private double latitude;//纬度
    private String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_register);
        initViews();
        initHandler();
        initUtils();
        initDatas();
        setIsShowAnonymousActivity(false);
    }

    // 初始化视图
    private void initViews() {
        ((TextView) findViewById(R.id.titlebar_id_content))
                .setText(R.string.string_title_register);

        mLogin = (Button) findViewById(R.id.titlebar_id_more);
        mRegister = (Button) findViewById(R.id.info_id_register_submit);
        mSelectProvinceFirst = (Button) findViewById(R.id.info_id_register_select_province);
        mSelectCityFirst = (Button) findViewById(R.id.info_id_register_select_city);
        mSelectEndProvinceFirst = (Button) findViewById(R.id.info_id_register_select_end_province);
        mSelectEndCityFirst = (Button) findViewById(R.id.info_id_register_select_end_city);

        mGetVerifyCode = (Button) findViewById(R.id.info_id_register_getverifycode);
        mPhone = (EditText) findViewById(R.id.info_id_register_phone);
        mPassword = (EditText) findViewById(R.id.info_id_register_password);
        mPassword2 = (EditText) findViewById(R.id.info_id_register_password2);
        mVerifyCode = (EditText) findViewById(R.id.info_id_register_verifycode);
        mRecommender = (EditText) findViewById(R.id.info_id_register_recommender);
        mGridViewFirst = (MyGridView) findViewById(R.id.info_id_register_city);
        mGridViewFirst2 = (MyGridView) findViewById(R.id.info_id_register_endcity);

        mSkan = (TextView) findViewById(R.id.info_id_register_skan_remark);
        // mSkan.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        // mSkan.getPaint().setUnderlineText(true);

        mSkan.setText(Html.fromHtml("<u>点击可进入查看协议</u>"));

        mCheckBox = (CheckBox) findViewById(R.id.info_id_login_autologin);

        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mGetVerifyCode.setOnClickListener(this);
        mSelectProvinceFirst.setOnClickListener(this);
        mSelectCityFirst.setOnClickListener(this);
        mSelectEndProvinceFirst.setOnClickListener(this);
        mSelectEndCityFirst.setOnClickListener(this);

        mSkan.setOnClickListener(this);

        mPhone.setOnFocusChangeListener(mOnFocusChangeListener);
        mVerifyCode.setOnFocusChangeListener(mOnFocusChangeListener);
        mPassword.setOnFocusChangeListener(mOnFocusChangeListener);
        mPassword2.setOnFocusChangeListener(mOnFocusChangeListener);

        mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    /**
     * 初始化工具类 *
     */
    private void initUtils() {
        mDBUtils = new CityDBUtils(application.getCitySDB());
        mAdapterFirst = new CityListAdapter(mContext);
        mAdapterFirst2 = new CityListAdapter(mContext);

        mGridViewFirst.setAdapter(mAdapterFirst);
        mGridViewFirst2.setAdapter(mAdapterFirst2);
        mGridViewFirst.setOnItemClickListener(mOnItemClickListenerFirst);
        mGridViewFirst2.setOnItemClickListener(mOnItemClickListenerFirst2);

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

    private OnItemClickListener mOnItemClickListenerFirst2 = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position,
                                long id) {
            if (mGridViewFirst.isShown()) {
                mGridViewFirst.setVisibility(View.GONE);
            }
            final CityInfo mCityInfo = (CityInfo) mAdapterFirst2
                    .getItem(position);
            final int mDeep = mCityInfo.getDeep();
            switch (mDeep) {
                case 1:
                    currentEndCityFirst = null;
                    currentEndProvinceFirst = mCityInfo;
                    mSelectEndProvinceFirst.setText(mCityInfo.getName());
                    mSelectEndCityFirst.setText(R.string.string_city);
                    List<CityInfo> mList4 = mDBUtils
                            .getSecondCity(currentEndProvinceFirst.getId());
                    mAdapterFirst2.setList(mList4);
                    break;
                case 2:
                    currentEndCityFirst = mCityInfo;
                    mSelectEndCityFirst.setText(mCityInfo.getName());
                    mGridViewFirst2.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }
        }
    };

    private OnItemClickListener mOnItemClickListenerFirst = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position,
                                long id) {
            if (mGridViewFirst2.isShown()) {
                mGridViewFirst2.setVisibility(View.GONE);
            }
            final CityInfo mCityInfo = (CityInfo) mAdapterFirst
                    .getItem(position);
            final int mDeep = mCityInfo.getDeep();
            switch (mDeep) {
                case 1:
                    currentCityFirst = null;
                    currentProvinceFirst = mCityInfo;
                    mSelectProvinceFirst.setText(mCityInfo.getName());
                    mSelectCityFirst.setText(R.string.string_city);
                    List<CityInfo> mList2 = mDBUtils
                            .getSecondCity(currentProvinceFirst.getId());
                    mAdapterFirst.setList(mList2);
                    break;
                case 2:
                    currentCityFirst = mCityInfo;
                    mSelectCityFirst.setText(mCityInfo.getName());
                    mGridViewFirst.setVisibility(View.GONE);
                    break;

                default:
                    break;
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
            case R.id.info_id_register_select_province:
                if (mGridViewFirst.isShown()) {
                    mGridViewFirst.setVisibility(View.GONE);
                    return;
                }
                if (mGridViewFirst2.isShown()) {
                    mGridViewFirst2.setVisibility(View.GONE);
                }
                mGridViewFirst.setVisibility(View.VISIBLE);
                List<CityInfo> mList = mDBUtils.getFirstCity();
                mAdapterFirst.setList(mList);
                break;
            case R.id.info_id_register_select_city:
                if (currentProvinceFirst == null) {
                    showMsg(R.string.tips_register_select_province);
                    return;
                }
                if (mGridViewFirst2.isShown()) {
                    mGridViewFirst2.setVisibility(View.GONE);
                }
                if (mGridViewFirst.isShown()) {
                    mGridViewFirst.setVisibility(View.GONE);
                    return;
                }
                mGridViewFirst.setVisibility(View.VISIBLE);
                List<CityInfo> mList2 = mDBUtils.getSecondCity(currentProvinceFirst
                        .getId());
                mAdapterFirst.setList(mList2);
                break;
            case R.id.info_id_register_submit:
                submit();
                break;
            case R.id.info_id_register_select_end_province:
                if (mGridViewFirst2.isShown()) {
                    mGridViewFirst2.setVisibility(View.GONE);
                    return;
                }
                if (mGridViewFirst.isShown()) {
                    mGridViewFirst.setVisibility(View.GONE);
                }
                mGridViewFirst2.setVisibility(View.VISIBLE);
                List<CityInfo> mList3 = mDBUtils.getFirstCity();
                mAdapterFirst2.setList(mList3);
                break;
            case R.id.info_id_register_select_end_city:
                if (currentEndProvinceFirst == null) {
                    showMsg(R.string.tips_register_select_province);
                    return;
                }
                if (mGridViewFirst.isShown()) {
                    mGridViewFirst.setVisibility(View.GONE);
                }
                if (mGridViewFirst2.isShown()) {
                    mGridViewFirst2.setVisibility(View.GONE);
                    return;
                }
                mGridViewFirst2.setVisibility(View.VISIBLE);
                List<CityInfo> mList4 = mDBUtils
                        .getSecondCity(currentEndProvinceFirst.getId());
                mAdapterFirst2.setList(mList4);
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

    /**
     * 坐标定位
     */
    public void onGetLocation() {
        LocHelper.getInstance(mContext).getResult(new LocHelper.LocCallback() {

            @Override
            public void onRecivedLoc(double lat, double lng, String addr) {

                if (lat != 0 && lng != 0 && !TextUtils.isEmpty(addr)) {
                    longitude = lng;
                    latitude = lat;
                    address = addr;
                    LogUtil.d(TAG, addr);
                }
            }
        });
    }

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

        if (currentProvinceFirst == null || currentCityFirst == null
                || currentEndProvinceFirst == null
                || currentEndCityFirst == null) {
            showMsg("第一条路线为必选");
            return;
        }

        final JSONObject jsonObject = new JSONObject();
        final JSONObject params = new JSONObject();
        try {
            jsonObject.put(Constants.ACTION, Constants.DRIVER_REG);
            jsonObject.put(Constants.TOKEN, null);
            params.put("phone", mPhone.getText().toString());
            // params.put("car_phone", mShuiChePhone.getText().toString());
            params.put("vcode", mVerifyCode.getText().toString());
            params.put("password", MD5.encode(mPassword.getText().toString()));
            params.put("longitude", longitude);//经度
            params.put("latitude", latitude);//纬度
            params.put("address", address);//位置
            params.put("location_time", System.currentTimeMillis());//发布日期
            // params.put("name", mName.getText().toString());
            // params.put("car_length", mLength.getText().toString());
            // params.put(
            // "car_type",
            // mCarTypeAdapter.getList()
            // .get(mCarType.getSelectedItemPosition()).getId());
            // params.put("car_weight", mWeight.getText().toString());
            // params.put("plate_number", mNumber.getText().toString());

            if (currentProvinceFirst != null && currentCityFirst != null
                    && currentEndProvinceFirst != null
                    && currentEndCityFirst != null) {
                params.put("start_province", currentProvinceFirst.getId());
                params.put("start_city", currentCityFirst.getId());
                params.put("end_province", currentEndProvinceFirst.getId());
                params.put("end_city", currentEndCityFirst.getId());
            }
            params.put("name", "");

            params.put("recommender", mRecommender.getText().toString());
            params.put("device_type", Constants.DEVICE_TYPE);
            jsonObject.put(Constants.JSON, params);
            showSpecialProgress();
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
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
                                                        Intent intent = new Intent(mContext, OptionalActivity.class);
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
                                                        getNewSourceData(1);
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
            params.put(Constants.ACTION, Constants.DRIVER_REG_GETCODE);
            params.put(Constants.TOKEN, null);
            params.put(Constants.JSON, new JSONObject().put("phone", phone)
                    .toString());
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, params, null,
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
            android.util.Log.d("ybxiang", "=======");// 没返回结果
        } catch (JSONException e) {
            android.util.Log.d("ybxiang", "e==" + e.toString());
            e.printStackTrace();
        }
    }

    // 获取所有新货源推送
    private void getNewSourceData(int page) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.QUERY_SOURCE_ORDER);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject().put("page", page)
                    .toString());

            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    NewSourceInfo.class, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result instanceof List) {
                                        List<NewSourceInfo> mList = (List<NewSourceInfo>) result;

                                        if (mList != null) {
                                            NotificationManager mNotificationManager = (NotificationManager) mContext
                                                    .getSystemService(Context.NOTIFICATION_SERVICE);

                                            for (int i = 0; i < mList.size(); i++) {
                                                NewSourceInfo newSourceInfo = mList
                                                        .get(i);

                                                StringBuffer sb = new StringBuffer();
                                                sb.append("有新的货源,");
                                                sb.append(newSourceInfo.getCargo_desc());
                                                sb.append(",");
                                                sb.append(newSourceInfo.getCargo_number());

                                                Integer unitPrice = newSourceInfo.getCargo_unit();
                                                if (unitPrice != null && unitPrice > 0) {
                                                    String[] unitPriceStr = mContext.getResources().getStringArray(R.array.car_price_unit);
                                                    for (int j = 0; j < Constants.unitTypeValues.length; j++) {
                                                        if (Constants.unitTypeValues[i] == unitPrice) {
                                                            sb.append(unitPriceStr[i]);
                                                        }
                                                    }
                                                }

                                                mNotificationManager.notify(i, getOrderNotification(
                                                        mContext,
                                                        sb.toString(),
                                                        newSourceInfo, i));
                                            }
                                        }
                                    }
                                    break;
                            }

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 跳入新货源详情
    private Notification getOrderNotification(Context context, String title,
                                              NewSourceInfo sourceInfo, int num) {
        Notification notification = new Notification(R.drawable.ic_launcher,
                title, System.currentTimeMillis());
        final Intent intent = new Intent(context, SourceDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SourceDetailActivity.ORDER_INFO, sourceInfo);
        intent.putExtra("type", "MessageBroadCastReceiver");
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, num,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, title, "点击查看订单详情",
                pendingIntent);
        // notification.sound = Uri.parse("android.resource://"
        // + mContext.getPackageName() + "/" + R.raw.mm);

        if (application.checkIsRingNewSource()) {
            notification.sound = Uri.parse("android.resource://"
                    + context.getPackageName() + "/" + R.raw.mm);
        } else {
            notification.sound = Uri.parse("android.resource://"
                    + context.getPackageName() + "/" + R.raw.silence);
        }

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        return notification;
    }

}
