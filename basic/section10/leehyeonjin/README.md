# 객체지향 쿼리 언어(JPQL)

---

### 소개

**JPA는 다양한 쿼리 방법을 지원한다**

- JPQL
- JPA Criteria
- QueryDSL
- 네이티브 SQL → 특정 DB에 종속적인 쿼리를 짜야하는 경우
- JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 함께 사용
</br></br>

**가장 단순한 조회 방법**

- EntityManager.find()
- 객체 그래프 탐색(a.getB())

> 💡 **나이가 18살 이상인 회원을 모두 검색하고 싶다면?**
>
> - JPA를 사용하면 엔티티 객체를 중심으로 개발. 
> - 문제는 검색 쿼리 → 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색.
> - 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능.
> - 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요함.

</br>

**JPQL(Java Persistence Query Language)**

SQL을 추상화한 JPQL이라는 객체지향 쿼리 언어 제공.

- SQL을 추상화하여 특정 데이터베이스 SQL에 의존하지 않음.

SQL과 문법 유사, select, from, where, group by, having, join 지원.

JPQL은 엔티티 객체를 대상으로 쿼리.

SQL은 데이터베이스 테이블을 대상으로 쿼리.

```java
// 검색
String jpql = "select m from Member m where m.age > 18";
List<Member> members = manager.createQuery(jpql, Member.class).getResultList();
```

```java
Hibernate: 
/* select m from Member m where m.age > 18 */ 
select
    member0_.id as id1_3_,
    member0_.age as age2_3_,
    member0_.username as username3_3_ 
from
    Member member0_ 
where
    member0_.age>18
```
</br>

**JPA Criteria**

문자가 아닌 자바코드로 JPQL을 작성할 수 있음.

JPQL 빌더 역할(JPA 공식 기능).

단점 : 너무 복잡하고, 실용성이 없음.

> JPQL 만으로는 동적 쿼리를 생성하는 것이 너무 어렵고 복잡하다.
동적 쿼리 작성시, Criteria 대신 QueryDSL 사용 권장.

```java
// Criteria 사용 준비
CriteriaBuilder builder = manager.getCriteriaBuilder();
CriteriaQuery<Member> query = builder.createQuery(Member.class);

// 루트 클래스(조회를 시작할 클래스)
Root<Member> m = query.from(Member.class);

// 쿼리 생성 : 쿼리가 자바 코드(메소드가 제공됨)로 생성됨
CriteriaQuery<Member> cq = query.select(m).where(builder.greaterThan(m.get("age"), 18));
List<Member> members = manager.createQuery(cq).getResultList();
```
</br>

**QueryDSL**

문자가 아닌 자바코드로 JPQL을 작성할 수 있음.

JPQL 빌더 역할.

컴파일 시점에 문법 오류를 찾을 수 있음.

동적쿼리 작성 편리함.

단순하고 쉬움.

실무에서 사용 권장.

```java
JPAFactoryQuery query = new JPAQueryFactory(em);
QMember m = QMember.member;

List<Member> list = query.selectFrom(m)
	.where(m.age.gt(18))
	.orderBy(m.name.desc())
	.fetch();
```
</br>

**네이티브 SQL**

SQL을 직접 사용하는 기능.

JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능.

Ex. 오라클 Connect by, 특정 DB만 사용하는 SQL 힌트.

```java
String sql = "SELECT id, age, team_id, name from Member WHERE name = 'kim'";
List<Member> members = manager.createNamedQuery(sql, Member.class).getResultList();
```
</br>

**JDBC 직접 사용, SpringJdbcTemplate 등**

JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링 JdbcTemplate, 마이바티스 등을 함께 사용 가능.

단 영속성 컨텍스트를 적절한 시점에 강제로 플러시 해주어야 함.

Ex. JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 플러시.

---

### 기본 문법과 쿼리 API

**JPQL 문법**

```java
// select 문
select
from
[where]
[group by]
[having]
[order by]

// update 문
update
[where]

// delete 문
delete
[where]
```

- select 뒤에 명시된 m을 alias 처럼 사용(인스턴스).

```java
select m from Member as m where m.age > 18
```

- 엔티티와 속성은 대소문자 구분 O(Member, age).
- JPQL 키워드는 대소문자 구분 X(Select, From, where).
- 엔티티 이름 사용, 테이블 이름이 아님(Member).
- 별칭은 필수(m) (as는 생략 가능).
</br>

**집합과 정렬**

```java
select 
	.count(m),   // 회원수
	.sum(m.age), // 나이합
	.avg(m.age), // 평균 나이
	.max(m.age), // 최대 나이
	.min(m.age)  // 최소 나이
from Member m
```
</br>

