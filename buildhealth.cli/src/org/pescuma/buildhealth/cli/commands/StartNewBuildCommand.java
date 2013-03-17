package org.pescuma.buildhealth.cli.commands;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;

@Command(name = "new", description = "Start a new build")
public class StartNewBuildCommand extends BuildHealthCliCommand {
	
	@Override
	public void execute() {
		buildHealth.startNewBuild();
	}
}
