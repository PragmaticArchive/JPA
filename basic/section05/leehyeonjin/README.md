# 연관관계 맵핑 기초

---

### 단방향 연관관계

**목표**

1. 객체와 테이블 연관관계의 차이를 이해.
2. 객체의 참조와 테이블의 외래키를 맵핑.

**용어 이해**

- 방향(Direction) : 단방향, 양방향.
- 다중성(Multiplicity) : 다대일(N:1), 일대다(1:N), 일대일(1:1), 다대다(N:M).
- 연관관계의 주인 : 객체 양방향 연관관계는 관리 주인이 필요함.

**예제 시나리오**

- 회원과 팀이 있다.
- 회원은 하나의 팀에만 소속될 수 있다.
- 회원과 팀은 다대일 관계이다.
    - 하나의 팀은 여러명의 회원을 가질 수 있기 때문 ⇒ 회원 : 팀 = 다 : 1.
    - 따라서 “다” 쪽인 회원 엔티티에서 팀의 PK를 FK로 받아서 관리해야 함 → 테이블 개념.
    - 그리고 “다” 쪽인 회원 엔티티에서 팀의 엔티티 객체를 필드로 가지고 참조가능 → 객체 개념.
</br></br>

**객체 지향 모델링(ORM 맵핑)**

<img width="681" alt="Untitled" src="https://github.com/hgene0929/JPA/assets/90823532/6aa7c970-c540-4c92-932e-88ede4124261">

```java
// 아래 주석코드와 같은 방식은 연관관계를 테이블 중점적으로 작성한 코드
// @Column(name = "team_id")
// private Long teamId;

// 아래 코드는 객체 지향적으로 연관관계를 작성한 코드
@ManyToOne
@JoinColumn(name = "team_id")
private Team team;
```

[테이블 중심으로 작성된 코드의 단점 참고자료 바로가기](../../section04/leehyeonjin/PRACTICE.md)

1. 객체 지향 모델링 엔티티 연관관계 생성(엔티티 등록).

```java
// 팀 저장
Team team = new Team();
team.setName("TeamA");
manager.persist(team);

// 회원 저장
Member member1 = new Member();
member1.setUsername("member1");
member1.setTeam(team);
manager.persist(member1);
```

2. 객체 지향 모델링 엔티티 연관관계 조회(객체 그래프 탐색).

```java
// 조회
Member findMember = manager.find(Member.class, member1.getId());

// 참조를 사용해서 연관관계 조회 가능
Team findTeam = findMember.getTeam();
```

3. 객체 지향 모델링 연관관계 수정

```java
// 새로운 팀B
Team teamB = new Team();
teamB.setName("TeamB");
manager.persist(teamB);

// 회원1에 새로운 팀B 설정
member1.setTeam(teamB);
```

---

### 양방향 연관관계와 연관관계의 주인

<img width="660" alt="Untitled (1)" src="https://github.com/hgene0929/JPA/assets/90823532/ffd74401-2022-4bdc-b79e-562bb6519220">

**양방향 맵핑 정의**

연관관계를 맺고 있는 엔티티 간에 서로를 참조할 수 있도록 코드 작성.

단방향 맵핑만으로도 이미 연관관계 맵핑은 완료됨 → 양방향 맵핑은 반대 방향으로 조회(객체 그래프 탐색) 기능이 추가된 것 뿐임.

JPQL에서는 역방향으로 탐색하는 경우가 많음.

단방향 맵핑을 우선 해주고, 필요하다면 이후에 양방향 맵핑을 추가해도 무관(테이블에는 영향 X).

1. 다대일 관계에서 “다”에 해당하는 Member 엔티티에는 기존의 단방향 맵핑과 동일하게 설계 가능.

2. 다대일 관계에서 “일”에 해당하는 Team 엔티티에는 자신이 가진 Member 엔티티들을 컬렉션 타입으로 필드에 저장가능.

```java
// 양방향 맵핑 관계 설정(mappedBy를 통해 연관관계의 주인 설정)
@OneToMany(mappedBy = "team")
List<Member> members = new ArrayList<>();
```

3. 양방향 맵핑시, 반대 방향으로 객체 그래프 탐색 가능.

