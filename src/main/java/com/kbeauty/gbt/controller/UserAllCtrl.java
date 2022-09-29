package com.kbeauty.gbt.controller;

import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.User2;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.view.FplusUserView;
import com.kbeauty.gbt.service.UserService2;
import com.kbeauty.gbt.util.StringUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class UserAllCtrl {

    @Autowired
    private UserService2 userService;

//    수정
    @RequestMapping(value = "/update_user",method = RequestMethod.POST)
    public String updateImg(HttpServletRequest request, FplusUserView view, RedirectAttributes redirectAttributes) {
        User2 user = view.getUser();

        if(user == null) {
            user.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
            return "없는 유저입니다..";
        }

//        String loginUserId = "d0864806d3b04612b16aa92a45ad97e7";

        if(!StringUtil.isEmpty(user.getUserId())) {
            User2 oldUser = userService.getUser(user.getUserId());
            oldUser.setEmail(user.getEmail());
            oldUser.setBirth(user.getBirth());
            oldUser.setSex(user.getSex());
            oldUser.setUserRole(user.getUserRole());
            user = userService.save(oldUser);
        } else {
            user = userService.save(user);
        }

        user.setOk();

        String nextPage = "redirect:/";
        return nextPage;
    }

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
}
