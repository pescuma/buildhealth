package org.pescuma.buildhealth.extractor.staticanalysis.console;

import java.io.IOException;
import java.io.InputStream;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class BuckminsterConsoleExtractor extends BaseBuildDataExtractor {

	public BuckminsterConsoleExtractor(PseudoFiles files) {
		super(files, "txt", "out");
	}

	@Override
	protected void extract(String path, InputStream input, BuildData data) throws IOException {
		WarningsHelper.extractFromParser("Buckminster", new hudson.plugins.warnings.parser.BuckminsterParser(),
				input, data);
	}

}
