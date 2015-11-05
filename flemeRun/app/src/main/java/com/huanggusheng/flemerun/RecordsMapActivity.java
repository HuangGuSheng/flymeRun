package com.huanggusheng.flemerun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Huang on 2015/10/24.
 */
public class RecordsMapActivity extends Activity{

    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private List<LatLng> latLngList;
    private FileOperate operate;
    private String file_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.record_mapview);
        Intent intent = getIntent();
        file_name = intent.getStringExtra("file_name");
        operate = new FileOperate(this);
        initData();
        initView();
        draw();
    }

    /**
     * 得到被点击的历史记录的List
     */
    public void initData() {
        latLngList = new ArrayList<>();
        //从本地文件获取数据
        String data = operate.readFile(file_name);
        String[] datas = data.split("/");
        String[] temp;
        LatLng latLng_cell;
        for (String latLng : datas){
            if (latLng.length()>1){
                temp = latLng.split(",");
                latLng_cell = new LatLng(Double.parseDouble(temp[0]),Double.parseDouble(temp[1]));
                latLngList.add(latLng_cell);
            }
        }
    }

    /**
     * 初始化界面
     */
    public void initView() {
        mMapView = (MapView) findViewById(R.id.record_map);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(19f);       //地图缩放比例
        mBaiduMap.setMapStatus(msu);
        mMapView.showZoomControls(false);            //隐藏缩放按钮
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLngList.get(0));
        mBaiduMap.animateMapStatus(mapStatusUpdate);        //地图定位到轨迹的第一个点
    }

    /**
     * 画出历史轨迹
     */
    public void draw() {
        DrawLine drawLine = new DrawLine(getApplicationContext(), mBaiduMap);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 2; i < latLngList.size(); i++) {
            drawLine.draw(latLngList.get(i-2),latLngList.get(i-1),latLngList.get(i));
        }
    }
}
