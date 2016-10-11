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
			String token = PreProcessUtil.removeMark(stok.nextToken());
			outList = maAnal.analyze(token);
			returnList.add(outList);
		}
		return returnList;
	}
}
