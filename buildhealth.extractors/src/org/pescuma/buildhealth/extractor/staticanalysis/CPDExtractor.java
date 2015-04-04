package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.apache.commons.io.IOUtils.*;
import static org.pescuma.programminglanguagedetector.FilenameToLanguage.*;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.LineIterator;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.utils.Location;
import org.pescuma.datatable.DataTable;

// http://pmd.sourceforge.net/snapshot/cpd-usage.html
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
	protected void extract(String path, Reader input, DataTable data) throws IOException {
		LineIterator lines = lineIterator(input);
		
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
	
	private void extract(String message, DataTable data) {
		message = message.trim();
		if (message.isEmpty())
			return;
		
		List<Location> locations = new ArrayList<Location>();
		
		String[] lines = message.split("\n");
		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];
			
			Matcher matcher = FILE_LINE.matcher(line);
			if (!matcher.matches())
				throw new IllegalStateException();
			
			String linenum = matcher.group(1);
			String file = matcher.group(2);
			locations.add(Location.create(file, linenum));
		}
		
		data.add(1, "Static analysis", detectLanguage(locations.get(0).file), "CPD",
				Location.toFormatedString(locations), "Code duplication", message);
	}
}
