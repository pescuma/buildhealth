package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.pescuma.buildhealth.extractor.utils.FilenameToLanguage.*;
import static org.pescuma.buildhealth.utils.StringHelper.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.utils.Location;

public class ResharperDupFinderExtractor extends BaseXMLExtractor {
	
	public ResharperDupFinderExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(File inputFile, String filename, Document doc, BuildData data) {
		checkRoot(doc, "DuplicatesReport", filename);
		
		for (Element duplicate : findElementsXPath(doc, "//Duplicate")) {
			List<Location> locations = new ArrayList<Location>();
			
			for (Element fs : duplicate.getChildren("Fragment")) {
				String file = fs.getChildTextTrim("FileName");
				Element lines = fs.getChild("LineRange");
				
				if (file == null || lines == null)
					continue;
				
				locations.add(Location.create(file, lines.getAttributeValue("Start"), null,
						lines.getAttributeValue("End"), null));
			}
			
			if (locations.size() < 2)
				continue;
			
			String cost = duplicate.getAttributeValue("Cost");
			
			String language = firstNonEmpty(detectLanguage(locations.get(0).file), "C#");
			data.add(1, "Static analysis", language, "Resharper DupFinder", Location.toFormatedString(locations),
					"Code duplication", getMessageFor(locations), "", "Cost: " + cost);
		}
	}
	
	private String getMessageFor(List<Location> locations) {
		StringBuilder result = new StringBuilder();
		
		int lines = 0;
		for (Location loc : locations)
			lines += loc.endLine - loc.beginLine + 1;
		lines = Math.round(lines / (float) locations.size());
		
		result.append("There are ").append(lines).append(" lines of code duplicated in:");
		
		for (Location loc : locations)
			result.append("\n  ").append(loc.file).append(" lines ").append(loc.beginLine).append(" to ")
					.append(loc.endLine);
		
		return result.toString();
	}
}
