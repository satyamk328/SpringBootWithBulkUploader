package com.test.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.test.exception.BaseException;
import com.text.request.model.RestCustom;
import com.text.request.model.RestResponse;
import com.text.request.model.RestStatus;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { BaseException.class })
	protected ResponseEntity<Object> handleBussinessException(BaseException ex) {
		log.error(ex.getMessage());
		
		RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

	}

	
	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
			WebRequest request) {
		log.error(ex.getMessage());
		
		if (ex.getCause() instanceof ConstraintViolationException) {
			RestResponse<String> response = new RestResponse<>(null,
					new RestStatus<>(HttpStatus.CONFLICT, "Database error"));
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}
		RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.CONFLICT, ex.getLocalizedMessage()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(value = { Exception.class })
	protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
		log.error(ex.getMessage());
		
		RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error while performing Operation"),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		final RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getContentType());
		builder.append(" media type is not supported. Supported media types are ");
		ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

		final RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2)),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		final RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.NOT_ACCEPTABLE, ex.getMessage()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
	}

	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		final RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		String error = ex.getParameterName() + " parameter is missing";
		RestResponse<String> response = new RestResponse<>(null, new RestStatus<>(HttpStatus.BAD_REQUEST, error),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		final RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.BAD_REQUEST, ex.getMessage()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		final RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
			WebRequest request) {
		log.error(ex.getMessage());
		
		RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.BAD_REQUEST,
						String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
								ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName())),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		String error = "Unable to parse JSON request";
		RestResponse<String> response = new RestResponse<>(null, new RestStatus<>(HttpStatus.BAD_REQUEST, error),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		String error = "Error writing JSON output";
		RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.INTERNAL_SERVER_ERROR, error),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		List<String> errors = new ArrayList<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}

		RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.BAD_REQUEST, errors.toString()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		final RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.BAD_REQUEST, ex.getMessage()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		log.error(ex.getMessage());
		
		final RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.BAD_REQUEST, ex.getMessage()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		
		final RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.NOT_FOUND, ex.getMessage()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@Override
	protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
			HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
		log.error(ex.getMessage());
		
		final RestResponse<String> response = new RestResponse<>(null,
				new RestStatus<>(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage()),
				RestCustom.builder().cause(ex.toString()).build());
		return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
	}
}