**TypeQuery, Query**

- TypeQuery : 반환 타입이 명확할 때 사용.
- Query : 반환 타입이 명확하지 않을 때 사용.

```java
TypedQuery<Member> typedQuery = manager.createQuery("select m from Member m", Member.class);
Query query = manager.createQuery("select m.username, m.age from Member m");
```
</br>

**결과 조회 API**

- query.getResultList() : 결과가 하나 이상일 때, 리스트 반환.
    - 결과가 없으면 빈 리스트 반환 ⇒ NullPointerException 걱정 없음.
- query.getSingleResult() : 결과가 정확히 하나, 단일 객체 반환.
    - 결과가 없으면 NoResultException.
    - 둘 이상이면 NonUniqueResultException.
</br>

**파라미터 바인딩 : 이름 기준, 위치 기준**

```java
Query query1 = manager.createQuery("select m from Member m where m.username =: username");
query1.setParameter("username", "멤버A");

Query query2 = manager.createQuery("select m from Member m where m.username = ?1");
query2.setParameter(1, "멤버B");
```

---

### 프로젝션

select절에 조회할 대상을 지정하는 것.

프로젝션 대상 : 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자 등 데이터 타입).

distinct로 중복 제거.

```java
select m from Member m // 엔티티 프로젝션
select m.team from Member m // 엔티티 프로젝션
select m.address from Member m // 임베디드 타입 프로젝션
select m.username, m.age from Member m // 스칼라 타입 프로젝션
```

```java
Member member = new Member();
member.setUsername("member");
member.setAge(10);
manager.persist(member);

manager.flush();
manager.clear();

// 아래의 조회 쿼리(JPQL)로 조회된 엔티티는 영속성 컨텍스트에 모두 관리된다
List<Member> members = manager.createQuery("select m from Member m", Member.class).getResultList();

// 따라서 아래의 setter에 따라 update 쿼리로 날아감
Member findMember = members.get(0);
findMember.setAge(20);

// 다음과 같이 참조를 통해 연관관계의 엔티티를 조회하는 경우 -> 자동으로 join 쿼리를 날린다
// 다만, 이러한 경우 join 쿼리에 따라 성능 등의 여러가지 튜닝이 가능한 상황이기 때문에 실제 join 까지 명시해주는 것을 권장한다 
List<Team> teams = manager.createQuery("select m.team from Member m", Team.class).getResultList();
// 아래와 같이 실제 쿼리와 비슷하게 작성하여 결과를 예측할 수 있도록 해야한다
List<Team> teams = manager.createQuery("select t from Member m join m.team t", Team.class).getResultList();
```
</br>

**프로젝션 : 여러 값 조회**

select m.username, m.age from Member m

1. Query 타입으로 조회.
2. Object[] 타입으로 조회.
3. new 명령어로 조회.
- 단순값을 DTO로 바로 조회.
- 패키지명을 포함한 전체 클래스명 입력.
- 순서와 타입이 일치하는 생성자 필요.

```java
// 순서와 타입이 일치하는 생성자
public MemberDto(long id, String username, int age) {
	this.id = id;
	this.username = username;
	this.age = age;
}

// 호출할 dto의 패키지명부터 작성해주어야 함
List<MemberDto> memberDtos 
	= manager.createQuery("select new hellojpa.MemberDto(m.id, m.username, m.age) from Member m").getResultList();
```

---

### 페이징 API

JPA는 페이징을 다음 두 API로 추상화함.

1. setFirstResult(int startPosition) : 조회시작 위치(0부터 시작).
2. setMaxResults(int maxResult) : 조회할 데이터 수.

```java
List<Member> members = manager.createQuery("select m from Member m order by m.age desc", Member.class)
				.setFirstResult(0)
				.setMaxResults(10)
				.getResultList();
```
</br>

**페이징 방언**

JPQL을 통해 페이징 쿼리를 날릴경우, 데이터베이스의 방언에 따른 페이징 쿼리를 생성하여 날려줌.

> 💡 MySQL 페이징 쿼리
>
> ```sql
> SELECT
> 	M.ID AS ID,
>	M.AGE AS AGE,
>	M.TEAM_ID AS TEAM_ID,
>	M.NAME AS NAME
> FROM MEMBER M
> ORDER BY M.NAME DESC 
> LIMIT ?, ?
> ```
>
> ```sql
> SELECT * 
> FROM ( SELECT ROW_.*, ROWNUM ROWNUM_
>		 FROM ( SELECT
>								M.ID AS ID,
>								M.AGE AS AGE,
>								M.TEAM_ID AS TEAM_ID,
>								M.NAME AS NAME
>						FROM MEMBER M
>						ORDER BY M.NAME
>					) ROW_
>		WHERE ROWNUM <= ?)
> WHERE ROWNUM_ > ?
> ```

