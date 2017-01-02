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
		
		BufferedReader br = 
				new BufferedReader(new FileReader("C:\\Users\\Seungwoo\\PycharmProjects\\postProcessAbstract\\edit\\abstract6.txt"));
		while(true) {
			String line = br.readLine();
			if(line==null) break;
			if(line.length()==0) continue;
			
			StringTokenizer tokens = new StringTokenizer(line, ".");
			while(tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				List<List<AnalysisOutput>> result = aah.morphAnalayzer(token);
				result = PostProcessUtil.scoreSelect(result);
				PostProcessUtil.nounEomi(result);
				for(int i=0; i<result.size();i++) {
					SubVerbUtil.determinSubVerb(result, i);
				}
				PostProcessUtil.noDicCase(result);
				for(int i = 0;i<result.size(); i++) {
					if(result.get(i).size()>1 && differType(result.get(i))) {
						PostProcessUtil.selectResults(result, i);
					}
				}
				System.out.println("exercise"+result);
			}
		}
		
		
		String input2 = "경험적 분석의 결과 도움을 받을 이웃이 많고, 주변에 소통할 사람이 있으며, 일인가구가 아닌 경우 삶의 만족이 높은 반면, 자신의 객관적 소득계층에 비해 스스로를 낮게 평가하거나 다른 사람들로부터 무시당하거나 인정을 받지 못한 경험이 있으면 삶의 만족이 낮았다.";
		String input1 = "나에게 그 책의 의미는 '단순한 지식'이 아니라 '삶의 지혜'이다.";
		StringTokenizer token = new StringTokenizer(input2,".");
		while(token.hasMoreTokens()) {
			String token2 = token.nextToken();
			List<List<AnalysisOutput>> result = aah.morphAnalayzer(token2);
			
			System.out.println("형태소 분석 : " +result);
			result = PostProcessUtil.scoreSelect(result);
			System.out.println("스코어 분류 : " +result);
			PostProcessUtil.nounEomi(result);
			for(int i = 0; i<result.size(); i++) {
				SubVerbUtil.determinSubVerb(result, i);
			}
			PostProcessUtil.noDicCase(result);
			for(List<AnalysisOutput> preList : result) {
				PostProcessUtil.complexJE(preList);
			}
			System.out.println(result);
			for(int i = 0;i<result.size(); i++) {
				if(result.get(i).size()>1) {
					int count = 1;
					char type = result.get(i).get(0).getUsedPos();
					for(int j = 1;j<result.get(i).size(); j++) {
						if(type==result.get(i).get(j).getUsedPos()) {
							count += 1;
						}
					}
					if(count != result.get(i).size()){
						System.out.println(result.get(i));
						PostProcessUtil.selectResults(result, i);
					}
				}
			}
			System.out.println("실험"+result);
		}
		
		//String token = "'사람";
		//System.out.println(ArirangAnalyzerHandler.markRemove2(token, "'"));
	}
	
	private static boolean differType(List<AnalysisOutput> preList) {
		int count = 1;
		char type = preList.get(0).getUsedPos();
		for(int j = 1;j<preList.size(); j++) {
			if(type==preList.get(j).getUsedPos()) {
				count += 1;
			}
		}
		if(count != preList.size()) return true;
		else return false;
	}
}
	

