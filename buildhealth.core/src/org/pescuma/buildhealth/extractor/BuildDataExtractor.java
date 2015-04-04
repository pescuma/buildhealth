package org.pescuma.buildhealth.extractor;

import org.pescuma.datatable.DataTable;

public interface BuildDataExtractor {
	
	void extractTo(DataTable data, BuildDataExtractorTracker tracker);
	
}
