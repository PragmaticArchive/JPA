# 다양한 연관관계 맵핑

---

### 다대일 [N : 1]

**다대일 단방향**

<img width="638" alt="Untitled" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/502ceecd-00e9-4dd7-a969-83ff5d7cb566">

@ManyToOne.

가장 많이 사용하는 연관관계.

다대일의 반대는 일대다.

```java
@ManyToOne
@JoinColumn(name = "team_id")
private Team team;
```
</br></br>

**다대일 양방향**

<img width="597" alt="Untitled (1)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/347edc50-985a-498a-a07b-6930e5267ecf">

외래키가 있는 쪽이 연관관계의 주인.

양쪽을 서로 참조하도록 개발.

```java
@OneToMany(mappedBy = "team") // 연관관계의 주인 설정
List<Member> members = new ArrayList<>();

// 일대다 엔티티의 연관관계 편의 메소드
public void addMember(Member member) {
	this.members.add(member);
	member.setTeam(this); // 연관관계 편의메소드를 통해 한쪽에 연관관계 주입시 나머지 엔티티에도 자동으로 주입되도록 설정
}

// 다대일 엔티티의 연관관계 편의 메소드
public void setTeam(Team team) {
	this.team = team;
	team.getMembers().add(this); // 연관관계 편의메소드를 통해 한쪽에 연관관계 주입시 나머지 엔티티에도 자동으로 주입되도록 설정
}
```

---

### 일대다 [1 : N]

**일대다 단방향**

<img width="600" alt="Untitled (2)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/421364ff-2f05-4857-b809-dfab0055c8fb">

@ManyToOne

일대다 단방향은 일대다에서 일이 연관관계의 주인.

테이블 일대다 관계는 항상 다쪽에 외래키가 있음.

객체와 테이블의 차이 때문에 반대편 테이블의 외래키를 관리하는 특이한 구조.

@JoinColumn을 꼭 사용해야 함. 그렇지 않으면 조인 테이블 방식을 사용함(중간에 테이블이 하나 추가 됨).

일대다 단방향 맵핑의 단점 :

- 엔티티가 관리하는 외래 키가 다른 테이블에 있음.
- 연관관계 관리를 위해 추가로 UPDATE SQL 실행해야 함.

> 일대다 단방향 맵핑보다는 다대일 양방향 맵핑을 사용할 것!

```java
@OneToMany // 1:N 관계에서 1이 주인이기 때문에 mappedBy 사용X
@JoinColumn(name = "team_id") // JoinColumn을 통해 연관관계를 맺는 엔티티의 외래키를 명시해줘야함
List<Member> members = new ArrayList<>();

// Member 테이블에는 두 엔티티의 연관관계를 위한 어떤 필드도 정의 X

Member member = new Member();
member.setUsername("MemberA");

manager.persist(member);

Team team = new Team();
team.setName("TeamA");
// 아래의 코드는 테이블에 insert 가능한 내용이 아님
// 두 엔티티의 연관관계를 테이블에 반영하기 위해서는 현재 내용을 반영하기 위해
// insert 쿼리 뿐만 아니라 update 쿼리가 한번 더 나감
team.getMembers().add(member);

manager.persist(team);
```

```sql
Hibernate: 
/* 
insert hellojpa.Member
manager.persist(member) 로 인해 생성된 쿼리
*/ 
insert into Member (username) 
values (?)

Hibernate: 
/* 
insert hellojpa.Team 
manager.persiste(team) 로 인해 생성된 쿼리
*/ 
insert into Team (name) 
values (?)

Hibernate: 
/* 
create one-to-many row hellojpa.Team.members
team.getMembers().add(member) 로 인해 생성된 쿼리
연관관계는 코드에서는 Team(일대다)만 가지고 있지만, 
실제 FK는 Member가 가지기 때문에 update 쿼리를 따로 날려줘서 반영
*/ 
update Member 
set team_id = ? 
where id=?
```
</br></br>

**일대다 양방향**

<img width="611" alt="Untitled (3)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/95250c30-3d39-4308-8c1e-a7fde783c4ea">

이런 맵핑은 공식적으로 존재하지 않음(스펙상 지원하지는 않음).

@JoinColumn(insertable = false, updatable = false)

읽기 전용 필드를 사용해서 양방향처럼 사용하는 방법.

> 일대다 양방향 맵핑보다는 다대일 양방향 맵핑을 사용할 것!

```java
@ManyToOne
@JoinColumn(name = "team_id", insertable = false, updatable = false) // 이 친구는 연관관계의 주인이 아니기 때문에 읽기전용임을 명시
private Team team;

@OneToMany // 1:N 관계에서 1이 주인이기 때문에 mappedBy 사용X
@JoinColumn(name = "team_id") // 이 친구가 연관관계의 주인이므로 FK를 여기서 update하겠다는 의미(관리)
List<Member> members = new ArrayList<>();
```

---

### 일대일 [1 : 1]

