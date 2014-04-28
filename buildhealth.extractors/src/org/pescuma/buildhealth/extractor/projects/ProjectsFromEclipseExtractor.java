package org.pescuma.buildhealth.extractor.projects;

import static org.pescuma.buildhealth.extractor.BaseXMLExtractor.*;
import static org.pescuma.buildhealth.utils.FileHelper.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.Validate;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.BuildDataExtractorTracker;
import org.pescuma.buildhealth.extractor.JDomUtil;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.utils.FilenameToLanguage;

public class ProjectsFromEclipseExtractor implements BuildDataExtractor {
	
	private static final String PROJECT_FILENAME = ".project";
	private static final String CLASSPATH_FILENAME = ".classpath";
	
	private final PseudoFiles files;
	
	public ProjectsFromEclipseExtractor(PseudoFiles files) {
		Validate.notNull(files);
		
		this.files = files;
	}
	
	@Override
	public void extractTo(BuildData data, BuildDataExtractorTracker tracker) {
		// Streams are not supported yet
		Validate.isTrue(!files.isStream());
		
		for (File projectFile : files.getFilesByName(PROJECT_FILENAME)) {
			try {
				
				processProject(data, projectFile);
				tracker.onFileProcessed(projectFile);
				
			} catch (JDOMException e) {
				tracker.onErrorProcessingFile(projectFile, e);
			} catch (IOException e) {
				tracker.onErrorProcessingFile(projectFile, e);
			}
		}
	}
	
	private void processProject(BuildData data, File projFile) throws IOException, JDOMException {
		Document projDoc = JDomUtil.parse(projFile);
		checkRoot(projDoc, PROJECT_FILENAME, "projectDescription");
		
		String name = findElementXPath(projDoc, "/projectDescription/name").getText();
		
		data.add(0, "Project", name, "BasePath", projFile.getParent());
		
		File classpathFile = new File(projFile.getParentFile(), CLASSPATH_FILENAME);
		if (classpathFile.exists())
			processClasspath(data, name, classpathFile);
	}
	
	private void processClasspath(BuildData data, String projectName, File classpathFile) throws JDOMException,
			IOException {
		Document classpathDoc = JDomUtil.parse(classpathFile);
		checkRoot(classpathDoc, CLASSPATH_FILENAME, "classpath");
		
		for (Element cp : findElementsXPath(classpathDoc, "//classpathentry[@kind='src']")) {
			String path = cp.getAttributeValue("path", "");
			if (path.isEmpty() || path.startsWith("/"))
				continue;
			
			File srcPath = new File(classpathFile.getParentFile(), path);
			if (!srcPath.exists())
				continue;
			
			for (File file : FileUtils.listFiles(srcPath, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
				String language = FilenameToLanguage.detectLanguage(file.getName());
				if (language == null)
					continue;
				
				data.add(0, "Project", projectName, "File", language, getCanonicalPath(file));
			}
		}
	}
}
