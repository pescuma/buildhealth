package org.pescuma.buildhealth.extractor.unittest.xunit;

import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.thalesgroup.dtkit.junit.BoostTest;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

// http://www.boost.org/doc/libs/1_53_0/libs/test/doc/html/index.html
public class BoostTestExtractor extends XUnitExtractor {
	
	public BoostTestExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(BoostTest.class);
	}
	
	@Override
	protected String getLanguage() {
		return "C++";
	}
	
}