**일대일 관계**

@OneToOne

일대일 관계는 그 반대도 일대일.

주 테이블이나 대상 테이블 중에 외래키 선택 가능.

- 주 테이블에 외래키.
- 대상 테이블에 외래키.

외래키에 데이터베이스 유니크(UNI) 제약조건 추가.

---

1. 주 테이블에 외래키
- 주 객체가 대상 객체의 참조를 가지는 것처럼, 주 테이블에 외래키를 두고 대상 테이블을 찾음.
- 객체지향 개발자 선호.
- JPA 맵핑 편리.
- 장점 : 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능.
- 단점 : 값이 없으면 외래키에 null 허용.
2. 대상 테이블에 외래키
- 대상 테이블에 외래키가 존재.
- 전통적인 데이터베이스 개발자 선호.
- 장점 : 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경할 때 테이블 구조 유지.
- 단점 : 프록시 기능의 한계로 지연 로딩으로 설정해도 항상 즉시 로딩됨(프록시는 뒤에서 설명).
</br></br>

**일대일 : 주 테이블에 외래키 단방향**

<img width="548" alt="Untitled (4)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/a5b0360d-1e31-455e-b2a2-5212f09c109e">

다대일(@ManyToOne) 단방향 맵핑과 유사.

```java
@OneToOne
@JoinColumn(name = "locker_id")
private Locker locker;
```
</br></br>

**일대일 : 주 테이블에 외래키 양방향**

<img width="555" alt="Untitled (5)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/5155f884-aec1-4035-abaa-8cb8d333e7b5">

다대일 양방향 맵핑처럼 외래키가 있는 곳이 연관관계의 주인.

반대편은 mappedBy 적용.

```java
@OneToOne
@JoinColumn(name = "locker_id")
private Locker locker;

@OneToOne(mappedBy = "locker")
private Member member;
```
</br></br>

**일대일 : 대상 테이블에 외래키 단방향**

<img width="505" alt="Untitled (6)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/84b1304d-242e-41ab-a7a0-0279cb86ca91">

단방향 관계는 JPA 지원 X.

양방향 관계는 지원.
</br></br>

**일대일 : 대상 테이블에 외래키 양방향**

<img width="518" alt="Untitled (7)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/4dfc4905-e07e-4ba4-acc7-e81e36929fc8">

사실 일대일 주 테이블에 외래키 양방향과 맵핑 방법은 동일.

---

### 다대다 [N : M]

관계형 데이터베이스는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없음.

따라서 연결 테이블을 추가해서 일대다, 다대일 관계로 풀어내야 함.

<img width="570" alt="Untitled (8)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/e452edc8-a65f-4fe7-a331-ef414b41d845">

객체는 컬렉션을 사용해서 객체 2개로 다대다 관계 가능.

<img width="596" alt="Untitled (9)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/4691d8f8-db1c-4a6e-b1da-f7052b079766">

@ManyToMany 사용.

@JoinTable로 연결 테이블 지정.

다대다 맵핑 : 단방향, 양방향 가능.

```java
@ManyToMany
@JoinTable(name = "member_product") // member_product라는 테이블을 생성하여 연관관계의 두 테이블의 id를 맵핑
private List<Product> products = new ArrayList<>();

// 아래의 코드를 통해 양방향 맵핑도 가능
@ManyToMany(mappedBy = "products")
private List<Member> members = new ArrayList<>();
```

```sql
Hibernate:   
create table Member (
   id bigint not null auto_increment,
   username varchar(255),
   locker_id bigint,
   team_id bigint,
   primary key (id)
);

Hibernate: 
-- @JoinTable을 통해서 다대다 관계를 풀어내기 위한 테이블이 생성됨
create table member_product (
   Member_id bigint not null,
   products_id bigint not null
);

Hibernate: 
create table Product (
   id bigint not null auto_increment,
   name varchar(255),
   primary key (id)
);
```
</br></br>

**다대다 맵핑의 한계**

편리해 보이지만 실무에서 사용 X.

연결 테이블이 단순히 연결만 하고 끝나지 않음 → @JoinTable로 생성된 테이블에는 딱 맵핑정보만 넣을 수 있음.

주문시간, 수량 같은 데이터가 들어올 수 있음.

<img width="617" alt="Untitled (10)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/3696d959-cdec-47a3-b87c-4a5ecb5ffadd">
</br></br>

**다대다 한계 극복**

연결 테이블용 엔티티 추가(연결 테이블을 엔티티로 승격).

@ManyToMany → @OneToMany, @ManyToOne

<img width="593" alt="Untitled (11)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/e2f0b2af-3845-4d1a-8c18-453592b3fbaf">

```java
// 승격된 연결 테이블
@Entity
public class MemberProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 다대다 -> 일대다, 다대일 관계로 풀어냄
	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	// 추가정보도 연결테이블에 저장 가능
	private int orderAmount;

	private LocalDateTime orderDate;
}
```
