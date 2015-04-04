package org.pescuma.buildhealth.extractor.staticanalysis.console;

import java.io.IOException;
import java.io.Reader;

import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.datatable.DataTable;

public class XlcCompilerConsoleExtractor extends BaseBuildDataExtractor {
	
	public XlcCompilerConsoleExtractor(PseudoFiles files) {
		super(files, "txt", "out");
	}
	
	@Override
	protected void extract(String path, Reader input, DataTable data) throws IOException {
		WarningsHelper.extractFromParser("IBM XLC Compiler", new hudson.plugins.warnings.parser.XlcCompilerParser(),
				input, data);
	}
	
}
