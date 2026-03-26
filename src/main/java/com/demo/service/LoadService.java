package com.demo.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LoadService {
    private static final Logger logger = LoggerFactory.getLogger(LoadService.class);
    
    // Use a single client for all requests
    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public Map<String, Object> runLoadTest(int numberOfRequests) throws InterruptedException {
        logger.info("🚀 Starting load test with {} requests...", numberOfRequests);
        String url = "http://localhost:8080/load";
        
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numberOfRequests; i++) {
                executor.submit(() -> {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("Accept", "text/plain")
                            .GET()
                            .build();

                    try {
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        int completed = successCounter.get() + failureCounter.get();
                        
                        if (response.statusCode() == 200) {
                            successCounter.incrementAndGet();
                        } else {
                            logger.error("❌ Request failed with status: {}", response.statusCode());
                            failureCounter.incrementAndGet();
                        }
                        
                        // Log progress every 10%
                        int progressStep = Math.max(1, numberOfRequests / 10);
                        if (completed > 0 && completed % progressStep == 0) {
                            logger.info("🔄 Progress: {}/{} requests completed...", completed, numberOfRequests);
                        }
                    } catch (Exception e) {
                        if (failureCounter.get() == 0) {
                            logger.error("❌ First request failed with error:", e);
                        }
                        failureCounter.incrementAndGet();
                    }
                });
            }
            // try-with-resources will automatically call shutdown() and awaitTermination()
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("✅ Load test finished! Result: {} success, {} failures in {}ms", successCounter.get(), failureCounter.get(), duration);
            
        Map<String, Object> results = new HashMap<>();
        results.put("totalRequests", numberOfRequests);
        results.put("timeTakenMs", duration);
        results.put("successCount", successCounter.get());
        results.put("failureCount", failureCounter.get());
        results.put("requestsPerSec", (numberOfRequests / (duration / 1000.0)));
            
        return results;
    }
}
