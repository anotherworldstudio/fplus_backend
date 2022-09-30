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

    @RequestMapping(value="/update/user", method=RequestMethod.POST)
    public String updateUser(HttpServletRequest request,@RequestBody User2 userInfo, RedirectAttributes redirectAttributes){
        HttpSession session = request.getSession();
        User2 OldUserInfo = (User2) session.getAttribute("user");
        User2 user = new User2();

        if(OldUserInfo == null) {
            return "없는 유저입니다.";
        }
        user = userService.update(OldUserInfo,userInfo);

        System.out.println(OldUserInfo + "userId!@!@#!!2");
        System.out.println(userInfo + "info@@@@@@@@@@@@@@@@@@@!@#!!2");

        user.setOk();

        return "return" + user;
    }

//    @RequestMapping(value="/update/user", method=RequestMethod.POST)
//    public String save(HttpServletRequest request, UserView2 view, RedirectAttributes redirectAttributes){
//        User2 user = view.getUser();
//
//        if(user == null) {
//            user.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
//            redirectAttributes.addFlashAttribute(ConstantMapKey.ERROR_MSG_KEY.toString(), ErrMsg.CONTENT_NO_SAVE_ERR);
//            return "/error/validate";
//        }
//
//        String loginUserId = login.getUserId();
//
//        if( ! StringUtil.isEmpty(user.getUserId())) {
//            // 기존 사용자 조회 이후에 해당 항목만 수정함
//            // 이멜/사용자명/생년월일/성별/휴대폰/국적//사용자구분//로그인구분/마켓팅/상태/비고
//            User2 oldUser = userService.getUser(user.getUserId());
//            oldUser.setEmail(user.getEmail());
//            oldUser.setBirth(user.getBirth());
//            oldUser.setPlace(user.getPlace());
//            oldUser.setSex(user.getSex());
//            oldUser.setTeam(user.getTeam());
//            oldUser.setIntro(user.getIntro());
//            oldUser.setUserRole(user.getUserRole());
//            oldUser.setJob(user.getJob());
//            oldUser.setCareer(user.getCareer());
//            oldUser.setAwards(user.getAwards());
//            oldUser.setSocial(user.getSocial());
////            oldUser.setHeight(user.getHeight());
////            oldUser.setBust(user.getBust());
////            oldUser.setShose(user.getShose());
////            oldUser.setWaist(user.getWaist());
////            oldUser.setHair(user.getHair());
////            oldUser.setHip(user.getHip());
//            user = userService.save(oldUser);
//        }else {
//            user = userService.save(user);
//        }
//
//        user.setOk();
//
//        return "user";
//    }
}
