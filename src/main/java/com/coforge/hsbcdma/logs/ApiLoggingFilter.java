package com.coforge.hsbcdma.logs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Slf4j
public class ApiLoggingFilter extends OncePerRequestFilter {
    private static final int MAX_LOG_LENGTH = 2000;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.contains("/actuator") ||
                path.contains("/swagger") ||
                path.contains("/health");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper req =
                new ContentCachingRequestWrapper(request,10*1024);

        ContentCachingResponseWrapper res =
                new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(req, res);
        } finally {

            long timeTaken = System.currentTimeMillis() - startTime;

            String username = getUser();
            String ip = getClientIP(request);


            String requestBody = limit(mask(getRequestBody(req)));

            String responseBody = isBinaryResponse(response)
                    ? "[BINARY DATA]"
                    : limit(mask(getResponseBody(res)));


            log.info("""
                    
                    🔹 API LOG START
                    ➤ User        : {}
                    ➤ IP          : {}
                    ➤ Time        : {}
                    ➤ Method      : {}
                    ➤ URI         : {}
                    ➤ Status      : {}
                    ➤ Duration    : {} ms
                    ➤ Request     : {}
                    ➤ Response    : {}
                    🔹 API LOG END
                    """,
                    username,
                    ip,
                    LocalDateTime.now(),
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    timeTaken,
                    requestBody,
                    responseBody

            );

            res.copyBodyToResponse();
        }
    }

    private String getUser() {
        try {
            return SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();
        } catch (Exception e) {
            return "ANONYMOUS";
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        return (xf == null) ? request.getRemoteAddr() : xf.split(",")[0];
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        return buf.length > 0 ? new String(buf, StandardCharsets.UTF_8) : "";
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        return buf.length > 0 ? new String(buf, StandardCharsets.UTF_8) : "";
    }

    private String mask(String body) {
        if (body == null) return null;

        return body
                .replaceAll("(?i)\"password\"\\s*:\\s*\".*?\"", "\"password\":\"****\"")
                .replaceAll("(?i)\"token\"\\s*:\\s*\".*?\"", "\"token\":\"****\"")
                .replaceAll("(?i)\"refreshToken\"\\s*:\\s*\".*?\"", "\"refreshToken\":\"****\"")
                .replaceAll("(?i)\"panNumber\"\\s*:\\s*\".*?\"", "\"panNumber\":\"****\"");
    }

    private String limit(String body) {
        if (body == null) return null;

        if (body.length() > MAX_LOG_LENGTH) {
            return body.substring(0, MAX_LOG_LENGTH) + "... [TRUNCATED]";
        }
        return body;
    }

    private boolean isBinaryResponse(HttpServletResponse response) {
        String contentType = response.getContentType();
        return contentType != null &&
                (contentType.contains("application/octet-stream") ||
                        contentType.contains("application/pdf") ||
                        contentType.contains("image") ||
                        contentType.contains("excel"));
    }
}
