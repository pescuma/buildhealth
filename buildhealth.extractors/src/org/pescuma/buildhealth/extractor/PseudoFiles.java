package org.pescuma.buildhealth.extractor;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FilenameUtils.directoryContains;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.pescuma.buildhealth.utils.FileHelper;

public class PseudoFiles {
	
	private final List<File> files = new ArrayList<File>();
	private final List<String> excludes = new ArrayList<String>();
	private final InputStream stream;
	private final String streamPath;
	private final boolean ignoreSourceControlFolders;
	
	public PseudoFiles(InputStream stream) {
		this(stream, null);
	}
	
	public PseudoFiles(InputStream stream, String streamPath) {
		this.stream = stream;
		this.streamPath = streamPath;
		ignoreSourceControlFolders = true;
	}
	
	public PseudoFiles(File file) {
		this(asList(file), null);
	}
	
	public PseudoFiles(List<File> files) {
		this(files, null);
	}
	
	public PseudoFiles(File file, List<File> excludes) {
		this(asList(file), excludes);
	}
	
	public PseudoFiles(List<File> files, List<File> excludes) {
		this(files, excludes, true);
	}
	
	public PseudoFiles(File file, List<File> excludes, boolean ignoreSourceControlFolders) {
		this(asList(file), excludes, ignoreSourceControlFolders);
	}
	
	public PseudoFiles(List<File> files, List<File> excludes, boolean ignoreSourceControlFolders) {
		for (File file : files)
			this.files.add(fixFileName(file));
		if (excludes != null) {
			for (File file : excludes)
				this.excludes.add(fixFileName(file).getPath());
		}
		this.stream = null;
		this.streamPath = null;
		this.ignoreSourceControlFolders = ignoreSourceControlFolders;
	}
	
	private File fixFileName(File file) {
		String name = file.getPath();
		if (name.length() > 2 //
				&& ((name.startsWith("'") && name.endsWith("'")) //
				|| (name.startsWith("\"") && name.endsWith("\""))))
			file = new File(name.substring(1, name.length() - 1));
		
		return FileHelper.getCanonicalFile(file);
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
				result.addAll(listFiles(file, fileFilter,
						ignoreSourceControlFolders ? IgnoreVersionControlFoldersFileFilter.INSTANCE
								: TrueFileFilter.INSTANCE));
				
			} else {
				if (file.exists())
					result.add(file);
			}
		}
		
		// Make all canonical
		for (int i = 0; i < result.size(); i++)
			result.set(i, getCanonicalFile(result.get(i)));
		
		for (Iterator<File> it = result.iterator(); it.hasNext();) {
			File file = it.next();
			if (isInExcludes(file))
				it.remove();
		}
		
		return result;
	}
	
	private boolean isInExcludes(File file) {
		for (String exclude : excludes) {
			String path = file.getPath();
			
			if (exclude.equals(path))
				return true;
			
			try {
				if (directoryContains(exclude, path))
					return true;
			} catch (IOException e) {
				// Ignore this one
			}
		}
		return false;
	}
	
	private File getCanonicalFile(File file) {
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			return file.getAbsoluteFile();
		}
	}
	
	private static class IgnoreVersionControlFoldersFileFilter implements IOFileFilter {
		
		public static final IOFileFilter INSTANCE = new IgnoreVersionControlFoldersFileFilter();
		
		private final Set<String> ignored = new HashSet<String>();
		
		protected IgnoreVersionControlFoldersFileFilter() {
			ignored.add(".git");
			ignored.add(".hg");
			ignored.add(".svn");
			ignored.add(".nuget");
			ignored.add("CVS");
		}
		
		@Override
		public boolean accept(File file) {
			return !ignored.contains(file.getName());
		}
		
		@Override
		public boolean accept(File dir, String name) {
			return accept(dir);
		}
		
	}
}
