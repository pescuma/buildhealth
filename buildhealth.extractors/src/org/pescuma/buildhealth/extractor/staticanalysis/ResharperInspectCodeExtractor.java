package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.pescuma.buildhealth.extractor.utils.FilenameToLanguage.*;
import static org.pescuma.buildhealth.utils.StringHelper.*;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class ResharperInspectCodeExtractor extends BaseXMLExtractor {
	
	public ResharperInspectCodeExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, "Report", filename);
		
		Map<String, IssueType> types = loadIssueTypes(doc);
		
		for (Element proj : findElementsXPath(doc, "//Issues/Project")) {
			String projName = proj.getAttributeValue("Name", "");
			
			if (!projName.isEmpty())
				projName += "\\";
			
			for (Element issue : proj.getChildren("Issue")) {
				String typeId = issue.getAttributeValue("TypeId");
				if (typeId == null)
					continue;
				
				String file = projName + issue.getAttributeValue("File", "");
				String line = issue.getAttributeValue("Line", "");
				String message = issue.getAttributeValue("Message", "");
				
				String language = firstNonEmpty(detectLanguage(file), "C#");
				
				IssueType type = types.get(typeId);
				if (type == null)
					type = new IssueType("", typeId, "Low");
				
				data.add(1, "Static analysis", language, "Resharper InspectCode", file, line, type.category, message,
						type.severity);
			}
		}
	}
	
	private Map<String, IssueType> loadIssueTypes(Document doc) {
		Map<String, IssueType> types = new HashMap<String, IssueType>();
		for (Element type : findElementsXPath(doc, "//IssueTypes/IssueType")) {
			String id = type.getAttributeValue("Id");
			if (id == null)
				continue;
			
			String category = type.getAttributeValue("Category", "");
			String descrption = type.getAttributeValue("Description", id);
			String severity = toBuildHealthSeverity(type.getAttributeValue("Severity"));
			
			types.put(id, new IssueType(category, descrption, severity));
		}
		return types;
	}
	
	private String toBuildHealthSeverity(String severity) {
		if ("ERROR".equalsIgnoreCase(severity))
			return "High";
		if ("WARNING".equalsIgnoreCase(severity))
			return "Medium";
		if ("SUGGESTION".equalsIgnoreCase(severity))
			return "Low";
		if ("HINT".equalsIgnoreCase(severity))
			return "Low";
		return "Low";
	}
	
	private static class IssueType {
		final String category;
		@SuppressWarnings("unused")
		final String description;
		final String severity;
		
		IssueType(String category, String description, String severity) {
			this.category = category;
			this.description = description;
			this.severity = severity;
		}
	}
	
}
