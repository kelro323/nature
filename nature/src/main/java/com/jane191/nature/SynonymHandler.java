package com.jane191.nature;

public class SynonymHandler {
	public SynonymOutput synonym(String key) {
		SynonymEntry entry = DictionaryUtil.getSynonym(key);
		if(entry==null) return null;
		SynonymOutput syOutput = new SynonymOutput(entry.getCategory(), entry.getDetail(),
				entry.getDegree());
		return syOutput;
	}
}
