package com.test.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import com.opencsv.CSVWriter;
import com.test.exception.FileStorageException;
import com.test.model.ErrorFile;
import com.test.model.UserBulkImport;

@Component
public class FileServerWrapperService {

	public void logErrorInFile(List<ErrorFile> errorFiles, UserBulkImport bulkImport) {
		try {
			Path fileUploadPath = Paths.get(bulkImport.getFolderName());
			if (!(new File(fileUploadPath.toString())).exists())
				Files.createDirectories(fileUploadPath);
			File file = new File(bulkImport.getFolderName() + File.separator + bulkImport.getModifiedFileName());
			FileWriter outputfile = new FileWriter(file);
			CSVWriter writer = new CSVWriter(outputfile);
			String[] header = { "Email", "Name", "Roles", "Errors" };
			writer.writeNext(header);
			for (ErrorFile errorFile : errorFiles) {
				String[] data1 = { errorFile.getEmail(), errorFile.getName(), errorFile.getRoles(),
						errorFile.getError() };
				writer.writeNext(data1);
			}
			writer.close();
		} catch (IOException ex) {
			throw new FileStorageException(
					"Could not store file " + bulkImport.getModifiedFileName() + ". Please try again!", ex);
		}
	}

	public File getFile(UserBulkImport fileDetail) {
		try {
			Path fileUploadPath = Paths
					.get(fileDetail.getFolderName() + File.separator + fileDetail.getModifiedFileName());
			Resource resource = new UrlResource(fileUploadPath.toUri());
			if (resource.exists()) {
				return resource.getFile();
			} else {
				throw new FileNotFoundException("File not found in file Server " + fileUploadPath);
			}
		} catch (Exception ex) {
			throw new FileStorageException("File not found ", ex);
		}
	}

	public void delete(UserBulkImport fileDetail) {
		try {
			Path fileUploadPath = Paths
					.get(fileDetail.getFolderName() + File.separator + fileDetail.getModifiedFileName());
			Resource resource = new UrlResource(fileUploadPath.toUri());
			if (resource.exists()) {
				resource.getFile().delete();
			} else {
				throw new FileNotFoundException("File not found in file Server " + fileUploadPath);
			}
		} catch (Exception ex) {
			throw new FileStorageException("File not found ", ex);
		}
	}

}
