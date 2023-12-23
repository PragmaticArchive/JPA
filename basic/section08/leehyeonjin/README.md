# 프록시와 연관관계 정리

---

### 프록시

**프록시 기초**

em.find() vs em.getReference()

- em.find() : 데이터베이스를 통해서 실제 엔티티 객체 조회.
- em.getReference() : 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회.

<img width="462" alt="Untitled" src="https://github.com/hgene0929/JPA/assets/90823532/fd05a756-b29e-44dd-923f-7ee78dc544f3">

**프록시 특징**

<img width="504" alt="Untitled (1)" src="https://github.com/hgene0929/JPA/assets/90823532/daa4b75a-9795-4f85-a8ba-ffcb03172df6">

실제 클래스를 상속받아서 만들어짐.

실제 클래스와 겉모양이 같다.

사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 됨.

프록시 객체는 실제 객체의 참조(target)를 보관.

프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드 호출.

---

프록시 객체는 처음 사용할 때 한번만 초기화.

프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아님 ⇒ 초기화 되면 프록시 객체를 통해서 실제 엔티티에 접근 가능.

프록시 객체는 원본 엔티티를 상속받음 ⇒ 타입 체크시 주의해야 함( == 비교가 아니라, instance of 를 사용할 것).

영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티 반환.

영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 문제 발생.

- 하이버네이트는 LazyInitializationException 예외를 터트림.
</br></br>

**프록시 객체의 초기화**

```java
Member member = new Member();
member.setUsername("hello");

manager.persist(member);

manager.flush(); // DB에 변경내용 반영
manager.clear(); // 영속성 컨텍스트를 비움
// 이 시점부터는 영속성 컨텍스트의 1차 캐시가 비워져있다(Member 데이터 모름)

// 프록시 객체 호출
Member findMember = manager.getReference(Member.class, member.getId());

System.out.println(findMember.getClass()); // class hellojpa.Member$HibernateProxy$txI4IPsc

// 이 시점에는 실제객체(target) 호출 X -> 프록시 객체에는 id만 가지고 있기 때문에 그 아이디 뱉어냄
System.out.println(findMember.getId());

// 이 시점에는 프록시 객체가 가지지 않은 값을 호출하고 있음
// 따라서 이때, 실제 객체(target) 호출 => DB에 Select 쿼리 날림
System.out.println(findMember.getUsername());

// 이 이후로는 영속성 컨텍스트에 의해 프록시 - 실제 객체 간의 데이터가 연결됨
// 따라서 이후로는 Select 쿼리 필요 없음
System.out.println(findMember.getUsername());
```

<img width="623" alt="Untitled (2)" src="https://github.com/hgene0929/JPA/assets/90823532/95fca506-ba0f-4a8e-9028-28300da83a81">

실제 객체 호출시 일어나는 일(프록시 객체 초기화)

1. 프록시 객체에 없는 값을 호출(name)이 없음을 확인.
2. JPA가 영속성 컨텍스트에 실제 객체(target)을 가져오기를 요청.
3. 영속성 컨텍스트는 실제 엔티티를 DB에서 조회 후, 프록시 객체와 연결.
</br></br>

**프록시 확인**

```java
// 프록시 인스턴스의 초기화 여부 확인
PersistenceUnitUtil.isLoaded(Object entity)

// 프록시 클래스 확인 방법
entity.getClass().getName() //..javasist.. or HibernateProxy..

// 프록시 강제 초기화
org.hibernate.Hibernate.initialize(entity);

// 참고 : JPA 표준은 강제 초기화 없음(강제 호출)
member.getName()
```

---

### 즉시 로딩과 지연 로딩

Member를 조회할 때 Team도 함께 조회해야 할까?

<img width="644" alt="Untitled (3)" src="https://github.com/hgene0929/JPA/assets/90823532/1ca9354a-7e8a-490c-810c-83ffe5f764f6">

