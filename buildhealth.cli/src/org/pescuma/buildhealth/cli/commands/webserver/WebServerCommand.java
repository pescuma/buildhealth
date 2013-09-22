package org.pescuma.buildhealth.cli.commands.webserver;

import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;
import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.IOException;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.core.Report;

@Command(name = "webserver", description = "Serve the results using a embeded webserver")
public class WebServerCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "port", description = "The port to bind the webserver (default: 80190)", required = false)
	public int port = 8190;
	
	@Override
	public void execute() {
		Report report = buildHealth.generateReport(Full);
		
		BuildHealthWebServer server = new BuildHealthWebServer(report, null, port);
		
		try {
			server.start();
		} catch (IOException e) {
			System.err.println("Couldn't start server:");
			e.printStackTrace();
			System.exit(-1);
		}
		
		String url = "http://localhost:" + port;
		System.out.println("Web server started at " + url);
		System.out.println("Available urls:");
		System.out.println("  " + url + "/            => Browse the reports as a web site");
		System.out.println("  " + url + "/report.json => Report in JSON format");
		System.out.println("  " + url + "/report.xml  => Report in XML format");
		System.out.println();
		System.out.println("Hit Enter to stop...");
		
		try {
			System.in.read();
		} catch (Exception ignored) {
		}
		
		server.stop();
		System.out.println("Server stopped.\n");
	}
	
}
