package org.pescuma.buildhealth.cli;

import static java.util.Arrays.*;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.model.GlobalMetadata;

import javax.inject.Inject;

// HACK
@Command(name = "computegrouphelp", description = "display help information")
public class ComputeGroupHelp implements Runnable {
	
	@Inject
	public GlobalMetadata global;
	
	@Override
	public void run() {
		Help.help(global, asList("compute"));
	}
	
}
