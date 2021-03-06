package org.pescuma.buildhealth.ant.tasks.add.staticanalysis.console;

import org.pescuma.buildhealth.ant.FileListBuildHealthAntSubTask;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.Gcc4CompilerConsoleExtractor;

public class Gcc4CompilerConsoleExtractorAntTask extends FileListBuildHealthAntSubTask {

	@Override
	protected void execute(BuildHealth buildHealth) {
		buildHealth.extract(new Gcc4CompilerConsoleExtractor(new PseudoFiles(getFiles())));
	}

}
