package org.pescuma.buildhealth.extractor.staticanalysis.console;

import static org.pescuma.buildhealth.utils.ObjectUtils.*;
import static org.pescuma.programminglanguagedetector.FilenameToLanguage.*;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.LineRange;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.warnings.parser.AbstractWarningsParser;
import hudson.plugins.warnings.parser.ParsingCanceledException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.StringEscapeUtils;
import org.pescuma.buildhealth.utils.Location;
import org.pescuma.datatable.DataTable;

class WarningsHelper {
	
	static void extractFromParser(String name, AbstractWarningsParser parser, Reader input, DataTable data)
			throws IOException, ParsingCanceledException {
		Collection<FileAnnotation> anns = parser.parse(input);
		
		// Remove dups
		anns = new HashSet<FileAnnotation>(anns);
		
		for (FileAnnotation ann : anns) {
			LineRange r = ann.getLineRanges().iterator().next();
			String fileName = ann.getFileName().replace('/', File.separatorChar);
			String language = detectLanguage(fileName);
			Location loc = Location
					.create(fileName, r.getStart(), ann.getColumnStart(), r.getEnd(), ann.getColumnEnd());
			String category = firstNonNull(ann.getCategory(), "").replace('\\', '/');
			String message = unescapeHTML(ann.getMessage());
			
			if (ann.getPriority() == Priority.HIGH)
				data.add(1, "Compiler error", language, name, Location.toFormatedString(loc), category, message);
			else
				data.add(1, "Static analysis", language, name, Location.toFormatedString(loc), category, message,
						toSeverity(ann.getPriority()));
		}
	}
	
	private static String unescapeHTML(String message) {
		return StringEscapeUtils.unescapeHtml(message).replace("&apos;", "'");
	}
	
	private static String toSeverity(Priority priority) {
		switch (priority) {
			case HIGH:
				return "High";
			case NORMAL:
				return "Medium";
			case LOW:
				return "Low";
			default:
				throw new IllegalStateException();
		}
	}
	
}
