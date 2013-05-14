package org.pescuma.buildhealth.extractor.coverage;

import static com.google.common.base.Strings.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.extractor.BaseXMLExtractor;
import org.pescuma.buildhealth.extractor.PseudoFiles;

// http://emma.sourceforge.net/
public class EmmaExtractor extends BaseXMLExtractor {
	
	// TODO private static final Logger log = LoggerFactory.getLogger(EmmaExtractor.class);
	
	private static final Pattern VALUE_PATTERN = Pattern.compile("(\\d+)% +\\((\\d+(?:\\.\\d+)?)/(\\d+)\\)");
	
	public EmmaExtractor(PseudoFiles files) {
		super(files);
	}
	
	private static class NodeInfo {
		final String emma;
		final String type;
		final boolean skipForPlace;
		
		public NodeInfo(String emma, String type, boolean skipForPlace) {
			this.emma = emma;
			this.type = type;
			this.skipForPlace = skipForPlace;
		}
	}
	
	@Override
	protected void extractDocument(String filename, Document doc, BuildData data) {
		checkRoot(doc, "report", filename);
		
		addAll(data, doc.getRootElement().getChild("data").getChild("all"));
	}
	
	private static void addAll(BuildData data, Element all) {
		addCoverage(data, all, "all", null);
		
		NodeInfo[] nodes = new NodeInfo[] { new NodeInfo("package", "package", false), //
				new NodeInfo("srcfile", "sourceFile", true), //
				new NodeInfo("class", "class", false), //
				new NodeInfo("method", "method", false), //
		};
		
		List<String> place = new ArrayList<String>();
		
		addChildren(data, all, nodes, 0, place);
	}
	
	private static void addChildren(BuildData data, Element el, NodeInfo[] nodes, int i, List<String> place) {
		if (i >= nodes.length)
			return;
		
		for (Element child : el.getChildren(nodes[i].emma))
			add(data, child, nodes, i, place);
	}
	
	private static void add(BuildData data, Element el, NodeInfo[] nodes, int i, List<String> place) {
		NodeInfo node = nodes[i];
		
		String name = el.getAttributeValue("name");
		if (isNullOrEmpty(name)) {
			// TODO log.info("Ignoring " + node.emma + " because of missing name");
			return;
		}
		
		place.add(name);
		
		addCoverage(data, el, node.type, place);
		
		if (node.skipForPlace)
			place.remove(place.size() - 1);
		
		addChildren(data, el, nodes, i + 1, place);
		
		if (!node.skipForPlace)
			place.remove(place.size() - 1);
	}
	
	private static void addCoverage(BuildData data, Element el, String placeType, List<String> place) {
		for (Element coverage : el.getChildren("coverage")) {
			String type = emmaTypeToCoverageType(coverage.getAttributeValue("type"));
			if (type == null) {
				// TODO log.info("Ignoring coverage of " + placeType + " because of unknown type: " + type);
				continue;
			}
			
			String value = coverage.getAttributeValue("value");
			Matcher m = VALUE_PATTERN.matcher(value);
			if (!m.matches()) {
				// TODO log.info("Ignoring coverage of " + placeType + " because of unknown value: " + value);
				continue;
			}
			
			double covered = Double.parseDouble(m.group(2));
			double total = Double.parseDouble(m.group(3));
			
			List<String> infos = new ArrayList<String>();
			infos.add("Coverage");
			infos.add("Java");
			infos.add("Emma");
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
	
	private static String emmaTypeToCoverageType(String type) {
		if ("class, %".equals(type))
			return "class";
		else if ("method, %".equals(type))
			return "method";
		else if ("block, %".equals(type))
			return "block";
		else if ("line, %".equals(type))
			return "line";
		else
			return null;
	}
	
}
