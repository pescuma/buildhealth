package org.pescuma.buildhealth.extractor.utils;

public class StringHelper {
	
	public static String splitCamelCase(String text) {
		return text.replaceAll("(?=[A-Z]+)", " ").trim();
	}
	
	public static String firstNonEmpty(String... args) {
		for (String a : args) {
			if (a != null && !a.isEmpty())
				return a;
		}
		return "";
	}
	
}
