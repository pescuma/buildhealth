package org.pescuma.buildhealth.extractor;

import static org.apache.commons.io.IOUtils.*;
import static org.pescuma.buildhealth.extractor.utils.EncodingHelper.*;
import static org.pescuma.buildhealth.utils.FileHelper.*;
import static org.pescuma.buildhealth.utils.ObjectUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;

import org.apache.commons.lang.Validate;
import org.pescuma.datatable.DataTable;

public abstract class BaseBuildDataExtractor implements BuildDataExtractor {
	
	private final PseudoFiles files;
	private final String[] extensions;
	
	public BaseBuildDataExtractor(PseudoFiles files, String... extensions) {
		Validate.notNull(files);
		
		this.files = files;
		this.extensions = firstNonNull(extensions, new String[0]);
	}
	
	@Override
	public void extractTo(DataTable data, BuildDataExtractorTracker tracker) {
		
		if (files.isStream()) {
			try {
				
				extractStream(files.getStreamPath(), files.getStream(), data);
				tracker.onStreamProcessed();
				
			} catch (IOException e) {
				throw new BuildDataExtractorException(e);
			}
			
		} else {
			Collection<File> toProcess = (extensions.length == 0 ? files.getFilesByExtension() : files
					.getFilesByExtension(extensions));
			for (File file : toProcess) {
				try {
					
					extractFile(file, data);
					tracker.onFileProcessed(file);
					
				} catch (BuildDataExtractorException e) {
					tracker.onErrorProcessingFile(file, e);
				} catch (IOException e) {
					tracker.onErrorProcessingFile(file, e);
				}
			}
		}
	}
	
	private void extractFile(File file, DataTable data) throws IOException {
		InputStream input = new FileInputStream(file);
		try {
			extract(getCanonicalPath(file), input, data);
		} finally {
			closeQuietly(input);
		}
	}
	
	private void extractStream(String path, InputStream input, DataTable data) throws IOException {
		extract(path, input, data);
	}
	
	protected void extract(String path, InputStream input, DataTable data) throws IOException {
		extract(path, toReader(input), data);
	}
	
	protected abstract void extract(String path, Reader input, DataTable data) throws IOException;
	
}
