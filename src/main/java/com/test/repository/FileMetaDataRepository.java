package com.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.test.model.UserBulkImport;

@Repository
public interface FileMetaDataRepository extends JpaRepository<UserBulkImport, String> {

	@Query("select max(fd.sameFileNumber) from UserBulkImport fd where fd.actualFileName=:actualfilename ")
	Long getMaxSameFileNumberForAFile(String actualfilename);
	
}
