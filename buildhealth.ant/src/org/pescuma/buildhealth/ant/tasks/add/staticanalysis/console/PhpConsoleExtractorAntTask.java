package org.pescuma.buildhealth.ant.tasks.add.staticanalysis.console;

import java.io.File;
import java.util.List;

import org.pescuma.buildhealth.ant.FileListBuildHealthAntSubTask;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.PhpConsoleExtractor;

public class PhpConsoleExtractorAntTask extends FileListBuildHealthAntSubTask {

	@Override
	protected void execute(BuildHealth buildHealth) {
		buildHealth.extract(new PhpConsoleExtractor(new PseudoFiles(getFiles())));
	}

}
