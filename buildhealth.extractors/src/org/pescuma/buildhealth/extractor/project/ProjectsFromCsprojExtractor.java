package org.pescuma.buildhealth.extractor.project;

import static org.pescuma.buildhealth.utils.FileHelper.*;

import java.io.File;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.utils.FilenameToLanguage;

public class ProjectsFromCsprojExtractor extends BaseXMLExtractor {
	
	public ProjectsFromCsprojExtractor(PseudoFiles files) {
		super(files, "csproj");
	}
	
	@Override
	protected void extractDocument(File file, String filename, Document doc, BuildData data) {
		checkRoot(doc, "Project", filename);
		removeNamespace(doc, "http://schemas.microsoft.com/developer/msbuild/2003", filename);
		
		File basePath = file.getParentFile();
		
		data.add(0, "Project", filename, "BasePath", basePath.getPath());
		
		for (Element el : findElementsXPath(doc, "//Compile")) {
			String include = el.getAttributeValue("Include", "");
			if (include.isEmpty())
				continue;
			
			String language = FilenameToLanguage.detectLanguage(include);
			if (language == null)
				continue;
			
			data.add(0, "Project", filename, "File", language, getCanonicalPath(new File(basePath, include)));
		}
	}
	
}
