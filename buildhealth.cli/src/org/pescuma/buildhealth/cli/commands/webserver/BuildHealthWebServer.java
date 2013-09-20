package org.pescuma.buildhealth.cli.commands.webserver;

import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Locale;

import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.core.Report;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

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
		
		else if ("/report.xml".equals(session.getUri()))
			return reportXml();
		
		return super.serve(session);
	}
	
	private void log(HTTPSession session) {
		StringBuilder log = new StringBuilder();
		log.append(session.getMethod()).append(" ").append(session.getUri());
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
	
	private Response reportXml() {
		Report report = buildHealth.generateReport(Full);
		
		try {
			
			XmlMapper mapper = new XmlMapper();
			mapper.addMixInAnnotations(Report.class, ReportMixIn.class);
			AnnotationIntrospector first = new SimpleTypesAsAttributesAnnotationIntrospector();
			AnnotationIntrospector second = new JacksonXmlAnnotationIntrospector(false);
			mapper.setAnnotationIntrospector(new AnnotationIntrospectorPair(first, second));
			
			StringWriter result = new StringWriter();
			mapper.writeValue(result, report);
			return new Response(Response.Status.OK, "application/xml", result.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
			return new Response(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
		}
	}
	
	private static class SimpleTypesAsAttributesAnnotationIntrospector extends AnnotationIntrospector implements
			XmlAnnotationIntrospector {
		
		private static final long serialVersionUID = -2885574490579415829L;
		
		@Override
		public Version version() {
			return Version.unknownVersion();
		}
		
		@Override
		public Include findSerializationInclusion(Annotated a, Include defValue) {
			return Include.NON_EMPTY;
		}
		
		@Override
		public PropertyName findNameForSerialization(Annotated ann) {
			if ("children".equals(getName(ann)))
				return new PropertyName("child");
			if ("coverages".equals(getName(ann)))
				return new PropertyName("coverage");
			
			return super.findNameForSerialization(ann);
		}
		
		@Override
		public Boolean isOutputAsAttribute(Annotated ann) {
			if ("details".equals(getName(ann)))
				return null;
			
			Class<?> type = ann.getRawType();
			if (type.isPrimitive() || type.isEnum() || type == String.class)
				return true;
			
			return null;
		}
		
		private String getName(Annotated ann) {
			AnnotatedElement element = ann.getAnnotated();
			
			if (element instanceof Field)
				return ((Field) element).getName();
			
			if (element instanceof java.lang.reflect.Method) {
				String name = ((java.lang.reflect.Method) element).getName();
				if (name.startsWith("is"))
					name = name.substring(2);
				if (name.startsWith("get"))
					name = name.substring(3);
				if (name.length() < 2)
					return name;
				return name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
			}
			
			return null;
		}
		
		@Override
		public String findNamespace(Annotated ann) {
			return null;
		}
		
		@Override
		public Boolean isOutputAsText(Annotated ann) {
			return null;
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
