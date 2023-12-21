# 영속성 관리

---

### JPA에서 가장 중요한 2가지

1. 객체와 관계형 데이터베이스 맵핑하기(Object Relational Mapping).
2. 영속성 컨텍스트.

---

### 영속성 컨텍스트 정의

**엔티티 매니저 팩토리와 엔티티 매니저**

<img width="644" alt="Untitled (1)" src="https://github.com/hgene0929/JPA/assets/90823532/2293d2ab-8f08-4e1e-b0f4-e21a48399e45">

1. 애플리케이션이 실행되면 Persistence 에 의해 엔티티 매니저 팩토리가 생성.
2. 사용자가 엔티티 매니저를 호출할 경우, 엔티티 매니저 팩토리에 의해 엔티티 매니저 생성.
3. 엔티티 매니저는 필요에 의해 DB 커넥션 풀에 존재하는 커넥션을 가져와 사용.
</br></br>

**영속성 컨텍스트**

엔티티를 영구 저장하는 환경.

`EntityManager.persist(entity)` 라는 코드는 사실상 DB에 엔티티를 저장하는 것이 아닌, 영속성 컨텍스트에 엔티티를 영속화한다는 의미.

엔티티 매니저를 통해 영속성 컨텍스트에 접근할 수 있음.

- 엔티티 매니저를 생성하는 경우, 그 내부에 영속성 컨텍스트가 1:1로 생성됨.
</br></br>

**엔티티의 생명주기**

<img width="662" alt="Untitled (2)" src="https://github.com/hgene0929/JPA/assets/90823532/381476a8-48fd-4aac-bd4c-952316632919">

1. 비영속(new/transient) : 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태.
2. 영속(managed) : 영속성 컨텍스트에 관리되는 상태.
3. 준영속(detached) : 영속성 컨텍스트에 저장되었다가 분리된 상태.
4. 삭제(removed) : 삭제된 상태.

```java
// 객체를 생성한 상태(비영속)
Member member = new Member();
member.setId(100L);
member.setName("회원1");

// 객체를 저장한 상턔(영속) -> EntityManager.persistContext에 의해 해당 객체과 관리되기 시작
manager.persist(member);

// 회원 엔티티를 영속성 컨텍스트에서 분리(준영속)
manager.detach(member);

// 객체를 삭제한 상태(삭제)
manager.remove(member);

// 트랜잭션이 커밋되는 바로 이 시점에 DB에 엔티티 등록
transaction.commit();
```

---

### **영속성 컨텍스트의 이점**

**1차 캐시**

<img width="706" alt="Untitled (3)" src="https://github.com/hgene0929/JPA/assets/90823532/47f0e274-c580-42e1-8d7f-e1aa08c7e09f">

영속성 컨텍스트 내부에는 1차 캐시가 생성됨.

- 1차 캐시는 PK를 Key, 엔티티를 Value인 key-value 형태로 구성되어 있음.

이때, 엔티티 매니저를 통해 사용자가 특정 엔티티를 조회할 경우, JPA는 곧바로 DB로 가지 않고, 1차 캐시에서 먼저 해당 엔티티가 있는지 확인 후 없을때만 DB 조회.

- 일단 엔티티 매니저(영속성 컨텍스트)의 1차 캐시 조회.
- 없으면 DB 조회.
- DB에서 새로 찾아온 객체라면 1차캐시에 캐싱.
</br></br>

**동일성(identity) 보장**

1차 캐시로 반복 가능한 읽기(Repeatable read) 등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공.

```java
// 영속 엔티티의 동일성 보장
Member a = manager.find(Member.class, 2L);
Member b = manager.find(Member.class, 2L);
System.out.println(a == b); // 동일성 비교 true
```
</br></br>
**트랜잭션을 지원하는 쓰기 지원**

엔티티 매니저를 통해 엔티티를 영속화하는 시점에는 엔티티를 DB에 저장하지 않고, 영속성 컨텍스트의 1차 캐시에 쌓아두었다가(쓰기 지연), 트랜잭션이 커밋되는 시점에 DB에 한꺼번에 저장.

- 영속화 되는 시점에 쓰기 지연 SQL 저장소(영속성 컨텍스트 내부에 존재)에 필요한 SQL(Insert SQL)을 생성해서 저장해둠.

<img width="710" alt="Untitled (7)" src="https://github.com/hgene0929/JPA/assets/90823532/9feb2fad-eb94-41ca-bf8d-fb675c708260">
<img width="710" alt="Untitled (5)" src="https://github.com/hgene0929/JPA/assets/90823532/eb6bbd2b-3cad-4560-aa60-8e8d3856dccc">

