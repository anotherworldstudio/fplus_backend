package com.kbeauty.gbt.entity.view;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.kbeauty.gbt.ai.FaceEnum;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.SkinInfo;
import com.kbeauty.gbt.entity.domain.Training;
import com.kbeauty.gbt.entity.enums.StoragePath;
import com.kbeauty.gbt.util.CroppingUtil;
import com.kbeauty.gbt.util.FileUtil;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class SaveTraining  extends CommonView{
	
	private Training training;
	private MultipartFile faceImg;
	private String fileName;
	private String userFaceName;		
		
}
