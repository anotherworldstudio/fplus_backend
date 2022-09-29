package com.kbeauty.gbt.config;

import com.kbeauty.gbt.dao.UserMapper2;
import com.kbeauty.gbt.dao.UserRepo2;
import com.kbeauty.gbt.entity.domain.User2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FplusLoginService {
    private final UserRepo2 userRepo2;
    private final UserMapper2 userMapper2;

//    유저가 입력한 비밀번호와 같은 회원을 반환하는데,
//    없으면 null 반환함
    public User2 login(String email,String password) {
        Optional<User2> findUser = userRepo2.findByEmail(email);
        return findUser.filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}
