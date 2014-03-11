package org.pescuma.buildhealth.ant.tasks.add.coverage;

import org.pescuma.buildhealth.ant.FileListBuildHealthAntSubTask;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.coverage.OpenCoverExtractor;

public class OpenCoverExtractorAntTask extends FileListBuildHealthAntSubTask {
	
	@Override
	protected void execute(BuildHealth buildHealth) {
		buildHealth.extract(new OpenCoverExtractor(new PseudoFiles(getFiles())));
	}
	
}
