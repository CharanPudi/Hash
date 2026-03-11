import java.util.*;
import java.util.concurrent.*;
public class RateLimit {
    private static class TokenBucket {
        int tokens;
        long lastRefill;
        final int maxTokens;
        final int refillRate;
        TokenBucket(int maxTokens, int refillRate) {
            this.tokens = maxTokens;
            this.lastRefill = System.currentTimeMillis();
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
        }
        synchronized boolean allowRequest() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }
        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefill;
            int refillTokens = (int)(elapsed * refillRate);
            if (refillTokens > 0) {
                tokens = Math.min(maxTokens, tokens + refillTokens);
                lastRefill = now;
            }
        }
        synchronized int remaining() {
            refill();
            return tokens;
        }
    }
    private Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final int maxTokens = 1000;
    private final int refillRate = maxTokens / (3600 * 1000);
    public String checkRateLimit(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId,
                k -> new TokenBucket(maxTokens, refillRate));
        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.remaining() + " requests remaining)";
        } else {
            long reset = (bucket.lastRefill + 3600_000) / 1000;
            return "Denied (0 requests remaining, retry after " +
                    ((bucket.lastRefill + 3600_000 - System.currentTimeMillis()) / 1000) + "s)";
        }
    }
    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId,
                k -> new TokenBucket(maxTokens, refillRate));
        int used = maxTokens - bucket.remaining();
        long reset = (bucket.lastRefill + 3600_000) / 1000;
        return "{used: " + used + ", limit: " + maxTokens + ", reset: " + reset + "}";
    }
    public static void main(String[] args) {
        RateLimit limiter = new RateLimit();
        String client = "abc123";
        for (int i = 0; i < 1002; i++) {
            System.out.println(limiter.checkRateLimit(client));
        }
        System.out.println(limiter.getRateLimitStatus(client));
    }
}