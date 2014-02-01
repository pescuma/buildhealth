package org.pescuma.buildhealth.extractor.japex;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.performance.JapexExtractor;

public class JapexExtractorTest extends BaseExtractorTest {
	
	@Test
	public void test6PassedGeral() {
		InputStream stream = load("japex.xml");
		
		JapexExtractor extractor = new JapexExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(2, 0.175 + 0.315, table.filter("Performance"));
		
		assertEquals(0.175, table.get("Performance", "Java", "Japex", "ms", "Driver1 Driver", "TestSub"), 0.0001);
		assertEquals(0.315, table.get("Performance", "Java", "Japex", "ms", "Driver1 Driver", "TestClass"), 0.0001);
	}
	
}
