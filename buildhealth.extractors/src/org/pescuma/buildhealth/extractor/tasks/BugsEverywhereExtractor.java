package org.pescuma.buildhealth.extractor.tasks;

import static org.apache.commons.io.IOUtils.*;
import static org.pescuma.buildhealth.extractor.utils.StringBuilderUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.LineIterator;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// http://bugseverywhere.org/
public class BugsEverywhereExtractor extends BaseBuildDataExtractor {
	
	private static Pattern LINE = Pattern.compile("^([^:]+):([^:])([^:]):(?:([^:]+):)?(.*)$");
	private static Map<String, String> statuses = new HashMap<String, String>();
	private static Map<String, String> severities = new HashMap<String, String>();
	static {
		statuses.put("u", "Unconfirmed");
		statuses.put("o", "Open");
		statuses.put("a", "Assigned");
		statuses.put("t", "Test");
		statuses.put("c", "Closed");
		statuses.put("f", "Fixed");
		statuses.put("w", "Won't fix");
		
		severities.put("t", "Target");
		severities.put("w", "Wishlist");
		severities.put("m", "Minor");
		severities.put("s", "Serious");
		severities.put("c", "Critical");
		severities.put("f", "Fatal");
	}
	
	public BugsEverywhereExtractor(PseudoFiles files) {
		super(files, "txt");
	}
	
	@Override
	protected void extract(String filename, InputStream input, BuildData data) throws IOException {
		LineIterator lines = lineIterator(input, "UTF-8");
		
		while (lines.hasNext()) {
			String line = lines.next().trim();
			
			Matcher m = LINE.matcher(line);
			if (!m.matches())
				continue;
			
			extract(data, m.group(1), m.group(2), m.group(3), m.group(4), m.group(5));
		}
	}
	
	private void extract(BuildData data, String id, String status, String severity, String tags, String message) {
		status = statusFromFirstLetter(status);
		severity = severityFromFirstLetter(severity);
		if (tags == null)
			tags = "";
		tags = tags.replace(",", ", ");
		
		String type = "Bug";
		if ("Target".equals(severity)) {
			type = "Milestone";
			severity = "";
		}
		
		StringBuilder details = new StringBuilder();
		appendInNewLine(details, "Severity", severity);
		appendInNewLine(details, "Tags", tags);
		
		data.add(1, "Tasks", "BugsEverywhere", type, status, message.trim(), "", "", "", id, "", details.toString());
	}
	
	private String statusFromFirstLetter(String status) {
		String result = statuses.get(status.toLowerCase(Locale.ENGLISH));
		if (result == null)
			return status;
		else
			return result;
	}
	
	private String severityFromFirstLetter(String severity) {
		String result = severities.get(severity.toLowerCase(Locale.ENGLISH));
		if (result == null)
			return severity;
		else
			return result;
	}
	
}
