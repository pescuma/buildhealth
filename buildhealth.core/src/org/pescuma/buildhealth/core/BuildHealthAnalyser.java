package org.pescuma.buildhealth.core;

import java.util.List;

public interface BuildHealthAnalyser {
	
	List<Report> computeSimpleReport(BuildData data);
}
