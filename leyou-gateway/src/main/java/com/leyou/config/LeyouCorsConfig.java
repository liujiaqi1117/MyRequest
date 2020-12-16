package com.leyou.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfig {

    @Bean
    public CorsFilter corsFilter(){

        //添加cors的配置信息
        CorsConfiguration config = new CorsConfiguration();
        //设置允许的域 不要设置为* 要不cookie不能用了
        config.addAllowedOrigin("http://manage.leyou.com");
        config.addAllowedOrigin("http://www.leyou.com");
        //设置cookie允许
        config.setAllowCredentials(true);
        //请求允许的方式
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PATCH");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("OPTIONS");
        //允许的头信息
        config.addAllowedHeader("*");

        //添加映射路径 拦截所有请求
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",config);
        return new CorsFilter(configurationSource);

    }

}
