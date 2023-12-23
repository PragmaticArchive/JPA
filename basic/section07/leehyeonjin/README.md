# 고급 맵핑(상속관계 맵핑)

---

### 상속관계 맵핑

관계형 데이터베이스는 상속관계가 존재하지 않음.

슈퍼타입 -  서브타입 관계라는 모델링 기법이 객체 상속과 유사함.

상속관계 맵핑 : 객체의 상속과 구조와 DB의 슈퍼타입 서브타입 관계를 맵핑.

<img width="664" alt="Untitled" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/cd9a5f45-ca0e-4972-b65c-d1f885f9e90e">
</br></br>

**상속관계 맵핑이란**

슈퍼타입 - 서브타입 논리 모델을 실제 물리 모델로 구현하는 방법.

1. 각각 테이블로 변환 : 조인 전략.
2. 통합 테이블로 변환 : 단일 테이블 전략.
3. 서브타입 테이블로 변환 : 구현 클래스마다 테이블 전략.
</br></br>

**주요 어노테이션**

@Inheritance(strategy = InheritanceType.XXX)

- JOINED : 조인전략.
- SINGLE_TABLE : 단일 테이블 전략.
- TABLE_PER_CLASS : 구현 클래스마다 테이블 전략.

@DiscriminatorColumn(name = “DTYPE”)

@DiscriminatorValue(”XXX”)
</br></br>

**조인 전략**

<img width="682" alt="Untitled (1)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/92f1eabe-93b2-459e-bbc5-5979dc645305">

장점 :

- 테이블 정규화.
- 외래키 참조 무결성 제약조건 활용가능.
- 저장공간 효율화.

단점 :

- 조회시 조인을 많이 사용 ⇒ 성능 저하.
- 조회 쿼리가 복잡함.
- 데이터 저장시 Insert SQL 2번 호출.

```java
@Entity
// 상속관계 맵핑전략 지정(해당 어노테이션 자체를 생략할 경우 디폴트는 단일 테이블 전략)
@Inheritance(strategy = InheritanceType.JOINED)
// 부모 클래스의 테이블에 자식의 타입을 저장하는 필드 추가(컬럼명 디폴트는 DTYPE)
@DiscriminatorColumn(name = "dtype")
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private int price;
	
	...
}

@Entity
// 부모 테이블의 필드에 DTYPE이 추가될 경우, 해당 컬럼의 값을 지정 가능
@DiscriminatorValue("movie")
public class Movie extends Item {

	private String director;

	private String actor;

	...
}
```
</br></br>

**단일 테이블 전략(JPA 상속관계 맵핑 기본전략)**

<img width="635" alt="Untitled (2)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/c179b75a-8e54-4de7-a7f0-6a4bfaf27935">

장점 :

- 조인이 필요없으므로 일반적으로 조회 성능이 빠름.
- 조회 쿼리가 단순함.

단점 :

- 자식 엔티티가 맵핑한 컬럼은 모두 null 허용.
- 단인 테이블에 모든 것을 저장하므로 테이블이 커질 수 있음 ⇒ 상황에 따라서 조회 성능이 오히려 느려질 수 있음.

```java
// 위의 상속관계 맵핑 코드에서 전략만 수정
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
```
</br></br>

**구현 클래스마다 테이블 전략**

<img width="645" alt="Untitled (3)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/cc54a67f-1e1d-4a30-ba08-76d52957c2e1">

> 이 전략은 데이터베이스 설계자와 ORM 전문가 둘 다 추천하지 않음.

장점 :

- 서브 타입을 명확하게 구분해서 처리할 때 효과적.
- not null 제약조건 사용 가능.

단점 :

- 여러 자식 테이블을 함께 조회할 때 성능이 느림(UNION SQL 필요).
- 자식 테이블을 통합해서 쿼리하기 어려움.

---

### @MappedSuperclass

<img width="575" alt="Untitled (4)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/5e5c16fb-91d5-46e6-8a3e-8681a1628105">

공통 맵핑 정보가 필요할 때 사용(id, name).

상속관계 맵핑 X.

엔티티 X, 테이블과 맵핑 X.

부모 클래스를 상속받는 자식 클래스에 맵핑 정보만 제공.

조회, 검색 불가(em.find(BaseEntity) 불가).

직접 생성해서 사용할 일이 없으므로 추상 클래스 권장.

테이블과 관계 없고, 단순히 엔티티가 공통으로 사용하는 맵핑 정보를 모으는 역할.

주로 등록일, 수정일, 등록자, 수정자 같은 전체 엔티티에서 공통으로 적용하는 정보를 모을 때 사용.

```java
// 공통 속성 정보를 추출하여 클래스 생성
// 상속관계가 아니라 단순히 중복되는 속성을 가짐
@MappedSuperclass
public class BaseEntity {

	private String createdBy;

	private LocalDateTime createdDate;

	private String lastModifiedBy;

	private String lastModifiedDate;

	...
}
```

```java
@Entity
public class Member extends BaseEntity {...}
```

> 참고 : @Entity 클래스는 엔티티나 @MappedSuperclass로 지정한 클래스만 상속 가능.
