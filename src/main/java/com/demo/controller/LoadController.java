package com.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.demo.service.LoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@RestController
public class LoadController {
	private static final Logger logger = LoggerFactory.getLogger(LoadController.class);

	@Autowired
	private LoadService loadService;

	@GetMapping("/run-test")
	public Map<String, Object> runTest(@RequestParam(defaultValue = "1000") int count) throws InterruptedException {
		return loadService.runLoadTest(count);
	}

	/**
	 * This endpoint simulates a slow I/O-bound operation.
	 * 
	 * In a standard thread-per-request model, Thread.sleep() would block a precious 
	 * OS thread. With Virtual Threads, the underlying OS thread is released to 
	 * do other work while this virtual thread is "waiting."
	 */
	@GetMapping("/load")
	public String handleLoad() throws InterruptedException {
		// Log the thread name to show if it's a virtual thread or not
		logger.info("📍 Received request on: {}", Thread.currentThread());
		
		// Simulate a blocking operation (e.g., database query or external API call)
		Thread.sleep(200); 
		
		return "Handled by: " + Thread.currentThread();
	}
}
