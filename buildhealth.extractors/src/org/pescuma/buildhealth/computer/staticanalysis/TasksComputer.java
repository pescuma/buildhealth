package org.pescuma.buildhealth.computer.staticanalysis;

import static com.google.common.base.Objects.*;
import static org.apache.commons.io.IOUtils.*;
import static org.pescuma.buildhealth.extractor.utils.FilenameToLanguage.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.pescuma.buildhealth.computer.BuildDataComputer;
import org.pescuma.buildhealth.computer.BuildDataComputerException;
import org.pescuma.buildhealth.computer.BuildDataComputerTracker;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.BuildDataExtractorException;
import org.pescuma.buildhealth.extractor.CSVExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.utils.CSV;

import au.com.bytecode.opencsv.CSVWriter;

// Based on https://github.com/jenkinsci/tasks-plugin/blob/master/src/main/java/hudson/plugins/tasks/parser/TaskScanner.java by Ulli Hafner
public class TasksComputer implements BuildDataComputer {
	
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
	private final Pattern ownerPattern = Pattern.compile("^\\[([^]]+)\\]");
	
	public TasksComputer(PseudoFiles files) {
		this(files, false);
	}
	
	public TasksComputer(PseudoFiles files, boolean caseInsensitive, String... markers) {
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
		File outputFile = new File(folder, "tasks-" + new Random().nextInt() + ".csv");
		
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
	
	private void extractTo(CSVWriter out, BuildDataComputerTracker tracker) {
		try {
			if (files.isStream()) {
				extractStream(firstNonNull(files.getStreamFilename(), "<stream>"), null,
						new InputStreamReader(files.getStream()), out);
				tracker.onStreamProcessed();
				
			} else {
				for (File file : files.getFiles()) {
					String language = detectLanguage(file.getPath());
					if (language.isEmpty())
						continue;
					
					FileReader reader = new FileReader(file);
					try {
						
						extractStream(file.getPath(), language, reader, out);
						tracker.onFileProcessed(file);
						
					} finally {
						IOUtils.closeQuietly(reader);
					}
				}
			}
		} catch (IOException e) {
			throw new BuildDataExtractorException(e);
		}
	}
	
	private void extractStream(String filename, String language, Reader reader, CSVWriter out) throws IOException {
		if (language == null)
			language = detectLanguage(filename);
		
		LineIterator it = IOUtils.lineIterator(reader);
		int lineNum = 0;
		while (it.hasNext()) {
			String line = it.next();
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
				
				Matcher ownerMatcher = ownerPattern.matcher(text);
				String owner = "";
				if (ownerMatcher.find()) {
					owner = ownerMatcher.group(1).trim();
					text = text.substring(ownerMatcher.end()).trim();
				}
				
				StringBuilder message = new StringBuilder();
				message.append(text);
				if (!owner.isEmpty()) {
					if (message.length() > 0)
						message.append(" ");
					message.append("(").append(owner).append(")");
				}
				
				write(out, Double.toString(1), "Static analysis", language, "Tasks", filename,
						Integer.toString(lineNum), marker, message.toString());
			}
		}
	}
	
	private String removeSeparators(String text) {
		if (text.startsWith(":"))
			return text.substring(1).trim();
		else
			return text;
	}
	
	private void write(CSVWriter out, String... line) {
		out.writeNext(line);
	}
}
