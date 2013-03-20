package org.pescuma.buildhealth.ant;

import java.io.File;
import java.util.List;

import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.loc.CLOCExtractor;

public class CLOCExtractorAntTask extends FileListTask {
	
	@Override
	protected void execute(BuildHealth buildHealth) {
		List<File> files = getFiles();
		
		buildHealth.extract(new CLOCExtractor(new PseudoFiles(files)));
	}
	
}
