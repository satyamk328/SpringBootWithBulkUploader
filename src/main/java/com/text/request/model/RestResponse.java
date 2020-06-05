package com.text.request.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * Wrapper for rest response containing status and message
 * 
 * @author satyam.kumar
 *
 * @param <T>
 */
@Getter
@Setter
public class RestResponse<T> {

	private T data;
	private RestStatus<?> status;
	private RestCustom custom;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	private LocalDateTime timestamp = LocalDateTime.now();

	public RestResponse() {
	}

	public RestResponse(final T data, final RestStatus<?> status) {
		this.data = data;
		this.status = status;
	}

	public RestResponse(final T data) {
		this.data = data;
	}

	public RestResponse(final T data, final RestStatus<?> status, final RestCustom custom) {
		this.data = data;
		this.status = status;
		this.custom = custom;
	}
}
