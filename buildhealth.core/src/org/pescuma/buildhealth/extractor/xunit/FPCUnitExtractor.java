package org.pescuma.buildhealth.extractor.xunit;

import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.thalesgroup.dtkit.junit.FPCUnit;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

public class FPCUnitExtractor extends XUnitExtractor {
	
	public FPCUnitExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(FPCUnit.class);
	}
	
}
