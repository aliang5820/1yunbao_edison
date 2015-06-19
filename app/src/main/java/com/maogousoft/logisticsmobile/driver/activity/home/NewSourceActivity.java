package com.maogousoft.logisticsmobile.driver.activity.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseListActivity;
import com.maogousoft.logisticsmobile.driver.activity.ShakeActivity;
import com.maogousoft.logisticsmobile.driver.activity.info.OptionalActivity;
import com.maogousoft.logisticsmobile.driver.activity.share.ShareActivity;
import com.maogousoft.logisticsmobile.driver.adapter.NewSourceListAdapter;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.model.NewSourceInfo;
import com.maogousoft.logisticsmobile.driver.model.SourceCount;
import com.maogousoft.logisticsmobile.driver.utils.MyAlertDialog;
import com.ybxiang.driver.activity.MyFriendsActivity;
import com.ybxiang.driver.model.FocusLineInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 新货源列表
 *
 * @author lenovo
 */
public class NewSourceActivity extends BaseListActivity implements OnScrollListener {

    private Button mMore;
    private int rightButton = 0;// 0显示查找货源，1显示关注此线路,2不显示此button
    // 底部更多
    private View mFootView, mHeaderView;
    private ProgressBar mFootProgress;
    private TextView mFootMsg, titlebar_id_content;
    private String params;//路线快捷搜索的信息
    // 当前模式
    private int state = WAIT;
    // 当前页码
    private int pageIndex = 1;
    // 滑动状态
    private boolean state_idle = false;
    // 已加载全部
    private boolean load_all = false;

