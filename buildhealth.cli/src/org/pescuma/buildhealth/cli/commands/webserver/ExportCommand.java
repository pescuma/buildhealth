package org.pescuma.buildhealth.cli.commands.webserver;

import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;
import io.airlift.command.Arguments;
import io.airlift.command.Command;
import io.airlift.command.Option;
import io.airlift.command.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;
import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.utils.FileHelper;

import com.google.common.io.Closer;

@Command(name = "export", description = "Export the report")
public class ExportCommand extends BuildHealthCliCommand {
	
	@Option(name = { "-f", "--format" }, title = "format", description = "The format to export: html, xml, or json (default: html)")
	public String format = "html";
	
	@Arguments(title = "path", description = "Where to export. If exporting an html, it should be a path, else it should be a file name.")
	public File path;
	
	@Override
	protected void execute() {
		Report report = buildHealth.generateReport(Full);
		
		try {
			
			Closer closer = Closer.create();
			try {
				
				export(report, closer);
				
			} catch (IOException e) {
				throw closer.rethrow(e);
				
			} finally {
				closer.close();
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void export(Report report, Closer closer) throws IOException {
		if ("html".equals(format)) {
			mkdirs(path);
			
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(path, "report.json")),
					"UTF-8");
			closer.register(writer);
			
			BuildHealthWebServer.reportJson(writer, report);
			
			copyResources(closer, "index.html", "index.css", "index.js");
			copyResources(closer, "images/bg-table-thead.png", "images/collapse.png", "images/expand.png",
					"images/collapse-light.png", "images/expand-light.png");
			
			copyResources(closer, createIconURL("build", report.getStatus()));
			for (Report child : report.getChildren())
				copyResources(closer, createIconURL(getImageId(child), child.getStatus()));
			
		} else if ("xml".equals(format)) {
			FileWriter result = closer.register(new FileWriter(path));
			
			BuildHealthWebServer.reportXml(result, report);
			
		} else if ("json".equals(format)) {
			FileWriter result = closer.register(new FileWriter(path));
			
			BuildHealthWebServer.reportJson(result, report);
			
		} else {
			throw new ParseException("Unkwnon format " + format);
		}
	}
	
	private static void mkdirs(File dir) throws IOException {
		if (!dir.exists() && !dir.mkdirs())
			throw new IOException("Could not create path: " + FileHelper.getCanonicalPath(dir));
	}
	
	private String getImageId(Report report) {
		return report.getName().toLowerCase().replace(" ", "");
	}
	
	private String createIconURL(String type, BuildStatus status) {
		return "icons/" + type + "-" + status.toString().toLowerCase() + ".png";
	}
	
	private void copyResources(Closer closer, String... resources) throws IOException {
		for (String resource : resources) {
			File file = new File(path, resource);
			mkdirs(file.getParentFile());
			
			InputStream in = closer.register(BuildHealthWebServer.findResource("/" + resource));
			OutputStream out = closer.register(new FileOutputStream(file));
			
			IOUtils.copy(in, out);
		}
	}
}
