package org.pescuma.buildhealth.computer.loc;

import static org.apache.commons.io.IOUtils.*;
import static org.pescuma.buildhealth.utils.FileHelper.*;
import static org.pescuma.programminglanguagedetector.FilenameToLanguage.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.pescuma.buildhealth.computer.BuildDataComputer;
import org.pescuma.buildhealth.computer.BuildDataComputerException;
import org.pescuma.buildhealth.computer.BuildDataComputerTracker;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.loc.CLOCExtractor;
import org.pescuma.buildhealth.extractor.utils.SimpleExecutor;
import org.pescuma.buildhealth.utils.CSV;
import org.pescuma.buildhealth.utils.FileHelper;
import org.pescuma.programminglanguagedetector.SimpleFileParser;
import org.pescuma.programminglanguagedetector.SimpleFileParser.LineCount;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.io.Closer;

public class LOCComputer implements BuildDataComputer {
	
	private final PseudoFiles files;
	
	public LOCComputer(PseudoFiles files) {
		this.files = files;
	}
	
	private static class Line {
		File file;
		String language;
		int empty;
		int comment;
		int code;
		
		public Line(File file, String language) {
			super();
			this.file = file;
			this.language = language;
		}
	}
	
	@Override
	public BuildDataExtractor compute(final File folder, final BuildDataComputerTracker tracker) {
		Validate.isTrue(!files.isStream());
		
		List<Line> allFiles = findFilesToProcess();
		
		SimpleExecutor exec = new SimpleExecutor();
		
		for (final Line file : allFiles) {
			exec.submit(new Runnable() {
				@Override
				public void run() {
					try {
						
						LineCount lines = SimpleFileParser.countLines(file.file);
						file.code = lines.codeLines;
						file.comment = lines.commentLines;
						file.empty = lines.emptyLines;
						
					} catch (IOException e) {
						tracker.onErrorProcessingFile(file.file, e);
					}
				}
			});
		}
		
		try {
			exec.awaitTermination();
		} catch (InterruptedException e) {
			throw new BuildDataComputerException(e);
		}
		
		boolean success = false;
		Closer closer = Closer.create();
		File out = null;
		try {
			
			out = FileHelper.createUniquiFileName(folder, "cloc-", ".csv");
			
			CSVWriter csv = closer.register(CSV.newWriter(closer.register(new FileWriter(out))));
			
			csv.writeNext(new String[] { "language", "filename", "blank", "comment", "code" });
			
			for (Line file : allFiles)
				csv.writeNext(new String[] { file.language, file.file.getAbsolutePath(), Integer.toString(file.empty),
						Integer.toString(file.comment), Integer.toString(file.code) });
			
			success = true;
			tracker.onFileOutputCreated(out);
			
		} catch (IOException e) {
			tracker.onErrorProcessingFile(out, e);
			
			return null;
			
		} finally {
			if (!success)
				deleteFile(out);
			
			closeQuietly(closer);
		}
		
		return new CLOCExtractor(new PseudoFiles(out));
	}
	
	private List<Line> findFilesToProcess() {
		List<Line> result = new ArrayList<Line>();
		
		for (File file : files.getFilesByExtension()) {
			String language = detectLanguage(file);
			if (language == null)
				continue;
			
			result.add(new Line(file, language));
		}
		
		return result;
	}
	
}
