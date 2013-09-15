package org.pescuma.buildhealth.extractor.performance;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.google.common.base.Strings;

// http://japex.java.net/
public class JapexExtractor extends BaseXMLExtractor {
	
	public JapexExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, "testSuiteReport", filename);
		
		extractSuite(filename, doc.getRootElement(), data);
	}
	
	private void extractSuite(String filename, Element suite, BuildData data) {
		String type = suite.getChildTextTrim("resultUnit", suite.getNamespace());
		
		if ("ms".equalsIgnoreCase(type))
			type = "ms";
		else if ("tps".equalsIgnoreCase(type))
			type = "runsPerS";
		else if ("mbps".equalsIgnoreCase(type))
			// TODO Handle japex.InputFile
			type = "runsPerS";
		else
			// What to do?
			return;
		
		for (Element driver : suite.getChildren("driver", suite.getNamespace()))
			extractDriver(driver, type, data);
	}
	
	private void extractDriver(Element driver, String type, BuildData data) {
		String name = driver.getAttributeValue("name", "<no name>");
		
		String descr = driver.getChildTextTrim("description");
		if (!Strings.isNullOrEmpty(descr))
			name = descr;
		
		for (Element testCase : driver.getChildren("testCase", driver.getNamespace()))
			extractTestCase(testCase, name, type, data);
	}
	
	private void extractTestCase(Element testCase, String driver, String type, BuildData data) {
		String name = testCase.getAttributeValue("name", "<no name>");
		double val = Double.parseDouble(testCase.getChildTextTrim("resultValue", testCase.getNamespace()));
		
		data.add(val, "Performance", "Java", "Japex", type, driver + "/" + name);
	}
}
