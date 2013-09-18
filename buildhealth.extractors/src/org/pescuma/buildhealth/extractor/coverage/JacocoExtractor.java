package org.pescuma.buildhealth.extractor.coverage;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Strings.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.google.common.base.Predicate;

// http://www.eclemma.org/jacoco/
public class JacocoExtractor extends BaseXMLExtractor {
	
	public JacocoExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, "report", filename);
		
		PlacesTracker place = new PlacesTracker(data, "Java", "JaCoCo");
		extract(data, doc.getRootElement(), "all", place);
	}
	
	private static void extract(BuildData data, Element el, String placeType, PlacesTracker place) {
		int bookmark = place.getBookmark();
		
		String name = getFullName(el);
		
		if (!isNullOrEmpty(name)) {
			if ("package".equals(placeType)) {
				place.goInto(placeType, name.split("/"));
				
			} else if ("class".equals(placeType)) {
				String[] path = name.split("/");
				place.goInto(placeType, path[path.length - 1]);
				
			} else if (!"all".equals(placeType)) {
				place.goInto(placeType, name);
			}
		}
		
		if ("method".equals(placeType)) {
			addCoverage(data, el, placeType, place, new Predicate<String>() {
				@Override
				public boolean apply(String input) {
					return !"method".equals(input) && !"class".equals(input);
				}
			});
			
		} else if ("class".equals(placeType)) {
			addCoverage(data, el, placeType, place, new Predicate<String>() {
				@Override
				public boolean apply(String input) {
					return "method".equals(input);
				}
			});
			
		} else if ("package".equals(placeType)) {
			addCoverage(data, el, placeType, place, new Predicate<String>() {
				@Override
				public boolean apply(String input) {
					return "class".equals(input);
				}
			});
		}
		
		for (Element pkg : el.getChildren("package"))
			extract(data, pkg, "package", place);
		
		for (Element group : el.getChildren("group"))
			extract(data, group, "group", place);
		
		for (Element cls : el.getChildren("class"))
			extract(data, cls, "class", place);
		
		for (Element method : el.getChildren("method"))
			extract(data, method, "method", place);
		
		// for (Element source : el.getChildren("sourcefile"))
		// extract(data, source, "sourceFile", place);
		
		place.goBackTo(bookmark);
	}
	
	private static String getFullName(Element el) {
		String name = el.getAttributeValue("name");
		
		String desc = el.getAttributeValue("desc");
		if (!isNullOrEmpty(desc))
			name = firstNonNull(name, "") + " " + desc;
		return name;
	}
	
	private static void addCoverage(BuildData data, Element el, String placeType, PlacesTracker place,
			Predicate<String> typeFilter) {
		for (Element coverage : el.getChildren("counter")) {
			String type = jacocoTypeToCoverageType(coverage.getAttributeValue("type"));
			if (type == null)
				continue;
			if (!typeFilter.apply(type))
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
