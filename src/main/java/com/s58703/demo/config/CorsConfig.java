package com.s58703.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) Configuration
 * Defines which origins, methods, and headers are allowed for cross-origin requests
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins (domains that can make requests to this API)
        // For development: use specific origins or "*" for testing
        // For production: ALWAYS specify exact origins, never use "*"
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",  // React dev server
                "http://localhost:4200",  // Angular dev server
                "http://localhost:8080",  // Local testing
                "https://localhost:8443"  // HTTPS local testing
        ));

        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS",
                "HEAD"
        ));

        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Exposed headers (headers that the browser can access)
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "ETag",
                "Last-Modified",
                "Cache-Control"
        ));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Max age for preflight request caching (in seconds)
        // Browser will cache the preflight response for this duration
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Apply CORS configuration to all endpoints
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}