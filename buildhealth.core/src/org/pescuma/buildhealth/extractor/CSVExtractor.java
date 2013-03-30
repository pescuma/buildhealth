package org.pescuma.buildhealth.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.pescuma.buildhealth.core.BuildData;

import au.com.bytecode.opencsv.CSVReader;

public class CSVExtractor implements BuildDataExtractor {
	
	private final PseudoFiles files;
	
	public CSVExtractor(PseudoFiles files) {
		this.files = files;
	}
	
	@Override
	public void extractTo(BuildData data, BuildDataExtractorTracker tracker) {
		try {
			
			if (files.isStream()) {
				extractStream(files.getStream(), data);
				tracker.onStreamProcessed();
				
			} else {
				for (File file : files.getFiles("csv")) {
					extractFile(file, data);
					tracker.onFileProcessed(file);
				}
			}
			
		} catch (IOException e) {
			throw new BuildDataExtractorException(e);
		}
	}
	
	private void extractFile(File file, BuildData data) throws IOException {
		InputStream stream = new FileInputStream(file);
		try {
			extractStream(stream, data);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	private void extractStream(InputStream stream, BuildData data) throws IOException {
		@SuppressWarnings("resource")
		CSVReader reader = new CSVReader(new InputStreamReader(stream));
		
		String[] line;
		while ((line = reader.readNext()) != null) {
			if (line.length < 2)
				continue;
			
			double val = Double.parseDouble(line[0]);
			line = Arrays.copyOfRange(line, 1, line.length);
			data.add(val, line);
		}
	}
	
}