    private List<NewSourceInfo> newSourceInfos = null;// 上一页传来的
    private FocusLineInfo focusLineInfo = new FocusLineInfo();// 上一页传输过来的省市区
    //请求类型
    private String queryType = Constants.QUERY_SOURCE_ORDER;
    private JSONObject queryParams = new JSONObject();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //重置数据
        if(mAdapter != null && !mAdapter.isEmpty()) {
            mAdapter.removeAll();
        }
        initData(intent);
    }

    private void initViews() {
        mMore = (Button) findViewById(R.id.titlebar_id_more);
        mMore.setText("查找货源");
        setIsRightKeyIntoShare(false);

        titlebar_id_content = (TextView) findViewById(R.id.titlebar_id_content);
        titlebar_id_content.setText(R.string.string_title_newsource);
        // 页脚信息
        mFootView = getLayoutInflater().inflate(R.layout.listview_footview, null);

        mFootView.setClickable(false);
        mFootProgress = (ProgressBar) mFootView.findViewById(android.R.id.progress);
        mFootMsg = (TextView) mFootView.findViewById(android.R.id.text1);
        mListView.addFooterView(mFootView);

        mListView.setOnScrollListener(this);
        mMore.setOnClickListener(this);

    }

    private void initData(Intent intent) {
        mAdapter = new NewSourceListAdapter(mContext);
        setListShown(false);
        if (intent.hasExtra(Constants.COMMON_OBJECT_KEY)) {
            String params = getIntent().getStringExtra(Constants.COMMON_KEY);
            try {
                queryParams = new JSONObject(params);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            newSourceInfos = (List<NewSourceInfo>) intent.getSerializableExtra(Constants.COMMON_OBJECT_KEY);
            // 从 搜索货源进入，不需要显示 搜索货源按钮 modify
            mMore.setText("关注此路线");
            rightButton = 1;
            mAdapter.addAll(sort(newSourceInfos));
            setListShown(true);
            if(newSourceInfos.size() < 10) {
                load_all = true;
                mFootProgress.setVisibility(View.GONE);
                mFootMsg.setText("已加载全部");
            }
            state = WAIT;
        }
        if (intent.hasExtra("focusLineInfo")) {
            focusLineInfo = (FocusLineInfo) intent.getSerializableExtra("focusLineInfo");
            params = intent.getStringExtra("params");
        }
        if (intent.hasExtra("getFriendOrderList")) {
            if (intent.getBooleanExtra("getFriendOrderList", false)) {
                queryType = Constants.FRIEND_ORDER_LIST;//好友货源
                mMore.setText("好友管理");
                titlebar_id_content.setText("好友货源");
                rightButton = 2;
            }
        }
        if (intent.hasExtra("QUERY_MAIN_LINE_ORDER")) {
            if (intent.getBooleanExtra("QUERY_MAIN_LINE_ORDER", false)) {
                queryType = Constants.QUERY_MAIN_LINE_ORDER;//关注货源
                mMore.setText("主营线路");
                titlebar_id_content.setText("关注货源");
                rightButton = 3;
            }
        }
        if (intent.hasExtra("SHAKE_ONE_SHAKE")) {
            if (intent.getBooleanExtra("SHAKE_ONE_SHAKE", false)) {
                queryType = Constants.SHAKE_ONE_SHAKE;//摇一摇
                params = intent.getStringExtra("params");
                mMore.setVisibility(View.GONE);
                titlebar_id_content.setText(R.string.string_title_newsource);
            }
        }
        if(intent.getBooleanExtra(Constants.SEARCH_SOURCE, false)) {
            //从搜索过来的货源需要显示总货源数
            addHeaderSearchView();
        }
        setListAdapter(mAdapter);
    }

    private void addHeaderSearchView() {
        if(mListView.getHeaderViewsCount() == 0) {
            mHeaderView = getLayoutInflater().inflate(R.layout.listview_header_search_layout, null);
            mHeaderView.setOnClickListener(this);
            mListView.addHeaderView(mHeaderView);
        }
        //查询今日货源数量
        queryAllSourceSize();
    }

    @Override
    public void onClick(View v) {
        ((BaseListActivity) mContext).setIsRightKeyIntoShare(false);
        switch (v.getId()) {
            case R.id.titlebar_id_more:
                switch (rightButton) {
                    case 0:// 查找新货源
                        startActivity(new Intent(mContext, SearchSourceActivity.class));
                        finish();
                        break;
                    case 1:// 关注此线路
                        doFocus();
                        break;
                    case 2:
                        startActivity(new Intent(mContext, MyFriendsActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(mContext, MainLineActivity.class));
                        break;
                    default:
                        break;
                }
                break;
            case R.id.search:
                startActivity(new Intent(mContext, SearchSourceActivity.class));
                break;
        }
        super.onClick(v);
    }

    // 点击关注此路线
    public void doFocus() {
        final JSONObject jsonObject = new JSONObject();
        final JSONObject params = new JSONObject();
        try {

            jsonObject.put(Constants.ACTION, Constants.FOCUS_LINE);
            jsonObject.put(Constants.TOKEN, application.getToken());
            if (focusLineInfo != null) {
                params.put("start_province", focusLineInfo.getStart_province());
                params.put("start_city", focusLineInfo.getStart_city());
                params.put("start_district", focusLineInfo.getStart_district());

                params.put("end_province", focusLineInfo.getEnd_province());
                params.put("end_city", focusLineInfo.getEnd_city());
                params.put("end_district", focusLineInfo.getEnd_district());
                params.put("type", application.getUserType());
            }
            jsonObject.put(Constants.JSON, params);
            showSpecialProgress();
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    null, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    Toast.makeText(mContext, "关注此线路成功", Toast.LENGTH_SHORT).show();
                                    break;
                                case ResultCode.RESULT_ERROR:
                                    if (result != null)
                                        showMsg(result.toString());

                                    break;
                                case ResultCode.RESULT_FAILED:
                                    if (result != null) {
                                        // 您当月的免费搜索次数已经用完

                                        // if (result.equals("您当月的免费搜索次数已经用完")) {
                                        final MyAlertDialog dialog = new MyAlertDialog(
                                                mContext, R.style.DialogTheme);
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

    private void queryAllSourceSize() {
        final JSONObject jsonObject = new JSONObject();
        try {
            // 搜索货源总数
            jsonObject.put(Constants.ACTION, Constants.QUERY_SOURCE_ORDER_COUNT);
            jsonObject.put(Constants.TOKEN, application.getToken());
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    SourceCount.class, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    //显示货源总数
                                    SourceCount sourceCount = (SourceCount) result;
                                    changeSourceNumber(sourceCount.getCount());
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (position <= mAdapter.getList().size()) {

            // 先检测是否已经完善了资料
            if (!application.checkIsRegOptional() && application.getUserType() == Constants.USER_DRIVER) {
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
            } else {
                final Intent intent = new Intent(mContext, SourceDetailActivity.class);
                if(getIntent().getBooleanExtra(Constants.SEARCH_SOURCE, false)) {
                    position = position - 1;
                }
                intent.putExtra(Constants.ORDER_ID,
                        ((NewSourceListAdapter) mAdapter).getList().get(position).getId());
                intent.putExtra("type", "NewSourceActivity");
                startActivityForResult(intent, 1000);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.CALL_NUMBER_SOURCE = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter.isEmpty()) {
            pageIndex = 1;
            getData(pageIndex);
        }
        //如果是拨打的货主电话，弹出反馈页面
        if(TextUtils.equals(Constants.CALL_NUMBER_SOURCE, Constants.CALL_NUMBER) && !TextUtils.isEmpty(Constants.CALL_NUMBER_SOURCE)) {
            Constants.CALL_NUMBER = "";
            Constants.CALL_NUMBER_SOURCE = "";

            // 创建PopupWindow对象
            if(popupWindow == null) {
                // 引入窗口配置文件
                View view = LayoutInflater.from(mContext).inflate(R.layout.view_source_contact_feedback_layout, null, false);
                popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                // 需要设置一下此参数，点击外边可消失
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                //设置点击窗口外边窗口消失
                popupWindow.setOutsideTouchable(true);
                // 设置此参数获得焦点，否则无法点击
                popupWindow.setFocusable(true);
                view.findViewById(R.id.error_type1).setOnClickListener(popupWindowListener);
                view.findViewById(R.id.error_type2).setOnClickListener(popupWindowListener);
                view.findViewById(R.id.error_type3).setOnClickListener(popupWindowListener);
                view.findViewById(R.id.qiangdan).setOnClickListener(popupWindowListener);
            }
            // 显示窗口
            popupWindow.showAtLocation(mListView, Gravity.CENTER, 0, 0);
        }
    }
    private PopupWindow popupWindow = null;
    private OnClickListener popupWindowListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(popupWindow.isShowing()) {
                // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
                popupWindow.dismiss();
            }
            switch (view.getId()) {
                case R.id.error_type1:
                    callFeedBack(Constants.CALL_NUMBER_SOURCE_ORDER_ID, 1);//电话反馈(信息有误)
                    break;
                case R.id.error_type2:
                    callFeedBack(Constants.CALL_NUMBER_SOURCE_ORDER_ID, 3);//电话反馈(没有谈好)
                    break;
                case R.id.error_type3:
                    callFeedBack(Constants.CALL_NUMBER_SOURCE_ORDER_ID, 2);//电话反馈(货已订出)
                    break;
                case R.id.qiangdan:
                    placeOrder(Constants.CALL_NUMBER_SOURCE_ORDER_ID);
                    break;
            }
        }
    };

    /**
     * 电话结果反馈
     * @param orderId
     * @param type
     */
    private void callFeedBack(Integer orderId, int type) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ACTION, Constants.DRIVER_PHONE_FEEDBACK);
            jsonObject.put(Constants.TOKEN, application.getToken());
            jsonObject.put(Constants.JSON, new JSONObject().put("order_id", orderId).put("error_type", type).toString());

            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, jsonObject,
                    null, new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            showMsg(result.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 抢单
     */
    public void placeOrder(Integer orderId) {
        final JSONObject params = new JSONObject();
        try {
            params.put(Constants.ACTION, Constants.PLACE_SOURCE_ORDER);
            params.put(Constants.TOKEN, application.getToken());
            params.put(Constants.JSON, new JSONObject().put("order_id", orderId).toString());
            showProgress(getString(R.string.tips_sourcedetail_qiang));
            ApiClient.doWithObject(Constants.DRIVER_SERVER_URL, params, null,
                    new AjaxCallBack() {

                        @Override
                        public void receive(int code, Object result) {
                            dismissProgress();
                            switch (code) {
                                case ResultCode.RESULT_OK:
                                    showMsg(R.string.tips_sourcedetail_qiang_success);
                                    break;
                                case ResultCode.RESULT_ERROR:
                                    showMsg(result.toString());
                                    break;
                                case ResultCode.RESULT_FAILED:
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

    // 请求指定页数的数据
    private void getData(final int page) {
        try {
            state = ISREFRESHING;
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.ACTION, queryType);
            jsonObject.put(Constants.TOKEN, application.getToken());
            if(TextUtils.isEmpty(params)) {
                jsonObject.put(Constants.JSON, queryParams.put("page", page).toString());
            } else {
                JSONObject json = new JSONObject(params);
                jsonObject.put(Constants.JSON, json.put("page", page).toString());
            }

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
                                            mAdapter.addAll(sort(mList));
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

    private void changeSourceNumber(int size) {
        if(mHeaderView != null) {
            ((TextView) mHeaderView.findViewById(R.id.number)).setText(getString(R.string.source_number, size * 16));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) {
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
            getData(++pageIndex);
            mFootProgress.setVisibility(View.VISIBLE);
            mFootMsg.setText(R.string.tips_isloading);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1000) {
            System.out.println("执行onActivityResult");
            if (data.hasExtra("sourceInfo")) {
                NewSourceInfo sourceInfo = (NewSourceInfo) data
                        .getSerializableExtra("sourceInfo");
                try {
                    for (int i = 0; i < mAdapter.getList().size(); i++) {
                        if (sourceInfo.getId().intValue() == ((NewSourceInfo) mAdapter
                                .getList().get(i)).getId().intValue()) {
                            ((NewSourceInfo) mAdapter.getList().get(i))
                                    .setFavorite_status(sourceInfo
                                            .getFavorite_status());
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 按照已关注，未读，已读 排序
     */
    public List<NewSourceInfo> sort(List<NewSourceInfo> list) {
        ArrayList<NewSourceInfo> listRestul = new ArrayList<NewSourceInfo>();

        if (list == null || list.size() == 0) {
            return listRestul;
        }

        for (int i = 0; i < list.size(); i++) {
            int status = list.get(i).getFavorite_status();
            if (status == 1) {
                listRestul.add(list.get(i));
            }
        }

        for (int y = 0; y < list.size(); y++) {
            int status = list.get(y).getFavorite_status();
            if (status != 1) {
                listRestul.add(list.get(y));
            }
        }
        return listRestul;
    }
}
