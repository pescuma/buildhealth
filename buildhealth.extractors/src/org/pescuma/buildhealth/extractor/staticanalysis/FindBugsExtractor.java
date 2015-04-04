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
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.utils.Location;
import org.pescuma.datatable.DataTable;

// http://findbugs.sourceforge.net/
public class FindBugsExtractor extends BaseXMLExtractor {
	
	public FindBugsExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String path, Document doc, DataTable data) {
		checkRoot(doc, path, "BugCollection");
		
		Map<String, String> categoryFullName = findCategories(doc);
		Map<String, String> bugDetails = findBugDetails(doc);
		List<String> sourceDirs = findSourceDirs(doc);
		
		for (Element bug : findElementsXPath(doc, "//BugInstance")) {
			String type = bug.getAttributeValue("type", "");
			String shortMessage = firstNonNull(bug.getChildTextTrim("ShortMessage"), "");
			String longMessage = firstNonNull(bug.getChildTextTrim("LongMessage"), "");
			String details = firstNonNull(bugDetails.get(type), "");
			String rank = toFindBugsRank(bug.getAttributeValue("rank", ""));
			String confidence = toFindBugsConfidence(bug.getAttributeValue("priority", ""));
			
			String category = bug.getAttributeValue("category", "");
			if (categoryFullName.containsKey(category))
				category = categoryFullName.get(category);
			
			Element sourceline = findSourceLine(bug);
			String sourcepath = sourceline.getAttributeValue("sourcepath", "");
			String startLine = sourceline.getAttributeValue("start", "");
			String endLine = sourceline.getAttributeValue("end", "");
			
			sourcepath = findRealSource(sourcepath, sourceDirs);
			
			Location loc = Location.create(sourcepath, startLine, null, endLine, null);
			
			StringBuilder desc = new StringBuilder();
			desc.append(longMessage);
			for (Element message : findElementsXPath(bug, "*/Message"))
				desc.append("\n").append(message.getTextTrim());
			if (!rank.isEmpty())
				desc.append("\nRank: ").append(rank);
			if (!confidence.isEmpty())
				desc.append("\nConfidence: ").append(confidence);
			desc.append("\n\n").append(details);
			
			data.add(1, "Static analysis", "Java", "FindBugs", Location.toFormatedString(loc), category, shortMessage,
					toBuildHealthSeverity(rank), desc.toString());
		}
	}
	
	private String toFindBugsRank(String rank) {
		try {
			
			int r = Integer.parseInt(rank);
			if (r <= 4)
				return "scariest";
			if (r <= 9)
				return "scary";
			if (r <= 14)
				return "troubling";
			if (r <= 20)
				return "concern";
			return rank;
			
		} catch (NumberFormatException e) {
			return rank;
		}
	}
	
	private String toBuildHealthSeverity(String rank) {
		if ("scariest".equals(rank))
			return "High";
		if ("scary".equals(rank))
			return "High";
		if ("troubling".equals(rank))
			return "Medium";
		if ("concern".equals(rank))
			return "Low";
		return rank;
	}
	
	private String toFindBugsConfidence(String confidence) {
		try {
			
			int p = Integer.parseInt(confidence);
			if (p >= 1)
				return "high";
			if (p == 2)
				return "normal";
			if (p == 3)
				return "low";
			if (p == 4)
				return "experimental";
			return "ignore";
			
		} catch (NumberFormatException e) {
			return confidence;
		}
	}
	
	private Element findSourceLine(Element bug) {
		List<Element> sls = bug.getChildren("SourceLine");
		if (sls.size() == 1)
			return sls.get(0);
		
		List<Element> tmp;
		
		tmp = bug.getChildren("Field");
		if (tmp.size() == 1)
			return tmp.get(0).getChild("SourceLine");
		
		// What to do here?
		return sls.get(0);
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
