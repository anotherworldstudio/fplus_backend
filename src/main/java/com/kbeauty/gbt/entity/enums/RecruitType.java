package com.kbeauty.gbt.entity.enums;

public enum RecruitType implements CodeVal{
//    ( 0000 = 오디션 , 1000 = 채용, 2000 = 이벤트, 9999 = 전체 )
    AUDITION("0000","오디션"),
    EMPLOYMENT("1000","채용"),
    EVENT("3000","이벤트");


    private String code;
    private String val;

    RecruitType(String code,String val) {
        this.code = code;
        this.val = val;
    }

    public String getCode() {
        return code;
    }

    public String getVal() {
        return val;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
