package org.pescuma.buildhealth.extractor.loc;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class CLOCExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testSimple() {
		CLOCExtractor extractor = new CLOCExtractor(new PseudoFiles(load("cloc.simple.csv")));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(3, 61, table.filter("LOC").filter(2, "files"));
		assertTable(1, 55, table.filter("LOC", "Java", "files"));
		assertTable(1, 609, table.filter("LOC", "Java", "blank"));
		assertTable(1, 89, table.filter("LOC", "Java", "comment"));
		assertTable(1, 1795, table.filter("LOC", "Java", "code"));
	}
	
	@Test
	public void testFiles() {
		CLOCExtractor extractor = new CLOCExtractor(new PseudoFiles(load("cloc.files.csv")));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(61, 61, table.filter("LOC").filter(2, "files"));
		assertTable(55, 55, table.filter("LOC", "Java", "files"));
		assertTable(55, 609, table.filter("LOC", "Java", "blank"));
		assertTable(55, 89, table.filter("LOC", "Java", "comment"));
		assertTable(55, 1795, table.filter("LOC", "Java", "code"));
	}
	
}
