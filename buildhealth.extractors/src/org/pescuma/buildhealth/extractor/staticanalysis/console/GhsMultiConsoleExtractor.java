package org.pescuma.buildhealth.extractor.staticanalysis.console;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class GhsMultiConsoleExtractor extends BaseBuildDataExtractor {

	public GhsMultiConsoleExtractor(PseudoFiles files) {
		super(files, "txt", "out");
	}

	@Override
	protected void extract(File file, String filename, InputStream input, BuildData data) throws IOException {
		WarningsHelper.extractFromParser("GHS Multi Compiler", new hudson.plugins.warnings.parser.GhsMultiParser(),
				input, data);
	}

}
