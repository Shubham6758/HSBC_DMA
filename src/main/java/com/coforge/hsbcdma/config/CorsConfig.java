package com.coforge.hsbcdma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // ✅ VERY IMPORTANT: match your frontend exactly
        config.setAllowedOrigins(List.of(
                "http://hsbcdma-frontend.s3-website.eu-north-1.amazonaws.com"
        ));

        // ✅ Browser preflight requires OPTIONS
        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
        ));

        // ✅ Must explicitly allow Content-Type
        config.setAllowedHeaders(List.of("*"));

        // ✅ JWT-based APIs → no cookies
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}