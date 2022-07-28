package com.kbeauty.gbt.entity;

import java.io.Serializable;
import java.util.List;

import javax.swing.event.SwingPropertyChangeSupport;

import com.kbeauty.gbt.entity.domain.Code;
import com.kbeauty.gbt.entity.enums.SkinArea;
import com.kbeauty.gbt.entity.view.SkinRank;
import com.kbeauty.gbt.entity.view.SkinResult;

import lombok.Data;


@Data
public class AiRawScore  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int glasses;
	private double pigment;
	private double pore;
	private double redness;
	private double trouble;
	private double wrinkle;
	private int mask;
	
	//변환 점수
	private int cPigment;
	private int cPore;
	private int cRedness;
	private int cTrouble;
	private int cWrinkle;	
	
	// 환산 점수 ==> baseScore + 원점수 * weight
	//private int baseScore; // 최하 점수 구간 
	//private int weight; // 원점수별 가중치 
	
	
	private int convertScore(double score, int baseScore) {
		score = 6 - score; //AI 점수는 0~6점까지 점수이고, 작은 점수가 좋은 피부 상태임 
		
		int weightScore = 100 - baseScore;
		double weight = weightScore / 6d;		
		
		if(baseScore == 0 && weight==0) {
			weight = 17; // 100 / 6 
		}
		
//		int convertScore = (int)(baseScore + score * weight);
		
		int convertScore = (int)Math.round(baseScore + score * weight);
		
		if(convertScore > 100) {
			convertScore = 100;
		}else if(convertScore < 0) {
			convertScore = 0;
		}
		
		return convertScore;
	}
	
	public static void main(String[] args) {
		AiRawScore score = new AiRawScore();
		
		int s = score.convertScore(0.0124, 60);
		System.out.println(s);
	}
	
	public void setScore(SkinArea skinArea, int basScore) {
		switch (skinArea) {		
		case WRINKLE:			
			cWrinkle = convertScore(wrinkle, basScore);
			
		case PIGMENT:
			cPigment = convertScore(pigment, basScore);
			
		case PORE:
			cPore = convertScore(pore, basScore);
		case REDNESS:
			cRedness = convertScore(redness, basScore);
			
		case TROUBLE:
			cTrouble = convertScore(trouble, basScore);
			
		default:
			
		}
	}
	
	public int getScore(SkinArea skinArea) {
		switch (skinArea) {		
		case WRINKLE:			
			return cWrinkle;
			
		case PIGMENT:
			return cPigment;
			
		case PORE:
			return cPore;
			
		case REDNESS:
			return cRedness;
			
		case TROUBLE:
			return cTrouble;
		default:
			return 0;
		}
	}
	
	public double getOrgScore(SkinArea skinArea) {
		switch (skinArea) {		
		case WRINKLE:			
			return wrinkle;
			
		case PIGMENT:
			return pigment;
			
		case PORE:
			return pore;
			
		case REDNESS:
			return redness;
			
		case TROUBLE:
			return trouble;
		default:
			return 0;
		}
	}
	
	public int getSkinAgeData01() {
		int result = 0;
		result = result + cWrinkle * 8; 
		result = result + cPigment * 4; 
		result = result + cTrouble * 1;
		result = result + cPore * 2; 
		result = result + cRedness * 2;
		
		return result;
	}
	
	public int getSkinAgeData01(List<Code> codeDataList) {
		int score = 0;
		String code = null;
		for (Code codeData : codeDataList) {
			code = codeData.getNCode();
			if(SkinArea.WRINKLE.getCode().equals(code)) {
				score = score + cWrinkle * codeData.getIntVal();
			}else if(SkinArea.PORE.getCode().equals(code)) {
				score = score + cPore * codeData.getIntVal();				
			}else if(SkinArea.TROUBLE.getCode().equals(code)) {				
				score = score + cTrouble * codeData.getIntVal();				
			}else if(SkinArea.PIGMENT.getCode().equals(code)) {
				score = score + cPigment * codeData.getIntVal();
			}else if(SkinArea.REDNESS.getCode().equals(code)) {				
				score = score + cRedness * codeData.getIntVal();
			}			
		}
		return score;
	}
	
	public int getSkinAgeData02() {
		
		int result = 0;
		
		result = result + cWrinkle * 8; 
		result = result + cPigment * 4; 
		result = result + cTrouble * -1;
		result = result + cPore * 2; 
		result = result + cRedness * 2;
		
		return result;
	}
	
	public int getSkinRankData() {
		int result = 0;
		result = result + cWrinkle * 1; 
		result = result + cPigment * 1; 
		result = result + cTrouble * 1;
		result = result + cPore * 1; 
		result = result + cRedness * 1;
		
		return result;
	}
	
	private static int[] skinAgeArray = {16,17,18,	19,	20,	21,	22,	23,	24,	25,	26,	27,	28,	29,	30,	31,	32,	33,	34,	35,	36,	37,	38,	39,	40,	41,	42,	43, 44, 45, 46, 47, 48 };
	
	public static int getSkinAge(SkinRank skinRank) {
		int result = 0;
		
		int totalCnt = skinRank.getCnt();
		int rangeCnt = skinAgeArray.length;		
		int ranking = skinRank.getRanking();
		
		float rate01 = ranking / (float)totalCnt;
		float rate02 = rangeCnt * rate01;
		
		int adjRank = Math.round((float)Math.floor(rate02));
		
		result = skinAgeArray[0] + adjRank;
				
		return result;
	}
}
