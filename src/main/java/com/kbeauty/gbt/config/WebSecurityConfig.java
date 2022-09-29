package com.kbeauty.gbt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    public void configure(WebSecurity web) throws Exception {
    	super.configure(web);
    	
//    	web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui",
//                "/swagger-resources", "/configuration/security",
//                "/swagger-ui.html", "/webjars/**","/swagger/**");
    	    
    	    web.ignoring().antMatchers("/v2/api-docs/**");
    	    web.ignoring().antMatchers("/swagger.json");
    	    web.ignoring().antMatchers("/swagger-ui.html");
    	    web.ignoring().antMatchers("/swagger-resources/**");
    	    web.ignoring().antMatchers("/webjars/**");
    	    web.ignoring().antMatchers("/vendors/**");
    	    web.ignoring().antMatchers("/css/**");
    	    web.ignoring().antMatchers("/images/**");
    	    web.ignoring().antMatchers("/js/**");
    	
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
        // disables cors and csrf
//     // authenticate
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/v1/**").permitAll()
                .antMatchers("/w1/**").permitAll()                
//                .antMatchers("/vendors/**").permitAll()                
//                .antMatchers("/css/**").permitAll()                
//                .antMatchers("/images/**").permitAll()                
//                .antMatchers("/js/**").permitAll()                
                .antMatchers("/error/**").permitAll()
                .antMatchers("/login/**").permitAll()
                .anyRequest().authenticated();
        
        this.log.error("=================");
        
        http.httpBasic().disable()
        .cors().and()
        .csrf()
        .disable();
//                .formLogin()
//                .loginProcessingUrl("/login");

        
        
//                .and()
//                .oauth2Login();
//                .userInfoEndpoint()
//                .customUserType(KakaoOAuth2User.class, "kakao").and()


        // disables session creation on Spring Security
//        http.sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
 
}