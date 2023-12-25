# 객체 지향 쿼리 언어(중급 문법)

---

### JPQL : 경로 표현식

**경로 표현식**

점(.)을 찍어 객체 그래프를 탐색하는 것.

- 상태필드(state field) : 단순히 값을 저장하기 위한 필드.
- 연관필드(association field) : 연관관계를 위한 필드.
    - 단일 값 연관필드 : @ManyToOne, @OneToOne, 대상이 엔티티.
    - 컬렉션 값 연관필드 : @OneToMany, @ManyToMany, 대상이 컬렉션.

```java
select m.username // 상태필드
from Member m
	join m.team t   // 단일값 연관 필드
	join m.orders o // 컬렉션값 연관 필드
where t.name = '팀A'
```
</br>

**경로 표현식 특징**

상태 필드 : 경로 탐색의 끝, 탐색 X.

단일 값 연관 경로 : 묵시적 내부 조인(inner join) 발생, 탐색 O.

컬렉션 값 연관 경로 : 묵시적 내부 조인 발생, 탐색 X.

- from절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능.

```java
// 상태 필드
// join 쿼리 X
// 점(.) 연산자로 더이상 탐색 불가
select m.username from Member m

// 단일값 연관 필드
// join 쿼리 O
// 점(.) 연산자로 그 값 내부의 값 탐색 가능
select m.team.name from Member m

// 컬렉셥값 연관 필드
// join 쿼리 O
// 점(.) 연산자로 그 값 내부의 값 탐색 불가
select t.members from Team t
// 다만, 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색가능
select m.username from Team t join t.members m
```
</br>

**명시적 조인, 묵시적 조인**

명시적 조인 : join 키워드 직접 사용.

묵시적 조인 : 경로 표현식에 의해 묵시적으로 조인 발생(내부 조인만 가능).
</br>

**경로 탐색을 사용한 묵시적 조인시 주의사항**

- 항상 내부 조인이 발생.
- 컬렉션은 경로 탐색의 끝, 명시적 조인을 통해 별칭을 얻어야 함.
- 경로 탐색은 주로 select, where 절에서 사용하지만 묵시적 조인으로 인해 SQL의 from(join)절에 영향을 줌.

> 💡 **실무 조언**
>
> - 가급적 묵시적 조인 대신 명시적 조인 사용.
> - 조인은 SQL 튜닝에 중요 포인트.
> - 묵시적 조인은 조인이 일어나는 상황을 한눈에 파악하기 어려움.

---

### JPQL : 페치 조인(fetch join)

**페치 조인(fetch join)**

SQL 조인 종류는 아님(데이터베이스의 쿼리문에 페치 조인 존재 X).

JPQL에서 성능 최적화를 위해 제공하는 기능.

연관된 엔티티나 컬렉션을 SQL 한번에 함께 조회하는 기능.

fetch join 명령어 사용.

```java
[left [outer] | inner] join fetch 조인경로
```
</br>

**엔티티 페치 조인**

회원을 조회하면서 연관된 팀도 함께 조회(SQL 한번에).

SQL을 보면 회원 뿐만 아니라 팀(T.*)도 함께 select.

```java
// jpql
select m from Member m join fetch m.team

// sql
select M.*, T.* from Member m
inner join Team T on M.team_id = T.id
```

```java
// fetch join 사용 X
String jpql = "select m from Member m";
List<Member> members = manager.createQuery(jpql, Member.class).getResultList();

for (Member member : members) {
	// 지연로딩으로 되어있는 경우에도 N+1 문제 발생가능
	// 현재 영속성 컨텍스트에는 Member만 존재하고, Team은 프록시
	// 따라서 Team의 값을 사용하려고 하면 Team과 연관관계 개수(N)만큼 쿼리가 추가 발생
	System.out.println("username = " + member.getUsername() + ", teamName = " + member.getTeam().getName());
}

// fetch join 사용 O
String jpql = "select m from Member m join fetch m.team";
List<Member> members = manager.createQuery(jpql, Member.class).getResultList();

for (Member member : members) {
	// 페치조인으로 회원과 팀을 함께 조회해서 지연 로딩 X
	System.out.println("username = " + member.getUsername() + ", teamName = " + member.getTeam().getName());
}
```
</br>

**컬렉션 페치 조인**

일대다 관계, 컬렉션 페치 조인.

컬렉션에 대한 페치 조인시, 데이터가 예상보다 많아질 수 있음.

<img width="548" alt="Untitled" src="https://github.com/hgene0929/JPA/assets/90823532/4b01a8cc-1abf-4205-ae86-e8cc5afc0d05">

