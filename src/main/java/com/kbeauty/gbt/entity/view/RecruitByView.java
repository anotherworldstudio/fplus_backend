package com.kbeauty.gbt.entity.view;


import com.kbeauty.gbt.entity.domain.Premium;
import com.kbeauty.gbt.entity.domain.Recruit;
import lombok.Data;

@Data
public class RecruitByView extends CommonView{
    private String userId;
    private String premiumYn;
    private String premiumStart;
    private String premiumEnd;
    private Premium premium;


}
