package com.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple load tester to simulate heavy load using Virtual Threads.
 * This should be run while the Spring Boot application is active.
 */
public class LoadTester {

    public static void main(String[] args) throws InterruptedException {
        int numberOfRequests = 10000;
        String url = "http://localhost:8080/load";
        
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);

        System.out.println("🚀 Starting load test with " + numberOfRequests + " requests...");

        // Create a thread pool using Virtual Threads to fire requests concurrently.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            HttpClient client = HttpClient.newBuilder()
                    .executor(executor)
                    .build();

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < numberOfRequests; i++) {
                executor.submit(() -> {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();

                    try {
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() == 200) {
                            successCounter.incrementAndGet();
                        } else {
                            failureCounter.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failureCounter.incrementAndGet();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("\n📊 --- Load Test Results ---");
            System.out.println("⏱️ Time taken: " + duration + " ms");
            System.out.println("✅ Success: " + successCounter.get());
            System.out.println("❌ Failures: " + failureCounter.get());
            System.out.println("⚡ Requests/sec: " + (numberOfRequests / (duration / 1000.0)));
        }
    }
}
