package com.huami.irun.gpx.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangfan on 16-10-26.
 */
public class DetailJSONDataBean {

    @SerializedName("longitude_latitude")
    public String mLngLat;
    @SerializedName("time")
    public String mTime;
    @SerializedName("trackid")
    public long mTrackId;
    @SerializedName("altitude")
    public String mAltitude;
    @SerializedName("heart_rate")
    public String mHeartRate;
    @SerializedName("gait")
    public String mGait;
}
