package com.coforge.hsbcdma.config;

import com.coforge.hsbcdma.entity.User;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import com.coforge.hsbcdma.service.CustomUserDetailsService;
import com.coforge.hsbcdma.service.TokenBlacklistService;
import com.coforge.hsbcdma.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.unbescape.json.JsonEscape.escapeJson;

/**
 * Filter responsible for validating JWT tokens for each incoming HTTP request.
 *
 * It intercepts requests, extracts the JWT from the Authorization header,
 * validates the token, and sets the authentication context if the token is valid.
 *
 * This filter is executed once per request as part of the Spring Security filter chain.
 *
 * @author Vandana Pal
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // ✅ VERY IMPORTANT: skip CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        String username = null;

        String header = request.getHeader("Authorization");

        logger.info("************* header info from Authorization {} ",header);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        logger.info("************* token info substring {} ",token);

        // Block logged-out tokens
        if (tokenBlacklistService.isBlacklisted(token)) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token is invalidated (logged out). Please login again."
            );
            return;
        }
        try {
               username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {

                    User user = userAccountRepository
                            .findByUserId(username)
                            .orElse(null);


                    // 🔒 BLOCK OLD / PARALLEL TOKENS
                    if (!token.equals(user.getActiveToken())) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                "User logged in from another device");
                        return;
                    }


                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException ex){
            sendErrorResponse(response, "JWT Token Expired. Please re-login");
        } catch (MalformedJwtException ex){
            sendErrorResponse(response, "Malformed Token");
        }catch (IllegalArgumentException ex){
            sendErrorResponse(response, "Invalid Token. Token is Empty or null");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        if (response.isCommitted()) {
            return; // don't write twice
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("WWW-Authenticate", "Bearer"); // helps clients understand auth scheme

        // Build proper JSON (simple and safe)
        String body = String.format(
                "{ \"error\": \"%s\", \"message\": \"%s\", \"status\": %d, \"timestamp\": \"%s\" }",
                escapeJson("Invalid Token"), escapeJson(message), HttpServletResponse.SC_UNAUTHORIZED, java.time.Instant.now().toString()
        );
        response.getWriter().write(body);
        response.getWriter().flush();
    }
}
