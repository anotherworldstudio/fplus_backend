package com.kbeauty.gbt.entity.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kbeauty.gbt.entity.enums.ContentType;
import com.kbeauty.gbt.entity.enums.SkinArea;
import com.kbeauty.gbt.entity.enums.SkinAreaProd;
import com.kbeauty.gbt.entity.enums.SkinAreaTreat;
import com.kbeauty.gbt.entity.enums.SkinLevelType;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class AiRecommandView extends CommonView {

//	AiRecommandRaw ==> 한줄평 원본
//	AiRecommand ==> 한줄평 text 
//	AiRecommandProd ==> 상품 정보 
//	AiRecommandTreat ==> 시술 정보

//	10000	주름
//	10010	모공
//	10020	Trouble
//	10030	Pigment
//	10040	Redness

	private List<AiRecommandRaw> aiRecommandRawList;

	private List<AiRecommand> aiRecommandList;
	private List<AiRecommand> aiRecommandProdList;
	private List<AiRecommand> aiRecommandTreatList;
	private List<AiRecommand> aiRecommandEditorList;

	public AiRecommandView() {
		aiRecommandList = new ArrayList<AiRecommand>();
		aiRecommandProdList = new ArrayList<AiRecommand>();
		aiRecommandTreatList = new ArrayList<AiRecommand>();
		aiRecommandEditorList = new ArrayList<AiRecommand>();
	}

	/**
	 * 의사진단 추천제품 ==> 랜덤으로 처리한 내용으로 contentId 셋팅 
	 * 추천시술 ==> 랜덤으로 처리한 내용으로 contentId 셋팅
	 */
	public void generate() {
		// 종류별로 contentType는 하나만 존재해야 함.
		Map<String, String> levelTypeMap = new HashMap<>(); // key contentType : levelType

		// 랜덤으로 가지고 올 refGroupId
		Set<String> commentGroupSet = new HashSet<>();
		Set<String> productGroupSet = new HashSet<>();
		Set<String> treatGroupSet = new HashSet<>();
		Set<String> editorGroupSet = new HashSet<>();

		String contentType = null;

		for (AiRecommandRaw raw : aiRecommandRawList) {
			contentType = raw.getContentType();
			if (isComment(contentType)) {
				commentGroupSet.add(raw.getRefGroup());
				aiRecommandList.add(raw.genAiRecommand());
			} else if (isProduct(contentType)) {
				productGroupSet.add(raw.getRefGroup());
				aiRecommandProdList.add(raw.genAiRecommand());
			} else if (isTreat(contentType)) {
				treatGroupSet.add(raw.getRefGroup());
				aiRecommandTreatList.add(raw.genAiRecommand());
			} else if (isEditor(contentType)) {
				editorGroupSet.add(raw.getRefGroup());
				aiRecommandEditorList.add(raw.genAiRecommand());
			}
		}

		setData(aiRecommandList, commentGroupSet);
		setData(aiRecommandProdList, productGroupSet);
		setData(aiRecommandTreatList, treatGroupSet);
		setData(aiRecommandEditorList, editorGroupSet);
	}

	private void setData(List<AiRecommand> aiRecList, Set<String> groupSet) {
		List<AiRecommand> tempRecommandList = new ArrayList<AiRecommand>();

		if (StringUtil.isEmpty(aiRecList))
			return;

		AiRecommand aiRecommand = aiRecList.get(0);
		if (SkinLevelType.MAPPING.getCode().equals(aiRecommand.getLevelType()) || 
			SkinLevelType.BOTTOM.getCode().equals(aiRecommand.getLevelType())	
				)
			return;
		if (SkinLevelType.RANDOM_GROUP.getCode().equals(aiRecommand.getLevelType())) {
			String randomRefGroupId = getRandomRefGroupId(groupSet);
			for (AiRecommand aiRec : aiRecList) {
				if (randomRefGroupId.equals(aiRec.getRefGroup())) {
					tempRecommandList.add(aiRec);
				}
			}
		}

		aiRecList = tempRecommandList;
	}

	private boolean isComment(String itemCode) {
		return ContentType.AI_DOCTOR.getCode().equals(itemCode);
	}

	private boolean isProduct(String itemCode) {
		return ContentType.AI_PRODUCT.getCode().equals(itemCode);
	}

	private boolean isTreat(String itemCode) {
		return ContentType.AI_TREAT.getCode().equals(itemCode);
	}
	
	private boolean isEditor(String itemCode) {
		return ContentType.AI_EXPERT_TIP.getCode().equals(itemCode);
	}

	private String getRandomRefGroupId(Set<String> set) {
		if (StringUtil.isEmpty(set))
			return null;

		int randomInt = CommonUtil.getRandomInt(set.size());
		int i = 0;
		for (String string : set) {
			if (i == randomInt)
				return string;
			i++;
		}
		return null;
	}

}
