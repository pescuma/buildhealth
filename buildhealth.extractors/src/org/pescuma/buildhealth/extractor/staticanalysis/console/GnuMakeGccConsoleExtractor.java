package org.pescuma.buildhealth.extractor.staticanalysis.console;

import java.io.IOException;
import java.io.Reader;

import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.datatable.DataTable;

public class GnuMakeGccConsoleExtractor extends BaseBuildDataExtractor {
	
	public GnuMakeGccConsoleExtractor(PseudoFiles files) {
		super(files, "txt", "out");
	}
	
	@Override
	protected void extract(String path, Reader input, DataTable data) throws IOException {
		WarningsHelper.extractFromParser("GNU Make + GNU C Compiler (gcc)",
				new hudson.plugins.warnings.parser.GnuMakeGccParser(), input, data);
	}
	
}
