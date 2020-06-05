package com.test.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.test.model.OutputFileDto;
import com.test.model.UserBulkImport;
import com.test.service.BulkImportService;
import com.test.service.UserBulkImportResponse;
import com.text.request.model.RestCustom;
import com.text.request.model.RestResponse;
import com.text.request.model.RestStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0/file")
@Api(value = "BulkImportController")
public class BulkImportController {

	interface Constants {
		String FETCH_RECORDS = "Record fetch successfully";
		String NOT_FOUND = "Record not found";
		String ADD_RECORD = "File Uploaded Successfully";
		String INVALID_UID = "Invalid Id. Please enter valid id";
		String DELETE_TYPE = "Record deleted successfully";
		String UPDATE_TYPE = "Record updated successfully";
	}

	@Autowired
	private BulkImportService bulkImportService;

	@ApiOperation(value = "All FileMetadata")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Your request was successful"),
			@ApiResponse(code = 400, message = "Your request is not accepted"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 408, message = "Your request has timed out"),
			@ApiResponse(code = 409, message = "There was a resource conflict"),
			@ApiResponse(code = 500, message = "Generic server error"),
			@ApiResponse(code = 503, message = "Server Unavailable timeout") })
	@GetMapping("/")
	public ResponseEntity<RestResponse<?>> getAllFilesByModule() {

		RestStatus<?> restStatus = new RestStatus<String>(HttpStatus.OK, Constants.FETCH_RECORDS);
		List<UserBulkImport> fileResponses = bulkImportService.findAll();

		List<EntityModel<?>> entityModels = new ArrayList<>();
		for (UserBulkImport fileResponse : fileResponses) {
			pupulateLinks(entityModels, fileResponse);
		}

		RestResponse<?> response = new RestResponse<>(fileResponses, restStatus, RestCustom.builder().build());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@ApiOperation(value = "Get FileMetadata")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Your request was successful"),
			@ApiResponse(code = 400, message = "Your request is not accepted"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 408, message = "Your request has timed out"),
			@ApiResponse(code = 409, message = "There was a resource conflict"),
			@ApiResponse(code = 500, message = "Generic server error"),
			@ApiResponse(code = 503, message = "Server Unavailable timeout") })
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<?>> getById(@PathVariable(name = "id", required = true) String id) {

		RestStatus<?> restStatus = new RestStatus<String>(HttpStatus.OK, Constants.FETCH_RECORDS);
		UserBulkImport u = bulkImportService.getById(id);
		Link download = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(BulkImportController.class).getFile(u.getId(), 0))
				.withRel("download").withType("GET");
		Link view = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(BulkImportController.class).getFile(u.getId(), 1)).withRel("view")
				.withType("GET");
		Link delete = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(BulkImportController.class).deleteFile(u.getId())).withRel("delete")
				.withType("DELETE");
		EntityModel<UserBulkImport> entityModel = new EntityModel<>(u, download, view, delete);

		RestResponse<?> response = new RestResponse<>(entityModel, restStatus, RestCustom.builder().build());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@ApiOperation(value = "Add FileMetadata")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Your request was successful"),
			@ApiResponse(code = 400, message = "Your request is not accepted"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 408, message = "Your request has timed out"),
			@ApiResponse(code = 409, message = "There was a resource conflict"),
			@ApiResponse(code = 500, message = "Generic server error"),
			@ApiResponse(code = 503, message = "Server Unavailable timeout") })
	@PostMapping(value = "/")
	public ResponseEntity<RestResponse<?>> uploadFile(
			@RequestPart(name = "file", required = true) MultipartFile uploadfile) {

		RestStatus<?> restStatus = new RestStatus<String>(HttpStatus.CREATED, Constants.ADD_RECORD);
		Map<UserBulkImport, UserBulkImportResponse> fileResponses = bulkImportService.bulkImportSave(uploadfile);

		UserBulkImport bulkImport = new UserBulkImport();
		UserBulkImportResponse bulkImportResponse = new UserBulkImportResponse();
		for (Map.Entry<UserBulkImport, UserBulkImportResponse> entry : fileResponses.entrySet()) {
			bulkImport = entry.getKey();
			bulkImportResponse = entry.getValue();
		}

		Link download = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(BulkImportController.class).getFile(bulkImport.getId(), 0))
				.withRel("error_file_url").withType("GET");
		EntityModel<UserBulkImportResponse> entityModel = new EntityModel<>(bulkImportResponse, download);
		RestResponse<?> response = new RestResponse<>(entityModel, restStatus, RestCustom.builder().build());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/downloadFile/{fileDetailId}")
	public ResponseEntity<Resource> getFile(@PathVariable(name = "fileDetailId", required = true) String fileDetailId,
			@RequestParam(name = "download", required = false, defaultValue = "0") int download) {

		OutputFileDto outputFileDto = bulkImportService.fetchFile(fileDetailId);
		HttpHeaders headers = new HttpHeaders();
		if (download == 0)
			headers.setContentDispositionFormData("attachment", outputFileDto.getOriginalFileName());
		else
			headers.setContentDispositionFormData("inline", outputFileDto.getOriginalFileName());

		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		InputStreamResource resource = null;
		try {
			resource = new InputStreamResource(new FileInputStream(outputFileDto.getFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok().headers(headers).contentLength(outputFileDto.getFile().length())
				.contentType(MediaType.parseMediaType(outputFileDto.getContentType())).body(resource);
	}

	@ApiOperation(value = "Delete UserBulkImport")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Your request was successful"),
			@ApiResponse(code = 400, message = "Your request is not accepted"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 408, message = "Your request has timed out"),
			@ApiResponse(code = 409, message = "There was a resource conflict"),
			@ApiResponse(code = 500, message = "Generic server error"),
			@ApiResponse(code = 503, message = "Server Unavailable timeout") })
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<?>> deleteFile(@PathVariable(value = "id", required = true) String id) {

		RestStatus<?> restStatus = new RestStatus<String>(HttpStatus.OK, Constants.DELETE_TYPE);
		int i = bulkImportService.delete(id);
		if (i == 0)
			restStatus = new RestStatus<String>(HttpStatus.NOT_FOUND, Constants.NOT_FOUND);
		RestResponse<?> response = new RestResponse<>(i, restStatus, RestCustom.builder().build());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	private void pupulateLinks(List<EntityModel<?>> entityModels, UserBulkImport fileResponse) {
		Link download = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(BulkImportController.class).getFile(fileResponse.getId(), 0))
				.withRel("download").withType("GET");
		Link view = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(BulkImportController.class).getFile(fileResponse.getId(), 1))
				.withRel("view").withType("GET");
		Link delete = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(BulkImportController.class).deleteFile(fileResponse.getId()))
				.withRel("delete").withType("DELETE");
		EntityModel<UserBulkImport> entityModel = new EntityModel<>(fileResponse, download, view, delete);
		entityModels.add(entityModel);
	}

}
