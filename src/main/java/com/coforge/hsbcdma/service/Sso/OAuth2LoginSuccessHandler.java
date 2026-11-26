package com.coforge.hsbcdma.service.Sso;

import com.coforge.hsbcdma.dto.AuthResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler
        extends SavedRequestAwareAuthenticationSuccessHandler {

    private final SsoService ssoService;
    private final ObjectMapper objectMapper;
    private final SsoCacheService ssoCacheService;
//    @Override
//    public void onAuthenticationSuccess(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Authentication authentication)
//            throws IOException {
//        log.info("===== SSO SUCCESS =====");
//        log.info("Authentication = {}", authentication);
//        log.info("Session Id = {}", request.getSession().getId());
////        OAuth2User oauthUser =
////                (OAuth2User) authentication.getPrincipal();
////
////        String email =
////                oauthUser.getAttribute(
////                        "preferred_username");
////
////        AuthResponseDTO authResponse =
////                ssoService.processSSOUser(email);
////
////        response.setStatus(HttpServletResponse.SC_OK);
////        response.setContentType("application/json");
////        response.setCharacterEncoding("UTF-8");
////        objectMapper.writeValue(
////                response.getWriter(),
////                authResponse
////        );
//        String redirectUrl =
//                "https://dma.coforge.com/sso-success";
//
//        response.sendRedirect(redirectUrl);
//    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();

        String email =
                oauthUser.getAttribute(
                        "preferred_username"
                );

        AuthResponseDTO authResponse =
                ssoService.processSSOUser(email);

        String loginCode =
                UUID.randomUUID().toString();

        ssoCacheService.save(
                loginCode,
                authResponse
        );

        response.sendRedirect(
                "https://dma.coforge.com/sso-success?code="
                        + loginCode
        );
    }
}

