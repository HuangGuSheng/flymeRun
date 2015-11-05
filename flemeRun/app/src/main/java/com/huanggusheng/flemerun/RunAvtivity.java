package com.huanggusheng.flemerun;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import at.markushi.ui.CircleButton;

/**
 * Created by Huang on 2015/10/24.
 */
public class RunAvtivity extends Activity implements TextToSpeech.OnInitListener {

    private BaiduMap mBaiduMap;
    private MapView mMapView;

    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = null;
    private MyLocationConfiguration.LocationMode mLocationMode;

    private LocationManager mLocationManager;           //用来查看GPS状态
    private boolean isOpenGPS = false;          //GPS是否开启
    private double distance;        //距离
    private int duration;       //跑步时长
    private List<LatLng> mLatlngList;
    private double latitude;
    private double longitude;
    private boolean isFirstLocation = true;      //是否第一次定位
    private boolean isRunning = false;   //是否在跑步
    private boolean isVoiceOpen = true;     //是否开启语音播报

    private CircleButton mCircleButton = null;
    private TextView txt_currentspeed, txt_distance,txt_time,txt_averagespeed;
    private BitmapDescriptor bitmap;
    private DrawLine drawLine;
    private TextToSpeech tts = null;

    private long press_up_time = 0;

    private Button button;
    /*
    数据库相关
     */
    private DbHelper helper;
    private SQLiteDatabase writer;

    private StringBuilder latLngs = new StringBuilder();

    private FileOperate operate = new FileOperate(this);
    private String file_name = getCurrentDate();

    DecimalFormat df = new DecimalFormat("######0.00");      //用于保留两位double小数

    /*
    测试
     */
    private TextView textView_duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.run_mapview);

        Intent intent = getIntent();
        isVoiceOpen = intent.getBooleanExtra("voiceState", true);

        /*
        把file_name写入数据库
         */

        initView();
        initLocationListener();
        mLatlngList = new ArrayList<>();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(19f);       //地图缩放比例
        mBaiduMap.setMapStatus(msu);
        mMapView.showZoomControls(false);            //隐藏缩放按钮
        bitmap = BitmapDescriptorFactory.
                fromResource(R.mipmap.location1);
        txt_currentspeed = (TextView) findViewById(R.id.curret_speed);
        txt_distance = (TextView) findViewById(R.id.distance);
        txt_time = (TextView) findViewById(R.id.time);
