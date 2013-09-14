package org.pescuma.buildhealth.analyser.performance;

import static java.util.Arrays.*;
import static org.pescuma.buildhealth.analyser.BuildStatusHelper.*;
import static org.pescuma.buildhealth.analyser.NumbersFormater.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Line;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.prefs.Preferences;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Performance,language,framework,{type:ms,runsPerS},{test},{subTest},{category}
 * </pre>
 * 
 * The value is the number for covered or total entries. For all entries you must have both.
 * 
 * Place may contain multiple columns. The value for a line {a,b,c} is the total over all lines(a,b,c,**}. You should
 * always have a place type all.
 * 
 * Example:
 * 
 * <pre>
 * 10 | Performance,Java,Japex,ms,SerializaionA,small
 * 15 | Performance,Java,Japex,ms,SerializaionA,big
 * 7 | Performance,Java,Japex,ms,SerializaionB,small
 * 12 | Performance,Java,Japex,ms,SerializaionA,big
 * 10 | Performance,Java,Japex,runsPerS,Serializaion,big,Timers
 * </pre>
 */
@MetaInfServices
public class PerformanceAnalyser implements BuildHealthAnalyser {
	
	@Override
	public String getName() {
		return "Performance";
	}
	
	@Override
	public int getPriority() {
		return 500;
	}
	
	@Override
	public List<BuildHealthPreference> getPreferences() {
		List<BuildHealthPreference> result = new ArrayList<BuildHealthPreference>();
		
		result.add(new BuildHealthPreference("Minimun runs per second for a Good build", "<no limit>",
				"performace", "runsPerS", "good"));
		result.add(new BuildHealthPreference("Minimun runs per second for a Good build", "<no limit>",
				"performace", "runsPerS", "warn"));
		
		result.add(new BuildHealthPreference("Maximun run time (ms) for a Good build", "<no limit>",
				"performace", "ms", "good"));
		result.add(new BuildHealthPreference("Maximun run time (ms) for a Good build", "<no limit>",
				"performace", "ms", "warn"));
		
		result.add(new BuildHealthPreference("How to show the agregated results (runsPerS or ms)",
				"<ms if both available, else what is available>", "performace", "report"));
		
		return Collections.unmodifiableList(result);
	}
	
	@Override
	public List<Report> computeReport(BuildData data, Preferences prefs, int opts) {
		data = data.filter("Performance");
		if (data.isEmpty())
			return Collections.emptyList();
		
		prefs = prefs.child("performance");
		
		int msCount = 0;
		double msTotal = 0;
		double runsPerSTotal = 0;
		
		for (Line line : data.filter(3, "ms").getLines()) {
			msCount++;
			double val = line.getValue();
			msTotal += val;
			runsPerSTotal += 1000 / val;
		}
		
		for (Line line : data.filter(3, "runsPerS").getLines()) {
			double val = line.getValue();
			msTotal += 1000 / val;
			runsPerSTotal += val;
		}
		
		boolean reportMs = isToReportMs(prefs, msCount);
		
		String text;
		if (reportMs)
			text = format1000(msTotal / 1000, "s");
		else
			text = format1000(runsPerSTotal, "") + " runs per second";
		
		BuildStatus status = BuildStatus.Good;
		status = status.mergeWith(computeStatus(prefs.child("runsPerS"), runsPerSTotal, true));
		status = status.mergeWith(computeStatus(prefs.child("ms"), msTotal, false));
		
		return asList(new Report(status, getName(), text));
	}
	
	private boolean isToReportMs(Preferences prefs, int msTotal) {
		String reportType = prefs.get("report", "");
		
		if (reportType.equals("ms")) {
			return true;
			
		} else if (reportType.equals("runsPerS")) {
			return false;
			
		} else {
			return (msTotal > 0);
		}
	}
	
}
