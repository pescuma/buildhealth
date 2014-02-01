package buildhealth.codegen;

import hudson.plugins.warnings.parser.AbstractWarningsParser;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

class WarningsParser {
	
	private static Map<String, String> names = new HashMap<String, String>();
	static {
		names.put("hudson.plugins.warnings.parser.AppleLLVMClangParser", "clang");
		names.put("hudson.plugins.warnings.parser.GhsMultiParser", "chs");
		names.put("hudson.plugins.warnings.parser.JavaDocParser", "javadoc");
		names.put("hudson.plugins.warnings.parser.MsBuildParser", "msbuild");
		names.put("hudson.plugins.warnings.parser.TiCcsParser", "ticcs");
		names.put("hudson.plugins.warnings.parser.CoolfluxChessccParser", "chesscc");
		names.put("hudson.plugins.warnings.parser.FlexSDKParser", "flex");
		names.put("hudson.plugins.warnings.parser.GccParser", "gcc3");
	}
	
	private static Map<String, String> descriptions = new HashMap<String, String>();
	static {
		descriptions.put("hudson.plugins.warnings.parser.AppleLLVMClangParser", "LLVM Compiler (Clang)");
	}
	
	private static Map<String, String> baseClassNames = new HashMap<String, String>();
	static {
		baseClassNames.put("hudson.plugins.warnings.parser.AppleLLVMClangParser", "Clang");
	}
	
	final AbstractWarningsParser parser;
	
	public WarningsParser(AbstractWarningsParser parser) {
		this.parser = parser;
	}
	
	public String getClassName() {
		return parser.getClass().getName();
	}
	
	public String getBaseClassName() {
		String result = getClassName();
		
		if (baseClassNames.containsKey(result))
			return baseClassNames.get(result);
		
		result = result.substring(result.lastIndexOf('.') + 1);
		result = StringUtils.removeEnd(result, "Parser");
		return result;
	}
	
	public String getName() {
		return createName(parser);
	}
	
	public String getDescription() {
		return createDescriptionFromParser(parser);
	}
	
	public static String createName(AbstractWarningsParser parser) {
		String cls = parser.getClass().getName();
		if (names.containsKey(cls))
			return names.get(cls);
		
		String name = parser.getClass().getSimpleName();
		name = StringUtils.removeEnd(name, "Parser");
		name = StringUtils.removeEnd(name, "Compiler");
		name = StringUtils.removeEnd(name, "Console");
		name = StringUtils.uncapitalize(name);
		
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c))
				result.append("-").append(Character.toLowerCase(c));
			else
				result.append(c);
		}
		return result.toString();
	}
	
	public static String createDescriptionFromParser(AbstractWarningsParser parser) {
		String cls = parser.getClass().getName();
		if (descriptions.containsKey(cls))
			return descriptions.get(cls);
		
		return parser.getParserName().toString();
	}
	
	public static String createDescriptionFromLink(AbstractWarningsParser parser) {
		String name = parser.getLinkName().toString();
		name = StringUtils.removeEnd(name, "Warnings").trim();
		return name;
	}
}
