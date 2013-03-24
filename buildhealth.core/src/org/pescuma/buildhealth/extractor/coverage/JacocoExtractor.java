package org.pescuma.buildhealth.extractor.coverage;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Strings.*;
import static org.apache.commons.io.FilenameUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildDataExtractorTracker;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;
import org.pescuma.buildhealth.extractor.BuildDataExtractorException;
import org.pescuma.buildhealth.extractor.JDomUtil;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class JacocoExtractor implements BuildDataExtractor {
	
	private final PseudoFiles files;
	
	public JacocoExtractor(PseudoFiles files) {
		if (files == null)
			throw new IllegalArgumentException();
		
		this.files = files;
	}
	
	@Override
	public void extractTo(BuildData data, BuildDataExtractorTracker tracker) {
		try {
			
			if (files.isStream()) {
				extractStream(null, files.getStream(), data);
				tracker.streamProcessed();
				
			} else {
				for (File file : files.getFiles("xml")) {
					extractFile(file, data);
					tracker.fileProcessed(file);
				}
			}
			
		} catch (JDOMException e) {
			throw new BuildDataExtractorException(e);
		} catch (IOException e) {
			throw new BuildDataExtractorException(e);
		}
	}
	
	public static void extractFile(File file, BuildData data) throws JDOMException, IOException {
		Document doc = JDomUtil.parse(file);
		extractDocument(getBaseName(file.getName()), doc, data);
	}
	
	public static void extractStream(String filename, InputStream input, BuildData data) throws JDOMException,
			IOException {
		Document doc = JDomUtil.parse(input);
		extractDocument(filename, doc, data);
	}
	
	private static void extractDocument(String filename, Document doc, BuildData data) {
		Element report = doc.getRootElement();
		if (!report.getName().equals("report"))
			throw new BuildDataExtractorException("Invalid file format: top node must be a report");
		
		List<String> place = new ArrayList<String>();
		
		extract(data, report, "all", place);
	}
	
	private static void extract(BuildData data, Element el, String placeType, List<String> place) {
		String name = el.getAttributeValue("name");
		
		String desc = el.getAttributeValue("desc");
		if (!isNullOrEmpty(desc))
			name = firstNonNull(name, "") + " " + desc;
		
		if (!isNullOrEmpty(name))
			place.add(name);
		
		addCoverage(data, el, placeType, place);
		
		for (Element pkg : el.getChildren("package"))
			extract(data, pkg, "package", place);
		
		for (Element group : el.getChildren("group"))
			extract(data, group, "group", place);
		
		for (Element cls : el.getChildren("class"))
			extract(data, cls, "class", place);
		
		for (Element method : el.getChildren("method"))
			extract(data, method, "method", place);
		
		for (Element source : el.getChildren("sourcefile"))
			extract(data, source, "sourceFile", place);
		
		if (!isNullOrEmpty(name))
			place.remove(place.size() - 1);
	}
	
	private static void addCoverage(BuildData data, Element el, String placeType, List<String> place) {
		for (Element coverage : el.getChildren("counter")) {
			String type = jacocoTypeToCoverageType(coverage.getAttributeValue("type"));
			if (type == null)
				continue;
			
			double missed = Double.parseDouble(coverage.getAttributeValue("missed"));
			double covered = Double.parseDouble(coverage.getAttributeValue("covered"));
			double total = covered + missed;
			
			List<String> infos = new ArrayList<String>();
			infos.add("Coverage");
			infos.add("java");
			infos.add("jacoco");
			infos.add(type);
			infos.add("covered");
			infos.add(placeType);
			if (place != null)
				infos.addAll(place);
			
			data.add(covered, infos.toArray(new String[infos.size()]));
			
			infos.set(4, "total");
			data.add(total, infos.toArray(new String[infos.size()]));
		}
	}
	
	private static String jacocoTypeToCoverageType(String type) {
		if ("INSTRUCTION".equals(type))
			return "instruction";
		else if ("BRANCH".equals(type))
			return "branch";
		else if ("LINE".equals(type))
			return "line";
		else if ("COMPLEXITY".equals(type))
			return "complexity";
		else if ("METHOD".equals(type))
			return "method";
		else if ("CLASS".equals(type))
			return "class";
		else
			return null;
	}
	
}
