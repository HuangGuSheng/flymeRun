package com.huanggusheng.flemerun;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Huang on 2015/10/24.
 */
public class ListActivity extends Activity implements AdapterView.OnItemClickListener {

    private List<Records> list;
    private RecordsAdapter recordsAdapter;
    private ListView listView;

    public DbHelper dbHelper;
    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.history_list);
        initData();
        recordsAdapter = new RecordsAdapter(ListActivity.this, R.layout.item_history, list);
        listView = (ListView) findViewById(R.id.listview_history);
        listView.setAdapter(recordsAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initData() {
        list = new ArrayList<>();

        Records records;
        dbHelper = new DbHelper(this,"flymeRun.db",null,1);
        db = dbHelper.getReadableDatabase();
        //查询数据库
        Cursor cursor = db.query(DbHelper.TABLE_NAME,null,null,null,null,null,"id desc");
        if(cursor.moveToFirst()){
            do{
                //遍历cursor对象
                String date = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DATE));
                String duration = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DURATION));
                String distance = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DISTANCE));
                String speed = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_SPEED));
                records = new Records();
                records.setDate(date);
                records.setDuration(duration);
                records.setDistance(Double.parseDouble(distance));
                records.setSpeed(Double.parseDouble(speed));
                list.add(records);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Records record = list.get(position);
        Intent intent = new Intent(ListActivity.this,RecordsMapActivity.class);
        intent.putExtra("file_name",record.getDate());
        startActivity(intent);
    }
}
