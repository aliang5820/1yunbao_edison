package com.ybxiang.driver.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.activity.home.UserCreditActivity;
import com.maogousoft.logisticsmobile.driver.activity.info.PushToDriverActivity;
import com.maogousoft.logisticsmobile.driver.activity.share.ShareActivity;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.db.CityDBUtils;
import com.maogousoft.logisticsmobile.driver.model.CarInfo;
import com.maogousoft.logisticsmobile.driver.model.DriverInfo;
import com.maogousoft.logisticsmobile.driver.model.PopupMenuInfo;
import com.maogousoft.logisticsmobile.driver.utils.LogUtil;
import com.maogousoft.logisticsmobile.driver.widget.HeaderView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ybxiang.driver.model.LocationInfo;
import com.ybxiang.driver.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aliang on 2014/8/22.
 */
public class MyCarsDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private int id;
    private TextView driver_name, carState, way1, way2, way3, plate_number, car_weight, car_length,
            car_type, location_address, location_time;
    private RatingBar mCreditRatingbar;
    private ImageView mPhoto;
    private TextView mOnlineTime, mOnlineTimeRank, mRecommonedCount, mRecommonedCountRank, mClinch, mClinchRank;
    private View delete, free_location, phone_location;
    private CityDBUtils dbUtils;
    private String phoneNumber = "";
    private CarInfo carInfo;
    private View edit_action_layout, location_action_desc, location_action_layout;//我的车队详情特有
    private View remark_layout;//搜索货源详情特有
    private TextView remark, price;
    private boolean isSearchFromMap = false;
    private PopupWindow popWindow;// popupwindow

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_car_detail_layout);
        initViews();
        initData();
    }

    private void initViews() {
        HeaderView headerView = (HeaderView) findViewById(R.id.headerView);
        headerView.getTipViewVisible().setOnClickListener(this);
        headerView.setTitle("车辆详情");
        popWindow = headerView.createPopupWindow(this, initPopupData());

        driver_name = (TextView) findViewById(R.id.driver_name);
        carState = (TextView) findViewById(R.id.car_info_state);
        way1 = (TextView) findViewById(R.id.way1);
        way2 = (TextView) findViewById(R.id.way2);
        way3 = (TextView) findViewById(R.id.way3);
        plate_number = (TextView) findViewById(R.id.plate_number);
        car_weight = (TextView) findViewById(R.id.car_weight);
        car_length = (TextView) findViewById(R.id.car_length);
        car_type = (TextView) findViewById(R.id.car_type);
        location_address = (TextView) findViewById(R.id.location_address);
        location_time = (TextView) findViewById(R.id.location_time);
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(this);
        free_location = findViewById(R.id.free_location);
        free_location.setOnClickListener(this);
        phone_location = findViewById(R.id.phone_location);
        phone_location.setOnClickListener(this);

        edit_action_layout = findViewById(R.id.edit_action_layout);
        location_action_desc = findViewById(R.id.location_action_desc);
        location_action_layout = findViewById(R.id.location_action_layout);
        remark_layout = findViewById(R.id.remark_layout);
        remark = (TextView) findViewById(R.id.remark);
        price = (TextView) findViewById(R.id.price);
        mPhoto = (ImageView) findViewById(R.id.account_photo);
        // 信誉记录
        mCreditRatingbar = (RatingBar) findViewById(R.id.myabc_id_ratingbar);
        mCreditRatingbar.setIsIndicator(true);
        // 在线天数
        mOnlineTime = (TextView) findViewById(R.id.myabc_id_onlinetime);
        mOnlineTimeRank = (TextView) findViewById(R.id.myabc_id_onlinetime_rank);
        // 成交单数
        mClinch = (TextView) findViewById(R.id.myabc_id_clinch);
        mClinchRank = (TextView) findViewById(R.id.myabc_id_clinch_rank);
        // 推荐人数
        mRecommonedCount = (TextView) findViewById(R.id.myabc_id_recommendcount);
        mRecommonedCountRank = (TextView) findViewById(R.id.myabc_id_recommendcount_rank);
    }

    private void initData() {
        id = getIntent().getIntExtra(Constants.COMMON_KEY, 0);
        Serializable serializable = getIntent().getSerializableExtra(Constants.COMMON_OBJECT_KEY);
        dbUtils = new CityDBUtils(application.getCitySDB());
        if (serializable != null) {
            //搜索车源详情, 直接显示
            carInfo = (CarInfo) serializable;
            remark.setText(carInfo.getRemark());
            //显示其他数据
            displayData(true);
            //隐藏我的车队详情特有的控件
            edit_action_layout.setVisibility(View.GONE);
            location_action_desc.setVisibility(View.GONE);
            location_action_layout.setVisibility(View.GONE);
            //显示搜索车源详情特有的控件
            remark_layout.setVisibility(View.VISIBLE);

            getDriverInfo(carInfo.getDriverInfo());
        } else if (getIntent().getBooleanExtra(Constants.COMMON_BOOLEAN_KEY, false)) {
            //隐藏我的车队详情特有的控件
            edit_action_layout.setVisibility(View.GONE);
            location_action_desc.setVisibility(View.GONE);
            location_action_layout.setVisibility(View.GONE);
            //显示搜索车源详情特有的控件
            remark_layout.setVisibility(View.VISIBLE);
            getData(true);
        } else if (getIntent().getBooleanExtra(Constants.QUERY_CAR_INFO_FROM_MAP, false)) {
            //需要判断是否是直接从我的车队查看的车源,以此判断是否显示编辑按钮
            isSearchFromMap = true;
            getData(true);
        } else {
            getData(false);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.edit:
                /*if (carInfo != null) {
                    Intent intent = new Intent(mContext, AddCarActivity.class);
                    intent.putExtra(Constants.COMMON_KEY, carInfo);
                    intent.putExtra(Constants.CAR_EDIT_TYPE, Constants.EDIT_CAR);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(mContext, "没有车辆数据!", Toast.LENGTH_SHORT).show();
                }*/
                break;
            case R.id.delete:
                deleteData();
                break;
            case R.id.free_location:
                location(true);
                break;
            case R.id.phone_location:
                showPhoneLocationDialogConfirm();
                break;
            case R.id.titlebar_id_more:
                LogUtil.e(TAG, "titlebar_id_more");
                startActivity(new Intent(mContext, ShareActivity.class).putExtra("share", content));
                break;
            case R.id.titlebar_id_tip:
                if (popWindow.isShowing()) {
                    // 关闭
                    popWindow.dismiss();
                } else {
                    // 显示
                    popWindow.showAsDropDown(v);
                }
                break;
        }
    }

    // 请求车辆数据
    private void getData(final boolean isFromSearch) {
        try {
            showSpecialProgress();
            final JSONObject jsonObject = new JSONObject();
            final JSONObject params = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.GET_MY_FLEET_DETAIL);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, params.put("id", id).toString());
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    CarInfo.class, new AjaxCallBack() {
                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result instanceof CarInfo) {
                                        carInfo = (CarInfo) result;
                                        displayData(isFromSearch);
                                        //获取司机信息
                                        getDriverInfo(carInfo.getDriverInfo());
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

    /**
     * 显示车源数据
     */
    private void displayData(boolean isFromSearch) {
        //线路
        String wayStart = dbUtils.getCityInfo(carInfo.getStart_province(), carInfo.getStart_city(), carInfo.getStart_district());
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        StringBuffer sb3 = new StringBuffer();
        StringBuffer sb4 = new StringBuffer();
        if (isFromSearch) {
            if (carInfo.getEnd_province() > 0 || carInfo.getEnd_city() > 0 || carInfo.getEnd_district() > 0) {
                String wayEnd = dbUtils.getCityInfo(carInfo.getEnd_province(), carInfo.getEnd_city(), carInfo.getEnd_district());
                sb1.append(wayStart + "--" + wayEnd);
                way1.setText(sb1.toString());
                way1.setVisibility(View.VISIBLE);
            }
            //隐藏我的车队详情特有的控件
            edit_action_layout.setVisibility(View.GONE);
            //如果是从地图上搜索的,可以显示
            if (!isSearchFromMap) {
                location_action_desc.setVisibility(View.GONE);
                location_action_layout.setVisibility(View.GONE);
            }
            //显示搜索车源详情特有的控件
            remark_layout.setVisibility(View.VISIBLE);
            //报价
            /*if (!TextUtils.isEmpty(carInfo.getPrice())) {
                int priceUnits = carInfo.getUnits();
                String[] unitsArray = mContext.getResources().getStringArray(R.array.car_price_unit);
                for (int i = 0; i < Constants.unitTypeValues.length; i++) {
                    if (Constants.unitTypeValues[i] == priceUnits) {
                        price.setText(price.getText() + carInfo.getPrice() + "元/" + unitsArray[i]);
                    }
                }
            } */
        } else {
            if (carInfo.getEnd_province1() > 0 || carInfo.getEnd_city1() > 0 || carInfo.getEnd_district1() > 0) {
                String wayEnd1 = dbUtils.getCityInfo(carInfo.getEnd_province1(), carInfo.getEnd_city1(), carInfo.getEnd_district1());
                sb2.append(wayStart + "--" + wayEnd1);
                way1.setText(sb2.toString());
                way1.setVisibility(View.VISIBLE);
            } else if (carInfo.getEnd_province() > 0 || carInfo.getEnd_city() > 0 || carInfo.getEnd_district() > 0) {
                String wayEnd = dbUtils.getCityInfo(carInfo.getEnd_province(), carInfo.getEnd_city(), carInfo.getEnd_district());
                sb1.append(wayStart + "--" + wayEnd);
                way1.setText(sb1.toString());
                way1.setVisibility(View.VISIBLE);
            }
            if (carInfo.getEnd_province2() > 0 || carInfo.getEnd_city2() > 0 || carInfo.getEnd_district2() > 0) {
                String wayEnd2 = dbUtils.getCityInfo(carInfo.getEnd_province2(), carInfo.getEnd_city2(), carInfo.getEnd_district2());
                sb3.append(wayStart + "--" + wayEnd2);
                way2.setText(sb3.toString());
                way2.setVisibility(View.VISIBLE);
            }
            if (carInfo.getEnd_province3() > 0 || carInfo.getEnd_city3() > 0 || carInfo.getEnd_district3() > 0) {
                String wayEnd3 = dbUtils.getCityInfo(carInfo.getEnd_province3(), carInfo.getEnd_city3(), carInfo.getEnd_district3());
                sb4.append(wayStart + "--" + wayEnd3);
                way3.setText(sb4.toString());
                way3.setVisibility(View.VISIBLE);
            }
        }
        plate_number.setText(plate_number.getText() + carInfo.getPlate_number());
        if (TextUtils.isEmpty(carInfo.getDriver_name())) {
            driver_name.setText(driver_name.getText() + carInfo.getOwer_name());
        } else {
            driver_name.setText(driver_name.getText() + carInfo.getDriver_name());
        }
        if (TextUtils.isEmpty(carInfo.getPhone())) {
            phoneNumber = carInfo.getOwer_phone();
        } else {
            phoneNumber = carInfo.getPhone();
        }
        car_length.setText(car_length.getText() + carInfo.getCar_length() + "米");
        //车型
        int carTypeValue = carInfo.getCar_type();
        String[] carTypeStr = mContext.getResources().getStringArray(R.array.car_types_name);
        for (int i = 0; i < Constants.carTypeValues.length; i++) {
            if (Constants.carTypeValues[i] == carTypeValue) {
                car_type.setText(car_type.getText().toString() + carTypeStr[i]);
            }
        }
        car_weight.setText(car_weight.getText().toString() + carInfo.getCar_weight() + "吨");
        //位置
        if (!TextUtils.isEmpty(carInfo.getLocation())) {
            location_address.setText(location_address.getText() + carInfo.getLocation());
            location_address.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(carInfo.getAddress())) {
            location_address.setText(location_address.getText() + carInfo.getAddress());
            location_address.setVisibility(View.VISIBLE);
        }
        //显示注册时间
        /*Date date;
        if (isFromSearch) {
            if (carInfo.getLast_position_time() != null && carInfo.getLast_position_time().contains(":")) {
                location_time.setText(location_time.getText() + carInfo.getLast_position_time());
            } else {
                if (!TextUtils.isEmpty(carInfo.getLast_position_time()) && Long.valueOf(carInfo.getLast_position_time()) > 0) {
                    date = new Date(Long.valueOf(carInfo.getLast_position_time()));
                } else {
                    date = new Date(carInfo.getPulish_date());
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String locationTime = simpleDateFormat.format(date);
                location_time.setText(location_time.getText() + locationTime);
            }
        } else {
            location_time.setText(location_time.getText() + carInfo.getLocation_time());
        }*/

        if(carInfo.getStatus() == 0) {
            carState.setText(R.string.car_info_wait_f);
        } else {
            carState.setText(R.string.car_info_doing_f);
        }
        //保证金
        price.setText(price.getText() + (TextUtils.isEmpty(carInfo.getBond()) ?  "0元" : carInfo.getBond() + "元"));
    }

    // 获取司机信息
    private void getDriverInfo(DriverInfo driverInfo) {
        if(driverInfo != null) {
            mOnlineTime.setText(driverInfo.getOnline_time() + "天");
            mOnlineTimeRank.setText(String.format(getString(R.string.string_home_myabc_rank), driverInfo.getOnline_time_rank()));
            mRecommonedCount.setText(driverInfo.getRecommender_count() + "人");
            mRecommonedCountRank.setText(String.format(getString(R.string.string_home_myabc_rank), driverInfo.getRecommender_count_rank()));
            mClinch.setText(driverInfo.getOrder_count() + "单");
            mClinchRank.setText(String.format(getString(R.string.string_home_myabc_rank), driverInfo.getOrder_count_rank()));
            float score = driverInfo.getScore();
            mCreditRatingbar.setRating(score == 0 ? 5 : score);

            ImageLoader.getInstance().displayImage(driverInfo.getId_card_photo(), mPhoto, options,
                    new Utils.MyImageLoadingListener(mContext, mPhoto));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String locationTime = simpleDateFormat.format(new Date(driverInfo.getRegist_time()));
            location_time.setText(location_time.getText() + locationTime);
        }
    }

    // 删除数据
    private void deleteData() {
        try {
            showSpecialProgress();
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.DELETE_MY_FLEET);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject().put("id", id).toString());
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    CarInfo.class, new AjaxCallBack() {
                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    Toast.makeText(mContext, "删除车辆成功", Toast.LENGTH_SHORT).show();
                                    finish();
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

    /**
     * 定位
     *
     * @param isFreeLocation 是否是免费定位
     */
    private void location(final boolean isFreeLocation) {
        try {
            showSpecialProgress();
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ACTION, isFreeLocation ? Constants.FREE_LOCATION : Constants.PHONE_LOCATION);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject().put("mobile", phoneNumber).toString());
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    LocationInfo.class, new AjaxCallBack() {
                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result instanceof LocationInfo) {
                                        LocationInfo info = (LocationInfo) result;
                                        if (!info.isDone()) {
                                            if (info.getBeginTime() != null) {
                                                Toast.makeText(mContext, "没有找到对方地理数据", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else {
                                                Toast.makeText(mContext, "定位成功", Toast.LENGTH_SHORT).show();
                                            }
                                            location_address.setText(info.getAddress());
                                            location_address.setVisibility(View.VISIBLE);
                                            location_time.setText("定位时间:" + info.getTimestamp());
//                                            Intent intent = new Intent(mContext, MapActivity.class);
//                                            intent.putExtra(Constants.COMMON_KEY, info);
//                                            startActivity(intent);
                                        } else {
                                            showMsg("定位超时");
                                        }
                                    }
                                    break;
                                case ResultCode.RESULT_ERROR:
                                    if (result instanceof String)
                                        showMsg(result.toString());
                                    break;
                                case ResultCode.RESULT_FAILED:
                                    if (result instanceof String) {
                                        if (isFreeLocation) {
                                            showFreeLocationDialog();
                                        } else {
                                            showPhoneLocationDialogFailed();
                                        }
                                    }
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

    private void onSendMessage() {
        if (phoneNumber == null || TextUtils.isEmpty(phoneNumber)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
        startActivity(intent);
    }

    private void onCall() {
        if (phoneNumber == null || TextUtils.isEmpty(phoneNumber)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    /**
     * 我的信誉
     *
     * @param view
     */
    public void onMyReputation(View view) {
        if(carInfo.getDriverInfo() != null && carInfo.getDriverInfo().getId() != -1) {
            startActivity(new Intent(mContext, UserCreditActivity.class)
                    .putExtra(Constants.IS_CAR_REPUTATION, true)
                    .putExtra(Constants.COMMON_KEY, carInfo.getDriverInfo()));
        } else {
            showMsg("没有对应的司机信息");
        }
    }

    /**
     * 免费定位未注册提示
     */
    private void showFreeLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("邀请加入");
        builder.setMessage("对方现在还不是易运宝用户,邀请对方注册使用,从此平台随时自动更新司机位置.");
        builder.setPositiveButton("邀请", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                share();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 收费定位未开通提示
     */
    private void showPhoneLocationDialogFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("定位授权");
        builder.setMessage("将发送信息给对方，对方回复Y后，请再次点击手机定位.司机也可以主动编辑短信Y，移动发送至10658012174，联通发送至106550101832224261");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 收费定位确认提示
     */
    private void showPhoneLocationDialogConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("确认信息");
        builder.setMessage("您正在使用收费定位功能,将扣除您0.1个物流币!");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                location(false);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    // 添加到我的车队s
    private void addToMyCar() {
        try {
            if (TextUtils.isEmpty(carInfo.getPlate_number())) {
                Toast.makeText(mContext, "车源的车牌号码为空，不能添加到车队", Toast.LENGTH_SHORT).show();
                return;
            }
            final JSONObject jsonObject = new JSONObject();
            JSONObject params = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.ADD_MY_FLEET_BY_ACCOUNT);
            jsonObject.put(Constants.TOKEN, application.getToken());
            //组装参数
            params.put("account", carInfo.getPlate_number());
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

    /*header更多菜单*/
    private List<PopupMenuInfo> initPopupData() {
        List<PopupMenuInfo> moreList = new ArrayList<PopupMenuInfo>();
        moreList.add(new PopupMenuInfo("分享", R.drawable.header_more_share));
        moreList.add(new PopupMenuInfo("礼品", R.drawable.header_more_present));
        moreList.add(new PopupMenuInfo("车队", R.drawable.header_more_add));
        moreList.add(new PopupMenuInfo("短信", R.drawable.header_more_message));
        moreList.add(new PopupMenuInfo("电话", R.drawable.header_more_call));
        moreList.add(new PopupMenuInfo("推送", R.drawable.header_more_push));
        return moreList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        popWindow.dismiss();
        switch (position) {
            case 0:
                share();
                break;
            case 1:
                startActivity(new Intent(mContext, ShareActivity.class));
                break;
            case 2:
                addToMyCar();
                break;
            case 3:
                onSendMessage();
                break;
            case 4:
                onCall();
                break;
            case 5:
                if(carInfo.getDriverInfo() != null && carInfo.getDriverInfo().getId() != -1) {
                    startActivity(new Intent(mContext, PushToDriverActivity.class).putExtra(Constants.DRIVER_ID, carInfo.getDriverInfo().getId()));
                } else {
                    showMsg("没有对应的司机信息！");
                }
                break;
        }
    }
}
