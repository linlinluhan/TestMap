package com.zhuoxin.hellomap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;

/**
 * Created by 月林 on 2016/12/14.
 */

public class PoiSearchActivity extends Activity implements LocationSource {
    Button btnChange;
    TextView tvTitle;
    AMap aMap;
    MapView mapView;
    private AMapLocationClientOption aMapOption;
    private AMapLocationClient aMapLClient;
    private double lat,lon;
    private OnLocationChangedListener changedListener;
    private AMapLocationListener aMapListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_map_change);
        initView();
        mapView.onCreate(savedInstanceState);
    }

    private void initView() {
        btnChange = (Button) findViewById(R.id.btn_poi_change);
        tvTitle = (TextView) findViewById(R.id.tv_poi_title);
        mapView = (MapView) findViewById(R.id.poi_mapview);
        if (aMap==null){
            aMap = mapView.getMap();
            //设置定位图标
            aMap.setMyLocationStyle(new MyLocationStyle().
                    myLocationIcon(BitmapDescriptorFactory.
                            fromResource(R.mipmap.navi_map_gps_locked)));
        }
        aMap.setLocationSource(this);
        //显示定位按钮
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
        aMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        changedListener=onLocationChangedListener;
        initLocation();
    }

    @Override
    public void deactivate() {
        changedListener=null;
        aMapLClient.stopLocation();
        aMapListener=null;
    }

    boolean flag=false;
    /**初始化定位信息，设置定位模式*/
    private void initLocation() {
        aMapOption = new AMapLocationClientOption();
        //初始化定位
        aMapLClient = new AMapLocationClient(getApplicationContext());
        aMapListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                lat=aMapLocation.getLatitude();//纬度
                lon=aMapLocation.getLongitude();//经度
                if (aMapLocation!=null){
                    if (aMapLocation.getErrorCode()==0){
                        if (!flag){
                            changedListener.onLocationChanged(aMapLocation);
                            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                            flag = true;
                        }
                    }
                }
            }
        };
        //设置定位回调监听
        aMapLClient.setLocationListener(aMapListener);
        //设置定位模式为高精度模式
        aMapOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置返回地址信息
        aMapOption.setNeedAddress(true);
        aMapOption.setHttpTimeOut(20000);
        //设置定位间隔时间
        aMapOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        aMapLClient.setLocationOption(aMapOption);
        //启动定位
        aMapLClient.startLocation();
    }
}
