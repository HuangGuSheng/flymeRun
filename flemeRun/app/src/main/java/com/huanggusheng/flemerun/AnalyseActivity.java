package com.huanggusheng.flemerun;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.StackedBarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.DecimalFormat;

/**
 * Created by admin on 2015/10/25.
 */
public class AnalyseActivity extends Activity {

    private SQLiteDatabase reader;
    private DbHelper helper;
    private DecimalFormat df = new DecimalFormat("######0.00");      //用于保留两位double小数


    /*
    记录红绿蓝次数
     */
    private int red = 0;
    private int green = 0;
    private int blue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.analyse_layout);
        final ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);
        PieChart mPieChart = (PieChart) findViewById(R.id.piechart);

        ValueLineSeries line_series = new ValueLineSeries();
        line_series.setColor(0xFF56B7F1);

        /*
        读取数据库
         */
        helper = new DbHelper(this,"flymeRun.db",null,1);
        reader = helper.getReadableDatabase();
        Cursor cursor = reader.query(DbHelper.TABLE_NAME,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                //遍历cursor对象
                String date = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DATE));
                String duration = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DURATION));
                String distance = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DISTANCE));
                String speed = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_SPEED));
                float float_distance = Float.parseFloat(distance);
                if (float_distance<0.1){
                    red++;
                }else if (float_distance>=0.1&&float_distance<0.2){
                    green++;
                }else if (float_distance>=0.2){
                    blue++;
                }
                line_series.addPoint(new ValueLinePoint(date, Float.parseFloat(df.format(Double.parseDouble(distance)))));
            }while(cursor.moveToNext());
        }
        cursor.close();

        mPieChart.addPieSlice(new PieModel("0-0.1km", red, Color.parseColor("#ff0000")));
        mPieChart.addPieSlice(new PieModel("0.1-0.2km", green, Color.parseColor("#00ff00")));
        mPieChart.addPieSlice(new PieModel(">0.2km", blue, Color.parseColor("#0000ff")));
        mPieChart.startAnimation();

        mCubicValueLineChart.addSeries(line_series);
        mCubicValueLineChart.setShowDecimal(true);
        mCubicValueLineChart.setLegendTextSize(10);
        mCubicValueLineChart.startAnimation();
        mCubicValueLineChart.setClickable(true);
    }
}
