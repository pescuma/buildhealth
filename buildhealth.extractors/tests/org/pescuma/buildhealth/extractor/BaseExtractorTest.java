package org.pescuma.buildhealth.extractor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.pescuma.buildhealth.utils.FileHelper.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pescuma.buildhealth.computer.BuildDataComputerTracker;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.data.BuildDataTable;

import com.google.common.io.Closer;

public abstract class BaseExtractorTest {
	
	protected BuildDataTable table;
	protected BuildDataExtractorTracker tracker;
	protected BuildDataComputerTracker computerTracker;
	protected Closer closer;
	private List<File> toDelete;
	
	@Before
	public void setUp() {
		table = new BuildDataTable();
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
	
	protected void assertTable(int size, double sum, BuildData data) {
		assertEquals(size, data.size());
		assertEquals(sum, data.sum(), 0.0001);
	}
}