```java
// jpql
select t from Team t join fetch t.members
where t.name = '팀A'

// sql
select T.*, M.* from Team T
inner join Member m on T.id = M.team_id
where T.name = '팀A'

// 컬렉션 페치 조인 수행시 데이터가 뻥튀기 됨
// 아래 쿼리 결과 :
// teamname = TeamA, members size = 2
// teamname = TeamA, members size = 2 -> 2번 같은 결과가 반환됨
// teamname = TeamB, members size = 1
String jpql = "select t from Team t join fetch t.members"
List<Team> teams = manager.createQuery(jpql, Team.class).getResultList();

for (Team team : teams) {
	System.out.println("teamname = " + team.getName() + ", members size = " + team.getMembers().size());
}
```
</br>

**페치 조인과 DISTINCT**

SQL의 DISTINCT는 중복된 결과를 제거하는 명령.

JPQL의 DISTINCT는 2가지 기능을 제공함.

1. SQL에 DISTINCT 추가.
- SQL에 DISTINCT를 추가하지만 데이터가 다르므로(똑같은 TeamA와 연관관계를 맺고있지만, Member id가 다름) SQL 결과에서 중복제거 실패.

<img width="456" alt="Untitled (1)" src="https://github.com/hgene0929/JPA/assets/90823532/7b398536-35cf-488f-9253-be02256bd055">

2. 애플리케이션에서 엔티티 중복 제거.
- 따라서 애플리케이션에서 추가로 중복 제거 시도.
- 같은 식별자를 가진 Team 엔티티를 제거.

<img width="434" alt="Untitled (2)" src="https://github.com/hgene0929/JPA/assets/90823532/09aaa5c9-7cd7-4513-97f1-5f6b64f34395">

> 페치 조인으로 컬렉션을 조회할 때의 추가적인 데이터 발생 문제 해결방안.
>

```java
// jpql distinct + fetch join
// 아래 쿼리 결과 :
// teamname = TeamA, members size = 2
// teamname = TeamB, members size = 1
String jpql = "select disinct t from Team t join fetch t.members"
List<Team> teams = manager.createQuery(jpql, Team.class).getResultList();

for (Team team : teams) {
	System.out.println("teamname = " + team.getName() + ", members size = " + team.getMembers().size());
}
```
</br>

**페치 조인 vs 일반 조인**

일반 조인 실행시 연관된 엔티티를 함께 조회하지 않음.

JPQL은 결과를 반환할 때 연관관계 고려X.

단지 select절에 지정한 엔티티만 조회.

페치 조인을 사용할 때만 연관된 엔티티도 함께 조회(즉시 로딩).

페치 조인은 객체 그래프를 SQL 한번에 조회하는 개념.
</br>

**페치 조인의 한계**

페치 조인 대상에는 별칭을 줄 수 없다.

둘 이상의 컬렉션은 페치 조인할 수 없다.

컬렉션을 페치 조인하면 페이징 API(setFirstResult, setMaxResult)를 사용할 수 없다.

- 일대일, 다대일 같은 단일 값 연관 필드들은 페치 조인해도 페이징 가능.

> 💡 **컬렉션 페치 조인시, 페이징하는 법**
>
> 1. 일대다 관계를 다대일로 풀어낸다.
> 2. @BatchSize 혹은 batch_fetch_size글로벌 설정을 사용해서 한꺼번에 조회할때 연관엔티티 개수를 지정해준다.

</br>

**페치조인의 특징**

연관된 엔티티들을 SQL 한번으로 조회 → 성능 최적화.

엔티티에 직접 적용하는 글로벌 로딩 전략보다 우선함.

- @OneToMany(fetch = FetchType.LAZY)

실무에서 글로벌 로딩 전략은 모두 지연 로딩 → 최적화가 필요한 곳은 페치 조인 적용.

> 💡 **페치 조인의 사용**
>
> - 페치 조인은 객체 그래프를 유지할 때 사용하면 효과적.
> - 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야 한다면, 페치 조인보다는 일반 조인을 사용하고 필요한 데이터들만 조회해서 DTO로 반환하는 것이 효과적.

---

### 다형성 쿼리

**type**

조회 대상을 특정 자식으로 한정

Ex. Item 중에 Book, Movie를 조회해라.

```java
// jpql
select i from Item i where type(i) in (Book, Movie)

// sql
select i from i where i.DTYPE in ('Book', 'Movie')
```
</br>

**treat**

자바 타입 캐스팅과 유사.

상속 구조에서 부모 타입을 특정 자식 타입으로 다룰 때 사용.

