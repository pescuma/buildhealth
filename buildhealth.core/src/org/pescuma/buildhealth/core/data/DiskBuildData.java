package org.pescuma.buildhealth.core.data;

import static java.lang.System.*;
import static java.util.Arrays.*;
import static org.apache.commons.io.FileUtils.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.pescuma.buildhealth.core.BuildData;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.io.Closer;

public class DiskBuildData implements BuildData {
	
	private final File file;
	private final BuildData data;
	private boolean loadedFromDisk = false;
	private boolean wroteData = false;
	
	public DiskBuildData(File file, BuildData data) {
		this.file = file;
		this.data = data;
	}
	
	public void saveToDisk() {
		if (!wroteData)
			return;
		
		Closer closer = Closer.create();
		try {
			try {
				forceMkdir(file.getParentFile());
				
				FileWriter writer = closer.register(new FileWriter(file, !loadedFromDisk));
				CSVWriter csv = closer.register(new CSVWriter(writer));
				
				for (Line line : data.getLines()) {
					String[] cols = line.getColumns();
					
					String[] toWrite = new String[cols.length + 1];
					toWrite[0] = Double.toString(line.getValue());
					arraycopy(cols, 0, toWrite, 1, cols.length);
					
					csv.writeNext(toWrite);
				}
				
			} catch (IOException e) {
				throw closer.rethrow(e);
				
			} finally {
				closer.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error writing csv: " + file, e);
		}
		
	}
	
	private void loadFromDisk() {
		if (loadedFromDisk)
			return;
		loadedFromDisk = true;
		
		if (!file.exists())
			return;
		
		Closer closer = Closer.create();
		try {
			try {
				
				FileReader reader = closer.register(new FileReader(file));
				CSVReader csv = closer.register(new CSVReader(reader));
				
				String[] line;
				while ((line = csv.readNext()) != null) {
					double val = Double.parseDouble(line[0]);
					String[] info = copyOfRange(line, 1, line.length);
					data.add(val, info);
				}
				
			} catch (IOException e) {
				throw closer.rethrow(e);
				
			} finally {
				closer.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading csv: " + file, e);
		}
	}
	
	@Override
	public boolean isEmpty() {
		loadFromDisk();
		return data.isEmpty();
	}
	
	@Override
	public void add(double value, String... info) {
		wroteData = true;
		data.add(value, info);
	}
	
	@Override
	public double get(String... info) {
		loadFromDisk();
		return data.get(info);
	}
	
	@Override
	public Collection<Line> getLines() {
		loadFromDisk();
		return data.getLines();
	}
	
	@Override
	public Collection<String> getDistinct(int column) {
		loadFromDisk();
		return data.getDistinct(column);
	}
	
	@Override
	public Map<String, Value> sumDistinct(int columns) {
		loadFromDisk();
		return data.sumDistinct(columns);
	}
	
	@Override
	public Map<String[], Value> sumDistinct(int... columns) {
		loadFromDisk();
		return data.sumDistinct(columns);
	}
	
	@Override
	public BuildData filter(String... info) {
		loadFromDisk();
		return data.filter(info);
	}
	
	@Override
	public BuildData filter(int column, String name) {
		loadFromDisk();
		return data.filter(column, name);
	}
	
	@Override
	public double sum() {
		loadFromDisk();
		return data.sum();
	}
	
	@Override
	public int size() {
		loadFromDisk();
		return data.size();
	}
	
}
