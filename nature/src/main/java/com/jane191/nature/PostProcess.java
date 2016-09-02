package com.jane191.nature;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.ko.morph.AnalysisOutput;

public class PostProcess {
	
	private static final List<String> nomiJosa = new ArrayList<String>();
	private static final List<String> objJosa = new ArrayList<String>();
	private static final List<String> endEomi = new ArrayList<String>();
	private static final List<String> conEomi = new ArrayList<String>();
	private static final List<String> adnomiEomi = new ArrayList<String>();
	private static final List<String> busaJosa = new ArrayList<String>();
	
	static {
		String[] nomis = new String[] {"이","가","께서","에서","은","는","도","만"};
		String[] objs = new String[] {"을","를","은","는","도","만"};
		String[] ends = new String[] {"ㄴ다","는다","ㅂ니다","습니다"};
		String[] cons = new String[] {"고","며","으며","면서"};
		String[] adnomis = new String[] {"는","은","ㄴ","을","ㄹ","던"};
		String[] busas = new String[] {"에","에서","에게","와","과","으로","로"};
		addList(nomiJosa,nomis);
		addList(objJosa,objs);
		addList(endEomi,ends);
		addList(conEomi,cons);
		addList(adnomiEomi,adnomis);
		addList(busaJosa,busas);
	};
	
	public static boolean analysisNJVM(List<List<AnalysisOutput>> outlist, int index) {
		//index-1 & index+1의 결과값을 보고 index 의 결과값을 결정.
		//ex) VM vs NJ 의 경우 V가 타동사이고 index-1이 목적격 조사를 포함한 경우 V로 판단 하는 등. 확률형보단 법칙형으로 설계 예정
		return true;
	}
	
	private static void addList(List<String> set, String[] components){
		for(String s : components) {
			set.add(s);
		}
	}
}
