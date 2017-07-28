package com.huami.irun.gpx.extension;

import com.huami.irun.gpx.parser.extension.IExtensionParser;
import com.huami.irun.gpx.utils.Preconditions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by zhangfan on 16-11-1.
 */
public class HeartRateParser implements IExtensionParser {
    public static final String HEART_RATE_PARSER_ID = "com.huami.sport.heart_rate_parser";
    public static final String XMLNS = "http://www.cluetrust.com/XML/GPXDATA/1/0";
    public static final String NAME = "heartrate";

    public static class HeartRate {

        public HeartRate(short mHeartRate) {
            this.mHeartRate = mHeartRate;
        }

        private final short mHeartRate;

    }

    @Override
    public String getNameSpace() {
        return XMLNS;
    }

    @Override
    public String getId() {
        return HEART_RATE_PARSER_ID;
    }

    @Override
    public Object parseExtensions(Node node) {
        return null;
    }

    @Override
    public void writeExtensions(String nsPrefix, Object obj, Node node, Document doc) {
        Preconditions.checkNotNull(obj, "obj should not be null");
        Preconditions.checkArgument(obj instanceof HeartRate, "obj should instance of heart rate");
        HeartRate hr = (HeartRate) obj;
        String name = NAME;
        Element hrElement = doc.createElement(name);
        hrElement.setTextContent("" + hr.mHeartRate);
        node.appendChild(hrElement);
    }
}
