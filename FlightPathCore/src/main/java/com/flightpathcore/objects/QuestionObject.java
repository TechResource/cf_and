package com.flightpathcore.objects;

public class QuestionObject {
	public String question;
	public String answer;
	
	public QuestionObject(){
		
	}
	
	public QuestionObject(String q, boolean answerB){
		this.question = q;
		this.answer = answerB ? "Yes" : "No";
	}
}
