package org.pescuma.buildhealth.utils;

import static java.lang.Math.*;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class FileHelper {
	
	public static File getCanonicalFile(File file) {
		if (file == null)
			return null;
		
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			return file.getAbsoluteFile();
		}
	}
	
	public static String getCanonicalPath(File file) {
		if (file == null)
			return null;
		
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}
	
	public static void deleteFile(File file) {
		if (file == null)
			return;
		
		if (!FileUtils.deleteQuietly(file))
			file.deleteOnExit();
	}
	
	public static File createUniquiFileName(File folder, String prefix, String suffix) {
		File out;
		
		Random random = new Random();
		do {
			out = new File(folder, prefix + abs(random.nextInt()) + suffix);
		} while (out.exists());
		
		return out;
	}
	
}
