package org.pescuma.buildhealth.extractor;

import static org.apache.commons.io.IOUtils.*;
import static org.pescuma.buildhealth.utils.FileHelper.*;
import static org.pescuma.buildhealth.utils.ObjectUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.commons.lang.Validate;
import org.pescuma.buildhealth.core.BuildData;

public abstract class BaseBuildDataExtractor implements BuildDataExtractor {
	
	private final PseudoFiles files;
	private final String[] extensions;
	
	public BaseBuildDataExtractor(PseudoFiles files, String... extensions) {
		Validate.notNull(files);
		
		this.files = files;
		this.extensions = firstNonNull(extensions, new String[0]);
	}
	
	@Override
	public void extractTo(BuildData data, BuildDataExtractorTracker tracker) {
		try {
			
			if (files.isStream()) {
				extractStream(files.getStreamPath(), files.getStream(), data);
				tracker.onStreamProcessed();
				
			} else {
				Collection<File> toProcess = (extensions.length == 0 ? files.getFilesByExtension() : files
						.getFilesByExtension(extensions));
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
			extract(getCanonicalPath(file), input, data);
		} finally {
			closeQuietly(input);
		}
	}
	
	private void extractStream(String path, InputStream input, BuildData data) throws IOException {
		extract(path, input, data);
	}
	
	protected abstract void extract(String path, InputStream input, BuildData data) throws IOException;
	
}
