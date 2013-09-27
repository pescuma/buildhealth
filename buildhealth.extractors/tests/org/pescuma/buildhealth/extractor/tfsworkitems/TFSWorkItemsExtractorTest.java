package org.pescuma.buildhealth.extractor.tfsworkitems;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.tasks.TFSWorkItemsExtractor;

public class TFSWorkItemsExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testSimple() {
		InputStream stream = load("tfs-wi-simple.xml");
		
		TFSWorkItemsExtractor extractor = new TFSWorkItemsExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(1, 1, table);
		
		assertEquals(1,
				table.get("Tasks", "TFS", "Bug", "Active", "Description bla", "Assigned", "Created", "Date", "1234"),
				0.0001);
	}
	
	@Test
	public void testWithChildren() {
		InputStream stream = load("tfs-wi-children.xml");
		
		TFSWorkItemsExtractor extractor = new TFSWorkItemsExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(3, 3, table);
		
		assertEquals(1, table.get("Tasks", "TFS", "Tyype", "Sttate", "Ti tle", "", "Pescuma", "01/02/2003 01:23:45",
				"12", "", "Team Project: VSS"), 0.0001);
		assertEquals(1, table.get("Tasks", "TFS", "B Tyype", "B Sttate", "B Ti tle", "", "B Pescuma",
				"B 01/02/2003 01:23:45", "13", "", "Team Project: B VSS"), 0.0001);
		assertEquals(1, table.get("Tasks", "TFS", "C Tyype", "C Sttate", "C Ti tle", "", "C Pescuma",
				"C 01/02/2003 01:23:45", "14", "13", "Team Project: C VSS"), 0.0001);
	}
}
