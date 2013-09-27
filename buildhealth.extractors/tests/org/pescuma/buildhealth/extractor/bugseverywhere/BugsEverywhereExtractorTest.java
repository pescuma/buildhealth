package org.pescuma.buildhealth.extractor.bugseverywhere;

import org.junit.Assert;
import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.tasks.BugsEverywhereExtractor;

public class BugsEverywhereExtractorTest extends BaseExtractorTest {
	
	private BugsEverywhereExtractor extractorFor(String text) {
		BugsEverywhereExtractor extractor = new BugsEverywhereExtractor(new PseudoFiles(getInputStream(text)));
		return extractor;
	}
	
	@Test
	public void testSimple() {
		BugsEverywhereExtractor extractor = extractorFor("bea/28f:om: Simple message");
		
		extractor.extractTo(table, tracker);
		
		assertTable(1, 1, table);
		Assert.assertEquals(1, table.get("Tasks", "BugsEverywhere", "Bug", "Open", "Simple message", "", "", "",
				"bea/28f", "", "Severity: Minor"), 0.001f);
	}
	
	@Test
	public void testMilestone() {
		BugsEverywhereExtractor extractor = extractorFor("bea/28f:ot: Simple message");
		
		extractor.extractTo(table, tracker);
		
		assertTable(1, 1, table);
		Assert.assertEquals(1,
				table.get("Tasks", "BugsEverywhere", "Milestone", "Open", "Simple message", "", "", "", "bea/28f"),
				0.001f);
	}
	
	@Test
	public void testTags() {
		BugsEverywhereExtractor extractor = extractorFor("bea/28f:om:tag1,tag2: Simple message");
		
		extractor.extractTo(table, tracker);
		
		assertTable(1, 1, table);
		Assert.assertEquals(1, table.get("Tasks", "BugsEverywhere", "Bug", "Open", "Simple message", "", "", "",
				"bea/28f", "", "Severity: Minor\nTags: tag1, tag2"), 0.001f);
	}
	
	@Test
	public void testMultipleLines() {
		BugsEverywhereExtractor extractor = extractorFor("bea/28f:om: Missing demuxalizer functionality\n"
				+ "bea/81a:om: Missing whatsit...\n" //
				+ "bea/91a:om:tag: With tags");
		
		extractor.extractTo(table, tracker);
		
		assertTable(3, 3, table);
	}
}
