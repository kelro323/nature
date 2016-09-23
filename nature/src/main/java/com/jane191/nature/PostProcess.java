package com.jane191.nature;

import java.util.ArrayList;
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
	private static final HashSet<String> busaEomi = new HashSet<String>();
	private static final HashSet<String> deJosa = new HashSet<String>();
	
	static {
		String[] nomis = new String[] {"이","가","께서","에서","은","는","도","만"}; //주격 조사
		String[] objs = new String[] {"을","를","은","는","도","만"}; //목적격 조사
		String[] ends = new String[] {"ㄴ다","는다","ㅂ니다","습니다"}; //종결 어미
		String[] cons = new String[] {"고","며","으며","면서"}; //연결 어미
		String[] adnomis = new String[] {"는","은","ㄴ","을","ㄹ","던","의"}; //관형사형 어미&조사
		String[] busaj = new String[] {"에","에서","에게","와","과","으로","로"}; //부사격 조사
		String[] busae = new String[] {"도록", "게"}; //부사형 어미
		String[] dej = new String[] {"이다", "이면", "이고", "이니", "이지"}; //서술격 조사
		
		addList(nomiJosa,nomis);
		addList(objJosa,objs);
		addList(endEomi,ends);
		addList(conEomi,cons);
		addList(adnomiEJ,adnomis);
		addList(busaJosa,busaj);
		addList(busaEomi,busae);
		addList(deJosa, dej);
	};
	
	public static boolean removeAdnomi(List<List<AnalysisOutput>> outList) {
		for(int i=0; i<outList.size(); i++) {
			for(AnalysisOutput o : outList.get(i)) {
				
			}
		}
		return true;
	}
	
	public static boolean analysisNJVM(List<List<AnalysisOutput>> outList, int index) {
		//index-1 & index+1의 결과값을 보고 index 의 결과값을 결정.
		//ex) VM vs NJ 의 경우 V가 타동사이고 index-1이 목적격 조사를 포함한 경우 V로 판단 하는 등. 확률형보단 법칙형으로 설계 예정
		AnalysisOutput foreAnal = new AnalysisOutput();
		List<AnalysisOutput> nextList = new ArrayList<AnalysisOutput>();
		if(index > 0) {
			foreAnal = outList.get(index-1).get(0);
		}
		if(index < outList.size()) {
			nextList = outList.get(index+1);
		}
		
		if(adnomiEJ.contains(foreAnal.getJosa())) {
			if(index>1) foreAnal = outList.get(index-2).get(0);
			else {
				foreAnal.setUsedPos('A'); //관형사로 포스 셋
				foreAnal.setScore(AnalysisOutput.SCORE_FAIL); //실패 스코어로 변경
			}
		}
		List<AnalysisOutput> preList = outList.get(index);
		int preSize = preList.size();
		if(foreAnal.getScore()> AnalysisOutput.SCORE_FAIL) {
			if(foreAnal.getUsedPos()==PatternConstants.POS_NOUN) {
				//조사가 존재하고, 그 조사가 주격 조사나 서술격 조사가 아닐때
				if(foreAnal.getJosa()!=null 
						&& !(nomiJosa.contains(foreAnal.getEomi())||deJosa.contains(foreAnal.getEomi()))) { 
					for(AnalysisOutput o : preList) {
						if(o.getUsedPos()==PatternConstants.POS_NOUN) {
							preList.remove(o);
						}
					}
					if(preList.size()>1) {
						boolean detBusa = false;
						for(AnalysisOutput no : nextList) {
							if(no.getUsedPos()==PatternConstants.POS_VERB && !busaEomi.contains(no.getEomi())) {
								detBusa = true;
							}
						}
						List<AnalysisOutput> tempBusaList = new ArrayList<AnalysisOutput>();
						List<AnalysisOutput> tempVerbList = new ArrayList<AnalysisOutput>();
						for(AnalysisOutput o : preList) {
							if(o.getUsedPos()==PatternConstants.POS_AID || busaEomi.contains(o.getEomi())) {
								tempBusaList.add(o);
							} else tempVerbList.add(o);
						}
						if(detBusa) preList = tempBusaList;
						else preList = tempVerbList;
					}
					outList.remove(index);
					outList.add(index, preList);
				} else if(nomiJosa.contains(foreAnal.getJosa())) { 
					// 주격 조사일때
					for(AnalysisOutput o : preList) {
						if(o.getUsedPos()==PatternConstants.POS_NOUN && !deJosa.contains(o.getJosa())) {
							preList.remove(o);
						}
					}
					outList.remove(index);
					outList.add(index, preList);
				} else if(deJosa.contains(foreAnal.getJosa())) {
					// 서술격 조사일때
					for(AnalysisOutput o : preList) {
						if(o.getUsedPos()==PatternConstants.POS_NOUN && !nomiJosa.contains(o.getJosa())) {
							preList.remove(o);
						}
					}
					outList.remove(index);
					outList.add(index, preList);
				} else {
					// 조사가 업을때
				}
			} else if(foreAnal.getUsedPos()==PatternConstants.POS_VERB) {
				if(foreAnal.getEomi()!=null) {
					if(preSize==2) {
						int check = 0;
						if(preList.get(1).getUsedPos()==PatternConstants.POS_NOUN) check=1;
						preList.remove(check);
						outList.remove(index);
						outList.add(index, preList);
					}
				}
			}
		} else {
			//관형격일때
			if(foreAnal.getUsedPos()=='A') {
				boolean nounChecker = false;
				for(AnalysisOutput o : preList) {
					if(o.getUsedPos()==PatternConstants.POS_NOUN) nounChecker = true;
				}
				if(nounChecker) {
					if(preSize==2) {
						int check = 0;
						if(preList.get(1).getUsedPos()==PatternConstants.POS_NOUN) check=1;
						preList.remove(check);
						outList.remove(index);
						outList.add(index, preList);
					}
				}
			} else { //처음일때
				AnalysisOutput newAO = new AnalysisOutput();
				for(AnalysisOutput o : nextList) {
					if(o.getUsedPos()==PatternConstants.POS_NOUN) {
						for(AnalysisOutput pre : preList) {
							if((pre.getPosType()=='d'||pre.getPosType()=='n'||pre.getPosType()=='p')
									||(adnomiEJ.contains(pre.getEomi())||adnomiEJ.contains(pre.getJosa()))) {
								newAO = pre;
								break;
							} else if(pre.getPosType()=='c') {
								newAO = pre;
								break;
							} else if(pre.getUsedPos()==PatternConstants.POS_NOUN &&
									pre.getPatn()==PatternConstants.PTN_N) {
								newAO = pre;
								break;
							}
						}
					}
				}
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
