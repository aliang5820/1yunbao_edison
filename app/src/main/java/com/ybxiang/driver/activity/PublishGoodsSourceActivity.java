package com.ybxiang.driver.activity;

// add this file for 发布货源

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.maogousoft.logisticsmobile.driver.CitySelectView;
import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.activity.MainActivity;
import com.maogousoft.logisticsmobile.driver.activity.info.OptionalActivity;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.model.NewSourceInfo;
import com.maogousoft.logisticsmobile.driver.utils.MyAlertDialog;

import org.json.JSONObject;

import java.io.Serializable;

public class PublishGoodsSourceActivity extends BaseActivity implements OnClickListener {
    private Button mRightButton;
    private CitySelectView cityselectStart, cityselectEnd;
    private TextView mTitleBarContent;
    private int selectedCarWay = Constants.CAR_WAY_WHOLE;// 选择的车辆方式
    private EditText source_id_publish_cargo_desc, source_id_publish_car_length,
            source_id_publish_unit_price, source_id_publish_user_bond, source_id_publish_cargo_remark,
            source_id_publish_contact_name, source_id_publish_contact_phone,
            source_id_publish_validate_hour, source_id_publish_source_weight;
    private Spinner source_id_publish_cargo_type, source_id_publish_car_type,
            source_id_publish_cargo_unit, source_id_publish_cargo_tip, source_id_publish_source_weight_unit;
    private NewSourceInfo mSourceInfo;
    private boolean isUpdateSource = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_goods_source);
        initViews();
        initData();
    }

    // 初始化视图
    private void initViews() {
        mTitleBarContent = (TextView) findViewById(R.id.titlebar_id_content);
        mRightButton = (Button) findViewById(R.id.titlebar_id_more);

        mTitleBarContent.setText("发布货源");
        mRightButton.setText("已发布");
        cityselectStart = (CitySelectView) findViewById(R.id.cityselect_start);
        cityselectEnd = (CitySelectView) findViewById(R.id.cityselect_end);

        source_id_publish_cargo_desc = (EditText) findViewById(R.id.source_id_publish_cargo_desc);
        source_id_publish_car_length = (EditText) findViewById(R.id.source_id_publish_car_length);
        source_id_publish_unit_price = (EditText) findViewById(R.id.source_id_publish_unit_price);
        source_id_publish_user_bond = (EditText) findViewById(R.id.source_id_publish_user_bond);
        source_id_publish_cargo_remark = (EditText) findViewById(R.id.source_id_publish_cargo_remark);
        source_id_publish_contact_name = (EditText) findViewById(R.id.source_id_publish_contact_name);
        source_id_publish_contact_phone = (EditText) findViewById(R.id.source_id_publish_contact_phone);
        source_id_publish_validate_hour = (EditText) findViewById(R.id.source_id_publish_validate_hour);
        source_id_publish_source_weight = (EditText) findViewById(R.id.source_id_publish_source_weight);

        source_id_publish_cargo_type = (Spinner) findViewById(R.id.source_id_publish_cargo_type);
        source_id_publish_car_type = (Spinner) findViewById(R.id.source_id_publish_car_type);
        source_id_publish_cargo_unit = (Spinner) findViewById(R.id.source_id_publish_cargo_unit);
        source_id_publish_cargo_tip = (Spinner) findViewById(R.id.source_id_publish_cargo_tip);
        source_id_publish_source_weight_unit = (Spinner) findViewById(R.id.source_id_publish_source_weight_unit);
        mRightButton.setOnClickListener(this);
    }

    private void initData() {
        Serializable serializable = getIntent().getSerializableExtra(Constants.COMMON_KEY);
        if (serializable != null) {
            mSourceInfo = (NewSourceInfo) serializable;
            isUpdateSource = true;//标记是编辑货源

            source_id_publish_cargo_desc.setText(mSourceInfo.getCargo_desc() + "");
            source_id_publish_car_length.setText(mSourceInfo.getCar_length() + "");
            source_id_publish_unit_price.setText(mSourceInfo.getUnit_price() + "");
            source_id_publish_user_bond.setText(mSourceInfo.getUser_bond() + "");
            source_id_publish_cargo_remark.setText(mSourceInfo.getCargo_remark() + "");
            source_id_publish_contact_name.setText(mSourceInfo.getUser_name() + "");
            source_id_publish_contact_phone.setText(mSourceInfo.getUser_phone() + "");
            source_id_publish_source_weight.setText(mSourceInfo.getCargo_number() + "");
            //选择货物类型
            Integer sourceType = mSourceInfo.getCargo_type();
            if (sourceType != null && sourceType > 0) {
                for (int i = 0; i < Constants.sourceTypeValues.length; i++) {
                    if (Constants.sourceTypeValues[i] == sourceType) {
                        source_id_publish_cargo_type.setSelection(i);
                    }
                }
            }
            //选择车型
            Integer carType = mSourceInfo.getCar_type();
            if (carType != null && carType > 0) {
                for (int i = 0; i < Constants.carTypeValues.length; i++) {
                    if (Constants.carTypeValues[i] == carType) {
                        source_id_publish_cargo_type.setSelection(i);
                    }
                }
            }
            //选择货物单位
            Integer unit = mSourceInfo.getCargo_unit();
            if (unit != null && unit > 0) {
                for (int i = 0; i < Constants.unitTypeValues.length; i++) {
                    if (Constants.unitTypeValues[i] == unit) {
                        source_id_publish_source_weight_unit.setSelection(i);
                    }
                }
            }
            if (null != mSourceInfo.getShip_type() && mSourceInfo.getShip_type() > 0) {
                if (mSourceInfo.getShip_type() == 58) {
                    ((RadioButton) findViewById(R.id.car_way_part)).setChecked(true);
                }
            }
        }
        source_id_publish_contact_phone.setText(application.getUserName());
        source_id_publish_contact_name.setText(application.getDriverName());
    }

    /**
     * 管理发布过的货源
     */
    @Override
    public void onClick(View v) {
        ((BaseActivity) mContext).setIsRightKeyIntoShare(false);
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titlebar_id_more:
                Intent intent = new Intent();
                intent.setClass(mContext, MySourceActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 运输方式：零担，整车，二选其一
     *
     * @param view
     */
    public void onChooseCarWay(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (!checked) {
            showMsg("必须选择一种运输方式【零担，整车】");
            return;
        }
        switch (view.getId()) {
            case R.id.car_way_part:
                // 零担
                selectedCarWay = Constants.CAR_WAY_PART;
                break;
            case R.id.car_way_whole:
                selectedCarWay = Constants.CAR_WAY_WHOLE;
                // 整车
                break;
        }
    }

    /**
     * 发布货源
     *
     * @param view
     */
    public void onPublishSource(View view) {
        if (cityselectStart.getSelectedProvince() == null || cityselectEnd.getSelectedProvince() == null) {
            showMsg("请选择出发地，目的地。");
            return;
        }
        if (TextUtils.isEmpty(source_id_publish_contact_phone.getText())) {
            showMsg("联系人手机号码是必填项,请重新填写");
            return;
        }
        if (source_id_publish_validate_hour.length() == 0) {
            showMsg("请输入货源有效时间！");
            return;
        }
        if (Integer.parseInt(source_id_publish_validate_hour.getText().toString()) > 72) {
            showMsg("货源有效时间不能超过72小时！");
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        final JSONObject params = new JSONObject();
        try {
//            jsonObject.put(Constants.ACTION, isUpdateSource?Constants.UPDATE_PUBLISH_ORDER:Constants.PUBLISH_SOURCE);
            jsonObject.put(Constants.ACTION, Constants.PUBLISH_SOURCE);
            jsonObject.put(Constants.TOKEN, application.getToken());
            params.put("start_province", cityselectStart.getSelectedProvince()
                    .getId());
            params.put("end_province", cityselectEnd.getSelectedProvince()
                    .getId());

            if (cityselectStart.getSelectedCity() != null) {
                params.put("start_city", cityselectStart.getSelectedCity()
                        .getId());
            }

            if (cityselectEnd.getSelectedCity() != null) {
                params.put("end_city", cityselectEnd.getSelectedCity().getId());
            }

            if (cityselectStart.getSelectedTowns() != null) {
                params.put("start_district", cityselectStart.getSelectedTowns()
                        .getId());
            }

            if (cityselectEnd.getSelectedTowns() != null) {
                params.put("end_district", cityselectEnd.getSelectedTowns()
                        .getId());
            }

            if (!TextUtils.isEmpty(source_id_publish_cargo_desc.getText())) {
                params.put("cargo_desc", source_id_publish_cargo_desc.getText().toString());
            }

            params.put("cargo_type", Constants.getSourceTypeValues(source_id_publish_cargo_type.getSelectedItemPosition()));
            params.put("car_way", selectedCarWay);  // add PR1.3
            params.put("car_length", source_id_publish_car_length.getText());
            params.put("car_type", Constants.getCarTypeValues(source_id_publish_car_type.getSelectedItemPosition()));
            params.put("unit_price", source_id_publish_unit_price.getText());
            params.put("user_bond", source_id_publish_user_bond.getText());
            params.put("cargo_tip", ((TextView) source_id_publish_cargo_tip.getSelectedView()).getText());
            params.put("cargo_remark", source_id_publish_cargo_remark.getText());
            params.put("contact_name", source_id_publish_contact_name.getText());
            params.put("contact_phone", source_id_publish_contact_phone.getText());
            params.put("validate_hour", source_id_publish_validate_hour.getText());
            //货物数量和单位
            params.put("cargo_number", source_id_publish_source_weight.getText());
            params.put("cargo_unit", Constants.getUnitTypeValues(source_id_publish_source_weight_unit.getSelectedItemPosition()));
            if(null != mSourceInfo) {
                params.put("id", mSourceInfo.getId());
            }
            long hour = Integer.valueOf(source_id_publish_validate_hour.getText().toString()) * 60 * 60 * 1000;
            params.put("validate_time", hour);
            params.put("loading_time", 60 * 60 * 1000);

            jsonObject.put(Constants.JSON, params);
            showSpecialProgress();
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
                                    dialog.setMessage("货源发布成功");
                                    dialog.setLeftButton("确定",
                                            new OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent();
                                                    intent.setClass(mContext, MySourceActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                    break;
                                case ResultCode.RESULT_FAILED:
                                    if(result instanceof String) {
                                        Toast.makeText(mContext, result.toString(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "发布货源失败", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        application.finishAllActivity();
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
    }
}
