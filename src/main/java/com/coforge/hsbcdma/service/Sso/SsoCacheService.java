package com.coforge.hsbcdma.service.Sso;

import com.coforge.hsbcdma.dto.AuthResponseDTO;
import com.coforge.hsbcdma.dto.sso.SsoLoginSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SsoCacheService {

    private final Map<String, SsoLoginSession> cache =
            new ConcurrentHashMap<>();

    public void save(
            String code,
            AuthResponseDTO response) {

        cache.put(
                code,
                new SsoLoginSession(
                        response,
                        LocalDateTime.now().plusMinutes(2),
                        false
                )
        );
    }

    public AuthResponseDTO exchange(
            String code) {

        SsoLoginSession session =
                cache.get(code);

        if (session == null) {
            throw new RuntimeException(
                    "Invalid login code"
            );
        }

        if (session.isUsed()) {
            throw new RuntimeException(
                    "Code already used"
            );
        }

        if (session.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            cache.remove(code);

            throw new RuntimeException(
                    "Code expired"
            );
        }

        session.setUsed(true);

        AuthResponseDTO response =
                session.getAuthResponse();

        cache.remove(code);

        return response;
    }
}
