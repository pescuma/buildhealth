package org.pescuma.buildhealth.extractor.coverage;

import static com.google.common.base.Strings.*;
import static org.pescuma.buildhealth.utils.StringHelper.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.datatable.DataTable;

// http://msdn.microsoft.com/en-us/library/vstudio/jj155796.aspx
// http://reportgenerator.codeplex.com/wikipage?title=Visual%20Studio%20Coverage%20Tools
public class VstestCoverageExtractor extends BaseXMLExtractor {
	
	public VstestCoverageExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String path, Document doc, DataTable data) {
		checkRoot(doc, path, "CoverageDSPriv");
		
		PlacesTracker place = new PlacesTracker(data, "C#", "vstest");
		extract(doc.getRootElement(), null, "all", place);
	}
	
	private void extract(Element el, String nameAttribute, String placeType, PlacesTracker place) {
		int bookmark = place.getBookmark();
		
		if (nameAttribute != null) {
			String name = el.getChildText(nameAttribute);
			
			if (isNullOrEmpty(name))
				name = "<no " + placeType + " name>";
			
			place.goInto(placeType, name);
		}
		
		// TODO Add "all" so it can be used for LOC
		if ("method".equals(placeType))
			addCoverage(el, placeType, place);
		
		extractChildren(el, place, "Module", "ModuleName", "library");
		extractChildren(el, place, "NamespaceTable", "NamespaceName", "package");
		extractChildren(el, place, "Class", "ClassName", "class");
		extractChildren(el, place, "Method", "MethodName", "method");
		
		place.goBackTo(bookmark);
	}
	
	private void extractChildren(Element el, PlacesTracker place, String xmlTag, String nameAttribute, String placeType) {
		for (Element e : el.getChildren(xmlTag))
			extract(e, nameAttribute, placeType, place);
	}
	
	private static void addCoverage(Element el, String placeType, PlacesTracker place) {
		int linesCovered = Integer.parseInt(firstNonEmpty(el.getChildText("LinesCovered"), "0"));
		int linesPartiallyCovered = Integer.parseInt(firstNonEmpty(el.getChildText("LinesPartiallyCovered"), "0"));
		int linesNotCovered = Integer.parseInt(firstNonEmpty(el.getChildText("LinesNotCovered"), "0"));
		
		place.addToData(linesCovered + linesPartiallyCovered, "line", "covered");
		place.addToData(linesCovered + linesPartiallyCovered + linesNotCovered, "line", "total");
		
		int blocksCovered = Integer.parseInt(firstNonEmpty(el.getChildText("BlocksCovered"), "0"));
		int blocksNotCovered = Integer.parseInt(firstNonEmpty(el.getChildText("BlocksNotCovered"), "0"));
		
		place.addToData(blocksCovered, "block", "covered");
		place.addToData(blocksCovered + blocksNotCovered, "block", "total");
	}
	
}
