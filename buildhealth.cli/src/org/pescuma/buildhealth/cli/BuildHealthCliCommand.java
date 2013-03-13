package org.pescuma.buildhealth.cli;

import io.airlift.command.Option;
import io.airlift.command.OptionType;

import java.io.File;

import org.pescuma.buildhealth.core.BuildHealth;

public abstract class BuildHealthCliCommand implements Runnable {
	
	private BuildHealth buildHealth;
	
	@Option(type = OptionType.GLOBAL, name = "--home", title = "buildhealth home", description = "Folder to store buildhealth data")
	public File buildHealthHome;
	
	protected BuildHealth getBuildHealth() {
		if (buildHealth == null)
			buildHealth = new BuildHealth(buildHealthHome);
		
		return buildHealth;
	}
	
}
