package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.pescuma.buildhealth.utils.ObjectUtils.*;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.utils.Location;
import org.pescuma.datatable.DataTable;

public class DependencyCheckerExtractor extends BaseXMLExtractor {
	
	public DependencyCheckerExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String path, Document doc, DataTable data) {
		checkRoot(doc, path, "DependencyChecker-Results");
		
		for (Element entry : findElementsXPath(doc, "//Entry")) {
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
			
			data.add(1, "Static analysis", "C#", "Dependency Checker", Location.toFormatedString(locations), type,
					message, toBuildHealthSeverity(severity));
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
