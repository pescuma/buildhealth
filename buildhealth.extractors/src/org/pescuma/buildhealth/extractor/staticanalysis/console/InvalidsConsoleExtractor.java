package org.pescuma.buildhealth.extractor.staticanalysis.console;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class InvalidsConsoleExtractor extends BaseBuildDataExtractor {

	public InvalidsConsoleExtractor(PseudoFiles files) {
		super(files, "txt", "out");
	}

	@Override
	protected void extract(File file, String filename, InputStream input, BuildData data) throws IOException {
		WarningsHelper.extractFromParser("Oracle Invalids", new hudson.plugins.warnings.parser.InvalidsParser(),
				input, data);
	}

}
