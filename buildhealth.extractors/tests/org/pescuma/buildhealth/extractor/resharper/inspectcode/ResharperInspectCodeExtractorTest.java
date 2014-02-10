package org.pescuma.buildhealth.extractor.resharper.inspectcode;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.ResharperInspectCodeExtractor;

public class ResharperInspectCodeExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testOne() {
		InputStream stream = load("resharper.inspectcode.1.xml");
		
		ResharperInspectCodeExtractor extractor = new ResharperInspectCodeExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(1, 1, table);
		
		assertEquals(1, table.get("Static analysis", //
				"C#", //
				"Resharper InspectCode", //
				"Proj1\\AssemblyInfo.cs", //
				"2", //
				"Redundancies in Code", //
				"Using directive is not required by the code and can be safely removed", //
				"Medium"), 0.0001);
	}
	
}
