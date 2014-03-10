package org.pescuma.buildhealth.extractor.project;

import static org.pescuma.buildhealth.utils.FileHelper.*;

import java.io.File;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.utils.FilenameToLanguage;

public class ProjectsFromVsprojExtractor extends BaseXMLExtractor {
	
	public ProjectsFromVsprojExtractor(PseudoFiles files) {
		super(files, "vcxproj", "csproj", "vbproj", "fsproj");
	}
	
	@Override
	protected void extractDocument(File file, String filename, Document doc, BuildData data) {
		checkRoot(doc, "Project", filename);
		removeNamespace(doc, "http://schemas.microsoft.com/developer/msbuild/2003", filename);
		
		File basePath = file.getParentFile();
		
		data.add(0, "Project", filename, "BasePath", basePath.getPath());
		
		for (Element el : findElementsXPath(doc, "//Compile"))
			Add(data, filename, basePath, el.getAttributeValue("Include", ""));
		
		for (Element el : findElementsXPath(doc, "//EmbeddedResource"))
			Add(data, filename, basePath, el.getAttributeValue("Include", ""));
		
		for (Element el : findElementsXPath(doc, "//None"))
			Add(data, filename, basePath, el.getAttributeValue("Include", ""));
		
		for (Element el : findElementsXPath(doc, "//ClCompile"))
			Add(data, filename, basePath, el.getAttributeValue("Include", ""));
		
		for (Element el : findElementsXPath(doc, "//ClInclude"))
			Add(data, filename, basePath, el.getAttributeValue("Include", ""));
	}
	
	private void Add(BuildData data, String project, File basePath, String include) {
		if (include.isEmpty())
			return;
		
		String language = FilenameToLanguage.detectLanguage(include);
		if (language == null)
			return;
		
		data.add(0, "Project", project, "File", language, getCanonicalPath(new File(basePath, include)));
	}
	
}
