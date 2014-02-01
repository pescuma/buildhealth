package org.pescuma.buildhealth.extractor.tasks;

import static org.pescuma.buildhealth.utils.ObjectUtils.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// http://bugseverywhere.org/
public class BugsEverywhereExtractor extends BaseXMLExtractor {
	
	public BugsEverywhereExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, "be-xml", filename);
		
		for (Element bug : doc.getRootElement().getChildren("bug"))
			extractBug(data, bug);
	}
	
	private void extractBug(BuildData data, Element bug) {
		String id = firstNonNull(bug.getChildText("short-name"), bug.getChildText("uuid"), "");
		String severity = firstNonNull(bug.getChildText("severity"), "");
		String status = firstNonNull(bug.getChildText("status"), "");
		String assigned = firstNonNull(bug.getChildText("assigned"), "");
		String reporter = firstNonNull(bug.getChildText("reporter"), bug.getChildText("creator"), "");
		String created = firstNonNull(bug.getChildText("created"), "");
		String summary = firstNonNull(bug.getChildText("summary"), "");
		
		String type = "Bug";
		
		if (severity.equals("target")) {
			type = "Milestone";
			severity = "";
		}
		
		StringBuilder description = new StringBuilder();
		
		if (!severity.isEmpty())
			description.append("severity: ").append(severity);
		
		data.add(1, "Tasks", "BugsEverywhere", type, status, summary, assigned, reporter, created, id, "",
				description.toString());
	}
	
}
