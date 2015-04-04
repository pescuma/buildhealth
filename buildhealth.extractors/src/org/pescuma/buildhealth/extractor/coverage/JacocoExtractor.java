package org.pescuma.buildhealth.extractor.coverage;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Strings.*;

import org.apache.commons.lang.ArrayUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.datatable.DataTable;

// http://www.eclemma.org/jacoco/
public class JacocoExtractor extends BaseXMLExtractor {
	
	public JacocoExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String path, Document doc, DataTable data) {
		checkRoot(doc, path, "report");
		
		PlacesTracker place = new PlacesTracker(data, "Java", "JaCoCo");
		extract(doc.getRootElement(), "all", place);
	}
	
	private static void extract(Element el, String placeType, PlacesTracker place) {
		int bookmark = place.getBookmark();
		
		String name = getFullName(el);
		
		if (!isNullOrEmpty(name)) {
			if ("package".equals(placeType)) {
				place.goInto(placeType, name.replace('/', '.'));
				
			} else if ("class".equals(placeType)) {
				String[] path = name.split("/");
				place.goInto(placeType, path[path.length - 1]);
				
			} else if (!"all".equals(placeType)) {
				place.goInto(placeType, name);
			}
		}
		
		// Always add "all" so it can be used for LOC
		// Line is strange. Always add line
		if ("method".equals(placeType) || "all".equals(placeType)) {
			addCoverage(el, placeType, place);
			
		} else if ("class".equals(placeType)) {
			addCoverage(el, placeType, place, "line", "method", "class");
			
		} else {
			addCoverage(el, placeType, place, "line");
		}
		
		extractChildren(el, place, "package", "package");
		extractChildren(el, place, "group", "group");
		extractChildren(el, place, "class", "class");
		extractChildren(el, place, "method", "method");
		// extractChildren(el, place, "sourcefile", "sourceFile");
		
		place.goBackTo(bookmark);
	}
	
	private static void extractChildren(Element el, PlacesTracker place, String xmlTag, String placeType) {
		for (Element e : el.getChildren(xmlTag))
			extract(e, placeType, place);
	}
	
	private static String getFullName(Element el) {
		String name = el.getAttributeValue("name");
		
		String desc = el.getAttributeValue("desc");
		if (!isNullOrEmpty(desc))
			name = firstNonNull(name, "") + " " + desc;
		
		return name;
	}
	
	private static void addCoverage(Element el, String placeType, PlacesTracker place, String... typeFilter) {
		for (Element coverage : el.getChildren("counter")) {
			String type = jacocoTypeToCoverageType(coverage.getAttributeValue("type"));
			if (type == null)
				continue;
			if (typeFilter.length > 0 && ArrayUtils.indexOf(typeFilter, type) < 0)
				continue;
			
			double missed = Double.parseDouble(coverage.getAttributeValue("missed"));
			double covered = Double.parseDouble(coverage.getAttributeValue("covered"));
			double total = covered + missed;
			
			place.addToData(covered, type, "covered");
			place.addToData(total, type, "total");
		}
	}
	
	private static String jacocoTypeToCoverageType(String type) {
		if ("INSTRUCTION".equals(type))
			return "instruction";
		else if ("BRANCH".equals(type))
			return "branch";
		else if ("LINE".equals(type))
			return "line";
		else if ("COMPLEXITY".equals(type))
			return "complexity";
		else if ("METHOD".equals(type))
			return "method";
		else if ("CLASS".equals(type))
			return "class";
		else
			return null;
	}
}
