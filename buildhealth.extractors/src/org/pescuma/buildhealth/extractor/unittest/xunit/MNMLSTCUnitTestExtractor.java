package org.pescuma.buildhealth.extractor.unittest.xunit;

import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.thalesgroup.dtkit.junit.UnitTest;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

// https://github.com/mnmlstc/unittest
public class MNMLSTCUnitTestExtractor extends XUnitExtractor {
	
	public MNMLSTCUnitTestExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(UnitTest.class);
	}
	
	@Override
	protected String getLanguage() {
		return "C++";
	}
	
}
