package org.pescuma.buildhealth.extractor.unittest.xunit;

import static org.apache.commons.io.FilenameUtils.*;
import static org.apache.commons.io.IOUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildDataExtractorTracker;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.BuildDataExtractorException;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.unittest.JUnitExtractor;

import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricException;
import com.thalesgroup.dtkit.util.converter.ConversionException;
import com.thalesgroup.dtkit.util.validator.ValidationError;
import com.thalesgroup.dtkit.util.validator.ValidationException;

abstract class XUnitExtractor implements BuildDataExtractor {
	
	private final PseudoFiles files;
	
	protected XUnitExtractor(PseudoFiles files) {
		if (files == null)
			throw new IllegalArgumentException();
		
		this.files = files;
	}
	
	@Override
	public void extractTo(BuildData data, BuildDataExtractorTracker tracker) {
		if (files.isStream()) {
			File tmp = createTmpFile();
			try {
				
				FileWriter writer = null;
				try {
					writer = new FileWriter(tmp);
					IOUtils.copy(files.getStream(), writer);
				} catch (IOException e) {
					throw new BuildDataExtractorException(e);
				} finally {
					IOUtils.closeQuietly(writer);
				}
				
				extractFile(tmp, null, data);
				tracker.streamProcessed();
				
			} finally {
				delete(tmp);
			}
			
		} else {
			for (File f : files.getFiles("xml")) {
				extractFile(f, getBaseName(f.getName()), data);
				tracker.fileProcessed(f);
			}
		}
	}
	
	private File createTmpFile() {
		try {
			return File.createTempFile("XUnit-", ".xml");
		} catch (IOException e) {
			throw new BuildDataExtractorException("Failed to create tmp file", e);
		}
	}
	
	private void extractFile(File inputFile, String filename, BuildData data) {
		InputMetric metric = getInputMetric();
		
		File junitFile = convertFile(metric, inputFile);
		
		InputStream stream = null;
		try {
			stream = new FileInputStream(junitFile);
			
			new JUnitExtractor(new PseudoFiles(stream, filename), getLanguage(), metric.getToolName()).extractTo(data,
					new BuildDataExtractorTracker() {
						@Override
						public void streamProcessed() {
							// Ignore
						}
						
						@Override
						public void fileProcessed(File file) {
							// Ignore
						}
					});
			
		} catch (IOException e) {
			throw new BuildDataExtractorException(e);
		} finally {
			closeQuietly(stream);
			delete(junitFile);
		}
	}
	
	protected abstract InputMetric getInputMetric();
	
	protected abstract String getLanguage();
	
	private File convertFile(InputMetric metric, File file) {
		File junitFile = null;
		boolean succeeded = false;
		try {
			
			if (!metric.validateInputFile(file)) {
				StringBuilder out = new StringBuilder();
				out.append("Error loading file " + file.getPath() + ":\n");
				appendErrors(out, metric.getInputValidationErrors());
				throw new BuildDataExtractorException(out.toString());
			}
			
			junitFile = createTmpFile();
			
			metric.convert(file, junitFile);
			
			if (!metric.validateOutputFile(junitFile)) {
				StringBuilder out = new StringBuilder();
				out.append("The converted file for '").append(file.getPath());
				out.append("' doesn't match the JUnit format:\n");
				appendErrors(out, metric.getInputValidationErrors());
				throw new BuildDataExtractorException(out.toString());
			}
			
			succeeded = true;
			return junitFile;
			
		} catch (InputMetricException e) {
			throw new BuildDataExtractorException(e);
		} catch (ValidationException e) {
			throw new BuildDataExtractorException(e);
		} catch (ConversionException e) {
			throw new BuildDataExtractorException(e);
		} finally {
			if (!succeeded)
				delete(junitFile);
		}
	}
	
	private void delete(File file) {
		if (file == null)
			return;
		
		if (!FileUtils.deleteQuietly(file))
			file.deleteOnExit();
	}
	
	private void appendErrors(StringBuilder out, List<ValidationError> errors) {
		for (ValidationError error : errors)
			out.append(error.toString()).append("\n");
	}
	
}
