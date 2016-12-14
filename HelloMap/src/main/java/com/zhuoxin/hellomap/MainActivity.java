package com.zhuoxin.hellomap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

public class MainActivity extends Activity implements View.OnClickListener, LocationSource,SensorEventListener {
    ImageView ivTraffic,ivUp,ivDown,ivDirection;
    LinearLayout llNear,llWay,llMine;
    EditText et;
    AMapLocationClient aMapLClient;
    AMapLocationListener aMapListener;
    AMapLocationClientOption aMapOption;
    OnLocationChangedListener changedListener;
    MapView mapView;
    AMap aMap;
    private double lat;
    private double lon;
    private SensorManager sensorManager;
    private Sensor sensorM,sensorS;
    //加速度
    float[]  fAcc= new float[3];
    //磁场
    float[]  fMagnetic = new float[3];
    float x;
    private Marker mGPSMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        llMine = (LinearLayout) findViewById(R.id.ll_mine);
        llNear = (LinearLayout) findViewById(R.id.ll_near);
        llWay = (LinearLayout) findViewById(R.id.ll_way);
        llWay.setOnClickListener(this);
        llNear.setOnClickListener(this);
        llMine.setOnClickListener(this);
        mapView= (MapView) findViewById(R.id.mv_main);
        // 此方法必须重写
        mapView.onCreate(savedInstanceState);
        ivTraffic = (ImageView) findViewById(R.id.iv_traffic);
        ivUp = (ImageView) findViewById(R.id.iv_zoom_up);
        ivDown = (ImageView) findViewById(R.id.iv_zoom_down);
        ivDirection= (ImageView) findViewById(R.id.iv_direction);
        //实时路况监听
        ivTraffic.setOnClickListener(this);
        ivDown.setOnClickListener(this);
        ivDirection.setOnClickListener(this);
        ivUp.setOnClickListener(this);
        et= (EditText) findViewById(R.id.et_main_search);
        //初始化地图变量
        if(aMap==null){
            aMap = mapView.getMap();
            //设置缩放按钮是否显示
            aMap.getUiSettings().setZoomControlsEnabled(false);
            //设置定位图标
            aMap.setMyLocationStyle(new MyLocationStyle().
                    myLocationIcon(BitmapDescriptorFactory.
                            fromResource(R.mipmap.navi_map_gps_locked)));
        }
        setMarker();
        aMap.setLocationSource(this);
        //显示定位按钮
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
        aMap.setMyLocationEnabled(true);
        //设置显示比例尺
        aMap.getUiSettings().setScaleControlsEnabled(true);
        //logo的位置
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
        //获取到传感器管理对象
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //传感器监听
        sensorM = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//磁场传感器
        sensorS = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器
        //注册监听
        sensorManager.registerListener(this,sensorM,Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this,sensorS,Sensor.TYPE_ACCELEROMETER);
    }

    private void setMarker() {
        mGPSMarker=aMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.b_child_poi_hl))
                .anchor(0.5f,0.5f));
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
                    //位置移动时修改marker
                    mGPSMarker.setPosition(new LatLng(aMapLocation.getLatitude(),
                            aMapLocation.getLongitude()));
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
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_traffic://实时路况
                if (aMap.isTrafficEnabled()==false){
                    Toast.makeText(this,"开启实时路况",Toast.LENGTH_SHORT).show();
                    ivTraffic.setImageResource(R.mipmap.ic_check_on_day);
                    aMap.setTrafficEnabled(true);
                }else {
                    Toast.makeText(this,"关闭实时路况",Toast.LENGTH_SHORT).show();
                    ivTraffic.setImageResource(R.mipmap.ic_check_off_day);
                    aMap.setTrafficEnabled(false);
                }
                break;
            case R.id.iv_zoom_down://缩小地图
                aMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.iv_zoom_up://放大地图
                aMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.iv_direction:
                aMap.showBuildings(true);
                CameraPosition position = new CameraPosition(new LatLng(lat,lon),17,60,0);
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                break;
            case R.id.ll_near://附近
                startActivity(new Intent(this,NearActivity.class));
                break;
            case R.id.ll_way://路线
                startActivity(new Intent(this,RoutePlanActivity.class));
                break;
            case R.id.ll_mine://我的
                break;
        }
    }
    /***/
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
        //注销监听
        sensorManager.unregisterListener(this,sensorM);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            fAcc=event.values.clone();
        }else  if (event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            fMagnetic = event.values.clone();
        }
        //创建一个存储旋转矩阵值的数组
        float[] r = new float[9];
        //目标方向值的数据
        float[] target = new float[3];
        //获取旋转矩阵值的数据
        SensorManager.getRotationMatrix(r,null,fAcc,fMagnetic);
        //获取方向值
        SensorManager.getOrientation(r,target);
        //将方向值转成角度
        float oritation = (float) Math.toDegrees(target[0]);
        if (oritation-x>3){

        }
        x=oritation;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
