package org.pescuma.buildhealth.ant.tasks.add.coverage;

import org.pescuma.buildhealth.ant.FileListBuildHealthAntSubTask;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.coverage.DotCoverExtractor;

public class DotCoverExtractorAntTask extends FileListBuildHealthAntSubTask {
	
	@Override
	protected void execute(BuildHealth buildHealth) {
		buildHealth.extract(new DotCoverExtractor(new PseudoFiles(getFiles())));
	}
	
}
