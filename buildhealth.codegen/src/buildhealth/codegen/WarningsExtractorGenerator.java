package buildhealth.codegen;

import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.warnings.parser.AbstractWarningsParser;
import hudson.plugins.warnings.parser.ParsingCanceledException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.stringtemplate.v4.ST;

import com.bethecoder.ascii_table.ASCIITable;
import com.bethecoder.ascii_table.ASCIITableHeader;
import com.google.common.io.PatternFilenameFilter;

public class WarningsExtractorGenerator {
	
	public void generate() throws IOException {
		List<Class<? extends AbstractWarningsParser>> subs = listParsers();
		
		List<WarningsParser> parsers = testAndWrapParsers(subs);
		
		dump(parsers);
		
		System.out.println("Creating extractors...");
		createExtractors(parsers);
		
		System.out.println("Creating ant tasks...");
		createAntTasks(parsers);
		fillAntlib(parsers);
		
		System.out.println("Creating cli...");
		createCli(parsers);
		fillCli(parsers);
	}
	
	private List<Class<? extends AbstractWarningsParser>> listParsers() {
		Reflections reflections = new Reflections("hudson");
		
		List<Class<? extends AbstractWarningsParser>> result = new ArrayList<Class<? extends AbstractWarningsParser>>(
				reflections.getSubTypesOf(AbstractWarningsParser.class));
		
		Collections.sort(result, new Comparator<Class<? extends AbstractWarningsParser>>() {
			@Override
			public int compare(Class<? extends AbstractWarningsParser> o1, Class<? extends AbstractWarningsParser> o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		
		return result;
	}
	
	private List<WarningsParser> testAndWrapParsers(List<Class<? extends AbstractWarningsParser>> subs)
			throws IOException, ParsingCanceledException, FileNotFoundException {
		List<WarningsParser> result = new ArrayList<WarningsParser>();
		
		for (Class<? extends AbstractWarningsParser> sub : subs) {
			System.out.println("Processing " + sub + "...");
			
			if (ignored.contains(sub.getName())) {
				System.out.println("    Ignored");
				continue;
			}
			
			AbstractWarningsParser parser = null;
			try {
				parser = sub.newInstance();
			} catch (InstantiationException e) {
				System.out.println("    Ignoring: " + e);
				continue;
			} catch (IllegalAccessException e) {
				System.out.println("    Ignoring: " + e);
				continue;
			}
			
			run(parser);
			
			result.add(new WarningsParser(parser));
		}
		
		Collections.sort(result, new Comparator<WarningsParser>() {
			@Override
			public int compare(WarningsParser o1, WarningsParser o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		
		return result;
	}
	
	private void createExtractors(List<WarningsParser> parsers) throws IOException {
		generateFromTemplate(parsers,
				"../buildhealth.extractors/src/org/pescuma/buildhealth/extractor/staticanalysis/console/",
				"templates/WarningsExtractor.st", "ConsoleExtractor");
	}
	
	private void createAntTasks(List<WarningsParser> parsers) throws IOException {
		generateFromTemplate(parsers,
				"../buildhealth.ant/src/org/pescuma/buildhealth/ant/tasks/add/staticanalysis/console/",
				"templates/WarningsExtractorAntTask.st", "ConsoleExtractorAntTask");
	}
	
	private void fillAntlib(List<WarningsParser> parsers) throws IOException {
		ST st = new ST(FileUtils.readFileToString(new File("templates/antlib.st")), '$', '$');
		for (WarningsParser parser : parsers) {
			st.addAggr("items.{name, class}", parser.getName() + "-console",
					"org.pescuma.buildhealth.ant.tasks.add.staticanalysis.console." + parser.getBaseClassName()
							+ "ConsoleExtractorAntTask");
		}
		String toAdd = st.render();
		
		String filename = "../buildhealth.ant/src/org/pescuma/buildhealth/ant/antlib.xml";
		String start = "<!-- Start of auto generated entries -->";
		String end = "	<!-- End of auto generated entries -->";
		
		insertInto(filename, start, end, toAdd);
	}
	
	private void createCli(List<WarningsParser> parsers) throws IOException {
		generateFromTemplate(parsers,
				"../buildhealth.cli/src/org/pescuma/buildhealth/cli/commands/add/staticanalysis/console/",
				"templates/WarningsExtractorCommand.st", "ConsoleExtractorCommand");
	}
	
	private void fillCli(List<WarningsParser> parsers) throws IOException {
		ST st = new ST(FileUtils.readFileToString(new File("templates/BuildHealthCli.st")), '$', '$');
		for (WarningsParser parser : parsers) {
			st.addAggr("items.{className}",
					"org.pescuma.buildhealth.cli.commands.add.staticanalysis.console." + parser.getBaseClassName()
							+ "ConsoleExtractorCommand");
		}
		String toAdd = st.render();
		
		String filename = "../buildhealth.cli/src/org/pescuma/buildhealth/cli/BuildHealthCli.java";
		String start = "// Start of auto generated entries";
		String end = "				// End of auto generated entries";
		
		insertInto(filename, start, end, toAdd);
	}
	
	private void insertInto(String filename, String start, String end, String toAdd) throws IOException {
		File antlibFile = new File(filename);
		
		String contents = FileUtils.readFileToString(antlibFile);
		int startPos = contents.indexOf(start);
		int endPos = contents.indexOf(end);
		contents = contents.substring(0, startPos) + start + "\n" + toAdd + "\n" + contents.substring(endPos);
		
		FileUtils.write(antlibFile, contents);
	}
	
	private void generateFromTemplate(List<WarningsParser> parsers, String dirName, String templateName,
			String classSuffix) throws IOException {
		File dir = new File(dirName);
		
		deleteOldExtractors(dir, classSuffix);
		
		for (WarningsParser parser : parsers) {
			ST st = new ST(FileUtils.readFileToString(new File(templateName)), '$', '$');
			st.add("parser", parser);
			st.add("class", parser.getBaseClassName() + classSuffix);
			String contents = st.render();
			
			FileUtils.write(new File(dir, parser.getBaseClassName() + classSuffix + ".java"), contents);
		}
	}
	
	private void deleteOldExtractors(File dir, String classSuffix) {
		File[] files = dir.listFiles(new PatternFilenameFilter(".*" + classSuffix + ".java"));
		if (files != null)
			for (File e : files)
				e.delete();
		dir.mkdirs();
	}
	
	private static void run(AbstractWarningsParser parser) throws IOException, ParsingCanceledException,
			FileNotFoundException {
		Collection<FileAnnotation> anotations = parser.parse(new FileReader("templates/WarningsExtractor.st"));
		
		for (FileAnnotation ann : anotations) {
			System.out.println(" - " + ann);
		}
	}
	
	private void dump(List<WarningsParser> parsers) {
		String[][] data = new String[parsers.size()][7];
		
		for (int i = 0; i < data.length; i++) {
			WarningsParser parser = parsers.get(i);
			data[i][0] = parser.getClassName() + " -> " + parser.getBaseClassName();
			data[i][1] = parser.getName();
			data[i][2] = WarningsParser.createDescriptionFromParser(parser.parser);
			data[i][3] = WarningsParser.createDescriptionFromLink(parser.parser);
			data[i][4] = parser.parser.getParserName().toString();
			data[i][5] = parser.parser.getLinkName().toString();
			data[i][6] = parser.parser.getTrendName().toString();
		}
		
		ASCIITableHeader[] headerObjs = { new ASCIITableHeader("Class", ASCIITable.ALIGN_LEFT), //
				new ASCIITableHeader("Name", ASCIITable.ALIGN_LEFT), //
				new ASCIITableHeader("Desc From PArser", ASCIITable.ALIGN_LEFT), //
				new ASCIITableHeader("Desc From Link", ASCIITable.ALIGN_LEFT), //
				new ASCIITableHeader("Parser", ASCIITable.ALIGN_LEFT), //
				new ASCIITableHeader("Link", ASCIITable.ALIGN_LEFT), //
				new ASCIITableHeader("Trend", ASCIITable.ALIGN_LEFT), //
		};
		
		ASCIITable.getInstance().printTable(headerObjs, data);
	}
	
	private static Set<String> ignored = new HashSet<String>();
	static {
		ignored.add("hudson.plugins.warnings.parser.ParserAdapter");
		ignored.add("hudson.plugins.warnings.parser.DynamicParser");
		ignored.add("hudson.plugins.warnings.parser.LintParser");
		ignored.add("hudson.plugins.warnings.parser.JSLintParser");
		ignored.add("hudson.plugins.warnings.parser.CssLintParser");
		ignored.add("hudson.plugins.warnings.parser.CppLintParser");
		ignored.add("hudson.plugins.warnings.parser.PuppetLintParser");
		ignored.add("hudson.plugins.warnings.parser.ViolationsAdapter");
		ignored.add("hudson.plugins.warnings.parser.fxcop.FxCopParser");
		ignored.add("hudson.plugins.warnings.parser.gendarme.GendarmeParser");
		ignored.add("hudson.plugins.warnings.parser.jcreport.JcReportParser");
		ignored.add("hudson.plugins.warnings.parser.StyleCopParser");
		ignored.add("");
		ignored.add("");
	}
	
}
