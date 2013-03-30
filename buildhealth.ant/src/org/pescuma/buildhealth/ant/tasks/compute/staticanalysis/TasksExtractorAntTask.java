package org.pescuma.buildhealth.ant.tasks.compute.staticanalysis;

import java.io.File;
import java.util.List;

import org.pescuma.buildhealth.ant.FileListBuildHealthAntSubTask;
import org.pescuma.buildhealth.computer.staticanalysis.TasksComputer;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class TasksExtractorAntTask extends FileListBuildHealthAntSubTask {
	
	@Override
	protected void execute(BuildHealth buildHealth) {
		List<File> files = getFiles();
		
		buildHealth.compute(new TasksComputer(new PseudoFiles(files)));
	}
	
}
