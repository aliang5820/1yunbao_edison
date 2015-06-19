package com.maogousoft.logisticsmobile.driver.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.activity.info.OptionalActivity;
import com.maogousoft.logisticsmobile.driver.activity.info.OptionalShipperActivity;
import com.maogousoft.logisticsmobile.driver.activity.info.UpdatePwdActivity;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.model.DriverInfo;
import com.maogousoft.logisticsmobile.driver.model.ShipperInfo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ybxiang.driver.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Myabc 我的账户
 *
 * @author ybxiang
 */
public class MyabcAccountInfoActivity extends BaseActivity {
    private View driver_layout, shipper_layout;
    /*司机特有信息*/
    private ImageView account_photo;
    private TextView myabc_id_name, myabc_account_card_number, myabc_account_name_dirver, myabc_id_cjh;
    /*货主特有信息*/
    private ImageView account_logo_photo, account_other_photo;
    private TextView myabc_id_company_name, myabc_account_name_shipper, myabc_id_zhizhao, myabc_id_contact, myabc_id_daibiao;
    //common
    private TextView myabc_id_contact_phone, myabc_id_contact_home_phone, myabc_id_bank_name, myabc_id_bank_number, myabc_id_address;
    // 个人abc信息
    private DriverInfo mDriverInfo;
    private ShipperInfo mShipperInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myabc_account);
        initViews();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initViews() {
        ((TextView) findViewById(R.id.titlebar_id_content)).setText("我的账户");
        driver_layout = findViewById(R.id.driver_layout);
        shipper_layout = findViewById(R.id.shipper_layout);
        account_photo = (ImageView) findViewById(R.id.account_photo);
        account_logo_photo = (ImageView) findViewById(R.id.account_logo_photo);
        account_other_photo = (ImageView) findViewById(R.id.account_other_photo);
        myabc_id_name = (TextView) findViewById(R.id.myabc_id_name);
        myabc_id_company_name = (TextView) findViewById(R.id.myabc_id_company_name);
        myabc_account_card_number = (TextView) findViewById(R.id.myabc_account_card_number);
        myabc_account_name_dirver = (TextView) findViewById(R.id.myabc_account_name_dirver);
        myabc_account_name_shipper = (TextView) findViewById(R.id.myabc_account_name_shipper);
        myabc_id_contact = (TextView) findViewById(R.id.myabc_id_contact);
        myabc_id_zhizhao = (TextView) findViewById(R.id.myabc_id_zhizhao);
        myabc_id_daibiao = (TextView) findViewById(R.id.myabc_id_daibiao);
        myabc_id_cjh = (TextView) findViewById(R.id.myabc_id_cjh);
        myabc_id_contact_phone = (TextView) findViewById(R.id.myabc_id_contact_phone);
        myabc_id_contact_home_phone = (TextView) findViewById(R.id.myabc_id_contact_home_phone);
        myabc_id_bank_name = (TextView) findViewById(R.id.myabc_id_bank_name);
        myabc_id_bank_number = (TextView) findViewById(R.id.myabc_id_bank_number);
        myabc_id_address = (TextView) findViewById(R.id.myabc_id_address);
    }

    private void initData() {
        if (Constants.USER_DRIVER == application.getUserType()) {
            getDriverInfo();
        } else {
            getShipperInfo();
            driver_layout.setVisibility(View.GONE);
            myabc_id_cjh.setVisibility(View.GONE);
            shipper_layout.setVisibility(View.VISIBLE);
            myabc_id_contact.setVisibility(View.VISIBLE);
        }
    }

    // 获取司机信息
    private void getDriverInfo() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.ACTION, Constants.GET_DRIVER_INFO);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject().put("user_id", application.getUserId()));
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    DriverInfo.class, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result != null) {
                                        mDriverInfo = (DriverInfo) result;
                                        myabc_id_name.setText(mDriverInfo.getName());
                                        myabc_account_card_number.setText(mDriverInfo.getId_card());
                                        myabc_account_name_dirver.setText(mDriverInfo.getPhone());
                                        myabc_id_cjh.setText(getString(R.string.chejiahao, mDriverInfo.getFrame_number()));
                                        myabc_id_contact_phone.setText(getString(R.string.lianxidianhua, mDriverInfo.getPhone()));
                                        myabc_id_contact_home_phone.setText(getString(R.string.jiatingdianhua, mDriverInfo.getPhone()));
                                        myabc_id_bank_name.setText(getString(R.string.yinhangmingcheng, mDriverInfo.getBank()));
                                        myabc_id_bank_number.setText(getString(R.string.shoukuankahao, mDriverInfo.getBank_account()));
                                        myabc_id_address.setText(getString(R.string.address, mDriverInfo.getBusiness_address()));
                                        ImageLoader.getInstance().displayImage(mDriverInfo.getId_card_photo(), account_photo, options,
                                                new Utils.MyImageLoadingListener(mContext, account_photo));
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

    // 获取货主信息
    private void getShipperInfo() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.ACTION, Constants.GET_USER_INFO);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject().put("user_id", application.getUserId()));
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    ShipperInfo.class, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result != null) {
                                        mShipperInfo = (ShipperInfo) result;
                                        myabc_id_company_name.setText(mShipperInfo.getCompany_name());
                                        myabc_account_name_shipper.setText(mShipperInfo.getPhone());
                                        myabc_id_zhizhao.setText(mShipperInfo.getName());
                                        myabc_id_daibiao.setText(mShipperInfo.getName());
                                        myabc_id_contact.setText(getString(R.string.lianxiren, mShipperInfo.getName()));
                                        myabc_id_contact_phone.setText(getString(R.string.lianxidianhua, mShipperInfo.getPhone()));
                                        myabc_id_contact_home_phone.setText(getString(R.string.jiatingdianhua, mShipperInfo.getTelcom()));
                                        myabc_id_bank_name.setText(getString(R.string.yinhangmingcheng, mShipperInfo.getBank()));
                                        myabc_id_bank_number.setText(getString(R.string.shoukuankahao, mShipperInfo.getBank_account()));
                                        myabc_id_address.setText(getString(R.string.address, mShipperInfo.getAddress()));
                                        ImageLoader.getInstance().displayImage(mShipperInfo.getCompany_logo(), account_logo_photo, options,
                                                new Utils.MyImageLoadingListener(mContext, account_logo_photo));
                                        ImageLoader.getInstance().displayImage(mShipperInfo.getCompany_photo1(), account_other_photo, options,
                                                new Utils.MyImageLoadingListener(mContext, account_other_photo));
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

    /*@Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.myabc_id_updatepwd:
                onChangePasswd(v);
                break;
        }
    }*/

    public void onEdit(View view) {
        if(Constants.USER_DRIVER == application.getUserType()) {
            startActivity(new Intent(mContext, OptionalActivity.class).putExtra("info", mDriverInfo));
        } else {
            startActivity(new Intent(mContext, OptionalShipperActivity.class).putExtra("info", mShipperInfo));
        }
    }

    /**
     * 修改密码
     *
     * @param view
     */
    public void onChangePasswd(View view) {
        startActivity(new Intent(mContext, UpdatePwdActivity.class));
    }
}
