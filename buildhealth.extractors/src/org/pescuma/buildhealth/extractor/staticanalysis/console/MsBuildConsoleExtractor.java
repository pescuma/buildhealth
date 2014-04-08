package org.pescuma.buildhealth.extractor.staticanalysis.console;

import java.io.IOException;
import java.io.Reader;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class MsBuildConsoleExtractor extends BaseBuildDataExtractor {

	public MsBuildConsoleExtractor(PseudoFiles files) {
		super(files, "txt", "out");
	}

	@Override
	protected void extract(String path, Reader input, BuildData data) throws IOException {
		WarningsHelper.extractFromParser("MSBuild", new hudson.plugins.warnings.parser.MsBuildParser(),
				input, data);
	}

}
