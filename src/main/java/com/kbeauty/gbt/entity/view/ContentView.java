package com.kbeauty.gbt.entity.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Count;
import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.domain.Likes;
import com.kbeauty.gbt.entity.domain.Others;
import com.kbeauty.gbt.entity.domain.Price;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.enums.OtherType;

import lombok.Data;

@Data
public class ContentView extends CommonView {
	private Content content;
	private Count count;
	private List<Likes> likeList;
	private List<com.kbeauty.gbt.entity.domain.Resources> resourceList;
	private List<com.kbeauty.gbt.entity.domain.Resources> hashList;
	private Follow follow;

	/**
	 * 제품 전용 필드
	 */

	private Price price;
	private List<Others> featureList; // 특장점
	private List<Others> peerProductList; // 유사품
	private List<Others> productImgList; // 제품이미지 목록

	/**
	 * 제품 전용필드 셋팅
	 * 
	 * @param othersList
	 */
	public void setOthersData(List<Others> othersList) {

		featureList = new ArrayList<Others>();
		peerProductList = new ArrayList<Others>();
		productImgList = new ArrayList<Others>();

		if (othersList == null)
			return;

		for (Others others : othersList) {
			if (OtherType.FEATURE.getCode().equals(others.getOtherType())) {
				featureList.add(others);
			} else if (OtherType.PEER_PRODUCT.getCode().equals(others.getOtherType())) {
				peerProductList.add(others);
			} else if (OtherType.PRODUCT_IMG.getCode().equals(others.getOtherType())) {
				productImgList.add(others);
			}
		}

		Collections.sort(featureList);
		Collections.sort(peerProductList);
		Collections.sort(productImgList);

	}

	public void setShuffleResoureList(int count) {
		List<Resources> tempResourceList = new ArrayList<>(resourceList);
		List<Resources> resultList = new ArrayList<>();

		if (tempResourceList.size() < count) {
			count = tempResourceList.size();
		}

		Random rand = new Random();
		for (int i = 0; i < count; i++) {
			int randomIndex = rand.nextInt(tempResourceList.size());
			Resources randomElement = tempResourceList.get(randomIndex);
			resultList.add(randomElement);
			tempResourceList.remove(randomIndex);
		}

		resourceList = resultList;
	}

}