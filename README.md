[🇰🇷 한국어](README.ko.md) | [🇺🇸 English](README.md)

# ☠️ Deadlock4j ☠️

## Why Deadlock4j? 🤔

Deadlocks are an inevitable challenge when operating Java applications. Tools like JConsole or VisualVM can be cumbersome and don't provide real-time monitoring. Additionally, generic exception messages often lack sufficient detail, making rapid issue resolution difficult.

Deadlock4j was created to eliminate these inconveniences.

## 🚀 Why Choose Deadlock4j?

Deadlock4j is a lightweight library designed exclusively to detect Java application deadlocks automatically, providing immediate logging and event transmission capabilities. Powerful APM solutions such as DataDog, New Relic, and Prometheus offer extensive features but come with complexity and high costs.

In contrast, Deadlock4j focuses solely on **deadlock detection**, minimizing resource consumption and complexity. Its simplicity enhances application stability and accelerates issue resolution.

## Key Features ⚙️

- **Deadlock Detection and Detailed Logging**
    - Automatic detection and logging of thread deadlocks.
    - Optional annotation-based detection and logging of database deadlocks.
- **Event Logging and Integration with External Systems**
    - TCP transmission (Protocol Buffers-based).
    - Customizable extensions for Kafka, Redis, RabbitMQ, etc.
- **Protocol Buffers-based Lightweight Data Transmission** (ensuring reliable delivery).
- **Easy Setup for Spring Boot Applications**.
- **Extensible Central Server Transmission Interface**.

> ⚠️ **Caution**: Deadlock4j currently does not provide automatic deadlock resolution due to stability and data consistency concerns. Future support is under careful consideration.

## Supported Databases 🗃️

Deadlock detection is available for the following databases:

- MySQL
- PostgreSQL
- Oracle
- MariaDB
- H2

## Dependency Installation 📦

You can easily add Deadlock4j via Maven or Gradle.

### Gradle

#### 1\. Add JitPack repository (`settings.gradle` or `settings.gradle.kts`)

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

#### 2\. Add dependency (`build.gradle` or `build.gradle.kts`)

```gradle
dependencies {
    implementation 'com.github.kgggh.deadlock4j:deadlock4j-spring-boot-starter:1.0.0'
}
```

### Maven

#### 1\. Add JitPack repository (`pom.xml`)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

#### 2\. Add dependency (`pom.xml`)

```xml
<dependency>
    <groupId>com.github.kgggh.deadlock4j</groupId>
    <artifactId>deadlock4j-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration 🛠️

Deadlock4j can be easily configured within Spring Boot:

```yaml
deadlock4j:
  enabled: true             # Enable the library (default: false)
  logEnabled: true          # Log deadlock events (default: true)
  instanceId: "my-app"      # Application identifier (default: "application")
  tcpServerIp: "127.0.0.1"  # TCP server IP for event transmission
  tcpServerPort: 58282      # TCP server port
  monitorInterval: 1000     # Deadlock check interval in milliseconds (default: 1000)
  heartbeatInterval: 30000  # Server connection check interval (default: 30000)
  transportType: NONE       # Event transport method (NONE, TCP, QUEUE) (default: NONE)
```

### Annotation-Based Detection 📝

Deadlock4j provides annotations for selective database deadlock detection:

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DetectDatabaseDeadlock {}
```

#### Usage Example

Method-level:

```java
@Service
public class PostService {

    @DetectDatabaseDeadlock
    @Transactional
    public void updatePost(Long postId, PostDto dto) {
        // Database deadlock detection for this method only
    }
}
```

Class-level:

```java
@DetectDatabaseDeadlock
@Service
public class PostService {

    @Transactional
    public void processPayment(PaymentDto dto) {
        // Database deadlock detection for all methods within this class
    }
}
```

## Example Projects 📂

To assist your integration, we provide several examples:

- **spring-queue-client (Kafka)**: Kafka message queue integration
- **spring-tcp-client (Basic)**: Basic TCP-based event transmission
- **spring-tcp-server (Netty)**: Netty-based event reception server

Refer to these examples when integrating Deadlock4j into your environment.

## Log Examples 🖥️

Deadlock4j provides intuitive logs when a deadlock is detected.

### Database Deadlock Example

```
[DEADLOCK DETECTED]
──────────────────────────────────────────
Type           : DATABASE
Timestamp      : 2025-03-18T19:20:41.877+09:00[Asia/Seoul]
Exception Name : CannotAcquireLockException
Sql State      : 40001
Reason         : could not execute statement [Deadlock found when trying to get lock; try restarting transaction] [update post set content=?,title=?,views=? where id=?]; SQL [update post set content=?,title=?,views=? where id=?]
──────────────────────────────────────────
```

### Thread Deadlock Example

```
[DEADLOCK DETECTED]
──────────────────────────────────────────
Type           : THREAD
Timestamp      : 2025-03-18T19:21:16.739+09:00[Asia/Seoul]
Thread Name    : Thread-4
Thread ID      : 73
Thread State   : WAITING
Blocked Count  : 0
Waited Count   : 2
Lock Name      : java.util.concurrent.locks.ReentrantLock$NonfairSync@628121fa
Lock Owner ID  : 72
Lock Owner Name: Thread-2
──────────────────────────────────────────
Stack Trace:
java.base@17.0.6/jdk.internal.misc.Unsafe.park(Native Method)
...
──────────────────────────────────────────
```

## Future Roadmap 🌱

- **Evaluating automatic deadlock resolution** (planned after stability assurance)
- **Support for additional databases**
- **Dedicated Deadlock4j dashboard server**
    - Centralized deadlock event management
    - Integration with Slack, Discord, Email, etc.

## License 📜

Deadlock4j is released under the [MIT License](LICENSE).

---

Deadlock4j is designed for simple and effective deadlock management. Contributions and feedback are always welcome! 🚀

