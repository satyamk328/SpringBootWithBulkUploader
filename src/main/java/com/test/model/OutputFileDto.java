package com.test.model;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutputFileDto {

	private File file;
	private String originalFileName;
	private String contentType;
	
}
