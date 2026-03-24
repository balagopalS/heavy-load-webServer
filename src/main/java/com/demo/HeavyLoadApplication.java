package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@RestController
class LoadController {
	private static final Logger logger = LoggerFactory.getLogger(LoadController.class);

	/**
	 * This endpoint simulates a slow I/O-bound operation.
	 * 
	 * In a standard thread-per-request model, Thread.sleep() would block a precious 
	 * OS thread. With Virtual Threads, the underlying OS thread is released to 
	 * do other work while this virtual thread is "waiting."
	 */
	@GetMapping("/load")
	public String handleLoad() throws InterruptedException {
		// Simulate a blocking operation (e.g., database query or external API call)
		Thread.sleep(200); 
		
		// Notice that the thread name will indicate it's a virtual thread.
		return "Handled by: " + Thread.currentThread();
	}
}
