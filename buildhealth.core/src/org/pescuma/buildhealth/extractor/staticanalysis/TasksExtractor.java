package org.pescuma.buildhealth.extractor.staticanalysis;

import static com.google.common.base.Objects.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildDataExtractorTracker;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.BuildDataExtractorException;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.utils.FilenameToLanguage;

// Based on https://github.com/jenkinsci/tasks-plugin/blob/master/src/main/java/hudson/plugins/tasks/parser/TaskScanner.java by Ulli Hafner
public class TasksExtractor implements BuildDataExtractor {
	
	private final PseudoFiles files;
	private final Pattern[] patterns;
	private final String[] markers;
	
	public TasksExtractor(PseudoFiles files) {
		this(files, false);
	}
	
	public TasksExtractor(PseudoFiles files, boolean caseInsensitive, String... markers) {
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
	public void extractTo(BuildData data, BuildDataExtractorTracker tracker) {
		try {
			if (files.isStream()) {
				extractStream(firstNonNull(files.getStreamFilename(), "<stream>"),
						new InputStreamReader(files.getStream()), data);
				tracker.streamProcessed();
				
			} else {
				for (File file : files.getFiles("xml")) {
					FileReader reader = new FileReader(file);
					try {
						
						extractStream(file.getPath(), reader, data);
						tracker.fileProcessed(file);
						
					} finally {
						IOUtils.closeQuietly(reader);
					}
				}
			}
		} catch (IOException e) {
			throw new BuildDataExtractorException(e);
		}
	}
	
	private void extractStream(String filename, Reader reader, BuildData data) throws IOException {
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
					
					if (text.startsWith(":"))
						text = text.substring(1).trim();
				}
				
				data.add(1, "Static analysis", FilenameToLanguage.detectLanguage(filename), "Task", filename,
						Integer.toString(lineNum), marker, text);
			}
		}
	}
}
