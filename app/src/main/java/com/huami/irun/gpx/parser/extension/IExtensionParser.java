package com.huami.irun.gpx.parser.extension;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface IExtensionParser {

	public String getNameSpace();

	public String getId();

	public Object parseExtensions(Node node);

	public void writeExtensions(String nsPrefix, Object obj, Node node, Document doc);

}
