package org.pescuma.buildhealth.ant.tasks.add.staticanalysis.console;

import org.pescuma.buildhealth.ant.FileListBuildHealthAntSubTask;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.PerlCriticConsoleExtractor;

public class PerlCriticConsoleExtractorAntTask extends FileListBuildHealthAntSubTask {

	@Override
	protected void execute(BuildHealth buildHealth) {
		buildHealth.extract(new PerlCriticConsoleExtractor(new PseudoFiles(getFiles())));
	}

}
