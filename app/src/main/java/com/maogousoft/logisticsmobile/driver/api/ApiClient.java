package com.maogousoft.logisticsmobile.driver.api;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.maogousoft.logisticsmobile.driver.AppException;
import com.maogousoft.logisticsmobile.driver.utils.HttpUtils;
import com.maogousoft.logisticsmobile.driver.utils.LogUtil;

public class ApiClient<T> {
	private static final String TAG = "ApiClient";

	// 最大线程数
	public static final int MAX_THREAD = 5;

	private static final String STATUS = "status";

	private static final String MESSAGE = "message";

	private static final String ITEMS = "items";

	private static final String ITEM = "item";

	// 解析单条数据
	public static <T> void doWithObject(final String url, final JSONObject params, final Class<T> cls, final AjaxCallBack callBack) {

		new FetchTask<T>(url, cls, params, callBack).execute();
	}

	private static final class FetchTask<T> extends AsyncTask<Void, Void, String> {

		private Class<T> cls;

		private String url;

		private JSONObject params;

		private AjaxCallBack callBack;

		private AppException appException;

		public FetchTask(String url, Class<T> cls, JSONObject params, AjaxCallBack callBack) {
			this.url = url;
			this.cls = cls;
			this.params = params;
			this.callBack = callBack;
		}

		@Override
		protected String doInBackground(Void... p) {
			try {
				final String result = HttpUtils.getUrlData(url, params);
				LogUtil.e(TAG, result);
				return result;
			} catch (AppException e) {
				e.printStackTrace();
				appException = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (!TextUtils.isEmpty(result)) {
					final JSONObject jsonObject = new JSONObject(result);
					final int status = jsonObject.optInt(STATUS);
					final String message = jsonObject.optString(MESSAGE);
					// 0表示请求成功
					if (status == 0) {
						if (jsonObject.has(ITEMS)) {
							final String items = jsonObject.optString(ITEMS);
							if (cls!=null) {
								List<T> mList = JSON.parseArray(items, cls);
								callBack.receive(ResultCode.RESULT_OK, mList);
							}else {
								callBack.receive(ResultCode.RESULT_OK,new JSONArray(items));
							}
						} else if (jsonObject.has(ITEM)) {
							final String item = jsonObject.optString(ITEM);
							if (cls!=null) {
								callBack.receive(ResultCode.RESULT_OK, JSON.parseObject(item, cls));
							}else {
								callBack.receive(ResultCode.RESULT_OK, new JSONObject(item));
							}
						} else {
							callBack.receive(ResultCode.RESULT_OK, message);
						}
					} else {
						callBack.receive(ResultCode.RESULT_FAILED, message);
					}
				} else {
					callBack.receive(ResultCode.RESULT_FAILED, appException == null ? "网络异常!" : appException.getErrorMessage());
				}
			} catch (JSONException e) {
				e.printStackTrace();
				callBack.receive(ResultCode.RESULT_FAILED, e.getMessage());
			}
		}
	}
}
