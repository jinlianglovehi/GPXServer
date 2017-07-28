package com.huami.irun.gpx;

import com.huami.irun.Global;
import com.huami.irun.gpx.data.DetailJSONDataBean;
import com.huami.irun.gpx.data.SummaryJSONDataBean;
import com.huami.irun.gpx.extension.CadenceParser;
import com.huami.irun.gpx.extension.HeartRateParser;
import com.huami.irun.gpx.parser.modal.*;
import com.huami.irun.gpx.utils.Preconditions;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by zhangfan on 16-10-26.
 */
public class BeanGPXBuilder {

    private static final long MILLI_SECOND_ONE_SECOND = 1000;
    private static final int LAT_LNG_MULTIPLE = 100000000;
    private static final int MMS_PER_METER = 100;
    private static final String METADATA_NAME = "Huami Amazfit Sports Watch";
    private static final String TRACK_NAME = "Sport";
    private static final String TRACK_TYPE_RUNNING = "Running";
    private static final String TRACK_TYPE_WALKING = "Walking";
    private static final String TRACK_TYPE_CROSSING = "Crossing";
    private static final String TRACK_TYPE_INDOOR = "Indoor";
    private static final String TRACK_TYPE_OUTDOOR_RIDING = "OutdoorRiding";
    private static final String TRACK_TYPE_INDOOR_RIDING = "IndoorRiding";
    private long[] mTime;
    private long[] mLat;
    private long[] mLng;
    private float[] mAltitude;
    private int mSize = -1;
    private Logger mLogger = Logger.getLogger("BeanGPXBuilder");
    private short[] mHeartRate;
    private DetailJSONDataBean mDetailBean;

    private SummaryJSONDataBean mSummaryBean;
    public BeanGPXBuilder() {
    }

    private boolean parseHeartRate(DetailJSONDataBean bean) {
        String heartRateStr = bean.mHeartRate;
        if (heartRateStr == null || heartRateStr.isEmpty()) {
            mHeartRate = new short[0];
            return true;
        }
        String[] heartrateSplitStr = heartRateStr.split(";");
        short[] heartRateDatas = new short[heartrateSplitStr.length];
        long[] heartRateDataTime = new long[heartrateSplitStr.length + 1];
        Arrays.fill(heartRateDataTime, -1);
        long lastTimestamp = bean.mTrackId * MILLI_SECOND_ONE_SECOND;
        short lastHeartRate = 0;
        long currentTime = 0;
        short currentHeartRate = 0;
        int i = 0;
        for (String hss : heartrateSplitStr) {
            String[] heartrateInfoStr = hss.split(",");
            if (heartrateInfoStr.length < 2) {
                break;
            }
            try {
                currentTime = Long.parseLong(heartrateInfoStr[0]) * MILLI_SECOND_ONE_SECOND + lastTimestamp;
                currentHeartRate = (short) (Short.parseShort(heartrateInfoStr[1]) + lastHeartRate);
                heartRateDataTime[i] = currentTime;
                heartRateDatas[i] = currentHeartRate;
                lastTimestamp = currentTime;
                lastHeartRate = currentHeartRate;
            } catch (NumberFormatException e) {
                return false;
            }
            i++;
        }
        short[] heartRates = new short[mTime.length];
        int currentDataIndex = 0;
        short heartRate = -1;
        for (int j = 0; j < mTime.length; j++) {
            while (heartRateDataTime[currentDataIndex] != -1 && heartRateDataTime[currentDataIndex] < mTime[j]) {
                heartRate = heartRateDatas[currentDataIndex];
                currentDataIndex++;
            }
            heartRates[j] = heartRate;
        }
        mHeartRate = heartRates;
        return true;
    }

