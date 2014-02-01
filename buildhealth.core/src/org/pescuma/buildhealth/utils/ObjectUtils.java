package org.pescuma.buildhealth.utils;

public class ObjectUtils {
	
	public static <T> T firstNonNull(T... args) {
		for (T arg : args) {
			if (arg != null)
				return arg;
		}
		return null;
	}
}
