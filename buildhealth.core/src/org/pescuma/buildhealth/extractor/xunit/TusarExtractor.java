package org.pescuma.buildhealth.extractor.xunit;

import java.io.File;

import com.thalesgroup.dtkit.junit.Tusar;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

public class TusarExtractor extends XUnitExtractor {
	
	public TusarExtractor(File fileOrFolder) {
		super(fileOrFolder);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(Tusar.class);
	}
	
}
