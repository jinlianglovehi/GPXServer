package com.huami.irun.test;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TabHost;

import com.google.gson.Gson;
import com.huami.irun.JSONToGPXWriter;
import com.huami.irun.R;
import com.huami.irun.gpx.data.DetailJSONDataBean;
import com.huami.irun.gpx.data.SummaryJSONDataBean;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by jinliang on 17-7-28.
 */
public class TestActivity extends Activity {
    private static final String TAG = TestActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i(TAG," run ") ;
                DetailJSONDataBean detailJSONDataBean  =getDetalDataBean();
                if(detailJSONDataBean!=null){
                    writeDataToFile("test_modify_after.gpx",detailJSONDataBean) ;
                }else{
                    Log.i(TAG," detalJsonDataBean is null ");
                }

            }
        }).start();
    }

    public DetailJSONDataBean getDetalDataBean(){

        Log.i(TAG," getDetalDataBean " ) ;
        String fileName = "3000034750_1501170687.txt" ;
        try{
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String text = new String(buffer);
            Log.i(TAG," FileContent:"+ text.length());
           return  new Gson().fromJson(text,DetailJSONDataBean.class);

        }catch(Exception e){
            e.printStackTrace();
            Log.i(TAG," getDetalDataBean error  " ) ;
        }
        return null;

    };

    public void writeDataToFile(String fileName, DetailJSONDataBean detailJSONDataBean){
        try{
            FileOutputStream outputStream =openFileOutput(fileName, MODE_PRIVATE);
            JSONToGPXWriter jsonToGPXWriter = new JSONToGPXWriter() ;
            int sportType = SummaryJSONDataBean.JSON_VALUE_SPORT_TYPE_CROSSING;
            String detailJsonBeanStr = new Gson().toJson(detailJSONDataBean);
            jsonToGPXWriter.writeTo( sportType,detailJsonBeanStr,outputStream);
        } catch(Exception e){
            e.printStackTrace();
        }

    }
}

