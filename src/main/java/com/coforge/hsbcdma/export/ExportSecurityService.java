package com.coforge.hsbcdma.export;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ExportSecurityService {

    public Set<String> getCurrentRoles() {

        Authentication auth =
                SecurityContextHolder.getContext()
                        .getAuthentication();


        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_")
                        ? role.substring(5)
                        : role)
                .collect(Collectors.toSet());

    }

    public boolean hasRole(Set<String> roles, String role) {
        return roles.contains(role)
                || roles.contains("ROLE_" + role);
    }
}
