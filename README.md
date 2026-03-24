# Heavy-Load Web Server Demo (Java 23 + Spring Boot 3.4)

This project demonstrates how to handle massive concurrency using **Java Virtual Threads (Project Loom)**. By using lightweight, user-mode threads, the server can handle thousands of concurrent "blocking" requests without exhausting Operating System resources.

## 🚀 Key Features
- **Spring Boot 3.4**: Modern web framework.
- **Java 23**: Leveraging the latest JVM features.
- **Virtual Threads**: Enabled via `spring.threads.virtual.enabled=true`.
- **Load Simulation**: A built-in tester to fire 10,000+ requests.

## 🧠 How it Works
Traditional Java servers use a **thread-per-request** model mapped to OS threads. OS threads are "heavy" (consuming ~1MB of memory each), limiting typical servers to a few hundred or thousand concurrent connections.

**Virtual Threads** are "lightweight" threads managed by the JVM. When a virtual thread hits a blocking operation (like `Thread.sleep()` or a database call), it "yields" its underlying OS thread to other tasks, allowing millions of virtual threads to run on just a few OS threads.

## 🛠️ Getting Started

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
While the server is running, open a **separate** terminal and run the `LoadTester`:
```bash
mvn test-compile exec:java -Dexec.mainClass="com.demo.LoadTester" -Dexec.classpathScope="test"
```
Or simply run the `LoadTester.java` file directly from your IDE.

## 📊 Expected Results
The `LoadTester` will fire 10,000 concurrent requests to the `/load` endpoint (which has a 200ms simulated delay). 

In a traditional server, this would likely cause a timeout or require a massive thread pool. With Virtual Threads, you should see:
- 100% success rate.
- Minimal CPU/Memory spikes.
- Thread names like `VirtualThread[#123,...]` in the server logs.
