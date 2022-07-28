package com.kbeauty.gbt.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kbeauty.gbt.entity.CommonConstants;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//            .select()
//            .apis(RequestHandlerSelectors.any()) // 모든 RequestMapping URI 추출
//            // .apis(RequestHandlerSelectors.basePackage("com")) // 패키지 기준 추출
//            .paths(PathSelectors.ant("/v1/**")) // 경로 패턴 URI만 추출
//            .paths(PathSelectors.ant("/login/**")) // 경로 패턴 URI만 추출
//            .build()
//            .apiInfo(apiInfo());
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfo(
//            "NEO REST API ", //title
//            "스프링부트 샘플 프 로젝트", //description
//            "v1", //version
//            "서비스 약관 URL", //termsOfServiceUrl
//            "linked2ev", //contactName
//            "License", //license
//            "localhost:8080"); //licenseUrl
//    }
    
	@Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.kbeauty.gbt"))
                .paths(PathSelectors.ant("/v1/**"))
                .build()
                .apiInfo(metaData())
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()));

    }
	
//    @Bean
//    public Docket swaggerApi() {
//        return new Docket(DocumentationType.SWAGGER_2).apiInfo(swaggerInfo()).select()
//                .apis(RequestHandlerSelectors.basePackage("com.kbeauty.gbt"))
//                .paths(PathSelectors.any())
//                .build()
//                .useDefaultResponseMessages(false); // 기본으로 세팅되는 200,401,403,404 메시지를 표시 하지 않음
//    }
//
//    private ApiInfo swaggerInfo() {
//        return new ApiInfoBuilder().title("Spring API Documentation")
//                .description("앱 개발시 사용되는 서버 API에 대한 연동 문서입니다")
//                .license("labellelab").licenseUrl("http://www.labellelab.com").version("1").build();
//    }
//    
    
    private ApiInfo metaData() {    	
        return new ApiInfoBuilder()
                .title("Beautage REST API")
                .description("앱 개발시 사용되는 서버 API에 대한 연동 문서입니다")
                .version("0.1.0")
                .termsOfServiceUrl("Terms of service")
                .license("LaBelleLab.com")
                .licenseUrl("http://www.labellelab.com")
                .build();
    }
    
    private ApiKey apiKey() {
        return new ApiKey("JWT", CommonConstants.HEADER_AUTH, "header");
    }

    private SecurityContext securityContext() {
        return springfox
                .documentation
                .spi.service
                .contexts
                .SecurityContext
                .builder()
                .securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
    }
    
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
    
}