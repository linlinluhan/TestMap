package com.zhuoxin.hellomap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

/**
 * Created by 月林 on 2016/12/12.
 */

public class RoutePlanActivity extends Activity implements RouteSearch.OnRouteSearchListener, View.OnClickListener, GeocodeSearch.OnGeocodeSearchListener {
    RouteSearch routeSearch;
    ImageView ivBus,ivCar,ivFoot;
    GeocodeSearch geocodeSearch;
    DriveRouteResult drResult;
    private AutoCompleteTextView actvStart,actvEnd;
    private Marker geoMarker;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private LatLonPoint startPosition,endPosition;
    private LatLonPoint point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_plan);
        initView();
        registerListener();
    }

    private void registerListener() {
        ivFoot.setOnClickListener(this);
        ivCar.setOnClickListener(this);
        ivBus.setOnClickListener(this);
        routeSearch.setRouteSearchListener(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
    }

    private void initView() {
        ivBus = (ImageView) findViewById(R.id.iv_route_bus);
        ivCar = (ImageView) findViewById(R.id.iv_route_car);
        ivFoot = (ImageView) findViewById(R.id.iv_route_onfoot);
        actvEnd = (AutoCompleteTextView) findViewById(R.id.actv_end);
        actvStart = (AutoCompleteTextView) findViewById(R.id.actv_start);

        //初始化routesearch对象
        routeSearch = new RouteSearch(this);
    }


    /**公交出行*/
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }
    /**驾车出行*/
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        //获取到result结果，并解析
        if (i==1000){
            if (driveRouteResult!=null&&driveRouteResult.getPaths()!=null){
                if (driveRouteResult.getPaths().size()>0){
                    drResult = driveRouteResult;
                    DrivePath drivePath = drResult.getPaths().get(0);
                }
            }
        }
    }
    /**步行出行*/
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }
    /**骑行出行*/
    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_route_bus://公交
                getLatlon(actvStart.getText().toString());
                startPosition=point;
                getLatlon(actvEnd.getText().toString());
                endPosition=point;
                break;
            case R.id.iv_route_car://驾车
                setStartEndMarker();
                break;
            case R.id.iv_route_onfoot://步行
                break;
        }
    }
    /**给起点和终点添加marker*/
    private void setStartEndMarker() {

    }

    /**
     * 响应地理编码
     * @param name
     */
    public void getLatlon(String name){
        GeocodeQuery query = new GeocodeQuery(name,"0311");
        geocodeSearch.getFromLocationNameAsyn(query);
    }

    /**
     * 响应逆地理编码
     * @param latLonPoint
     */
    public void getAddress(LatLonPoint latLonPoint){
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint,200,GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }
    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        dissmissProgressDialog();
        if (i==1000){
            if (geocodeResult!=null&&geocodeResult.getGeocodeAddressList()!=null&&
                    geocodeResult.getGeocodeAddressList().size()>0){
                GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                point = address.getLatLonPoint();
                setStartEndMarker();
            }
        }
    }
}
