package org.pescuma.buildhealth.cli.commands.webserver;

import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.core.Report;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

import fi.iki.elonen.NanoHTTPD;

public class BuildHealthWebServer extends NanoHTTPD {
	
	private final BuildHealth buildHealth;
	
	public BuildHealthWebServer(BuildHealth buildHealth, String hostname, int port) {
		super(hostname, port);
		
		this.buildHealth = buildHealth;
	}
	
	@Override
	public Response serve(HTTPSession session) {
		log(session);
		
		if ("/report.json".equals(session.getUri()))
			return reportJson();
		
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
		System.out.println(log.toString());
	}
	
	private Response reportJson() {
		Report report = buildHealth.generateReport(Full);
		
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.addMixInAnnotations(Report.class, ReportMixIn.class);
			
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
			
			StringWriter result = new StringWriter();
			mapper.writeValue(result, report);
			return new Response(Response.Status.OK, "application/json", result.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
			return new Response(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
		}
	}
	
	@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonTypeIdResolver(ReportTypeIdResolver.class)
	private static class ReportMixIn {
	}
	
	private static class ReportTypeIdResolver implements TypeIdResolver {
		
		private JavaType baseType;
		
		@Override
		public void init(JavaType baseType) {
			this.baseType = baseType;
		}
		
		@Override
		public String idFromValue(Object value) {
			return value.getClass().getSimpleName();
		}
		
		@Override
		public String idFromValueAndType(Object value, Class<?> suggestedType) {
			return idFromValue(value);
		}
		
		@Override
		public String idFromBaseType() {
			return idFromValue(baseType);
		}
		
		@Override
		public JavaType typeFromId(String id) {
			return null;
		}
		
		@Override
		public Id getMechanism() {
			return null;
		}
		
	}
}
