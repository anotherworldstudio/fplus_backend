package com.kbeauty.gbt.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbeauty.gbt.controller.NoticeResCtrl;
import com.kbeauty.gbt.controller.PointResCtrl;
import com.kbeauty.gbt.dao.ExpRepo;
import com.kbeauty.gbt.dao.GameDataRepo;
import com.kbeauty.gbt.dao.GameLogRepo;
import com.kbeauty.gbt.dao.MileageRepo;
import com.kbeauty.gbt.dao.PointMapper;
import com.kbeauty.gbt.dao.PointTypeRepo;
import com.kbeauty.gbt.dao.UserRepo;
import com.kbeauty.gbt.dao.WeatherMapper;
import com.kbeauty.gbt.dao.WeatherRepo;
import com.kbeauty.gbt.entity.domain.Exp;
import com.kbeauty.gbt.entity.domain.GameData;
import com.kbeauty.gbt.entity.domain.GameLog;
import com.kbeauty.gbt.entity.domain.Mileage;
import com.kbeauty.gbt.entity.domain.PointType;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.PointUseGetType;
import com.kbeauty.gbt.entity.enums.PointUseGetTypeDetail;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.MileExp;
import com.kbeauty.gbt.entity.view.PointCondition;
import com.kbeauty.gbt.entity.view.PointView;
import com.kbeauty.gbt.entity.view.WeatherCondition;
import com.kbeauty.gbt.entity.view.WeatherView;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PointService {

	@Autowired
	MileageRepo mileageRepo;

	@Autowired
	ExpRepo expRepo;

	@Autowired
	PointTypeRepo pointTypeRepo;

	@Autowired
	PointMapper pointMapper;

	@Autowired
	UserRepo userRepo;

	@Autowired
	GameDataRepo gameDataRepo;
	
	@Autowired
	GameLogRepo gameLogRepo;
	
	public User findUserByNickname(String userName) {
		return userRepo.findByUserName(userName).get(0);
	}

	public List<Mileage> getMileageList(PointCondition condition) {
		List<Mileage> list = new ArrayList<>();
		List<Mileage> mileList = pointMapper.getMileageList(condition);
		if (mileList != null && mileList.size()>0) {
			for (Mileage mileage : mileList) {
				mileage.setTypeName();
			}
			list = mileList;
		}
		return list;
	}

	public int getMileageListCnt(PointCondition condition) {
		return pointMapper.getMileageListCnt(condition);
	}

	public List<Exp> getExpList(PointCondition condition) {
		List<Exp> list = new ArrayList<>();
		List<Exp> expList = pointMapper.getExpList(condition);
		if (expList != null && expList.size() > 0) {
			for (Exp exp : expList) {
				exp.setTypeName();
			}
			list = expList;
		}
		return list;
	}

	public int getExpListCnt(PointCondition condition) {
		return pointMapper.getExpListCnt(condition);
	}
	
	public PointView updateMileage(Mileage mile, String userId) {
		PointView view = new PointView();
		try {
			Mileage mileage = new Mileage();
			// mile 이 비었나 안비었나 검사
			if (mile.isEmpty()) {
				view.setError(ErrMsg.LACK_DATA);
				return view;
			}
			PointType pointType = pointTypeRepo.findBySystemType(mile.getMileType());

			// mile + / - 검사
			String plusMinus = pointType.getPlusMinus();
			int milePoint = pointType.getMileage();
			// 전거 가져오기
			List<Mileage> oldList = pointMapper.getMileageListByUserId(mile.getUserId());


			// total이없는데 -면 리턴 err
			if (oldList.size() <= 0) {
				mileage.setOldTotal(0);
				if (plusMinus.equals("M")) {
					view.setError(ErrMsg.LACK_MILEAGE);
					return view;
				} else if (plusMinus.equals("P")) {
					mileage.setNewTotal(milePoint);
				} else {
					view.setError(ErrMsg.LACK_DATA);
					return view;
				}
			} else {
				// total 이 있으면 현재값 계산해서 total set
				int oldTotal = oldList.get(0).getNewTotal();
				int newTotal;
				mileage.setOldTotal(oldTotal);
				if (plusMinus.equals("M")) {
					newTotal = oldTotal - milePoint;
					if (newTotal < 0) {
						view.setError(ErrMsg.LACK_MILEAGE);
						return view;
					} else {
						mileage.setNewTotal(newTotal);
					}
				} else if (plusMinus.equals("P")) {
					newTotal = oldTotal + milePoint;
					mileage.setNewTotal(newTotal);
				} else {
					view.setError(ErrMsg.LACK_DATA);
					return view;
				}
			}
			String mileId = CommonUtil.getGuid();
			mileage.setMileId(mileId);
			mileage.setUserId(mile.getUserId());
			mileage.setBasicInfo(userId);
			mileage.setPlusMinus(plusMinus);
			mileage.setBigType(pointType.getBigType());
			mileage.setMileType(pointType.getSystemType());
			mileage.setMile(milePoint);
			mileageRepo.save(mileage);
			List<Mileage> returnList = new ArrayList<Mileage>();
			returnList.add(mileage);
			view.setMileList(returnList);
			view.setOk();
			// save
		} catch (Exception E) {
			log.error("ARROR", E);
			view.setError(ErrMsg.POINT_ERROR);
			return view;
		}

		return view;
	}

	public PointView updateExp(Exp exp, String userId) {
		PointView view = new PointView();
		try {
			Exp newExp = new Exp();
			// mile 이 비었나 안비었나 검사
			if (exp.isEmpty()) {
				view.setError(ErrMsg.LACK_DATA);
				return view;
			}
			PointType pointType = pointTypeRepo.findBySystemType(exp.getExpType());

			// mile + / - 검사
			String plusMinus = pointType.getPlusMinus();
			int expPoint = pointType.getExp();
			// 전거 가져오기
			List<Exp> oldList = pointMapper.getExpListByUserId(exp.getUserId());
			
			// total이없는데 -면 리턴 err
			if (oldList.size() <= 0) {
				newExp.setOldTotal(0);
				if (plusMinus.equals("M")) {
					view.setError(ErrMsg.LACK_MILEAGE);
					return view;
				} else if (plusMinus.equals("P")) {
					newExp.setNewTotal(expPoint);
				} else {
					view.setError(ErrMsg.LACK_DATA);
					return view;
				}
			} else {
				// total 이 있으면 현재값 계산해서 total set
				int oldTotal = oldList.get(0).getNewTotal();
				int newTotal;
				newExp.setOldTotal(oldTotal);
				if (plusMinus.equals("M")) {
					newTotal = oldTotal - expPoint;
					if (newTotal < 0) {
						view.setError(ErrMsg.LACK_MILEAGE);
						return view;
					} else {
						newExp.setNewTotal(newTotal);
					}
				} else if (plusMinus.equals("P")) {
					newTotal = oldTotal + expPoint;
					newExp.setNewTotal(newTotal);
				} else {
					view.setError(ErrMsg.LACK_DATA);
					return view;
				}
			}
			String expId = CommonUtil.getGuid();
			newExp.setExpId(expId);
			newExp.setUserId(exp.getUserId());
			newExp.setBasicInfo(userId);
			newExp.setPlusMinus(plusMinus);
			newExp.setBigType(pointType.getBigType());
			newExp.setExpType(pointType.getSystemType());
			newExp.setExp(expPoint);
			expRepo.save(newExp);
			List<Exp> returnList = new ArrayList<>();
			returnList.add(newExp);
			view.setExpList(returnList);
			view.setOk();

		} catch (Exception E) {
			log.error("ARROR", E);
			view.setError(ErrMsg.POINT_ERROR);
			return view;
		}

		return view;
	}

	public int getHaveMileage(String userId) {
		int getTotal, useTotal;
		getTotal = pointMapper.getTotalGetMileage(userId);
		if (getTotal == 0)
			return getTotal;
		useTotal = pointMapper.getTotalUseMileage(userId);

		return getTotal - useTotal;
	}

	public int getHaveExp(String userId) {
		int getTotal, useTotal;
		getTotal = pointMapper.getTotalGetExp(userId);
		if (getTotal == 0)
			return getTotal;
		useTotal = pointMapper.getTotalUseExp(userId);

		return getTotal - useTotal;
	}

	public List<PointType> getPointTypeList(PointCondition condition) {
			
		List<PointType> list = new ArrayList<>();
		List<PointType> pointList = pointMapper.getPointTypeList(condition);
		if (pointList != null)
			for (PointType PointType : pointList) {
				PointType.setTypeName();
			}
			list = pointList;
		
		return list;
	}

	public void deleteMileage(String mileId,String userId){
		Mileage mile = mileageRepo.findByMileId(mileId);
		mileDeleteLog(mile,userId);
		mile.setDelYn(YesNo.YES.getVal());
		mile.setBasicInfo(userId);
		mileageRepo.save(mile);
	}
	public void deleteExp(String expId,String userId){
		Exp exp = expRepo.findByExpId(expId);
		expDeleteLog(exp,userId);
		exp.setDelYn(YesNo.YES.getVal());
		exp.setBasicInfo(userId);
		expRepo.save(exp);
	}
	
	public void mileDeleteLog(Mileage mile,String userId) {
		Mileage mileage = new Mileage();
		mileage.setMileId(CommonUtil.getGuid());
		mileage.setUserId(mile.getUserId());
		mileage.setBigType(PointUseGetType.ADMIN.getCode());
		mileage.setMileType(PointUseGetTypeDetail.ADMIN_DEL.getCode());
		List<Mileage> oldList = pointMapper.getMileageListByUserId(mile.getUserId());
		int oldTotal = oldList.get(0).getNewTotal();
		mileage.setOldTotal(oldTotal);
		mileage.setMile(0);
		if(mile.getPlusMinus().equals("P")) {
			mileage.setPlusMinus("M");
			mileage.setNewTotal(oldTotal - mile.getMile());
		}else{
			mileage.setPlusMinus("P");
			mileage.setNewTotal(oldTotal + mile.getMile());
		}
		mileage.setBasicInfo(userId);
		mileageRepo.save(mileage);
	}
	public void expDeleteLog(Exp exp,String userId) {
		Exp newExp = new Exp();
		newExp.setExpId(CommonUtil.getGuid());
		newExp.setUserId(exp.getUserId());
		newExp.setBigType(PointUseGetType.ADMIN.getCode());
		newExp.setExpType(PointUseGetTypeDetail.ADMIN_DEL.getCode());
		List<Exp> oldList = pointMapper.getExpListByUserId(exp.getUserId());
		int oldTotal = oldList.get(0).getNewTotal();
		newExp.setOldTotal(oldTotal);
		newExp.setExp(0);
		if(exp.getPlusMinus().equals("P")) {
			newExp.setPlusMinus("M");
			newExp.setNewTotal(oldTotal - exp.getExp());
		}else{
			newExp.setPlusMinus("P");
			newExp.setNewTotal(oldTotal + exp.getExp());
		}
		newExp.setBasicInfo(userId);	
		expRepo.save(newExp);
		
	}
		
	public int getPointTypeListCnt(PointCondition condition) {
		return pointMapper.getPointTypeListCnt(condition);
	}

	public PointType findByPointTypeId(String pointTypeId) {
		return pointTypeRepo.findByPointTypeId(pointTypeId);
	}

	public void save(PointType pointType) {
		pointTypeRepo.save(pointType);
	}
	
	public GameData getGameData(String userId) {
		GameData data = gameDataRepo.findByuserId(userId);
		if(data==null){
			data = new GameData();
			data.setUserId(userId);
			data.setStage(1); // 1단계 
			data.setStatus(0); // 0번
			data.setBasicInfo(userId);
			data.setClearMoment();
			gameDataRepo.save(data);
			data = gameDataRepo.findByuserId(userId);
			return data;
		}
		data.setStatus(getGameStatus(data));
		gameDataRepo.save(data);
		return data;
	}
	
	public GameData goNextStage(String userId) {
		GameData data = gameDataRepo.findByuserId(userId);
		if(data==null) {
			return null;
		}
		GameLog log = new GameLog(); log.setGameLogOnData(data);
		gameLogRepo.save(log);
		data.setStage(data.getStage()+1);
		data.setStatus(0);
		data.setClearMoment();
		data.setBasicInfo(userId);
		gameDataRepo.save(data);
		return data;
	}
			
	public int getGameStatus(GameData data) { // 사진찍기,리뷰달기,화장품등록하기 
		int oldStatus = data.getStage();
		int stage = data.getStage();
		int status = 0;
		if(stage == 1) { 		// 피드 올리기
			status = pointMapper.getStatusStage1(data);
		}else if(stage == 2) { 	// 댓글 세개 달기
			status = pointMapper.getStatusStage2(data);  
		}else if(stage == 3) { 	// 피부분석 3일 하기
			status = pointMapper.getStatusStage3(data);  
		}else if(stage == 4) {	
			status = pointMapper.getStatusStage4(data);  
		}else if(stage == 5) {	
			status = pointMapper.getStatusStage5(data);  
		}else if(stage == 6) {	
			status = pointMapper.getStatusStage6(data);  
		}else if(stage == 7) {	
			status = pointMapper.getStatusStage7(data);  
		}else if(stage == 8) {
			status = pointMapper.getStatusStage8(data);  
		}else if(stage == 9) {
			status = pointMapper.getStatusStage9(data); 
		}else if(stage == 10) {
			status = pointMapper.getStatusStage10(data);
		}else if(stage == 11) {
			status = pointMapper.getStatusStage11(data);
		}else if(stage == 12) {
			status = pointMapper.getStatusStage12(data);
		}else if(stage == 13) {
			status = pointMapper.getStatusStage13(data);
		}else if(stage == 14) {
			status = pointMapper.getStatusStage14(data);
		}else if(stage == 15) {
			status = pointMapper.getStatusStage15(data);
		}
		
		return status;
	}
	
	public String getNowTime() {
		String dateStr = CommonUtil.getSysTime();
		dateStr = dateStr.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
		return dateStr;
	}
	
	
	
}
