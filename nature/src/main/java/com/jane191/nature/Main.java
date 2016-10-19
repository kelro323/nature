package com.jane191.nature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.ko.morph.AnalysisOutput;
import org.apache.lucene.analysis.ko.morph.MorphException;
import org.apache.lucene.analysis.ko.morph.PatternConstants;

public class Main {
	public static void main(String[] args) throws MorphException {
		
		ArirangAnalyzerHandler aah = new ArirangAnalyzerHandler();
		String input2 = "내가 진짜 너를 싫어하는 것은 아니다.";
		String input1 = "나에게 그 책의 의미는 '단순한 지식'이 아니라 '삶의 지혜'이다.";
		StringTokenizer token = new StringTokenizer(input2,".");
		while(token.hasMoreTokens()) {
			String token2 = token.nextToken();
			List<List<AnalysisOutput>> result = aah.morphAnalayzer(token2);
			
			System.out.println("형태소 분석 : " +result);
			
			result = PostProcessUtil.scoreSelect(result);
			System.out.println("스코어 분류 : " +result);
			result = PostProcessUtil.nounEomi(result);
			
			List<ArrayList<String>> result2 = new ArrayList<ArrayList<String>>();
			List<ArrayList<String>> result3 = new ArrayList<ArrayList<String>>();
			checking(result, result3);
			//불용어 처리 - 어간 문자열과 어간 속성만 뽑아서 따로 저장 
			for (int i=0; i < result.size() ; i++) {
				postProcess(result, i, result2);
				}
			System.out.println("불용어 처리  : "+result2);
			//System.out.println("불용어 처리   : "+result3);
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
	
	/* 결과 입력 메소드 */
	private static void addResult(int index, List<ArrayList<String>> result, List<AnalysisOutput> temp) {
		ArrayList<String> tempResult = new ArrayList<String>();
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
				//그림을 -> 그림+을  - 그리+ㅁ+을 로 나오는 경우 다음 토큰의 형태값에 따라 결과 하나 선택 파트.
				//부사를 일단 처리하고 체크해야할 듯, 11,3,5,13,15의 경우 서술어 파트에 해당될 가능성이 크므로 NJ형태를 출력함.
				if(anal.get(index).get(i).getPatn()==12 && anal.get(index).get(i+1).getPatn()==2) {
					int check= anal.get(index+1).get(0).getPatn();
					if(check==11 || check==3 || check==5 || check ==13 || check==15) {
						i++;
					}
					else {
						addResult(i, result, temp);
						i++;
						continue;
					}
				}
				addResult(i, result, temp);
			}
		}
		else {
			addResult(0, result, temp);
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
	

