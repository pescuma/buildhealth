package org.pescuma.buildhealth.cli.commands;

import static org.apache.commons.io.IOUtils.*;
import static org.pescuma.buildhealth.cli.commands.CliUtils.*;
import static org.pescuma.buildhealth.extractor.utils.EncodingHelper.*;
import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.pescuma.buildhealth.cli.BuildHealthCliCommand;

@Command(name = "script", description = "Runs multiple commands from a script file")
public class ScriptCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file", description = "Script file", required = true)
	public File file;
	
	@Override
	protected void execute() {
		
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			
			LineIterator lines = lineIterator(toReader(input));
			
			while (lines.hasNext()) {
				String line = lines.next();
				
				String[] cmd = parseCommand(line);
				if (cmd != null) {
					System.out.println("> " + line);
					executeCommand(buildHealth, cmd);
					System.out.println();
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
			
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
}
