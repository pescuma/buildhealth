package org.pescuma.buildhealth.extractor.mstest;

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
import org.pescuma.buildhealth.extractor.unittest.MSTestExtractor;

public class MSTestExtractorTest extends BaseExtractorTest {
	
	@Test
	public void test6PassedGeral() {
		InputStream stream = load("mstest.xml");
		
		MSTestExtractor extractor = new MSTestExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(2, 2, table.filter("Unit test").filter(3, "passed"));
		assertTable(1, 1, table.filter("Unit test").filter(3, "failed"));
		assertTable(3, 3724.465, table.filter("Unit test").filter(3, "time"));
		assertEquals(6, table.size(), 0.0001);
		
		Collection<Line> lines = table.filter(5, "Test1").filter(3, "passed").getLines();
		assertEquals(1, lines.size());
		
		Line line = lines.iterator().next();
		assertEquals(1, line.getValue(), 0.0001);
		assertArrayEquals(new String[] { "Unit test", "C#", "MSTest", "passed", "A.B", "Test1",
				"c:\\Test Project\\bin\\Debug\\A.dll" }, line.getColumns());
	}
}
