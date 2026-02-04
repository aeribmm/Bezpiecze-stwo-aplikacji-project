package com.s58703.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Configuration
public class CacheConfig implements WebMvcConfigurer {
    @Bean
    public Filter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CacheControlInterceptor());
    }
    private static class CacheControlInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Object handler) {
            String path = request.getRequestURI();
            if (path.startsWith("/api/v1/auth")) {
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
            } else if (path.startsWith("/api/todos")) {
                response.setHeader("Cache-Control", "private, max-age=60, must-revalidate");
                String lastModified = ZonedDateTime.now(ZoneId.of("GMT"))
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME);
                response.setHeader("Last-Modified", lastModified);
            } else {
                response.setHeader("Cache-Control", "public, max-age=300");
            }
            return true;
        }
    }
}