```java
// jpql
select i from Item i where treat(i as Book).author = 'Kim'
```

---

### 엔티티 직접 사용

**기본 키 값**

JPQL에서 엔티티를 직접 사용하면 SQL에서 해당 엔티티의 기본 키 값을 사용.

```java
// jpql
select count(m.id) from Member m // 엔티티 아이디를 사용
select count(m) from Member m    // 엔티티를 직접 사용

// sql : 둘 다 같은 SQL 실행
select count(m.id) as cnt from Member m

// jpql
select m from Member m where m.id =: memberId // 엔티티 아이디를 사용
select m from Member m where m =: member      // 엔티티를 직접 사용

// sql
select m.* from Member m where m.id = ?

// jpql
select m from Member m where m.team.id =: teamId // 엔티티 아이디를 사용
select m from Member m where m.team =: team      // 엔티티를 직접 사용
```

---

### Named 쿼리

미리 정의해서 이름을 부여해두고 사용하는 JPQL.

정적 쿼리.

어노테이션, XML에 정의.

- XML이 항상 우선권을 가짐.
- 애플리케이션 운영 환경에 따라 다른 XML을 배포 가능.

애플리케이션 로딩 시점에 초기화 후 재사용 ⇒ 애플리케이션 로딩 시점에 쿼리를 검증.

- JPA 쿼리는 SQL로 파싱되어야 하는데, 컴파일타임에 검증되어 SQL로 파싱된 결과가 캐싱되어 있는 상태.

```java
// NamedQuery 어노테이션
@Entity
@NamedQuery(
	name = "Member.findByUsername",
	query = "select m from Member m where m.username =: username"
)
public class Member {...}

List<Member> resultList = manager.createNamedQuery("Member.findByUsername", Member.class)
		.setParameter("username", "회원1")
		.getResultList();
```

```xml
<!-- NamedQuery XML -->
<!-- [META-INF/persistence.xml] -->
<persistence-unit name="hellojpa" >
<mapping-file>META-INF/ormMember.xml</mapping-file>

<!-- [META-INF/ormMember.xml] -->
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm" version="2.1">
	<named-query name="Member.findByUsername">
		<query><![CDATA[
		select m
		from Member m
		where m.username = :username
		]]></query>
		</named-query>
	<named-query name="Member.count">
		<query>select count(m) from Member m</query>
	</named-query>
</entity-mappings>
```

> 💡 **spring data jpa Named 쿼리(이름 없는 Named 쿼리)**
>
> ```java
> public interface UserRepository extends JpaRepository<User, Long> {
>	
>	// spring data jpa의 named query 작성방식
>	@Query("select u from User u where u.emailAddress =? 1")
>	User findByEmailAddress(String emailAddress);
> }
> ```

---

### 벌크 연산

**예제**

재고가 10개 미만인 모든 상품의 가격을 10% 상승하려면?

JPA 변경 감지 기능으로 실행하려면 너무 많은 SQL 실행.

- 변경된 데이터가 100건이라면 100번의 UPDATE SQL이 실행됨.
1. 재고가 10개 미만인 상품을 리스트로 조회한다.
2. 상품 엔티티의 가격을 10% 증가한다.
3. 트랜잭션 커밋 시점에 변경감지가 동작한다.

---

**벌크 연산 사용시**

쿼리 한번으로 여러 테이블 로우 변경(엔티티).

executeUpdate()의 결과는 영향받은 엔티티 수 반환.

UPDATE, DELETE 지원.

INSERT(하이버네이트 지원).

```java
String sql = "update Product p " +
						 "set p.price = p.price * 1.1 " +
						 "where p.stockAmount <: stockAmount";
int resultCount = manager.createQuery(sql)
				.setParameter("stockAmount", 10)
				.executeUpdate();
```
</br>

**벌크 연산 주의**

벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리.

- 벌크 연산을 먼저 실행.
- 벌크 연산 수행 후 영속성 컨텍스트 초기화.
```java
Member member1 = new Member();
manager.persist(member1);

Member member2 = new Member();
manager.persist(member2);

// 벌크 연산 수행시 자동 flush -> DB 반영
String sql = "update Product p " +
						 "set p.price = p.price * 1.1 " +
						 "where p.stockAmount <: stockAmount";
int resultCount = manager.createQuery(sql)
				.setParameter("stockAmount", 10)
				.executeUpdate();

// 벌크 연산 수행후 clear -> 영속성 컨텍스트 초기화
manager.clear();

// 나머지 연산 수행
manager.find(Member.class, member1.getId());
...
```
