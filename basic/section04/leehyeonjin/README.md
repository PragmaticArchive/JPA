# 엔티티 맵핑

---

### 엔티티 맵핑 소개

객체와 테이블 맵핑 : @Entity, @Table

필드와 컬럼 맵핑 : @Column

기본 키 맵핑 : @Id

연관관계 맵핑 : @ManyToOne, @JoinColumn

---

### 객체와 테이블 맵핑

**@Entity : @Entity가 붙은 클래스는 JPA가 관리하는 엔티티**

JPA를 사용해서 테이블과 맵핑할 클래스는 @Entity를 필수로 붙여주어야 함.

주의사항 :

- 기본 생성자 필수(파라미터가 없는 public or protected 생성자).
    - 이는 JPA의 라이브러리들이 리플렉션 등을 통해 객체를 생성하는 등의 동작을 수행하기 때문.
- final 클래스, enum, interface, inner 클래스 사용불가.
- DB에 저장하고 싶은 필드에는 final을 사용할 수 없음.

name :

- JPA에서 사용할 엔티티의 이름을 지정.
- 기본값 : 클래스 이름을 그대로 사용(Ex. Member).
- 같은 클래스 이름이 없으면 가급적 기본값을 사용할 것.
</br></br>

**@Table : 엔티티와 맵핑할 테이블 지정**

name :

- 맵핑할 테이블 이름 지정.
- 기본값 : 엔티티 이름을 그대로 사용.

catalog : 데이터베이스 catalog 맵핑.

schema : 데이터베이스 schema 맵핑.

uniqueConstraints(DDL) : DDL 생성시 유니크 제약 조건 생성.

> 💡 **데이터베이스 스키마 자동 생성**
>
> - DDL을 애플리케이션 실행 지점에 자동 생성(필요한 테이블을 엔티티 정보에 따라 자동으로 생성해줌).
> - 테이블 중심 → 객체 중심.
> - 데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL 생성.
> - 이렇게 생성된 DDL은 개발 장비에서만 사용.
> - 생성된 DDL은 운영서버에서는 사용하지 않거나 적절히 다듬은 후 사용할 것.
>
> ---
>
> 💡 **데이터베이스 스키마 자동 생성 - 속성**
>
> - create : 기존테이블 삭제 후 다시 생성(DROP + CREATE).
> - create-drop : create와 같으나 종료시점에 테이블 DROP.
> - update : 변경분만 반영(운영 DB에는 사용하면 안됨, 지우는건 안됨).
> - validate : 엔티티와 테이블이 정상 맵핑되었는지 확인.
> - none : 사용하지 않음.
>
> [전체 코드 보기](./jpa-basic/src/main/resources/META-INF/persistence.xml)
> ```xml
> <property name="hibernate.hbm2ddl.auto" value="create"/>
> ```
> 
> ---
>
> - 개발 초기 → create, update.
> - 테스트 → update, validate.
> - 스테이징과 운영 → validate, none.
> - 운영 장비에는 절대 create, create-drop, update를 사용하면 안됨!

> 💡 **DDL 생성 기능**
>
> - DDL 생성 기능은 DDL을 자동 생성할 때만 사용되고, JPA 실행로직에는 영향을 주지 않음.
>
> [전체 코드 보기](./jpa-basic/src/main/java/hellojpa/Member.java)
> ```java
> // 제약조건 추가 : 회원 이름을 필수, 10자 초과 X.
> @Column(nullable = false, length = 10)
> // 유니크 제약조건 추가 : NAME, AGE 필드에 대해 유니크 제약조건.
> @Table(uniqueConstraints = {
>	@UniqueConstraint(name = "NAME_AGE_UNIQUE", columnNames = {"NAME", "AGE"})
> })
> ```

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/Member.java)
```java
// 1. 엔티티-테이블 맵핑 어노테이션(엔티티 관련 설정)
@Entity(name = "member") // 1-4. JPA가 내부적으로 관리하는 엔티티 이름을 속성으로 지정(다른 패키지에 중복되는 이름이 있을 경우에만 사용할 것)
// 2. 엔티티-테이블 맵핑 어노테이션(테이블 관련 설정)
@Table(
	name = "MBR", // 2-1. 해당 엔티티와 맵핑해줄 테이블의 이름 지정
	uniqueConstraints = @UniqueConstraint(name = "NAME_AGE_UNIQUE", columnNames = {"name", "age"}) // 2-2. 유니크 제약조건 추가
)
// 1-2. 엔티티가 붙은 클래스는 final 클래스, enum 클래스, interface, inner 클래스가 아니어야 함
public class Member {

	@Id
	private Long id;
	// 1-3. DB에 저장하고자하는 필드에는 final 사용불가
	private String name;

	// 1-1. 엔티티가 붙은 클래스에 반드시 기본 생성자 존재해야함(현재는 아무런 생성자도 생성하지 않았으므로 기본값이 기본 생성자)

	// getter...setter...
}
```

