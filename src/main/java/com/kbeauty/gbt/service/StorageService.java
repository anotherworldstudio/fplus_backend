package com.kbeauty.gbt.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.kbeauty.gbt.util.FileUtil;

@Service
public class StorageService {

	//@Value("${upload.dir}")
	private String uploadDir;
	
//	@Value("${spring.cloud.gcp.project-id}")
	private String projectId;
	
	//@Value("${spring.cloud.gcp.storage.url}")
	private String strorageUrl;
	
	//@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucket;
	
	//@Value("${spring.cloud.gcp.storage.bucket.user}")
	private String userFolder;
	
//	@Value("${spring.cloud.gcp.storage.bucket.content}")
	private String contentFolder;
	
	@Autowired
	private Storage storage;
	

//	@Value("gs://${gcs-resource-test-bucket}/my-file.txt")
//	private Resource gcsFile;
	
	public String saveStorage(BufferedImage resizeFaceImg, String name, String contentType) throws IOException {
    	List<Acl> acl = getReadAcl();
    	byte[] bytes = FileUtil.getBytesByContentType(resizeFaceImg, contentType);
    	
    	BlobId blobId = BlobId.of(bucket, name);
    	BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setAcl(acl).setContentType(contentType).build();
    	storage.create(blobInfo, bytes);
    	String selfLink = blobInfo.getSelfLink();
    	
//    	https://storage.cloud.google.com/bucket-beautage-com/ai/bec8b2bc98b54fcdbfcbc8588ce3c26a/20201204/a40515518bfc46519d3c6d850e728767.jpg
    	return strorageUrl + "/" + bucket + "/" + name;
    		
	}

    public BlobInfo saveStorage(MultipartFile file, String name) throws IOException {    	
    	List<Acl> acl = getReadAcl();
    	byte[] bytes = file.getBytes();
    	
    	BlobId blobId = BlobId.of(bucket, name);
    	//BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
    	
    	BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setAcl(acl).setContentType(file.getContentType()).build();
    	
    	storage.create(blobInfo, bytes);
    	
//    	storage.create(blobInfo, file.getInputStream());
//        BlobInfo blobInfo = storage.create(
//                BlobInfo.newBuilder(uploadReqDto.getBucketName(), uploadReqDto.getUploadFileName())
//                        .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER))))
//                        .build(),
//                new FileInputStream(uploadReqDto.getLocalFileLocation()));

        return blobInfo;
    }
	
	public void saveUserImg(MultipartFile file, String fileName)  throws IOException {
		saveStorage(file, fileName);
	}
	
	private List<Acl> getReadAcl() {
		Acl alluserAcl = Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER);
    	List<Acl> acl = new ArrayList<>();
    	acl.add(alluserAcl);
		return acl;
	}
	
	// TODO skin 부분 개발할때 파일 업로드 부분 확인해야 함.
	private String saveFile(String guid, MultipartFile file, String type) {
		if(file.isEmpty()) return "";
		
		StringBuffer fileName = new StringBuffer();
		String orgName = file.getOriginalFilename();
		String extention = FilenameUtils.getExtension(orgName).toLowerCase();
				
		fileName.append(guid).append("_").append(type).append(".").append(extention);
		File neoFile = new File(uploadDir+fileName.toString());
		
		try {
			file.transferTo(neoFile);
		} catch (IllegalStateException e) {
			fileName = new StringBuffer("");
		} catch (IOException e) {
			fileName = new StringBuffer("");
		} catch (Exception e) {
			fileName = new StringBuffer("");
		}
		
		return fileName.toString();
	}
	
	public boolean downloadObject(String objectName, String destFilePath) {
		boolean result = false;
		Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
		Blob blob = storage.get(BlobId.of(bucket, objectName));
		if(blob != null) {
			blob.downloadTo(Paths.get(destFilePath));
			result = true;
		}
		return result;
	}
	
	public String getFileName(String userId, String keyId, String uploadFileName) {
		
		StringBuffer sb = new StringBuffer();
		sb.append(contentFolder);
//		sb.append("/").append(userId).append("/").append(keyId).append(".").append(extention);
		String fileName = sb.toString();
		
		return fileName;
	}
	

}
