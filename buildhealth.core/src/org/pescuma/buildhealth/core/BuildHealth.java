package org.pescuma.buildhealth.core;

import static org.apache.commons.io.FileUtils.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.core.data.BuildDataTable;
import org.pescuma.buildhealth.core.data.DiskBuildData;
import org.pescuma.buildhealth.core.listener.BuildHealthListener;
import org.pescuma.buildhealth.core.listener.CompositeBuildHealthListener;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;

public class BuildHealth {
	
	private static final String DEFAULT_FOLDER_NAME = ".buildhealth";
	
	private final File home;
	private final BuildData table;
	private final List<BuildHealthAnalyser> analysers = new ArrayList<BuildHealthAnalyser>();
	private final CompositeBuildHealthListener listeners = new CompositeBuildHealthListener();
	
	public BuildHealth() {
		this(null);
	}
	
	public BuildHealth(File buildHealthHome) {
		this.home = getCanonicalFile(buildHealthHome);
		
		if (home != null)
			table = new DiskBuildData(new File(home, "data.csv"), new BuildDataTable());
		else
			table = new BuildDataTable();
	}
	
	public void shutdown() {
		if (table instanceof DiskBuildData)
			((DiskBuildData) table).saveToDisk();
	}
	
	public boolean addListener(BuildHealthListener listener) {
		return listeners.addListener(listener);
	}
	
	public boolean removeListener(BuildHealthListener listener) {
		return listeners.removeListener(listener);
	}
	
	public void startNewBuild() {
		if (home == null)
			throw new IllegalStateException();
		
		try {
			
			if (home.exists())
				forceDelete(home);
			
			forceMkdir(home);
			
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	public void addAnalyser(BuildHealthAnalyser analyser) {
		analysers.add(analyser);
	}
	
	public void extract(final BuildDataExtractor extractor) {
		extractor.extractTo(table, new BuildDataExtractorTracker() {
			@Override
			public void fileProcessed(File file) {
				listeners.onFileExtracted(extractor, file);
			}
			
			@Override
			public void streamProcessed() {
				listeners.onStreamExtracted(extractor);
			}
		});
	}
	
	public Report generateReportSummary() {
		if (table.isEmpty())
			return null;
		
		List<Report> reports = new ArrayList<Report>();
		
		for (BuildHealthAnalyser analyser : analysers)
			reports.addAll(analyser.computeSimpleReport(table));
		
		if (reports.isEmpty())
			return null;
		
		BuildStatus status = Report.mergeBuildStatus(reports);
		
		return new Report(status, "Build", status.name(), null, reports);
	}
	
	// Helper methods to find home
	
	public static File findHome(File home, boolean searchCurrentFolder) {
		if (home != null)
			return home;
		
		if (searchCurrentFolder) {
			File result = searchForHomeFolder(new File("."));
			if (result != null)
				return result;
		}
		
		return getDefaultHomeFolder();
	}
	
	public static File searchForHomeFolder(File currentPath) {
		currentPath = getCanonicalFile(currentPath);
		
		do {
			File result = new File(currentPath, DEFAULT_FOLDER_NAME);
			if (result.exists() && result.isDirectory())
				return result;
			currentPath = currentPath.getParentFile();
		} while (currentPath != null);
		
		return null;
	}
	
	private static File getCanonicalFile(File file) {
		if (file == null)
			return null;
		
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			return file.getAbsoluteFile();
		}
	}
	
	public static File getDefaultHomeFolder() {
		return new File(FileUtils.getUserDirectory(), DEFAULT_FOLDER_NAME);
	}
}
