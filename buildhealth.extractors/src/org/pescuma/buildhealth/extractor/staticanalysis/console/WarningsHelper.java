package org.pescuma.buildhealth.extractor.staticanalysis.console;

import static org.pescuma.buildhealth.extractor.utils.FilenameToLanguage.*;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.LineRange;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.warnings.parser.AbstractWarningsParser;
import hudson.plugins.warnings.parser.ParsingCanceledException;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.utils.Location;

class WarningsHelper {
	
	static void extractFromParser(String name, AbstractWarningsParser parser, Reader input, BuildData data)
			throws IOException, ParsingCanceledException {
		Collection<FileAnnotation> anns = parser.parse(input);
		
		// Remove dups
		anns = new HashSet<FileAnnotation>(anns);
		
		for (FileAnnotation ann : anns) {
			LineRange r = ann.getLineRanges().iterator().next();
			Location loc = Location.create(ann.getFileName(), r.getStart(), ann.getColumnStart(), r.getEnd(),
					ann.getColumnEnd());
			
			String category = (ann.getPriority() == Priority.HIGH ? "Compiler errors" : "Compiler warnings");
			
			if (!StringUtils.isBlank(ann.getCategory()))
				category += "/" + ann.getCategory().replace('\\', '/');
			
			String message = unescapeHTML(ann.getMessage());
			
			data.add(1, "Static analysis", detectLanguage(ann.getFileName()), name, Location.toFormatedString(loc),
					category, message, toSeverity(ann.getPriority()));
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
