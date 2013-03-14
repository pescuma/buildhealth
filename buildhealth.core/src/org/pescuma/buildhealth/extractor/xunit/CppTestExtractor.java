package org.pescuma.buildhealth.extractor.xunit;

import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.thalesgroup.dtkit.junit.CppTestUnit;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

public class CppTestExtractor extends XUnitExtractor {
	
	public CppTestExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(CppTestUnit.class);
	}
	
}
