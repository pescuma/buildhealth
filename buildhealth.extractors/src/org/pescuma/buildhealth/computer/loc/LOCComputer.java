package org.pescuma.buildhealth.computer.loc;

import static java.lang.Math.*;
import static org.apache.commons.io.IOUtils.*;
import static org.pescuma.buildhealth.extractor.utils.FilenameToLanguage.*;
import static org.pescuma.buildhealth.utils.FileHelper.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.Validate;
import org.pescuma.buildhealth.computer.BuildDataComputer;
import org.pescuma.buildhealth.computer.BuildDataComputerException;
import org.pescuma.buildhealth.computer.BuildDataComputerTracker;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.loc.CLOCExtractor;
import org.pescuma.buildhealth.extractor.utils.MapReduceExecutor;

public class LOCComputer implements BuildDataComputer {
	
	private final PseudoFiles files;
	
	public LOCComputer(PseudoFiles files) {
		this.files = files;
	}
	
	@Override
	public BuildDataExtractor compute(final File folder, final BuildDataComputerTracker tracker) {
		Validate.isTrue(!files.isStream());
		
		File clocTmp = null;
		try {
			
			final File cloc = extractCLOCToTmp();
			clocTmp = cloc;
			
			List<String> allFiles = findFilesToProcess();
			
			Collection<File> clocFiles = MapReduceExecutor.submit(allFiles, 500,
					new MapReduceExecutor.Func<String, File>() {
						@Override
						public File process(Collection<String> files) throws Exception {
							boolean success = false;
							File out = null;
							File fileList = null;
							try {
								
								fileList = File.createTempFile("cloc.flielist.", ".txt");
								FileUtils.writeLines(fileList, files);
								
								Random random = new Random();
								do {
									out = new File(folder, "cloc-" + abs(random.nextInt()) + ".csv");
								} while (out.exists());
								
								if (cloc.getName().endsWith(".pl"))
									exec("perl", toPath(cloc), "--by-file", "--csv", "--skip-uniqueness",
											"--list-file=" + toPath(fileList), "--out=" + toPath(out),
											"--progress-rate=0");
								else
									exec(toPath(cloc), "--by-file", "--csv", "--skip-uniqueness", "--list-file="
											+ toPath(fileList), "--out=" + toPath(out), "--progress-rate=0");
								
								tracker.onFileOutputCreated(out);
								success = true;
								
								return out;
								
							} catch (IOException e) {
								tracker.onErrorProcessingFile(out, e);
								
								return null;
								
							} finally {
								deleteFile(fileList);
								
								if (!success)
									deleteFile(out);
							}
						}
					});
			
			return new CLOCExtractor(new PseudoFiles(clocFiles));
			
		} catch (IOException e) {
			throw new BuildDataComputerException(e);
		} catch (InterruptedException e) {
			throw new BuildDataComputerException(e);
		} finally {
			deleteFile(clocTmp);
		}
	}
	
	private String toPath(File file) {
		return getCanonicalFile(file).getPath();
	}
	
	private void exec(String... args) throws IOException, InterruptedException {
		Process process = Runtime.getRuntime().exec(args);
		
		int result = process.waitFor();
		if (result != 0)
			throw new IOException("Process returned: " + result);
	}
	
	private List<String> findFilesToProcess() {
		List<String> result = new ArrayList<String>();
		
		for (File file : files.getFilesByExtension()) {
			if (!isKnownFileType(file.getPath(), false))
				continue;
			
			result.add(toPath(file));
		}
		
		return result;
	}
	
	private File extractCLOCToTmp() throws IOException {
		File result = null;
		ZipInputStream in = null;
		FileOutputStream out = null;
		boolean success = false;
		try {
			
			String type = (SystemUtils.IS_OS_WINDOWS ? ".exe" : ".pl");
			
			result = File.createTempFile("cloc.", type);
			
			in = new ZipInputStream(load("cloc.zip"));
			if (!findEntryByExtension(in, type))
				throw new IOException("No files inside zip");
			
			out = new FileOutputStream(result);
			
			copy(in, out);
			
			success = true;
			return result;
			
		} finally {
			closeQuietly(in);
			closeQuietly(out);
			
			if (!success)
				deleteFile(result);
		}
	}
	
	private boolean findEntryByExtension(ZipInputStream zip, String extension) throws IOException {
		ZipEntry entry;
		do {
			entry = zip.getNextEntry();
			if (entry != null && entry.getName().endsWith(extension))
				return true;
		} while (entry != null);
		
		return false;
	}
	
	protected InputStream load(String filename) throws IOException {
		Class<?> cls = getClass();
		
		InputStream result = cls.getClassLoader().getResourceAsStream(
				cls.getPackage().getName().replace('.', '/') + "/" + filename);
		if (result == null)
			throw new IOException("Could not read " + filename);
		
		return result;
	}
	
}
