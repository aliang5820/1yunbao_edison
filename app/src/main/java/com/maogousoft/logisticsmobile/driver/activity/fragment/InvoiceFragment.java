package com.maogousoft.logisticsmobile.driver.activity.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseListFragment;
import com.maogousoft.logisticsmobile.driver.activity.home.InvoiceActivity;
import com.maogousoft.logisticsmobile.driver.activity.home.SourceDetailActivity;
import com.maogousoft.logisticsmobile.driver.adapter.BaseListAdapter;
import com.maogousoft.logisticsmobile.driver.adapter.InvoiceAdapter;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.model.NewSourceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by aliang on 2015/4/25.
 */
public class InvoiceFragment extends BaseListFragment implements AbsListView.OnScrollListener {
    private static final String TAG = "InvoiceFragment";
    private int invoiceType = 0;
    // 底部更多
    private View mFootView;
    private ProgressBar mFootProgress;
    private TextView mFootMsg;
    // 当前模式
    private int state = WAIT;
    // 当前页码
    private int pageIndex = 1;
    // 滑动状态
    private boolean state_idle = false;
    // 已加载全部
    private boolean load_all = false;
    private InvoiceActivity.SelectItemCallBack callBack;


    public static InvoiceFragment newInstance(int invoiceType) {
        InvoiceFragment newFragment = new InvoiceFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.INVOICE_TYPE, invoiceType);
        newFragment.setArguments(bundle);
        //bundle还可以在每个标签里传送数据
        return newFragment;
    }

    public void setCallBack(InvoiceActivity.SelectItemCallBack callBack) {
        this.callBack = callBack;
    }

    public BaseListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Bundle args = getArguments();
        invoiceType = args.getInt(Constants.INVOICE_TYPE, 0);
        // 页脚信息
        mFootView = LayoutInflater.from(mContext).inflate(R.layout.listview_footview, null);
        mFootView.setClickable(false);
        mFootProgress = (ProgressBar) mFootView.findViewById(android.R.id.progress);
        mFootMsg = (TextView) mFootView.findViewById(android.R.id.text1);
        mListView.addFooterView(mFootView);
        mListView.setOnScrollListener(this);
        //历史货单没有底部菜单,所以没有单选按钮
        mAdapter = new InvoiceAdapter(mContext, invoiceType != 3, invoiceType == 1, application.getUserType(), callBack);
        setListAdapter(mAdapter);
        setListShown(false);

        getData(invoiceType, pageIndex);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public NewSourceInfo getSelectedItem() {
        return ((InvoiceAdapter) mAdapter).getSelectedSource();
    }

    public void removeDataAndNotifyDataChange(Object object) {
        if(mAdapter != null) {
            if(mAdapter.getList() != null) {
                mAdapter.getList().remove(object);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    // 根据条件请求指定页数的数据
    private void getData(int invoiceType, int page) {
        try {
            state = ISREFRESHING;
            final JSONObject jsonObject = new JSONObject();
            if (application.getUserType() == Constants.USER_DRIVER) {
                jsonObject.put(Constants.ACTION, Constants.QUERY_PENDING_SOURCE_ORDER);
            } else {
                jsonObject.put(Constants.ACTION, Constants.QUERY_ORDER);
            }
            jsonObject.put(Constants.TOKEN, application.getToken());
            JSONObject json = new JSONObject();
            json.put("page", page);
            json.put("order_type", invoiceType);
            jsonObject.put(Constants.JSON, json.toString());

            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    NewSourceInfo.class, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            setListShown(true);
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    if (result instanceof List) {
                                        List<NewSourceInfo> mList = (List<NewSourceInfo>) result;
                                        if (mList == null || mList.isEmpty()) {
                                            load_all = true;
                                            mFootProgress.setVisibility(View.GONE);
                                            mFootMsg.setText("已加载全部");
                                        } else {
                                            if (mList.size() < 10) {
                                                load_all = true;
                                                mFootProgress.setVisibility(View.GONE);
                                                mFootMsg.setText("已加载全部");
                                            }
                                            mAdapter.addAll(mList);
                                            mAdapter.notifyDataSetChanged();
                                        }
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
                            if (mAdapter.isEmpty()) {
                                setEmptyText("没有找到数据哦");
                            }
                            state = WAIT;
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            return;
        }
        if (state != WAIT) {
            return;
        }
        this.state_idle = true;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (!this.state_idle) {
            return;
        }
        if (firstVisibleItem == 0
                || (firstVisibleItem + visibleItemCount) != totalItemCount) {
            return;
        }
        // 如果当前没有加载数据
        if (state != ISREFRESHING && !load_all) {
            getData(invoiceType, ++pageIndex);
            mFootProgress.setVisibility(View.VISIBLE);
            mFootMsg.setText(R.string.tips_isloading);
        }
    }
}
