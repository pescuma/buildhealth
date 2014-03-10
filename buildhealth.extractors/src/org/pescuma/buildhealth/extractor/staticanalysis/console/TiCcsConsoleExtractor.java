package org.pescuma.buildhealth.extractor.staticanalysis.console;

import java.io.IOException;
import java.io.InputStream;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class TiCcsConsoleExtractor extends BaseBuildDataExtractor {

	public TiCcsConsoleExtractor(PseudoFiles files) {
		super(files, "txt", "out");
	}

	@Override
	protected void extract(String path, InputStream input, BuildData data) throws IOException {
		WarningsHelper.extractFromParser("Texas Instruments Code Composer Studio (C/C++)", new hudson.plugins.warnings.parser.TiCcsParser(),
				input, data);
	}

}
