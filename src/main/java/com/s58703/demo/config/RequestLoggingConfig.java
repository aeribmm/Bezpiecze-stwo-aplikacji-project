package com.s58703.demo.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Configuration
public class RequestLoggingConfig {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingConfig.class);

    @Bean
    public Filter requestLoggingFilter() {
        return new RequestResponseLoggingFilter();
    }

    private static class RequestResponseLoggingFilter implements Filter {

        private static final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            ContentCachingRequestWrapper requestWrapper =
                    new ContentCachingRequestWrapper(httpRequest, 1024 * 1024);
            ContentCachingResponseWrapper responseWrapper =
                    new ContentCachingResponseWrapper(httpResponse);

            long startTime = System.currentTimeMillis();

            try {
                logRequest(requestWrapper);

                chain.doFilter(requestWrapper, responseWrapper);

            } catch (Exception e) {
                logger.error("❌ ERROR during request processing: {}", e.getMessage(), e);
                throw e;

            } finally {
                long duration = System.currentTimeMillis() - startTime;

                logResponse(responseWrapper, duration);

                responseWrapper.copyBodyToResponse();
            }
        }

        private void logRequest(ContentCachingRequestWrapper request) {
            String timestamp = LocalDateTime.now().format(formatter);

            StringBuilder logMessage = new StringBuilder();
            logMessage.append("\n========== INCOMING REQUEST ==========\n");
            logMessage.append("⏰ Timestamp: ").append(timestamp).append("\n");
            logMessage.append("🔵 Method: ").append(request.getMethod()).append("\n");
            logMessage.append("🔗 URI: ").append(request.getRequestURI()).append("\n");

            if (request.getQueryString() != null) {
                logMessage.append("❓ Query: ").append(request.getQueryString()).append("\n");
            }

            logMessage.append("🌐 Remote Address: ").append(request.getRemoteAddr()).append("\n");

            logMessage.append("📋 Headers:\n");
            Collections.list(request.getHeaderNames()).forEach(headerName -> {
                String headerValue = request.getHeader(headerName);
                if (headerName.equalsIgnoreCase("Authorization")) {
                    headerValue = "Bearer ***" + (headerValue.length() > 10 ?
                            headerValue.substring(headerValue.length() - 10) : "***");
                }
                logMessage.append("   ").append(headerName).append(": ").append(headerValue).append("\n");
            });

            if ("POST".equals(request.getMethod()) ||
                    "PUT".equals(request.getMethod()) ||
                    "PATCH".equals(request.getMethod())) {

                byte[] content = request.getContentAsByteArray();
                if (content.length > 0) {
                    String body = new String(content, StandardCharsets.UTF_8);
                    body = body.replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"");
                    logMessage.append("📦 Body: ").append(body).append("\n");
                }
            }

            logMessage.append("=====================================");
            logger.info(logMessage.toString());
        }

        private void logResponse(ContentCachingResponseWrapper response, long duration) {
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("\n========== OUTGOING RESPONSE ==========\n");
            logMessage.append("⏱️  Duration: ").append(duration).append(" ms\n");
            logMessage.append("📊 Status: ").append(response.getStatus()).append(" ");
            logMessage.append(getStatusEmoji(response.getStatus())).append("\n");

            logMessage.append("📋 Headers:\n");
            response.getHeaderNames().forEach(headerName -> {
                logMessage.append("   ").append(headerName).append(": ")
                        .append(response.getHeader(headerName)).append("\n");
            });

            byte[] content = response.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                if (body.length() > 500) {
                    body = body.substring(0, 500) + "... (truncated)";
                }
                logMessage.append("📦 Body: ").append(body).append("\n");
            }

            logMessage.append("======================================");

            if (response.getStatus() >= 500) {
                logger.error(logMessage.toString());
            } else if (response.getStatus() >= 400) {
                logger.warn(logMessage.toString());
            } else {
                logger.info(logMessage.toString());
            }
        }

        private String getStatusEmoji(int status) {
            if (status >= 200 && status < 300) {
                return "✅ OK";
            } else if (status >= 300 && status < 400) {
                return "🔄 Redirect";
            } else if (status >= 400 && status < 500) {
                return "⚠️ Client Error";
            } else if (status >= 500) {
                return "❌ Server Error";
            }
            return "";
        }
    }
}