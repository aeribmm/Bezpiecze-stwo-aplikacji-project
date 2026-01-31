package com.s58703.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Configuration for Security Headers
 * Implements: CSP, X-Content-Type-Options, HSTS, Referrer-Policy
 */
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    @Bean
    public Filter securityHeadersFilter() {
        return new SecurityHeadersFilter();
    }

    /**
     * Custom filter to add security headers to all responses
     */
    private static class SecurityHeadersFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // Content Security Policy (CSP)
            // Restricts sources from which content can be loaded
            httpResponse.setHeader("Content-Security-Policy",
                    "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                            "style-src 'self' 'unsafe-inline'; " +
                            "img-src 'self' data: https:; " +
                            "font-src 'self' data:; " +
                            "connect-src 'self'; " +
                            "frame-ancestors 'self'");

            // X-Content-Type-Options
            // Prevents MIME-sniffing attacks
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");

            // HTTP Strict Transport Security (HSTS)
            // Forces HTTPS connections for 1 year, including subdomains
            httpResponse.setHeader("Strict-Transport-Security",
                    "max-age=31536000; includeSubDomains; preload");

            // Referrer-Policy
            // Controls referrer information sent with requests
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // X-Frame-Options (additional protection against clickjacking)
            httpResponse.setHeader("X-Frame-Options", "DENY");

            // X-XSS-Protection (legacy, but still useful for older browsers)
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

            // Permissions-Policy (formerly Feature-Policy)
            httpResponse.setHeader("Permissions-Policy",
                    "geolocation=(), microphone=(), camera=(), payment=()");

            chain.doFilter(request, response);
        }
    }
}