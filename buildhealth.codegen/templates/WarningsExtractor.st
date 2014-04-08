package org.pescuma.buildhealth.extractor.staticanalysis.console;

import java.io.IOException;
import java.io.Reader;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class $class$ extends BaseBuildDataExtractor {
	
	public $class$(PseudoFiles files) {
		super(files, "txt", "out");
	}
	
	@Override
	protected void extract(String path, Reader input, BuildData data) throws IOException {
		WarningsHelper.extractFromParser("$parser.description$", new $parser.className$(),
				input, data);
	}
	
}
