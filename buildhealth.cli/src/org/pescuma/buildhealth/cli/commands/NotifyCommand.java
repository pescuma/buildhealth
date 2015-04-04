package org.pescuma.buildhealth.cli.commands;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.notifiers.BuildHealthNotifierTracker;

@Command(name = "notify", description = "Send notifications with the status of the current build")
public class NotifyCommand extends BuildHealthCliCommand {
	
	@Override
	protected void execute() {
		buildHealth.sendNotifications(new BuildHealthNotifierTracker() {
			@Override
			public void reportNotified(String message) {
				System.out.println(message);
			}
		});
	}
	
}
