package org.pescuma.buildhealth.extractor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.pescuma.buildhealth.utils.FileHelper.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.ReaderInputStream;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pescuma.buildhealth.computer.BuildDataComputerTracker;
import org.pescuma.datatable.DataTable;
import org.pescuma.datatable.MemoryDataTable;

import com.google.common.io.Closer;

public abstract class BaseExtractorTest {
	
	protected MemoryDataTable table;
	protected BuildDataExtractorTracker tracker;
	protected BuildDataComputerTracker computerTracker;
	protected Closer closer;
	private List<File> toDelete;
	
	@Before
	public void setUp() {
		table = new MemoryDataTable();
		tracker = Mockito.mock(BuildDataExtractorTracker.class);
		closer = Closer.create();
		
		toDelete = new ArrayList<File>();
		computerTracker = Mockito.mock(BuildDataComputerTracker.class);
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				toDelete.add((File) invocation.getArguments()[0]);
				return null;
			}
		}).when(computerTracker).onFileOutputCreated(any(File.class));
	}
	
	@After
	public void tearDown() throws IOException {
		closer.close();
		
		for (File file : toDelete)
			deleteFile(file);
	}
	
	protected InputStream load(String filename) {
		Class<?> cls = getClass();
		
		InputStream result = cls.getClassLoader().getResourceAsStream(
				cls.getPackage().getName().replace('.', '/') + "/" + filename);
		if (result == null)
			throw new RuntimeException("Could not read " + filename);
		
		closer.register(result);
		
		return result;
	}
	
	protected ReaderInputStream getInputStream(String str) {
		return new ReaderInputStream(new StringReader(str), "UTF-8");
	}
	
	protected void assertTable(int size, double sum, DataTable data) {
		assertEquals(size, data.count());
		assertEquals(sum, data.sum(), 0.0001);
	}
}
