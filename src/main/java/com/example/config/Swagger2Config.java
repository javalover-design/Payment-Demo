package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author lambda
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    ApiInfoBuilder apiInfoBuilder=new ApiInfoBuilder();

    @Bean
    public Docket getDocket(){
        return new Docket( DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder.title("微信支付案例").description("payment -demo").build());
    }

}
