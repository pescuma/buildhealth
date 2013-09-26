package org.pescuma.buildhealth.extractor.diskusage;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class DiskUsageExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testNoTag() {
		DiskUsageExtractor extractor = new DiskUsageExtractor(new PseudoFiles(getInputStream("asdf")));
		
		extractor.extractTo(table, tracker);
		
		assertTable(1, 4, table);
		assertTable(1, 4, table.filter("Disk usage", ""));
	}
	
	@Test
	public void testTag() {
		DiskUsageExtractor extractor = new DiskUsageExtractor(new PseudoFiles(getInputStream("123\n456")), "TAG");
		
		extractor.extractTo(table, tracker);
		
		assertTable(1, 7, table);
		assertTable(1, 7, table.filter("Disk usage", "TAG"));
	}
	
}
