package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.pescuma.buildhealth.utils.ObjectUtils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.utils.Location;

public class DotNetDependencyCheckerExtractor extends BaseXMLExtractor {
	
	public DotNetDependencyCheckerExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(File inputFile, String filename, Document doc, BuildData data) {
		checkRoot(doc, "dotnet-dependency-checker", filename);
		
		for (Element entry : findElementsXPath(doc, "/dotnet-dependency-checker/Entry")) {
			String type = entry.getAttributeValue("Type", "");
			String severity = entry.getAttributeValue("Severity", "");
			String message = firstNonNull(entry.getChildText("Message"), "");
			
			if (message.isEmpty())
				continue;
			
			List<Location> locations = new ArrayList<Location>();
			
			for (Element location : findElementsXPath(entry, "*//Location")) {
				String file = location.getAttributeValue("File", "");
				String line = location.getAttributeValue("Line", "");
				if (file.isEmpty())
					continue;
				
				locations.add(new Location(file, Integer.parseInt(line)));
			}
			
			if (locations.isEmpty()) {
				// It's a project uniqueness error
				for (Element location : findElementsXPath(entry, "*//Path")) {
					String file = location.getText();
					if (file.isEmpty())
						continue;
					
					locations.add(new Location(file));
				}
			}
			
			data.add(1, "Static analysis", "C#", "dotnet-dependency-checker", Location.toFormatedString(locations),
					type, message, toBuildHealthSeverity(severity));
		}
	}
	
	private String toBuildHealthSeverity(String severity) {
		if ("Error".equalsIgnoreCase(severity))
			return "High";
		if ("Warning".equalsIgnoreCase(severity))
			return "Medium";
		if ("Info".equalsIgnoreCase(severity))
			return "Low";
		return severity;
	}
	
}
