package com.maogousoft.logisticsmobile.driver.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.maogousoft.logisticsmobile.driver.utils.LogUtil;

/**
 * Created by aliang on 2015/4/14.
 */
public class PhoneBroadcastListener  extends BroadcastReceiver {

    private static final String TAG = "PhoneBroadcastListener";
    /**
     * 手机没有通话，在一般的状态值
     */
    public static final int CALL_TYPE_IDEL = 0;
    /**
     * 手机通话状态值
     */
    public static final int CALL_TYPE_CALLING = 1;
    /**
     * 手机响铃状态值
     */
    public static final int CALL_TYPE_RING = 2;

    /**
     * 当前手机通话状态值
     */
    private int currentState = CALL_TYPE_IDEL ;
    /**
     * 手机原来的通话状态值
     */
    private int oldState = CALL_TYPE_IDEL ;

    private PhoneListener listener;
    private Context mContext;

    @Override//当发生监听的事件，系统会调用这个方法
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        //进行细节上的监控，我们需要操作TelephonyManager，为它设置监听器，他就给我们反馈
        //拿到系统的TelephonyManager
        TelephonyManager tpManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        listener = new PhoneListener();//创建监听器
        tpManager.listen( listener, PhoneStateListener.LISTEN_CALL_STATE);//设置监听器
    }
    private class PhoneListener extends PhoneStateListener {
        @Override//当电话状态发生改变的时候，系统会调用这个方法
        public void onCallStateChanged(int state, String incomingNumber) {
            //首先取得当前的状态值
            oldState = SharedPreferencesProvider.getInstance(mContext).getOldPhoneState();

            switch( state ){
                case TelephonyManager.CALL_STATE_IDLE :
                    currentState = CALL_TYPE_IDEL;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK :
                    currentState = CALL_TYPE_CALLING;
                    break;
                case TelephonyManager.CALL_STATE_RINGING :
                    currentState = CALL_TYPE_RING;
                    break;
            }
            //当通话状态发生改变
            if( oldState == CALL_TYPE_RING && currentState == CALL_TYPE_CALLING ){
                LogUtil.d(TAG, "接听");
            } else if( oldState == CALL_TYPE_CALLING && currentState == CALL_TYPE_IDEL ){
                LogUtil.d(TAG, "挂断" );
            } if( oldState == CALL_TYPE_IDEL && currentState == CALL_TYPE_CALLING ){
                LogUtil.d(TAG, "拨号" );
            }

            SharedPreferencesProvider.getInstance(mContext).saveOldPhoneState( currentState);
        }
    }
}