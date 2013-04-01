package org.pescuma.buildhealth.extractor;

import static org.apache.commons.io.FilenameUtils.*;
import static org.apache.commons.io.IOUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.pescuma.buildhealth.core.BuildData;

public abstract class BaseBuildDataExtractor implements BuildDataExtractor {
	
	private final PseudoFiles files;
	private final String extension;
	
	public BaseBuildDataExtractor(PseudoFiles files, String extension) {
		if (files == null)
			throw new IllegalArgumentException();
		
		this.files = files;
		this.extension = extension;
	}
	
	@Override
	public void extractTo(BuildData data, BuildDataExtractorTracker tracker) {
		try {
			
			if (files.isStream()) {
				extractStream(files.getStreamFilename(), files.getStream(), data);
				tracker.onStreamProcessed();
				
			} else {
				Collection<File> toProcess = (extension == null ? files.getFiles() : files.getFiles(extension));
				for (File file : toProcess) {
					extractFile(file, data);
					tracker.onFileProcessed(file);
				}
			}
			
		} catch (IOException e) {
			throw new BuildDataExtractorException(e);
		}
	}
	
	private void extractFile(File file, BuildData data) throws IOException {
		InputStream input = new FileInputStream(file);
		try {
			extract(getBaseName(file.getName()), input, data);
		} finally {
			closeQuietly(input);
		}
	}
	
	private void extractStream(String filename, InputStream input, BuildData data) throws IOException {
		extract(getBaseName(filename), input, data);
	}
	
	protected abstract void extract(String filename, InputStream input, BuildData data) throws IOException;
	
}
