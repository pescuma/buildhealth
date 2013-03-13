package org.pescuma.buildhealth.extractor.xunit;

import static org.apache.commons.io.FilenameUtils.*;
import static org.apache.commons.io.IOUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom2.JDOMException;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.BuildDataExtractorException;
import org.pescuma.buildhealth.extractor.junit.JUnitExtractor;

import com.thalesgroup.dtkit.metrics.model.InputMetric;
import com.thalesgroup.dtkit.metrics.model.InputMetricException;
import com.thalesgroup.dtkit.util.converter.ConversionException;
import com.thalesgroup.dtkit.util.validator.ValidationError;
import com.thalesgroup.dtkit.util.validator.ValidationException;

abstract class XUnitExtractor implements BuildDataExtractor {
	
	private final File fileOrFolder;
	
	protected XUnitExtractor(File fileOrFolder) {
		if (fileOrFolder == null)
			throw new IllegalArgumentException();
		
		this.fileOrFolder = fileOrFolder;
	}
	
	@Override
	public void extractTo(BuildData data) {
		if (fileOrFolder.isDirectory()) {
			for (File file : FileUtils.listFiles(fileOrFolder, new String[] { "xml" }, true))
				extractFile(file, data);
			
		} else {
			extractFile(fileOrFolder, data);
		}
	}
	
	private void extractFile(File inputFile, BuildData data) {
		File junitFile = convertFile(inputFile);
		
		InputStream stream = null;
		try {
			stream = new FileInputStream(junitFile);
			
			JUnitExtractor.extractStream(getBaseName(inputFile.getName()), stream, data);
			
		} catch (JDOMException e) {
			throw new BuildDataExtractorException(e);
		} catch (IOException e) {
			throw new BuildDataExtractorException(e);
		} finally {
			closeQuietly(stream);
			delete(junitFile);
		}
	}
	
	protected abstract InputMetric getInputMetric();
	
	private File convertFile(File file) {
		File junitFile = null;
		boolean succeeded = false;
		try {
			InputMetric metric = getInputMetric();
			
			if (!metric.validateInputFile(file)) {
				StringBuilder out = new StringBuilder();
				out.append("Error loading file " + file.getPath() + ":\n");
				appendErrors(out, metric.getInputValidationErrors());
				System.out.println(out.toString());
				return null;
			}
			
			junitFile = File.createTempFile(getBaseName(file.getName()) + "-TEST-", ".xml");
			
			metric.convert(file, junitFile);
			
			if (!metric.validateOutputFile(junitFile)) {
				StringBuilder out = new StringBuilder();
				out.append("The converted file for '").append(file.getPath());
				out.append("' doesn't match the JUnit format:\n");
				appendErrors(out, metric.getInputValidationErrors());
				System.out.println(out.toString());
				return null;
			}
			
			succeeded = true;
			return junitFile;
			
		} catch (InputMetricException e) {
			throw new BuildDataExtractorException(e);
		} catch (ValidationException e) {
			throw new BuildDataExtractorException(e);
		} catch (ConversionException e) {
			throw new BuildDataExtractorException(e);
		} catch (IOException e) {
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
