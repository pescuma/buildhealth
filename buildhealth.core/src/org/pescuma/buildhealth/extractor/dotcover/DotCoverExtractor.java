package org.pescuma.buildhealth.extractor.dotcover;

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

public class DotCoverExtractor implements BuildDataExtractor {
	
	private final PseudoFiles files;
	
	public DotCoverExtractor(PseudoFiles files) {
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
		if (!report.getName().equals("Root"))
			throw new BuildDataExtractorException("Invalid file format: top node must be Root");
		
		List<String> place = new ArrayList<String>();
		
		extract(data, report, "all", place);
	}
	
	private static void extract(BuildData data, Element el, String placeType, List<String> place) {
		String name = el.getAttributeValue("Name");
		if ("Root".equals(el.getName()) && "Root".equals(name))
			name = null;
		
		if (!isNullOrEmpty(name))
			place.add(name);
		
		addCoverage(data, el, placeType, place);
		
		for (Element pkg : el.getChildren("Assembly"))
			extract(data, pkg, "library", place);
		
		for (Element pkg : el.getChildren("Namespace"))
			extract(data, pkg, "package", place);
		
		for (Element cls : el.getChildren("Type"))
			extract(data, cls, "class", place);
		
		for (Element method : el.getChildren("Member"))
			extract(data, method, "method", place);
		
		if (!isNullOrEmpty(name))
			place.remove(name);
	}
	
	private static void addCoverage(BuildData data, Element el, String placeType, List<String> place) {
		double total = Double.parseDouble(el.getAttributeValue("TotalStatements"));
		double covered = Double.parseDouble(el.getAttributeValue("CoveredStatements"));
		
		List<String> infos = new ArrayList<String>();
		infos.add("Coverage");
		infos.add("c#");
		infos.add("dotCover");
		infos.add("line");
		infos.add("covered");
		infos.add(placeType);
		if (place != null)
			infos.addAll(place);
		
		data.add(covered, infos.toArray(new String[infos.size()]));
		
		infos.set(4, "total");
		data.add(total, infos.toArray(new String[infos.size()]));
	}
	
}
