package com.kbeauty.gbt.entity.domain;

import com.kbeauty.gbt.entity.enums.*;
import com.kbeauty.gbt.entity.view.ImageData;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name="RECRUIT")
@Data
@EqualsAndHashCode(callSuper = false)
public class Recruit extends CommonDomain{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    DB가 자동으로 증가시키는 값?
    private long seq;
    @Column(name = "recruitid") private String recruitId;

    @Column(name = "userid") private String userId;
    @Column(name = "recruittype") private String recruitType;
    private String title;
    private String content;
    private String status;
    private int depth;

    private String active;
    private String path;

    @Column(name="startdate") private String startDate;
    @Column(name = "enddate")	private String endDate;


//    이미지 링크 관련
    @Column(name="thumbnailimage") private String thumbnailImage;
    @Column(name="mainimage") private String mainImage;


    @Transient private String upperRecruitId; //???????????????...
    @Transient private String userName;
    @Transient private String userImgUrl;
    @Transient private ImageData userImgData;

//ENUM
    @Transient private String activeName;
    @Transient private String recruitTypeName;
    @Transient private String statusName;

//    ENUM 이름 설정
    public void setEnumName() {
        activeName = CommonUtil.getValue(Active.values(), active);
        recruitTypeName = CommonUtil.getValue(RecruitType.values(), recruitType);
        statusName = CommonUtil.getValue(ContentStatus.values(), status);

    }

    public boolean isMainImg() {
        return ! StringUtil.isEmpty(mainImage);
    }

}
