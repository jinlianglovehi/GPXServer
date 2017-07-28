package com.huami.irun.gpx.extension;

import com.huami.irun.gpx.parser.extension.IExtensionParser;
import com.huami.irun.gpx.utils.Preconditions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by zhangfan on 16-11-2.
 */
public class CadenceParser implements IExtensionParser {
    public static final String CADENCE_PARSER_ID = "com.huami.sport.cadence_parser";
    public static final String XMLNS = "http://www.cluetrust.com/XML/GPXDATA/1/0";
    public static final String NAME = "cadence";

    public static class Cadence {

        public Cadence(int cadence) {
            this.mValue = cadence;
        }

        private final int mValue;

    }

    @Override
    public String getNameSpace() {
        return XMLNS;
    }

    @Override
    public String getId() {
        return CADENCE_PARSER_ID;
    }

    @Override
    public Object parseExtensions(Node node) {
        return null;
    }

    @Override
    public void writeExtensions(String nsPrefix, Object obj, Node node, Document doc) {
        Preconditions.checkNotNull(obj, "obj should not be null while write cadence extensions");
        Preconditions.checkArgument(obj instanceof Cadence, "obj should instance of cadence");
        Cadence cadence = (Cadence) obj;
        String name = NAME;
        Element cadenceElement = doc.createElement(name);
        //change to revolutions per minute
        cadenceElement.setTextContent("" + cadence.mValue / 2);
        node.appendChild(cadenceElement);
    }
}
