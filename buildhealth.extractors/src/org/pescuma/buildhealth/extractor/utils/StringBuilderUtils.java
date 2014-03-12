package org.pescuma.buildhealth.extractor.utils;

public class StringBuilderUtils {
	
	public static void appendInNewLine(StringBuilder out, String name, String text) {
		appendInNewLine(out, name, text, "");
	}
	
	public static void appendInNewLine(StringBuilder out, String name, String text, String suffix) {
		if (text.isEmpty())
			return;
		
		if (out.length() > 0)
			out.append("\n");
		
		out.append(name).append(": ").append(text).append(suffix);
	}
	
}
