package org.pescuma.buildhealth.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.FileResourceIterator;

abstract class FileListTask extends BaseAntTask {
	
	private File dir;
	private File file;
	private final List<FileSet> filesets = new ArrayList<FileSet>();
	
	public void setDir(File dir) {
		this.dir = dir;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public void addFileset(FileSet set) {
		filesets.add(set);
	}
	
	protected List<File> getFiles() {
		List<File> result = new ArrayList<File>();
		
		if (dir != null) {
			if (!dir.isDirectory())
				throw new BuildException("dir is not a directory: " + dir, getLocation());
			
			result.add(dir);
		}
		
		if (file != null) {
			if (!file.isFile())
				throw new BuildException("file is not a file: " + file, getLocation());
			
			result.add(file);
		}
		
		for (FileSet fileSet : filesets) {
			Iterator<?> iter = getFileIterator(fileSet);
			while (iter.hasNext()) {
				FileResource resource = (FileResource) iter.next();
				result.add(resource.getFile());
			}
		}
		
		if (result.isEmpty())
			throw new BuildException("You need to expecify which files to load.", getLocation());
		
		return result;
	}
	
	private Iterator<?> getFileIterator(FileSet fileSet) {
		DirectoryScanner ds = fileSet.getDirectoryScanner();
		Iterator<?> iter;
		if (ds.isEverythingIncluded() && !fileSet.hasPatterns()) {
			iter = new FileResourceIterator(fileSet.getDir(), ds.getIncludedDirectories());
		} else {
			iter = new FileResourceIterator(fileSet.getDir(), ds.getIncludedFiles());
		}
		return iter;
	}
}
