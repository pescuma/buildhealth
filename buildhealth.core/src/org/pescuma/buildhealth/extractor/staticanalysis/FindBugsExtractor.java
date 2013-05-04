package org.pescuma.buildhealth.extractor.staticanalysis;

import static com.google.common.base.Objects.*;
import static org.pescuma.buildhealth.utils.FileHelper.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jsoup.Jsoup;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class FindBugsExtractor extends BaseXMLExtractor {
	
	public FindBugsExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, "BugCollection", filename);
		
		Map<String, String> categoryFullName = findCategories(doc);
		Map<String, String> bugDetails = findBugDetails(doc);
		List<String> sourceDirs = findSourceDirs(doc);
		
		for (Element bug : findElementsXPath(doc, "//BugInstance")) {
			String type = bug.getAttributeValue("type", "");
			String shortMessage = firstNonNull(bug.getChildTextTrim("ShortMessage"), "");
			String longMessage = firstNonNull(bug.getChildTextTrim("LongMessage"), "");
			String details = firstNonNull(bugDetails.get(type), "");
			
			String category = bug.getAttributeValue("category", "");
			if (categoryFullName.containsKey(category))
				category = categoryFullName.get(category);
			
			for (Element sourceline : bug.getChildren("SourceLine")) {
				String path = sourceline.getAttributeValue("sourcepath", "");
				String startLine = sourceline.getAttributeValue("start", "");
				String endLine = sourceline.getAttributeValue("end", "");
				
				path = findRealSource(path, sourceDirs);
				
				String line;
				if (startLine.equals(endLine))
					line = startLine;
				else
					line = startLine + ":0:" + endLine + ":999";
				
				data.add(1, "Static analysis", "Java", "FindBugs", path, line, category, shortMessage, longMessage
						+ "\n" + details);
			}
		}
	}
	
	private String findRealSource(String relativePath, List<String> roots) {
		if (roots.isEmpty())
			return relativePath;
		
		for (String root : roots) {
			File f = new File(root, relativePath);
			if (f.exists())
				return getCanonicalFile(f).getPath();
		}
		
		if (roots.size() == 1)
			return getCanonicalFile(new File(roots.get(0), relativePath)).getPath();
		
		return relativePath;
	}
	
	private Map<String, String> findCategories(Document doc) {
		Map<String, String> result = new HashMap<String, String>();
		
		for (Element cat : findElementsXPath(doc, "//BugCategory")) {
			String name = cat.getAttributeValue("category", "");
			String description = firstNonNull(cat.getChildTextTrim("Description"), "");
			
			if (!name.isEmpty() && !description.isEmpty())
				result.put(name, description);
		}
		
		return result;
	}
	
	private Map<String, String> findBugDetails(Document doc) {
		Map<String, String> result = new HashMap<String, String>();
		
		for (Element cat : findElementsXPath(doc, "//BugPattern")) {
			String name = cat.getAttributeValue("type", "");
			String description = firstNonNull(cat.getChildTextTrim("Details"), "");
			description = Jsoup.parse(description).text();
			
			if (!name.isEmpty() && !description.isEmpty())
				result.put(name, description);
		}
		
		return result;
	}
	
	private List<String> findSourceDirs(Document doc) {
		List<String> result = new ArrayList<String>();
		
		for (Element dir : findElementsXPath(doc, "//Project/SrcDir")) {
			String path = dir.getTextTrim();
			if (!path.isEmpty())
				result.add(path);
		}
		
		return result;
	}
	
}
