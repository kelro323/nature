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
		String input2 = "20대 중반의 이른 나이에 갑자기 찾아온 백내장 진단에 제가 크게 당황하지 않도록 담당 의사와 간호 실장이 함께 발병원인부터 수술과정 및 보험적용 여부까지 상세히 상담을 해주셨습니다. 그뿐만 아니라 이 사실을 부모님께도 따로 전화를 드려 차분하게 알려주었습니다. 수술 전, 후로도 마음을 가다듬고 안정을 취할 수 있도록 내 방같이 포근하고 안락한 분위기의 대기실에서 쉴 수 있었고 귀가 시엔 병원 앞에 택시까지 미리 대기시켜주는 서비스를 제공해 주었습니다. 시작부터 마지막까지 책임지고 고객의 완벽한 편의를 위한 서비스를 받을 수 있었습니다. "+
"해외구매대행으로 가방을 구매한 적이 있었습니다. 공지사항을 상세히 읽고 구매를 했음에도 불구하고 배송예정일이 훨씬 지나도 가방은 오지 않았습니다. 이에 문의 글을 남겼으나 오랜 기간 답변도 없었고 수차례 전화를 한 뒤에야 입금확인이 안 돼서 가방구매가 되지 않았고 그 사이에 가방 가격이 올랐으니 차액도 입금해달라는 답변을 받았습니다. 분명 사이트상에선 입금확인과 더불어 물품 구매단계 중이라 되어있었습니다. 거짓 정보와 문의에 대해 뒤늦은 대응과 책임 전가는 최악의 서비스였습니다.";
				
		String input1 = "나는 그림을 연습하였다.";
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
			for(int i = 0;i<result.size(); i++) {
				
				if(result.get(i).size()>1) {
					PostProcess.selectResults(result, i);
				}
			}
			System.out.println("실험"+result);
			}
			
		
		//String token = "'사람";
		//System.out.println(ArirangAnalyzerHandler.markRemove2(token, "'"));
		
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
				//그림을 -> 그림+을  - 그리+ㅁ+을 로 나오는 경우 다음 토큰의 형태값에 따라 결과 하나 선택 파트.
				//부사를 일단 처리하고 체크해야할 듯, 11,3,5,13,15의 경우 서술어 파트에 해당될 가능성이 크므로 NJ형태를 출력함.
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
	

