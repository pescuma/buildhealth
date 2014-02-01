package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.apache.commons.io.IOUtils.*;
import static org.pescuma.buildhealth.utils.ObjectUtils.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.LineIterator;
import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// https://github.com/douglascrockford/JSLint
// https://github.com/FND/jslint-reporter
// http://code.google.com/p/jslint4java/
// https://github.com/jamietre/SharpLinter
// http://www.javascriptlint.com
// http://www.jshint.com
public class JSXLintExtractor extends BaseXMLExtractor {
	
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
		super(files, "txt", "out", "xml");
	}
	
	@Override
	protected void extract(String filename, InputStream input, BuildData data) throws IOException {
		if (!input.markSupported())
			input = new BufferedInputStream(input);
		
		if (isXML(filename, input))
			super.extract(filename, input, data);
		else
			extractTxt(filename, input, data);
	}
	
	private boolean isXML(String filename, InputStream input) throws IOException {
		if (filename != null && filename.endsWith(".xml"))
			return true;
		
		byte[] start = new byte[5];
		input.mark(start.length + 1);
		int read = input.read(start);
		input.reset();
		
		if (read != start.length)
			return false;
		
		return Arrays.equals(start, "<?xml".getBytes()) || Arrays.equals(start, "<jsli".getBytes())
				|| Arrays.equals(start, "<jshi".getBytes());
	}
	
	private void extractTxt(String filename, InputStream input, BuildData data) throws IOException {
		LineIterator lines = lineIterator(input, "UTF-8");
		
		while (lines.hasNext()) {
			String line = lines.next();
			
			if (line.isEmpty())
				continue;
			
			for (Format format : formats) {
				Matcher m = format.pattern.matcher(line);
				if (!m.matches())
					continue;
				
				String type = format.type;
				String file = m.group(format.file);
				String lineNum = m.group(format.line);
				String column = (format.column > 0 ? m.group(format.column) : null);
				String text = m.group(format.text);
				
				add(data, type, file, lineNum, column, text);
				
				break;
			}
		}
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, new String[] { "jslint", "jshint" }, filename);
		
		String type = toType(doc.getRootElement().getName());
		
		for (Element el : doc.getRootElement().getChildren("file"))
			extractFile(data, el, type);
	}
	
	private String toType(String name) {
		if (name.equals("jslint"))
			return "JSLint";
		else
			return "JSHint";
	}
	
	private void extractFile(BuildData data, Element file, String type) {
		String fileName = file.getAttributeValue("name", "");
		
		for (Element el : file.getChildren("issue"))
			extractIssue(data, el, type, fileName);
	}
	
	private void extractIssue(BuildData data, Element el, String type, String fileName) {
		String lineNum = el.getAttributeValue("line", "");
		String column = el.getAttributeValue("char", "");
		String text = el.getAttributeValue("reason", "");
		
		add(data, type, fileName, lineNum, column, text);
	}
	
	private void add(BuildData data, String type, String file, String lineNum, String column, String text) {
		file = firstNonNull(file, "");
		lineNum = firstNonNull(lineNum, "");
		column = firstNonNull(column, "");
		text = firstNonNull(text, "");
		
		if (text.isEmpty())
			return;
		
		if (!column.isEmpty())
			lineNum += ":" + column;
		
		data.add(1, "Static analysis", "Javascript", type, file, lineNum, "", text);
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
