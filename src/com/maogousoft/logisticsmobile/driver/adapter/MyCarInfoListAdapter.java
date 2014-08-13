package com.maogousoft.logisticsmobile.driver.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.model.CarInfo;
import com.ybxiang.driver.activity.CarInfoDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 我的车队的adapter 1：姓名，车牌号，车型，车长 2：路线，位置，定位时间
 * 
 * @author ybxiang
 * 
 */
public class MyCarInfoListAdapter extends BaseListAdapter<CarInfo> {

	public MyCarInfoListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        CarInfo carInfo = mList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_mycarinfo, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.nameId)).setText(carInfo.getOwer_name());
        ((TextView) convertView.findViewById(R.id.plate_numberId)).setText(carInfo.getPlate_number());
        ((TextView) convertView.findViewById(R.id.phone)).setText(carInfo.getOwer_phone());
        if(!TextUtils.isEmpty(carInfo.getLocation_time())) {
            Date date = new Date(carInfo.getLocation_time());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm");
            String locationTime = simpleDateFormat.format(date);
            ((TextView) convertView.findViewById(R.id.location_time)).setText(locationTime);
        }
        ((TextView) convertView.findViewById(R.id.locationId)).setText(carInfo.getLocation());
        return convertView;
    }
}
