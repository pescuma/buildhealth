package org.pescuma.buildhealth.extractor;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildDataExtractorTracker;
import org.pescuma.buildhealth.core.data.BuildDataTable;

import com.google.common.io.Closer;

public abstract class BaseExtractorTest {
	
	protected BuildDataTable table;
	protected BuildDataExtractorTracker tracker;
	protected Closer closer;
	
	@Before
	public void setUp() {
		table = new BuildDataTable();
		tracker = Mockito.mock(BuildDataExtractorTracker.class);
		closer = Closer.create();
	}
	
	@After
	public void tearDown() throws IOException {
		closer.close();
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
