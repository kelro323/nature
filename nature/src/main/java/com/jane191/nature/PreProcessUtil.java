package com.jane191.nature;

public class PreProcessUtil {
	private static final String[] marks = new String[] {",", "'", "-", "‘", "’", "·"};

	public static String removeMark(String token) {
		for(String str : marks) {
			token = token.replaceAll(str, "");
		}
		return token;
	}
}
