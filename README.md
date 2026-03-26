# Heavy-Load Web Server Demo (Java 23 + Spring Boot 3.4)

This project demonstrates how to handle massive concurrency using **Java Virtual Threads (Project Loom)**. By using lightweight, user-mode threads, the server can handle thousands of concurrent "blocking" requests without exhausting Operating System resources.

## Key Features
- **Spring Boot 3.4**: Modern web framework.
- **Java 23**: Leveraging the latest JVM features.
- **Virtual Threads**: Enabled via `spring.threads.virtual.enabled=true`.
- **Integrated Load Testing**: Trigger a high-concurrency test (5k-10k+ requests) with a single REST call.
- **Detailed Logging**: Color-coded progress logs to see virtual threads in action.

## Architectural Highlights

### 1. Traditional Structure
The project is organized into a standard Spring Boot layout:
- `com.demo.controller`: Entry points for requests.
- `com.demo.service`: Business logic for handling load simulations.

### 2. Singleton HttpClient
To avoid `selector manager closed` errors and maximize efficiency, the project uses a single, shared `HttpClient`. Creating a new client for every task can exhaust system resources or lead to race conditions during shutdown.

### 3. Virtual Threads everywhere
Both the incoming web requests and the internal load tester leverage virtual threads. You can see this in the logs, where thread names look like `VirtualThread[#123,...]`.

## Getting Started

### Prerequisites
- Java 23 installed (`java -version`).
- Maven installed (`mvn -version`).

### 1. Run the Server
Open a terminal and run:
```bash
mvn spring-boot:run
```
The server will start on `http://localhost:8080`.

### 2. Run the Load Test
Instead of a separate script, you can now trigger the test directly via HTTP:
```bash
# Triggers 5,000 requests to the server
curl "http://localhost:8080/run-test?count=5000"
```

## Expected Results
Watch your terminal! You will see:
- **Test Start**: Initializing thousands of requests.
- **Real-time Progress**: Logging every 10% of completion.
- **Incoming Requests**: Logs showing which `VirtualThread` is handling which request.
- **Final Stats**: Success counts, time taken, and requests per second.

---

## Troubleshooting & Physical Limits

If you see a high number of **failures** when running with very large counts (e.g., 50,000+), here is why:

### 1. Ephemeral Port Exhaustion
Operating systems (especially Windows) have a finite pool of "ephemeral" ports for outgoing connections (usually around 16,000 by default).
*   **The Problem**: If you fire 50k requests in 40 seconds, the OS literal runs out of ports to give the connections.
*   **The Fix**: Keep test counts under **10,000** for a guaranteed 100% success rate on consumer hardware, or tune your OS `MaxUserPort` settings.

### 2. TCP TIME_WAIT
Even after a request finishes, the OS tracks the socket for a minute to handle stray packets. This "cool down" period means ports aren't recycled instantly.

### 3. "Selector Manager Closed"
If you encounter this error, ensure you are using the **Singleton HttpClient** pattern implemented in `LoadService.java`. This prevents race conditions where the networking engine is shut down while tasks are still in flight.

---

## Future Expansion Plan (Complex Load Scenarios)

To fully explore modern Java and realistic enterprise patterns, we plan to implement the following expansions:

### 1. Structured Concurrency (JEP 480)
Migrating from raw `ExecutorService` (and its potentially sloppy task cancellations) to `StructuredTaskScope`. This modern API ties a group of concurrent virtual tasks into a single managed block—if one sub-task fails, the others are cleanly and automatically cancelled without leaking threads.

### 2. Nested Calls
In reality, servers don't just sleep; they call other services. We'll introduce a `DataService` that the Load Test spawns multiple sub-tasks into (a Controller -> Service -> Downstream Service chain). This proves we can safely spawn virtual threads deep inside business logic without resource exhaustion.

### 3. Heavy Data Payloads (CPU / Memory Stress)
Replacing `Thread.sleep()` with CPU-intensive data transformations (like parsing massive JSON or XML strings). This demonstrates the difference between I/O bound workloads (where Virtual Threads give near-infinite scalability) and CPU bound workloads (where hardware actually limits performance).

### 4. Lock Contention (Shared Resources)
Introducing a mock shared cache to force our virtual threads to contend for access. This demonstrates a known virtual thread pitfall: `synchronized` blocks can "pin" the underlying OS thread. We will show how refactoring to `ReentrantLock` avoids this and keeps the app scalable.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
