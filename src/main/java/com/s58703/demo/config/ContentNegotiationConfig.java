package com.s58703.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Content Negotiation Configuration
 * Enables multiple response formats based on Accept header or URL extension
 *
 * Supported formats:
 * - JSON (default)
 * - XML
 * - Plain text
 *
 * Usage examples:
 * - Accept: application/json → JSON response
 * - Accept: application/xml → XML response
 * - /api/todos.json → JSON response
 * - /api/todos.xml → XML response
 */
@Configuration
public class ContentNegotiationConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                // Use Accept header
                .ignoreAcceptHeader(false)

                // Optional: enable request parameter (?mediaType=json)
                .favorParameter(true)
                .parameterName("mediaType")

                // Default content type
                .defaultContentType(MediaType.APPLICATION_JSON)

                // Supported media types
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("text", MediaType.TEXT_PLAIN);
    }
}