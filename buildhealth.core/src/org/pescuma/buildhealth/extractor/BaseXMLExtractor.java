package org.pescuma.buildhealth.extractor;

import static org.apache.commons.io.FilenameUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.pescuma.buildhealth.core.BuildData;

public abstract class BaseXMLExtractor implements BuildDataExtractor {
	
	private final PseudoFiles files;
	
	public BaseXMLExtractor(PseudoFiles files) {
		if (files == null)
			throw new IllegalArgumentException();
		
		this.files = files;
	}
	
	@Override
	public void extractTo(BuildData data, BuildDataExtractorTracker tracker) {
		try {
			
			if (files.isStream()) {
				extractStream(files.getStreamFilename(), files.getStream(), data);
				tracker.onStreamProcessed();
				
			} else {
				for (File file : files.getFiles("xml")) {
					extractFile(file, data);
					tracker.onFileProcessed(file);
				}
			}
			
		} catch (JDOMException e) {
			throw new BuildDataExtractorException(e);
		} catch (IOException e) {
			throw new BuildDataExtractorException(e);
		}
	}
	
	private void extractFile(File file, BuildData data) throws JDOMException, IOException {
		Document doc = JDomUtil.parse(file);
		extractDocument(getBaseName(file.getName()), doc, data);
	}
	
	private void extractStream(String filename, InputStream input, BuildData data) throws JDOMException, IOException {
		Document doc = JDomUtil.parse(input);
		extractDocument(getBaseName(filename), doc, data);
	}
	
	protected abstract void extractDocument(String filename, Document doc, BuildData data);
	
	protected void checkRoot(Document doc, String name) {
		if (!doc.getRootElement().getName().equals(name))
			throw new BuildDataExtractorException("Invalid file format: top node must be " + name);
	}
	
}
