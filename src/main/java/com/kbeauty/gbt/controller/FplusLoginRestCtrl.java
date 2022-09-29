package com.kbeauty.gbt.controller;

import com.github.scribejava.core.oauth.OAuth20Service;
import com.kbeauty.gbt.entity.domain.AppleLogin;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.User2;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.view.CommonView;
import com.kbeauty.gbt.entity.view.FplusUserView;
import com.kbeauty.gbt.service.LoginService;
import com.kbeauty.gbt.service.UserService;
import com.kbeauty.gbt.service.UserService2;
import com.kbeauty.gbt.util.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
//@Api(value = "Fplus Login REST Controller")
@Slf4j
public class FplusLoginRestCtrl {

    @Resource
    Login login;

    //	회원가입 로직
    @Autowired
    private UserService2 service;

    @Autowired
    private LoginService defaultService;

    @ApiOperation(value = "save", notes = "사용자 저장 함수.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!"),
            @ApiResponse(code = 500, message = "Internal Server Error !!"),
            @ApiResponse(code = 404, message = "Not Found !!")
    })


    @RequestMapping(value = "/save/user", method = RequestMethod.POST)
    public User2 save(@RequestBody User2 user) {
        user = service.save(user);
        user.setOk();
        return user;
    }

    //	아이디 중복 체크 API, 중복이면 TRUE, 아니면 FALSE 뱉어냄
//	JPA 함수
    @GetMapping("/emailCheck/{email}/exists")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.checkEmail(email));
    }


//    TODO: LOGIN
@RequestMapping(value = "/login/fplus", method = RequestMethod.POST)
public String login(@RequestBody User2 vo, HttpServletRequest req, HttpServletResponse response,RedirectAttributes rttr) throws Exception{

    HttpSession session = req.getSession();
//    System.out.println("email만 체크:"+req.getParameter("email"));
//    System.out.println("User2 vo : " + vo.toString());
    User2 login = service.login(vo);
    System.out.println("login : " + login.toString()) ;
    if(login == null) {
        session.setAttribute("user", null);
        rttr.addFlashAttribute("msg", false);
    }else {
        session.setAttribute("user", login);
    }
    System.out.println(login);
    return "redirect:/";
}

    @RequestMapping(value = "/logout/fplus", method = RequestMethod.GET)
    public String logout(HttpSession session) throws Exception{
        System.out.println("!!!!!!!!!!!!!!!!SESSION!!!!" + session.getAttribute("user"));

        session.invalidate();


        return "redirect:/";
    }

}


