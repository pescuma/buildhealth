package org.pescuma.buildhealth.analyser.coverage;

import static java.util.Collections.*;

import java.util.List;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

public class CoverageReport extends Report {
	
	private final List<CoverageMetric> coverages;
	private final String placeType;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CoverageReport(BuildStatus status, String name, String value, String description,
			List<CoverageMetric> coverages, String placeType, String problemDescription, List<CoverageReport> children) {
		super(status, name, value, description, problemDescription, (List) children);
		
		this.coverages = unmodifiableList(coverages);
		this.placeType = placeType;
	}
	
	public List<CoverageMetric> getCoverages() {
		return coverages;
	}
	
	public String getPlaceType() {
		return placeType;
	}
	
}
