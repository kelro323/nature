package com.jane191.nature;

public class SynonymOutput {
	
	private String category;
	
	private String detail;
	
	private int degree;
	
	public SynonymOutput() {	
	}
	
	public SynonymOutput(String category, String detail, int degree) {
		this.category = category;
		this.detail = detail;
		this.degree = degree;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public String getDetail() {
		return detail;
	}
	
	public void setDegree(int degree) {
		this.degree = degree;
	}
	
	public int getDegree() {
		return degree;
	}
}
