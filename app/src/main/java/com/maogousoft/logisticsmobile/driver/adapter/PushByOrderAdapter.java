package com.maogousoft.logisticsmobile.driver.adapter;


import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.home.NewSourceActivity;
import com.maogousoft.logisticsmobile.driver.activity.home.SourceDetailActivity;
import com.maogousoft.logisticsmobile.driver.db.CityDBUtils;
import com.maogousoft.logisticsmobile.driver.model.CarInfo;
import com.maogousoft.logisticsmobile.driver.model.NewSourceInfo;
import com.maogousoft.logisticsmobile.driver.utils.CheckUtils;
import com.maogousoft.logisticsmobile.driver.widget.InvoiceCarrierView;
import com.ybxiang.driver.activity.MyCarsDetailActivity;

/**
 * 我的货源的adapter
 */
public class PushByOrderAdapter extends BaseListAdapter<NewSourceInfo> implements View.OnClickListener {

    private Context mContext;
    private CityDBUtils dbUtils;
    private SparseArray<NewSourceInfo> sparseArray = new SparseArray<NewSourceInfo>();

    public PushByOrderAdapter(Context context) {
        super(context);
        mContext = context;
        dbUtils = new CityDBUtils(application.getCitySDB());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_myinvoice_push_order, parent, false);
            holder = new ViewHolder();
            holder.order_info = (TextView) convertView.findViewById(R.id.source_id_order_info);
            holder.order_info_detail = (TextView) convertView.findViewById(R.id.source_id_order_info_detail);
            holder.order_money = (TextView) convertView.findViewById(R.id.source_id_order_money);
            holder.order_id = (TextView) convertView.findViewById(R.id.source_id);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final NewSourceInfo sourceInfo = mList.get(position);
        final StringBuilder title = new StringBuilder();
        final StringBuilder detail = new StringBuilder();

        String wayStart = dbUtils.getCityInfo(sourceInfo.getStart_province(), sourceInfo.getStart_city(), sourceInfo.getStart_district());
        if (sourceInfo.getEnd_province() > 0 || sourceInfo.getEnd_city() > 0 || sourceInfo.getEnd_district() > 0) {
            String wayEnd = dbUtils.getCityInfo(sourceInfo.getEnd_province(), sourceInfo.getEnd_city(), sourceInfo.getEnd_district());
            title.append(wayStart).append("--").append(wayEnd);
        }

        detail.append(sourceInfo.getCargo_type_str());

        if (sourceInfo.getCargo_number() != null && sourceInfo.getCargo_number() != 0) {
            detail.append("/")
                    .append(sourceInfo.getCargo_desc())
                    .append(sourceInfo.getCargo_number())
                    .append(CheckUtils.getCargoUnitName(mContext, sourceInfo.getCargo_unit()));
        }

        if (!TextUtils.isEmpty(sourceInfo.getCar_type_str())) {
            detail.append("/");
            detail.append(sourceInfo.getCar_type_str());

        }

        if (sourceInfo.getCar_length() != null && sourceInfo.getCar_length() != 0
                && sourceInfo.getCar_length() != 0.0) {
            detail.append("/").append(sourceInfo.getCar_length()).append("米");
        }
        holder.order_id.setText("" + sourceInfo.getId());
        holder.order_info.setText(title.toString());
        holder.order_info_detail.setText(detail.toString());
        holder.order_money.setText(Html.fromHtml(String.format(mResources.getString(R.string.string_home_newsource_order_money), sourceInfo.getUser_bond())));

        //是否被选中
        if (sparseArray.get(sourceInfo.getId()) != null) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    sparseArray.put(sourceInfo.getId(), sourceInfo);
                } else {
                    sparseArray.remove(sourceInfo.getId());
                }
            }
        });
        convertView.setOnClickListener(this);
        convertView.setTag(R.id.common_key, sourceInfo);
        return convertView;
    }

    public SparseArray<NewSourceInfo> getSelectedOrder() {
        return sparseArray;
    }

    class ViewHolder {
        TextView order_info, order_info_detail, order_money, order_id;
        CheckBox checkBox;
    }

    @Override
    public void onClick(View view) {
        NewSourceInfo newSourceInfo = (NewSourceInfo) view.getTag(R.id.common_key);
        Intent intent = new Intent(mContext, SourceDetailActivity.class);
        intent.putExtra(Constants.ORDER_ID, newSourceInfo.getId());
        intent.putExtra("type", "NewSourceActivity");
        mContext.startActivity(intent);
    }
}
