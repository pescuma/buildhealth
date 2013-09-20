package org.pescuma.buildhealth.cli.commands.webserver;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.IOException;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;

@Command(name = "webserver", description = "Serve the results using a embeded webserver")
public class WebServerCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "port", description = "The port to bind the webserver (default: 80190)", required = false)
	public int port = 80190;
	
	@Override
	public void execute() {
		BuildHealthWebServer server = new BuildHealthWebServer(null, port);
		
		try {
			server.start();
		} catch (IOException e) {
			System.err.println("Couldn't start server:\n");
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Web server started at http://localhost:" + port + "\n");
		System.out.println("Hit Enter to stop...\n");
		
		try {
			System.in.read();
		} catch (Exception ignored) {
		}
		
		server.stop();
		System.out.println("Server stopped.\n");
	}
	
}
