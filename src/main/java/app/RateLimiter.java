package app;

import io.github.bucket4j.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter implements HandlerInterceptor {

        private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

        private Bucket createNewBucket() {
            Bandwidth limit = Bandwidth.simple(10, Duration.ofMinutes(1));
            return Bucket.builder().addLimit(limit).build();
        }

        private Bucket resolveBucket(String key) {
            return buckets.computeIfAbsent(key, k -> createNewBucket());
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String ip = request.getRemoteAddr();
            Bucket bucket = resolveBucket(ip);

            if (bucket.tryConsume(1)) { return true; }

            response.setStatus(429);
            response.getWriter().write("Too many requests");
            return false;
        }
}
