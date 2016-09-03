package com.DFM.StormFront.Util;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class StringUtil {
	public static String removeUnicode(String inputString) {
		String cleanString = StringUtil.filter(inputString);

		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u00BB"), ">>");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u00A0"), " ");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u00BC"), "1/4");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u00BD"), "1/2");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u00BE"), "3/4");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u2013"), "-");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u2014"), "-");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u2018"), "'");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u2019"), "'");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u201A"), "'");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u201B"), "'");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u201C"), "\"");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u201D"), "\"");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u201E"), "\"");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u201F"), "\"");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u2026"), "...");
		cleanString = cleanString.replaceAll("(?i)"+Pattern.quote("\\u0027"), "'");
		cleanString = cleanString.replace("¶", "");
		return cleanString;
	}

	public static String hyphenateString(String input) {
		// Replace invalid characters with empty strings.
		String hyphenated = input;
		//hyphenated = hyphenated.replace(" ", "-");
		hyphenated = hyphenated.replaceAll("[\\s]", "-");
		hyphenated = hyphenated.replaceAll("[^a-zA-Z0-9-\\._]", "");
		hyphenated = hyphenated.replaceAll("\\-{2,}", "-");
		Pattern pattern = Pattern.compile("\\.([a-zA-Z]{2,5}[0-9]*)\\.");
		while (pattern.matcher(hyphenated).matches()) {
			hyphenated = hyphenated.replaceAll("\\.([a-zA-Z]{2,5}[0-9]*)\\.", ".$1_.");
		}
		return hyphenated;
	}

	public static boolean isNullOrEmpty(String str){
		return StringUtils.isEmpty(str) || StringUtils.isBlank(str);
	}

	public static boolean isNotNullOrEmpty(String str){
		return StringUtils.isNotEmpty(str) && StringUtils.isNotBlank(str);
	}

	public static String filter(String str) {
		char previous = 0;
		StringBuilder filtered = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			char current = str.charAt(i);
			if (current >= 32 && current <= 126) {
				filtered.append(current);
			} else if (current == 65533 && current != previous) {
				filtered.append("&#8212;");
			} else {
				if (current != previous) {
					filtered.append("&#").append(Integer.toString(current)).append(";");
				}
			}
			previous = current;
		}

		return filtered.toString();
	}

	public static ArrayList<String> CSVtoArrayListStr(String csv) {
		ArrayList<String> csvArrayList = new ArrayList<>();

		if (csv != null) {
			String[] splitData = csv.split("\\s*,\\s*");
			for (String aSplitData : splitData) {
				if (!(aSplitData == null) || !(aSplitData.length() == 0)) {
					csvArrayList.add(aSplitData.trim());
				}
			}
		}

		return csvArrayList;
	}

	public static ArrayList<Integer> CSVtoArrayListInt(String csv) {
		ArrayList<Integer> csvArrayList = new ArrayList<>();

		if (csv != null) {
			String[] splitData = csv.split("\\s*,\\s*");
			for (String aSplitData : splitData) {
				if (!(aSplitData == null) || !(aSplitData.length() == 0)) {
					csvArrayList.add(Integer.parseInt(aSplitData.trim()));
				}
			}
		}

		return csvArrayList;
	}

    public static String getErrorInfo(Exception e) {
        return "error: " + e + ", message: " + e.getMessage() + ", StackTrace: " + Arrays.toString(e.getStackTrace());
	}

	public static InputSource toInputSource(String str) {
		return new InputSource(new StringReader(str));
	}

    public static InputStream toInputStream(String str) {
		return  new ByteArrayInputStream(str.getBytes());
	}

    public static String fromInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        String contents;
        while ((contents = reader.readLine()) != null) {
            out.append(contents);
		}
        reader.close();
        return out.toString();
	}

	public static boolean toBoolean(String booleanValue) {
		booleanValue = booleanValue.trim().toLowerCase();
		List<String> trueSet = Arrays.asList("1", "true", "yes");
		List<String> falseSet = Arrays.asList("0", "false", "no");

		if (trueSet.contains(booleanValue)) {
			return true;
		}
		if (falseSet.contains(booleanValue)) {
			return false;
		}

		throw new IllegalArgumentException(booleanValue + " is not a boolean.");
	}

	public static String NVL(String value, String defaut){
		if(StringUtils.isEmpty(value)) {
			return defaut;
		} else {
			return value;
		}
	}
}
