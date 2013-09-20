package org.pescuma.buildhealth.cli.commands.webserver;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class BuildHealthWebServer extends NanoHTTPD {
	
	public BuildHealthWebServer(String hostname, int port) {
		super(hostname, port);
	}
	
	@Override
	public Response serve(HTTPSession session) {
		log(session);
		
		if ("/report.json".equals(session.getUri()))
			return report();
		
		return super.serve(session);
	}
	
	private void log(HTTPSession session) {
		StringBuilder log = new StringBuilder();
		log.append(session.getMethod()).append(" ").append(session.getUri());
		
		Map<String, String> params = session.getParms();
		boolean first = true;
		for (Map.Entry<String, String> param : params.entrySet()) {
			if (first)
				log.append("?");
			else
				log.append("&");
			first = false;
			
			log.append(param.getKey()).append("=").append(param.getValue());
		}
		log.append("\n");
		System.out.println(log);
	}
	
	private Response report() {
		return null;
	}
	
}
