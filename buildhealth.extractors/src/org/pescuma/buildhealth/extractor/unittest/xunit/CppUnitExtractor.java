package org.pescuma.buildhealth.extractor.unittest.xunit;

import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.thalesgroup.dtkit.junit.CppUnit;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

// http://sourceforge.net/projects/cppunit/
public class CppUnitExtractor extends XUnitExtractor {
	
	public CppUnitExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(CppUnit.class);
	}
	
	@Override
	protected String getLanguage() {
		return "C++";
	}
	
}
