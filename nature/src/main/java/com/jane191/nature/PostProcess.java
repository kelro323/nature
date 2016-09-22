package com.jane191.nature;

import java.util.HashSet;
import java.util.List;

import org.apache.lucene.analysis.ko.morph.AnalysisOutput;
import org.apache.lucene.analysis.ko.morph.PatternConstants;

public class PostProcess {
	
	private static final HashSet<String> nomiJosa = new HashSet<String>();
	private static final HashSet<String> objJosa = new HashSet<String>();
	private static final HashSet<String> endEomi = new HashSet<String>();
	private static final HashSet<String> conEomi = new HashSet<String>();
	private static final HashSet<String> adnomiEJ = new HashSet<String>();
	private static final HashSet<String> busaJosa = new HashSet<String>();
	
	static {
		String[] nomis = new String[] {"이","가","께서","에서","은","는","도","만"}; //주격 조사
		String[] objs = new String[] {"을","를","은","는","도","만"}; //목적격 조사
		String[] ends = new String[] {"ㄴ다","는다","ㅂ니다","습니다"}; //종결 어미
		String[] cons = new String[] {"고","며","으며","면서"}; //연결 어미
		String[] adnomis = new String[] {"는","은","ㄴ","을","ㄹ","던","의"}; //관형사형 어미&조사
		String[] busas = new String[] {"에","에서","에게","와","과","으로","로"}; //부사격 조사
		
		addList(nomiJosa,nomis);
		addList(objJosa,objs);
		addList(endEomi,ends);
		addList(conEomi,cons);
		addList(adnomiEJ,adnomis);
		addList(busaJosa,busas);
	};
	
	public static boolean analysisNJVM(List<List<AnalysisOutput>> outlist, int index) {
		//index-1 & index+1의 결과값을 보고 index 의 결과값을 결정.
		//ex) VM vs NJ 의 경우 V가 타동사이고 index-1이 목적격 조사를 포함한 경우 V로 판단 하는 등. 확률형보단 법칙형으로 설계 예정
		AnalysisOutput foreAnal = outlist.get(index-1).get(0);
		if(foreAnal.getUsedPos()==PatternConstants.POS_NOUN) {
			if(adnomiEJ.contains(foreAnal.getEomi())||adnomiEJ.contains(foreAnal.getJosa())) {
				foreAnal = outlist.get(index-2).get(0);
			}
			
		}
		return true;
	}
	
	
	private static void addList(HashSet<String> set, String[] components){
		for(String s : components) {
			set.add(s);
		}
	}
}
