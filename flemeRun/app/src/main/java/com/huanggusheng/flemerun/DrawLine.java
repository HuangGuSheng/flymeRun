package com.huanggusheng.flemerun;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Huang on 2015/10/24.
 */
public class DrawLine {
    private Context context;
    private BaiduMap mBaiduMap;

    public DrawLine(Context context, BaiduMap baiduMap) {
        this.context = context;
        this.mBaiduMap = baiduMap;
    }

    public void draw(LatLng p1,LatLng p2,LatLng p3) {
        List<LatLng> point = new ArrayList<>();
        point.add(0, p1);
        point.add(1, p2);
        point.add   (2, p3);
        OverlayOptions ooPolyline = new PolylineOptions()
                .width(5)
                .color(R.color.chartreuse)
                .points(point);
        mBaiduMap.addOverlay(ooPolyline);
    }


}
