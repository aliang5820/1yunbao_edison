package com.maogousoft.logisticsmobile.driver.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.home.NewSourceActivity;
import com.maogousoft.logisticsmobile.driver.utils.HttpUtils;
import com.maogousoft.logisticsmobile.driver.utils.LocHelper;
import com.maogousoft.logisticsmobile.driver.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Edison on 2015/3/29.
 */
public class ShakeActivity extends BaseActivity implements BDLocationListener {
    private ShakeListener mShakeListener = null;
    private Vibrator mVibrator;
    /*private RelativeLayout mImgUp;
    private RelativeLayout mImgDn;*/
    private LocationClient mLocClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //司机才有摇一摇功能
        if(application.getUserType() == Constants.USER_DRIVER) {
        /*setContentView(R.layout.activity_shake_layout);
        ((TextView) findViewById(R.id.titlebar_id_content)).setText("摇一摇");*/
            //drawerSet ();//设置  drawer监听    切换 按钮的方向
            mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
        /*mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
        mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);*/
            if (!getIntent().hasExtra("QUERY_MAIN_LINE_ORDER") && !getIntent().hasExtra("getFriendOrderList")) {
                mShakeListener = new ShakeListener(mContext);
                mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
                    public void onShake() {
                        shakeAction();
                    }
                });
            }
        }
    }

    /**
     * 执行摇一摇事件
     */
    public void shakeAction() {
        LogUtil.e(TAG, "mShakeListener");
        //startAnim();  //开始 摇一摇手掌动画
        mShakeListener.stop();
        startVibrato(); //开始 震动
        showDefaultProgress();
        locationAction();
    }

    // 定位初始化
    private void locationAction() {
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /*public void startAnim() {   //定义摇一摇动画动画
        AnimationSet animUp = new AnimationSet(true);
        TranslateAnimation animationStep0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -0.5f);
        animationStep0.setDuration(500);
        TranslateAnimation animationStep1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, +0.5f);
        animationStep1.setDuration(500);
        animationStep1.setStartOffset(500);
        animUp.addAnimation(animationStep0);
        animUp.addAnimation(animationStep1);
        mImgUp.startAnimation(animUp);

        AnimationSet animDown = new AnimationSet(true);
        TranslateAnimation animationStep2 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, +0.5f);
        animationStep2.setDuration(500);
        TranslateAnimation animationStep3 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -0.5f);
        animationStep3.setDuration(500);
        animationStep3.setStartOffset(500);
        animDown.addAnimation(animationStep2);
        animDown.addAnimation(animationStep3);
        mImgDn.startAnimation(animDown);
    }*/

    public void startVibrato() {
        MediaPlayer player;
        player = MediaPlayer.create(this, R.raw.shake);
        player.setLooping(false);
        player.start();
        //第一个｛｝里面是节奏数组， 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
        //定义震动
        mVibrator.vibrate(new long[]{500, 200, 500, 200}, -1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mShakeListener != null) {
            mShakeListener.stop();
        }
    }

    /**
     * 开始查询
     *
     * @param params
     */
    private void startSearch(String params) {
        Intent intent = new Intent(mContext, NewSourceActivity.class);
        intent.putExtra("SHAKE_ONE_SHAKE", true);
        intent.putExtra("params", params);
        startActivity(intent);
        dismissProgress();
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        LogUtil.e(TAG, "onReceiveLocation");
        if (location == null) {
            //定位失败重新定位一次
            mLocClient.requestLocation();
            LogUtil.e(TAG, "location:" + location);
            return;
        }
        final JSONObject params = new JSONObject();
        if (TextUtils.isEmpty(location.getCity())) {
            getAddress(location.getLatitude(), location.getLongitude(), new LocHelper.LocAddrCallback() {
                @Override
                public void onRecivedLoc(String province, String city) {
                    try {
                        //获取到城市地名
                        params.put("location_province", province);
                        params.put("location_city", city);
                        startSearch(params.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            try {
                params.put("location_province", location.getProvince());
                params.put("location_city", location.getCity());
                startSearch(params.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mLocClient.stop();
    }

    /**
     * 通过经纬度,查询地址
     *
     * @param lat
     * @param lng
     */
    public void getAddress(final double lat, final double lng, final LocHelper.LocAddrCallback resultCallback) {
        new AsyncTask<BDLocation, Object, HashMap<String, Object>>() {

            @Override
            protected void onPostExecute(HashMap<String, Object> result) {
                if (result != null) {
                    String province = (String) result.get("province");
                    String city = (String) result.get("city");
                    LogUtil.e(TAG, "geocoder，获取到的城市:" + province + city);
                    if (resultCallback != null) {
                        resultCallback.onRecivedLoc(province, city);
                    }
                } else {
                    if (resultCallback != null) {
                        resultCallback.onRecivedLoc(null, null);
                    }
                }
                super.onPostExecute(result);
            }

            @Override
            protected HashMap<String, Object> doInBackground(BDLocation... params) {

                String url = "http://api.map.baidu.com/geocoder?" + "location=" + lat + "," + lng + "&output=json&key="
                        + Constants.strKey;

                // 时效宝 百度key b66fbb5a289082fa86ef1a7df81ab57f
                String returnStr = HttpUtils.getURLData(url);
                HashMap<String, Object> hmResult = null;
                JSONObject jObject;
                try {
                    jObject = new JSONObject(returnStr);

                    if (!jObject.getString("status").equalsIgnoreCase("OK")) {
                        LogUtil.e(TAG, "geocoder，没有获取到数据");
                        return null;
                    }

                    hmResult = new HashMap<String, Object>();
                    String province = jObject.getJSONObject("result").getJSONObject("addressComponent").getString("province");
                    String city = jObject.getJSONObject("result").getJSONObject("addressComponent").getString("city");
                    hmResult.put("province", province);
                    hmResult.put("city", city);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return hmResult;
            }
        }.execute();
    }
}

/**
 * 一个检测手机摇晃的监听器
 */
class ShakeListener implements SensorEventListener {
    // 速度阈值，当摇晃速度达到这值后产生作用
    private static final int SPEED_SHRESHOLD = 3000;
    // 两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 70;
    // 传感器管理器
    private SensorManager sensorManager;
    // 传感器
    private Sensor sensor;
    // 重力感应监听器
    private OnShakeListener onShakeListener;
    // 上下文
    private Context mContext;
    // 手机上一个位置时重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;
    // 上次检测时间
    private long lastUpdateTime;

    // 构造器
    public ShakeListener(Context c) {
        // 获得监听对象
        mContext = c;
        start();
    }

    // 开始
    public void start() {
        // 获得传感器管理器
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            // 获得重力传感器
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        // 注册
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }

    }

    // 停止检测
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    // 设置重力感应监听器
    public void setOnShakeListener(OnShakeListener listener) {
        onShakeListener = listener;
    }

    // 重力感应器感应获得变化数据
    public void onSensorChanged(SensorEvent event) {
        // 现在检测时间
        long currentUpdateTime = System.currentTimeMillis();
        // 两次检测的时间间隔
        long timeInterval = currentUpdateTime - lastUpdateTime;
        // 判断是否达到了检测时间间隔
        if (timeInterval < UPTATE_INTERVAL_TIME)
            return;
        // 现在的时间变成last时间
        lastUpdateTime = currentUpdateTime;

        // 获得x,y,z坐标
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // 获得x,y,z的变化值
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        // 将现在的坐标变成last坐标
        lastX = x;
        lastY = y;
        lastZ = z;
        //sqrt 返回最近的双近似的平方根
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ) / timeInterval * 10000;
        // 达到速度阀值，发出提示
        if (speed >= SPEED_SHRESHOLD) {
            onShakeListener.onShake();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 摇晃监听接口
    public interface OnShakeListener {
        public void onShake();
    }

}