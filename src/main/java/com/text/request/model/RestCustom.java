package com.text.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Wrapped in Rest Entity, contains case of exception to be send as response
 * 
 * @author satyam.kumar
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestCustom {

	private String cause;

}