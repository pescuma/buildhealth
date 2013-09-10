package org.pescuma.buildhealth.extractor.unittest;

import static com.google.common.base.Objects.*;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

/**
 * Based on hudson.tasks.junit.SuiteResult by Kohsuke Kawaguchi
 */
// http://junit.org/
public class JUnitExtractor extends BaseXMLExtractor {
	
	private final String language;
	private final String tool;
	
	public JUnitExtractor(PseudoFiles files) {
		this(files, "Java", "JUnit");
	}
	
	public JUnitExtractor(PseudoFiles files, String language, String tool) {
		super(files);
		this.language = language;
		this.tool = tool;
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		for (Element suite : findElementsXPath(doc, "//testsuite"))
			extractSuite(filename, suite, data);
	}
	
	private void extractSuite(String filename, Element suite, BuildData data) {
		// some user reported that name is null in their environment.
		// see http://www.nabble.com/Unexpected-Null-Pointer-Exception-in-Hudson-1.131-tf4314802.html
		String name = suite.getAttributeValue("name", firstNonNull(filename, "(no name)"));
		
		String pkg = suite.getAttributeValue("package", "");
		
		// Optional ID attribute of a test suite. E.g., Eclipse plug-ins tests always have the name 'tests' but a
		// different id.
		String id = suite.getAttributeValue("id", "");
		
		if (!pkg.isEmpty())
			name = pkg + "." + name;
		if (!id.isEmpty())
			name += "." + id;
		
		// according to junit-noframes.xsl l.229, this happens when the test class failed to load
		extractTestType(name, "<init>", suite.getChild("error"), "error", data);
		
		for (Element testcase : suite.getChildren("testcase"))
			extractTest(name, testcase, data);
	}
	
	private void extractTest(String suite, Element testcase, BuildData data) {
		String name = testcase.getAttributeValue("name", "");
		
		// https://hudson.dev.java.net/issues/show_bug.cgi?id=1233 indicates that
		// when <testsuites> is present, we are better off using @classname on the
		// individual testcase class.
		String classname = testcase.getAttributeValue("classname", "");
		
		// According to http://www.nabble.com/NPE-(Fatal%3A-Null)-in-recording-junit-test-results-td23562964.html
		// there's some odd-ball cases where testClassName is null but
		// @name contains fully qualified name.
		if (classname.isEmpty()) {
			int pos = name.lastIndexOf('.');
			if (pos >= 0) {
				classname = name.substring(0, pos);
				name = name.substring(pos + 1);
			}
		}
		
		// https://hudson.dev.java.net/issues/show_bug.cgi?id=1463 indicates that
		// @classname may not exist in individual testcase elements. We now
		// also test if the testsuite element has a package name that can be used
		// as the class name instead of the file name which is default.
		if (classname.isEmpty())
			classname = suite;
		
		double time = parseTime(testcase.getAttributeValue("time", ""));
		if (time >= 0)
			data.add(time, "Unit test", language, tool, "time", classname, name);
		
		if (extractTestType(classname, name, testcase.getChild("skipped"), "skipped", data))
			return;
		if (extractTestType(classname, name, testcase.getChild("error"), "error", data))
			return;
		if (extractTestType(classname, name, testcase.getChild("failure"), "failed", data))
			return;
		
		data.add(1, "Unit test", language, tool, "passed", classname, name);
	}
	
	private boolean extractTestType(String classname, String name, Element el, String type, BuildData data) {
		if (el == null)
			return false;
		
		data.add(1, "Unit test", language, tool, type, classname, name, el.getAttributeValue("message", ""),
				el.getTextTrim());
		return true;
	}
	
	private double parseTime(String time) {
		if (time.isEmpty())
			return -1;
		
		time = time.replace(",", "");
		
		try {
			return Double.parseDouble(time);
		} catch (NumberFormatException e) {
		}
		
		try {
			return new DecimalFormat().parse(time).doubleValue();
		} catch (ParseException x) {
		}
		
		return -1;
	}
}
