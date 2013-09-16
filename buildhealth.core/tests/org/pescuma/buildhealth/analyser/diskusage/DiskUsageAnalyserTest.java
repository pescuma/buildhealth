package org.pescuma.buildhealth.analyser.diskusage;

import static java.util.Arrays.*;
import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.pescuma.buildhealth.analyser.BaseAnalyserTest;
import org.pescuma.buildhealth.core.Report;

public class DiskUsageAnalyserTest extends BaseAnalyserTest {
	
	@Before
	public void setUp() {
		super.setUp(new DiskUsageAnalyser());
	}
	
	private void create(double size, String... tag) {
		List<String> infos = new ArrayList<String>();
		infos.add("Disk usage");
		infos.addAll(asList(tag));
		data.add(size, infos.toArray(new String[infos.size()]));
	}
	
	@Test
	public void testSimpleB() {
		create(10);
		
		assertReport(new Report(Good, "Disk usage", "10 B"), createReport());
	}
	
	@Test
	public void testMultipleB() {
		create(10);
		create(20);
		create(30, "");
		create(40, "asd");
		create(50, "x", "/c/d");
		
		assertReport(new Report(Good, "Disk usage", "150 B"), createReport());
	}
	
	@Test
	public void testSimpleBigB() {
		create(999);
		
		assertReport(new Report(Good, "Disk usage", "999 B"), createReport());
	}
	
	@Test
	public void testSimpleK() {
		create(19.44 * 1024);
		
		assertReport(new Report(Good, "Disk usage", "19.4 KiB"), createReport());
	}
	
	@Test
	public void testSimpleM() {
		create(12 * 1024 * 1024);
		
		assertReport(new Report(Good, "Disk usage", "12 MiB"), createReport());
	}
	
	@Test
	public void testSimpleT() {
		create(123.49 * 1024 * 1024 * 1024 * 1024);
		
		assertReport(new Report(Good, "Disk usage", "123.5 TiB"), createReport());
	}
	
	@Test
	public void testOtherLocaleT() {
		Locale.setDefault(Locale.FRANCE);
		
		create(12.3 * 1024);
		
		assertReport(new Report(Good, "Disk usage", "12,3 KiB"), createReport());
	}
	
	@Test
	public void testFullRerport_LinuxPaths() {
		create(50, "", "/c/d");
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Disk usage", "50 B", //
				new Report(Good, "/", "50 B", //
						new Report(Good, "c/", "50 B", //
								new Report(Good, "d", "50 B") //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFullRerport_WindowsPaths() {
		create(50, "", "C:\\c\\d.exe");
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Disk usage", "50 B", //
				new Report(Good, "C:\\", "50 B", //
						new Report(Good, "c\\", "50 B", //
								new Report(Good, "d.exe", "50 B") //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFullRerport_WithTags() {
		create(40, "y", "/c/e");
		create(50, "x", "/c/d");
		
		prefs.child("diskUsage").set("reportWithTags", true);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Disk usage", "90 B", //
				new Report(Good, "x", "50 B", //
						new Report(Good, "/", "50 B", //
								new Report(Good, "c/", "50 B", //
										new Report(Good, "d", "50 B") //
								) //
						) //
				), //
				new Report(Good, "y", "40 B", //
						new Report(Good, "/", "40 B", //
								new Report(Good, "c/", "40 B", //
										new Report(Good, "e", "40 B") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFullRerport_NoTags() {
		create(40, "y", "/c/e");
		create(50, "x", "/c/d");
		
		prefs.child("diskUsage").set("reportWithTags", false);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Disk usage", "90 B", //
				new Report(Good, "/", "90 B", //
						new Report(Good, "c/", "90 B", //
								new Report(Good, "d", "50 B"), //
								new Report(Good, "e", "40 B") //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFullRerport_WithTagsButNoneSet() {
		create(40, "y", "/c/e");
		create(50, "", "/c/d");
		
		prefs.child("diskUsage").set("reportWithTags", true);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Disk usage", "90 B", //
				new Report(Good, "No tag", "50 B", //
						new Report(Good, "/", "50 B", //
								new Report(Good, "c/", "50 B", //
										new Report(Good, "d", "50 B") //
								) //
						) //
				), //
				new Report(Good, "y", "40 B", //
						new Report(Good, "/", "40 B", //
								new Report(Good, "c/", "40 B", //
										new Report(Good, "e", "40 B") //
								) //
						) //
				) //
				), report);
	}
	
	@Test
	public void testFullRerport_WithTagsButNoneSetInAllLines() {
		create(40, "", "/c/e");
		create(50, "", "/c/d");
		
		prefs.child("diskUsage").set("reportWithTags", true);
		
		Report report = createReport(Full);
		
		assertReport(new Report(Good, "Disk usage", "90 B", //
				new Report(Good, "/", "90 B", //
						new Report(Good, "c/", "90 B", //
								new Report(Good, "d", "50 B"), //
								new Report(Good, "e", "40 B") //
						) //
				) //
				), report);
	}
}
