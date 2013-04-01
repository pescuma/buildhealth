package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.apache.commons.io.IOUtils.*;
import static org.pescuma.buildhealth.utils.FilenameToLanguage.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.LineIterator;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// TODO Maybe create a Code duplication analyser?
public class CPDExtractor extends BaseBuildDataExtractor {
	
	private static Pattern SEPARATOR_LINE = Pattern.compile("=========+");
	private static Pattern FIRST_LINE = Pattern
			.compile("Found a \\d+ line \\(\\d+ tokens\\) duplication in the following files:");
	private static Pattern FILE_LINE = Pattern.compile("Starting at line (\\d+) of (.+)");
	
	public CPDExtractor(PseudoFiles files) {
		super(files, "txt");
	}
	
	@Override
	protected void extract(String filename, InputStream input, BuildData data) throws IOException {
		LineIterator lines = lineIterator(input, "UTF-8");
		
		StringBuilder msg = null;
		boolean startCandidate = true;
		while (lines.hasNext()) {
			String line = lines.next().trim();
			
			if (msg != null) {
				if (FILE_LINE.matcher(line).matches()) {
					msg.append(line).append("\n");
					continue;
				} else {
					extract(msg.toString(), data);
					msg = null;
				}
			}
			
			if (SEPARATOR_LINE.matcher(line).matches()) {
				startCandidate = true;
				continue;
			}
			
			if (startCandidate && FIRST_LINE.matcher(line).matches()) {
				msg = new StringBuilder();
				msg.append(line).append("\n");
			}
			
			startCandidate = false;
		}
	}
	
	private void extract(String message, BuildData data) {
		message = message.trim();
		if (message.isEmpty())
			return;
		
		String[] lines = message.split("\n");
		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];
			
			Matcher matcher = FILE_LINE.matcher(line);
			if (!matcher.matches())
				throw new IllegalStateException();
			
			String linenum = matcher.group(1);
			String file = matcher.group(2);
			data.add(1, "Static analysis", detectLanguage(file), "CPD", file, linenum, "Code duplication", message);
		}
	}
}
