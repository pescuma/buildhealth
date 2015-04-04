package org.pescuma.buildhealth.cli;

import io.airlift.airline.Option;
import io.airlift.airline.OptionType;

import java.io.File;

import org.pescuma.buildhealth.computer.BuildDataComputer;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.core.listener.AbstractBuildHealthListener;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;

public abstract class BuildHealthCliCommand implements Runnable {
	
	protected BuildHealth buildHealth;
	
	@Option(type = OptionType.GLOBAL, name = "--home", title = "buildhealth home", description = "Folder to store buildhealth data")
	public File buildHealthHome;
	
	public void useExternalBuildHealth(BuildHealth buildHealth) {
		this.buildHealth = buildHealth;
	}
	
	@Override
	public final void run() {
		if (buildHealth == null) {
			
			buildHealth = new BuildHealth(BuildHealth.findHome(buildHealthHome, true));
			buildHealth.addListener(new AbstractBuildHealthListener() {
				@Override
				public void onFileExtracted(BuildDataExtractor extractor, File file) {
					System.out.println("File processed: " + file);
				}
				
				@Override
				public void onErrorExtractingFile(BuildDataExtractor extractor, File file, Exception ex) {
					System.out.println("Error processing file: " + file + " : " + ex.getMessage());
					ex.printStackTrace(System.out);
				}
				
				@Override
				public void onFileComputed(BuildDataComputer computer, File file) {
					System.out.println("File computed: " + file);
				}
				
				@Override
				public void onErrorComputingFile(BuildDataComputer computer, File file, Exception ex) {
					System.out.println("Error computing file: " + file + " : " + ex.getMessage());
					ex.printStackTrace(System.out);
				}
				
				@Override
				public void onOtherExtracted(BuildDataExtractor extractor, String message) {
					System.out.println(message);
				}
			});
			
			execute();
			
			buildHealth.shutdown();
			buildHealth = null;
			
		} else {
			execute();
		}
	}
	
	protected abstract void execute();
	
}
