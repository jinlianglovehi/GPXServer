package com.huami.irun;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.huami.irun.gpx.BeanGPXBuilder;
import com.huami.irun.gpx.data.DetailJSONDataBean;
import com.huami.irun.gpx.data.SummaryJSONDataBean;
import com.huami.irun.gpx.extension.CadenceParser;
import com.huami.irun.gpx.extension.HeartRateParser;
import com.huami.irun.gpx.parser.GPXWriter;
import com.huami.irun.gpx.parser.modal.GPX;
import com.huami.irun.gpx.utils.Preconditions;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by zhangfan on 16-10-26.
 */
public class JSONToGPXWriter {
    private Logger logger = Logger.getLogger("JSONToGPXWriter");
    private GPXWriter mGPXWriter = new GPXWriter();

    public JSONToGPXWriter() {
        mGPXWriter.addExtensionParser(new HeartRateParser());
        mGPXWriter.addExtensionParser(new CadenceParser());
    }

    //    public boolean writeTo(String summaryJSON, String detailJSON, File outFile) {
//        Preconditions.checkNotNull(outFile, "file should not be null");
//        try {
//            FileOutputStream outputStream = new FileOutputStream(outFile);
//            return writeTo(summaryJSON, detailJSON, outputStream);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public boolean writeTo(int sportType, String detailJSON, OutputStream out) {
        SummaryJSONDataBean summaryBean = new SummaryJSONDataBean();
        summaryBean.mSportType = sportType;
        Gson gson = new Gson();
        DetailJSONDataBean detailBean = gson.fromJson(detailJSON, DetailJSONDataBean.class);
        return writeToInternal(summaryBean, detailBean, out);
    }

    private boolean writeToInternal(SummaryJSONDataBean summaryBean, DetailJSONDataBean detailBean, OutputStream out) {
        BeanGPXBuilder beanGPXBuilder = new BeanGPXBuilder();
        GPX gpx = beanGPXBuilder.setSportDetailBean(detailBean).setSportSummaryBean(summaryBean).build();
        if (gpx == null) {
            return false;
        }
        try {
            mGPXWriter.writeGPX(gpx, out);
            return true;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean writeTo(String summaryJSON, String detailJSON, OutputStream out) {
        Preconditions.checkNotNull(detailJSON, "json should not be null");
        Preconditions.checkNotNull(out, "output stream should not be null");
        Gson gson = new Gson();
        try {
            DetailJSONDataBean bean = gson.fromJson(detailJSON, DetailJSONDataBean.class);
            if (bean == null) {
                logger.warning("cannot parse json : " + detailJSON);
                return false;
            }
            SummaryJSONDataBean summaryJSONDataBean = null;
            if (summaryJSON != null) {
                summaryJSONDataBean = gson.fromJson(summaryJSON, SummaryJSONDataBean.class);
            }
            return writeToInternal(summaryJSONDataBean, bean, out);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        final String inputPath = "/home/zhangfan/input";
        try {
            FileInputStream input = new FileInputStream(inputPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            FileOutputStream out = new FileOutputStream(new File("/home/zhangfan/out.gpx"));
            new JSONToGPXWriter().writeTo(1, sb.toString(), out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
