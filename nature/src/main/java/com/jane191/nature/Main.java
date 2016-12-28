package com.jane191.nature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.StringTokenizer;
import java.io.IOException;

import org.apache.lucene.analysis.ko.morph.AnalysisOutput;
import org.apache.lucene.analysis.ko.morph.MorphException;

public class Main {
	public static void main(String[] args) throws MorphException, IOException {
		ArirangAnalyzerHandler aah = new ArirangAnalyzerHandler();
		/*
		BufferedReader br = 
				new BufferedReader(new FileReader("C:\\Users\\Seungwoo\\PycharmProjects\\postProcessAbstract\\edit\\abstract0.txt"));
		while(true) {
			String line = br.readLine();
			if(line==null) break;
			if(line.length()==0) continue;
			
			StringTokenizer tokens = new StringTokenizer(line, ".");
			while(tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				List<List<AnalysisOutput>> result = aah.morphAnalayzer(token);
				result = PostProcessUtil.nounEomi(PostProcessUtil.scoreSelect(result));
				for(int i=0; i<result.size();i++) {
					SubVerbUtil.determinSubVerb(result, i);
				}
				PostProcessUtil.noDicCase(result);
				for(int i = 0;i<result.size(); i++) {
					if(result.get(i).size()>1) {
						PostProcessUtil.selectResults(result, i);
					}
				}
				System.out.println("exercise"+result);
			}
		}
		*/
		
		String input2 = "그러므로 본 연구는 우선, 사회적 배제와 포함에 대한 이론적 고찰로부터 각 개념과 구성요소, 특성, 유용성 및 제한성 등 기본적인 이해를 도모하고자 노력하였다.";
		String input1 = "나에게 그 책의 의미는 '단순한 지식'이 아니라 '삶의 지혜'이다.";
		StringTokenizer token = new StringTokenizer(input2,".");
		while(token.hasMoreTokens()) {
			String token2 = token.nextToken();
			List<List<AnalysisOutput>> result = aah.morphAnalayzer(token2);
			
			System.out.println("형태소 분석 : " +result);
			result = PostProcessUtil.scoreSelect(result);
			System.out.println("스코어 분류 : " +result);
			result = PostProcessUtil.nounEomi(result);
			for(int i = 0; i<result.size(); i++) {
				SubVerbUtil.determinSubVerb(result, i);
			}
			PostProcessUtil.noDicCase(result);
			for(int i = 0;i<result.size(); i++) {
				if(result.get(i).size()>1) {
					PostProcessUtil.selectResults(result, i);
				}
			}
			System.out.println("실험"+result);
		}
		
		//String token = "'사람";
		//System.out.println(ArirangAnalyzerHandler.markRemove2(token, "'"));
	}
}
	

