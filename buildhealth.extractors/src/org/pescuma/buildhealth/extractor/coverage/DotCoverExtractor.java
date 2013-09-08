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
		
		extractChildren(data, el, place, "Assembly", "library");
		extractChildren(data, el, place, "Namespace", "package");
		extractChildren(data, el, place, "Type", "class");
		extractChildren(data, el, place, "Property", "property");
		extractChildren(data, el, place, "Event", "event");
		extractChildren(data, el, place, "Member", "method");
		extractChildren(data, el, place, "Constructor", "method");
		extractChildren(data, el, place, "Method", "method");
		extractChildren(data, el, place, "AnonymousMethod", "method");
		
		for (Element method : el.getChildren("OwnCoverage")) {
			place.add("OwnCoverage");
			extract(data, method, "method", place);
			place.remove(place.size() - 1);
		}
		
		if (!isNullOrEmpty(name))
			place.remove(place.size() - 1);
	}
	
	private void extractChildren(BuildData data, Element el, List<String> place, String xmlTag, String placeType) {
		for (Element e : el.getChildren(xmlTag))
			extract(data, e, placeType, place);
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