```java
Member member = manager.find(Member.class, 1L);
printMember(member);
printMemberAndTeam(member);

// 회원만 출력 : 이 시점에는 Member 정보만 필요한데.. Team까지 join하면 성능적 손해
private static void printMember(Member member) {
	String username = member.getUsername();
	System.out.println("member name = " + username);
}

// 회원과 팀 출력 : 이 시점에야 말로 Member 조회시, Team 조회가 필요한 경우
private static void printMemberAndTeam(Member member) {
	String username = member.getUsername();
	System.out.println("member name = " + username);

	Team team = member.getTeam();
	String teamName = team.getName();
	System.out.println("team name = " + teamName);
}
```

**단순히 member 정보만 사용하는 비즈니스 로직이라면?**

<img width="591" alt="Untitled (4)" src="https://github.com/hgene0929/JPA/assets/90823532/452c0d5e-9ff2-40c6-8f72-b6e2ed26a223">

지연로딩 LAZY를 사용해서 프록시로 조회.

프록시 객체의 초기화는 해당 참조 객체(연관관계의 엔티티)의 실제 속성에 접근(사용)하는 시점.

- 단순히 m.getTeam() 만으로는 조회 X.

```java
@ManyToOne(fetch = FetchType.LAZY) // 지연로딩 방식 적용
@JoinColumn(name = "team_id")
private Team team;

// member, team 객체 호출(지연로딩 방식)
Member member = new Member();
member.setUsername("memberA");

Team team = new Team();
team.setName("teamA");

member.setTeam(team);

manager.persist(member);
manager.flush();
manager.clear();

Member m = manager.find(Member.class, member.getId());

System.out.println("m.team = " + m.getTeam().getClass());
System.out.println("여기까지는 프록시 객체가 m.team 객체를 대체");

// 이 시점에 프록시 객체가 영속성 컨텍스트에 요청해서 실제 객체(Team)을 조회한 후, 
// 프록시 객체와 실제 객체 연결
m.getTeam().getName();
```

```sql
-- Member m = manager.find(Member.class, member.getId());
-- 위 코드에 대한 select문
-- team과 연관관계가 있지만, 지연로딩 방식으로 설정되어 단순 조회시에는 아직 team 객체는 조회하지 않고 프록시 객체로 대체
Hibernate: 
select
    member0_.id as id1_1_0_,
    member0_.createdBy as createdB2_1_0_,
    member0_.createdDate as createdD3_1_0_,
    member0_.lastModifiedBy as lastModi4_1_0_,
    member0_.lastModifiedDate as lastModi5_1_0_,
    member0_.team_id as team_id7_1_0_,
    member0_.username as username6_1_0_ 
from
    Member member0_ 
where
    member0_.id=?

-- System.out.println("m.team = " + m.getTeam().getClass());
-- 위 코드에 대한 결과
-- 따라서 현재 team 객체는 프록시 객체임을 알 수 있음
m.team = class hellojpa.Team$HibernateProxy$h64MPVZO
여기까지는 프록시 객체가 m.team 객체를 대체

-- m.getTeam().getName();
-- 이제 team 객체의 실제 속성에 접근이 필요함으로 이 시점에 실제 객체 조회
Hibernate: 
select
    team0_.id as id1_4_0_,
    team0_.name as name2_4_0_ 
from
    Team team0_ 
where
    team0_.id=?
```
</br></br>

**Member와 Team를 함께 자주 사용한다면?**

<img width="591" alt="Untitled (5)" src="https://github.com/hgene0929/JPA/assets/90823532/f3e51765-80c1-402f-80c0-26a75c78646e">

Member와 Team을 거의 항상 같이 사용하는데, 지연로딩 방식으로 설정되어 있다면 매번 조회 쿼리가 2번씩 따로 네트워크를 타게 될 것 ⇒ 성능 저하.

따라서 즉시 로딩 EAGER을 사용해서 한꺼번에 함께 조회.

