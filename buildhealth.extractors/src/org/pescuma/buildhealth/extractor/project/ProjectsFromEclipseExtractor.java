package org.pescuma.buildhealth.extractor.project;

import static org.pescuma.buildhealth.extractor.BaseXMLExtractor.*;

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
import org.pescuma.buildhealth.extractor.BuildDataExtractorException;
import org.pescuma.buildhealth.extractor.BuildDataExtractorTracker;
import org.pescuma.buildhealth.extractor.JDomUtil;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.utils.FilenameToLanguage;
import org.pescuma.buildhealth.utils.FileHelper;

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
		
		try {
			
			for (File projectFile : files.getFilesByName(PROJECT_FILENAME)) {
				tracker.onFileProcessed(projectFile);
				processProject(data, projectFile);
			}
			
		} catch (JDOMException e) {
			throw new BuildDataExtractorException(e);
		} catch (IOException e) {
			throw new BuildDataExtractorException(e);
		}
	}
	
	private void processProject(BuildData data, File projFile) throws IOException, JDOMException {
		Document projDoc = JDomUtil.parse(projFile);
		checkRoot(projDoc, "projectDescription", PROJECT_FILENAME);
		
		String name = findElementXPath(projDoc, "/projectDescription/name").getText();
		
		data.add(0, "Project", name, "BasePath", projFile.getParent());
		
		File classpathFile = new File(projFile.getParentFile(), CLASSPATH_FILENAME);
		if (classpathFile.exists())
			processClasspath(data, name, classpathFile);
	}
	
	private void processClasspath(BuildData data, String projectName, File classpathFile) throws JDOMException,
			IOException {
		Document classpathDoc = JDomUtil.parse(classpathFile);
		checkRoot(classpathDoc, "classpath", CLASSPATH_FILENAME);
		
		for (Element cp : findElementsXPath(classpathDoc, "//classpathentry[@kind='src']")) {
			String path = cp.getAttributeValue("path", "");
			if (path.isEmpty() || path.startsWith("/"))
				continue;
			
			File srcPath = new File(classpathFile.getParentFile(), path);
			for (File file : FileUtils.listFiles(srcPath, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
				String language = FilenameToLanguage.detectLanguage(file.getName());
				
				if (language != null)
					data.add(0, "Project", projectName, "File", language, FileHelper.getCanonicalPath(file));
			}
		}
	}
}
