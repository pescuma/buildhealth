package org.pescuma.buildhealth.extractor;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class PseudoFiles {
	
	private final File fileOrFolder;
	private final InputStream stream;
	
	public PseudoFiles(InputStream stream) {
		this.fileOrFolder = null;
		this.stream = stream;
	}
	
	public PseudoFiles(File fileOrFolder) {
		this.fileOrFolder = fileOrFolder;
		this.stream = null;
	}
	
	public boolean isStream() {
		return stream != null;
	}
	
	public InputStream getStream() {
		return stream;
	}
	
	public Collection<File> getFiles(String... validExtensions) {
		if (fileOrFolder == null)
			return null;
		
		else if (fileOrFolder.isDirectory())
			return FileUtils.listFiles(fileOrFolder, new String[] { "xml" }, true);
		
		else
			return Arrays.asList(fileOrFolder);
	}
	
}