```java
@ManyToOne(fetch = FetchType.EAGER) // 즉시로딩 방식 적용
@JoinColumn(name = "team_id")
private Team team;

// member, team 객체 호출(즉시로딩 방식)
Team team = new Team();
team.setName("teamA");
manager.persist(team);

Member member = new Member();
member.setUsername("memberA");
member.setTeam(team);
manager.persist(member);

manager.flush();
manager.clear();

// 이 시점에서 join문을 사용해서 연관관계의 엔티티를 동시에 조회해옴
Member m = manager.find(Member.class, member.getId());

System.out.println("m.team = " + m.getTeam().getClass());

m.getTeam().getName();
```

```sql
-- Member m = manager.find(Member.class, member.getId());
-- 연관관계의 엔티티를 조회해오는 동시에 join문을 통해 연관관계의 엔티티도 함께 조회
Hibernate: 
select
    member0_.id as id1_1_0_,
    member0_.createdBy as createdB2_1_0_,
    member0_.createdDate as createdD3_1_0_,
    member0_.lastModifiedBy as lastModi4_1_0_,
    member0_.lastModifiedDate as lastModi5_1_0_,
    member0_.team_id as team_id7_1_0_,
    member0_.username as username6_1_0_,
    team1_.id as id1_4_1_,
    team1_.name as name2_4_1_ 
from
    Member member0_ 
left outer join
    Team team1_ 
        on member0_.team_id=team1_.id 
where
    member0_.id=?

-- System.out.println("m.team = " + m.getTeam().getClass());
-- 이 시점에 이미 연관관계의 엔티티 객체도 실제 객체
m.team = class hellojpa.Team

-- m.getTeam().getName();
-- 따라서 연관관계의 객체를 사용하려고 해도 따로 select문이 날아갈 필요없음
```
</br></br>

**프록시와 즉시로딩 주의**

- 가급적 지연 로딩만 사용(특히 실무에서).
- 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생.
- 즉시 로딩은 JPQL에서 N+1 문제를 일으킨다.
- @ManyToOne, @OneToOne은 기본이 즉시 로딩 → LAZY로 사용할 것!
- @OneToMany, @ManyToMany는 기본이 지연 로딩.

> 💡 **N + 1 문제**
> 
> 최초 쿼리 1개를 날렸는데(select * from Entity), Entity의 내부에 있는 연관관계의 엔티티를 조회해오기 위한 추가 쿼리가 N개(연관관계 필드의 데이터 개수)가 추가적으로 날아가게 되는 문제.

```java
Team teamA = new Team();
teamA.setName("teamA");
manager.persist(teamA);

Member memberA = new Member();
memberA.setUsername("memberA");
memberA.setTeam(teamA);
manager.persist(memberA);

Team teamB = new Team();
teamB.setName("teamB");
manager.persist(teamB);

Member memberB = new Member();
memberB.setUsername("memberB");
memberB.setTeam(teamB);
manager.persist(memberB);

manager.flush();
manager.clear();

// 이 시점에서 join문을 사용해서 연관관계의 엔티티를 동시에 조회해옴
List<Member> members = manager.createQuery("select m from Member m", Member.class)
	.getResultList();
```

```sql
Hibernate: 
/* 
원래 의도한 sql(Member 목록을 조회하고자 함) 1개
select m from Member m 
*/ 
select
    member0_.id as id1_1_,
    member0_.createdBy as createdB2_1_,
    member0_.createdDate as createdD3_1_,
    member0_.lastModifiedBy as lastModi4_1_,
    member0_.lastModifiedDate as lastModi5_1_,
    member0_.team_id as team_id7_1_,
    member0_.username as username6_1_ 
from
    Member member0_

/* 
의도하지 않은 추가적인 sql N개 발생
select * from Team t where m.team_id = t.id
PK로 정확히 엔티티 한개를 단건조회할때는 상관X
이런식으로 리스트 조회가 발생할 경우, 조회된 엔티티(Member)의 연관관계 맵핑 로딩방법이 즉시 로딩이라면
엔티티를 조회하는 즉시 다시 조회 쿼리를 날리게 됨으로 발생하는 문제 N + 1 
*/ 
Hibernate: 
select
    team0_.id as id1_4_0_,
    team0_.name as name2_4_0_ 
from
    Team team0_ 
where
    team0_.id=?

Hibernate: 
select
    team0_.id as id1_4_0_,
    team0_.name as name2_4_0_ 
from
    Team team0_ 
where
    team0_.id=?
```

