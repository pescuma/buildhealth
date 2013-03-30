package org.pescuma.buildhealth.analyser.staticanalysis;

import static java.util.Arrays.*;
import static org.pescuma.buildhealth.analyser.NumbersFormater.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Value;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Static analysis,language,framework,filename,line,category,description
 * </pre>
 * 
 * The number is the number of violations in that place. Empty line means all file.
 * 
 * Example:
 * 
 * <pre>
 * 1 | Static analysis,Java,Task,/a/b.java,12,Type1,Go to bed
 * 10 | Static analysis,Java,Task,/a/c.java,,Type2
 * </pre>
 */
public class StaticAnalysisAnalyser implements BuildHealthAnalyser {
	
	@Override
	public List<Report> computeSimpleReport(BuildData data) {
		data = data.filter("Static analysis");
		if (data.isEmpty())
			return Collections.emptyList();
		
		double total = 0;
		StringBuilder description = new StringBuilder();
		
		for (Map.Entry<String, Value> entry : data.sumDistinct(2).entrySet()) {
			total += entry.getValue().value;
			
			if (description.length() > 0)
				description.append(", ");
			description.append(entry.getKey()).append(": ").append(format1000(entry.getValue().value));
		}
		
		return asList(new Report(BuildStatus.Good, "Static analysis", format1000(total), description.toString()));
	}
	
}
