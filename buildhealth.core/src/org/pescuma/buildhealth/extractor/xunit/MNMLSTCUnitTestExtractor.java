package org.pescuma.buildhealth.extractor.xunit;

import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.thalesgroup.dtkit.junit.UnitTest;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

public class MNMLSTCUnitTestExtractor extends XUnitExtractor {
	
	public MNMLSTCUnitTestExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(UnitTest.class);
	}
	
}
