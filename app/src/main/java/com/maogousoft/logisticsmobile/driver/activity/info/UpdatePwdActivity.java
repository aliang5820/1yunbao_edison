package com.maogousoft.logisticsmobile.driver.activity.info;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.utils.CheckUtils;
import com.maogousoft.logisticsmobile.driver.utils.MD5;

/**
 * 修改密码
 * 
 * @author lenovo
 */
public class UpdatePwdActivity extends BaseActivity {

	private EditText mOldPwd, mNewPwd, mNewPwd2;

	private Button mSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_updatepwd);
		initViews();
	}

	// 初始化视图
	private void initViews() {

		((TextView)findViewById(R.id.titlebar_id_content)).setText(R.string.string_update_title);
		mOldPwd = (EditText) findViewById(R.id.info_id_update_oldpwd);
		mNewPwd = (EditText) findViewById(R.id.info_id_update_newpwd);
		mNewPwd2 = (EditText) findViewById(R.id.info_id_update_newpwd2);
		
		mSubmit=(Button)findViewById(R.id.info_id_login_loginbtn);
		
		mOldPwd.setOnFocusChangeListener(mOnFocusChangeListener);
		mNewPwd.setOnFocusChangeListener(mOnFocusChangeListener);
		mNewPwd2.setOnFocusChangeListener(mOnFocusChangeListener);
		
		mSubmit.setOnClickListener(this);
	}
	
	private final OnFocusChangeListener mOnFocusChangeListener=new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v==mOldPwd&&!hasFocus) {
				if (!CheckUtils.checkIsEmpty(mOldPwd)) {
					showMsg(R.string.tips_updatepwd_oldpwd);
				}
			}else if (v==mNewPwd&&!hasFocus) {
				if (!CheckUtils.checkIsEmpty(mNewPwd)) {
					showMsg(R.string.tips_updatepwd_newpwd);
				}
			}else if (v==mNewPwd2&&!hasFocus) {
				if (!CheckUtils.checkIsEmpty(mNewPwd2)) {
					showMsg(R.string.tips_updatepwd_newpwd2);
				}
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v==mSubmit) {
			if (!CheckUtils.checkIsEmpty(mOldPwd)) {
				showMsg(R.string.tips_updatepwd_oldpwd);
				return;
			}
			if (!CheckUtils.checkIsEmpty(mNewPwd)) {
				showMsg(R.string.tips_updatepwd_newpwd);
				return;
			}
			if (!CheckUtils.checkIsEmpty(mNewPwd2)) {
				showMsg(R.string.tips_updatepwd_newpwd2);
				return;
			}
			if (!mNewPwd.getText().toString().equals(mNewPwd2.getText().toString())) {
				showMsg(R.string.tips_updatepwd_newpwd2);
				return;
			}
			submit();
		}
	};
	
	//提交修改密码的请求
	private void submit(){
		final JSONObject jsonObject=new JSONObject();
		try {
            showSpecialProgress();
			jsonObject.put(Constants.ACTION,Constants.CHANGE_PASSWORD);
			jsonObject.put(Constants.TOKEN, application.getToken());
			jsonObject.put(Constants.JSON, new JSONObject().put("old_password", MD5.encode(mOldPwd.getText().toString())).put("new_password", MD5.encode(mNewPwd.getText().toString())).toString());
			ApiClient.doWithObject(Constants.COMMON_SERVER_URL, jsonObject,null, new AjaxCallBack() {
				
				@Override
				public void receive(int code, Object result) {
					dismissProgress();
					switch (code) {
					case ResultCode.RESULT_OK:
						application.writeInfo(Constants.XMPP_PASSWORD, mNewPwd2.getText().toString());
						showMsg(R.string.tips_updatepwd_success);
						finish();
						break;
					case ResultCode.RESULT_ERROR:
						if (result instanceof String) 
							showMsg(result.toString());
						else
							showMsg(R.string.tips_updatepwd_failed);
						break;
					case ResultCode.RESULT_FAILED:
						if (result instanceof String) 
							showMsg(result.toString());
						else
							showMsg(R.string.tips_updatepwd_failed);
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
