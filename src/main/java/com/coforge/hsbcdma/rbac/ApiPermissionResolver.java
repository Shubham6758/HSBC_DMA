package com.coforge.hsbcdma.rbac;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiPermissionResolver {

    private final ApiPermissionMappingRepository
            apiPermissionMappingRepository;

    private final PathPatternParser pathPatternParser =
            new PathPatternParser();

    public Optional<ApiPermissionMapping> resolve(
            String httpMethod,
            String requestPath
    ) {
        if (httpMethod == null || requestPath == null) {
            return Optional.empty();
        }

        return apiPermissionMappingRepository
                .findAllByActiveTrue()
                .stream()
                .filter(mapping ->
                        httpMethod.equalsIgnoreCase(
                                mapping.getHttpMethod()
                        )
                )
                .filter(mapping ->
                        matches(
                                mapping.getPathPattern(),
                                requestPath
                        )
                )
                .findFirst();
    }

    private boolean matches(
            String configuredPattern,
            String requestPath
    ) {
        try {
            PathPattern pathPattern =
                    pathPatternParser.parse(configuredPattern);

            return pathPattern.matches(
                    PathContainer.parsePath(requestPath)
            );
        } catch (Exception exception) {
            return false;
        }
    }
}