```java
// 쓰기 지연
Member memberA = new Member();
member.setId(101L);
member.setName("회원A");
Member memberB = new Member();
member.setId(102L);
member.setName("회원B");

// 이 시점에는 DB에 등록하지 않고 1차 캐시에만 저장
manager.persist(memberA);
manager.persist(memberB);

// 트랜잭션이 커밋되는 바로 이 시점에 DB에 엔티티 등록
transaction.commit();
```

> 💡 **쓰기 지연과 버퍼링**
>
> - 버퍼링 : 모았다가 DB에 한꺼번에 등록.
> - hibernate의 속성에는 batch size를 설정해야 하는 속성이 존재.
> - 이것은 JPA가 쓰기 지연을 지원하는 덕분에 영속성 컨텍스트에 쌓인 엔티티들을 한꺼번에 DB에 등록할 수 있는데, 이때 한꺼번에 보낼 수 있는 데이터 수를 지정하기 위함.
>
> ```xml
> <property name="hibernate.jdbc.batch.size" value="10"/>
> ```

> 💡 **JPA 사용시 생성자**
>
> JPA는 내부적으로 리플렉션 등을 통해서 객체를 생성할 수 있어야 하기 때문에 엔티티에는 기본 생성자가 존재해야 함.

</br></br>
**변경 감지(dirty checking)**

JPA는 flush(커밋시점에 호출됨)가 되는 시점에 1차 캐시의 엔티티와 스냄샷을 비교함.

- 이때, 만약 다른 값이 있다면 해당 변경내용을 반영한 Update SQL문을 생성하여 쓰기 지연 저장소에 저장.

<img width="695" alt="Untitled (6)" src="https://github.com/hgene0929/JPA/assets/90823532/e192304c-2dae-4173-b3e0-4f67bfec0f22">

```java
// 더티체킹
Member updateMember = manager.find(Member.class, 100L);
updateMember.setName("updateName");

// 트랜잭션이 커밋되는 바로 이 시점에 변경된 값을 자동으로 DB에 반영
transaction.commit();
```

**지연 로딩(lazy loading)**

보류.

---

### 플러시

영속성 컨텍스트의 변경내용을 DB에 동기화.

트랜잭션 커밋 시점에 flush() 호출 ⇒ 트랜잭션이라는 작업 단위가 중요함.

영속성 컨텍스트를 비우지는 않음.
</br></br>

**플러시 발생시 수행내용**

1. 변경 감지(1차 캐시의 엔티티 - 스냅샷 비교).
2. 수정된 엔티티에 대한 필요한 SQL을 생성하여 쓰기 지연 SQL 저장소에 등록.
3. 쓰기 지연 SQL 저장소의 쿼리를 DB에 전송(등록, 수정, 삭제 쿼리).
</br></br>

**영속성 컨텍스트를 플러시 하는 방법**

```java
// 영속성 컨텍스트를 플러시 하는 방법
manager.flush(); // 직접 호출(플러시 수동 호출)
transaction.commit(); // (플러시 자동 호출)
// JPQL 쿼리 실행(플러시 자동 호출)
```

> 💡 **JPQL 쿼리 실행시 플러시가 자동으로 호출되는 이유**
>
> JPQL 실행시, DB에 쿼리가 날라가지 않은 상태인데 동일한 트랜잭션 내에서 영속화한 엔티티를 조회한다면 오류가 발생할 것.</br>
> 따라서 이러한 오류를 방지하기 위해 JPA 내부 로직은 JPQL 실행시 무조건 flush()를 호출하여 동일한 트랜잭션 내부에서 영속화한 엔티티를 DB에 반영할 수 있도록 함.

</br></br>
**플러시 모드 옵션**

```java
// 플러시 모드 옵션
manager.setFlushMode(FlushModeType.AUTO); // 커밋이나 쿼리를 실행할 때 플러시(default)
manager.setFlushMode(FlushModeType.COMMIT); // 커밋할 때만 플러시
```

---

### 준영속 상태

영속 → 준영속.

영속 상태의 엔티티가 영속성 컨텍스트에서 분리(detached).

영속성 컨텍스트가 제공하는 기능을 사용할 수 없게 됨.

```java
// 준영속 상태로 만드는 방법
manager.detach(member); // 특정 엔티티만 준영속 상태로 전환
manager.close(); // 영속성 컨텍스트를 완전히 초기화
manager.close(); // 영속성 컨텍스트를 종료
```
