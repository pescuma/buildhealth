package org.pescuma.buildhealth.cli;

import io.airlift.command.Option;
import io.airlift.command.OptionType;

import java.io.File;

import org.pescuma.buildhealth.core.BuildHealth;

public abstract class BuildHealthCliCommand implements Runnable {
	
	protected BuildHealth buildHealth;
	
	@Option(type = OptionType.GLOBAL, name = "--home", title = "buildhealth home", description = "Folder to store buildhealth data")
	public File buildHealthHome;
	
	@Override
	public final void run() {
		buildHealth = new BuildHealth(BuildHealth.findHome(buildHealthHome, true));
		
		execute();
		
		buildHealth.shutdown();
	}
	
	protected abstract void execute();
	
}
