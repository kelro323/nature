package com.jane191.nature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.lucene.analysis.ko.morph.AnalysisOutput;
import org.apache.lucene.analysis.ko.morph.PatternConstants;
import org.apache.lucene.analysis.ko.morph.WordEntry;
import org.apache.lucene.analysis.ko.utils.DictionaryUtil;

public class PostProcessUtil {
	/** 주격 조사 :
	 * "이","가","께서","에서","은","는","도","만" */
	private static final HashSet<String> nomiJosa = new HashSet<String>();
	/** 목적격 조사	 :
	 * "을","를","은","는","도","만" */
	private static final HashSet<String> objJosa = new HashSet<String>();
	/** 종결 어미 :
	 *  "ㄴ다","는다","ㅂ니다","습니다","니라","다"*/
	private static final HashSet<String> endEomi = new HashSet<String>();
	/** 연결 어미 :
	 * "고","며","으며","면서","어서"*/
	private static final HashSet<String> conEomi = new HashSet<String>();
	/** 연결 조사 :
	 * "과", "와"*/
	private static final HashSet<String> conJosa = new HashSet<String>();
	/** 관형사형 어미 :
	 * "는","은","ㄴ","을","ㄹ","던"*/
	private static final HashSet<String> adnomiEomi = new HashSet<String>();
	/** 부사격 조사 :
	 * "에","에서","에게","와","과","으로","로" */
	private static final HashSet<String> busaJosa = new HashSet<String>();
	/** 부사형 어미 :
	 * "도록", "게"*/
	private static final HashSet<String> busaEomi = new HashSet<String>();
	/** 서술격 조사 :
	 * "이다", "이면", "이고", "이니", "이지"*/
	private static final HashSet<String> deJosa = new HashSet<String>();
	/** Vnj와 NJ 같은 의미인 경우 : 돕+ㅁ & 도움 // 그리+ㅁ & 그림
	 */
	private static final HashSet<String> sameMean = new HashSet<String>();
	
	static {
		String[] nomis = new String[] {"이","가","께서","에서","은","는","도","만"}; //주격 조사
		String[] objs = new String[] {"을","를","은","는","도","만"}; //목적격 조사
		String[] ends = new String[] {"ㄴ다","는다","ㅂ니다","습니다","니라","다"}; //종결 어미
		String[] cone = new String[] {"고","며","으며","면서","어서"}; //연결 어미
		String[] conj = new String[] {"과", "와"}; //연결 조사
		String[] adnomis = new String[] {"는","은","ㄴ","을","ㄹ","던"}; //관형사형 어미
		//관형사형 어미를 체크하는게 필요없을 가능성이 보임.
		String[] busaj = new String[] {"에","에서","에게","와","과","으로","로"}; //부사격 조사
		String[] busae = new String[] {"도록", "게"}; //부사형 어미
		String[] dej = new String[] {"이다", "이면", "이고", "이니", "이지"}; //서술격 조사
		
		String[] same = new String[] {"도움", "그림"}; // 같은 의미
		
		addList(nomiJosa,nomis);
		addList(objJosa,objs);
		addList(endEomi,ends);
		addList(conEomi,cone);
		addList(conJosa,conj);
		addList(adnomiEomi,adnomis);
		addList(busaJosa,busaj);
		addList(busaEomi,busae);
		addList(deJosa, dej);
		addList(sameMean, same);
	};
	
