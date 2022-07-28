package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Component
@Table(name = "LOGIN")
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Data
@EqualsAndHashCode(callSuper=false)
public class Login extends CommonDomain {	
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long seq;

	@Column(name = "userid")	private String userId;
	
	@Transient	
	private String userName;
	
	@Transient	
	private String userRole;
	
	@Transient	
	private String password;
	
	@Transient
	private String language;
	
	@Transient 
	private String oauthType;
	
	@Transient
	private String status;
	
	@Transient 
	private String keepLoginyn;
	
	@Transient 
	private String loginType;

}
