package org.pescuma.buildhealth.extractor.fxcop;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.pescuma.buildhealth.extractor.BaseExtractorTest;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.FxCopExtractor;

public class FxCopExtractorTest extends BaseExtractorTest {
	
	@Test
	public void testSimple() {
		InputStream stream = load("fxcop.xml");
		
		FxCopExtractor extractor = new FxCopExtractor(new PseudoFiles(stream));
		
		extractor.extractTo(table, tracker);
		
		verify(tracker).onStreamProcessed();
		verify(tracker, never()).onFileProcessed(any(File.class));
		
		assertTable(6, 6, table);
		
		assertEquals(
				1,
				table.get(
						"Static analysis",
						"C#",
						"FxCop",
						"Namespace.Name",
						"",
						"Design/Avoid namespaces with few types",
						"Consider merging the types defined in 'Namespace.Name' with another namespace.",
						"Medium",
						"Level: Warning\n" //
								+ "Certainty: 50%\n" //
								+ "Target: Namespace.Name\n" //
								+ "Target Kind: Namespace\n" //
								+ "Resolution: Consider merging the types defined in 'Namespace.Name' with another namespace.\n" //
								+ "Help: http://msdn.microsoft.com/library/ms182130(VS.100).aspx\n" //
								+ "Category: Microsoft.Design\n" //
								+ "CheckId: CA1020\n" //
								+ "Info: A namespace should generally have more than five types.\n" //
								+ "Created: 2013-09-18 16:57:47Z\n" //
								+ "Status: Active\n" //
								+ "Fix Category: Non Breaking", //
						"http://msdn.microsoft.com/library/ms182130(VS.100).aspx"), 0.0001);
		
		assertEquals(
				1,
				table.get(
						"Static analysis",
						"C#",
						"FxCop",
						"c:\\devel\\asd\\FcType1.cs",
						"21",
						"Design/Do not raise exceptions in unexpected locations",
						"'FcType1.MyProperty.get()' creates an exception of type 'NotImplementedException', an exception type that should not be raised in a property. If this exception instance might be raised, use a different exception type, convert this property into a method, or change this property's logic so that it no longer raises an exception.",
						"High",
						"Level: Error\n" //
								+ "Certainty: 90%\n" //
								+ "Target: Dll.Namespace.FcType1 #MyProperty #get_MyProperty()\n" //
								+ "Target Kind: Method\n" //
								+ "Resolution: 'FcType1.MyProperty.get()' creates an exception of type 'NotImplementedException', an exception type that should not be raised in a property. If this exception instance might be raised, use a different exception type, convert this property into a method, or change this property's logic so that it no longer raises an exception.\n" //
								+ "Help: http://msdn.microsoft.com/library/bb386039(VS.100).aspx\n" //
								+ "Category: Microsoft.Design\n" //
								+ "CheckId: CA1065\n" //
								+ "Info: Do not explicitly raise exceptions from unexpected locations. There are some methods, such as Equals and GetHashCode, which users do not expect to raise exceptions. Therefore calls to these methods are not commonly wrapped in try catch blocks.\n" //
								+ "Created: 2013-09-18 16:57:47Z\n" //
								+ "Status: Active\n" //
								+ "Fix Category: Breaking", //
						"http://msdn.microsoft.com/library/bb386039(VS.100).aspx"), 0.0001);
	}
}
