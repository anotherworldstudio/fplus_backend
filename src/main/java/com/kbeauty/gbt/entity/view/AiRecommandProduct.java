package com.kbeauty.gbt.entity.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Sort;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.enums.ResourceType;

import lombok.Data;

@Data
public class AiRecommandProduct extends CommonView {

	private String contentId;
	private String title;
	List<Resources> productList;

	public void setProductSizeOnRandom(int count) {
		List<Resources> tempResourceList = new ArrayList<>(productList);
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

		productList = resultList;
	}
}