	public static void noDicCase(List<List<AnalysisOutput>> outList) {
		for(int i=0; i<outList.size(); i++) {
			if(outList.get(i).size()>1) {
				List<AnalysisOutput> preList = outList.get(i);
				int count = 0;
				for(AnalysisOutput pre : preList) {
					if(pre.getUsedPosType() == '@') count += 1;
				}
				if(count == preList.size()) {
					List<AnalysisOutput> tempList = new ArrayList<AnalysisOutput>();
					outList.remove(i);
					tempList.add(preList.get(0));
					outList.add(i, tempList);
				}
				
			}
		}
	}
	/**
	 * index-1 & index+1의 결과값을 보고 index의 결과값을 결정
	 * ex) VM vs NJ의 경우 index-1이 N이면 VM으로 판단, V이면 NJ로 판단 함.
	 */
	public static void selectResults(List<List<AnalysisOutput>> outList, int index) {
		AnalysisOutput foreAnal = new AnalysisOutput();
		List<AnalysisOutput> nextList = new ArrayList<AnalysisOutput>();
		if(index > 0) {
			System.out.println(outList.get(index-1));
			System.out.println(outList.get(index).get(0));
			foreAnal = outList.get(index-1).get(0);
		}
		if(index < outList.size()-1) {
			nextList = outList.get(index+1);
		}
		//관형사 체크를 위한 어미, 관형사형 조사는 "의" 밖에 없으므로 따로
		if(foreAnal.getJosa()!=null&&(foreAnal.getJosa().equals("의")||foreAnal.getJosa().equals("들의"))) {
			if(index>1) foreAnal = outList.get(index-2).get(0);
			else {
				foreAnal.setUsedPos('A');
				foreAnal.setScore(AnalysisOutput.SCORE_FAIL);
			}
		}
		/* 관형어미도 체크하는건 V N 체크하는데 의미가 없어서 우선 삭제 
		if(adnomiEomi.contains(foreAnal.getEomi())||(foreAnal.getJosa()!=null&&foreAnal.getJosa().equals("의"))) {
			if(index>1) foreAnal = outList.get(index-2).get(0);
			else {
				foreAnal.setUsedPos('A'); //관형사로 포스 셋
				foreAnal.setScore(AnalysisOutput.SCORE_FAIL); //실패 스코어로 변경
			}
		}*/
		List<AnalysisOutput> preList = outList.get(index);
		if(sameMeaning(preList) != 0) {
			if(sameMeaning(preList) == 1) {
				preList.remove(0);
				outList.remove(index);
				outList.add(index, preList);
			} else {
				preList.remove(1);
				outList.remove(index);
				outList.add(index, preList);
			}
			return;
		}
		if(foreAnal.getScore()> AnalysisOutput.SCORE_FAIL && nextList!=null) {
			if(foreAnal.getUsedPos()==PatternConstants.POS_NOUN) {
				for(AnalysisOutput pre : preList) {
					if(pre.getStem().equals("토대") && pre.getUsedPos()==PatternConstants.POS_NOUN) {
						outList.remove(index);
						List<AnalysisOutput> tempList = new ArrayList<AnalysisOutput>();
						tempList.add(pre);
						outList.add(index, tempList);
						return;
					}
				}
				//명사 케이스
				nounCase(foreAnal, preList, nextList, outList, index);
			} else if(foreAnal.getUsedPos()==PatternConstants.POS_VERB) {
				//동사 케이스
				verbCase(foreAnal, preList, nextList, outList, index);
			} else if(foreAnal.getUsedPos()==PatternConstants.POS_AID) {
				//부사 케이스
				busaCase(foreAnal, preList, nextList, outList, index);
			}
		} else if(nextList!=null) {
			//관형격일때
			if(foreAnal.getUsedPos()=='A') {
				adnomiCase(preList, nextList, outList, index);
			} else { //처음일때
				firstCase(preList, nextList, outList, index);
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
			List<List<AnalysisOutput>> outList, int index) {
		List<AnalysisOutput> tempList = new ArrayList<AnalysisOutput>();
		//조사가 존재하고, 그 조사가 주격 조사나 서술격 조사가 아닐때
		if(foreAnal.getJosa()!=null 
				&& !(nomiJosa.contains(foreAnal.getJosa())||deJosa.contains(foreAnal.getJosa())||conJosa.contains(foreAnal.getJosa()))) {
			for(AnalysisOutput pre : preList) {
				if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
					tempList.add(pre);
				}
			}
			for(AnalysisOutput temp : tempList) {
				preList.remove(temp);
			}
			//tempList의 명사 제거 목적은 완료, 밑에서는 결과값 임시 저장을 위해서 사용.
			if(preList.size()>1) {
				boolean detBusa = false;
				for(AnalysisOutput ne : nextList) {
					if(ne.getUsedPos()==PatternConstants.POS_NOUN) break;
					if(ne.getUsedPos()==PatternConstants.POS_VERB) {
						//의존 명사에 의한 '것이다' '곳이다' 등등에 대한 거에 대한 고찰 필요.
						if(!busaEomi.contains(ne.getEomi()) && 
								!endEomi.contains(ne.getEomi()) && !adnomiEomi.contains(ne.getEomi())) {
							detBusa = true;
						}
					}
				}
				List<AnalysisOutput> tempBusaList = new ArrayList<AnalysisOutput>();
				List<AnalysisOutput> tempVerbList = new ArrayList<AnalysisOutput>();
				for(AnalysisOutput pre : preList) {
					if(pre.getUsedPos()==PatternConstants.POS_AID || busaEomi.contains(pre.getEomi())) {
						tempBusaList.add(pre);
					} else tempVerbList.add(pre);
				}
				if(detBusa) tempList = tempBusaList;
				else tempList = tempVerbList;
			} else tempList = preList;
			outList.remove(index);
			outList.add(index, removeSame(tempList));
		} else if(nomiJosa.contains(foreAnal.getJosa())) { 
			// 주격 조사일때
			// 동사의 자동사, 타동사 형도 고려하여 선택하는 part 추가 해야할듯
			// 관형사형 어미는 이럴때 쓰임
			// '지식이 아니라 지혜다'의 '아니'는 아니다의 쓰임새, '내가 아는 지식은'에서 '아는'은 알다의 쓰임새
			// 같은 N+주격조사 - 동사의 형태이나 아니다는 자동사로 목적어가 필요없으므로 1번에선 아니라 선택,
			// 2번에선 '-는'이 관형사형 어미로서 알다가 관형어로 쓰여서 '지식은'을 수식. 이런 케이스들을 고려해야할 듯
			for(AnalysisOutput pre : preList) {
				if(pre.getUsedPos()==PatternConstants.POS_NOUN && 
						(!deJosa.contains(pre.getJosa())&&!objJosa.contains(pre.getJosa())&&pre.getPatn()!=PatternConstants.PTN_N)) {
					tempList.add(pre);
				} else if(pre.getEomi()!=null) {
					if(index != outList.size()-1) {
						if(endEomi.contains(pre.getEomi())) {
							tempList.add(pre);
						}
					} else {
						if(!"기".equals(pre.getEomi())) {
							tempList.add(pre);
						}
					}
				}
			}
			for(AnalysisOutput temp : tempList) {
				preList.remove(temp);
			}
			outList.remove(index);
			outList.add(index, preList);
		} else if(deJosa.contains(foreAnal.getJosa())) {
			// 서술격 조사일때
			for(AnalysisOutput pre : preList) {
				if(pre.getUsedPos()==PatternConstants.POS_NOUN && !nomiJosa.contains(pre.getJosa())) {
					tempList.add(pre);
				}
			}
			for(AnalysisOutput temp : tempList) {
				preList.remove(temp);
			}
			outList.remove(index);
			outList.add(index, preList);
		} else if(conJosa.contains(foreAnal.getJosa())) {
			// 연결 조사일때 
			for(AnalysisOutput pre : preList) {
				if("함께".equals(pre.getStem()) && pre.getUsedPos()==PatternConstants.POS_AID) {
					tempList.add(pre);
					break;
				}
				if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
					tempList.add(pre);
				}
			}
			outList.remove(index);
			outList.add(index, removeSame(tempList));
		} else if(foreAnal.getEomi()!=null) {
			//-기 어미에 의해서 명사형으로 취급될때
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
					if(ne.getUsedPos()==PatternConstants.POS_VERB) {
						//의존 명사에 의한 '것이다' '곳이다' 등등에 대한 거에 대한 고찰 필요.
						if(!busaEomi.contains(ne.getEomi()) && !endEomi.contains(ne.getEomi())) {
							detBusa = true;
						}
					}
				}
				List<AnalysisOutput> tempBusaList = new ArrayList<AnalysisOutput>();
				List<AnalysisOutput> tempVerbList = new ArrayList<AnalysisOutput>();
				for(AnalysisOutput pre : preList) {
					if(pre.getUsedPos()==PatternConstants.POS_AID || busaEomi.contains(pre.getEomi())) {
						tempBusaList.add(pre);
					} else tempVerbList.add(pre);
				}
				if(detBusa) tempList = tempBusaList;
				else tempList = tempVerbList;
			} else tempList = preList;
			outList.remove(index);
			outList.add(index, removeSame(tempList));
		} else {
			// 조사가 없을때
			for(AnalysisOutput pre : preList) {
				if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
					tempList.add(pre);
				}
				if(pre.getUsedPos()==PatternConstants.POS_AID) {
					for(AnalysisOutput ne : nextList) {
						if(ne.getPatn()==PatternConstants.PTN_N) {
							tempList.add(pre);
						}
					}
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
			if(busaEomi.contains(foreAnal.getEomi())) {
				//뒤에 명사형, 동사형 둘 다 올 수도 있는 상황. 뒤에 구절이 타동사로 이루어지면 목적어가 들어와 명사형이 나타나고
				//자동사인 경우 바로 동사형이 선택.
				for(AnalysisOutput ne : nextList) {
					if(ne.getUsedPos()==PatternConstants.POS_VERB) {
						if(ne.getUsedPosType()=='t' || ne.getUsedPosType()=='k') {
							for(AnalysisOutput pre : preList) {
								if(pre.getUsedPos()==PatternConstants.POS_NOUN && 
										objJosa.contains(pre.getJosa())) {
									tempNounList.add(pre);
								}
							}
						} else if(ne.getUsedPosType()=='i' || ne.getUsedPosType()=='d') {
							for(AnalysisOutput pre : preList) {
								if(pre.getUsedPos()==PatternConstants.POS_AID) {
									tempVerbList.add(pre);
								}
							}
						} else if(ne.getUsedPosType()=='v') {
							for(AnalysisOutput pre : preList) {
								if(pre.getUsedPos()==PatternConstants.POS_NOUN && objJosa.contains(pre.getJosa())) {
									tempNounList.add(pre);
								} else if(pre.getUsedPos()==PatternConstants.POS_AID) {
									tempVerbList.add(pre);
								}
							}
						} else if(ne.getUsedPosType()=='b') {
							for(AnalysisOutput pre : preList) {
								if(pre.getUsedPos()==PatternConstants.POS_VERB) {
									tempVerbList.add(pre);
								}
							}
						}
					} else if(ne.getUsedPos()==PatternConstants.POS_AID) {
						for(AnalysisOutput pre : preList) {
							if(pre.getUsedPos()==PatternConstants.POS_VERB) {
								tempVerbList.add(pre);
							}
						}
					} else if(ne.getUsedPos()==PatternConstants.POS_NOUN) {
						for(AnalysisOutput pre : preList) {
							if(pre.getUsedPosType()=='n'||pre.getUsedPosType()=='p') {
								tempVerbList.add(pre);
							} else if(adnomiEomi.contains(pre.getEomi())) {
								tempVerbList.add(pre);
							} else if(pre.getPatn()==PatternConstants.PTN_N) {
								tempNounList.add(pre);
							} else if(pre.getPatn()==PatternConstants.PTN_NJ && pre.getJosa().equals("의")) {
								tempNounList.add(pre);
							}
						}
					}
				}
			} else if(foreAnal.getUsedPosType()=='b') {
				//보조 동사 파트
				for(AnalysisOutput pre : preList) {
					if(pre.getUsedPos()==PatternConstants.POS_VERB && pre.getUsedPosType()=='b') {
						tempVerbList.add(pre);
					}
					else if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
						tempNounList.add(pre);
					} 
				}
				
			} else if(conEomi.contains(foreAnal.getEomi())) {
				//연결 어미일 경우 둘다 가능해서 아직 정하지 못해 그냥 다 tempList에 넣는걸로 함.
				//연결 어미가 "고"의 경우 보조 동사의 앞에 오는 동사형태에서 종종 보이므로, 이 경우 현재 토큰이 보조동사 인지 확인하고
				//보조 동사가 있는지 확인되면 바로 루프문 종료, 보조동사 결과값만을 나타내기로 함
				if("고".equals(foreAnal.getEomi())) {
					for(AnalysisOutput pre : preList) {
						if(pre.getUsedPos()==PatternConstants.POS_VERB && pre.getUsedPosType()=='b') {
							tempVerbList.add(pre);
							break;
						} else {
							tempNounList.add(pre);
						}
					}
				} else {
					for(AnalysisOutput pre : preList) {
						tempNounList.add(pre);
					}
				}
			} else {
				for(AnalysisOutput pre : preList) {
					if(pre.getUsedPos()==PatternConstants.POS_VERB && pre.getUsedPosType()=='b') {
						tempVerbList.add(pre);
						break;
					}
					if(pre.getUsedPos()==PatternConstants.POS_VERB && adnomiEomi.contains(pre.getEomi())) {
						tempVerbList.add(pre);
						break;
					}
					if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
						tempNounList.add(pre);
					} 
				}
			}
			//어떤 List를 선택해야 하는 것에 대한 건 고민이 필요함
			outList.remove(index);
			if(tempNounList.size() >= tempVerbList.size()) outList.add(index, removeSame(tempNounList));
			else outList.add(index, removeSame(tempVerbList));
		}
	}
	
	private static void busaCase(AnalysisOutput foreAnal, List<AnalysisOutput> preList,
			List<AnalysisOutput> nextList, List<List<AnalysisOutput>> outList, int index) {
		List<AnalysisOutput> tempList = new ArrayList<AnalysisOutput>();
		if(foreAnal.getUsedPosType()=='c') {
			for(AnalysisOutput pre : preList) {
				if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
					tempList.add(pre);
					outList.remove(index);
					outList.add(index, removeSame(tempList));
					return;
				}
			}
		}
		for(AnalysisOutput ne : nextList) {
			if(ne.getUsedPos()==PatternConstants.POS_NOUN) {
				for(AnalysisOutput pre : preList) {
					if(pre.getUsedPos()==PatternConstants.POS_VERB) {
						tempList.add(pre);
					}
				}
			} else if(ne.getUsedPos()==PatternConstants.POS_VERB) {
				for(AnalysisOutput pre : preList) {
					if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
						tempList.add(pre);
					}
				}
			}
		}
		outList.remove(index);
		outList.add(index, removeSame(tempList));
	}
	
	private static void firstCase(List<AnalysisOutput> preList, List<AnalysisOutput> nextList,
			List<List<AnalysisOutput>>outList , int index) {
		List<AnalysisOutput> tempList = new ArrayList<AnalysisOutput>();
		for(AnalysisOutput ne : nextList) {
			if(ne.getUsedPos()==PatternConstants.POS_NOUN) {
				for(AnalysisOutput pre : preList) {
					//현재가 관형사일때
					if((pre.getUsedPos()==PatternConstants.POS_AID&&
							(pre.getUsedPosType()=='d'||pre.getUsedPosType()=='n'||pre.getUsedPosType()=='p')||pre.getUsedPosType()=='1')
							||"의".equals(pre.getJosa())) { //(adnomiEomi.contains(pre.getEomi()) 판단 부분 제외 //postype 미 완료로 1 추가
						tempList.add(pre);
					//현재가 접속부사일때
					} else if(pre.getUsedPosType()=='c'&&pre.getUsedPos()==PatternConstants.POS_AID) {
						tempList.add(pre);
					//현재가 단독 명사일때(고찰이 필요함)
					} else if(pre.getUsedPos()==PatternConstants.POS_NOUN &&
							pre.getPatn()==PatternConstants.PTN_N) {
						tempList.add(pre);
					} else if(pre.getUsedPos()==PatternConstants.POS_NOUN && 
							nomiJosa.contains(pre.getJosa())) {
						tempList.add(pre);
					}
				}
			} else if(ne.getUsedPos()==PatternConstants.POS_AID) {
				for(AnalysisOutput pre : preList) {
					if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
						tempList.add(pre);
					}
				}
			} else if(ne.getUsedPos()==PatternConstants.POS_VERB) {
				if(ne.getStem().equals("위하")) {
					for(AnalysisOutput pre : preList) {
						if(pre.getUsedPos()==PatternConstants.POS_NOUN && 
								(pre.getJosa().equals("을")||pre.getJosa().equals("를"))) {
							tempList.add(pre);
						}
					}
				} else {
					for(AnalysisOutput pre : preList) {
						if(pre.getUsedPos()==PatternConstants.POS_NOUN && nomiJosa.contains(pre.getJosa())) {
							tempList.add(pre);
						} else if(pre.getUsedPos()==PatternConstants.POS_AID) {
							tempList.add(pre);
						}
					}
				}
				
			}
		}
		outList.remove(index);
		outList.add(index, removeSame(tempList));
	}
	
	private static void lastCase(List<AnalysisOutput> preList, List<List<AnalysisOutput>>outList,
			int index) {
		List<AnalysisOutput> tempList = new ArrayList<AnalysisOutput>();
		for(AnalysisOutput pre : preList) {
			if(endEomi.contains(pre.getEomi())
					||"이다".equals(pre.getJosa())||"이지".equals(pre.getJosa())) {
				//이다 의 경우 보조동사가 뒤에 오면 종결 어미가 아니므로 고려해야하는가...
				tempList.add(pre);
			}
		}
		outList.remove(index);
		outList.add(index, tempList);
	}
	
	private static void adnomiCase(List<AnalysisOutput> preList, List<AnalysisOutput> nextList,
			List<List<AnalysisOutput>>outList, int index) {
		List<AnalysisOutput> tempList = new ArrayList<AnalysisOutput>();
		for(AnalysisOutput pre : preList) {
			if(pre.getUsedPos()==PatternConstants.POS_NOUN && nomiJosa.contains(pre.getJosa()))  {
				tempList.add(pre);
			} else if(pre.getUsedPos()==PatternConstants.POS_NOUN) {
				for(AnalysisOutput ne : nextList) {
					if(ne.getUsedPos()==PatternConstants.POS_VERB) {
						tempList.add(pre);
					}
				}
			} else if(pre.getUsedPos()==PatternConstants.POS_VERB) {
				for(AnalysisOutput ne : nextList) {
					if(ne.getUsedPos()==PatternConstants.POS_NOUN) {
						tempList.add(pre);
					}
				}
			} else {
				for(AnalysisOutput ne : nextList) {
					if(ne.getUsedPos()==PatternConstants.POS_VERB) {
						tempList.add(pre);
					}
				}
			}
		}
		outList.remove(index);
		outList.add(index, removeSame(tempList));
	}
	
	private static List<AnalysisOutput> removeSame(List<AnalysisOutput> list) {
		return new ArrayList<AnalysisOutput>(new LinkedHashSet<AnalysisOutput>(list));
	}
	
	private static int sameMeaning(List<AnalysisOutput> preList) {
		int count = 0;
		for(AnalysisOutput pre : preList) {
			if(sameMean.contains(pre.getStem())) return 1;
			if(pre.getUsedPos()==PatternConstants.POS_AID && 
					pre.getScore()==AnalysisOutput.SCORE_CORRECT) count += 1;
		}
		if(count== preList.size()) return 2;
		return 0;
	}
	/**
	 * Analysis Score 80점 이상만 고려하기 위해서
	 * 만약 결과값이 다 80점 아래라면 그대로 출력
	 */
	public static List<List<AnalysisOutput>> scoreSelect(List<List<AnalysisOutput>> result) {
		List<List<AnalysisOutput>> outList = new ArrayList<List<AnalysisOutput>>();
		for(List<AnalysisOutput> tempList : result) {
			List<AnalysisOutput> tempResult = new ArrayList<AnalysisOutput>();
			int count = 0;
			for(AnalysisOutput temp : tempList) {
				if(temp.getScore()>30)  {
					tempResult.add(temp);
					count++;
				}
			}
			if(count>0) outList.add(tempResult);
			else outList.add(tempList);
		}
		return outList;
	}
	/**
	 -기 어미에 의해서 명사형인 토큰을 찾아서 UsedPos 변경 N으로 변경
	 (morphAnalyzer에서 변경 가능하면 그 쪽에서 하는게 이득일듯)
	 */
	public static List<List<AnalysisOutput>> nounEomi(List<List<AnalysisOutput>> list) {
		for(List<AnalysisOutput> tempList : list) {
			for(AnalysisOutput temp : tempList) {
				if("기".equals(temp.getEomi())) {
					if(!"보".equals(temp.getStem())) {
						temp.setUsedPos(PatternConstants.POS_NOUN);
					}	
				}
				if("는가에".equals(temp.getEomi())) {
					temp.setUsedPos(PatternConstants.POS_NOUN);
					temp.setJosa("에");
					temp.setStem(temp.getSource().substring(0,temp.getSource().length()-1));
				}
			}
		}
		return list;
	}
	
	/**
	 -히로 끝나는 일반 부사형은 -히의 어간에 따라 속성값이 정해짐
	 (N(혹은 V,Z)+히 형태로 변경)
	 */
	public static AnalysisOutput hiCase(AnalysisOutput anal) {
		if(anal.getPatn()==PatternConstants.PTN_AID && anal.getStem().endsWith("히")) {
			String temp = anal.getStem().substring(0, anal.getStem().length()-1);
			WordEntry entry = DictionaryUtil.getWord(temp);
			if(entry!=null) {
				//조사 대신 접사에 -히를 넣어야하는데 이건 아직 추가안함.
				//성실히 같이 XX -> XX히가 되는 것
				AnalysisOutput output = new AnalysisOutput(temp,"히",null,PatternConstants.PTN_AID);
				output.setScore(AnalysisOutput.SCORE_CORRECT);
				output.setPos(PatternConstants.POS_AID);
				output.setUsedPos(PatternConstants.POS_AID);
				output.setPosType(entry.getFeature(WordEntry.IDX_NOUN));
				return output;
			} else { 
				//부단히 같이 XX하다 -> XX히의 형태로 부사형이 되는 것
				WordEntry entry2 = DictionaryUtil.getWord(temp+"하");
				if(entry2!=null) {
					AnalysisOutput output = new AnalysisOutput(temp,"히",null,PatternConstants.PTN_AID);
					output.setScore(AnalysisOutput.SCORE_CORRECT);
					output.setPos(PatternConstants.POS_AID);
					output.setUsedPos(PatternConstants.POS_AID);
					//이 경우 PosType은 기존 entry가 아닌 다른 사전에서 추출하는게 옳을 듯, 기존 entry는 XX의 뜻이 담긴 정보가 없고
					//V카테고리의 경우 자,타,형용사 구별을 위한 정보만 있어서 속성값에 관한 정보가 없으므로 다른 사전에서 추출해야함
					//아니면 V의 속성값이 있는 새로운 카테고리를 추가해야할듯
					output.setPosType(entry2.getFeature(WordEntry.IDX_NOUN));
					return output;
				} else return anal;
			}
		} else return anal;
	}
}
