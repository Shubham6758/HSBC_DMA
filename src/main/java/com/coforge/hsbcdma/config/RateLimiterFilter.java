package com.coforge.hsbcdma.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class RateLimiterFilter implements Filter {

    // Store buckets per client
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    // ✅ Create rate limit (100 requests / minute)
    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    // ✅ Resolve bucket per user/IP
    private Bucket resolveBucket(String key) {
        return bucketCache.computeIfAbsent(key, k -> createBucket());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // ✅ Use IP (you can change later to userId from JWT)
        String clientIp = getClientIP(req);

        Bucket bucket = resolveBucket(clientIp);

        // ✅ Try consume token
        if (bucket.tryConsume(1)) {

            // Optional headers (useful for frontend)
            res.setHeader("X-Rate-Limit-Remaining",
                    String.valueOf(bucket.getAvailableTokens()));

            chain.doFilter(request, response);

        } else {
            res.setStatus(429);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Too many requests. Try again later.\"}");
        }
    }

    // ✅ Handle real client IP (important behind CloudFront / proxy)
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");

        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
