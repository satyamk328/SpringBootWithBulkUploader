package com.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_bulkimport")
public class UserBulkImport {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", unique = true, nullable = false)
	private String id;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "import_mode")
	private String importMode;

	@Column(name = "status")
	private String status;

	@Column(name = "file_size")
	private Long size;

	@Column(name = "same_file_number")
	private Long sameFileNumber;

	@Column(name = "success_record")
	private Long successRecord;

	@Column(name = "failed_record")
	private Long failedRecord;

	@javax.persistence.Column(name = "actual_file_name")
	private java.lang.String actualFileName;

	@javax.persistence.Column(name = "modified_file_name")
	private java.lang.String modifiedFileName;

	@javax.persistence.Column(name = "folder_name")
	private java.lang.String folderName;

}
