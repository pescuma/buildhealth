package org.pescuma.buildhealth.extractor;

import static org.apache.commons.io.FileUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PseudoFiles {
	
	private final List<File> files = new ArrayList<File>();
	private final InputStream stream;
	
	public PseudoFiles(InputStream stream) {
		this.stream = stream;
	}
	
	public PseudoFiles(File file) {
		this.files.add(file);
		this.stream = null;
	}
	
	public PseudoFiles(List<File> files) {
		this.files.addAll(files);
		this.stream = null;
	}
	
	public boolean isStream() {
		return stream != null;
	}
	
	public InputStream getStream() {
		return stream;
	}
	
	public Collection<File> getFiles(String... validExtensions) {
		if (files.isEmpty())
			return Collections.emptyList();
		
		List<File> result = new ArrayList<File>();
		
		for (File file : files) {
			if (file.isDirectory()) {
				if (validExtensions.length > 0)
					result.addAll(listFiles(file, validExtensions, true));
				else
					result.addAll(listFiles(file, null, true));
				
			} else {
				if (file.exists())
					result.add(file);
			}
		}
		
		// Make all canonical
		for (int i = 0; i < result.size(); i++)
			result.set(i, getCanonicalFile(result.get(i)));
		
		return result;
	}
	
	private File getCanonicalFile(File file) {
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			return file.getAbsoluteFile();
		}
	}
}
