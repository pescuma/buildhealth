package org.pescuma.buildhealth.analyser.diskusage;

import static org.junit.Assert.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BaseAnalyserTest;
import org.pescuma.buildhealth.core.Report;

public class DiskUsageAnalyserTest extends BaseAnalyserTest {
	
	private DiskUsageAnalyser analyser;
	
	@Before
	public void setUp() {
		analyser = new DiskUsageAnalyser();
		
		super.setUp(analyser);
	}
	
	private void create(double size, String... tag) {
		List<String> infos = new ArrayList<String>();
		infos.add("Disk usage");
		infos.addAll(Arrays.asList(tag));
		data.add(size, infos.toArray(new String[infos.size()]));
	}
	
	@Test
	public void testSimpleB() {
		create(10);
		
		assertEquals(new Report(Good, "Disk usage", "10 B"), createReport());
	}
	
	@Test
	public void testMultipleB() {
		create(10);
		create(20);
		create(30, "");
		create(40, "asd");
		create(50, "x", "/c/d");
		
		assertEquals(new Report(Good, "Disk usage", "150 B"), createReport());
	}
	
	@Test
	public void testSimpleBigB() {
		create(999);
		
		assertEquals(new Report(Good, "Disk usage", "999 B"), createReport());
	}
	
	@Test
	public void testSimpleK() {
		create(19.44 * 1024);
		
		assertEquals(new Report(Good, "Disk usage", "19.4 KiB"), createReport());
	}
	
	@Test
	public void testSimpleM() {
		create(12 * 1024 * 1024);
		
		assertEquals(new Report(Good, "Disk usage", "12 MiB"), createReport());
	}
	
	@Test
	public void testSimpleT() {
		create(123.49 * 1024 * 1024 * 1024 * 1024);
		
		assertEquals(new Report(Good, "Disk usage", "123.5 TiB"), createReport());
	}
	
	@Test
	public void testOtherLocaleT() {
		Locale.setDefault(Locale.FRANCE);
		
		create(12.3 * 1024);
		
		assertEquals(new Report(Good, "Disk usage", "12,3 KiB"), createReport());
	}
}
