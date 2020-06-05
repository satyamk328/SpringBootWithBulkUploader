package com.test.service.impl;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.test.model.ErrorFile;
import com.test.model.OutputFileDto;
import com.test.model.Role;
import com.test.model.User;
import com.test.model.UserBulkImport;
import com.test.repository.RoleRepository;
import com.test.repository.FileMetaDataRepository;
import com.test.repository.UserRepository;
import com.test.service.BulkImportService;
import com.test.service.UserBulkImportResponse;

@Service
public class BulkImportServiceImpl implements BulkImportService {

	@Autowired
	private FileMetaDataRepository bulkRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Value("${file.upload.root.path}")
	private String fileUploadRootDir;
	
	@Autowired
	private FileServerWrapperService fileWrapperService;

	@Override
	public List<UserBulkImport> findAll() {
		return bulkRepo.findAll();
	}

	@Override
	public UserBulkImport getById(String roleId) {
		UserBulkImport bulkImport = bulkRepo.findById(roleId).orElse(null);
		if (bulkImport == null)
			throw new ServiceException("BulkImport Id is invalid. Please enter valid id");
		return bulkImport;
	}

	@Override
	public int delete(String id) {
		UserBulkImport fileStorage = getById(id);
		bulkRepo.delete(fileStorage);
		fileWrapperService.delete(fileStorage);
		return 1;
	}

	@Override
	public Map<UserBulkImport, UserBulkImportResponse> bulkImportSave(MultipartFile uploadfile) {
		try {
			String extension = FilenameUtils.getExtension(uploadfile.getOriginalFilename());
			if (extension != null && !extension.equalsIgnoreCase("csv")) {
				throw new ServiceException("Invalid file type. Please select csv file");
			}

			CsvToBean<User> csv = new CsvToBean<>();

			CSVReader csvReader = new CSVReader(new InputStreamReader(uploadfile.getInputStream()));
			String[] column = csvReader.readNext();

			ColumnPositionMappingStrategy<User> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(User.class);
			mappingStrategy.setColumnMapping(column);
			csv.setCsvReader(csvReader);
			csv.setMappingStrategy(mappingStrategy);
			List<User> list = csv.parse();

			if (column.length == 1 && column[0].equals("")) {
				throw new ServiceException("Please select valid csv file");
			}
			List<String> exstingColumns = Arrays.asList("name", "email", "roles");
			// validate csv files
			exstingColumns
					.removeAll(Arrays.asList(column).stream().map(String::toUpperCase).collect(Collectors.toList()));
			if (!exstingColumns.isEmpty()) {
				throw new ServiceException("Mendatory columns are messing. Please import valid csv file");
			}

			List<ErrorFile> errorList = new ArrayList<>();
			for (User user : list) {

				StringBuffer buffer = new StringBuffer();
				if (StringUtils.isBlank(user.getName())) {
					logError(buffer, "User Name is blank");
				}
				if (StringUtils.isBlank(user.getEmail())) {
					logError(buffer, "User Email is blank");
				} else if (user.getEmail().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
					logError(buffer, "Email is invalid");
				} else if (StringUtils.isNoneBlank(user.getEmail())) {
					User user1 = this.userRepo.findByEmail(user.getEmail().toLowerCase()).orElse(null);
					if (user1 != null) {
						logError(buffer, "Email is already exist");
					}
				}

				if (StringUtils.isBlank(user.getRoles())) {
					logError(buffer, "Role is blank");
				} else {
					String[] role = user.getRoles().split("#");
					Boolean isValid = true;
					List<Role> rList = new ArrayList<>();
					for (String str : role) {
						Role r = roleRepo.findByName(str.toLowerCase()).orElse(null);
						if (r == null) {
							logError(buffer, "Role is invalid  " + str);
							isValid = false;
						}else {
							rList.add(r);
						}
					}
                    if(isValid) {
                    	 if (buffer.length() == 0) {
 							user.getRList().addAll(rList);
 							user.setPassword("Test@1234");
 							userRepo.save(user);
 						} else {
 							ErrorFile errorFile = new ErrorFile(user.getName(), user.getEmail(), user.getRoles(), buffer.toString());
 							errorList.add(errorFile);
 						}
                    }
				}
			}
			UserBulkImportResponse bulkImportResponse = new UserBulkImportResponse();
            bulkImportResponse.setSuccessRecord(new Long(list.size()-errorList.size()));
            bulkImportResponse.setSuccessRecord(new Long(errorList.size()));
            UserBulkImport bulkImport= createFileStorage(uploadfile);
            if(errorList.size() > 0)
            	fileWrapperService.logErrorInFile(errorList, bulkImport);
            Map<UserBulkImport, UserBulkImportResponse> map = new HashMap<>();
            map.put(bulkImport, bulkImportResponse);
			return map;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	private void logError(StringBuffer buffer, String str) {
		if (str.length() == 0) {
			buffer.append(str);
		} else {
			buffer.append("#");
			buffer.append(str);
		}
	}

	private UserBulkImport createFileStorage(MultipartFile file) {
		UserBulkImport temp = new UserBulkImport();

		Calendar calendar = Calendar.getInstance();
		String folderName = fileUploadRootDir + File.separator+ calendar.get(Calendar.YEAR) + File.separator
				+ calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		temp.setFolderName(folderName);
		temp.setActualFileName(file.getOriginalFilename());
		temp.setSize(file.getSize());
		temp.setFileType(file.getContentType());

		String ext = FilenameUtils.getExtension(temp.getActualFileName());
		String fileNameWithoutExt = FilenameUtils.removeExtension(temp.getActualFileName());

		Long max = bulkRepo.getMaxSameFileNumberForAFile(temp.getActualFileName());
		max = max == null ? 0 : ++max;
		temp.setModifiedFileName(max == 0 ? temp.getActualFileName() : fileNameWithoutExt + "_" + max + "." + ext);
		temp.setSameFileNumber(max);
		return bulkRepo.save(temp);
	}

	
	@Override
	public OutputFileDto fetchFile(String fileDetailId) {
		UserBulkImport fileStorage = getById(fileDetailId);
		OutputFileDto dto = new OutputFileDto();
		File file = fileWrapperService.getFile(fileStorage);
		dto.setFile(file);
		dto.setOriginalFileName(fileStorage.getModifiedFileName());
		dto.setContentType(fileStorage.getFileType());
		return dto;
	}

}