---

### 필드와 컬럼 맵핑

**@Column : 컬럼 맵핑**

name : 필드와 맵핑할 테이블의 컬럼 이름(기본값 : 객체의 필드 이름).

insertable, updatable : 등록, 변경 가능 여부(기본값 : TRUE).

nullable(DDL) : null값의 허용 여부를 설정. false로 설정하면 DDL 생성시 not null 제약조건이 추가됨.

unique(DDL) : @Table의 uniqueConstraints와 같지만 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용.

columnDefinition(DDL) : 데이터베이스 컬럼 정보를 직접 줄 수 있음(Ex. varchar(100) default ‘EMPTY’.

length(DDL) : 문자 길이 제약조건, String 타입에만 사용(기본값 : 255).

precision, scale(DDL) :

- BigDecimal 타입에서 사용(BigInteger도 사용가능).
- precision은 소수점을 포함한 전체 자릿수를, scale은 소수의 자릿수.
- 참고로 double, float 타입에는 적용되지 않음.
- 아주 큰 숫자나 정밀한 소수를 다루어야 할 때만 사용됨.
- 기본값 : precision = 19, scale = 2.

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/Member.java)
```java
// DB의 name이라는 컬럼은 생성은 가능하지만(insertable의 기본값이 true이기 때문), 수정은 불가능
@Column(name = "name", updatable = false)
private String username;

// DDL 생성시 DB의 name이라는 컬럼의 제약조건으로 not null이 추가됨
@Column(name = "name", nullable = false)
private String username;

// DDL 생성시 name이라는 컬럼에 유니크 제약조건이 추가됨(잘 사용하지 X)
// 제약조건의 이름을 랜덤으로 생성해버리기 때문 => @Table에서 유니크 제약조건 거는 것 권장
@Column(name = "name", unique = true)
private String username;

// DDL 생성시 컬럼에 대한 정의를 직접 SQL문을 통해 하고싶은 경우
@Column(name = "name", columnDefinition = "varchar(100) default 'EMPTY")
private String username;

// 아주 큰 숫자의 소수점 등을 사용할 떄 사용
@Column(precision = 19, scale = 4)
private BigDecimal num;
```
</br></br>

**@Temporal : 날짜 타입(java.util.Date, java.util.Calendar) 맵핑**

LocalDate, LocalDateTime을 사용할 때는 생략 가능함 → Java 8 이후로는 사실상 사용 X.

Temporal.DATE : date 타입과 맵핑.

Temporal.TIME : time 타입과 맵핑.

Temporal.TIMESTAMP : timestamp(날짜와 시간) 타입과 맵핑.

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/Member.java)
```java
// 날짜를 표현하는 맵핑 정보(DATE, TIME, TIMESTAMP)
@Temporal(TemporalType.TIMESTAMP)
private Date createdDate;

// Java 8 이후로 LocalDate, LocalDateTime 타입이 추가되면서 필요없어짐
// @Temporal(TemporalType.TIMESTAMP)
private LocalDate lastModifiedDate;
```
</br></br>

**@Enumerated : enum 타입 맵핑**

EnumType.ORDINAL : enum 순서를 DB에 저장.

EnumType.STRING : enum 이름을 DB에 저장.

> **EnumType.ORDINAL은 사용하지 말 것!**</br>
DB의 데이터가 갱신되면서 순서가 밀릴 수도 있기 때문.
>

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/Member.java)
```java
@Enumerated(EnumType.STRING) // Java에는 Enum 타입이 있으나, DB에는 없기 때문에 Enumerated 어노테이션을 활용해서 맵핑
private RoleType roleType;
```
</br></br>

**@Lob : varchar 타입의 크기를 넘어가는 데이터 맵핑**

맵핑하는 필드 타입이 문자면 CLOB 맵핑, 나머지는 BLOB 맵핑.

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/Member.java)
```java
@Lob // varchar 크기를 넘어서는 데이터 저장에 필요
private String description;
```
</br></br>

**@Transient : 특정 필드를 컬럼에 맵핑하지 않음(맵핑 무시)**

필드에 맵핑X, DB에 저장X, 조회X.

주로 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용.

[전체 코드 보기](./jpa-basic/src/main/java/hellojpa/Member.java)
```java
@Transient
private int temp;
```

---

### 기본 키 맵핑

**기본 키 맵핑 어노테이션**

@Id, @GeneratedValue
</br></br>

**기본 키 맵핑 방법**

