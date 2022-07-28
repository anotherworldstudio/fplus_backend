package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.UserRole;
import com.kbeauty.gbt.entity.view.ImageData;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "USER")
@Data
@EqualsAndHashCode(callSuper=false)
public class User extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long seq;

	@Column(name = "userid")	private String userId;
	
	@Column(name = "username")  private String userName;	
		
	@Column(name = "birthday")	private String birthDay;
	private String sex;
	private String country;
	private String cellphone;
	private String email;

	@Column(name = "addrename")	private String addreName;			
	@Column(name = "addretype")	private String addreType;
	private String zipcode;
	private String addre;
	@Column(name = "addredetail") private String addreDetail;
	@Column(name = "userrole") private String userRole;
	
	@Column(name = "imagedir") private String imageDir;
	@Column(name = "imagename") private String imageName;
	private String comment;
	private String language;
	@Column(name = "oauthtype") private String oauthType;
	private String status;
	@Column(name = "keeploginyn") private String keepLoginyn;
	@Column(name = "agreeyn") private String agreeYn;
	@Column(name = "privateyn	") private String privateYn;
	@Column(name = "ageoveryn") private String ageoverYn;
	@Column(name = "marketingyn") private String marketingYn;
	@Column(name = "logintype") private String loginType;
	@Column(name = "website") private String webSite;
	@Column(name = "userapplekey") private String userAppleKey;
	@Column(name = "bsti") private String bsti;
	
	@Transient	private String accountType; //공개/비공개 여부 (Opne/Close)
	
	@Transient	private String password;
	@Transient	private String imgUrl;
	@Transient	private String token;
	@Transient	private String userRoleName;
	@Transient  private ImageData imgData;
	
	
	public boolean isSame(User user) {
		if(userId == null) return false;
		
		if(userId.equals(user.getUserId())) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isAdmin() {
		return UserRole.ADMIN.getCode().equals(userRole);
	}
	public boolean isProfessional() {
		return UserRole.PROFESSIONAL.getCode().equals(userRole);
	}
	
	public boolean isUserImg() {
		return ! StringUtil.isEmpty(imageDir);
	}
	
	public boolean isHomepageUser() {		
		return UserRole.HOMEPAGE.getCode().equals(userRole);
	}
}
