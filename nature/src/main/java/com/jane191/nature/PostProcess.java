package com.jane191.nature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.analysis.ko.morph.AnalysisOutput;
import org.apache.lucene.analysis.ko.morph.PatternConstants;

public class PostProcess {
	/** 주격 조사 */
	private static final HashSet<String> nomiJosa = new HashSet<String>();
	/** 목적격 조사	 */
	private static final HashSet<String> objJosa = new HashSet<String>();
	/** 종결 어미 */
	private static final HashSet<String> endEomi = new HashSet<String>();
	/** 연결 어미 */
	private static final HashSet<String> conEomi = new HashSet<String>();
	/** 관형사형 어미&조사 */
	private static final HashSet<String> adnomiEJ = new HashSet<String>();
	/** 부사격 조사 */
	private static final HashSet<String> busaJosa = new HashSet<String>();
	/** 부사형 어미 */
	private static final HashSet<String> busaEomi = new HashSet<String>();
	/** 서술격 조사 */
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
	
	public static void selectResults(List<List<AnalysisOutput>> outList, int index) {
		//index-1 & index+1의 결과값을 보고 index 의 결과값을 결정.
		//ex) VM vs NJ 의 경우 V가 타동사이고 index-1이 목적격 조사를 포함한 경우 V로 판단 하는 등. 확률형보단 법칙형으로 설계 예정
		AnalysisOutput foreAnal = new AnalysisOutput();
		List<AnalysisOutput> nextList = new ArrayList<AnalysisOutput>();

		if(index > 0) {
			foreAnal = outList.get(index-1).get(0);
		}
		if(index < outList.size()-1) {
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
		if(foreAnal.getScore()> AnalysisOutput.SCORE_FAIL && nextList!=null) {
			if(foreAnal.getUsedPos()==PatternConstants.POS_NOUN) {
				//명사 케이스
				nounCase(foreAnal, preList, nextList, index, outList);
			} else if(foreAnal.getUsedPos()==PatternConstants.POS_VERB) {
				//동사 케이스
				verbCase(foreAnal, preList, nextList, outList, index);
			}
		} else if(nextList!=null){
			//관형격일때
			if(foreAnal.getUsedPos()=='A') {
				adnomiCase(preList, outList, preSize, index);
			} else { //처음일때
				firstCase(preList, nextList, index, outList);
			}
		} else {
			//마지먁일때
			lastCase(preList, outList, index);
		}
	}
	
	private static void addList(HashSet<String> set, String[] components){
		for(String s : components) {
			set.add(s);
		}
	}
	
	//앞의 토큰이 명사 사용일때
	private static void nounCase(AnalysisOutput foreAnal, List<AnalysisOutput> preList, List<AnalysisOutput> nextList,
			int index, List<List<AnalysisOutput>> outList) {
		//조사가 존재하고, 그 조사가 주격 조사나 서술격 조사가 아닐때
		if(foreAnal.getJosa()!=null 
				&& !(nomiJosa.contains(foreAnal.getEomi())||deJosa.contains(foreAnal.getEomi()))) {
			List<AnalysisOutput> tempList = new ArrayList<AnalysisOutput>();
			for(AnalysisOutput pre : preList) {
				if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
					tempList.add(pre);
				}
			}
			for(AnalysisOutput temp : tempList) {
				preList.remove(temp);
			}
			if(preList.size()>1) {
				boolean detBusa = false;
				for(AnalysisOutput ne : nextList) {
					if(ne.getUsedPos()==PatternConstants.POS_VERB && !busaEomi.contains(ne.getEomi())) {
						detBusa = true;
					}
				}
				List<AnalysisOutput> tempBusaList = new ArrayList<AnalysisOutput>();
				List<AnalysisOutput> tempVerbList = new ArrayList<AnalysisOutput>();
				for(AnalysisOutput pre : preList) {
					if(pre.getUsedPos()==PatternConstants.POS_AID || busaEomi.contains(pre.getEomi())) {
						tempBusaList.add(pre);
					} else tempVerbList.add(pre);
				}
				if(detBusa) preList = tempBusaList;
				else preList = tempVerbList;
			}
			outList.remove(index);
			outList.add(index, preList);
		} else if(nomiJosa.contains(foreAnal.getJosa())) { 
			// 주격 조사일때
			for(AnalysisOutput pre : preList) {
				if(pre.getUsedPos()==PatternConstants.POS_NOUN && !deJosa.contains(pre.getJosa())) {
					preList.remove(pre);
				}
			}
			outList.remove(index);
			outList.add(index, preList);
		} else if(deJosa.contains(foreAnal.getJosa())) {
			// 서술격 조사일때
			for(AnalysisOutput pre : preList) {
				if(pre.getUsedPos()==PatternConstants.POS_NOUN && !nomiJosa.contains(pre.getJosa())) {
					preList.remove(pre);
				}
			}
			outList.remove(index);
			outList.add(index, preList);
		} else {
			// 조사가 없을때
			List<AnalysisOutput> tempList = new ArrayList<AnalysisOutput>();
			for(AnalysisOutput pre : preList) {
				if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
					tempList.add(pre);
				}
			}
			outList.remove(index);
			outList.add(index, tempList);
		}
	}
	
	private static void verbCase(AnalysisOutput foreAnal, List<AnalysisOutput> preList,
			List<AnalysisOutput> nextList, List<List<AnalysisOutput>>outList, int index) {
		if(foreAnal.getEomi()!=null) {
			List<AnalysisOutput> tempVerbList = new ArrayList<AnalysisOutput>();
			List<AnalysisOutput> tempNounList = new ArrayList<AnalysisOutput>();
			boolean verbCheck = false;
			if(busaEomi.contains(foreAnal.getEomi())) {
				//뒤에 명사형이 동사형이 올 수도 있는 상황. 뒤에 구절이 타동사로 이루어지면 목적어가 들어와 명사형이 나타나고
				//자동사인 경우 바로 동사형이 선택.
				for(AnalysisOutput pre : preList) {
					if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
						for(AnalysisOutput ne : nextList) {
							if(ne.getUsedPos()==PatternConstants.POS_VERB &&
									(ne.getPosType()=='t'||ne.getPosType()=='v'||ne.getPosType()=='k')) {
								verbCheck = false;
								tempNounList.add(pre);
								break;
							}
						}
						break;
					} else if(pre.getUsedPos()==PatternConstants.POS_VERB) {
						verbCheck = true;
						tempVerbList.add(pre);
						break;
					}
				}
			}
			if(verbCheck) preList = tempVerbList;
			else preList = tempNounList;
			outList.remove(index);
			outList.add(index, preList);
		}
	}
	
	private static void firstCase(List<AnalysisOutput> preList, List<AnalysisOutput> nextList, int index
			, List<List<AnalysisOutput>>outList) {
		List<AnalysisOutput> tempList = new ArrayList<AnalysisOutput>();
		for(AnalysisOutput ne : nextList) {
			if(ne.getUsedPos()==PatternConstants.POS_NOUN) {
				for(AnalysisOutput pre : preList) {
					//현재가 관형사일때
					if((pre.getPos()==PatternConstants.POS_AID&&
							(pre.getPosType()=='d'||pre.getPosType()=='n'||pre.getPosType()=='p'))
							||(adnomiEJ.contains(pre.getEomi())||adnomiEJ.contains(pre.getJosa()))) {
						tempList.add(pre);
						break;
					//현재가 접속부사일때
					} else if(pre.getPosType()=='c'&&pre.getPos()==PatternConstants.POS_AID) {
						tempList.add(pre);
						break;
					//현재가 단독 명사일때(고찰이 필요함)
					} else if(pre.getUsedPos()==PatternConstants.POS_NOUN &&
							pre.getPatn()==PatternConstants.PTN_N) {
						tempList.add(pre);
						break;
					}
				}
				outList.remove(index);
				outList.add(index, tempList);
				break;
			}
		}
	}
	
	private static void lastCase(List<AnalysisOutput> preList, List<List<AnalysisOutput>>outList,
			int index) {
		List<AnalysisOutput> tempList = new ArrayList<AnalysisOutput>();
		for(AnalysisOutput pre : preList) {
			if(endEomi.contains(pre.getEomi())
					||pre.getJosa().equals("이다")||pre.getJosa().equals("이지")) {
				tempList.add(pre);
			}
		}
		outList.remove(index);
		outList.add(index, tempList);
	}
	
	private static void adnomiCase(List<AnalysisOutput> preList, List<List<AnalysisOutput>>outList,
			int preSize, int index) {
		boolean nounChecker = false;
		for(AnalysisOutput pre : preList) {
			if(pre.getUsedPos()==PatternConstants.POS_NOUN) nounChecker = true;
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
	}
}