직접 할당 : @Id만 사용(@GeneratedValue 사용 X).

자동 생성(@GeneratedValue) :

1. GenerationType.IDENTITY : 데이터베이스에 위임, MYSQL.
- 기본 키 생성을 데이터베이스에 위임.
- 주로 MySQL(AUTO_INCREMENT), PostgreSQL, SQL Server, DB2에서 사용.
- AUTO_INCREMENT는 데이터베이스에 Insert SQL을 실행한 이후에야 ID값을 알 수 있음.
- IDENTITY 전략은 em.persist() 시점에 즉시 Insert SQL을 실행하고, DB에서 식별자를 조회.

2. GenerationType.SEQUENCE : 데이터베이스 시퀀스 오브젝트 사용, ORACLE.
- 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트(Ex. 오라클 시퀀스).
- Oracle, PostgreSQL, DB2, H2 데이터베이스에서 사용.
- 기본적으로 Sequence 객체는 hibernate가 제공하지만, 엔티티마다의 시퀀스 객체를 따로 지정해주고 싶으면 @SequenceGenerator를 통해 지정해주면 됨.
- @SequenceGenerator 필요.
    - name : 식별자 생성기 이름(필수).
    - sequenceName : 데이터베이스에 등록된 시퀀스 이름(기본값 : hibernate_sequence).
    - initialValue : DDL 생성시에만 사용됨, 시퀀스 DDL을 생성할 때 처음 1 시작하는 수를 지정(기본값 : 1).
    - allocationSize : 시퀀스 한번 호출에 증가하는 수(성능 최적화에 사용됨), 데이터베이스 시퀀스 값이 하나씩 증가하도록 설정되어 있다면 이 값을 반드시 1로 설정해야 함(기본값 : 50).
    - catalog, schema : 데이터베이스 catalog, schema 이름).

> 💡 **Sequence 객체를 활용한 성능 최적화**
> 
> Sequence 혹은 Table을 통해서 지정가능한 allocationSize 속성.</br>
> 동시성 문제 , 성능 문제 해결.
>
> ---
>
> 💡 **문제상황**
>
> - SEQUENCE 전략을 통해 PK 값을 자동할당하는 경우, 매번 Insert 쿼리를 날리기 전에 DB로부터 이전 PK 값을 조회해와야 함.
> - 그렇다면 SEQUENCE 전략 사용시, 엔티티의 PK를 지정해주기 위해 계속 조회쿼리를 날려야함 ⇒ 성능 걱정!
>
> ---
>
> 💡 **해결방안**
>
> - 이를 해결하기 위해 @SequenceGenerator를 통해 메모리에 저장할 시퀀스 객체 생성시에 한꺼번에 조회가능한 엔티티의 개수를 늘려줌.
> - 이렇게 하면 매번 DB를 조회하는것이 아니라, 지정해둔 크기만큼은 메모리에 미리 올려두고, 사용 ⇒ 성능 개선!

3. GenerationType.TABLE : 키 생성용 테이블 사용, 모든 DB에서 사용.
- 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략.
- 장점 : 모든 데이터베이스에 적용 가능.
- 단점 : 성능(최적화가 안되어 있음).
- @TableGenerator 필요(시퀀스와 유사).

4. GenerationType.AUTO : 방언에 따라 자동 지정, 기본값.

[엔티티 전체 코드 보기](./jpa-basic/src/main/java/hellojpa/Member.java)</br>
[main() 전체 코드 보기](./jpa-basic/src/main/java/hellojpa/JpaMain.java)
```java
// GeneratedValue X
// ID(PK) 값 직접 할당 : PK에는 유니크 제약조건이 자동으로 걸리는데, 이러한 방법은 제약조건 확인까지 해줘야함 => RDB 사용시 그냥 GeneratedValue 쓸 것

@Id // GeneratedValue 없이 Id 어노테이션만 지정 -> 해당 필드가 PK임만을 명시하고, 그 값은 직접 할당
private Long id;

Member member1 = new Member();
member1.setId(1L);

manager.persist(member1);

transaction.commit();

// IDENTITY
// ID(PK)의 값 할당을 데이터베이스에 위임
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY) // Id가 MySQL 기준 AUTO_INCREMENT 방식으로 할당됨
private Long id;

Member member2 = new Member();

manager.persist(member2);

transaction.commit();

// SEQUENCE
// ID(PK)의 값 할당을 시퀀스 객체를 생성하고 해당 객체에서 가져와서 수행

// SEQUENCE 전략으로 PK값을 할당하는 경우, 기본적으로 Sequence 객체는 hibernate가 제공
// 그러나 만약 테이블별로 다른 sequence 객체를 사용하고 싶다면 GenerateSequence 어노테이션을 통해 테이블마다 객체를 생성해서 맵핑해줄 것
@SequenceGenerator(
	name = "MEMBER_SEQ_GENERATOR",
	sequenceName = "MEMBER_SEQ", // 맵핑할 DB의 시퀀스 이름
	initialValue = 1, allocationSize = 1
)
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	...
}

Member member2 = new Member();

manager.persist(member2);

transaction.commit();

// TABLE 전략으로 PK값을 할당하는 경우 생성해야하는 테이블 생성
@TableGenerator(
	name = "MEMBER_SEQ_GENERATOR",
	table = "MY_SEQUENCES",
	pkColumnValue = "MEMBER_SEQ", allocationSize = 1
)
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE) // Id가 Table 테이블을 직접 생성하여 해당 테이블에서 값을 가져와서 할당
	private Long id;

	...
}

Member member3 = new Member();

manager.persist(member3);

transaction.commit();

// AUTO
// ID(PK)의 값 할당이 DB 방언에 따라 지정됨
@Id
@GeneratedValue(strategy = GenerationType.AUTO) // Id가 DB 방언에 맞춰서 자동 할당
private Long id;

Member member4 = new Member();

manager.persist(member4);

transaction.commit();
```

