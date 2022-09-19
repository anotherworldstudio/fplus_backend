package com.kbeauty.gbt.service;

import com.kbeauty.gbt.dao.UserMapper2;
import com.kbeauty.gbt.dao.UserRepo;
import com.kbeauty.gbt.dao.UserRepo2;
import com.kbeauty.gbt.entity.domain.CommonDomain;
import com.kbeauty.gbt.entity.domain.User2;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.UserRole;
import com.kbeauty.gbt.entity.enums.UserRole2;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService2 extends CommonService {

    @Autowired
    private UserMapper2 userMapper2;

    @Autowired
    private UserRepo2 userRepo2;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User2 save(User2 user2) {
        boolean isNew = false;


        String userId = user2.getUserId();
        if (StringUtil.isEmpty(userId)) {
//            유저아이디가 없으면 새로 가입
            isNew = true;
            userId = CommonUtil.getGuid();
            user2.setUserId(userId);
        }

        String email = user2.getEmail();
        if (email != null) {
            try {
                User2 findByEmail = userRepo2.findByEmail(email);
                if (findByEmail != null) {
                    if (findByEmail.isSame(user2)) {
                        throw new MessageException(ErrMsg.DUPLICATE_EMAIL);
                    }
                }
            } catch (Exception e) {
                throw new MessageException(ErrMsg.DUPLICATE_EMAIL);
            }
        }

        String userRole = user2.getUserRole();
        if(userRole == null) {
            user2.setUserRole(UserRole2.USER.getCode());
        }

        String friendId = user2.getFriendId();
        if(friendId.isEmpty() || friendId == null) {
            user2.setFriendId(null);
        }
        else {
            user2.setFriendId(friendId);
        }

        String dateStr = CommonUtil.getSysTime();
        user2.setBasicInfo(dateStr, userId);

        user2.setPassword(passwordEncoder.encode(user2.getPassword()));

        userRepo2.save(user2);
        return user2;
    }

    public boolean checkEmail(String email) {
        return userRepo2.existsByEmail(email);
}

}
