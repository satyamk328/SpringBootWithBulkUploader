package com.test.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBulkImportResponse {

	@JsonProperty("no_of_rows_parsed")
	private Long successRecord;
	
	@JsonProperty("no_of_rows_failed")
	private Long failedRecord;
	
	@JsonProperty("error_file_url")
	private String errorFileUrl;

}
