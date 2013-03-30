package org.pescuma.buildhealth.cli;

import static java.util.Arrays.*;
import io.airlift.command.Command;
import io.airlift.command.Help;
import io.airlift.command.model.GlobalMetadata;

import javax.inject.Inject;

// HACK
@Command(name = "addgrouphelp", description = "display help information")
public class AddGroupHelp implements Runnable {
	
	@Inject
	public GlobalMetadata global;
	
	@Override
	public void run() {
		Help.help(global, asList("add"));
	}
	
}
