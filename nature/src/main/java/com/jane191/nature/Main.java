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
				new BufferedReader(new FileReader("C:\\Users\\Seungwoo\\PycharmProjects\\postProcessAbstract\\edit\\abstract1.txt"));
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
		
		String input2 = "이런 상황과 더불어 지역의 보수적인 사회정치적 조건은 지역공동체가 전체적이고 갈등을 사회화하는 전략을 추진할 수 있는 집합적 행동의 여지를 거의 주지 않았다.";
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
	

