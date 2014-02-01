package org.pescuma.buildhealth.extractor.staticanalysis.console;

import static org.pescuma.buildhealth.extractor.utils.FilenameToLanguage.*;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.LineRange;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.warnings.parser.AbstractWarningsParser;
import hudson.plugins.warnings.parser.ParsingCanceledException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import org.pescuma.buildhealth.core.BuildData;

class WarningsHelper {
	
	static void extractFromParser(String name, AbstractWarningsParser parser, InputStream input, BuildData data)
			throws IOException, ParsingCanceledException {
		Collection<FileAnnotation> anns = parser.parse(new InputStreamReader(input));
		
		for (FileAnnotation ann : anns) {
			
			data.add(
					1,
					"Static analysis",
					detectLanguage(ann.getFileName()),
					name,
					ann.getFileName(),
					generateLine(ann),
					"Compiler warnings",
					ann.getMessage(),
					toSeverity(ann.getPriority()),
					String.format("Category: %s\nModule name: %s", ann.getCategory(), ann.getModuleName(),
							ann.getOrigin()));
		}
	}
	
	private static String generateLine(FileAnnotation ann) {
		LineRange r = ann.getLineRanges().iterator().next();
		
		int lineStart = r.getStart();
		int lineEnd = r.getEnd();
		int columnStart = ann.getColumnStart();
		int columnEnd = ann.getColumnEnd();
		
		boolean sameLine = (lineStart == lineEnd);
		boolean sameColumn = (columnStart == columnEnd);
		
		if (sameLine && sameColumn)
			return lineStart + ":" + columnStart;
		else
			return lineStart + ":" + columnStart + ":" + lineEnd + ":" + columnEnd;
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
