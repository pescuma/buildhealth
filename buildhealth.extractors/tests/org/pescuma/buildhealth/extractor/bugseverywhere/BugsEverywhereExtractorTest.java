package org.pescuma.buildhealth.extractor.bugseverywhere;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.tasks.BugsEverywhereExtractor;

public class BugsEverywhereExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testSimple() {
		InputStream stream = load("be-simple.xml");
		
		BugsEverywhereExtractor extractor = new BugsEverywhereExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		assertTable(2, 2, table);
		
		Assert.assertEquals(1, table.get("Tasks", "BugsEverywhere", "Bug", "wontfix", "Summary", "Assigned <a@b.com>",
				"Reporter <a@b.com>", "Fri, 13 Sep 2013 21:24:47 +0000", "bea/28f", "", "severity: minor"), 0.001f);
		Assert.assertEquals(1, table.get("Tasks", "BugsEverywhere", "Milestone", "open", "A milestone. Yay!",
				"Assigned <a@b.com>", "Creator <a@b.com>", "Fri, 13 Sep 2013 21:24:47 +0000", "bea/38f"), 0.001f);
	}
}
