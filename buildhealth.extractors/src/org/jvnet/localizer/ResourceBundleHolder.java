package org.jvnet.localizer;

import java.util.Locale;
import java.util.ResourceBundle;

public final class ResourceBundleHolder {
	
	public static ResourceBundleHolder get(@SuppressWarnings("rawtypes") Class clazz) {
		return new ResourceBundleHolder();
	}
	
	public ResourceBundle get(Locale locale) {
		return null;
	}
	
	public String format(String key, Object... args) {
		return "";
	}
}
