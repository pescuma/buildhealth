package org.pescuma.buildhealth.extractor.unittest;

import static org.pescuma.buildhealth.utils.ObjectUtils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// http://msdn.microsoft.com/en-us/library/ms182486.aspx
public class MSTestExtractor extends BaseXMLExtractor {
	
	private final Pattern durationFormat = Pattern.compile("(\\d+):(\\d+):(\\d+).(\\d+)");
	
	public MSTestExtractor(PseudoFiles fileOrFolder) {
		super(fileOrFolder, "trx");
	}
	
	@Override
	protected void extractDocument(String path, Document doc, BuildData data) {
		checkRoot(doc, path, "TestRun");
		removeNamespace(doc, "http://microsoft.com/schemas/VisualStudio/TeamTest/2010", path);
		
		Map<String, UnitTest> tests = new HashMap<String, UnitTest>();
		
		for (Element el : findElementsXPath(doc, "/TestRun/TestDefinitions/UnitTest")) {
			UnitTest unitTest = extractUnitTest(el);
			if (unitTest != null)
				tests.put(unitTest.id, unitTest);
		}
		
		for (Element el : findElementsXPath(doc, "/TestRun/Results/UnitTestResult")) {
			processTestResult(data, tests, el);
		}
	}
	
	private UnitTest extractUnitTest(Element el) {
		String id = el.getAttributeValue("id", "");
		if (id.isEmpty())
			return null;
		
		UnitTest result = new UnitTest();
		result.id = id;
		
		Element method = el.getChild("TestMethod");
		if (method != null) {
			result.codeBase = method.getAttributeValue("codeBase", "");
			result.className = method.getAttributeValue("className", "");
			result.name = method.getAttributeValue("name", "");
			
		} else {
			result.codeBase = el.getAttributeValue("storage", "");
			result.className = "";
			result.name = el.getAttributeValue("name", "");
		}
		
		return result;
	}
	
	private void processTestResult(BuildData data, Map<String, UnitTest> tests, Element el) {
		String id = el.getAttributeValue("testId", "");
		String codeBase = "";
		String className = "";
		String name = el.getAttributeValue("testName", "");
		String duration = el.getAttributeValue("duration", "");
		String outcome = toStatus(el.getAttributeValue("outcome", ""));
		
		Element output = getChildOrEmpty(el, "Output");
		String message = concatMessages(output, "StdOut", "StdErr", "DebugTrace", "TraceInfo", "TextMessages/Message");
		
		Element errorInfo = getChildOrEmpty(output, "ErrorInfo");
		String errorMessage = firstNonNull(errorInfo.getChildText("Message"), "");
		String stackTrace = firstNonNull(errorInfo.getChildText("StackTrace"), "");
		if (!errorMessage.isEmpty() && !stackTrace.isEmpty())
			stackTrace = errorMessage + "\n" + stackTrace;
		else if (stackTrace.isEmpty())
			stackTrace = errorMessage;
		
		UnitTest unitTest = tests.get(id);
		if (unitTest != null) {
			codeBase = unitTest.codeBase;
			className = unitTest.className;
			name = unitTest.name;
		}
		
		data.add(1, "Unit test", "C#", "MSTest", outcome, className, name, codeBase, message, stackTrace);
		
		if (duration.isEmpty())
			return;
		
		Matcher m = durationFormat.matcher(duration);
		if (!m.find())
			return;
		
		long ms = TimeUnit.MILLISECONDS.convert(Integer.parseInt(m.group(1)), TimeUnit.HOURS)
				+ TimeUnit.MILLISECONDS.convert(Integer.parseInt(m.group(2)), TimeUnit.MINUTES)
				+ TimeUnit.MILLISECONDS.convert(Integer.parseInt(m.group(3)), TimeUnit.SECONDS);
		
		String msPart = m.group(4);
		if (msPart.length() > 3)
			msPart = msPart.substring(0, 3);
		msPart = StringUtils.rightPad(msPart, 3, '0');
		
		ms += Integer.parseInt(msPart);
		
		data.add(ms / 1000.0, "Unit test", "C#", "MSTest", "time", className, name, codeBase);
	}
	
	private String toStatus(String value) {
		return StringUtils.uncapitalize(value);
	}
	
	private Element getChildOrEmpty(Element el, String name) {
		Element result = el.getChild(name);
		
		if (result == null)
			result = new Element(name);
		
		return result;
	}
	
	private String concatMessages(Element output, String... xpaths) {
		List<Message> messages = new ArrayList<Message>();
		
		for (String xpath : xpaths) {
			for (Element el : findElementsXPath(output, xpath)) {
				String value = el.getText();
				if (value == null || value.trim().isEmpty())
					continue;
				
				messages.add(new Message(el.getName(), value));
			}
		}
		
		if (messages.isEmpty())
			return "";
		
		if (messages.size() == 1)
			return messages.get(0).value;
		
		StringBuilder result = new StringBuilder();
		
		for (Message message : messages) {
			if (result.length() > 0)
				result.append("\n\n");
			result.append(message.name).append(":\n").append(message.value.replaceFirst("\\s+$", ""));
		}
		
		return result.toString();
	}
	
	private static class Message {
		public final String name;
		public final String value;
		
		public Message(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
	private static class UnitTest {
		public String id;
		public String codeBase;
		public String className;
		public String name;
	}
}
