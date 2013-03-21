package org.pescuma.buildhealth.analyser.coverage;

import java.util.Collections;
import java.util.List;

import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.Report;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Coverage,language,framework,{type:line,block,method,class},{covered,total},{place type:all,file,package,class,method},place
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
 * 10 | Coverage,java,emma,line,covered,all
 * 15 | Coverage,java,emma,line,total,all
 * 1 | Coverage,java,emma,line,covered,class,a,b,c,D
 * 2 | Coverage,java,emma,line,total,class,a,b,c,D
 * </pre>
 */
public class CoverageAnalyser implements BuildHealthAnalyser {
	
	@Override
	public List<Report> computeSimpleReport(BuildData data) {
		data = data.filter("Coverage");
		if (data.isEmpty())
			return Collections.emptyList();
		
		return Collections.emptyList();
	}
	
}
