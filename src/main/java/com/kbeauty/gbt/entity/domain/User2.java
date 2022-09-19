package com.kbeauty.gbt.entity.domain;

import com.kbeauty.gbt.entity.enums.UserRole;
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
    @Column(name = "email")
    private String email;
    @Column(name = "userrole")
    private String userRole;
    @Column(name = "friendid")
    private String friendId;
    @Column(name = "password")
    private String password;


    public boolean isSame(User2 user) {
        if (userId == null) return false;

        if (userId.equals(user.getUserId())) {
            return true;
        } else {
            return false;
        }
    }
}


