package com.kbeauty.gbt.entity.domain;

import com.kbeauty.gbt.entity.enums.UserRole;
import com.kbeauty.gbt.entity.enums.UserRole2;
import com.kbeauty.gbt.entity.view.ImageData;
import com.kbeauty.gbt.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "USER2")
@Data
@EqualsAndHashCode(callSuper = false)
public class User2 extends CommonDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;

    @Column(name = "userid")
    private String userId;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "userrole")
    private String userRole;
    @Column(name = "friendid")
    private String friendId;
    @Column(name = "password")
    private String password;
    @Column(name = "sex")
    private String sex;
    @Column(name = "birth")
    private String birth;
    @Column(name = "place")
    private String place;
    @Column(name = "imagedir")
    private String imageDir;
    @Column(name = "imagename")
    private String imageName;
    @Column(name = "team")
    private String team;
    @Column(name = "intro")
    private String intro;
    @Column(name = "mystatus")
    private String myStatus;
    @Column(name = "job")
    private String job;
    @Column(name = "career")
    private String career;
    @Column(name = "awards")
    private String awards;
    @Column(name = "social")
    private String social;
//    @Column(name = "height")
//    private String height;
//    @Column(name = "bust")
//    private String bust;
//    @Column(name="shose")
//    private String shose;
//    @Column(name="waist")
//    private String waist;
//    @Column(name="hair")
//    private String hair;
//    @Column(name="hip")
//    private String hip;

    @Transient	private String imgUrl;


    public boolean isSame(User2 user) {
        if (userId == null) return false;

        if (userId.equals(user.getUserId())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAdmin() {
        return UserRole2.ADMIN.getCode().equals(userRole);
    }

}


