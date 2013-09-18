package org.pescuma.buildhealth.extractor.coverage;

import static com.google.common.base.Strings.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// http://emma.sourceforge.net/
public class EmmaExtractor extends BaseXMLExtractor {
	
	// TODO private static final Logger log = LoggerFactory.getLogger(EmmaExtractor.class);
	
	private static final Pattern VALUE_PATTERN = Pattern.compile("(\\d+)% +\\((\\d+(?:\\.\\d+)?)/(\\d+)\\)");
	
	public EmmaExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, "report", filename);
		
		PlacesTracker place = new PlacesTracker(data, "Java", "Emma");
		extract(doc.getRootElement().getChild("data").getChild("all"), "all", place);
	}
	
	private static void extract(Element el, String placeType, PlacesTracker place) {
		int bookmark = place.getBookmark();
		
		String name = el.getAttributeValue("name");
		if (!isNullOrEmpty(name)) {
			if (!"all".equals(placeType) && !"sourceFile".equals(placeType))
				place.goInto(placeType, name);
		}
		
		// Emma does some strange stuff while counting so we always need to keep the lines
		// Always add "all" so it can be used for LOC
		if ("method".equals(placeType) || "all".equals(placeType)) {
			addCoverage(el, placeType, place);
			
		} else if ("class".equals(placeType)) {
			addCoverage(el, placeType, place, "line", "method", "class");
			
		} else if ("package".equals(placeType)) {
			addCoverage(el, placeType, place, "line");
		}
		
		extractChildren(el, place, "package", "package");
		extractChildren(el, place, "srcfile", "sourceFile");
		extractChildren(el, place, "class", "class");
		extractChildren(el, place, "method", "method");
		
		place.goBackTo(bookmark);
	}
	
	private static void extractChildren(Element el, PlacesTracker place, String xmlTag, String placeType) {
		for (Element e : el.getChildren(xmlTag))
			extract(e, placeType, place);
	}
	
	private static void addCoverage(Element el, String placeType, PlacesTracker place, String... typeFilter) {
		for (Element coverage : el.getChildren("coverage")) {
			String type = emmaTypeToCoverageType(coverage.getAttributeValue("type"));
			if (type == null)
				continue;
			if (typeFilter.length > 0 && ArrayUtils.indexOf(typeFilter, type) < 0)
				continue;
			
			String value = coverage.getAttributeValue("value");
			Matcher m = VALUE_PATTERN.matcher(value);
			if (!m.matches())
				continue;
			
			double covered = Double.parseDouble(m.group(2));
			double total = Double.parseDouble(m.group(3));
			
			place.addToData(covered, type, "covered");
			place.addToData(total, type, "total");
		}
	}
	
	private static String emmaTypeToCoverageType(String type) {
		if ("class, %".equals(type))
			return "class";
		else if ("method, %".equals(type))
			return "method";
		else if ("block, %".equals(type))
			return "block";
		else if ("line, %".equals(type))
			return "line";
		else
			return null;
	}
	
}
