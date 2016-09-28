package com.jane191.nature;

import java.util.HashSet;
import java.util.List;

import org.apache.lucene.analysis.ko.morph.AnalysisOutput;
import org.apache.lucene.analysis.ko.morph.PatternConstants;

public class SubVerbUtil {
	private static final HashSet<String> subVerb = new HashSet<String>();
	
	static {
		String[] subVerbs = new String[] {"가다","가지다","나다","내다","놓다","대다","두다","말다","먹다",
				"못하다","버리다","보다","쌓다","않다","오다","있다","주다","하다"};
		for(String s : subVerbs) {
			subVerb.add(s);
		}
	};
	//의미의 차이가 없이 그냥 삭제가 가능한 경우 하나로 묶으면 될듯
	//equals 이랑 endsWith 조건에 대해선 좀 더 검수가 필요함
	public static void checkSubVerb(List<AnalysisOutput> target, List<AnalysisOutput> former) {
		int size = target.size();
		for(int i=0; i<size;i++) {
			if(subVerb.contains(target.get(i).getStem())
					&& target.get(i).getUsedPos()==PatternConstants.POS_VERB) {
				String check = former.get(0).getSource()
						.substring(former.get(0).getStem().length());
				if(target.get(i).getStem().equals("보다")) {
					if(check.equals("어")){
						target.get(i).setUsedPosType('b'); //의미가 약한 보조 동사(생략 가능)
					} else if(check.equals("고")||check.equals("다")||check.equals("다가")) {
						target.get(i).setUsedPosType('b'); //앞말이 뒷말의 원인(?)
					} else if(check.endsWith("은가") || check.endsWith("는가")
							||check.endsWith("나")||check.endsWith("까")) {
						target.get(i).setUsedPosType('b'); //의도 추측의 의미(보조형용사3번 분류 필요)
					} 
				} else if(target.get(i).getStem().equals("가다")) {
					if(check.endsWith("어")) {
						target.get(i).setUsedPosType('b'); //진행의 의미
					}
				} else if(target.get(i).getStem().equals("가지다")) {
					if(check.endsWith("어")) {
						target.get(i).setUsedPosType('b'); //생략 가능 보조동사
					}
				} else if(target.get(i).getStem().equals("나다")) {
					if(check.equals("어")||check.equals("고")) {
						target.get(i).setUsedPosType('b'); //행동의 종결 의미
					}
				} else if(target.get(i).getStem().equals("내다")) {
					if(check.equals("어")) {
						target.get(i).setUsedPosType('b'); //생략가능
					}
				} else if(target.get(i).getStem().equals("놓다")) {
					if(check.equals("어")) {
						target.get(i).setUsedPosType('b'); //2번은 생략 가능 1번은 유지의 의미
					}
				} else if(target.get(i).getStem().equals("대다")
						||target.get(i).getStem().equals("두다")
						||target.get(i).getStem().equals("버리다")
						||target.get(i).getStem().equals("오다")
						||target.get(i).getStem().equals("주다")) {
					if(check.equals("어")||former.get(0).getEomi().endsWith("어")) {
						target.get(i).setUsedPosType('b'); //생략가능
					}
				} else if(target.get(i).getStem().equals("먹다")) {
					if(check.equals("어")||former.get(0).getEomi().endsWith("어")) {
						target.get(i).setUsedPosType('b'); //강조의 의미
				} else if(target.get(i).getStem().equals("말다")) {
				}
					if(check.equals("지")) {
						target.get(i).setUsedPosType('b'); //부정의 의미
					} else if(check.equals("고")||check.equals("고야")) {
						target.get(i).setUsedPosType('b');
					}
				} else if(target.get(i).getStem().equals("못하다")
						||target.get(i).getStem().equals("않다")) {
					if(check.equals("지")) {
						target.get(i).setUsedPosType('b');//부정의 의미(보조동사+보조형용사)
					}
				} else if(target.get(i).getStem().equals("쌓다")) {
					if(check.equals("어")) {
						target.get(i).setUsedPosType('b');//강조의 의미
					}
				} else if(target.get(i).getStem().equals("있다")) {
					if(check.equals("어")||check.equals("고")) {
						target.get(i).setUsedPosType('b');//진행의 의미
					}
				} else if(target.get(i).getStem().equals("하다")) {
					if(check.equals("게")) {
						target.get(i).setUsedPosType('b');//명령의 의미
					} else if(check.endsWith("으면")) {
						target.get(i).setUsedPosType('b');//바람의 의미
					} else if(check.endsWith("야")) {
						target.get(i).setUsedPosType('b');//필요의 의미
					} else if(check.endsWith("려고")||check.endsWith("으려")
							||check.endsWith("으려고")||check.endsWith("고자")) {
						target.get(i).setUsedPosType('b');//의도 or 바람의 의미
					} else if(check.endsWith("기는")||check.endsWith("기도")||check.endsWith("기나")) {
						target.get(i).setUsedPosType('b'); //강조의 의미
					} else if(check.endsWith("고")) {
						target.get(i).setUsedPosType('b');//이유나 근거의 의미
					} else if(check.endsWith("고는")||check.endsWith("곤")) {
						target.get(i).setUsedPosType('b');//반복의 의미
					}
				} 
			}
		}
	}
}
