package com.jane191.nature;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.ko.morph.AnalysisOutput;
import org.apache.lucene.analysis.ko.morph.MorphAnalyzer;
import org.apache.lucene.analysis.ko.morph.MorphException;


public class ArirangAnalyzerHandler {
	public List<List<AnalysisOutput>> morphAnalayzer(String source) throws MorphException  {
		MorphAnalyzer maAnal = new MorphAnalyzer();
		List<AnalysisOutput> outList = new ArrayList<AnalysisOutput>();
		List<List<AnalysisOutput>> returnList = new ArrayList<List<AnalysisOutput>>();
		StringTokenizer stok = new StringTokenizer(source);
		while(stok.hasMoreTokens()) {
			String token = markRemove(stok.nextToken());
			outList = maAnal.analyze(token);
			
			returnList.add(outList);
		}
		return returnList;
	}
	/* 문장 부호 처리*/
	private String markRemove(String token) {
		if(token.indexOf("'")>-1) {
			int[] index= {0, 0};
			index[0] = token.indexOf("'");
			index[1] = token.lastIndexOf("'");
			if(index[0]==0 && index[1]==0) {
				token = token.substring(1, token.length());
			}
			else if(index[0] == index[1]){
				token = token.substring(0,index[0])+token.substring(index[0]+1, token.length());
			}
			else {
				token = token.substring(0, index[0])+token.substring(index[0]+1, index[1])+
						token.substring(index[1]+1, token.length());
			}
		}
		if(token.indexOf(",")>-1) {
			token =  token.substring(0, token.indexOf(","))+
					token.substring(token.indexOf(",")+1, token.length());
		}
		if(token.indexOf("․")>-1) {
			token = token.substring(0, token.indexOf("․"))+
					token.substring(token.indexOf("․")+1, token.length());
		}
		return token;
	}
}
