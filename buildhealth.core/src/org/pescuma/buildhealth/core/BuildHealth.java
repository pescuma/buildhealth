package org.pescuma.buildhealth.core;

import static org.apache.commons.io.FileUtils.*;
import static org.pescuma.buildhealth.utils.FileHelper.*;
import static org.pescuma.buildhealth.utils.ReportHelper.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.io.FileUtils;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.computer.BuildDataComputer;
import org.pescuma.buildhealth.computer.BuildDataComputerException;
import org.pescuma.buildhealth.computer.BuildDataComputerTracker;
import org.pescuma.buildhealth.core.data.BuildDataTable;
import org.pescuma.buildhealth.core.data.DiskBuildData;
import org.pescuma.buildhealth.core.listener.BuildHealthListener;
import org.pescuma.buildhealth.core.listener.CompositeBuildHealthListener;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.BuildDataExtractorTracker;
import org.pescuma.buildhealth.notifiers.BuildHealthNotifier;
import org.pescuma.buildhealth.notifiers.BuildHealthNotifierTracker;
import org.pescuma.buildhealth.prefs.DiskPreferencesStore;
import org.pescuma.buildhealth.prefs.LowercaseDiskPreferencesStore;
import org.pescuma.buildhealth.prefs.LowercasePreferencesStore;
import org.pescuma.buildhealth.prefs.MemoryPreferencesStore;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.prefs.PreferencesStore;
import org.pescuma.buildhealth.prefs.PropertiesPreferencesStore;
import org.pescuma.buildhealth.projects.Projects;
import org.pescuma.buildhealth.projects.ProjectsAnalyser;
import org.pescuma.buildhealth.utils.ReportFormater;
import org.pescuma.buildhealth.utils.ReportHelper;

import com.google.common.base.Strings;

public class BuildHealth {
	
	private static final String DEFAULT_FOLDER_NAME = ".buildhealth";
	
	private final File home;
	private final BuildData table;
	private final PreferencesStore store;
	private final Preferences preferences;
	private final List<BuildHealthAnalyser> analysers = new ArrayList<BuildHealthAnalyser>();
	private final List<BuildHealthNotifier> notifiers = new ArrayList<BuildHealthNotifier>();
	private final CompositeBuildHealthListener listeners = new CompositeBuildHealthListener();
	
	public BuildHealth() {
		this(null);
	}
	
	public BuildHealth(File home) {
		this(home, getDefaultBuildData(home), getDefaultPreferencesStore(home));
	}
	
	public BuildHealth(File home, BuildData table, PreferencesStore store) {
		this.home = getCanonicalFile(home);
		this.table = table;
		this.store = store;
		preferences = new Preferences(store);
	}
	
	public void shutdown() {
		if (table instanceof DiskBuildData)
			((DiskBuildData) table).saveToDisk();
		
		if (store instanceof DiskPreferencesStore)
			((DiskPreferencesStore) store).saveToDisk();
	}
	
