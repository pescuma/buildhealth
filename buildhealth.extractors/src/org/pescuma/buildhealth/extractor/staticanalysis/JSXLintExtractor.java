package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.apache.commons.io.IOUtils.*;
import static org.apache.commons.lang3.ObjectUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.LineIterator;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseBuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// https://github.com/douglascrockford/JSLint
// https://github.com/FND/jslint-reporter
// http://code.google.com/p/jslint4java/
// https://github.com/jamietre/SharpLinter
// http://www.javascriptlint.com
// http://www.jshint.com/
public class JSXLintExtractor extends BaseBuildDataExtractor {
	
	private static final String FILE = "([^ ][^();&<>]?[^():;&<>]*[^ ])";
	private static final String NUMBER = "(\\d+)";
	private static final String MESSAGE = "([^ ].+)";
	
	private static final List<Format> formats = new ArrayList<Format>();
	static {
		formats.add(new Format("JavaScript Lint", "^" + FILE + "\\(" + NUMBER + "\\): (lint )?warning: " + MESSAGE
				+ "$", 1, 2, -1, 4));
		formats.add(new Format("JSLint", "^" + FILE + ":" + NUMBER + ":" + NUMBER
				+ ":(?!Stopping. \\(\\d+% scanned\\))" + MESSAGE + "\\.$", 1, 2, 3, 4));
		formats.add(new Format("JSLint", "^jslint:" + FILE + ":" + NUMBER + ":" + NUMBER + ":" + MESSAGE + "\\.$", 1,
				2, 3, 4));
		formats.add(new Format("JSHint", "^" + FILE + "\\(" + NUMBER + "\\): \\(lint\\) " + MESSAGE
				+ "\\. at character " + NUMBER + "$", 1, 2, 4, 3));
		formats.add(new Format("JSHint", "^" + FILE + ": line " + NUMBER + ", col " + NUMBER + ", " + MESSAGE + "\\.$",
				1, 2, 3, 4));
	}
	
	public JSXLintExtractor(PseudoFiles files) {
		super(files, "txt");
	}
	
	@Override
	protected void extract(String filename, InputStream input, BuildData data) throws IOException {
		LineIterator lines = lineIterator(input, "UTF-8");
		
		while (lines.hasNext()) {
			String line = lines.next();
			
			if (line.isEmpty())
				continue;
			
			for (Format format : formats) {
				Matcher m = format.pattern.matcher(line);
				if (!m.matches())
					continue;
				
				String file = firstNonNull(m.group(format.file), "");
				String lineNum = firstNonNull(m.group(format.line), "");
				String column = firstNonNull((format.column > 0 ? m.group(format.column) : null), "");
				String text = firstNonNull(m.group(format.text), "");
				
				if (text.isEmpty())
					continue;
				
				if (!column.isEmpty())
					lineNum += ":" + column;
				
				data.add(1, "Static analysis", "Javascript", format.type, file, lineNum, "", text);
				
				break;
			}
		}
	}
	
	private static class Format {
		
		final String type;
		final Pattern pattern;
		final int file;
		final int line;
		final int column;
		final int text;
		
		Format(String type, String pattern, int file, int line, int column, int text) {
			this.type = type;
			this.pattern = Pattern.compile(pattern);
			this.file = file;
			this.line = line;
			this.column = column;
			this.text = text;
		}
	}
}
