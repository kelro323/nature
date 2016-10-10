package com.jane191.nature;

import java.util.HashSet;

public class PreProcessUtil {
	private static final String[] marks = new String[] {",", "'", "-"};
	
	public static HashSet<String> countMark(String token) {
		HashSet<String> markSet = new HashSet<String>();
		for(String mark : marks) {
			if(token.indexOf(mark)>-1) {
				markSet.add(mark);
			}
		}
		return markSet;
	}
	
	public static String markRemove(String token, HashSet<String> markSet) {
		String returnValue = "";
		Loop1 :for(String mark : markSet) {
			returnValue = "";
			if(token.indexOf(mark)==0) token = token.substring(1,token.length());
			int temp1 = 0;
			int temp2 = 0;
			for(int i=0;i<token.length()-1;i++) {
				temp2 = token.indexOf(mark,i);
				if(temp2==-1) {
					token = returnValue + token.substring(temp1, token.length());
					System.out.println(token);
					continue Loop1;
				}
				if(i==0) returnValue = returnValue + token.substring(0,temp2);
				else returnValue = returnValue + token.substring(temp1,temp2);
				temp2++;
				temp1 = temp2;
				i=temp1;
				}
			returnValue = returnValue + token.substring(temp1, token.length());
		}
		return returnValue;
		/*
		if(token.indexOf(mark)>-1) {
			String returnValue = "";
			if(token.indexOf(mark)==0) token = token.substring(1,token.length());
			int temp1 = 0;
			int temp2 = 0;
			for(int i=0;i<token.length()-1;i++) {
				temp2 = token.indexOf(mark,i);
				if(temp2==-1) return token;
				if(i==0) returnValue = returnValue + token.substring(0,temp2);
				else returnValue = returnValue + token.substring(temp1,temp2);
				temp2++;
				temp1 = temp2;
				i=temp1;
			}
			returnValue = returnValue + token.substring(temp1, token.length());
			return returnValue;
		}*/
	}
}
