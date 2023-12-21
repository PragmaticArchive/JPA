# 엔티티 매핑

- 객체와 테이블 매핑 : `@Entity`, `@Table`
- 필드와 컬럼 매핑 : `@Column`
- 기본키 매핑 : `@Id`
- 연관관계 매핑 : `@ManyToOne`, `@JoinColumn`

# 객체와 테이블 매핑

### @Entity

- DB 테이블과 매핑하기 위한 클래스(주로 DTO)

>**!주의**<br/>
> 기본 생성자 필수(리플렉션 등에 사용됨)<br/>
> final, enum, interface, inner 클래스 사용X<br/>
> 저장할 필드에 final X

- 속성
  - name
    - 기본값은 클래스이름

### @Table

- DB 테이블과 매핑할 클래스, sql문에 적는 테이블명
- 속성
  - name
    - 매핑할 테이블 이름
    - 기본값은 엔티티 이름 사용(클래스 이름)
  - catalog
  - schema
  - uniqueConstraints(DDL)
    - DDL 생성시 유니크 제약 조건 생성
    - 필드 각각에 유니크 조건을 거는 것보다는 **테이블 속성에서 거는것을 권장**

#### 필드에 유니크 조건을 걸었을 경우, 제약조건 랜덤이름 생성

```java
@Colunm(unique=true)
private String name;

// add constraint UK_asdasdwqijaskjdiasd unique name
```
#### 테이블 속성에 유니크 조건을 걸었을 경우, 제약조건 이름 설정가능

```java
@Table(uniqueConstraints = {@UniqueConstraint(name = "NAME_AGE_UNIQUE", 
                            columnNames = {"NAME", "AGE"} )})
// add constraint NAME_AGE_UNIQUE unique name
```

# DB 스키마 자동 생성

```java
<property name="hibernate.hbm2ddl.auto" value="create" />
```

- persistance.xml / application.properties에 설정
- 어플리케이션 실행 시점에 DDL 자동 실행
- 데이터베이스 방언을 통해 DB에 적절한 DDL설정 가능
- JPA의 실행 로직에는 영향X, DDL생성에만 사용
- value
  - **create** : 기존 테이블 삭제 후 다시 생성
  - **create-drop** : create + 종료시점에 테이블 drop
  - **update** : 변경된 필드가 있을 경우 create
  - **validate** : 엔티티와 테이블이 정상 매핑되었는지 확인, 매핑이 안될경우 에러
  - **none** : 사용하지 않음

# 필드와 컬럼 매핑

```java
@Entity
public class Member {
  @Id
  private Long id;
  
  @Column(name = "name")
  private String username;
  
  private Integer age;
  
  @Enumerated(EnumType.STRING)
  private RoleType roleType;
  
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;
  
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModifiedDate;
  
  @Lob
  private String description;
  
  @Transient
  private int tmp;
}
```

### 매핑 어노테이션

#### @Column

- 컬럼 매핑
- nullable
- unique
- columnDefinition
  - 필드 조건을 직접 작성가능
  - ex) varchar(100) default ‘EMPTY'
- length
- precision, scale
  - BigDecimal 타입에서 사용
  - precision : 소수점을 포함한 전체 자릿수
  - scale : 소수의 자릿수 조정

#### @Temporal

- 날짜 타입(Date, Time클래스) 매핑
- LocalDate, LocalDateTime의 경우 생략가능

#### @Enumerated

- enum 타입 매핑
- EnumType.ODINAL : enum 순서를 데이터베이스에 저장
  - 필드가 추가, 삭제시 순서가 바뀔 수 있음, DB반영X
- EnumType.String 권장

#### @Lob 

- BLOB, CLOB 매핑
  - BLOB : varchar의 길이를 넘는 문자타입일때
  - BLOB : CLOB에 해당하지 않는 경우
  
#### @Transient

- 특정 필드를 컬럼에 매핑하지 않음(매핑 무시)

# 기본키 매핑

```java
@Id 
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;
```

#### @Id

- 기본키로 직접 할당
- unique, not null

#### @GeneratedValue

- 자동생성
- 방법
  - **IDENTITY**
    - MYSQL에 위임(AUTO_INCREMENT)
  - **SEQUENCE**
    - ORACLE에 위임
    - `@SequenceGenerator`필요
      - **allocationSize** : 미리 가져올 시퀀스 범위사이즈
  - **TABLE**
    - 키 생성용 테이블 사용, 모든 DB사용
    - `@TableGenerator`필요
  - **AUTO**
    - 데이터종류에 따라 자동 지정(identity or sequence), 기본값
    
#### 주의

- **IDENTITY**
> 1. insert문에서 id가 null로 들어감<br/>
> 2. 영속성 컨텍스트는 id값이 있어야 commit하기 때문에 persist()가 실행되는 순간, insert문 실행<br/>
> 3. DB에서 얻어온 id값을 1차캐시에 채워넣음<br/>

- **SEQUENCE**
> 1. persist() 하는 시점에 sequence 전략 확인<br/>
> 2. DB에 call next value for 시퀸스명"을 통해 다음 시퀸스 값을 가져옴<br/>
> 3. 영속성 컨텍스트에 시퀀스값 저장
> 4. 가져온 시퀀스를 다쓰면 다시 2.반복
