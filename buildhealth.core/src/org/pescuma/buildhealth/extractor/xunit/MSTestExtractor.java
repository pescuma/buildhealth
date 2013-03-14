package org.pescuma.buildhealth.extractor.xunit;

import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.thalesgroup.dtkit.junit.MSTest;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

public class MSTestExtractor extends XUnitExtractor {
	
	public MSTestExtractor(PseudoFiles fileOrFolder) {
		super(fileOrFolder);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(MSTest.class);
	}
	
}
