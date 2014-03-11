package org.pescuma.buildhealth.extractor.coverage;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// https://github.com/OpenCover/opencover
public class OpenCoverExtractor extends BaseXMLExtractor {
	
	public OpenCoverExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String path, Document doc, BuildData data) {
		checkRoot(doc, path, "CoverageSession");
		
		PlacesTracker place = new PlacesTracker(data, "C#", "OpenCover");
		extract(doc.getRootElement(), null, "all", place);
	}
	
	private void extract(Element el, String xmlNameTag, String placeType, PlacesTracker place) {
		Element summary = el.getChild("Summary");
		if (summary == null)
			// Skip ignored entries
			return;
		
		int bookmark = place.getBookmark();
		
		String name = xmlNameTag != null ? el.getChildText(xmlNameTag) : null;
		if (!isNullOrEmpty(name)) {
			if ("class".equals(placeType)) {
				if ("<Module>".equals(name))
					// Ignore module entry
					return;
				
				String[] cls = splitClassName(name);
				if (cls != null) {
					place.goInto("package", cls[0]);
					place.goInto("class", cls[1]);
				} else {
					place.goInto("class", name);
				}
				
			} else if ("method".equals(placeType)) {
				if ("true".equals(el.getAttributeValue("isGetter", "false"))
						|| "true".equals(el.getAttributeValue("isSetter", "false"))) {
					String propertyName = getPropertyName(name);
					if (propertyName != null)
						place.goInto("property", propertyName);
				}
				
				place.goInto(placeType, removeClassNameFromMethod(name));
				
			} else if (!"all".equals(placeType)) {
				place.goInto(placeType, name);
			}
		}
		
		addCoverage(summary, placeType, place);
		
		extractChildren(el, place, "Modules", "Module", "ModuleName", "library");
		extractChildren(el, place, "Classes", "Class", "FullName", "class");
		extractChildren(el, place, "Methods", "Method", "Name", "method");
		
		place.goBackTo(bookmark);
	}
	
	private final Pattern clsNameRE = Pattern.compile("([^/<>]+)\\.([^./<>]+.*)");
	
	private String[] splitClassName(String name) {
		Matcher m = clsNameRE.matcher(name);
		if (!m.matches())
			return null;
		
		return new String[] { m.group(1), m.group(2) };
	}
	
	private final Pattern methodNameRE = Pattern.compile("([^ (]+) ([^:(]+)::(.+)");
	
	private String removeClassNameFromMethod(String name) {
		Matcher m = methodNameRE.matcher(name);
		if (!m.matches())
			return name;
		
		return m.group(1) + " " + m.group(3);
	}
	
	private final Pattern propertyNameRE = Pattern.compile("([^ (]+) ([^:(]+)::(get|set)_([^(]+)\\(.*");
	
	private String getPropertyName(String name) {
		Matcher m = propertyNameRE.matcher(name);
		if (!m.matches())
			return name;
		
		return m.group(4);
	}
	
	private void extractChildren(Element el, PlacesTracker place, String xmlTag, String xmlInnerTag, String xmlNameTag,
			String placeType) {
		for (Element e1 : el.getChildren(xmlTag))
			for (Element e2 : e1.getChildren(xmlInnerTag))
				extract(e2, xmlNameTag, placeType, place);
	}
	
	private void addCoverage(Element summary, String placeType, PlacesTracker place) {
		double linesTotal = Double.parseDouble(summary.getAttributeValue("numSequencePoints"));
		double linesCovered = Double.parseDouble(summary.getAttributeValue("visitedSequencePoints"));
		place.addToData(linesCovered, "line", "covered");
		place.addToData(linesTotal, "line", "total");
		
		double branchTotal = Double.parseDouble(summary.getAttributeValue("numBranchPoints"));
		double branchCovered = Double.parseDouble(summary.getAttributeValue("visitedBranchPoints"));
		place.addToData(branchCovered, "branch", "covered");
		place.addToData(branchTotal, "branch", "total");
	}
	
}
