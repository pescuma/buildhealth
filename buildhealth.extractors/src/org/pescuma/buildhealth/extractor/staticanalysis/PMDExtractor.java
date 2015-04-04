package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.pescuma.buildhealth.extractor.utils.StringBuilderUtils.*;
import static org.pescuma.programminglanguagedetector.FilenameToLanguage.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.utils.Location;
import org.pescuma.datatable.DataTable;

// http://pmd.sourceforge.net/
public class PMDExtractor extends BaseXMLExtractor {
	
	public PMDExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String path, Document doc, DataTable data) {
		checkRoot(doc, path, "pmd");
		
		for (Element file : doc.getRootElement().getChildren("file"))
			extractFile(file, data);
	}
	
	private void extractFile(Element file, DataTable data) {
		String filename = file.getAttributeValue("name", "");
		
		for (Element violation : file.getChildren("violation"))
			extractViolation(filename, violation, data);
	}
	
	private void extractViolation(String filename, Element violation, DataTable data) {
		Location loc = Location.create(filename, violation.getAttributeValue("beginline", ""),
				violation.getAttributeValue("begincolumn", ""), violation.getAttributeValue("endline", ""),
				violation.getAttributeValue("endcolumn", ""));
		String rule = violation.getAttributeValue("rule", "");
		String ruleset = violation.getAttributeValue("ruleset", "");
		String externalInfoUrl = violation.getAttributeValue("externalInfoUrl", "");
		String message = violation.getTextTrim();
		String priority = toPMDPriority(violation.getAttributeValue("priority", ""));
		String pkg = violation.getAttributeValue("package", "");
		String cls = violation.getAttributeValue("class", "");
		String method = violation.getAttributeValue("method", "");
		String variable = violation.getAttributeValue("variable", "");
		
		StringBuilder description = new StringBuilder();
		appendWhere(description, "", pkg);
		appendWhere(description, ".", cls);
		appendWhere(description, ".", method);
		if (!variable.isEmpty())
			appendWhere(description, ", ", "variable " + variable);
		appendInNewLine(description, "Priority", priority);
		
		if (!ruleset.isEmpty())
			rule = ruleset + "/" + rule;
		
		data.add(1, "Static analysis", detectLanguage(filename), "PMD", Location.toFormatedString(loc), rule, message,
				toBuildHealthSeverity(priority), description.toString(), externalInfoUrl);
	}
	
	private void appendWhere(StringBuilder out, String prefix, String data) {
		if (data.isEmpty())
			return;
		
		if (out.length() < 1)
			out.append("in ");
		else
			out.append(prefix);
		
		out.append(data);
	}
	
	private String toPMDPriority(String priority) {
		try {
			
			int p = Integer.parseInt(priority);
			if (p <= 1)
				return "High";
			if (p == 2)
				return "Medium High";
			if (p == 3)
				return "Medium";
			if (p == 4)
				return "Medium Low";
			return "Low";
			
		} catch (NumberFormatException e) {
			return priority;
		}
	}
	
	private String toBuildHealthSeverity(String priority) {
		if ("High".equals(priority))
			return "High";
		if ("Medium High".equals(priority))
			return "High";
		if ("Medium".equals(priority))
			return "Medium";
		if ("Medium Low".equals(priority))
			return "Low";
		if ("Low".equals(priority))
			return "Low";
		return priority;
	}
}
