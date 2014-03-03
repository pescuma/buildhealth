package org.pescuma.buildhealth.extractor.resharper.dupfinder;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.ResharperDupFinderExtractor;

public class ResharperDupFinderExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testOne() {
		InputStream stream = load("resharper.dupfinder.1.xml");
		
		ResharperDupFinderExtractor extractor = new ResharperDupFinderExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(1, 1, table);
		
		assertEquals(1, table.get("Static analysis", //
				"C#", //
				"Resharper DupFinder", //
				"..\\A.cs>13:1:17:9999|..\\B.cs>8:1:13:9999", //
				"Code duplication", //
				"There are 6 lines of code duplicated in:\n  ..\\A.cs lines 13 to 17\n  ..\\B.cs lines 8 to 13", //
				"", "Cost: 132"), 0.0001);
	}
	
}
