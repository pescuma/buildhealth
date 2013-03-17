package org.pescuma.buildhealth.extractor.loc;

import static java.lang.Integer.*;
import static java.lang.Math.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildDataExtractorTracker;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.BuildDataExtractorException;
import org.pescuma.buildhealth.extractor.PseudoFiles;

import au.com.bytecode.opencsv.CSVReader;

public class CLOCExtractor implements BuildDataExtractor {
	
	private final PseudoFiles files;
	
	public CLOCExtractor(PseudoFiles files) {
		this.files = files;
	}
	
	@Override
	public void extractTo(BuildData data, BuildDataExtractorTracker tracker) {
		try {
			
			if (files.isStream()) {
				extractStream(files.getStream(), data);
				tracker.streamProcessed();
				
			} else {
				for (File file : files.getFiles("csv")) {
					extractFile(file, data);
					tracker.fileProcessed(file);
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
		
		String[] headers = null;
		int languageCol = -1;
		int filenameCol = -1;
		int filesCol = -1;
		
		String[] line;
		while ((line = reader.readNext()) != null) {
			if (headers == null) {
				headers = line;
				languageCol = findHeader(headers, "language");
				filenameCol = findHeader(headers, "filename");
				filesCol = findHeader(headers, "files");
				if (languageCol < 0)
					throw new IllegalStateException("Invalid cloc file: missing language column");
				if ((filesCol < 0) == (filenameCol < 0))
					throw new IllegalStateException(
							"Invalid cloc file: one column (and only one) of filename / files should exist");
				continue;
			}
			
			if (headers.length <= languageCol)
				// ?
				continue;
			
			String language = line[languageCol];
			String filename = (filenameCol < 0 ? "" : line[filenameCol]);
			
			if (filenameCol >= 0)
				data.add(1, "LOC", language, "files", filename);
			else
				data.add(parseInt(line[filesCol]), "LOC", language, "files");
			
			int length = min(line.length, headers.length);
			for (int i = 0; i < length; i++) {
				if (i == languageCol || i == filenameCol || i == filesCol)
					continue;
				
				int val = parseInt(line[i]);
				data.add(val, "LOC", language, headers[i], filename);
			}
		}
	}
	
	private int findHeader(String[] headers, String string) {
		for (int i = 0; i < headers.length; i++)
			if (string.equalsIgnoreCase(headers[i]))
				return i;
		return -1;
	}
}
