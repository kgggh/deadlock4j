[🇰🇷 한국어](README.ko.md) | [🇺🇸 English](README.md)
# ☠️  Deadlock4j ☠️

## 왜 Deadlock4j를 만들었을까? 🤔

Java 애플리케이션을 운영하다 보면 **데드락(Deadlock)** 문제를 피할 수 없습니다. JConsole이나 VisualVM과 같은 도구들은 사용이 번거롭고, 실시간으로 데드락을 확인하기 어렵습니다. 또한, 단순한 예외 메시지만으로는 충분한 정보를 얻기 어려워 신속한 대응에 한계가 있습니다.

이러한 불편함을 개선하고자 Deadlock4j가 탄생했습니다.

## 🚀 Deadlock4j를 사용해야 하는 이유

Deadlock4j는 Java 애플리케이션의 데드락 문제를 자동으로 감지하고, 로깅 및 외부 시스템으로 이벤트 전송을 즉시 수행하는 경량 라이브러리입니다. DataDog, New Relic, Prometheus 등 강력한 APM 도구들은 다양한 기능을 제공하지만, 복잡한 설정과 비용 부담이 큽니다.

반면 Deadlock4j는 **오직 데드락 감지에만 집중하여 시스템 리소스 사용량과 복잡성을 최소화했습니다.** 설정이 간단하여 애플리케이션의 안정성을 높이고, 장애 대응 속도를 크게 향상할 수 있습니다.

## 주요 기능 ⚙️

- **데드락 감지 및 상세 로그 제공** 
  - 스레드 데드락 자동 감지 및 로그 출력
  - 어노테이션 기반의 선택적 데이터베이스 데드락 감지 및 로그 출력
- **이벤트 로깅 및 외부 시스템 연동 지원**
    - TCP 전송 (Protocol Buffers 기반)
    - Kafka, Redis, RabbitMQ 등으로 사용자 정의 확장 가능
- **Protocol Buffers 기반의 경량 데이터 전송** (메시지 길이 포함하여 안정적 수신 보장)
- **Spring Boot 환경에서 간편한 설정 지원**
- **확장 가능한 중앙 서버 전송 인터페이스 제공**

> ⚠️ **주의**: Deadlock4j는 현재 데드락을 해제하는 기능은 제공하지 않습니다. 이는 애플리케이션의 안정성 및 데이터 정합성 때문이며, 향후 지원 가능성을 신중하게 검토하고 있습니다.

## 지원하는 데이터베이스 🗃️

Deadlock4j는 SQL 예외를 분석하여 데드락 정보를 추출합니다.  
현재 다음의 데이터베이스에서 벤더별 SQL 예외 정보를 기반으로 데드락 감지가 가능합니다

- MySQL
- PostgreSQL
- Oracle
- MariaDB
- H2

## 의존성 추가 방법 📦

Deadlock4j는 Maven과 Gradle을 통해 쉽게 설치할 수 있습니다.

### Gradle

#### 1\. JitPack 저장소 추가 (`settings.gradle` or `settings.gradle.kts`)

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

#### 2\. 의존성 추가 (`build.gradle` or `build.gradle.kts`)

```gradle
dependencies {
    implementation 'com.github.kgggh.deadlock4j:deadlock4j-spring-boot-starter:1.0.0'
}
```

### Maven

#### 1\. JitPack 저장소 추가 (`pom.xml`)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

#### 2\. 의존성 추가 (`pom.xml`)

```xml
<dependency>
    <groupId>com.github.kgggh.deadlock4j</groupId>
    <artifactId>deadlock4j-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 설정 방법 🛠️

Deadlock4j는 Spring Boot 환경에서 간단한 설정만으로 사용할 수 있습니다.

```yaml
deadlock4j:
  enabled: true             # 라이브러리 활성화 여부 (default: false)
  logEnabled: true          # 데드락 발생시 로그 기록 활성화 (default: true)
  instanceId: "my-app"      # 애플리케이션 구분을 위한 ID (default: "application")
  tcpServerIp: "127.0.0.1"  # 이벤트 전송을 위한 TCP 서버 IP 
  tcpServerPort: 58282       # 이벤트 전송을 위한 TCP 서버 Port
  monitorInterval: 1000     # 데드락 감지 주기(ms) (default: 1000)
  heartbeatInterval: 30000  # 서버 연결 상태 체크 주기(ms) (default: 30000)
  transportType: NONE       # 이벤트 전송 방식 선택 (NONE, TCP, QUEUE) (default: NONE)
```

### 어노테이션 기반 사용 📝
Deadlock4j는 특정 메소드 또는 클래스의 데이터베이스 데드락을 선택적으로 감지할 수 있도록 어노테이션을 제공합니다.

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DetectDatabaseDeadlock {
}
```

#### 사용 예시

메소드 단위로 사용:

```java
@Service
public class PostService {

    @DetectDatabaseDeadlock
    @Transactional
    public void updatePost(Long postId, PostDto dto) {
        // 이 메소드의 DB 데드락만 감지됨
    }
}
```

클래스 단위로 사용:

```java
@DetectDatabaseDeadlock
@Service
public class PostService {

    @Transactional
    public void updatePost(Long postId, PostDto dto) {
        // 클래스 내 모든 메소드의 DB 데드락을 감지
    }
}
```

## 예제 프로젝트 📂

Deadlock4j의 활용을 돕기 위해 예제 프로젝트를 제공합니다:

- **spring-queue-client (Kafka)**: Kafka 메시지 큐 연동 예제
- **spring-tcp-client (Basic)**: TCP 기반 이벤트 전송 예제
- **spring-tcp-server (Netty)**: Netty 기반 이벤트 수신 서버 예제

실제 환경에서 적용할 때 참고하세요.

## 로그 출력 예시 🖥️

Deadlock4j는 데드락 발생 시 직관적인 로그를 제공합니다.

### 데이터베이스 데드락 예시

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

### 스레드 데드락 예시

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

## 향후 계획 🌱

- **데드락 해제 기능 추가 검토** (안정성 확보 후 제공 예정)
- **지원 가능한 데이터베이스의 파싱 기능 확장**
  - SQL 예외를 기반으로 벤더별 데드락 감지 지원 데이터베이스 추가
- **Deadlock4j 전용 중앙 대시보드 서버 제공 예정**
    - 데드락 이벤트 중앙 관리
    - Slack, Discord, 이메일 등 외부 알림 시스템과의 연동 지원

## 라이선스 📜

Deadlock4j는 [MIT 라이선스](LICENSE)를 따릅니다.

---

Deadlock4j는 데드락 문제를 간편하고 효과적으로 해결하기 위해 설계되었습니다. 많은 피드백과 기여를 환영합니다! 🚀

