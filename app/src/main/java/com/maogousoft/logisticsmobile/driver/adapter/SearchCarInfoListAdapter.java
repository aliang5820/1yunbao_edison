package com.maogousoft.logisticsmobile.driver.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.info.PushToDriverActivity;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.db.CityDBUtils;
import com.maogousoft.logisticsmobile.driver.model.CarInfo;
import com.ybxiang.driver.activity.MyCarsDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aliang on 2014/8/30.
 */
public class SearchCarInfoListAdapter extends BaseListAdapter<CarInfo> implements View.OnClickListener {

    private Context mContext;
    private CityDBUtils dbUtils;
    public SearchCarInfoListAdapter(Context context) {
        super(context);
        mContext = context;
        dbUtils = new CityDBUtils(application.getCitySDB());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listitem_mycarinfo, parent, false);
            viewHolder.car_info_line = (TextView) convertView.findViewById(R.id.car_info_line);
            viewHolder.car_info_desc = (TextView) convertView.findViewById(R.id.car_info_desc);
            viewHolder.car_info_state = (TextView) convertView.findViewById(R.id.car_info_state);
            viewHolder.car_info_money = (TextView) convertView.findViewById(R.id.car_info_money);
            viewHolder.car_info_phone = convertView.findViewById(R.id.car_info_phone);
            viewHolder.car_info_notify = convertView.findViewById(R.id.car_info_notify);
            viewHolder.car_info_add = convertView.findViewById(R.id.car_info_add);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CarInfo carInfo = mList.get(position);
        final StringBuilder line = new StringBuilder();
        final StringBuilder detail = new StringBuilder();

        String wayStart = dbUtils.getCityInfo(carInfo.getStart_province(), carInfo.getStart_city(), carInfo.getStart_district());
        if (carInfo.getEnd_province() > 0 || carInfo.getEnd_city() > 0 || carInfo.getEnd_district() > 0) {
            String wayEnd = dbUtils.getCityInfo(carInfo.getEnd_province(), carInfo.getEnd_city(), carInfo.getEnd_district());
            line.append(wayStart + "--" + wayEnd);
        } else {
            if (carInfo.getEnd_province1() > 0 || carInfo.getEnd_city1() > 0 || carInfo.getEnd_district1() > 0) {
                String wayEnd1 = dbUtils.getCityInfo(carInfo.getEnd_province1(), carInfo.getEnd_city1(), carInfo.getEnd_district1());
                line.append(wayStart + "--" + wayEnd1);
            }
            if (carInfo.getEnd_province2() > 0 || carInfo.getEnd_city2() > 0 || carInfo.getEnd_district2() > 0) {
                String wayEnd2 = dbUtils.getCityInfo(carInfo.getEnd_province2(), carInfo.getEnd_city2(), carInfo.getEnd_district2());
                line.append("\n" + wayStart + "--" + wayEnd2);
            }
            if (carInfo.getEnd_province3() > 0 || carInfo.getEnd_city3() > 0 || carInfo.getEnd_district3() > 0) {
                String wayEnd3 = dbUtils.getCityInfo(carInfo.getEnd_province3(), carInfo.getEnd_city3(), carInfo.getEnd_district3());
                line.append("\n" + wayStart + "--" + wayEnd3);
            }
        }

        //车型
        int carTypeValue = carInfo.getCar_type();
        String[] carTypeStr = mContext.getResources().getStringArray(R.array.car_types_name);
        for (int i = 0; i < Constants.carTypeValues.length; i++) {
            if (Constants.carTypeValues[i] == carTypeValue) {
                detail.append(carTypeStr[i]);
            }
        }

        if(carInfo.getCar_weight() > 0) {
            detail.append("/" + carInfo.getCar_weight() + "吨");
        }

        if (carInfo.getCar_length() != null && !TextUtils.isEmpty(carInfo.getCar_length())) {
            detail.append("/" + carInfo.getCar_length() + "米");
        }

        viewHolder.car_info_line.setText(line.toString());
        viewHolder.car_info_desc.setText(detail.toString());
        viewHolder.car_info_money.setText((TextUtils.isEmpty(carInfo.getBond()) ? 0 : carInfo.getBond()) + "元");
        if(carInfo.getStatus() == 0) {
            viewHolder.car_info_state.setText(R.string.car_info_wait);
        } else {
            viewHolder.car_info_state.setText(R.string.car_info_doing);
        }
        //电话号码
        viewHolder.car_info_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneStr = carInfo.getPhone();
                if(TextUtils.isEmpty(phoneStr)) {
                    phoneStr = carInfo.getOwer_phone();
                }
                if (!TextUtils.isEmpty(phoneStr)) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneStr));
                    mContext.startActivity(intent);
                }
            }
        });
        //推送
        viewHolder.car_info_notify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                notifySource(carInfo);
            }
        });
        // 添加车队
        viewHolder.car_info_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                addCarToMyFleet(carInfo);
            }
        });

        convertView.setOnClickListener(this);
        convertView.setTag(R.id.common_key, carInfo);
        return convertView;
    }

    class ViewHolder {
        TextView car_info_line, car_info_desc, car_info_state, car_info_money;
        View car_info_phone, car_info_notify, car_info_add;
    }

    @Override
    public void onClick(View view) {
            CarInfo carInfo = (CarInfo) view.getTag(R.id.common_key);
            Intent intent = new Intent(mContext, MyCarsDetailActivity.class);
            intent.putExtra(Constants.COMMON_KEY, carInfo.getId());
            intent.putExtra(Constants.COMMON_OBJECT_KEY, carInfo);
            mContext.startActivity(intent);
    }

    //给司机推送货源
    public void notifySource(CarInfo carInfo) {
        if(carInfo.getDriverInfo() != null && carInfo.getDriverInfo().getId() != -1) {
            mContext.startActivity(new Intent(mContext, PushToDriverActivity.class).putExtra(Constants.DRIVER_ID, carInfo.getDriverInfo().getId()));
        } else {
            showMsg("没有对应的司机信息！");
        }
    }

    // 添加到我的车队
    public void addCarToMyFleet(CarInfo carInfo) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.ADD_MY_FLEET);
            jsonObject.put(Constants.TOKEN, application.getToken());
            //组装参数
            JSONObject params = new JSONObject();
            params.put("driver_name", carInfo.getOwer_name());
            params.put("phone", carInfo.getOwer_phone());
            params.put("start_province", carInfo.getStart_province());
            params.put("start_city", carInfo.getStart_city());
            params.put("start_district", carInfo.getStart_district());
            params.put("end_province1", carInfo.getEnd_province());
            params.put("end_city1", carInfo.getEnd_city());
            params.put("end_district1", carInfo.getEnd_district());
            params.put("car_length", carInfo.getCar_length());
            params.put("car_weight", carInfo.getCar_weight());
            params.put("plate_number", carInfo.getPlate_number());
            params.put("car_type", carInfo.getCar_type());
            params.put("remark", carInfo.getRemark());
            if(TextUtils.isEmpty(carInfo.getLast_position_time())) {
                params.put("location_time", carInfo.getPulish_date());
            } else {
                params.put("location_time", carInfo.getLast_position_time());
            }
            if(TextUtils.isEmpty(carInfo.getLocation())) {
                params.put("location", carInfo.getAddress());
            } else {
                params.put("location", carInfo.getLocation());
            }
            //组装参数结束
            jsonObject.put(Constants.JSON, params.toString());
            showProgress("正在请求,请稍后");
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    CarInfo.class, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    Toast.makeText(mContext, "成功添加到我的车队!", Toast.LENGTH_SHORT).show();
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
