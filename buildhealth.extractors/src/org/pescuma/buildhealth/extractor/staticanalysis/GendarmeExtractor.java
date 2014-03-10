package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.pescuma.buildhealth.extractor.utils.FilenameToLanguage.*;
import static org.pescuma.buildhealth.extractor.utils.StringBuilderUtils.*;
import static org.pescuma.buildhealth.utils.StringHelper.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.utils.Location;

// http://www.mono-project.com/Gendarme
public class GendarmeExtractor extends BaseXMLExtractor {
	
	private static final String RULE_PREFIX = "Gendarme.Rules.";
	
	public GendarmeExtractor(PseudoFiles files) {
		super(files);
	}
	
	private static final Pattern SOURCE_PATTERN = Pattern.compile("(.*)\\(\\u2248?(\\d+)(?:,(\\d*))?\\)");
	
	@Override
	protected void extractDocument(String path, Document doc, BuildData data) {
		checkRoot(doc, path, "gendarme-output");
		
		Map<String, String> fullNames = findRuleFullNames(doc);
		for (Element rule : findElementsXPath(doc, "/gendarme-output/results/rule")) {
			String uri = rule.getAttributeValue("Uri", "");
			if (uri.isEmpty())
				continue;
			
			String ruleName = fullNames.get(uri);
			if (ruleName == null)
				ruleName = rule.getAttributeValue("Name", "");
			if (ruleName.isEmpty())
				ruleName = uri;
			
			String problem = rule.getChildTextTrim("problem");
			String solution = rule.getChildTextTrim("solution");
			
			for (Element target : rule.getChildren("target")) {
				for (Element defect : target.getChildren("defect")) {
					String severity = defect.getAttributeValue("Severity", "");
					String confidence = defect.getAttributeValue("Confidence", "");
					String location = defect.getAttributeValue("Location", "");
					String message = defect.getTextTrim();
					String source = defect.getAttributeValue("Source", "");
					if (source.isEmpty())
						source = location;
					
					Location loc;
					
					Matcher m = SOURCE_PATTERN.matcher(source);
					if (m.matches()) {
						if (m.group(3) == null)
							loc = Location.create(m.group(1), m.group(2));
						else
							loc = Location.create(m.group(1), m.group(2), m.group(3));
					} else {
						loc = new Location(source);
					}
					
					String language = firstNonEmpty(detectLanguage(loc.file), "C#");
					
					StringBuilder details = new StringBuilder();
					appendInNewLine(details, "Problem", problem);
					appendInNewLine(details, "Solution", solution);
					appendInNewLine(details, "Severity", severity);
					appendInNewLine(details, "Confidence", confidence);
					appendInNewLine(details, "Location", location);
					
					data.add(1, "Static analysis", language, "Gendarme", Location.toFormatedString(loc), ruleName,
							message, toBuildHealthSeverity(severity), details.toString(), uri);
				}
			}
		}
	}
	
	private String toBuildHealthSeverity(String severity) {
		if ("Critical".equalsIgnoreCase(severity))
			return "High";
		if ("High".equalsIgnoreCase(severity))
			return "High";
		if ("Medium".equalsIgnoreCase(severity))
			return "Medium";
		if ("Low".equalsIgnoreCase(severity))
			return "Low";
		if ("Audit".equalsIgnoreCase(severity))
			return "Low";
		return severity;
	}
	
	private Map<String, String> findRuleFullNames(Document doc) {
		Map<String, String> result = new HashMap<String, String>();
		
		for (Element rule : findElementsXPath(doc, "/gendarme-output/rules/rule")) {
			String uri = rule.getAttributeValue("Uri", "");
			String name = rule.getTextTrim();
			
			if (name.startsWith(RULE_PREFIX))
				name = name.substring(RULE_PREFIX.length());
			
			name = name.replace('.', '/');
			
			if (!uri.isEmpty() && !name.isEmpty())
				result.put(uri, name);
		}
		
		return result;
	}
	
}