> 💡 **GenerationType.AUTO vs GenerationType.IDENTITY**
>
> Insert SQL문을 날리는 시점?
>
> AUTO : 트랜잭션 커밋 시점(tx.commit()).
>
> IDENTITY : 엔티티 영속화 시점(em.persist(entity)).
>
> ---
>
> 1. AUTO :
> - 사용하는 DB가 Sequence 혹은 Table 전략을 따르는 경우, 특정 객체(시퀀스 객체 혹은 키 테이블)에 종속적이게 된다.
> - 시퀀스 혹은 테이블 방식의 경우, 특정 객체에 종속적이기 때문에 해당 객체에서 id값을 조회해오면된다.
> - 이 말은 엔티티가 영속화(영속성 컨텍스트 내에 엔티티 & PK(시퀀스, 테이블) 존재)된 경우라면 id값을 기준으로 엔티티를 조회할 수 있는 상태가 되었음을 의미한다 ⇒ DB로부터 id값을 받아오지 않아도 됨.
> - 따라서 동일한 트랜잭션 내에서 엔티티 등록과 조회 로직이 동시에 존재하더라도, 조회시점에 해당 엔티티가 영속화되어 있기만 하면 DB에 등록되어있지 않아도 된다.
>
> 2. IDENTITY :
> - 그러나 IDENTITY의 경우, MySQL의 AUTO_INCREMENT 속성을 따르는 전략이다.
> - 따라서 엔티티 생성 시점에는 따로 PK가 지정되어 있지 않고, DB에 저장되어야만 PK가 할당된다.
> - 따라서 동일한 트랜잭션 내에서 엔티티 등록과 조회 로직이 동시에 존재한다면, 조회시점에 해당 엔티티가 DB에 등록되어 있어야 한다.

</br></br>

**권장하는 식별자 전략**

- 기본키 제약 조건 : null 아님, 유일, 변하면 안됨.
- 미래까지 이 조건을 만족하는 자연키는 찾기 어려움 → 대체키를 사용할 것.
    - Ex. 주민 등록 번호도 기본 키로 적절하지 않음.
- 권장 : Long형 + 대체키 + 키 생성 전략 사용할 것.

---

### **예제 코드**

1. 회원은 일반 회원과 관리자로 구분해야 한다.
2. 회원 가입일과 수정일이 있어야 한다.
3. 회원을 설명할 수 있는 필드가 있어야 한다. 이 필드는 길이 제한이 없다.

```java
@Entity
public class Member {

	@Id
	private Long id;

	@Column(name = "name")
	private String username;

	private Integer age;

	@Enumerated(EnumType.STRING) // Java에는 Enum 타입이 있으나, DB에는 없기 때문에 Enumerated 어노테이션을 활용해서 맵핑
	private RoleType roleType;
	
	private LocalDate createdDate;

	private LocalDate lastModifiedDate;

	@Lob // varchar 크기를 넘어서는 데이터 저장에 필요
	private String description;

	@Transient
	private int temp;
}
```

```sql
-- 자동생성된 SQL DDL문
create table Member (
     id bigint not null,
     age integer,
     createdDate date, // Java의 LocalDate 타입과 맵핑
     description longtext, // CLOB과 맵핑
     lastModifiedDate date,
     roleType varchar(255), // enum 타입을 String 타입으로 맵핑(이름 기준이므로)
     name varchar(255), // 따로 length 속성을 지정해주지 않아서 기본값인 255크기로 맵핑
     primary key (id)
);
```
