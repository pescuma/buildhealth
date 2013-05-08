package org.pescuma.buildhealth.extractor.coverage;

import static com.google.common.base.Strings.*;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// http://www.jetbrains.com/dotcover/
public class DotCoverExtractor extends BaseXMLExtractor {
	
	public DotCoverExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, "Root", filename);
		
		List<String> place = new ArrayList<String>();
		extract(data, doc.getRootElement(), "all", place);
	}
	
	private void extract(BuildData data, Element el, String placeType, List<String> place) {
		String name = el.getAttributeValue("Name");
		if ("Root".equals(el.getName()) && "Root".equals(name))
			name = null;
		
		if (!isNullOrEmpty(name))
			place.add(name);
		
		addCoverage(data, el, placeType, place);
		
		for (Element pkg : el.getChildren("Assembly"))
			extract(data, pkg, "library", place);
		
		for (Element pkg : el.getChildren("Namespace"))
			extract(data, pkg, "package", place);
		
		for (Element cls : el.getChildren("Type"))
			extract(data, cls, "class", place);
		
		for (Element method : el.getChildren("Property"))
			extract(data, method, "property", place);
		
		for (Element method : el.getChildren("Event"))
			extract(data, method, "event", place);
		
		for (Element method : el.getChildren("Member"))
			extract(data, method, "method", place);
		
		for (Element method : el.getChildren("Constructor"))
			extract(data, method, "method", place);
		
		for (Element method : el.getChildren("Method"))
			extract(data, method, "method", place);
		
		for (Element method : el.getChildren("AnonymousMethod"))
			extract(data, method, "method", place);
		
		for (Element method : el.getChildren("OwnCoverage")) {
			place.add("OwnCoverage");
			extract(data, method, "method", place);
			place.remove(place.size() - 1);
		}
		
		if (!isNullOrEmpty(name))
			place.remove(place.size() - 1);
	}
	
	private static void addCoverage(BuildData data, Element el, String placeType, List<String> place) {
		double total = Double.parseDouble(el.getAttributeValue("TotalStatements"));
		double covered = Double.parseDouble(el.getAttributeValue("CoveredStatements"));
		
		List<String> infos = new ArrayList<String>();
		infos.add("Coverage");
		infos.add("C#");
		infos.add("dotCover");
		infos.add("line");
		infos.add("covered");
		infos.add(placeType);
		if (place != null)
			infos.addAll(place);
		
		data.add(covered, infos.toArray(new String[infos.size()]));
		
		infos.set(4, "total");
		data.add(total, infos.toArray(new String[infos.size()]));
	}
	
}
