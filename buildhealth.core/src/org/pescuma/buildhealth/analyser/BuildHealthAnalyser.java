package org.pescuma.buildhealth.analyser;

import java.util.List;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.Report;

public interface BuildHealthAnalyser {
	
	List<Report> computeSimpleReport(BuildData data);
}