//        txt_averagespeed = (TextView) findViewById(R.id.average_speed);
        textView_duration = (TextView)findViewById(R.id.duration);
        mCircleButton = (CircleButton) findViewById(R.id.strart_Btn);
        mCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getGPSState()) {
                    if(isRunning == false) {
                        isRunning = true;
                        mCircleButton.setImageResource(R.mipmap.btn_pause);
//                        ObjectAnimator.ofFloat(mCircleButton,"translationY",0F,-20F).
//                                setDuration(80).start();
                        ObjectAnimator.ofFloat(mCircleButton, "translationY", 0F, 200F).
                                setDuration(250).start();
                    }
                    else{
                        isRunning = false;
                        mCircleButton.setImageResource(R.mipmap.btn_run_1);
                        ObjectAnimator.ofFloat(mCircleButton,"translationY",200f,40f).
                                setDuration(350).start();
                    }
                } else {
                    Toast.makeText(RunAvtivity.this, "请先开启GPS，并等待半分钟卫星定位~",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        button = (Button) findViewById(R.id.btn_stop);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止
                pushToDB(file_name);
                operate.saveToFile(file_name,latLngs.toString());
                isRunning = false;
                SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Float old_distance = pref.getFloat("total_distance",0);
                double temp_distance;
                temp_distance = distance/1000;
                old_distance = old_distance+Float.parseFloat(temp_distance+"");
                editor.putFloat("total_distance",old_distance);
                editor.commit();
                finish();
            }
        });

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    if (tts.isLanguageAvailable(Locale.CHINESE) >= 0) {
                        tts.setPitch(0.8f);
                        tts.setSpeechRate(1.1f);
                    }
                }
            }

        });
    }


    /**
     * 初始化监听器
     */
    private void initLocationListener(){
        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
        myListener =  new MyLocationListener();
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        initLocationSetting();
        mLocationClient.start();
    }
    /**
     * 设置定位参数
     */
    private void initLocationSetting(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mLocationClient.setLocOption(option);
    }


    /**
     * 回到所在位置
     */
    private void myLocation(double latitude,double lontitude) {
        LatLng latLng = new LatLng(latitude,lontitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    /**
     * 获取GPS状态,是否开启
     * @return
     */
    private boolean getGPSState() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isOpenGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isOpenGPS;
    }

    @Override
    public void onInit(int status) {
        if(status==TextToSpeech.SUCCESS){
            tts.setLanguage(Locale.CHINESE);
        }

    }


    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //receve localdata
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            if(isFirstLocation == true){
                myLocation(latitude,longitude);
                isFirstLocation = false;
            }
            if (isRunning == true && isOpenGPS == true) {
                LatLng latLng = new LatLng(latitude, longitude);
                /*
                往文件中添加坐标信息
                 */
                latLngs.append(latitude);
                latLngs.append(",");
                latLngs.append(longitude);
                latLngs.append("/");
                myLocation(latitude,longitude);
                mLatlngList.add(latLng);
                //往本地写数据
                duration = mLatlngList.size();
                //这里放个倒计时
                if (mLatlngList.size() > 3) {
                    drawLine = new DrawLine(getApplicationContext(), mBaiduMap);
                    drawLine.draw(mLatlngList.get(duration - 3), mLatlngList.get(duration - 2),
                            mLatlngList.get(duration -1));
                    getDistance(mLatlngList.get(duration -2),mLatlngList.get(duration -1));
                    txt_currentspeed.setText("时速:"+String.valueOf(df.format(bdLocation.getSpeed()))+" km/h");
                    txt_distance.setText("距离: "+String.valueOf(df.format(distance/1000))+" km");
//                    txt_averagespeed.setText("平均速度" + String.valueOf(df.format(distance / duration *3.6)) + " km/h");
//                    textView_duration.setText("时间: "+duration+"s");
                    formatTime(duration);
                    if((int)distance%50 == 0 && (int)distance != 0 && isVoiceOpen == true){
                        voiceOut();
                    }
                    }

            }
            mBaiduMap.setMyLocationEnabled(true);

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(bdLocation.getDirection()).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();

            mBaiduMap.setMyLocationData(locData);
            MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode,
                    true, bitmap);
            mBaiduMap.setMyLocationConfigeration(config);

        }
        public void getDistance(LatLng p1,LatLng p2){           //计算运动距离
            distance += DistanceUtil. getDistance(p1, p2);
        }

        /**
         * 转化时分秒
         */
        public void formatTime(int number) {
            int hour,min,sec;
            if(number > 3600) {
                hour = number / 3600;
                min = number/60;
                sec = number%60;
                textView_duration.setText("时间:"+hour+"h"+min+"min"+sec+"s");
            }else if(number > 60){
                min = number/60;
                sec = number%60;
                textView_duration.setText("时间:"+min+"min"+sec+"s");
            }else{
                textView_duration.setText("时间:"+number+"s");
            }
        }

    }

    /*
    获取当前日期，作为文件名
     */
    public String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
        Date current_date = new Date(System.currentTimeMillis());
        return formatter.format(current_date);
    }

    /*
    把文件名写入到本地数据库
     */
    public boolean pushToDB(String file_name){
        helper = new DbHelper(this,"flymeRun.db",null,1);
        writer = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        double speed = distance/duration*3.6;
        int hour = duration/3600;
        duration = duration%3600;
        int min = duration/60;
        duration = duration%60;
        int sec = duration;
        //写数据
        values.put(DbHelper.COLUMN_DATE,file_name);
        values.put(DbHelper.COLUMN_DURATION,hour+"时"+min+"分"+sec+"秒");
        String db_distance = String.valueOf(df.format(distance/1000));
        String db_speed = String.valueOf(df.format(distance/duration*3.6));
        values.put(DbHelper.COLUMN_DISTANCE,db_distance);
        values.put(DbHelper.COLUMN_SPEED,db_speed);
        long resultId = writer.insert(DbHelper.TABLE_NAME,null,values);
        if (resultId>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP :
                if (System.currentTimeMillis() - press_up_time<500){
                    //语音提示
                    voiceOut();
                }else{
                    press_up_time = System.currentTimeMillis();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /*
    语音提示
     */
    private void voiceOut() {

        //boolean ttsIsInit = true;
        if (tts!=null ) {
            Log.e("语音播报", String.valueOf((int) distance) + "米");
            tts.speak("你跑了" + String.valueOf((int) distance) + "米", TextToSpeech.QUEUE_ADD, null);
        }
    }

    public static void actionStart(Context context, boolean isVoiceOpen) {
        Intent intent = new Intent(context, RunAvtivity.class);
        intent.putExtra("voiceState", isVoiceOpen);
        context.startActivity(intent);
        Log.e("VoiceState", String.valueOf(isVoiceOpen));
    }
}

