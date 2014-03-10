package org.pescuma.buildhealth.extractor;

import static org.apache.commons.io.FileUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class PseudoFiles {
	
	private final List<File> files = new ArrayList<File>();
	private final InputStream stream;
	private final String streamPath;
	
	public PseudoFiles(InputStream stream) {
		this(stream, null);
	}
	
	public PseudoFiles(InputStream stream, String streamPath) {
		this.stream = stream;
		this.streamPath = streamPath;
	}
	
	public PseudoFiles(File file) {
		this.files.add(fixFileName(file));
		this.stream = null;
		this.streamPath = null;
	}
	
	public PseudoFiles(List<File> files) {
		for (File file : files)
			this.files.add(fixFileName(file));
		this.stream = null;
		this.streamPath = null;
	}
	
	private File fixFileName(File file) {
		String name = file.getPath();
		if (name.length() > 2 //
				&& ((name.startsWith("'") && name.endsWith("'")) //
				|| (name.startsWith("\"") && name.endsWith("\""))))
			file = new File(name.substring(1, name.length() - 1));
		
		return file.getAbsoluteFile();
	}
	
	public boolean isStream() {
		return stream != null;
	}
	
	public InputStream getStream() {
		return stream;
	}
	
	public String getStreamPath() {
		return streamPath;
	}
	
	public Collection<File> getFilesByExtension(String... validExtensions) {
		for (int i = 0; i < validExtensions.length; i++)
			validExtensions[i] = "." + validExtensions[i];
		
		return getFiles(validExtensions.length > 0 ? new SuffixFileFilter(validExtensions) : TrueFileFilter.INSTANCE);
	}
	
	public Collection<File> getFilesByName(String... filenames) {
		return getFiles(new NameFileFilter(filenames));
	}
	
	public Collection<File> getFiles(IOFileFilter fileFilter) {
		if (files.isEmpty())
			return Collections.emptyList();
		
		List<File> result = new ArrayList<File>();
		
		for (File file : files) {
			if (file.isDirectory()) {
				result.addAll(listFiles(file, fileFilter, TrueFileFilter.INSTANCE));
				
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
