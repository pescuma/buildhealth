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
		
		assertEquals(1, table.get("Tasks", "TFS", "Bug", "Active", "Description bla", "", "1234"), 0.0001);
	}
	
	@Test
	public void testWithChildren() {
		InputStream stream = load("tfs-wi-children.xml");
		
		TFSWorkItemsExtractor extractor = new TFSWorkItemsExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(3, 3, table);
		
		assertEquals(1, table.get("Tasks", "TFS", "Tyype", "Sttate", "Ti tle", "", "12", "",
				"Team Project: VSS\nCreated By: Pescuma\nCreated Date: 01/02/2003 01:23:45"), 0.0001);
		assertEquals(1, table.get("Tasks", "TFS", "B Tyype", "B Sttate", "B Ti tle", "", "13", "",
				"Team Project: B VSS\nCreated By: B Pescuma\nCreated Date: B 01/02/2003 01:23:45"), 0.0001);
		assertEquals(1, table.get("Tasks", "TFS", "C Tyype", "C Sttate", "C Ti tle", "", "14", "13",
				"Team Project: C VSS\nCreated By: C Pescuma\nCreated Date: C 01/02/2003 01:23:45"), 0.0001);
	}
}
