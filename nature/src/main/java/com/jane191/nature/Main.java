package com.jane191.nature;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.ko.morph.AnalysisOutput;
import org.apache.lucene.analysis.ko.morph.MorphException;
import org.apache.lucene.analysis.ko.morph.PatternConstants;

public class Main {
	public static void main(String[] args) throws MorphException {
		ArirangAnalyzerHandler aah = new ArirangAnalyzerHandler();
		String input2 = "그러므로 해외 경험, 외국어 실력에만 한정된 것이 아닌 '다양한 상황적 사고 및 행동방식'은 비단 글로벌 마인드에만 적용되는 것이 아닌 삶 전체에 있어 가장 중요한 가치라 생각합니다.";
				
		String input1 = "그는 착한 착하다 착하게.";
		StringTokenizer token = new StringTokenizer(input2,".");
		while(token.hasMoreTokens()) {
			String token2 = token.nextToken();
			System.out.println(token2);
			List<List<AnalysisOutput>> result = aah.morphAnalayzer(token2);
			System.out.println("형태소 분석 : " +result);
			
			List<ArrayList<String>> result2 = new ArrayList<ArrayList<String>>();
			List<ArrayList<String>> result3 = new ArrayList<ArrayList<String>>();
			checking(result, result3);
			 //불용어 처리 - 어간 문자열과 어간 속성만 뽑아서 따로 저장 
			for (int i=0; i < result.size() ; i++) {
				postProcess(result, i, result2);
				}
			System.out.println("불용어 처리   : "+result2);
			System.out.println("불용어 처리   : "+result3);
			}
	}
	
	/* 결과 입력 메소드 */
	private static void addResult(int index, ArrayList<String> tempResult,
			List<ArrayList<String>> result, List<AnalysisOutput> temp) {
		if(temp.get(index).getScore()>30) {
			if(String.valueOf(temp.get(index).getPos()).equals("V")) {
				tempResult.add(temp.get(index).getStem()+"다");
			}
			else {
				tempResult.add(temp.get(index).getStem());
			}
			tempResult.add(String.valueOf(temp.get(index).getPos()));
			tempResult.add(String.valueOf(temp.get(index).getScore()));
			result.add(tempResult);
		}
	}
	
	/*불용어 처리 메소드*/
	private static void postProcess(List<List<AnalysisOutput>> anal, int index, 
			List<ArrayList<String>> result) throws MorphException {
		List<AnalysisOutput> temp = anal.get(index);
		if(anal.get(index).size() > 1) {
			for (int i=0 ; i<temp.size(); i++) {
				
				if(anal.get(index).get(i).getPatn()==12 && anal.get(index).get(i+1).getPatn()==2) {
					int check= anal.get(index+1).get(0).getPatn();
					if(check==11 || check==3 || check==5 || check ==13 || check==15) {
						i++;
					}
					else {
						ArrayList<String> tempResult = new ArrayList<String>();
						addResult(i, tempResult,result, temp);
						i++;
						continue;
					}
				}
				ArrayList<String> tempResult = new ArrayList<String>();
				addResult(i, tempResult,result, temp);
			}
		}
		else {
			ArrayList<String> tempResult = new ArrayList<String>();
			addResult(0, tempResult, result, temp);
		}	
	}
	private static void checking(List<List<AnalysisOutput>> anal, List<ArrayList<String>> result) throws MorphException {
		for(int i=0; i<anal.size(); i++) {
			for(int j=0; j<anal.get(i).size(); j++) {
				if(anal.get(i).get(j).getScore()>30) {
					ArrayList<String> temp = new ArrayList<String>();
					temp.add(0, anal.get(i).get(j).getStem());
					temp.add(1, String.valueOf(anal.get(i).get(j).getPos()));
					result.add(temp);
				}
				//부사 삭제를 위한 if문
				if(anal.get(i).get(j).getPos()==PatternConstants.POS_AID) {
					/* 일반부사만 남기고 처리하기 위해서
					 if(anal.get(i).get(j).getPosType()!='일반부사') continue;
					 else {
					 	ArrayList<String> temp = new ArrayList<String>();
					 	temp.add(0, anal.get(i).get(j).getStem());
					 	temp.add(1, String.valueOf(anal.get(i).get(j).getPos()+"/"
					 		+anal.get(i).get(j).getPosType()));
					 	result.add(temp);
					 	}
					 */
					result.remove(result.size()-1);
				}
				
			}
		}
	}
	
}
	

