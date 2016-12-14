package com.zhuoxin.hellomap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 月林 on 2016/12/8.
 */

public class NearActivity extends Activity implements View.OnClickListener, TextWatcher, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, PoiSearch.OnPoiSearchListener {
    private AMap aMap;
    private AutoCompleteTextView searchText;//自动提示搜索框
    private String keyWord="";//要输入的poi搜索关键字
    private ProgressDialog dialog=null;//搜索时进度条
    private PoiResult poiResult;//poi返回的结果
    private int currentPage=0;
    private PoiSearch.Query query;//poi查询条件类
    private PoiSearch poiSearch;//POI搜索

    /**图片*/
    private int[] imgs={R.mipmap.shp_hw_meishi,R.mipmap.shp_hw_jiudian,R.mipmap.shp_hw_jingdian,
    R.mipmap.shp_hw_yinhang,R.mipmap.shp_hw_gongjiaozhan,R.mipmap.shp_hw_jiayouzhan,
    R.mipmap.shp_hw_shoucangdian,R.mipmap.shp_hw_gengduo};
    /**图片下的标题*/
    private String[] imgsName={"美食","酒店","景点","银行","公交站","加油站","收藏点","更多"};
    GridView gv;
    List<Map<String,Object>> list;
    SimpleAdapter adapter;
    ImageView ivLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_interest);
        gv = (GridView) findViewById(R.id.gv);
        ivLeft = (ImageView) findViewById(R.id.iv_title_left);
        ivLeft.setOnClickListener(this);
        list = new ArrayList<>();
        getData();
        String[] from={"image","name"};
        int[] to={R.id.iv_gv_item,R.id.tv_gv_item};
        //简单适配器
        adapter = new SimpleAdapter(this,list,R.layout.gridview_item,from,to);
        gv.setAdapter(adapter);
        //gridview的单项点击事件
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        setUp();
    }

    private void setUp() {
        searchText = (AutoCompleteTextView) findViewById(R.id.actv_near_search);
        searchText.addTextChangedListener(this);//添加文本框监听事件
        aMap.setOnMarkerClickListener(this);//添加点击marker监听事件
        aMap.setInfoWindowAdapter(this);//添加显示infowindow监听事件
    }
    public void searchText(){

    }
    /**显示进度框*/
    private void showProgressDialog(){
        if (dialog==null){
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setMessage("正在搜索:\n"+keyWord);
            dialog.show();
        }
    }
    /**隐藏进度框*/
    private void dismissProgressDialog(){
        if (dialog!=null){
            dialog.dismiss();
        }
    }
    private void doSearchQuery(){
        showProgressDialog();//显示进度框
        currentPage=0;
        //第一个参数是搜索字符串，第二个是获取到
        query = new PoiSearch.Query(keyWord,"",searchText.getText().toString());
        query.setPageSize(10);//设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);//设置查看第一页

        poiSearch = new PoiSearch(this,query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    /**将图片和标题添加到集合中*/
    private List<Map<String,Object>> getData() {
        for (int i=0;i<imgs.length;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("image",imgs[i]);
            map.put("name",imgsName[i]);
            list.add(map);
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_title_left:
                finish();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    /**点击marker的监听回调方法*/
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }
    /**InfoWindow事件监听的回调方法*/
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
    /**poi查询*/
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
//        if (i==1000){
//            if (poiResult!=null&&)
//        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

}
