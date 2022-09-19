package com.kbeauty.gbt.entity.enums;

public enum ErrMsg{
	
	SUCCESS("0000", "정상 처리되었습니다.")	
	,NO_RESULT("0010", "조회된 결과가 없습니다.")
	,NO_USER_ID("0020", "User ID가 누락되었습니다.")
	,NO_SEQ("0030", "Seq가 누락되었습니다.")
	,NO_FOLLOW_ID("0040", "Follow ID가 누락되었습니다.")	
	,AI_PROCESS_ERR("0050", "AI 피부분석 처리 중 오류가 발생하였습니다.")	
	,EXCEL_PROCESS_ERR("0060", "엑셀 업로드 중 에러가 발생했습니다.")
	,AI_GLASSES_ERR("0070", "안경을 착용하였습니다.")	
	,AI_MASK_ERR("0071", "마스크를 착용하였습니다.")	
	,AI_FACE_ERR("0080", "안면 인식에 실패하였습니다.")	
	,NO_USER("1000", "사용자 조회에 실패했습니다.")
	,USER_IMG_ERR("1001", "사용자 이미지 저장 중에 오류가 발생하였습니다.")
	,DUPLICATE("1002", "중복된 사용자명 입니다.")
	,DUPLICATE_EMAIL("1003", "중복된 Email 입니다.")
	
	,ALREADY_UNFOLLOW("1004", "이미 언팔한 사용자입니다.")
	,ALREADY_FOLLOW_REQ("1005", "이미 팔로우 요청한 사용자입니다.")	
	,ALREADY_FOLLOW("1005", "이미 팔로우한 사용자입니다.")	
	,NO_FOLLOW("1006", "조회된 팔로우 정보가 없습니다.")	
	,ALREADY_BLOCK("1007", "이미 블락처리한 사용자 입니다.")
	,ALREADY_DELETE("1008", "이미 삭제된 사용자 입니다.")
	,SAME_FOLLOW("1009", "사용자 아이디와 FOLLOW 아이디가 동일합니다.")
	,USER_FACE_VIEW_ERR("1010","유저 얼굴정보 리스트조회에 실패했습니다")
	,USER_FACE_SAVE_ERR("1020","유저 얼굴정보 저장,수정에  실패했습니다")
	,USER_FACE_MY_ERR("1020","내 얼굴정보 조회에 실패했습니다")
	,USERROLE_SET_ERR("1030","유저 타입 변경에 실패했습니다")
	
	,CONTENT_IMG_ERR("2000", "미디어 저장 중에 오류가 발생하였습니다.")
	,CONTENT_SAVE_ERR("2001", "Feed Data 저장 중에 오류가 발생하였습니다.")
	,CONTENT_NO_SAVE_ERR("2002", "저장할 Feed Data 가 없습니다.")
	,CONTENT_TYPE_ERR("2003", "Content Type이 적당하지 않습니다.")
	,CONTENT_NO_ID("2004", "Content ID가 누락되었습니다.")	
	,LIKE_DELETE_ERR("2005", "LIKE 삭제 중 오류가 발생하였습니다.")	
	,CONTENT_DATA_ID("2006", "Content ID가 누락되었습니다.")	
	,CONTENT_UPPER_ID("2007", "댓글의 상위 아이디가 없습니다.")	
	
	,PRODUCT_NO_IMG_ERR("3001", "수정할 제품 이미지 아이디가 없습니다.")
	
	,WEATHER_PROC_ERR("4000", "날씨정보 조회 중 오류입니다.")
	
	,SKIN_NO_ID("5001","Skin Id가 누락되었습니다.")
	,SKIN_NOT_SAVE("5002","Skin 정보 저장에 실패했습니다")
	,SKIN_NOT_RANKING("5100","Skin Ranking 정보 조회에 실패했습니다")
	,SIMPLE_SKIN_ERROR("5200","최근 스킨정보 에러")
	,SKIN_TYPE_TEST_ERROR("5300","스킨타입테스트 조회&검사중 에러")
	,SKIN_TYPE_RETEST_ERROR("5305","스킨타입 재테스트 조회&검사중 에러")
	,SAVE_SKIN_TYPE_TEST_STEP_ERROR("5310","스킨타입테스트 step 저장중 에러")
	,GET_SKIN_TYPE_TEST_STEP_ERROR("5320","스킨타입테스트 step 조회중 에러")
	,CREATE_SKIN_TYPE_TEST_ERROR("5330","스킨타입테스트 생성중 에러")
	,SKIN_TYPE_TEST_RESULT_ERROR("5340","스킨타입테스트 결과생성중 에러")
	,DEL_SKIN_TYPE_TEST_STEP_ERROR("5340","스킨타입테스트 결과생성중 에러")
	,SET_SKIN_TYPE_TEST_STEP_ERROR("5350","스킨타입테스트 스텝저장 or 조회 에러")
	
	,POINT_ERROR("6000","포인트 관련 처리 오류입니다")
	,LACK_DATA("6001","입력정보가 부족합니다")
	,LACK_MILEAGE("6002","마일리지가 부족합니다")
	,NO_RECOMMEND_USER("6666","해당 닉네임의 유저가 존재하지 않습니다.")
	
	,GAME_DATA("7000","게임데이터를 불러오는데 실패했습니다")
	
	,TRAINING_SAVE("8000","실습정보를 저장하는데 실패했습니다")
	,USER_FACE_SAVE("8010","유저 얼굴정보를 저장하는데 실패했습니다")
	,TRAINING_LACK_DATA("8020", "실습정보 입력정보가 부족합니다")
	,TRAINING_GRADE_ERR("8030","실습정보 평가하는데 실패했습니다")
	,LESSON_NO_SAVE_ERR("8040", "저장할 Lesson 데이터가 없습니다.")
	,LESSON_FACE_NO_SAVE_ERR("8060", "저장할 LessonFace 데이터가 없습니다.")
	,LESSON_FACE_OVERLAB_ERR("8070", "이미 있는 카테고리 입니다 ")
	,TRAINING_DUPLICATE_DATA("8080", "이미 저장된 실습이 있습니다")
	,LESSON_DELETE_ERR("8100", "실습 삭제실패 했습니다")
	,TRAINING_DELETE_ERR("8110", "실습정보 삭제에 실패했습니다")

	,NO_PREMIUM_USER("8000","구매내역이 없습니다.")
	
	;
	
	private String code;
	private String msg;
	
	ErrMsg(String code, String msg){
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}