package com.coforge.hsbcdma.rbac;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DynamicRbacAuthorizationFilter extends OncePerRequestFilter {

    private final ApiPermissionResolver apiPermissionResolver;
    private final DynamicPermissionService dynamicPermissionService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * The JWT filter must execute before this filter.
         * Therefore, authentication should already exist here.
         */
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            writeError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication is required"
            );
            return;
        }

        String httpMethod = request.getMethod();
        String requestPath = getApplicationPath(request);

        Optional<ApiPermissionMapping> mappingOptional =
                apiPermissionResolver.resolve(httpMethod, requestPath);

        /*
         * Deny-by-default:
         * Every non-public API must have a database mapping.
         */
        if (mappingOptional.isEmpty()) {
            writeError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "You are not authorized."
            );
            return;
        }

        ApiPermissionMapping mapping = mappingOptional.get();

        /*
         * permissionRequired = false means:
         * Any valid authenticated user may access this API.
         */
        if (!mapping.isPermissionRequired()) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * If permissionRequired = true,
         * the mapping must contain a valid action.
         */
        if (mapping.getAction() == null
                || mapping.getAction().getId() == null) {

            writeError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "Permission configuration is invalid for this API"
            );
            return;
        }

        String authenticatedUserId = authentication.getName();
        Long requiredActionId = mapping.getAction().getId();

        boolean allowed = dynamicPermissionService.userHasAction(
                authenticatedUserId,
                requiredActionId
        );

        if (!allowed) {
            writeError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to perform this action"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getApplicationPath(HttpServletRequest request) {

        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (contextPath != null
                && !contextPath.isBlank()
                && requestUri.startsWith(contextPath)) {

            String applicationPath = requestUri.substring(
                    contextPath.length()
            );

            return applicationPath.isBlank()
                    ? "/"
                    : applicationPath;
        }

        return requestUri;
    }

    private void writeError(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Map<String, Object> responseBody = new LinkedHashMap<>();

        responseBody.put("success", false);
        responseBody.put("status", status);
        responseBody.put("message", message);
        responseBody.put("timestamp", LocalDateTime.now().toString());

        objectMapper.writeValue(
                response.getWriter(),
                responseBody
        );
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String method = request.getMethod();
        String path = getApplicationPath(request);

        /*
         * CORS preflight request.
         */
        if (HttpMethod.OPTIONS.matches(method)) {
            return true;
        }

        /*
         * Only APIs accessible without authentication should be listed.
         */
        return path.equals("/auth_user/login")
                || path.equals("/auth_user/refresh")
                || path.equals("/auth_user/change-password")
                || path.equals("/api/auth/sso/login")
                || path.equals("/api/auth/exchange")
                || path.equals("/auth_user/forgot-password");
    }
}
