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
		extract(doc.getRootElement(), "all", place);
	}
	
	private void extract(Element el, String placeType, PlacesTracker place) {
		int bookmark = place.getBookmark();
		
		String name = el.getAttributeValue("Name");
		if (!isNullOrEmpty(name)) {
			if ("package".equals(placeType)) {
				place.goInto(placeType, name);
				
			} else if (!"all".equals(placeType)) {
				place.goInto(placeType, name);
			}
		}
		
		// Always add "all" so it can be used for LOC
		if ("all".equals(placeType) || "method".equals(placeType))
			addCoverage(el, placeType, place);
		
		extractChildren(el, place, "Assembly", "library");
		extractChildren(el, place, "Namespace", "package");
		extractChildren(el, place, "Type", "class");
		extractChildren(el, place, "Property", "property");
		extractChildren(el, place, "Event", "event");
		extractChildren(el, place, "Member", "method");
		extractChildren(el, place, "Constructor", "method");
		extractChildren(el, place, "Method", "method");
		extractChildren(el, place, "AnonymousMethod", "method");
		
		for (Element method : el.getChildren("OwnCoverage")) {
			int bookmark2 = place.getBookmark();
			place.goInto("method", "OwnCoverage");
			
			extract(method, "method", place);
			
			place.goBackTo(bookmark2);
		}
		
		place.goBackTo(bookmark);
	}
	
	private void extractChildren(Element el, PlacesTracker place, String xmlTag, String placeType) {
		for (Element e : el.getChildren(xmlTag))
			extract(e, placeType, place);
	}
	
	private static void addCoverage(Element el, String placeType, PlacesTracker place) {
		double total = Double.parseDouble(el.getAttributeValue("TotalStatements"));
		double covered = Double.parseDouble(el.getAttributeValue("CoveredStatements"));
		
		place.addToData(covered, "line", "covered");
		place.addToData(total, "line", "total");
	}
	
}
