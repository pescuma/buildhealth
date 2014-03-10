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

public class ProjectsFromVcprojExtractor extends BaseXMLExtractor {
	
	public ProjectsFromVcprojExtractor(PseudoFiles files) {
		super(files, "vcproj");
	}
	
	@Override
	protected void extractDocument(String path, Document doc, BuildData data) {
		Validate.notNull(path);
		
		checkRoot(doc, path, "VisualStudioProject");
		
		String project = getBaseName(path);
		File file = new File(path);
		File basePath = file.getParentFile();
		
		data.add(0, "Project", project, "BasePath", basePath.getPath());
		
		for (Element el : findElementsXPath(doc, "//File"))
			Add(data, project, basePath, el.getAttributeValue("RelativePath", ""));
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
