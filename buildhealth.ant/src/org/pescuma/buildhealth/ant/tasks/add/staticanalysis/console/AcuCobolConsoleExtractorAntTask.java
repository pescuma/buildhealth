package org.pescuma.buildhealth.ant.tasks.add.staticanalysis.console;

import org.pescuma.buildhealth.ant.FileListBuildHealthAntSubTask;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.AcuCobolConsoleExtractor;

public class AcuCobolConsoleExtractorAntTask extends FileListBuildHealthAntSubTask {

	@Override
	protected void execute(BuildHealth buildHealth) {
		buildHealth.extract(new AcuCobolConsoleExtractor(new PseudoFiles(getFiles())));
	}

}
