package com.kbeauty.gbt.dao;

import java.util.List;
import java.util.Map;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Count;
import com.kbeauty.gbt.entity.domain.Likes;
import com.kbeauty.gbt.entity.domain.Others;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.view.AiRecommandProduct;
import com.kbeauty.gbt.entity.view.ClassView;
import com.kbeauty.gbt.entity.view.ContentCondition;

public interface ContentMapper {	
	Long getContentSeq();
	
	List<Content> getContentList(ContentCondition condition);	
	int getContentListCnt(ContentCondition condition);
	
	public List<Content> getFollowContentList(ContentCondition condition);	
	public int getFollowContentListCnt(ContentCondition condition);
	
	public List<Content> getReplyList(ContentCondition condition);	
	public int getReplyListCnt(ContentCondition condition);
	
	public void plusViewCount(Count count);
	public void minusViewCount(Count count);
	
	public void plusLikeCount(Count count);
	public void minusLikeCount(Count count);
	
	public void plusFavCount(Count count);
	public void minusFavCount(Count count);
	
	public void plusReplyCount(Count count);
	public void minusReplyCount(Count count);
	
	public int getLastOrder(String contentId);
	
	public String getLikeId(Likes like);
		
	public List<Likes> getLikeByUserId(Likes like);
	
	public List<ClassView> getClassList(ClassView classView);
	
	String getClass02Name(String categoryId);
	
	String getClass03Name(String categoryId);
		
	Float getAvgReviewGrade(ContentCondition condition);
	
	List<Others> getPeerProductInfoMap(Map<String, List<String>> condition);
	
	  public String getClass02Code(String middleName);
		public String getClass03Code(String subName);

	public List<Content> searchProductName(String pdName);

	public List<AiRecommandProduct> getRecommandProduct();
	public List<Resources> checkProductCompare(Content content);

	
	//notice
	public List<Content> getNoticeByNoticeType(String noticeType);
	public List<Content> getNoticeAll();
	//notice
	
//	MIG
	public List<Resources> resourcesFile();
	public List<Others> othersFile();


}
