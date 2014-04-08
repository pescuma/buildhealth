package org.pescuma.buildhealth.extractor.staticanalysis.console;

import java.io.IOException;
import java.io.Reader;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class ArmccCompilerConsoleExtractor extends BaseBuildDataExtractor {

	public ArmccCompilerConsoleExtractor(PseudoFiles files) {
		super(files, "txt", "out");
	}

	@Override
	protected void extract(String path, Reader input, BuildData data) throws IOException {
		WarningsHelper.extractFromParser("Armcc Compiler", new hudson.plugins.warnings.parser.ArmccCompilerParser(),
				input, data);
	}

}
