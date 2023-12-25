# 섹션3. 영속성 관리 - 내부 동작 방식

## 영속성 컨텍스트

### JPA에서 가장 중요한 2가지
- 객체와 관계형 데이터베이스 매핑하기
- 영속성 컨텍스트

### 엔티티 매니저 팩토리와 엔티티 매니저
![[Pasted image 20231224205246.png]]
- 엔티티 매니저 팩토리 - 고객의 요청이 올 때마다 엔티티 매니저를 생성
- 엔티티 매니저 - 커넥션 풀 (DB)에 접근

### 영속성 컨텍스트
- JPA를 이해하는데 가장 중요한 용어
- "엔티티를 영구 저장하는 환경"이라는 뜻
- `EntityManager.persist(entity)`
- 영속성 컨텍스트는 논리적인 개념 - 눈에 보이지 않는다.
- 엔티티 매니저를 통해서 영속성 컨텍스트에 접근

##### J2SE 환경
- 엔티티 매니저와 영속성 컨텍스트가 1 : 1

### 엔티티의 생명 주기
- 비영속 (new/transient)
	- 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
- 영속 (managed)
	- 영속성 컨텍스트에 관리되는 상태
- 준영속 (detached)
	- 영속성 컨텍스트에 저장되었다가 분리된 상태
- 삭제 (removed)
	- 삭제된 상태
![[Pasted image 20231224220924.png]]

### 비영속
- JPA와 상관 없이 멤버 객체를 생성만 한 상태

### 영속
- 객체를 생성하여 엔티티메니저에 `persist(객체)`를 통해 영속 상태로 만듦

```java
Member member = new Memeber();
member.setId(100L);
member.setName("HelloJPA");
// 여기까지만 하면 비영속상태
em.persist(member); // em은 엔티티매니저
// 영속 상태
```

### 준영속
- `em.detach(객체)`- 영속성 컨텍스트에서 분리
### 삭제
- `em.remove(객체)`- 삭제

### 영속성 컨텍스트의 이점
- 1차 캐시
- 동일성(identity) 보장
- 트랜잭션을 지원하는 쓰기 지연 (transactional write-behind)
- 변경 감지(Dirty Checking)
- 지연 로딩(Lazy Loading)

##### 1차 캐시에서 조회
- `em.find(아이디)` 등으로 데이터를 조회할 때 1차 캐시에서 먼저 조회한다.
- ![[Pasted image 20231224221808.png]]
- ![[Pasted image 20231224221839.png]]
- 1차 캐시에 존재하지 않는 데이터는 DB에서 1차 캐시로 먼저 가져온다.
- 1차 캐시는 한 트랜잭션 안에서만 효과가 있다.

##### 영속 엔티티의 동일성 보장
- 1차 캐시로 반복가능한 읽기 등급의 트랜잭션 격리수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공
- `em.find(아이디)` 에서 아이디가 같다면 항상 같은 객체 반환

##### 트랜잭션을 지원하는 쓰기 지연
- `em.persist(객체)`
	- 추가된 객체를 저장하기 위한 SQL 생성 -> 쓰기 지연 SQL 저장소에 저장
	- 1차 캐시에 객체 저장
	- `persistence.xml` 파일에 `<property name="hibernate.jdbc.batch_size" value="10">` 에서 value 값을 조정해 그 사이즈만큼 한번에 쿼리를 보내 DB에 저장

##### 변경 감지(Dirty Checking)
- ![[Pasted image 20231224224005.png]]
- 처음 읽어온 객체를 스냅 샷으로 저장한다.
- 커밋 할 때
	- `flush()` 자동 실행
	- 현재 값과 스냅샷 값을 비교하여 update sql을 생성하여 쓰기 지연 SQL저장소에 저장한다.
	 - 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송
- 플러시
	- em.flush() - 직접 호출
	- 트랜잭션 커밋 - 자동 호출
	- JPQL 쿼리 실행 - 자동 호출
		- DB에 직접 접근할 수 있기 때문에 DB에 변경사항을 미리 적용
-  플러시 모드 옵션
	- `FlushModeType.AUTO` - 커밋이나 쿼리를 실행할 때 플러시
	- `FlushModeType.COMMIT` - 커밋할 때만 플러시
- 플러시는 !
	- 영속성 컨텍스트를 비우지 않음
	- 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화
	- 트랜잭션이라는 작업 단위가 중요 -> 커밋 직전에만 동기화 하면 됨

### 준영속 상태
- 영속 -> 준영속
- 영속 상태의 엔티티가 영속성 컨텍스트에서 분리(detached)
- 영속성 컨텍스트가 제공하는 기능을 사용하지 못함
- 준영속 상태로 만드는 방법
	- `em.detach(엔티티)`
	- `em.clear()` - 영속성 콘텍스트 비움
	- `em.close()` - 영속성 콘텍스트 종료