package org.pescuma.buildhealth.extractor.unittest.xunit;

import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.thalesgroup.dtkit.junit.PHPUnit;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

// https://github.com/sebastianbergmann/phpunit/
public class PHPUnitExtractor extends XUnitExtractor {
	
	public PHPUnitExtractor(PseudoFiles fileOrFolder) {
		super(fileOrFolder);
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(PHPUnit.class);
	}
	
	@Override
	protected String getLanguage() {
		return "PHP";
	}
	
}
