package org.pescuma.buildhealth.extractor.staticanalysis;

import static org.pescuma.buildhealth.extractor.utils.FilenameToLanguage.*;
import static org.pescuma.buildhealth.utils.StringHelper.*;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

public class ResharperDupFinderExtractor extends BaseXMLExtractor {
	
	public ResharperDupFinderExtractor(PseudoFiles files) {
		super(files);
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, "DuplicatesReport", filename);
		
		for (Element duplicate : findElementsXPath(doc, "//Duplicate")) {
			
			List<Fragment> fragments = new ArrayList<Fragment>();
			for (Element fs : duplicate.getChildren("Fragment")) {
				String file = fs.getChildTextTrim("FileName");
				Element lines = fs.getChild("LineRange");
				
				if (file == null || lines == null)
					continue;
				
				int startLine = Integer.parseInt(lines.getAttributeValue("Start"));
				int endLine = Integer.parseInt(lines.getAttributeValue("End"));
				
				fragments.add(new Fragment(file, startLine, endLine));
			}
			
			if (fragments.size() < 2)
				continue;
			
			String cost = duplicate.getAttributeValue("Cost");
			
			for (int i = 0; i < fragments.size(); i++) {
				Fragment frag = fragments.get(i);
				
				String language = firstNonEmpty(detectLanguage(frag.filename), "C#");
				data.add(1, "Static analysis", language, "Resharper DupFinder", frag.filename, frag.lineStart + ":"
						+ frag.lineEnd, "Code duplication", getMessageFor(fragments, i), "", "Cost: " + cost);
			}
		}
	}
	
	private String getMessageFor(List<Fragment> fragments, int index) {
		StringBuilder result = new StringBuilder();
		
		Fragment frag = fragments.get(index);
		result.append("These ").append(frag.lineEnd - frag.lineStart + 1)
				.append(" lines of code are duplicated with:\n");
		
		for (int i = 0; i < fragments.size(); i++) {
			if (i == index)
				continue;
			
			Fragment other = fragments.get(i);
			result.append("  ").append(other.filename).append(" lines ").append(other.lineStart).append(" to ")
					.append(other.lineEnd);
		}
		
		return result.toString();
	}
	
	private static class Fragment {
		final String filename;
		final int lineStart;
		final int lineEnd;
		
		Fragment(String filename, int lineStart, int lineEnd) {
			this.filename = filename;
			this.lineStart = lineStart;
			this.lineEnd = lineEnd;
		}
		
	}
	
}
