package org.pescuma.buildhealth.extractor.xunit;

import java.io.File;

import com.thalesgroup.dtkit.junit.UnitTest;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

public class MNMLSTCUnitTestExtractor extends XUnitExtractor {
	
	public MNMLSTCUnitTestExtractor(File fileOrFolder) {
		super(fileOrFolder);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(UnitTest.class);
	}
	
}
