package org.pescuma.buildhealth.extractor.project;

import static org.apache.commons.io.FilenameUtils.*;
import static org.pescuma.buildhealth.utils.FileHelper.*;

import java.io.File;

import org.apache.commons.lang.Validate;
import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.utils.FilenameToLanguage;

public class ProjectsFromVisualStudioExtractor extends BaseXMLExtractor {
	
	public ProjectsFromVisualStudioExtractor(PseudoFiles files) {
		super(files, "vcproj", "vcxproj", "csproj", "vbproj", "fsproj");
	}
	
	@Override
	protected void extractDocument(String path, Document doc, BuildData data) {
		Validate.notNull(path);
		
		if (doc.getRootElement().getName().equals("VisualStudioProject")) {
			// vcproj
			
			checkRoot(doc, path, "VisualStudioProject");
			
			String project = getBaseName(path);
			File file = new File(path);
			File basePath = file.getParentFile();
			
			AddBasePath(data, project, basePath);
			
			for (Element el : findElementsXPath(doc, "//File"))
				AddFile(data, project, basePath, el.getAttributeValue("RelativePath", ""));
			
		} else {
			checkRoot(doc, path, "Project");
			removeNamespace(doc, "http://schemas.microsoft.com/developer/msbuild/2003", path);
			
			String project = getBaseName(path);
			File file = new File(path);
			File basePath = file.getParentFile();
			
			AddBasePath(data, project, basePath);
			
			for (Element el : findElementsXPath(doc, "//Compile"))
				AddFile(data, project, basePath, el.getAttributeValue("Include", ""));
			
			for (Element el : findElementsXPath(doc, "//EmbeddedResource"))
				AddFile(data, project, basePath, el.getAttributeValue("Include", ""));
			
			for (Element el : findElementsXPath(doc, "//None"))
				AddFile(data, project, basePath, el.getAttributeValue("Include", ""));
			
			for (Element el : findElementsXPath(doc, "//ClCompile"))
				AddFile(data, project, basePath, el.getAttributeValue("Include", ""));
			
			for (Element el : findElementsXPath(doc, "//ClInclude"))
				AddFile(data, project, basePath, el.getAttributeValue("Include", ""));
		}
	}
	
	private void AddBasePath(BuildData data, String project, File basePath) {
		data.add(0, "Project", project, "BasePath", basePath.getPath());
	}
	
	private void AddFile(BuildData data, String project, File basePath, String include) {
		if (include.isEmpty())
			return;
		
		String language = FilenameToLanguage.detectLanguage(include);
		if (language == null)
			return;
		
		data.add(0, "Project", project, "File", language, getCanonicalPath(new File(basePath, include)));
	}
	
}
