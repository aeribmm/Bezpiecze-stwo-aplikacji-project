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

/**
 * Cache Configuration
 * Implements: ETag, Last-Modified, Cache-Control headers
 *
 * Features:
 * - ETag generation for response content validation
 * - Last-Modified timestamps for resource freshness
 * - Cache-Control directives for caching behavior
 */
@Configuration
public class CacheConfig implements WebMvcConfigurer {

    /**
     * ETag Filter
     * Generates ETag headers based on response content
     * Enables conditional requests (If-None-Match)
     */
    @Bean
    public Filter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    /**
     * Cache Control Interceptor
     * Adds Cache-Control and Last-Modified headers to responses
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CacheControlInterceptor());
    }

    /**
     * Custom interceptor to add cache headers
     */
    private static class CacheControlInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Object handler) {

            String path = request.getRequestURI();

            // Different cache strategies for different endpoints
            if (path.startsWith("/api/v1/auth")) {
                // Authentication endpoints - no cache
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");

            } else if (path.startsWith("/api/todos")) {
                // TODO endpoints - private cache with validation
                response.setHeader("Cache-Control", "private, max-age=60, must-revalidate");

                // Add Last-Modified header
                String lastModified = ZonedDateTime.now(ZoneId.of("GMT"))
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME);
                response.setHeader("Last-Modified", lastModified);

            } else {
                // Other endpoints - default caching
                response.setHeader("Cache-Control", "public, max-age=300");
            }

            return true;
        }
    }
}