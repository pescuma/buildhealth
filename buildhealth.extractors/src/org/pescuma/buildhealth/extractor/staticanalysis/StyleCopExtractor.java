package org.pescuma.buildhealth.extractor.staticanalysis;

import java.io.File;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.utils.Location;

// https://stylecop.codeplex.com/
// Based on Jenkins Violations plugin: https://github.com/jenkinsci/violations-plugin
public class StyleCopExtractor extends BaseXMLExtractor {
	
	private static final String[] RULE_PREFIXES = new String[] { "Microsoft.SourceAnalysis.CSharp.",
			"Microsoft.StyleCop.CSharp." };
	
	public StyleCopExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(File file, String filename, Document doc, BuildData data) {
		checkRoot(doc, new String[] { "SourceAnalysisViolations", "StyleCopViolations" }, filename);
		
		for (Element violation : findElementsXPath(doc, "//Violation")) {
			String section = violation.getAttributeValue("Section", "");
			String source = violation.getAttributeValue("Source", "");
			String lineNumber = violation.getAttributeValue("LineNumber", "");
			String ruleNamespace = violation.getAttributeValue("RuleNamespace", "");
			String rule = violation.getAttributeValue("Rule", "");
			String ruleId = violation.getAttributeValue("RuleId", "");
			String message = violation.getTextTrim();
			
			if (message.isEmpty() && source.isEmpty())
				continue;
			
			for (String prefix : RULE_PREFIXES) {
				if (ruleNamespace.startsWith(prefix)) {
					ruleNamespace = ruleNamespace.substring(prefix.length());
					break;
				}
			}
			
			String category = ruleNamespace.replace('.', '/');
			if (!category.isEmpty())
				category += '/';
			category += rule;
			
			String url;
			if (!ruleId.isEmpty())
				url = "http://www.stylecop.com/docs/" + ruleId + ".html";
			else
				url = "";
			
			String description;
			if (!section.isEmpty())
				description = "at " + section;
			else
				description = "";
			
			Location loc = Location.create(source, lineNumber);
			
			data.add(1, "Static analysis", "C#", "StyleCop", Location.toFormatedString(loc), category, message, "",
					description, url);
		}
	}
}