    private boolean parseCadence(DetailJSONDataBean bean) {
        String gait = bean.mGait;
        if (gait == null || gait.isEmpty()) {
            return true;
        }
        String[] gaitSplitStr = gait.split(";");
        if (gaitSplitStr.length == 0) {
            return true;
        }
        long lastTimestamp = 0;
        int count = gaitSplitStr.length;
        long[] time = new long[count + 1];
        Arrays.fill(time, -1);
        int[] stepDiffs = new int[count + 1];
        for (int i = 0; i < gaitSplitStr.length; i++) {
            String[] gaitStr = gaitSplitStr[i].split(",");
            if (gaitStr.length < 3) {
                break;
            } else {
                //解析
                try {
                    //第一个是时间差，第二个是步数差，第三个是步幅
                    long timeStamp = lastTimestamp + Integer.parseInt(gaitStr[0]) * MILLI_SECOND_ONE_SECOND;
                    int stepDiff = Integer.parseInt(gaitStr[1]);
                    time[i] = timeStamp;
                    stepDiffs[i] = stepDiff;
                    lastTimestamp = timeStamp;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        int[] cadences = parseStepDiffToCadence(time, stepDiffs);
        if (cadences == null) {
            return false;
        }
        int currentDataIndex = 0;
        int[] res = new int[mTime.length];
        final long startTime = bean.mTrackId * MILLI_SECOND_ONE_SECOND;
        int cadence = 0;
        for (int i = 0; i < mTime.length; i++) {
            while (time[currentDataIndex] != -1 && time[currentDataIndex] + startTime < mTime[i]) {
                cadence = cadences[currentDataIndex];
                currentDataIndex++;
            }
            res[i] = cadence;
        }
        mCadence = res;
        return true;
    }
    private static final int LIMIT_STEP_POOL = 30;
    private int[] mCadence;

    private int[] parseStepDiffToCadence(long[] times, int[] stepDiffs) {
        if (times == null) {
            return null;
        }
        if (stepDiffs == null) {
            return null;
        }
        Preconditions.checkArgument(times.length == stepDiffs.length, "length of times and step diff should be equal");
        int sumStep = 0;
        long sumTime = 0;
        int tail;
        int[] ret = new int[times.length];
        for (int head = 0; head < times.length; head++) {
            sumStep += stepDiffs[head];
            sumTime = times[head];
            if (head >= LIMIT_STEP_POOL) {
                tail = head - LIMIT_STEP_POOL;
                sumStep -= stepDiffs[tail];
                sumTime -= times[tail];
            }
            ret[head] = sumTime == 0 ? 0 : (int) (sumStep * MILLI_SECOND_ONE_MINUTE / sumTime);
        }
        return ret;
    }

    private static final int MILLI_SECOND_ONE_MINUTE = 60000;

    private boolean parseTime(DetailJSONDataBean bean) {
        String timeStr = bean.mTime;
        if (timeStr.isEmpty()) {
            mTime = new long[0];
            return true;
        }
        String[] timeSplitStr = timeStr.split(";");
        long startTime = bean.mTrackId * MILLI_SECOND_ONE_SECOND;
        int size = timeSplitStr.length;
        int index = 0;
        long currentTime = 0;
        long[] times = new long[size];
        for (String ts : timeSplitStr) {
            if (ts.isEmpty()) {
                return false;
            }
            try {
                long diffTime = Long.parseLong(ts);
                currentTime += diffTime * MILLI_SECOND_ONE_SECOND;
                times[index] = currentTime + startTime;
            } catch (NumberFormatException e) {
                return false;
            }
            if (++index >= size) {
                break;
            }
        }
        mTime = times;
        return true;
    }

    private boolean parseLatLng(DetailJSONDataBean bean) {
        String latLngStr = bean.mLngLat;
        if (latLngStr.isEmpty()) {
            mLat = new long[0];
            mLng = new long[0];
            return true;
        }
        String[] latLngSplitStr = latLngStr.split(";");
        int size = latLngSplitStr.length;
        if (size == 0) {
            return false;
        }
        long[] lats = new long[size];
        long[] lngs = new long[size];
        long currentLat = 0;
        long currentLng = 0;
        int index = 0;
        for (String ll : latLngSplitStr) {
            String[] llSplitStr = ll.split(",");
            if (llSplitStr.length != 2) {
                continue;
            }
            try {
                long diffLat = Long.parseLong(llSplitStr[0]);
                long diffLng = Long.parseLong(llSplitStr[1]);
                currentLat +=  diffLat ;
                currentLng +=  diffLng ;
                lats[index] = currentLat;
                lngs[index] = currentLng;
                index++;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        mLat = lats;
        mLng = lngs;
        return true;
    }

    private boolean parseAltitude(DetailJSONDataBean bean) {
        String altitudeStr = bean.mAltitude;
        if (altitudeStr.isEmpty()) {
            mAltitude = new float[0];
            return true;
        }
        String[] altitudeSplitStr = altitudeStr.split(";");
        int size = altitudeSplitStr.length;
        float[] altitudes = new float[altitudeSplitStr.length];
        int index = 0;
        float altitude;
        for (String as : altitudeSplitStr) {
            if (as.isEmpty()) {
                continue;
            }
            try {
                altitude = Float.parseFloat(as) / MMS_PER_METER;
                altitudes[index] = (altitude < 0 ? 0 : altitude);
            } catch (NumberFormatException e) {
                return false;
            }
            if (++index >= size) {
                break;
            }
        }
        mAltitude = altitudes;
        return true;
    }

    private GPX buildGPX() {
        Preconditions.checkNotNull(mTime, "time should not be null");
        Preconditions.checkNotNull(mLat, "latitude should not be null");
        Preconditions.checkNotNull(mLng, "longitude should not be null");
        Preconditions.checkNotNull(mAltitude, "altitude should not be null");
        Preconditions.checkArgument(
                mTime.length == mLat.length && mTime.length == mLng.length && mTime.length == mAltitude.length,
                "data is not alignment");
        Track track = new Track();
        track.setName(TRACK_NAME);
        String trackType = TRACK_TYPE_RUNNING;
        if (mSummaryBean != null) {
            switch (mSummaryBean.mSportType) {
                case SummaryJSONDataBean.JSON_VALUE_SPORT_TYPE_RUNNING:
                    trackType = TRACK_TYPE_RUNNING;
                    break;
                case SummaryJSONDataBean.JSON_VALUE_SPORT_TYPE_WALKING:
                    trackType = TRACK_TYPE_WALKING;
                    break;
                case SummaryJSONDataBean.JSON_VALUE_SPORT_TYPE_CROSSING:
                    trackType = TRACK_TYPE_CROSSING;
                    break;
                case SummaryJSONDataBean.JSON_VALUE_SPORT_TYPE_INDOOR_RUNNING:
                    trackType = TRACK_TYPE_INDOOR;
                    break;
                case SummaryJSONDataBean.JSON_VALUE_SPORT_TYPE_OUTDOOR_RIDING:
                    trackType = TRACK_TYPE_OUTDOOR_RIDING;
                    break;
                case SummaryJSONDataBean.JSON_VALUE_SPORT_TYPE_INDOOR_RIDING:
                    trackType = TRACK_TYPE_INDOOR_RIDING;
                    break;
            }
        }
        track.setType(trackType);
        int size = mTime.length;
        if (size != 0) {
            TrackSegment segment = new TrackSegment();
            for (int i = 0; i < size; i++) {
                Waypoint waypoint = new Waypoint((double) mLat[i]/LAT_LNG_MULTIPLE,(double) mLng[i]/LAT_LNG_MULTIPLE);
                waypoint.setTime(new Date(mTime[i]));
                waypoint.setElevation(mAltitude[i]);
                if (mHeartRate != null && i < mHeartRate.length && mHeartRate[i] > 0) {
                    HeartRateParser.HeartRate heartRate = new HeartRateParser.HeartRate(mHeartRate[i]);
                    waypoint.addExtensionData(HeartRateParser.HEART_RATE_PARSER_ID, heartRate);
                }
                if (mCadence != null && i < mCadence.length && mCadence[i] >= 0) {
                    CadenceParser.Cadence cadence = new CadenceParser.Cadence(mCadence[i]);
                    waypoint.addExtensionData(CadenceParser.CADENCE_PARSER_ID, cadence);
                }
                segment.addWaypoint(waypoint);
            }
            track.addTrackSegment(segment);
        }

        GPX gpx = new GPX();
//        gpx.setCreator(Global.DEVICE_NAME + " with Barometer");
        gpx.setCreator(Global.DEVICE_NAME);
        gpx.addTrack(track);
        Metadata metadata = new Metadata();
        metadata.setName(METADATA_NAME);
        metadata.setTime(new Date());
        gpx.setMetadata(metadata);
        return gpx;
    }

    public BeanGPXBuilder setSportDetailBean(DetailJSONDataBean bean) {
        mDetailBean = bean;
        return this;
    }

    public BeanGPXBuilder setSportSummaryBean(SummaryJSONDataBean bean) {
        mSummaryBean = bean;
        return this;
    }

    public GPX build() {
        if (!parseTime(mDetailBean)) {
            return null;
        }
        if (!parseLatLng(mDetailBean)) {
            return null;
        }
        if (!parseAltitude(mDetailBean)) {
            return null;
        }
        if (!parseHeartRate(mDetailBean)) {
            return null;
        }
        if (!parseCadence(mDetailBean)) {
            return null;
        }
        GPX gpx = null;
        try {
            gpx = buildGPX();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gpx;
    }
}
