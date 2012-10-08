package com.carit.platform.response;

import java.util.List;

public class ErrorResponse {

	
	private String code;
	
	private String message;
	
	private String solution;
	
	
	private List<SubError> subErrors;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public List<SubError> getSubErrors() {
		return subErrors;
	}

	public void setSubErrors(List<SubError> subErrors) {
		this.subErrors = subErrors;
	}

	@Override
	public String toString() {
		return "ErrorResponse [code=" + code + ", message=" + message
				+ ", solution=" + solution + ", subErrors=" + subErrors + "]";
	}
	
}
