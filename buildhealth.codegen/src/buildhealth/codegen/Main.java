package buildhealth.codegen;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		
		new WarningsExtractorGenerator().generate();
		
		System.out.println("DOne!");
	}
}
