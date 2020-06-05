package com.test.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.test.model.OutputFileDto;
import com.test.model.UserBulkImport;

public interface BulkImportService {

	public List<UserBulkImport> findAll();

	public UserBulkImport getById(String roleId);

	public int delete(String id);
	
	public OutputFileDto fetchFile(String fileDetailId);

	public Map<UserBulkImport, UserBulkImportResponse> bulkImportSave(MultipartFile uploadfile);
}
