package org.pescuma.buildhealth.extractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

public class JDomUtil {
	
	public static Document parse(File file) throws JDOMException, IOException {
		return createBuilder().build(file);
	}
	
	public static Document parse(InputStream input) throws JDOMException, IOException {
		return createBuilder().build(input);
	}
	
	private static SAXBuilder createBuilder() {
		SAXBuilder sax = new SAXBuilder(XMLReaders.NONVALIDATING);
		sax.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		return sax;
	}
	
}
