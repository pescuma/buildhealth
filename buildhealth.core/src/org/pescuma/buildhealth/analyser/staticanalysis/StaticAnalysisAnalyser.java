package org.pescuma.buildhealth.analyser.staticanalysis;

import static java.util.Arrays.*;
import static org.pescuma.buildhealth.analyser.NumbersFormater.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pescuma.buildhealth.analyser.BaseBuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyserPreference;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Value;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.prefs.Preferences;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Static analysis,language,framework,filename,line or beginLine:beginColumn:endLine:endColumn,category,description,details,URL with details
 * </pre>
 * 
 * The number is the number of violations in that place. Empty line means all file.
 * 
 * Example:
 * 
 * <pre>
 * 1 | Static analysis,Java,Task,/a/b.java,12,Type1,Go to bed
 * 1 | Static analysis,Java,PMD,/a/b.java,12:1:12:5,A/Type1,Go to bed,http://a.com/info.html
 * 10 | Static analysis,Java,Task,/a/c.java,,Type2
 * </pre>
 */
public class StaticAnalysisAnalyser extends BaseBuildHealthAnalyser {
	
	@Override
	public String getName() {
		return "Static analysis";
	}
	
	@Override
	public List<BuildHealthAnalyserPreference> getPreferences() {
		List<BuildHealthAnalyserPreference> result = new ArrayList<BuildHealthAnalyserPreference>();
		
		result.add(new BuildHealthAnalyserPreference("Maximun munber of violations for a Good build", "<no limit>",
				"staticanalysis", "good"));
		result.add(new BuildHealthAnalyserPreference("Maximun munber of violations for a So So build", "<no limit>",
				"staticanalysis", "warn"));
		
		return Collections.unmodifiableList(result);
	}
	
	@Override
	public List<Report> computeSimpleReport(BuildData data, Preferences prefs) {
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
		
		prefs = prefs.child("staticanalysis");
		double good = prefs.get("good", Double.MAX_VALUE);
		double warn = prefs.get("warn", Double.MAX_VALUE);
		
		return asList(new Report(computeStatus(total, good, warn), getName(), format1000(total), description.toString()));
	}
	
	private BuildStatus computeStatus(double total, double good, double warn) {
		if (total < good + 0.01)
			return BuildStatus.Good;
		if (total < warn + 0.01)
			return BuildStatus.SoSo;
		return BuildStatus.Problematic;
	}
}