	public Preferences getPreferences() {
		return preferences;
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
			throw new RuntimeException(e);
		}
	}
	
	public void addAnalyser(BuildHealthAnalyser analyser) {
		analysers.add(analyser);
	}
	
	public void addAnalysersFromServices() {
		ServiceLoader<BuildHealthAnalyser> locator = ServiceLoader.load(BuildHealthAnalyser.class, getClass()
				.getClassLoader());
		
		for (BuildHealthAnalyser analyser : locator)
			addAnalyser(analyser);
	}
	
	public List<BuildHealthAnalyser> getAnalysers() {
		loadAnalysersIfNeeded();
		return Collections.unmodifiableList(analysers);
	}
	
	private void loadAnalysersIfNeeded() {
		if (analysers.isEmpty())
			addAnalysersFromServices();
		
		if (analysers.isEmpty())
			throw new IllegalStateException("No analysers configured");
	}
	
	public void compute(final BuildDataComputer computer) {
		File computedFolder = new File(home, "computed");
		
		try {
			forceMkdir(computedFolder);
		} catch (IOException e) {
			throw new BuildDataComputerException(e);
		}
		
		BuildDataExtractor extractor = computer.compute(computedFolder, new BuildDataComputerTracker() {
			@Override
			public void onStreamProcessed() {
			}
			
			@Override
			public void onFileProcessed(File file) {
			}
			
			@Override
			public void onErrorProcessingFile(File file, Exception ex) {
				listeners.onErrorComputingFile(computer, file, ex);
			}
			
			@Override
			public void onFileOutputCreated(File file) {
				listeners.onFileComputed(computer, file);
			}
		});
		
		extract(extractor);
	}
	
	public void extract(final BuildDataExtractor extractor) {
		extractor.extractTo(table, new BuildDataExtractorTracker() {
			@Override
			public void onFileProcessed(File file) {
				listeners.onFileExtracted(extractor, file);
			}
			
			@Override
			public void onErrorProcessingFile(File file, Exception ex) {
				listeners.onErrorExtractingFile(extractor, file, ex);
			}
			
			@Override
			public void onStreamProcessed() {
				listeners.onStreamExtracted(extractor);
			}
			
			@Override
			public void onProcessed(String message) {
				listeners.onOtherExtracted(extractor, message);
			}
		});
	}
	
	/**
	 * @param opts Flags from BuildHealth.ReportFlags
	 */
	public BuildReport generateReport(int opts) {
		if (table.isEmpty())
			return null;
		
		loadAnalysersIfNeeded();
		
		List<Report> reports = new ArrayList<Report>();
		
		Collections.sort(analysers, new Comparator<BuildHealthAnalyser>() {
			@Override
			public int compare(BuildHealthAnalyser o1, BuildHealthAnalyser o2) {
				return o1.getPriority() - o2.getPriority();
			}
		});
		
		Projects projects = computeProjects();
		
		for (BuildHealthAnalyser analyser : analysers)
			reports.addAll(analyser.computeReport(table, projects, preferences, opts));
		
		List<Report> sourcesOfProblems = null;
		if ((opts & ReportFlags.ListSourcesOfProblems) != 0)
			sourcesOfProblems = findSourcesOfProblems(reports);
		
		simplifyReport(reports, opts);
		
		BuildStatus status = Report.mergeBuildStatus(reports);
		
		Preferences prefs = preferences.child("build");
		String buildName = prefs.get("name", "Build");
		String prefix = prefs.child("report").get("prefix", "");
		String suffix = prefs.child("report").get("suffix", "");
		
		return new BuildReport(status, buildName, status.name(), prefix, suffix, sourcesOfProblems, reports);
	}
	
	/**
	 * @param opts Flags from BuildHealth.ReportFlags
	 */
	public Report generateReport(String analyserName, int opts) {
		if (table.isEmpty())
			return null;
		
		BuildHealthAnalyser analyser = findAnalyser(analyserName);
		if (analyser == null)
			return null;
		
		Projects projects = computeProjects();
		
		List<Report> reports = analyser.computeReport(table, projects, preferences, opts);
		
		ReportHelper.simplifyReport(reports, opts);
		
		if (reports.isEmpty())
			return null;
		
		if (reports.size() == 1)
			return reports.get(0);
		
		BuildStatus status = Report.mergeBuildStatus(reports);
		
		return new Report(status, analyser.getName(), status.name(), null, null, reports);
	}
	
	private Projects computeProjects() {
		return new ProjectsAnalyser().computeProjects(table, preferences);
	}
	
	private BuildHealthAnalyser findAnalyser(String name) {
		loadAnalysersIfNeeded();
		
		for (BuildHealthAnalyser analyser : analysers)
			if (analyser.getName().equalsIgnoreCase(name))
				return analyser;
		
		return null;
	}
	
	public List<BuildHealthPreference> getKnownPreferences() {
		List<BuildHealthPreference> result = new ArrayList<BuildHealthPreference>();
		
		result.add(new BuildHealthPreference("Name of this build", "Build", "build", "name"));
		result.add(new BuildHealthPreference("Text added before the report for this build", "", "build", "report",
				"prefix"));
		result.add(new BuildHealthPreference("Text added after the report for this build", "", "build", "report",
				"suffix"));
		
		for (BuildHealthAnalyser analyser : getAnalysers())
			result.addAll(analyser.getKnownPreferences());
		
		for (BuildHealthNotifier notifier : getNotifiers())
			result.addAll(notifier.getKnownPreferences());
		
		return result;
	}
	
	// Helper methods to find home
	
	public static File findHome(File home, boolean searchCurrentFolder) {
		if (home != null)
			return home;
		
		File result = searchForHomeFolderInEnvironmentVariables();
		if (result != null)
			return result;
		
		if (searchCurrentFolder) {
			result = searchForHomeFolderInCurrentPath(new File("."));
			if (result != null)
				return result;
		}
		
		return getDefaultHomeFolder();
	}
	
	public static File searchForHomeFolderInCurrentPath(File currentPath) {
		currentPath = getCanonicalFile(currentPath);
		
		do {
			File result = new File(currentPath, DEFAULT_FOLDER_NAME);
			if (result.exists() && result.isDirectory())
				return result;
			currentPath = currentPath.getParentFile();
		} while (currentPath != null);
		
		return null;
	}
	
	public static File searchForHomeFolderInEnvironmentVariables() {
		String home = System.getenv("BUILDHEALTH_HOME");
		if (Strings.isNullOrEmpty(home))
			return null;
		else
			return new File(home);
	}
	
	public static File getDefaultHomeFolder() {
		return new File(FileUtils.getUserDirectory(), DEFAULT_FOLDER_NAME);
	}
	
	public static PreferencesStore getDefaultPreferencesStore(File home) {
		if (home != null)
			return new LowercaseDiskPreferencesStore(new PropertiesPreferencesStore(getConfigFile(home)));
		else
			return new LowercasePreferencesStore(new MemoryPreferencesStore());
	}
	
	private static File getConfigFile(File home) {
		return getCanonicalFile(new File(home, "config"));
	}
	
	public static BuildData getDefaultBuildData(File home) {
		if (home != null)
			return new DiskBuildData(getDataFile(home), new BuildDataTable());
		else
			return new BuildDataTable();
	}
	
	private static File getDataFile(File home) {
		return getCanonicalFile(new File(home, "data.csv"));
	}
	
	public void addNotifier(BuildHealthNotifier notifier) {
		notifiers.add(notifier);
	}
	
	public void addNotifiersFromServices() {
		ServiceLoader<BuildHealthNotifier> locator = ServiceLoader.load(BuildHealthNotifier.class, getClass()
				.getClassLoader());
		for (BuildHealthNotifier notifier : locator)
			addNotifier(notifier);
	}
	
	public List<BuildHealthNotifier> getNotifiers() {
		loadNotifiersIfNeeded();
		
		return Collections.unmodifiableList(notifiers);
	}
	
	private void loadNotifiersIfNeeded() {
		if (notifiers.isEmpty())
			addNotifiersFromServices();
	}
	
	public void sendNotifications(BuildHealthNotifierTracker tracker) {
		loadNotifiersIfNeeded();
		
		if (notifiers.isEmpty())
			return;
		
		Collections.sort(notifiers, new Comparator<BuildHealthNotifier>() {
			@Override
			public int compare(BuildHealthNotifier o1, BuildHealthNotifier o2) {
				return o1.getPriority() - o2.getPriority();
			}
		});
		
		Report report = generateReport(ReportFlags.SummaryOnly);
		
		if (report == null) {
			System.out.println(ReportFormater.NO_DATA);
			return;
		}
		
		for (BuildHealthNotifier notifier : notifiers)
			notifier.sendNotification(report, preferences, tracker);
	}
	
	public static abstract class ReportFlags {
		public static final int Full = 0;
		public static final int SummaryOnly = 1;
		public static final int HighlightProblems = 1 << 1;
		public static final int ListSourcesOfProblems = 1 << 2;
	}
}
