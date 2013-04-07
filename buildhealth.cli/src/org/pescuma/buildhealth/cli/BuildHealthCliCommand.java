package org.pescuma.buildhealth.cli;

import io.airlift.command.Option;
import io.airlift.command.OptionType;

import java.io.File;

import org.pescuma.buildhealth.analyser.coverage.CoverageAnalyser;
import org.pescuma.buildhealth.analyser.diskusage.DiskUsageAnalyser;
import org.pescuma.buildhealth.analyser.loc.LOCAnalyser;
import org.pescuma.buildhealth.analyser.staticanalysis.StaticAnalysisAnalyser;
import org.pescuma.buildhealth.analyser.unittest.UnitTestAnalyser;
import org.pescuma.buildhealth.computer.BuildDataComputer;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.core.listener.AbstractBuildHealthListener;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;

public abstract class BuildHealthCliCommand implements Runnable {
	
	protected BuildHealth buildHealth;
	
	@Option(type = OptionType.GLOBAL, name = "--home", title = "buildhealth home", description = "Folder to store buildhealth data")
	public File buildHealthHome;
	
	@Override
	public final void run() {
		buildHealth = new BuildHealth(BuildHealth.findHome(buildHealthHome, true));
		buildHealth.addListener(new AbstractBuildHealthListener() {
			@Override
			public void onFileExtracted(BuildDataExtractor extractor, File file) {
				System.out.println("File processed: " + file);
			}
			
			@Override
			public void onFileComputed(BuildDataComputer computer, File file) {
				System.out.println("File computed: " + file);
			}
		});
		
		// TODO
		buildHealth.addAnalyser(new UnitTestAnalyser());
		buildHealth.addAnalyser(new CoverageAnalyser());
		buildHealth.addAnalyser(new LOCAnalyser());
		buildHealth.addAnalyser(new StaticAnalysisAnalyser());
		buildHealth.addAnalyser(new DiskUsageAnalyser());
		
		execute();
		
		buildHealth.shutdown();
	}
	
	protected abstract void execute();
	
}
