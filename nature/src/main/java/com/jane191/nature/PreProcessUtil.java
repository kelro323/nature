package com.jane191.nature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;


public class PreProcessUtil {
	private static final String[] marks = new String[] {",", "'", "-"};
	
	public static String removeMark(String token) {
		LinkedHashSet<String> markSet = countMark(token);
		if(markSet.size()!=0) return remove(token, markSet);
		else return token;
	}
	
	private static LinkedHashSet<String> countMark(String token) {
		LinkedHashSet<String> markSet = new LinkedHashSet<String>();
		ArrayList<MarkInfo> markList = new ArrayList<MarkInfo>();
		for(String mark : marks) {
			if(token.indexOf(mark)>-1) {
				int count = 0;
				for(int i=0; i<token.length() ;i++) {
					if(token.indexOf(mark, i)>-1) count++;
				}
				markList.add(new MarkInfo(mark, count));
			}
		}
		//횟수가 작은 순서대로 remove 과정을 진행하기 위해서 오름차순으로 정렬, 횟수가 많은 문장부호부터 시작하니 제대로 처리가 안됨
		Collections.sort(markList, (a,b)-> a.count<b.count ? -1: a.count>b.count ? 1:0);
		for(MarkInfo mark : markList) {
			markSet.add(mark.mark);
		}
		return markSet;
	}
	
	private static String remove(String token, LinkedHashSet<String> markSet) {
		String returnValue = "";
		External :for(String mark : markSet) {
			returnValue = "";
			if(token.indexOf(mark)==0) token = token.substring(1,token.length());
			int temp1 = 0;
			int temp2 = 0;
			for(int i=0;i<token.length()-1;i++) {
				temp2 = token.indexOf(mark,i);
				if(temp2==-1) {
					token = returnValue + token.substring(temp1, token.length());
					returnValue = token;
					continue External;
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
	}
}

class MarkInfo {
	String mark;
	int count;

	MarkInfo(String mark, int count) {
		this.mark = mark;
		this.count = count;
	}
}


