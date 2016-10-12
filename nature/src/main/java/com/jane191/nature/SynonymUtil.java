package com.jane191.nature;

import org.apache.lucene.analysis.ko.utils.DictionaryUtil;
import org.apache.lucene.analysis.ko.utils.SynonymEntry;

public class SynonymUtil {
	public static SynonymOutput synonym(String key) {
		SynonymEntry entry = DictionaryUtil.getSynonym(key);
		if(entry==null) {
			SynonymOutput newOutput = new SynonymOutput("new", key, 0);
			return newOutput;
		}
		SynonymOutput syOutput = new SynonymOutput(entry.getCategory(), entry.getDetail(),
				entry.getDegree());
		return syOutput;
	}
}
