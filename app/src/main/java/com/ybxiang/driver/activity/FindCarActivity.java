package com.ybxiang.driver.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.maogousoft.logisticsmobile.driver.activity.CarCloudSearchActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.maogousoft.logisticsmobile.driver.CitySelectView;
import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.activity.home.NewSourceActivity;
import com.maogousoft.logisticsmobile.driver.activity.home.SearchSourceActivity;
import com.maogousoft.logisticsmobile.driver.activity.info.OptionalActivity;
import com.maogousoft.logisticsmobile.driver.activity.share.ShareActivity;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.model.NewSourceInfo;
import com.maogousoft.logisticsmobile.driver.utils.LogUtil;
import com.maogousoft.logisticsmobile.driver.utils.MyAlertDialog;

/**
 * 查找车源
 * 
 * @author ybxiang
 */
public class FindCarActivity extends BaseActivity {

	private static final String LOGTAG = LogUtil
			.makeLogTag(SearchSourceActivity.class);
	private Button mMore;

	private Button mSubmit;

	private CitySelectView cityselectStart, cityselectEnd;

	private Spinner carTypeSpinner, carStatusSpinner, carLocationStatus;
	private EditText edtCarLength;

	private ArrayList<HashMap<String, Object>> listCarTypes = new ArrayList<HashMap<String, Object>>();// 车辆类型集合

	private int selectedCarStatus = 0;// 选择的车辆状态 不限，空车，在途
	private int selectedCarLocationStatus = 0;// 选择车辆定位有效事件状态 今天，两小时内，六小时内
	private int selectedCarType = 0;// 选择的车辆类型
	private int selectedCarWay = 0;// 选择的车辆方式，PR1.3

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_car_source);
		initViews();
		initUtils();
		initDatas();

	}

	// 初始化视图
	private void initViews() {

		((TextView) findViewById(R.id.titlebar_id_content)).setText("查找车源");
		mMore = (Button) findViewById(R.id.titlebar_id_more);
		mMore.setText("附近车源");
		mSubmit = (Button) findViewById(R.id.search_source__submit);

		cityselectStart = (CitySelectView) findViewById(R.id.cityselect_start);
		cityselectEnd = (CitySelectView) findViewById(R.id.cityselect_end);
		mMore.setOnClickListener(this);
		mSubmit.setOnClickListener(this);

	}

	private void initUtils() {

		getCarTypes();

	}

	// 初始化数据，获取车型
	private void initDatas() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 附近车源
		case R.id.titlebar_id_more:
//			startActivity(new Intent(mContext, NearbyCarSourceActivity.class));
            startActivity(new Intent(mContext, CarCloudSearchActivity.class));
			break;
		case R.id.search_source__submit:
			// 先检测是否已经完善了资料
			if (application.checkIsRegOptional()) {
				submit();
			} else {
				final MyAlertDialog dialog = new MyAlertDialog(mContext, R.style.DialogTheme);
				dialog.show();
				dialog.setTitle("提示");
				dialog.setMessage("请完善信息，否则无法提供适合你车型、线路的货源。");
				dialog.setLeftButton("完善资料", new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Intent intent = new Intent(mContext, OptionalActivity.class);
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
			}

			break;

		default:
			break;
		}
	}

	private void submit() {

		if (cityselectStart.getSelectedProvince() == null
				| cityselectEnd.getSelectedProvince() == null) {

			showMsg("请选择出发地，目的地。");

			return;

		}

		final JSONObject jsonObject = new JSONObject();
		final JSONObject params = new JSONObject();
		try {
			jsonObject.put(Constants.ACTION, Constants.SEARCH_SOURCE_ORDER);
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
			// add ybxiang begin
			if (selectedCarStatus != 0) {
				params.put("car_status", selectedCarStatus);
			}
			if (selectedCarLocationStatus != 0) {
				params.put("car_location_status", selectedCarLocationStatus);
			}
			// add ybxiang end
			if (selectedCarType != 0) {
				params.put("car_type", selectedCarType);
			}
			if (!TextUtils.isEmpty(edtCarLength.getText())) {
				params.put("car_length", edtCarLength.getText().toString());
			}
			params.put("car_way", selectedCarWay); // add PR1.3
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
										Intent intent = new Intent(mContext,
												NewSourceActivity.class);
										intent.putExtra("isFromHomeActivity",
												true);
										intent.putExtra("NewSourceInfos",
												(Serializable) mList);
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
								if (result != null) {
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

													String content = null;
													startActivity(new Intent(
                                                            mContext,
															ShareActivity.class)
															.putExtra("share",
																	content));
													finish();
												}
											});

									// }
								}
								// showMsg(result.toString());
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

	public void getCarTypes() {

		HashMap<String, Object> hm = null;

		hm = new HashMap<String, Object>();
		hm.put("id", 43);
		hm.put("name", "不限");
		listCarTypes.add(hm);

		hm = new HashMap<String, Object>();
		hm.put("id", 44);
		hm.put("name", "平板");
		listCarTypes.add(hm);

		hm = new HashMap<String, Object>();
		hm.put("id", 45);
		hm.put("name", "高栏");
		listCarTypes.add(hm);

		hm = new HashMap<String, Object>();
		hm.put("id", 46);
		hm.put("name", "厢式");
		listCarTypes.add(hm);

		hm = new HashMap<String, Object>();
		hm.put("id", 47);
		hm.put("name", "冷藏");
		listCarTypes.add(hm);

		hm = new HashMap<String, Object>();
		hm.put("id", 48);
		hm.put("name", "集装箱");
		listCarTypes.add(hm);

		hm = new HashMap<String, Object>();
		hm.put("id", 49);
		hm.put("name", "全封闭");
		listCarTypes.add(hm);

		hm = new HashMap<String, Object>();
		hm.put("id", 50);
		hm.put("name", "特种");
		listCarTypes.add(hm);

		hm = new HashMap<String, Object>();
		hm.put("id", 51);
		hm.put("name", "危险");
		listCarTypes.add(hm);

		hm = new HashMap<String, Object>();
		hm.put("id", 52);
		hm.put("name", "自卸");
		listCarTypes.add(hm);

		hm = new HashMap<String, Object>();
		hm.put("id", 53);
		hm.put("name", "其他");
		listCarTypes.add(hm);
	}

	// add for PR1.3 begin
	/**
	 * 运输方式：零担或者整车，二者必选其一
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
			selectedCarWay = 0;
			break;
		case R.id.car_way_whole:
			selectedCarWay = 1;
			// 整车
			break;
		}
	}

	/**
	 * 关注线路 保持搜索的条件，保存为一个名字（本地）
	 * 
	 * @param view
	 */
	public void onFocusLine(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
				.setTitle(R.string.focus_line)
				.setMessage(R.string.focus_line_dialog_msg).setCancelable(true);
		builder.setPositiveButton(R.string.submit,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 保存此条线路信息
						// saveFocusLine();
						Toast.makeText(mContext, "关注成功", Toast.LENGTH_SHORT)
								.show();
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				});
		builder.create().show();
	}

	/**
	 * 保存关注路线的信息，存储到本地还是什么的？
	 */
	private void saveFocusLine() {

	}

	/**
	 * 快速设置 从关注的线路列表选择进行快速填充文本
	 * 
	 * @param view
	 */
	public void onFastSetting(View view) {
		Toast.makeText(mContext, "快速设置生效", Toast.LENGTH_SHORT).show();
	}
	// add for PR1.3 end
}
