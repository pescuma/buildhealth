package org.pescuma.buildhealth.extractor.junit;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import org.junit.Test;
import org.pescuma.buildhealth.core.BuildData.Line;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class JUnitExtractorTest extends BaseExtractorTest {
	
	@Test
	public void test6PassedGeral() {
		InputStream stream = load("junit.6passed.xml");
		
		JUnitExtractor extractor = new JUnitExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).streamProcessed();
		verify(tracker, never()).fileProcessed(any(File.class));
		
		assertTable(6, 6, table.filter("Unit test").filter(3, "passed"));
		assertTable(6, 0.031, table.filter("Unit test").filter(3, "time"));
		assertEquals(12, table.size(), 0.0001);
		
		Collection<Line> lines = table.filter(5, "test1").filter(3, "passed").getLines();
		assertEquals(1, lines.size());
		
		Line line = lines.iterator().next();
		assertEquals(1, line.getValue(), 0.0001);
		assertArrayEquals(new String[] { "Unit test", "java", "junit", "passed", "org.pescuma.test.junit", "test1" },
				line.getInfo());
	}
	
}
