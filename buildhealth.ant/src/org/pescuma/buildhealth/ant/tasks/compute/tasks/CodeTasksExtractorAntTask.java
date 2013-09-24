package org.pescuma.buildhealth.ant.tasks.compute.tasks;

import java.io.File;
import java.util.List;

import org.pescuma.buildhealth.ant.FileListBuildHealthAntSubTask;
import org.pescuma.buildhealth.computer.tasks.CodeTasksComputer;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class CodeTasksExtractorAntTask extends FileListBuildHealthAntSubTask {
	
	@Override
	protected void execute(BuildHealth buildHealth) {
		List<File> files = getFiles();
		
		buildHealth.compute(new CodeTasksComputer(new PseudoFiles(files)));
	}
	
}
