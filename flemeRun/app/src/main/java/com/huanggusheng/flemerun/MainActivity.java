package com.huanggusheng.flemerun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class MainActivity extends Activity {



    private TextView txt_totalDistance;
    private TextView txt_totalTime;
    private Button btn_start;
    private Button btn_history;
    private SharedPreferences pref;
    private Slidingmenu leftMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        txt_totalDistance = (TextView) findViewById(R.id.totalDistance);
        txt_totalTime = (TextView) findViewById(R.id.totalTime);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_history = (Button) findViewById(R.id.btn_history);

        Float total_distance = pref.getFloat("total_distance",0);
        total_distance = total_distance/1000;
        //格式化公里数
        DecimalFormat df = new DecimalFormat("######0.00");      //用于保留两位double小数
        txt_totalDistance.setText(String.valueOf(df.format(total_distance))+"米");
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始跑步按键响应
                Intent intent = new Intent(MainActivity.this, RunAvtivity.class);
                startActivity(intent);
            }
        });
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看历史轨迹按键响应
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
        leftMenu=(Slidingmenu)findViewById(R.id.id_menu);
    }


    public void toggleMenu(View view)
    {
        leftMenu.toggle();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //数据加载放这里
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