```java
// 양방향 연관관계 맵핑시 반대 방향으로 객체 그래프 탐색하기
Team findTeam = manager.find(Team.class, team.getId());
// 역방향 조회
int memberSize = findTeam.getMembers().size();
```
</br></br>
**연관관계 주인과 mappedBy**

객체와 테이블이 관계를 맺는 차이

- 객체 연관관계 = 2개.
    - 회원 → 팀 연관관계 1개(단방향).
    - 팀 → 회원 연관관계 1개(단방향).
- 테이블 연관관계 = 1개.
    - 회원 ↔ 팀의 연관관계 1개(양방향).

> 객체의 양방향 관계는 사실 양방향 관계가 아니라, 서로 다른 단방향 관계 2개이다.
객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.

</br>

테이블의 양방향 연관관계

- 테이블은 외래키 하나로 두 테이블의 연관관계를 관리.
- member.teamId 외래키 하나로 양방향 연관관계를 가짐(양쪽으로 JOIN 가능).

```sql
select *
from member m
    join team t on m.team_id = t.id;

select *
from team t
    join member m on t.team_id = m.id;
```
</br>

연관관계의 주인(양방향 맵핑 규칙)

1. 객체의 두 관계중 하나를 연관관계의 주인으로 지정.
2. 연관관계의 주인만이 외래키를 관리(등록, 수정).
3. 주인이 아닌쪽은 읽기만 가능.
4. 주인은 mappedBy 속성 사용 불가.
5. 주인이 아니면 mappedBy 속성으로 자신의 주인 지정.

> 💡 **그렇다면 둘 중 누구를 주인으로?**
>
> - 외래키가 있는 곳을 주인으로 정해라.
> - 위의 예제코드에서는 Member.team이 연관관계의 주인.

</br>

**양방향 연관관계 맵핑 코드 작성시 주의사항**

순수 객체 상태를 고려해서 항상 양쪽에 값을 설정해야 한다.

- 연관관계의 주인에 값을 입력하지 않아서 오류 발생!

```java
// 양방향 맵핑시 가장 많이 하는 실수 : 연관관계의 주인에 값을 입력하지 않음
Team team1 = new Team();
team1.setName("Team1");
manager.persist(team1);

Member member = new Member();
member.setUsername("member");

// 역방향(주인이 아닌 방향)만 연관관계 설정
// 해당 코드를 작성하지 않으면 이 연관관계를 가진 members 컬렉션을 조회해오는 코드를 작성했을 때 문제 발생
// -> flush(), clear()를 입력하여 DB에 반영하지 않고 그저 1차 캐시에만 있는 상태라면 컬렉션은 조회 불가
// 또한 테스트 코드 작성시에는 DB에 반영되지 않아도 조회가능하도록 해야 원활
team1.getMembers().add(member);

// 아래 주석(올바른) 코드를 반드시 추가하여 양방향 관계의 엔티티 모두에 연관관계를 설정해주어야 한다
// 해당 코드를 작성하지 않으면 member.teamId == null 이된다 => FK가 없음
// member.setTeam(team1);

manager.persist(member);
```
</br>

연관관계 편의 메소드를 생성해야 한다.

- 위의 두 엔티티에 모두 데이터를 함께 넣어주는 조건을 반드시 지키기 위해(잊지 않기 위해!) 연관관계 편의 메소드를 생성.

```java
public void setTeam(Team team) {
	this.team = team;
	// 주인의 엔티티에 연관관계가 설정될 때 반드시(자동으로) 반대편 엔티티에도 연관관계가 설정되도록 편의메소드 활용
	team.getMembers().add(this);
}

// team -> member 연관관계 설정
// team1.getMembers().add(member);

// member -> team 연관관계 설정
// 해당 코드 실행시 반대편의 연관관계도 자동설정됨
member.setTeam(team1);
```
</br>

양방향 맵핑시, 무한루프를 조심해야 한다.

- Ex. toString(), lombok, JSON  생성 라이브러리.
- 양쪽으로 서로 생성 메서드를 동시에 막 호출하면 계속해서 호출이 반복되고, 무한루프에 빠져버림.


> 1.  따라서 lombok 으로 toString() 생성하지마라!
> 2. 컨트롤러에서 엔티티로 절대 반환하지마라(DTO 써라)!
