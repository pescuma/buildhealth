package org.pescuma.buildhealth.analyser;

import static com.google.common.base.Strings.*;
import static java.lang.Math.*;

public class NumbersFormater {
	
	public static String formatBytes(double total) {
		// http://en.wikipedia.org/wiki/Kilobyte
		final String[] units = new String[] { "Ki", "Mi", "Gi", "Ti", "Pi", "Ei", "Zi", "Yi" };
		final int scale = 1024;
		
		return format(total, "B", units, scale);
	}
	
	public static String format1000(double total) {
		return format1000(total, "");
	}
	
	public static String format1000(double total, String baseUnit) {
		// http://en.wikipedia.org/wiki/Kilobyte
		final String[] units = new String[] { "k", "M", "G", "T", "P", "E", "Z", "Yi" };
		final int scale = 1000;
		
		return format(total, baseUnit, units, scale);
	}
	
	private static String format(double total, String baseUnit, final String[] units, final int scale) {
		String unit = "";
		for (int i = 0; i < units.length && total >= scale; i++) {
			total /= scale;
			unit = units[i];
		}
		
		int decimals = detectDecimals(total);
		if (!unit.isEmpty())
			decimals = min(decimals, 1);
		
		return String.format("%." + decimals + "f%s%s%s", total, isNullOrEmpty(baseUnit) ? "" : " ", unit, baseUnit);
	}
	
	private static int detectDecimals(double total) {
		int tmp = ((int) round(total * 100)) % 100;
		if (tmp == 0)
			return 0;
		else if (tmp % 10 == 0)
			return 1;
		else
			return 2;
	}
}
