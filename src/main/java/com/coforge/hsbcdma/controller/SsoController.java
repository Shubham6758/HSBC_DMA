package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.AuthResponseDTO;
import com.coforge.hsbcdma.service.Sso.SsoCacheService;
import com.coforge.hsbcdma.service.Sso.SsoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class SsoController {
    private final SsoService ssoService;
    private final SsoCacheService ssoCacheService;
    @GetMapping("/sso/login")
    public void login(
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                "/oauth2/authorization/azure"
        );
    }

    @GetMapping("/exchange")
    public ResponseEntity<AuthResponseDTO> exchange(
        @RequestParam String code) {

        return ResponseEntity.ok(
                ssoCacheService.exchange(code)
        );
    }
}