---

### 조인

내부 조인 : select m from Member m [inner] join m.team t

외부 조인 : select m from Member m left [outer] join m.team t

세타 조인(연관관계가 없을때) : select count(m) from Member m, Team t where m.username = t.name
</br></br>

**조인 : on절**

on절을 활용한 조인

- join 대상 필터링.

```java
// Ex. 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인.

// JPQL:
SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'A'

// SQL:
SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='A'
```

- 연관관계 없는 엔티티 외부 조인.

```java
// Ex. 회원의 이름과 같은 팀의 이름이 같은 대상 외부 조인

// JPQL:
SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name

// SQL:
SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
```

---

### 서브쿼리

**서브 쿼리 지원 함수**

[not] exists (subquery) : 서브쿼리에 결과가 존재하면 참.

- {all | any | some} (subquery).
- all 모두 만족하면 참.
- any, some : 같은 의미 조건을 하나라도 만족하면 참.

[not] in (subquery) : 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참.

```java
// 팀A 소속인 회원
select m from Member m
where exists (select t from m.team t where t.name = ‘팀A')

// 전체 상품 각각의 재고보다 주문량이 많은 주문들
select o from Order o
where o.orderAmount > ALL (select p.stockAmount from Product p)

// 어떤 팀이든 팀에 소속된 회원
select m from Member m
where m.team = ANY (select t from Team t)
```
</br>

**JPA 서브쿼리 한계**

JPA는 where, having 절에서만 서브쿼리 사용가능.

select 절도 가능(하이버네이트 지원).

from 절의 서브쿼리는 현재 JPQL에서 불가능.

- 조인으로 풀 수 있으면 풀어서 해결.

---

### JPQL 타입 표현식과 기타식

- 문자 : ‘HELLO’, ‘She’’(’ 2개)s’
- 숫자 : 10L(Long), 10D(Double), 10F(Float)
- Boolean : TRUE, FALSE
- ENUM : hellojpa.MemberType.Admin( 패키지명 포함 ).
- 엔티티 타입 : TYPE(m) = Member( 상속관계에서 사용 ).

```java
// 상속관계의 DTYPE을 의미
select i from Item i where type(i) Book
```
</br>

**JPQL 기타(SQL과 문법이 같은 식)**

- EXISTS, IN.
- AND, OR, NOT.
- =, >, >=, <, <=, <>.
- BETWEEN, LIKE, IS NULL.

---

### 조건식(CASE식)

```java
// 기본 CASE식
select
	case when m.age <= 10 then '학생요금'
		when m.age >= 60 then '경로요금'
		else '일반요금'
	end
from Member m

// 단순 CASE식
select
	case t.name
		when '팀A' then '인센티브110%'
		when '팀B' then '인센티브120%'
		else '인센티브105%'
	end
from Team t
```

- COALESCE : 하나씩 조회해서 null이 아니면 반환.
- NULLIF : 두 값이 같으면 null 반환, 다르면 첫번째 값 반환.

```java
// 사용자 이름이 없으면 이름 없는 회원을 반환
select coalesce(m.username,'이름 없는 회원') from Member m

// 사용자 이름이 '관리자'면 null을 반환하고 나머지는 본인의 이름 반환
select NULLIF(m.username, '관리자') from Member m
```

---

### JPQL 기본 함수

- CONCAT
- SUBSTRING
- TRIM
- LOWER, UPPER
- LENGTH
- LOCATE
- ABS, SQRT, MOD
- SIZE, INDEX(JPA 용도)
    - SIZE : 컬렉션의 크기.
    - INDEX : 컬렉션의 @OrderColumn에서의 인덱스.
</br>

**사용자 정의 함수 호출**

하이버네이트는 사용전 방언에 추가해야 한다.

- 사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록한다.

```java
public MySQLDialect() {
    ...
		// MySQLDialect(DB 방언 클래스 예시)에 보면 사용자 정의함수를 선언하는 부가 존재한다
    this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
		...
}
```

```java
// 사용자 정의 함수 사용 형태
select function('group_concat', i.name) from Item i

// 사용자 정의 함수 사용 예제
select locate('de', 'abcdefg') from Member m // 4 -> de가 abcdefg에서 존재하는 인덱스(1부터 시작)
```
