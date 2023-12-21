# 애플리케이션 개발

---

### JPA 구동 방식

<img width="642" alt="Untitled" src="https://github.com/hgene0929/JPA/assets/90823532/7dd10761-6acc-480e-abf7-bea8226a03c6">

1. JPA에는 Persistence 라는 클래스가 존재하고 가장 먼저 애플리케이션에 등록됨.
2. Persistence가 JPA의 설정정보(META-INF/persistence.xml or application.xml)를 가장 먼저 조회하여, 이를 기반으로 EntityManagerFactory 생성.
    - EntityManagerFactory는 애플리케이션 구동 후 최초 1회만 생성.
3. 이후로 EntityManagerFactory에서 EntityManager을 생성하여 이를 통해 필요한 JPA 동작 사용.
    - EntityManager는 매번 DB 커넥션을 얻억서 쿼리를 생성하고 날리는 작업을 할 때마다 생성.
- 여기까지의 과정은 Spring 프레임워크와 JPA를 연동해서 사용할 경우, Spring에 의해 자동수행됨.

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/JpaMain.java)
```java
public class JpaMain {
	public static void main(String[] args) {
		// 가장 먼저 애플리케이션에 로드된 Persistence 클래스를 통해 EntityManagerFactory 생성
		// 애플리케이션 로딩시점에 딱 하나만 생성가능
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("hello");

		// 생성된 EntityManagerFactory로부터 EntityManager 객체 생성
		// DB 커넥션을 얻억서 쿼리를 생성하고 날리는 작업을 할 때마다 생성해야 함
		EntityManager manager = factory.createEntityManager();
		
		// EntityManager 을 통해 필요한 동작 시작
		// 코드 작성부..
		// EntityManager 을 통해 필요한 동작 끝

		// 앞서 생성한 EntityManager의 동작이 완료되어 반환
		manager.close();

		// 애플리케이션 동작이 완전히 완료된 경우 EntityManagerFactory 까지 닫음
		factory.close();
	}
}
```

---

### 객체와 테이블을 생성하고 맵핑하기

**EntityManagerFactory, EntityManager, Transaction**

- 하나의 EntityManger가 생성되고 DB에 날려야 할 SQL문이 필요한 로직이 수행되는 하나의 단위를 트랜잭션이라고 함.
- 해당 트랜잭션은 DB와 연동하여 작업을 하기 전 생성되어 그 수명은 해당 로직이 끝날 때 함께 끝남 ⇒ try {…}.
- 로직이 정상적으로 수행되면 DB에 데이터를 반영하기 위해 commit()을 하고, 문제 발생시에는 rollback()을 통해 데이터 정합성을 유지해야 함 ⇒ catch {…}.
- EntityManager 또한 하나의 로직 단위로 생성되기 때문에 트랜잭션이 정상적으로 수행되든 그렇지않든, 자원이 반납되어야 함 ⇒ finally {…}.

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/Member.java)
```java
@Entity // 해당 클래스가 엔티티임을 알림
public class Member {

	@Id // 해당 필드가 이 엔티티의 PK임을 알림
	private Long id;
	private String name;

	// getter..setter..
}
```

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/JpaMain.java)
```java
// EntityManager 을 통해 필요한 동작 시작
EntityTransaction transaction = manager.getTransaction(); // 트랜잭션 객체 추출
transaction.begin(); // 트랜잭션 시작

try {
	Member member = new Member(); // 엔티티 생성
	member.setId(2L); // 엔티티의 값 저장
	member.setName("HelloB"); // 엔티티의 값 저장
	manager.persist(member); // 생성된 엔티티를 DB 테이블에 저장

	transaction.commit(); // 현대 트랜잭션의 동작 DB에 반영
} catch (Exception e) {
	transaction.rollback(); // 로직에서 문제가 발생한 경우, 트랜잭션 롤백
} finally {
	// 앞서 생성한 EntityManager의 동작이 완료되어 반환
	manager.close();
}
// EntityManager 을 통해 필요한 동작 끝
```

---

### DB로부터 테이블 조회 후, 객체와 맵핑

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/JpaMain.java)
```java
// EntityManager 을 통해 필요한 동작 시작
		EntityTransaction transaction = manager.getTransaction();
		transaction.begin();

		try {
			Member member = manager.find(Member.class, 1L); // DB로부터 PK를 기준으로 엔티티 조회
			System.out.println("member.id = " + member.getId()); // member.id = 1
			System.out.println("member.name = " + member.getName()); // member.name = HelloA

			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
		} finally {
			manager.close();
		}
		// EntityManager 을 통해 필요한 동작 끝
```

