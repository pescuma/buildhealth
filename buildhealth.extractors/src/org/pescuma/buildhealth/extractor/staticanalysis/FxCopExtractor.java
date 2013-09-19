package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.apache.commons.lang3.ObjectUtils.*;
import static org.pescuma.buildhealth.extractor.utils.FilenameToLanguage.*;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// http://msdn.microsoft.com/en-us/library/bb429476%28v=vs.80%29.aspx
public class FxCopExtractor extends BaseXMLExtractor {
	
	public FxCopExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, "FxCopReport", filename);
		
		Map<String, Rule> rules = findRules(doc);
		
		processChildren(data, doc.getRootElement(), "", rules);
	}
	
	private void processChildren(BuildData data, Element el, String target, Map<String, Rule> rules) {
		extractMessages(data, el, target, rules);
		
		processChildren(data, el, "Target", "", rules);
		processChildren(data, el, "Resource", "", rules);
		processChildren(data, el, "Module", "", rules);
		processChildren(data, el, "Namespace", "", rules);
		processChildren(data, el, "Type", target + ".", rules);
		processChildren(data, el, "Member", target + " ", rules);
		processChildren(data, el, "Accessor", target + " ", rules);
	}
	
	private void processChildren(BuildData data, Element el, String childName, String target, Map<String, Rule> rules) {
		Element group = el.getChild(childName + "s");
		if (group == null)
			return;
		
		for (Element child : group.getChildren(childName))
			processChildren(data, child, target + child.getAttributeValue("Name", ""), rules);
	}
	
	private void extractMessages(BuildData data, Element el, String target, Map<String, Rule> rules) {
		Element messages = el.getChild("Messages");
		if (messages == null)
			return;
		
		for (Element msg : messages.getChildren("Message")) {
			Element issue = msg.getChild("Issue");
			if (issue == null)
				// The most interesting stuff is inside issue
				continue;
			
			String targetKind = firstNonEmpty(el.getAttributeValue("Kind", ""), el.getName());
			String checkId = msg.getAttributeValue("CheckId", "");
			String typeName = msg.getAttributeValue("TypeName", "");
			String category = msg.getAttributeValue("Category", "");
			String status = msg.getAttributeValue("Status", "");
			String created = msg.getAttributeValue("Created", "");
			String fixCategory = msg.getAttributeValue("FixCategory", "");
			String certainty = issue.getAttributeValue("Certainty", "");
			String level = issue.getAttributeValue("Level", "");
			String path = issue.getAttributeValue("Path", "");
			String file = issue.getAttributeValue("File", "");
			String line = issue.getAttributeValue("Line", "");
			String resolution = issue.getTextTrim();
			
			Rule rule = rules.get(checkId);
			if (rule == null)
				rule = rules.get(category + "\n" + typeName);
			if (rule == null)
				rule = new Rule();
			
			String language = firstNonEmpty(detectLanguage(file), "C#");
			String bhCateg = toBuildHealthCategory(category, typeName, "");
			String bhServerity = toBuildHealthSeverity(level);
			
			String fullFilename = buildFullFilename(path, file);
			if (fullFilename.isEmpty()) {
				fullFilename = target;
				line = "";
			}
			
			StringBuilder details = new StringBuilder();
			append(details, "Level", firstNonEmpty(level, rule.level));
			append(details, "Certainty", firstNonEmpty(certainty, rule.certainty), "%");
			append(details, "Target", target);
			append(details, "Target Kind", targetKind);
			append(details, "Resolution", resolution);
			append(details, "Help", rule.url);
			append(details, "Category", category);
			append(details, "CheckId", firstNonEmpty(checkId, rule.checkId));
			append(details, "Info", rule.description);
			append(details, "Created", created);
			append(details, "Status", status);
			append(details, "Fix Category", splitCamelCase(fixCategory));
			
			data.add(1, "Static analysis", language, "FxCop", fullFilename, line,
					firstNonEmpty(rule.category, bhCateg), resolution, firstNonEmpty(bhServerity, rule.severity),
					details.toString(), rule.url);
		}
	}
	
	private String splitCamelCase(String text) {
		return text.replaceAll("(?=[A-Z]+)", " ").trim();
	}
	
	private void append(StringBuilder out, String name, String text) {
		append(out, name, text, "");
	}
	
	private void append(StringBuilder out, String name, String text, String suffix) {
		if (text.isEmpty())
			return;
		
		if (out.length() > 0)
			out.append("\n");
		
		out.append(name).append(": ").append(text).append(suffix);
	}
	
	private String firstNonEmpty(String... args) {
		for (String a : args) {
			if (a != null && !a.isEmpty())
				return a;
		}
		return "";
	}
	
	private String buildFullFilename(String path, String file) {
		StringBuilder result = new StringBuilder();
		
		if (!path.isEmpty()) {
			result.append(path);
			
			// Try to keep same separator
			if (path.indexOf('/') >= 0)
				result.append('/');
			else if (path.indexOf('\\') >= 0)
				result.append('\\');
			else
				result.append(File.separator);
		}
		
		if (!file.isEmpty())
			result.append(file);
		
		return result.toString();
	}
	
	private Map<String, Rule> findRules(Document doc) {
		Map<String, Rule> result = new HashMap<String, Rule>();
		
		for (Element rule : findElementsXPath(doc, "/FxCopReport/Rules/Rule")) {
			String checkId = rule.getAttributeValue("CheckId", "");
			String typeName = rule.getAttributeValue("TypeName", "");
			String category = rule.getAttributeValue("Category", "");
			String name = firstNonNull(rule.getChildText("Name"), "");
			String description = firstNonNull(rule.getChildText("Description"), "");
			String url = firstNonNull(rule.getChildText("Url"), "");
			String level = "";
			String certainty = "";
			
			Element messageLevel = rule.getChild("MessageLevel");
			if (messageLevel != null) {
				level = messageLevel.getTextTrim();
				certainty = messageLevel.getAttributeValue("Certainty", "");
			}
			
			Rule tmp = new Rule();
			tmp.checkId = checkId;
			tmp.category = toBuildHealthCategory(category, typeName, name);
			tmp.description = description;
			tmp.url = url;
			tmp.certainty = certainty;
			tmp.level = level;
			tmp.severity = toBuildHealthSeverity(level);
			
			if (!checkId.isEmpty())
				result.put(checkId, tmp);
			if (!category.isEmpty() || !typeName.isEmpty())
				result.put(category + "\n" + typeName, tmp);
		}
		
		return result;
	}
	
	private String toBuildHealthSeverity(String level) {
		if (level.isEmpty())
			return "";
		
		if (level.toLowerCase(Locale.ENGLISH).contains("warning"))
			return "Medium";
		
		else if (level.toLowerCase(Locale.ENGLISH).contains("error"))
			return "High";
		
		else
			return "Low";
	}
	
	private String toBuildHealthCategory(String category, String typeName, String name) {
		StringBuilder result = new StringBuilder();
		
		if (!category.isEmpty())
			result.append(category.replace('.', '/')).append('/');
		
		if (!name.isEmpty())
			result.append(name);
		else
			result.append(typeName);
		
		return result.toString();
	}
	
	private static class Rule {
		String checkId = "";
		String category = "";
		String description = "";
		String url = "";
		String certainty = "";
		String level = "";
		String severity = "";
	}
}
