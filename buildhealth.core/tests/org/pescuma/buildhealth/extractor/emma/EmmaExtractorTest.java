package org.pescuma.buildhealth.extractor.emma;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class EmmaExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testOnlyAll() {
		InputStream stream = load("emma.onlyall.xml");
		
		EmmaExtractor extractor = new EmmaExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).streamProcessed();
		verify(tracker, never()).fileProcessed(any(File.class));
		
		assertEquals(8, table.size());
		assertEquals(8, table.filter("Coverage", "java", "emma").size());
		
		assertEquals(85, table.get("Coverage", "java", "emma", "class", "covered", "all"), 0.0001);
		assertEquals(85, table.get("Coverage", "java", "emma", "class", "total", "all"), 0.0001);
		assertEquals(345, table.get("Coverage", "java", "emma", "method", "covered", "all"), 0.0001);
		assertEquals(61, table.get("Coverage", "java", "emma", "method", "total", "all"), 0.0001);
		assertEquals(4997, table.get("Coverage", "java", "emma", "block", "covered", "all"), 0.0001);
		assertEquals(4846, table.get("Coverage", "java", "emma", "block", "total", "all"), 0.0001);
		assertEquals(346.3, table.get("Coverage", "java", "emma", "line", "covered", "all"), 0.0001);
		assertEquals(3135, table.get("Coverage", "java", "emma", "line", "total", "all"), 0.0001);
	}
	
	@Test
	public void testMethods() {
		InputStream stream = load("emma.methods.xml");
		
		EmmaExtractor extractor = new EmmaExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).streamProcessed();
		verify(tracker, never()).fileProcessed(any(File.class));
		
		assertEquals(50, table.size());
		assertTable(8, 145100.3, table.filter("Coverage", "java", "emma").filter(5, "all"));
		assertTable(8, 10496, table.filter("Coverage", "java", "emma").filter(5, "package"));
		assertTable(8, 3020, table.filter("Coverage", "java", "emma").filter(5, "file"));
		assertTable(8, 3019, table.filter("Coverage", "java", "emma").filter(5, "class"));
		assertTable(18, 3018, table.filter("Coverage", "java", "emma").filter(5, "method"));
	}
	
	@Test
	public void testBig() {
		InputStream stream = load("emma.big.xml");
		
		EmmaExtractor extractor = new EmmaExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).streamProcessed();
		verify(tracker, never()).fileProcessed(any(File.class));
		
		assertEquals(14414, table.size());
		assertEquals(8, table.filter("Coverage", "java", "emma").filter(5, "all").size());
	}
	
}
