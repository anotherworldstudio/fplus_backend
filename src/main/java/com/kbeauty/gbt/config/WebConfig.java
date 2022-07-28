package com.kbeauty.gbt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.kbeauty.gbt.interceptor.JwtInterceptor;
import com.kbeauty.gbt.interceptor.JwtWebInterceptor;



@Configuration
//public class WebConfig implements WebMvcConfigurer {
public class WebConfig extends WebMvcConfigurationSupport {	
	
    private static final String[] EXCLUDE_PATHS = {
            "/v1/login",
            "/v1/skin/deep_skin_web",
            "/w1/login",
            "/w1/skin_share/**",
            "/w1/signin",
            "/w1/oauth2/code/kakao",
            "/w1/login_check",
            "/error/**"
    };

    @Autowired
    private JwtInterceptor jwtInterceptor;
    
    @Autowired
    private JwtWebInterceptor jwtWebInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(jwtInterceptor).addPathPatterns("/v1/**").excludePathPatterns(EXCLUDE_PATHS);		
		registry.addInterceptor(jwtWebInterceptor).addPathPatterns("/w1/**").excludePathPatterns(EXCLUDE_PATHS);		
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	registry.addResourceHandler("/vendors/**").addResourceLocations("classpath:/static/vendors/");
    	registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");    	
    	registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");    	
    	registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
    	registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
    	registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");    	
	}
    
    @Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
		CommonsRequestLoggingFilter c = new CommonsRequestLoggingFilter();
		c.setIncludeHeaders(true);
		c.setIncludeQueryString(true);
		c.setIncludePayload(true);
		c.setIncludeClientInfo(true);
		c.setMaxPayloadLength(100000);
		return c;
	}    
    
    
//    @Bean
//	public HttpMessageConverter<?> htmlEscapingConverter() {
//		ObjectMapper objectMapper = new ObjectMapper();
////		objectMapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
//		objectMapper.registerModule(new JavaTimeModule());
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//		MappingJackson2HttpMessageConverter htmlEscapingConverter = new MappingJackson2HttpMessageConverter();
//		htmlEscapingConverter.setObjectMapper(objectMapper);
//
//		return htmlEscapingConverter;
//	}
//
//	@Override
//	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//		converters.add(htmlEscapingConverter());
//		super.addDefaultHttpMessageConverters(converters);  // default Http Message Converter  추가
//	}
    
}
