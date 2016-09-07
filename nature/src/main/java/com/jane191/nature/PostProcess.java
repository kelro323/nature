package com.jane191.nature;

import java.util.HashSet;
import java.util.List;

import org.apache.lucene.analysis.ko.morph.AnalysisOutput;

public class PostProcess {
	
	private static final HashSet<String> nomiJosa = new HashSet<String>();
	private static final HashSet<String> objJosa = new HashSet<String>();
	private static final HashSet<String> endEomi = new HashSet<String>();
	private static final HashSet<String> conEomi = new HashSet<String>();
	private static final HashSet<String> adnomiEomi = new HashSet<String>();
	private static final HashSet<String> busaJosa = new HashSet<String>();
	private static final HashSet<String> subVerb = new HashSet<String>();
	
	static {
		String[] nomis = new String[] {"이","가","께서","에서","은","는","도","만"};
		String[] objs = new String[] {"을","를","은","는","도","만"};
		String[] ends = new String[] {"ㄴ다","는다","ㅂ니다","습니다"};
		String[] cons = new String[] {"고","며","으며","면서"};
		String[] adnomis = new String[] {"는","은","ㄴ","을","ㄹ","던"};
		String[] busas = new String[] {"에","에서","에게","와","과","으로","로"};
		String[] subVerbs = new String[] {"가다","가지다","나다","내다","놓다","대다","두다","말다","먹다",
				"못하다","버리다","보다","쌓다","않다","오다","있다","주다","지다","하다"};
		
		addList(nomiJosa,nomis);
		addList(objJosa,objs);
		addList(endEomi,ends);
		addList(conEomi,cons);
		addList(adnomiEomi,adnomis);
		addList(busaJosa,busas);
		addList(subVerb,subVerbs);
	};
	
	public static boolean analysisNJVM(List<List<AnalysisOutput>> outlist, int index) {
		//index-1 & index+1의 결과값을 보고 index 의 결과값을 결정.
		//ex) VM vs NJ 의 경우 V가 타동사이고 index-1이 목적격 조사를 포함한 경우 V로 판단 하는 등. 확률형보단 법칙형으로 설계 예정
		return true;
	}
	
	public static boolean analysisSubVerb(List<AnalysisOutput> outList, List<AnalysisOutput> checkList) {
		//이런식으로 하나하나 경우 다 입력해서 체크 해야할듯. 조건이 길어 질 경우 파일 따로 빼는 것도 괜찮을듯.
		//outList = 현재 분석할 결과값들, checkList = 분석할 결과 index-1 결과값들.
		int size = outList.size();
		for(int i=0; i<size;i++) {
			if(subVerb.contains(outList.get(i).getStem())
					&& outList.get(i).getPos()=='V') {
				String check = checkList.get(0).getSource()
						.substring(checkList.get(0).getStem().length());
				if(check.equals("어") && outList.get(i).getStem().equals("보다")) {
					outList.get(i).setPatn(50); //보조 동사로 세부 포지션 설정. 라이브러리 업데이트를 안해서 패턴으로 대신 입력 
					return true;
				} else if(true) {
					//블라블라블라...
				}
			}
		}
		return false;
		
	}
	
	private static void addList(HashSet<String> set, String[] components){
		for(String s : components) {
			set.add(s);
		}
	}
}
