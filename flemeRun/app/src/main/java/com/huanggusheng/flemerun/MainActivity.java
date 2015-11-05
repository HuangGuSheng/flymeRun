package com.huanggusheng.flemerun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.leaking.slideswitch.SlideSwitch;

import java.text.DecimalFormat;

import at.markushi.ui.CircleButton;

public class MainActivity extends Activity {



    private TextView txt_totalDistance;
    private TextView txt_totalTime;
    private CircleButton btn_start;
//    private Button btn_history;
    private SharedPreferences pref;
    private Slidingmenu leftMenu;

    private RelativeLayout r1,r2,r3,r4,r5;
    private SlideSwitch switch_speak;

    private boolean isVoiceOpen= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

    }


    /**
     * 初始化界面
     */
    private void initView() {
        txt_totalDistance = (TextView) findViewById(R.id.totalDistance);
        btn_start = (CircleButton) findViewById(R.id.btn_start);
//        btn_history = (Button) findViewById(R.id.btn_history);

        Float total_distance = pref.getFloat("total_distance",0);
        //格式化公里数
        DecimalFormat df = new DecimalFormat("######0.00");      //用于保留两位double小数
        txt_totalDistance.setText(String.valueOf(df.format(total_distance)) + " km");
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始跑步按键响应
                RunAvtivity.actionStart(MainActivity.this,isVoiceOpen);
            }
        });
//        btn_history.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //查看历史轨迹按键响应
//                Intent intent = new Intent(MainActivity.this, ListActivity.class);
//                startActivity(intent);
//            }
//        });
        leftMenu=(Slidingmenu)findViewById(R.id.id_menu);
        initClick();
    }

    public void initClick() {
        r1 = (RelativeLayout) findViewById(R.id.rel_user);
        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看账户信息
            }
        });
        r2 = (RelativeLayout) findViewById(R.id.rel_activity);
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
        r3 = (RelativeLayout) findViewById(R.id.rel_analys);
        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //数据分析，调到图表显示界面
                Intent intent = new Intent(MainActivity.this, AnalyseActivity.class);
                startActivity(intent);
            }
        });
        r4 = (RelativeLayout) findViewById(R.id.rel_fri);
        r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看使用此软件的flyme好友
            }
        });
        r5 = (RelativeLayout) findViewById(R.id.rel_around);
        r5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看周围的人
            }
        });

        switch_speak = (SlideSwitch) findViewById(R.id.switch_speak);
        switch_speak.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                // Do something ,,,
                Toast.makeText(MainActivity.this, "开启语音播报", Toast.LENGTH_SHORT).show();
                isVoiceOpen = true;
            }

            @Override
            public void close() {
                // Do something ,,,
                Toast.makeText(MainActivity.this, "关闭语音播报", Toast.LENGTH_SHORT).show();
                isVoiceOpen = false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //数据加载放这里
        pref = getSharedPreferences("data", MODE_PRIVATE);
        initView();
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
