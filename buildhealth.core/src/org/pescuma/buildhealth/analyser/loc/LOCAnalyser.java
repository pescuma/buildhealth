package org.pescuma.buildhealth.analyser.loc;

import static java.lang.Math.*;
import static java.util.Arrays.*;

import java.util.Collections;
import java.util.List;

import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.NumbersFormater;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * LOC,language,{files,source,comment,blank,all},file or folder name (optional)
 * </pre>
 * 
 * All values are in lines.
 * 
 * For the same file you should report (source,comment,blank) or (all), but not both.
 * 
 * (files) is the number of files and not exactly lines.
 * 
 * Example:
 * 
 * <pre>
 * 100 | LOC,java,comment
 * 900 | LOC,java,code
 * 10 | LOC,c#,blank
 * 12 | LOC,java,all,/tmp/X
 * 15 | LOC,java,files,/tmp/X
 * 12 | LOC,c++,files
 * </pre>
 */
public class LOCAnalyser implements BuildHealthAnalyser {
	
	@Override
	public List<Report> computeSimpleReport(BuildData data) {
		data = data.filter("LOC");
		if (data.isEmpty())
			return Collections.emptyList();
		
		long files = round(data.filter(2, "files").sum());
		long sources = round(data.filter(2, "source").sum());
		long comments = round(data.filter(2, "comment").sum());
		long blank = round(data.filter(2, "blank").sum());
		long all = round(data.filter(2, "all").sum());
		long total = comments + blank + sources + all;
		
		StringBuilder description = new StringBuilder();
		if (sources + comments + blank == 0) {
			append(description, "in", files, "file", "files");
			
		} else {
			append(description, sources, "of source", "of sources");
			append(description, comments, "of comment", "of comments");
			append(description, blank, "blank line", "blank lines");
			append(description, all, "unknown");
			append(description, "in", files, "file", "files");
		}
		
		return asList(new Report(BuildStatus.Good, "Lines of code", format(total), description.toString()));
	}
	
	private void append(StringBuilder out, long value, String name) {
		append(out, null, value, name, name);
	}
	
	private void append(StringBuilder out, long value, String singular, String plural) {
		append(out, null, value, singular, plural);
	}
	
	private void append(StringBuilder out, String prefix, long value, String singular, String plural) {
		if (value <= 0)
			return;
		if (out.length() > 0)
			out.append(", ");
		if (prefix != null)
			out.append(prefix).append(" ");
		out.append(format(value));
		out.append(" ");
		out.append(pluralize(value, singular, plural));
	}
	
	private String pluralize(long val, String singular, String plural) {
		return val == 1 ? singular : plural;
	}
	
	private String format(long total) {
		return NumbersFormater.format1000(total, "");
	}
	
}
