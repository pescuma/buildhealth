package org.pescuma.buildhealth.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.pescuma.buildhealth.core.BuildData;

import au.com.bytecode.opencsv.CSVReader;

public class CSVExtractor extends BaseBuildDataExtractor {
	
	public CSVExtractor(PseudoFiles files) {
		super(files, "csv");
	}
	
	@Override
	protected void extract(String filename, InputStream input, BuildData data) throws IOException {
		@SuppressWarnings("resource")
		CSVReader reader = new CSVReader(new InputStreamReader(input));
		
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