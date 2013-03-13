package org.pescuma.buildhealth.extractor.xunit;

import java.io.File;

import com.thalesgroup.dtkit.junit.CppUnit;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

public class CppUnitExtractor extends XUnitExtractor {
	
	public CppUnitExtractor(File fileOrFolder) {
		super(fileOrFolder);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(CppUnit.class);
	}
	
}
