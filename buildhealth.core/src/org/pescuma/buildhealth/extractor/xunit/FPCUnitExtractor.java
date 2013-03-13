package org.pescuma.buildhealth.extractor.xunit;

import java.io.File;

import com.thalesgroup.dtkit.junit.FPCUnit;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

public class FPCUnitExtractor extends XUnitExtractor {
	
	public FPCUnitExtractor(File fileOrFolder) {
		super(fileOrFolder);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(FPCUnit.class);
	}
	
}
