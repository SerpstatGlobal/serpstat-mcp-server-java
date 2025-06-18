package com.serpstat.core;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * A simple rate limiter that restricts the number of allowed requests within a specified time window.
 * If the limit is exceeded, the calling thread is paused until the next window starts.
 * Usage:
 *   RateLimiter limiter = new RateLimiter(10, Duration.ofSeconds(1));
 *   limiter.waitIfNeeded(); // Call before each request
 */
public class RateLimiter {
    private final int maxRequests;
    private final Duration timeWindow;
    private final AtomicInteger requestCount;
    private Instant windowStart;

    public RateLimiter(int requests, Duration duration) {
        this.maxRequests = requests;
        this.timeWindow = duration;
        this.requestCount = new AtomicInteger(0);
        this.windowStart = Instant.now();
    }

    // I think here can be a small bug like we had in php if there will be multiple threads operating essentially
    // bypassing the limit, but if this happens our serpstat api should return limit error so it should be fine
    public synchronized void waitIfNeeded() {
        Instant now = Instant.now();
        if (Duration.between(windowStart, now).compareTo(timeWindow) > 0) {
            // Reset the window
            windowStart = now;
            requestCount.set(0);
        }

        if (requestCount.incrementAndGet() > maxRequests) {
            try {
                Thread.sleep(timeWindow.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // Reset after sleep
            windowStart = Instant.now();
            requestCount.set(1);
        }
    }
}
