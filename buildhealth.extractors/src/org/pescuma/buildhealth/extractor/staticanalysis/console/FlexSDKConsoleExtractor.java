package org.pescuma.buildhealth.extractor.staticanalysis.console;

import java.io.IOException;
import java.io.InputStream;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class FlexSDKConsoleExtractor extends BaseBuildDataExtractor {

	public FlexSDKConsoleExtractor(PseudoFiles files) {
		super(files, "txt", "out");
	}

	@Override
	protected void extract(String filename, InputStream input, BuildData data) throws IOException {
		WarningsHelper.extractFromParser("Flex SDK Compilers (compc & mxmlc)", new hudson.plugins.warnings.parser.FlexSDKParser(),
				input, data);
	}

}
