package org.pescuma.buildhealth.extractor.xunit;

import java.io.File;

import com.thalesgroup.dtkit.junit.AUnit;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

public class AUnitExtractor extends XUnitExtractor {
	
	public AUnitExtractor(File fileOrFolder) {
		super(fileOrFolder);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(AUnit.class);
	}
	
}
