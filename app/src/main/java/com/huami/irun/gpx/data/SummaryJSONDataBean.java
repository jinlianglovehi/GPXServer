package com.huami.irun.gpx.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangfan on 16-10-26.
 */
public class SummaryJSONDataBean {
    public static final int JSON_VALUE_SPORT_TYPE_RUNNING = 1;
    public static final int JSON_VALUE_SPORT_TYPE_WALKING = 6;
    public static final int JSON_VALUE_SPORT_TYPE_CROSSING = 7;
    public static final int JSON_VALUE_SPORT_TYPE_INDOOR_RUNNING = 8;
    public static final int JSON_VALUE_SPORT_TYPE_OUTDOOR_RIDING = 9;
    public static final int JSON_VALUE_SPORT_TYPE_INDOOR_RIDING = 10;

    @SerializedName("type")
    public int mSportType;
}
