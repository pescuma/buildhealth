package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.pescuma.buildhealth.utils.FilenameToLanguage.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class PMDExtractor extends BaseXMLExtractor {
	
	public PMDExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, "pmd", filename);
		
		for (Element file : doc.getRootElement().getChildren("file"))
			extractFile(file, data);
	}
	
	private void extractFile(Element file, BuildData data) {
		String filename = file.getAttributeValue("name", "");
		
		for (Element violation : file.getChildren("violation"))
			extractViolation(filename, violation, data);
	}
	
	private void extractViolation(String filename, Element violation, BuildData data) {
		String line = violation.getAttributeValue("beginline", "") + ":"
				+ violation.getAttributeValue("begincolumn", "") + ":" //
				+ violation.getAttributeValue("endline", "") + ":" //
				+ violation.getAttributeValue("endcolumn", "");
		String rule = violation.getAttributeValue("rule", "");
		String ruleset = violation.getAttributeValue("ruleset", "");
		String externalInfoUrl = violation.getAttributeValue("externalInfoUrl", "");
		String message = violation.getTextTrim();
		
		if (!ruleset.isEmpty())
			rule = ruleset + "/" + rule;
		
		data.add(1, "Static analysis", detectLanguage(filename), "PMD", filename, line, rule, message, "",
				externalInfoUrl);
	}
}