---

### 객체의 필드 수정 후, DB에 반영

**JPA의 Update 동작**

- JPA의 EntityManager에 의해 조회된 데이터 객체는 트랜잭션 라이프사이클 동안 JPA에 의해 관리됨.
- 따라서 트랜잭션 커밋시점에 만약 관리하던 데이터 객체에 수정이 일어난 경우, JPA는 Update 쿼리문을 자동으로 생성하여 DB에 날림.

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/JpaMain.java)
```java
// EntityManager 을 통해 필요한 동작 시작
EntityTransaction transaction = manager.getTransaction();
transaction.begin();

try {
	Member member = manager.find(Member.class, 1L); // DB로부터 PK를 기준으로 엔티티 조회
	member.setName("HelloJPA"); // 조회된 엔티티의 필드 수정

	transaction.commit(); // 이 시점에 JPA는 엔티티의 데이터가 변경되었는지 확인 후, 변경되었다면 Update 쿼리 생성하여 수행
} catch (Exception e) {
	transaction.rollback();
} finally {
	manager.close();
}
// EntityManager 을 통해 필요한 동작 끝
```

---

### 주의사항

1. EntityManagerFactory는 단 1개만 생성해서 애플리케이션 전체에서 공유한다.
2. EntityManager는 스레드 간에 공유하면 절대 안된다(사용하고 버려야 한다).
3. JPA의 모든 데이터 변경은 트랜잭션 안에서 실행해야 한다.

> 💡 **EntityManager와 동시성 문제**
>
> - 엔티티 매니저는 데이터베이스 커넥션을 필요 시점에 맞춰 Connection Pool에서 꺼내어 사용하기 때문에 여러 스레드가 동시에 접근하면 동시성 문제가 발생 가능함.
> - 따라서 EntityManager는 스레드 간에 절대 공유하면 안됨.
> 
> ---
>
> 💡 **Spring DI 와 EntityManager**
> - 그렇다면 스프링 의존성 주입에 의해 주입되는 EntityManager는 스프링 컨테이너에 의해 관리되는 싱글톤 객체일텐데, 결론적으로 문제가 발생하지 않음.
> - 위의 동작이 가능한 이유는 스프링에 의해 주입되는 EntityManager가 실제 객체가 아니라, 실제 객체를 연결해주는 가짜 객체이기 때문임.
> - 따라서 스프링 의존성 주입에 의해 EntityManager가 주입되고, 해당 객체가 호출되는 시점에 EntityManager가 실제 객체(얘는 싱글톤 아님)를 호출하여 사용하는 덕분에 동시성 문제가 발생하지 않음.

---

### JPQL 소개

가장 단순한 조회 방법.

- EntityManager.find()
- 객체 그래프 탐색(entity.get())
</br></br>

**그렇다면 아이디가 1인 데이터만 조회하고 싶다면?**

단순한 방법으로 조회할 수 없고, 조건문 등 SQL문이 복잡해진다면 JPQL을 사용함.

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/JpaMain.java)
```java
// EntityManager 을 통해 필요한 동작 시작
EntityTransaction transaction = manager.getTransaction();
transaction.begin();

try {
	Member member = manager.createQuery("select m from Member m where m.id = 1", Member.class)
		.getSingleResult();// JPQL을 활용하여 복잡한 SQL 생성
	System.out.println("member.id = " + member.getId()); // member.id = 1
	System.out.println("member.name = " + member.getName()); // member.name = HelloJPA

	transaction.commit();
} catch (Exception e) {
	transaction.rollback();
} finally {
	manager.close();
}
// EntityManager 을 통해 필요한 동작 끝
```
</br>

**JPQL**

JPA를 사용하면 엔티티 객체를 중심으로 개발을 수행하며, 이때 문제가 되는 것은 검색 쿼리임.

- 검색을 할 때에도 테이블이 아닌 엔티티 객체를 대상으로 검색을 수행하기 때문.
- 이때, 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능(JOIN, 페이징, 동적쿼리 등).

따라서 애플리케이션이 필요한 데이터만 DB에서 불러오기 위해 검색 조건이 포함된 SQL을 자동으로 생성해주는 JPQL을 사용함.

JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공.

SQL 문법과 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원.
