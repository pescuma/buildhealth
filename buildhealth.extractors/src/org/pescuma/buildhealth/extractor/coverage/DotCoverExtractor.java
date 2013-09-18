package org.pescuma.buildhealth.extractor.coverage;

import static com.google.common.base.Strings.*;

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
		
		PlacesTracker place = new PlacesTracker(data, "C#", "dotCover");
		extract(data, doc.getRootElement(), "all", place);
	}
	
	private void extract(BuildData data, Element el, String placeType, PlacesTracker place) {
		int bookmark = place.getBookmark();
		
		String name = el.getAttributeValue("Name");
		if (!isNullOrEmpty(name)) {
			if ("package".equals(placeType)) {
				place.goInto(placeType, name.split("\\."));
				
			} else if (!"all".equals(placeType)) {
				place.goInto(placeType, name);
			}
		}
		
		if ("method".equals(placeType))
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
			int bookmark2 = place.getBookmark();
			place.goInto("method", "OwnCoverage");
			
			extract(data, method, "method", place);
			
			place.goBackTo(bookmark2);
		}
		
		place.goBackTo(bookmark);
	}
	
	private void extractChildren(BuildData data, Element el, PlacesTracker place, String xmlTag, String placeType) {
		for (Element e : el.getChildren(xmlTag))
			extract(data, e, placeType, place);
	}
	
	private static void addCoverage(BuildData data, Element el, String placeType, PlacesTracker place) {
		double total = Double.parseDouble(el.getAttributeValue("TotalStatements"));
		double covered = Double.parseDouble(el.getAttributeValue("CoveredStatements"));
		
		place.addToData(covered, "line", "covered");
		place.addToData(total, "line", "total");
	}
	
}
