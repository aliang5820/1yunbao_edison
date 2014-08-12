package com.maogousoft.logisticsmobile.driver.activity.vip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.model.ShopInfo;

/**
 * 地图
 *
 * @author wst
 */
public class ShopListMapActivity extends BaseActivity {

    private MapView mMapView;

    private ShopInfo shopInfo;

    // final static String TAG = "MainActivty";
    // static MapView mMapView = null;
    // private MapController mMapController = null;
    // public MKMapViewListener mMapListener = null;
    // Button testItemButton = null;
    // Button removeItemButton = null;
    // Button removeAllItemButton = null;
    // EditText indexText = null;
    // OverlayTest ov = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_shoplist_map);
        initView();
        initData();
        initMap();
    }

    private void initView() {
        findViewById(R.id.titlebar_id_back).setOnClickListener(this);
        mMapView = (MapView) findViewById(R.id.motorcade_map_vip);
    }

    private void initData() {
        if (getIntent().hasExtra("shopInfo")) {
            shopInfo = (ShopInfo) getIntent().getSerializableExtra("shopInfo");
            if (shopInfo != null) {
                String addr = shopInfo.getVender_address();
                double lat = shopInfo.getLatitude();
                double lng = shopInfo.getLongitude();
                String name = shopInfo.getVender_name();

                if (TextUtils.isEmpty("name")) {
                    ((TextView) findViewById(R.id.titlebar_id_content)).setText("商家位置");
                } else {
                    ((TextView) findViewById(R.id.titlebar_id_content)).setText(name);
                }

                if (lat == 0 || lng == 0 || TextUtils.isEmpty(addr)) {
                    return;
                }

            }

        }
    }

    private void initMap() {

        if (application.getBMapManager() == null) {
            application.initBMapManager();
        }

        mMapView.getController().setZoom(16);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setLongClickable(true);

        /**
         * 服务端存储反了
         */
        //GeoPoint geoPoint = new GeoPoint((int) (shopInfo.getLatitude() * 1e6), (int) (shopInfo.getLongitude() * 1e6));
        GeoPoint geoPoint = new GeoPoint((int) (shopInfo.getLongitude() * 1e6), (int) (shopInfo.getLatitude() * 1e6));
        //GeoPoint geoPoint = new GeoPoint((int) (39.945 * 1e6), (int) (116.404 * 1e6));
        OverlayItem overlayItem = new OverlayItem(geoPoint, "文字片段", "标题文字");
        Drawable mark = getResources().getDrawable(R.drawable.map_marker_blue);
        ItemizedOverlay itemizedOverlay = new ItemizedOverlay(mark, mMapView);

        mMapView.getOverlays().clear();
        mMapView.getOverlays().add(itemizedOverlay);
        itemizedOverlay.addItem(overlayItem);

        //mMapView.refresh();
        // 移动到地图中心点
        mMapView.getController().setCenter(geoPoint);

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存地图状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMapView.onRestoreInstanceState(savedInstanceState);
    }

}
