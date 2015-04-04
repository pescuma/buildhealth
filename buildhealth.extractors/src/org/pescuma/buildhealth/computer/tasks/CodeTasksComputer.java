package org.pescuma.buildhealth.computer.tasks;

import static org.apache.commons.io.IOUtils.*;
import static org.pescuma.buildhealth.extractor.utils.EncodingHelper.*;
import static org.pescuma.buildhealth.utils.StringHelper.*;
import static org.pescuma.programminglanguagedetector.FilenameToLanguage.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.pescuma.buildhealth.computer.BuildDataComputer;
import org.pescuma.buildhealth.computer.BuildDataComputerException;
import org.pescuma.buildhealth.computer.BuildDataComputerTracker;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.BuildDataExtractorException;
import org.pescuma.buildhealth.extractor.CSVExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.utils.SimpleExecutor;
import org.pescuma.buildhealth.utils.CSV;
import org.pescuma.buildhealth.utils.FileHelper;
import org.pescuma.buildhealth.utils.Location;

import au.com.bytecode.opencsv.CSVWriter;

// Based on https://github.com/jenkinsci/tasks-plugin/blob/master/src/main/java/hudson/plugins/tasks/parser/TaskScanner.java by Ulli Hafner
public class CodeTasksComputer implements BuildDataComputer {
	
	public static final String NO_MESSAGE = "<no message>";
	
	private static final Map<String, String> commentTerminators = new HashMap<String, String>();
	static {
		commentTerminators.put("Java", "*/");
		commentTerminators.put("Javascript", "*/");
		commentTerminators.put("C", "*/");
		commentTerminators.put("C++", "*/");
	}
	
	private final PseudoFiles files;
	private final String[] markers;
	private final Pattern[] patterns;
	private final Pattern createdByPattern = Pattern.compile("^\\[([^]]+)\\]");
	
	public CodeTasksComputer(PseudoFiles files) {
		this(files, false);
	}
	
	public CodeTasksComputer(PseudoFiles files, boolean caseInsensitive, String... markers) {
		if (files == null)
			throw new IllegalArgumentException();
		
		if (markers == null || markers.length < 1)
			markers = new String[] { "TODO", "FIXME", "HACK", "XXX" };
		
		int flags = (caseInsensitive ? Pattern.CASE_INSENSITIVE : 0);
		
		this.markers = markers;
		this.patterns = new Pattern[markers.length];
		for (int i = 0; i < markers.length; i++) {
			String marker = markers[i];
			
			if (marker.isEmpty())
				throw new IllegalArgumentException("Pattern can't be empty");
			
			if (Character.isLetterOrDigit(marker.charAt(0)))
				marker = "\\b" + marker;
			if (Character.isLetterOrDigit(marker.charAt(marker.length() - 1)))
				marker += "\\b";
			
			this.patterns[i] = Pattern.compile(marker, flags);
		}
		
		this.files = files;
	}
	
	@Override
	public BuildDataExtractor compute(File folder, BuildDataComputerTracker tracker) {
		File outputFile = FileHelper.createUniquiFileName(folder, "tasks-", ".csv");
		
		FileWriter writer = null;
		CSVWriter out = null;
		try {
			
			writer = new FileWriter(outputFile);
			out = CSV.newWriter(writer);
			
			extractTo(out, tracker);
			
			tracker.onFileOutputCreated(outputFile);
			
			return new CSVExtractor(new PseudoFiles(outputFile));
			
		} catch (IOException e) {
			throw new BuildDataComputerException(e);
			
		} finally {
			closeQuietly(out);
			closeQuietly(writer);
		}
	}
	
	private void extractTo(final CSVWriter out, final BuildDataComputerTracker tracker) {
		try {
			
			if (files.isStream()) {
				extractStream(files.getStreamPath(), null, new InputStreamReader(files.getStream()), out);
				tracker.onStreamProcessed();
				
			} else {
				
				SimpleExecutor exec = new SimpleExecutor();
				
				for (final File file : files.getFilesByExtension()) {
					final String language = detectLanguage(file.getPath());
					if (language == null)
						continue;
					
					exec.submit(new Runnable() {
						@Override
						public void run() {
							List<String> lines;
							
							Reader reader = null;
							try {
								
								reader = toReader(new FileInputStream(file));
								lines = IOUtils.readLines(reader);
								
							} catch (IOException e) {
								tracker.onErrorProcessingFile(file, e);
								return;
								
							} finally {
								IOUtils.closeQuietly(reader);
							}
							
							extractLines(file.getPath(), language, out, lines);
							tracker.onFileProcessed(file);
						}
					});
				}
				
				exec.awaitTermination();
			}
			
		} catch (IOException e) {
			throw new BuildDataExtractorException(e);
		} catch (InterruptedException e) {
			throw new BuildDataExtractorException(e);
		}
	}
	
	private void extractStream(String filename, String language, Reader reader, CSVWriter out) throws IOException {
		List<String> lines = IOUtils.readLines(reader);
		
		extractLines(filename, language, out, lines);
	}
	
	private void extractLines(String filename, String language, CSVWriter out, List<String> lines) {
		if (language == null)
			language = firstNonEmpty(detectLanguage(filename), "");
		
		int lineNum = 0;
		for (String line : lines) {
			lineNum++;
			
			if (line.isEmpty())
				continue;
			
			for (int i = 0; i < patterns.length; i++) {
				Matcher m = patterns[i].matcher(line);
				if (!m.find())
					continue;
				
				String marker = markers[i];
				String text = "";
				
				int end = m.end();
				if (end < line.length()) {
					text = line.substring(end).trim();
					
					text = removeSeparators(text);
				}
				
				String terminator = commentTerminators.get(language);
				if (terminator != null) {
					int pos = text.indexOf(terminator);
					if (pos >= 0)
						text = text.substring(0, pos).trim();
				}
				
				Matcher createdByMatcher = createdByPattern.matcher(text);
				String createdBy = "";
				if (createdByMatcher.find()) {
					createdBy = createdByMatcher.group(1).trim();
					text = text.substring(createdByMatcher.end()).trim();
				}
				
				if (text.isEmpty())
					text = NO_MESSAGE;
				
				Location loc = Location.create(filename, lineNum);
				
				write(out, Double.toString(1), "Tasks", "From code", marker, "", text, "", createdBy, "", "", "", "",
						Location.toFormatedString(loc));
			}
		}
	}
	
	private String removeSeparators(String text) {
		if (text.startsWith(":"))
			return text.substring(1).trim();
		else
			return text;
	}
	
	private synchronized void write(CSVWriter out, String... line) {
		out.writeNext(line);
	}
}
