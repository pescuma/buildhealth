package org.pescuma.buildhealth.extractor.unittest.xunit;

import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.thalesgroup.dtkit.junit.AUnit;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

// http://libre.adacore.com/tools/aunit/
public class AUnitExtractor extends XUnitExtractor {
	
	public AUnitExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(AUnit.class);
	}
	
	@Override
	protected String getLanguage() {
		return "Ada";
	}
	
}
