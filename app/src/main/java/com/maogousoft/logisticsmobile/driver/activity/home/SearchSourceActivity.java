package com.maogousoft.logisticsmobile.driver.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import com.maogousoft.logisticsmobile.driver.CitySelectView;
import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.activity.MainActivity;
import com.maogousoft.logisticsmobile.driver.activity.info.OptionalActivity;
import com.maogousoft.logisticsmobile.driver.activity.info.OptionalShipperActivity;
import com.maogousoft.logisticsmobile.driver.activity.share.ShareActivity;
import com.maogousoft.logisticsmobile.driver.adapter.BaseListAdapter;
import com.maogousoft.logisticsmobile.driver.adapter.FocusLineAdapter;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.db.CityDBUtils;
import com.maogousoft.logisticsmobile.driver.model.NewSourceInfo;
import com.maogousoft.logisticsmobile.driver.utils.MyAlertDialog;
import com.ybxiang.driver.activity.FocusLineInfoActivity;
import com.ybxiang.driver.model.FocusLineInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 查找 新货源
 *
 * @author lenovo
 */
public class SearchSourceActivity extends BaseActivity {
    private Button mTitleBarMore, mSubmit;
    private CitySelectView citySelectStart, citySelectEnd;
    private Spinner carTypeSpinner, edtCarLength;
    private GridView mGridView;
    private int car_type = 43; // 车型 ：
    private int ship_type = 0; // 运输方式： 0 不限 1整车 2 零担
    private CityDBUtils dbUtils;
    private BaseListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search_source);
        initViews();
        dbUtils = new CityDBUtils(application.getCitySDB());
        queryAllFocusLine();
    }

    // 初始化视图
    private void initViews() {

        ((TextView) findViewById(R.id.titlebar_id_content)).setText("查找货源");
        mTitleBarMore = (Button) findViewById(R.id.titlebar_id_more);
        mTitleBarMore.setVisibility(View.GONE);
        carTypeSpinner = (Spinner) findViewById(R.id.search_car_type);
        edtCarLength = (Spinner) findViewById(R.id.edt_search_source_carlength);

        mSubmit = (Button) findViewById(R.id.search_source__submit);

        citySelectStart = (CitySelectView) findViewById(R.id.cityselect_start);
        citySelectEnd = (CitySelectView) findViewById(R.id.cityselect_end);
        mGridView = (GridView) findViewById(R.id.focus_line_gridview);
        mTitleBarMore.setOnClickListener(this);
        mSubmit.setOnClickListener(this);
        // 获取车型
        carTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                car_type = Constants.getCarTypeValues(arg2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        mAdapter = new FocusLineAdapter(mContext);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FocusLineInfo focusLineInfo = (FocusLineInfo) view.getTag(R.id.focus_line_key);
                fastSearchSource(focusLineInfo);
            }
        });
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        setIsRightKeyIntoShare(false);// 设置分享按钮失效
        super.onClick(v);
        final int id = v.getId();
        switch (id) {
            // 长期货源
            case R.id.titlebar_id_more:
                // 参数1代表长期货源
                search(1);
                break;
            case R.id.search_source__submit:
                // 参数0代表短期货源
                search(0);
                break;

            default:
                break;
        }
    }

    private void search(int validateDateLong) {
        submit(validateDateLong);
        /*// 先检测是否已经完善了资料
        if (application.getUserType() == Constants.USER_SHIPPER || application.checkIsRegOptional()) {
            // 参数0代表短期货源
        } else {
            final MyAlertDialog dialog = new MyAlertDialog(mContext, R.style.DialogTheme);
            dialog.show();
            dialog.setTitle("提示");
            dialog.setMessage("请完善信息，否则无法提供适合你车型、线路的货源。");
            dialog.setLeftButton("完善资料", new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent;
                    if(application.getUserType() == Constants.USER_DRIVER) {
                        intent = new Intent(mContext, OptionalActivity.class);
                    } else {
                        intent = new Intent(mContext, OptionalShipperActivity.class);
                    }
                    intent.putExtra("isFormRegisterActivity", false);
                    startActivity(intent);
                    finish();
                }
            });
            dialog.setRightButton("取消", new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void submit(int validateDateLong) {
        // 计算路线的开始ID,结束ID
        final FocusLineInfo focusLineInfo = new FocusLineInfo();
        if (citySelectStart.getSelectedProvince() == null
                | citySelectEnd.getSelectedProvince() == null) {
            showMsg("请选择出发地，目的地。");
            return;
        }

        final JSONObject jsonObject = new JSONObject();
        final JSONObject params = new JSONObject();
        try {
            // 搜索新货源
            jsonObject.put(Constants.ACTION, Constants.QUERY_SOURCE_ORDER);
            jsonObject.put(Constants.TOKEN, application.getToken());
            // 添加长期货源
            if (validateDateLong == 1) {
                params.put("validateDateLong", 1);
            }

            params.put("start_province", citySelectStart.getSelectedProvince().getId());
            focusLineInfo.setStart_province(citySelectStart.getSelectedProvince().getId());
            params.put("end_province", citySelectEnd.getSelectedProvince().getId());
            focusLineInfo.setEnd_province(citySelectEnd.getSelectedProvince().getId());
            if (citySelectStart.getSelectedCity() != null) {
                params.put("start_city", citySelectStart.getSelectedCity().getId());
                focusLineInfo.setStart_city(citySelectStart.getSelectedCity().getId());
            }

            if (citySelectEnd.getSelectedCity() != null) {
                params.put("end_city", citySelectEnd.getSelectedCity().getId());
                focusLineInfo.setEnd_city(citySelectEnd.getSelectedCity().getId());
            }

            if (citySelectStart.getSelectedTowns() != null) {
                params.put("start_district", citySelectStart.getSelectedTowns().getId());
                focusLineInfo.setStart_district(citySelectStart.getSelectedTowns().getId());
            }

            if (citySelectEnd.getSelectedTowns() != null) {
                params.put("end_district", citySelectEnd.getSelectedTowns().getId());
                focusLineInfo.setEnd_district(citySelectEnd.getSelectedTowns().getId());
            }
            // 车型
            if (car_type != 43) {
                params.put("car_type", car_type);
            }
            // add 车长范围
            if (edtCarLength.getSelectedItemPosition() != 0) {
                params.put("car_length", edtCarLength.getSelectedItemPosition());
            }
            params.put("ship_type", ship_type); // add 运输方式
            params.put("device_type", Constants.DEVICE_TYPE);
            jsonObject.put(Constants.JSON, params);
            showSpecialProgress();
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    NewSourceInfo.class, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result instanceof List) {
                                        List<NewSourceInfo> mList = (ArrayList<NewSourceInfo>) result;

                                        if (mList.size() != 0) {
                                            Intent intent = new Intent(mContext, NewSourceActivity.class);
                                            intent.putExtra(Constants.COMMON_KEY, params.toString());
                                            intent.putExtra(Constants.COMMON_OBJECT_KEY, (Serializable) mList);
                                            intent.putExtra(Constants.SEARCH_SOURCE, true);
                                            intent.putExtra("focusLineInfo", focusLineInfo);
                                            mContext.startActivity(intent);
                                        } else {
                                            showMsg("暂无满足条件的信息，请扩大搜索范围再试。");
                                        }
                                    }
                                    break;
                                case ResultCode.RESULT_ERROR:
                                    if (result != null)
                                        showMsg(result.toString());

                                    break;
                                case ResultCode.RESULT_FAILED:
                                        // 您当月的免费搜索次数已经用完
                                        // if (result.equals("您当月的免费搜索次数已经用完")) {
                                        final MyAlertDialog dialog = new MyAlertDialog(mContext, R.style.DialogTheme);
                                        dialog.show();
                                        dialog.setTitle("提示");
                                        // 您本月的搜索次数已达到10次，你须要向朋友分享易运宝才能继续使用搜索功能！
                                        dialog.setMessage(result.toString());
                                        dialog.setLeftButton("确定",
                                                new OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                        startActivity(new Intent(mContext,ShareActivity.class));
                                                        finish();
                                                    }
                                                });
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
     * 运输方式
     *
     * @param view
     */
    public void onChooseCarWay(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (!checked) {
            showMsg("必须选择一种运输方式【零担，整车，不限】");
            return;
        }
        switch (view.getId()) {
            case R.id.car_way_part:
                // 零担
                ship_type = 2;
                break;
            case R.id.car_way_whole:
                ship_type = 1;
                // 整车
                break;
            case R.id.car_way_all:
                ship_type = 0;
                // 不限
                break;
        }
    }

    // 搜索全部已关注线路
    private void queryAllFocusLine() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.ACTION, Constants.QUERY_ALL_FOCUS_LINE);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject().put("type", application.getUserType()));
            showDefaultProgress();
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    FocusLineInfo.class, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result instanceof List) {
                                        final List<FocusLineInfo> focusLineInfoList = (List<FocusLineInfo>) result;
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //解析数据
                                                for (FocusLineInfo focusLineInfo : focusLineInfoList) {
                                                    String start = dbUtils.getCityInfo(focusLineInfo.getStart_province());
                                                    String end = dbUtils.getCityInfo(focusLineInfo.getEnd_province());
                                                    if (focusLineInfo.getStart_city() > 0) {
                                                        start = dbUtils.getCityInfo(focusLineInfo.getStart_city());
                                                        if (focusLineInfo.getStart_district() > 0) {
                                                            start = dbUtils.getCityInfo(focusLineInfo.getStart_district());
                                                        }
                                                    }
                                                    if (focusLineInfo.getEnd_city() > 0) {
                                                        end = dbUtils.getCityInfo(focusLineInfo.getEnd_city());
                                                        if (focusLineInfo.getEnd_district() > 0) {
                                                            end = dbUtils.getCityInfo(focusLineInfo.getEnd_district());
                                                        }
                                                    }
                                                    focusLineInfo.setStart_str(start);
                                                    focusLineInfo.setEnd_str(end);
                                                }
                                                //刷新数据
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ViewGroup.LayoutParams lp = mGridView.getLayoutParams();
                                                        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                                        int size = mContext.getResources().getDimensionPixelSize(R.dimen.focus_line_item_height);
                                                        int row = (focusLineInfoList.size() / 2) + (focusLineInfoList.size() % 2);
                                                        lp.height = size * row * 2;
                                                        mAdapter.addAll(focusLineInfoList);
                                                        mAdapter.notifyDataSetChanged();
                                                        mGridView.setLayoutParams(lp);
                                                        mGridView.invalidate();
                                                    }
                                                });
                                            }
                                        }).start();
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
                            }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 已关注路线的快捷设置
     */
    public void onFastSetting(View view) {
        startActivity(new Intent(mContext, FocusLineInfoActivity.class));
    }

    @Override
    public void onBackPressed() {
        application.finishAllActivity();
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
    }
}
