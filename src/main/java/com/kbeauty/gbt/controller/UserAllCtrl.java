package com.kbeauty.gbt.controller;

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.User2;
import com.kbeauty.gbt.entity.enums.ConstantMapKey;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.view.*;
import com.kbeauty.gbt.service.UserService2;
import com.kbeauty.gbt.util.StringUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RestController
public class UserAllCtrl {

    @Autowired
    private UserService2 userService;

    @Resource
    private Login login;

    private final static String conditionKey = "UserCondition2";

    private void setCondition(HttpServletRequest request, UserCondition2 condition) {
        //TODO 기존에 conditionKey를 다 삭제한다.
        HttpSession session = request.getSession();
        session.setAttribute(conditionKey, condition);
    }

    private UserCondition2 getCondition(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserCondition2 condiiton = (UserCondition2)session.getAttribute(conditionKey);
        return condiiton;
    }

//    수정



    //이미지 저장 함수
    @RequestMapping(value="/save_img", method= RequestMethod.POST, headers="Content-Type=multipart/form-data")
    public User2 saveImg(@RequestPart MultipartFile faceImg, @RequestParam String userId, @RequestParam String fileName,
                         HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception{

        //사용자 아이디
        User2 user = userService.getUser(userId);
        if(user == null) {
            user = new User2();
            user.setError(ErrMsg.NO_USER);
            return user;
        }

        try {
            user = userService.saveImg(faceImg, user, fileName);
        } catch (IOException e) {
            user = new User2();
            user.setError(ErrMsg.USER_IMG_ERR);
            return user;
        }

        user.setOk();
        return user;
    }

//이미지 삭제 함수
    @RequestMapping(value="/delete_img/{userId}", method=RequestMethod.POST)
    public User2 deleteImg(
            @ApiParam(value = "사용자ID",  required = false, example = "사용자GUID")
            @PathVariable String userId) {

        //사용자 아이디
        User2 user = userService.getUser(userId);
        if(user == null) {
            user = new User2();
            user.setError(ErrMsg.NO_USER);
            return user;
        }

        user = userService.deleteImg(user);
        user.setOk();

        return user;
    }


//    TODO : profile UPDATE

    @RequestMapping(value="/update/user", method=RequestMethod.POST)
    public User2 updateUser(HttpServletRequest request,@RequestBody User2 userInfo, RedirectAttributes redirectAttributes){
        HttpSession session = request.getSession();
        User2 OldUserInfo = (User2) session.getAttribute("user");
        User2 user = new User2();

        if(OldUserInfo == null) {
            return null;
        }
        user = userService.update(OldUserInfo,userInfo);

        user.setOk();

        return user;
    }

}
