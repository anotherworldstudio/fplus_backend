package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "APPLELOGIN")
@Data
@EqualsAndHashCode(callSuper = false)
public class AppleLogin extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long seq;
	
	@Column(name = "useridentifier") 	 private String userIdentifier;
	@Column(name = "idtoken")			 private String idToken;
	@Column(name = "authorizationcode")  private String authorizationcode;
	@Column(name = "email") 			 private String email;
	@Column(name = "givenname")			 private String givenname;
	@Column(name  = "familyname") 		 private String familyname;
	@Column(name  = "nickname") 		 private String nickname;
	@Column(name = "userid") 			 private String userId;
	@Column(name  = "status") 		 	 private String status;
	
	
}
