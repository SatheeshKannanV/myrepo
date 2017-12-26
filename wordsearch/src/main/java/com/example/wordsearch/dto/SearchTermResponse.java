package com.example.wordsearch.dto;

public class SearchTermResponse {	
	
	private String statusMessage;
	
	private SearchKeyword searchKeyword;


	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public SearchKeyword getSearchKeyword() {
		return searchKeyword;
	}

	public void setSearchKeyword(SearchKeyword searchKeyword) {
		this.searchKeyword = searchKeyword;
	}

}
