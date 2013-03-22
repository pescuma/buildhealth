package org.pescuma.buildhealth.analyser.loc;

import static java.lang.Math.*;
import static java.util.Arrays.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.NumbersFormater;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Value;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * LOC,language,type,file or folder name (optional)
 * </pre>
 * 
 * All values are in lines.
 * 
 * Usual values for type are: source, comment, blank, unknown. There is a special type called files that means the
 * number of files (and not a line count).
 * 
 * Example:
 * 
 * <pre>
 * 100 | LOC,java,comment
 * 900 | LOC,java,code
 * 10 | LOC,c#,blank
 * 12 | LOC,java,unknown,/tmp/X
 * 15 | LOC,java,files,/tmp/X
 * 12 | LOC,c++,files
 * </pre>
 * 
 * If not LOC information is found, it will try to get this information from coverage data.
 */
public class LOCAnalyser implements BuildHealthAnalyser {
	
	@Override
	public List<Report> computeSimpleReport(BuildData data) {
		List<Report> result = computeFromLOC(data);
		
		if (result.isEmpty())
			result = computeFromCoverage(data);
		
		return result;
	}
	
	private List<Report> computeFromCoverage(BuildData data) {
		data = data.filter("Coverage").filter(5, "all").filter(3, "line").filter(4, "total");
		if (data.isEmpty())
			return Collections.emptyList();
		
		long lines = round(data.sum());
		if (lines < 1)
			return Collections.emptyList();
		
		return asList(new Report(BuildStatus.Good, "Lines of code", format(lines), "from coverage"));
	}
	
	private List<Report> computeFromLOC(BuildData data) {
		data = data.filter("LOC");
		if (data.isEmpty())
			return Collections.emptyList();
		
		Map<String, BuildData.Value> vals = data.sumDistinct(2);
		
		Value files = vals.remove("files");
		
		int count = vals.size();
		
		long total = 0;
		for (Map.Entry<String, BuildData.Value> val : vals.entrySet())
			total += round(val.getValue().value);
		
		StringBuilder description = new StringBuilder();
		
		if (count > 1) {
			for (Map.Entry<String, BuildData.Value> val : vals.entrySet())
				append(description, round(val.getValue().value), val.getKey());
		}
		
		if (files != null)
			append(description, "in", round(files.value), "file", "files");
		
		return asList(new Report(BuildStatus.Good, "Lines of code", format(total), description.toString()));
	}
	
	private void append(StringBuilder out, long value, String name) {
		append(out, null, value, name, name);
	}
	
	private void append(StringBuilder out, String prefix, long value, String singular, String plural) {
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
