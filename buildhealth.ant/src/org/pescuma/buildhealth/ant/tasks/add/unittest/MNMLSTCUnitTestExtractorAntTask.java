package org.pescuma.buildhealth.ant.tasks.add.unittest;

import java.io.File;
import java.util.List;

import org.pescuma.buildhealth.ant.FileListBuildHealthAntSubTask;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.unittest.xunit.MNMLSTCUnitTestExtractor;

public class MNMLSTCUnitTestExtractorAntTask extends FileListBuildHealthAntSubTask {
	
	@Override
	protected void execute(BuildHealth buildHealth) {
		List<File> files = getFiles();
		
		buildHealth.extract(new MNMLSTCUnitTestExtractor(new PseudoFiles(files)));
	}
	
}