> 💡 **fetch join**
>
> 기본 연관관계 전략은 LAZY로 설정 → 쿼리시 만약 동시에 join하고 싶은 연관관계의 엔티티 조회시에는 fetch join을 사용해 동적으로 필요한 때에만 join 한방 쿼리를 날릴 수 있도록 한다.

```java
manager.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();
```

---

### 영속성 전이 : CASCADE

특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶은 경우.

- 영속성 전이는 연관관계를 맵핑하는 것과는 아무 관련이 없음.
- 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화하는 편리함을 제공할 뿐임.

Ex. 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장.

```java
@ManyToOne
@JoinColumn(name = "parent_id")
private Parent parent;

@OneToMany(mappedBy = "parent")
private List<Child> children = new ArrayList<>();

public void addChild(Child child) {
	children.add(child);
	child.setParent(this);
}

// (1) 위와 같은 연관관계의 경우
Child child1 = new Child();
Child child2 = new Child();

Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

manager.persist(parent);
manager.persist(child1);
manager.persist(child2);
```

```java
@ManyToOne
@JoinColumn(name = "parent_id")
private Parent parent;

@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
private List<Child> children = new ArrayList<>();

public void addChild(Child child) {
	children.add(child);
	child.setParent(this);
}

// (2) 위와 같은 연관관계의 엔티티에서 Parent를 중심으로 코드를 짜고 싶은 경우
Child child1 = new Child();
Child child2 = new Child();

Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

manager.persist(parent);
```

1. (1)번 예제의 경우, 모든 엔티티를 영속화해야 한다.
2. (2)번 예제의 경우, 중심(Parent) 엔티티를 영속화하면 그 영속화가 자식에게 까지 전이되어 함께 영속화된다.
</br></br>

**CASCADE의 종류**

- ALL : 모두 적용.
- PERSIST : 영속.
- REMOVE : 삭제.
- MERGE : 병합.
- REFRESH : REFRESH.
- DETACH : DETACH.

---

### 고아 객체

고아 객체 제거 : 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제.

orphanRemoval = true.

- 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고, 삭제하는 기능.
- 참조하는 곳이 하나일 때 사용해야 함(Parent - Child의 라이프사이클이 거의 동일할 때).
- 특정 엔티티가 개인 소유할 때(단일 소유자) 사용.
- @OneToOne, @OneToMany만 가능.

> 참고 : 개념적으로 부모를 제거하면 자식은 고아가 된다. 따라서 고아 객체 제거 기능을 활성화하면, 부모를 제거할 때 자식도 함께 제거된다. 이것은 CascadeType.REMOVE 처럼 동작한다.

```java
@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Child> children = new ArrayList<>();

Child child1 = new Child();
Child child2 = new Child();

Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

// Parent가 관리하는 자식까지 한꺼번에 영속화
manager.persist(parent);

manager.flush();
manager.clear();

Parent findParent = manager.find(Parent.class, parent.getId());
// Parent를 삭제하면, 고아객체가 된 Child 엔티티도 자동 삭제됨
manager.remove(findParent);
```
</br></br>
**영속성 전이 + 고아 객체, 생명주기**

CascadeType.ALL + orphanRemoval = true.

- 스스로 생명주기를 관리하는 엔티티는 em.persist()로 영속화, em.remove()로 제거.
- 두 옵션을 모두 활성화하면 부모 엔티티를 통해서 자식의 생명주기를 관리할 수 있음 ⇒ DAO or Repository가 필요없어짐.
- 도메인 주도 설계(DDD)의 Aggregate Root 개념을 구현할 때 유용.
