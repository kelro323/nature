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
	
	public static void determinSubVerb(List<List<AnalysisOutput>> outList, int index) {
		AnalysisOutput foreAnal = new AnalysisOutput();
		boolean verbCheck = false;
		if(index>0) {
			foreAnal = outList.get(index-1).get(0);
		}
		List<AnalysisOutput> preList = outList.get(index);
		if(foreAnal.getUsedPos()==PatternConstants.POS_VERB || foreAnal.getPatn()==PatternConstants.PTN_NJ) {
			if(foreAnal.getUsedPos()==PatternConstants.POS_VERB) verbCheck = true;
			for(AnalysisOutput pre : preList) {
				if(subVerb.contains(pre.getStem()+"다") 
						&& pre.getUsedPos()==PatternConstants.POS_VERB) {
					subVerbCheck(foreAnal, pre, verbCheck);
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static void subVerbCheck(AnalysisOutput foreAnal, AnalysisOutput pre, boolean verbCheck) {
		String foreEomi = "";
		String foreJosa = "";
		if(verbCheck) {
			if(foreEomi!=null) foreEomi = foreAnal.getEomi();
			else return;
		} else {
			if(foreJosa!=null) foreJosa = foreAnal.getJosa();
			else return;
		}
		if(pre.getStem().equals("가")) {
			if(foreEomi.equals("어")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("가지")) {
			if(foreEomi.equals("어")||foreEomi.equals("아")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("나")) {
			if(foreEomi.equals("어")||foreEomi.equals("고")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("내")) {
			if(foreEomi.equals("어")||foreEomi.equals("아")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("놓")) {
			if(foreEomi.equals("어")||foreJosa.equals("라")||foreJosa.equals("이어")) {
				pre.setUsedPosType('b');
			}
		} else if(pre.getStem().equals("대")) {
			if(foreEomi.equals("어")||foreEomi.equals("아")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("두")) {
			if(foreEomi.equals("어")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("말")) {
			if(foreEomi.equals("지")) pre.setUsedPosType('b');
			else if(foreEomi.equals("고")||foreEomi.equals("고야")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("먹")) {
			if(foreEomi.equals("어")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("못하")) {
			if(foreEomi.equals("지")) pre.setUsedPosType('b');
			else if(foreEomi.equals("다")||foreEomi.equals("다가")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("버리")) {
			if(foreEomi.equals("어")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("보")) {
			if(foreEomi.equals("어")) {
				pre.setUsedPosType('b');
			} else if(foreEomi.equals("고")||foreEomi.equals("다")||foreEomi.equals("다가")) {
				pre.setUsedPosType('b');
			} else if(foreEomi.equals("은가")||foreEomi.equals("는가")||foreEomi.equals("나")) {
				pre.setUsedPosType('b');
			} else if(foreEomi.equals("ㄹ까")||foreEomi.equals("을까")) {
				pre.setUsedPosType('b');
			} else if(foreJosa.equals("이다")) {
				if(pre.getEomi().equals("아")||pre.getEomi().equals("니")||pre.getEomi().equals("다")) {
					pre.setUsedPosType('b');
				}
			}
		} else if(pre.getStem().equals("쌓")) {
			if(foreEomi.equals("어")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("않")) {
			if(foreEomi.equals("지")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("오")) {
			if(foreEomi.equals("아")||foreEomi.equals("어")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("있")) {
			if(foreEomi.equals("아")||foreEomi.equals("어")) pre.setUsedPosType('b');
			else if(foreEomi.equals("고")) pre.setUsedPosType('b');
		} else if(pre.getStem().equals("주")) {
			if(foreEomi.equals("어")) pre.setUsedPosType('b');
		} else {
			if(foreEomi.equals("게")) pre.setUsedPosType('b');
			else if(foreEomi.equals("으면") && foreAnal.getPomi().equals("었")) pre.setUsedPosType('b');
			else if(foreEomi.equals("어야")) pre.setUsedPosType('b');
			else if(foreEomi.equals("으려")||foreEomi.equals("려")
					||foreEomi.equals("으려고")||foreEomi.equals("려고")||foreEomi.equals("고자")) {
				pre.setUsedPosType('b');
			} else if((foreEomi.equals("는")||foreEomi.equals("도")||foreEomi.equals("나"))) {
				if(foreAnal.getElist().size() > 0) {
					if("기".equals(foreAnal.getElist().get(0))) pre.setUsedPosType('b');
				}
			} else if(foreEomi.equals("고")) {
				if(pre.getEomi().equals("어서")||pre.getEomi().equals("어")||pre.getEomi().equals("니")) {
					pre.setUsedPosType('b');
				}
			} else if(foreEomi.equals("고는")||foreEomi.equals("곤")) pre.setUsedPosType('b');
		}
	}
}
