package org.pescuma.buildhealth.extractor.diskusage;

import java.io.StringReader;

import org.apache.commons.io.input.ReaderInputStream;
import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class DiskUsageExtractorTest extends BaseExtractorTest {
	
	private ReaderInputStream getInputStream(String str) {
		return new ReaderInputStream(new StringReader(str), "UTF-8");
	}
	
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
