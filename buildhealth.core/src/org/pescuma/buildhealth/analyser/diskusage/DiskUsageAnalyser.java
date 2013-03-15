package org.pescuma.buildhealth.analyser.diskusage;

import static java.util.Arrays.*;

import java.util.Collections;
import java.util.List;

import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Disk usage,tag (optional),folder or file name (optional)
 * </pre>
 * 
 * All values are in bytes
 * 
 * Example:
 * 
 * <pre>
 * 10 | Disk usage
 * 1024 | Disk usage,executable
 * 1024 | Disk usage,executable,/tmp/X
 * </pre>
 */
public class DiskUsageAnalyser implements BuildHealthAnalyser {
	
	@Override
	public List<Report> computeSimpleReport(BuildData data) {
		data = data.filter("Disk usage");
		if (data.isEmpty())
			return Collections.emptyList();
		
		double total = data.sum();
		
		return asList(new Report(BuildStatus.Good, "Disk usage", formatSize(total)));
	}
	
	private String formatSize(double total) {
		// http://en.wikipedia.org/wiki/Kilobyte
		final String[] units = new String[] { "Ki", "Mi", "Gi", "Ti", "Pi", "Ei", "Zi", "Yi" };
		final int scale = 1024;
		
		String unit = null;
		for (int i = 0; i < units.length && total >= 10 * scale; i++) {
			total /= scale;
			unit = units[i];
		}
		
		if (unit == null)
			return String.format("%.0f B", total);
		else
			return String.format("%.1f %sB", total, unit);
	}
}
