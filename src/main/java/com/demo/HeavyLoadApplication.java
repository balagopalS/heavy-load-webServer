package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HeavyLoadApplication demonstrates the power of Java Virtual Threads (Project Loom).
 * 
 * Traditional web servers use a thread-per-request model where each request maps to a 
 * heavy Operating System (OS) thread. Under heavy load (e.g., thousands of concurrent 
 * requests), OS threads become a bottleneck because they are expensive to create 
 * and switch between.
 * 
 * By enabling 'spring.threads.virtual.enabled=true', Spring Boot uses Virtual Threads.
 * Virtual Threads are lightweight, user-mode threads managed by the JVM. 
 * They allow us to handle millions of concurrent requests with minimal overhead.
 */
@SpringBootApplication
public class HeavyLoadApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeavyLoadApplication.class, args);
	}
}

