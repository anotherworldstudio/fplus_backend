package com.kbeauty.gbt.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.FileInfo;

@Repository
public interface FileInfoRepo extends CrudRepository<FileInfo, Long>{
	
	FileInfo findByFilename(String fileName);
	

}
