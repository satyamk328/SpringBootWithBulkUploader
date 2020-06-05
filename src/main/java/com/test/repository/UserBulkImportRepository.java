package com.test.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.test.model.UserBulkImport;

@Repository
public interface UserBulkImportRepository extends JpaRepository<UserBulkImport, String> {

	@Query(value = "FROM UserBulkImport r WHERE r.isDelete= false AND r.isActive=true AND (lower(r.actualFileName) like %?1% or lower(r.fileType) like %?1% or lower(r.status) like %?1% )")
	Page<UserBulkImport> findByfullTextSearch(String text, Pageable pageable);

	@Query("FROM UserBulkImport r WHERE r.isDelete= false AND r.isActive=true")
	public Page<UserBulkImport> findAll(Pageable pageable);

	@Query("FROM UserBulkImport r WHERE r.isDelete= false AND r.isActive= true")
	public List<UserBulkImport> findAll();

	@Query("FROM UserBulkImport r WHERE r.id=:roleId AND r.isDelete= false AND r.isActive= true")
	public Optional<UserBulkImport> findById(String roleId);

	@Query("select max(fd.sameFileNumber) from UserBulkImport fd where fd.actualFileName=:actualfilename ")
	Long getMaxSameFileNumberForAFile(String actualfilename);
	
	@Query("update UserBulkImport r set r.isDelete=true, r.isActive= false where r.id=?1")
	@Modifying
	public int updateInActiveAndDelete(String id);

	@Query("update UserBulkImport r set r.isDelete=false, r.isActive=true where r.id=?1")
	@Modifying
	public int updateActiveAndDelete(String id);

}
