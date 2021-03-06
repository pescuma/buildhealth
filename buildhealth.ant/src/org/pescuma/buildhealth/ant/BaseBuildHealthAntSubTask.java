package org.pescuma.buildhealth.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.pescuma.buildhealth.computer.BuildDataComputer;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.core.listener.AbstractBuildHealthListener;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;

public abstract class BaseBuildHealthAntSubTask extends Task {
	
	@Override
	public void execute() throws BuildException {
		BuildHealth buildHealth = BuildHealthAntTask.buildHealth;
		if (buildHealth == null)
			throw new BuildException("This task must be inside a buildhealth task.", getLocation());
		
		AbstractBuildHealthListener listener = new AbstractBuildHealthListener() {
			@Override
			public void onFileExtracted(BuildDataExtractor extractor, File file) {
				log("File processed: " + file);
			}
			
			@Override
			public void onErrorExtractingFile(BuildDataExtractor extractor, File file, Exception ex) {
				log("Error processing file: " + file + " : " + ex.getMessage(), ex, Project.MSG_WARN);
			}
			
			@Override
			public void onFileComputed(BuildDataComputer computer, File file) {
				log("File computed: " + file);
			}
			
			@Override
			public void onErrorComputingFile(BuildDataComputer computer, File file, Exception ex) {
				log("Error computing file: " + file + " : " + ex.getMessage(), ex, Project.MSG_WARN);
			}
			
			@Override
			public void onOtherExtracted(BuildDataExtractor extractor, String message) {
				log(message);
			}
		};
		buildHealth.addListener(listener);
		
		try {
			
			execute(buildHealth);
			
		} finally {
			buildHealth.removeListener(listener);
		}
	}
	
	protected abstract void execute(BuildHealth buildHealth);
	
}
