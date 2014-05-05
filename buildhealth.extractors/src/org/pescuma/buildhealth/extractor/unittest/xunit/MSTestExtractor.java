package org.pescuma.buildhealth.extractor.unittest.xunit;

import org.pescuma.buildhealth.extractor.PseudoFiles;

import com.thalesgroup.dtkit.junit.MSTest;
import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricFactory;

// http://msdn.microsoft.com/en-us/library/ms182486.aspx
public class MSTestExtractor extends XUnitExtractor {
	
	public MSTestExtractor(PseudoFiles fileOrFolder) {
		super(fileOrFolder, "trx");
	}
	
	@Override
	protected InputMetric getInputMetric() {
		return InputMetricFactory.getInstance(MSTest.class);
	}
	
	@Override
	protected String getLanguage() {
		// Guess
		return "C#";
	}
	
}
