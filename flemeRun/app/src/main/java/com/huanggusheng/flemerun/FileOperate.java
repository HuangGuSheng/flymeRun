package com.huanggusheng.flemerun;

import android.app.Activity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by admin on 2015/10/24.
 */
public class FileOperate{
    /*
        file_name 文件名
        inputData 文件信息
     */
    private Context mContext;

    public FileOperate(Context context){
        mContext = context;
    }
    public void saveToFile(String file_name, String inputData){
        BufferedWriter writer = null;
        try{
            FileOutputStream out = mContext.openFileOutput(file_name, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputData);
//            Toast.makeText(mContext,inputData,Toast.LENGTH_SHORT).show();
            Log.e("writeing", inputData);
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(writer != null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public String readFile(String file_name){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try{
            in = mContext.openFileInput(file_name);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine()) != null){
                content.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(mContext, "文件读取有错", Toast.LENGTH_SHORT).show();
        }finally{
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
//        Toast.makeText(mContext,content.toString(),Toast.LENGTH_SHORT).show();
        Log.e("reading", content.toString());
        return content.toString();
    }

}
