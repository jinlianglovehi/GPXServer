package com.huami.irun.gpx.parser;

import com.huami.irun.gpx.parser.extension.IExtensionParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;


class BaseGPX {

	protected final SimpleDateFormat xmlDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	protected final ArrayList<IExtensionParser> extensionParsers = new ArrayList<IExtensionParser>();

	/**
	 * Adds a new extension parser to be used when parsing a gpx steam
	 *
	 * @param parser
	 *            an instance of a {@link IExtensionParser} implementation
	 */
	public void addExtensionParser(IExtensionParser parser) {
		this.extensionParsers.add(parser);
	}

	/**
	 * Removes an extension parser previously added
	 *
	 * @param parser
	 *            an instance of a {@link IExtensionParser} implementation
	 */
	public void removeExtensionParser(IExtensionParser parser) {
		this.extensionParsers.remove(parser);
	}

	public BaseGPX() {
		//Should be UTC time.
		xmlDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
}
