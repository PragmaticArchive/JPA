# 값 타입

---

### 기본값 타입

**JPA의 데이터 타입 분류**

1. 엔티티 타입
- @Entity로 정의하는 객체.
- 데이터가 변해도 식별자로 지속해서 추적 가능.
- 식별자 O.
- 생명 주기 관리.
- 공유.
- Ex. 회원 엔티티의 키나 나이값을 변경해도 식별자로 인식 가능.

1. 값 타입
- int, Integer, String 처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체.
- 식별자가 없고 값만 있으므로 변경시 추적 불가.
- 식별자 X.
- 생명 주기를 엔티티에 의존.
- 공유하지 않는 것이 안전(복사해서 사용할 것) → 불변객체로 만드는 것이 안전.
- Ex. 숫자 100을 200으로 변경하면 완전히 다른 값으로 대체.
</br></br>

**값 타입 분류**

1. 기본 값 타입
- 자바 기본 타입(int, double).
- 래퍼 클래스(Integer, Long).
- String.
- 생명주기를 엔티티에 의존 → 회원을 삭제하면 이름, 나이 필드도 함께 삭제됨.
- 값 타입은 공유하면 안됨 → 회원 이름 변경시 다른 회원의 이름도 함께 변경되면 안됨.

> 💡 **참고 : 자바의 기본 타입은 절대 공유 안됨**
>
> 기본 타입은 항상 값을 복사한다.</br>
> Integer같은 래퍼 클래스나 String 같은 특수한 클래스는 공유 가능한 객체이지만 변경할 수 없다(불변).
>
> ```java
> int a = 10;
> int b = a;
>
> b = 20;
>
> // 위에서 두 객체를 = 연산자를 통해 a를 b에 대입했지만,
> // 실제로는 값 자체를 공유하는 것이 아니라 a의 값을 복사해서 b에 넣어줌
> // 따라서 b의 값을 변경해도 a의 값은 그대로
> System.out.println("a = " + a); //10
> System.out.println("b = " + b); //20
> ```

1. 임베디드 타입(embedded type, 복합 값 타입).
2. 컬렉션 값 타입(collection value type).

---

### 임베디드 타입

새로운 값을 직접 정의할 수 있음.

주로 기본 값 타입을 모아서 만들어서 복합 값 타입이라고도 함.

<img width="250" alt="Untitled" src="https://github.com/hgene0929/JPA/assets/90823532/cc4d8f23-aec8-4801-85b9-855b90e410a7">

<img width="220" alt="Untitled (1)" src="https://github.com/hgene0929/JPA/assets/90823532/caf6631e-d79c-4d5f-b3fd-372f109c2bfb">

<img width="475" alt="Untitled (2)" src="https://github.com/hgene0929/JPA/assets/90823532/41a98239-7f73-484a-9a77-205735a44cee">
</br></br>

**임베디드 타입 사용법**

@Embeddable : 값타입을 정의하는 곳에 표시.

@Embedded : 값타입을 사용하는 곳에 표시.

기본 생성자 필수.

```java
@Embeddable // 임베디드 타입을 정의하는 곳에 어노테이션 추가
public class Period {

	private LocalDateTime startDate;

	private LocalDateTime endDate;

	// getter/setter	

	// 임베디드 타입을 활용하는 의미있는 메소드
	public boolean isWork(LocalDateTime now) {
		return now.isAfter(startDate) && now.isBefore(endDate);
	}
}

@Embeddable // 임베디드 타입을 정의하는 곳에 어노테이션 추가
public class Address {

	private String city;

	private String street;

	private String zipcode;

	// getter/setter
}

@Entity
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	private String username;

	// 임베디드 타입을 사용하는 곳에 어노테이션
	@Embedded
	private Period workPeriod;

	@Embedded
	private Address homeAddress;
}
```
</br></br>

**임베디드 타입 장점**

재사용.

높은 응집도.

Period.work() 처럼 해당 값 타입만 사용하는 의미있는 메소드를 만들 수 있음.

임베디드 타입을 포함한 모든 값 타입은 값 타입을 소유한 엔티티에 생명주기를 의존함.
</br></br>

**임베디드 타입과 테이블 맵핑**

임베디드 타입은 엔티티의 값일 뿐이다.

임베디드 타입을 사용하기 전과 후에 맵핑하는 테이블은 같다.

객체와 테이블을 아주 세밀하게(find-grained) 맵핑하는 것이 가능.

잘 설계한 ORM 애플리케이션은 맵핑한 테이블의 수보다 클래스의 수가 더 많음.

<img width="612" alt="Untitled (3)" src="https://github.com/hgene0929/JPA/assets/90823532/d608d332-e193-43fc-9eb6-3e7c58c9d78e">

**임베디드 타입과 연관관계**

<img width="637" alt="Untitled (4)" src="https://github.com/hgene0929/JPA/assets/90823532/2ec1d9d4-7814-4b84-81bf-b98cc0f28d0c">

```java
@Embeddable // 임베디드 타입을 정의하는 곳에 어노테이션 추가
public class Address {

	private String city;

	private String street;

	private String zipcode;

	// 임베디드 타입 클래스 내부에 엔티티도 필드로 추가가능
	private Member member;

	// getter/setter
}
```
</br></br>

**@AttributeOverride : 속성 재정의**

한 엔티티에서 같은 값 타입을 사용하면?

컬럼명이 중복됨.

@AttributeOverrides, @AttributeOverride를 사용해서 컬럼명 속성을 재정의.

```java
// 아래와 같은 같은 임베디드 타입이 중복되어서 사용될 경우
// 나머지 하나에는 속성 재정의를 해서 사용
@Embedded
private Address homeAddress;

@Embedded
@AttributeOverrides({ // 속성재정의 방법
	@AttributeOverride(name = "city", column = @Column(name = "city")),
	@AttributeOverride(name = "street", column = @Column(name = "street")),
	@AttributeOverride(name = "zipcode", column = @Column(name = "zipcode"))
})
private Address companyAddress;
```
</br>

**임베디드 타입과 null**

임베디드 타입의 값이 null이면 맵핑한 컬럼 값은 모두 null.

```java
// 다음과 같은 경우 Period 내부의 모든 필드도 null
private Period workPeriod = null;
```

---

### 값 타입과 불변 객체

**값 타입 공유 참조**

임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험함.

부작용(side effect) 발생.

```java
// 2개의 엔티티에서 공유할 임베디드 타입 객체
Address address = new Address("city", "street", "zipcode");

Member memberA = new Member();
memberA.setUsername("memberA");
memberA.setHomeAddress(address);
manager.persist(memberA);

Member memberB = new Member();
memberB.setUsername("memberB");
memberB.setHomeAddress(address);
manager.persist(memberB);

// 부작용 : 
// 해당 코드의 결과, 주소 객체를 공유하는 두 엔티티의 값이 모두 변경되어버림
memberA.getHomeAddress().setCity("newCity");
```

<img width="752" alt="Untitled (5)" src="https://github.com/hgene0929/JPA/assets/90823532/cc7964de-f36b-4d9e-acad-e2ecd97ad807">

**값 타입 복사**

값 타입의 실제 인스턴스인 값을 공유하는 것은 위험.

대신 값(인스턴스)를 복사해서 사용.

```java
// 값 타입 객체는 절대 공유 불가
Address address = new Address("city", "street", "zipcode");

Member memberA = new Member();
memberA.setUsername("memberA");
memberA.setHomeAddress(address);
manager.persist(memberA);

// 따라서 같은 값을 사용하고 싶다면, 복사할 것!
Address newAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());

Member memberB = new Member();
memberB.setUsername("memberB");
memberB.setHomeAddress(newAddress);
manager.persist(memberB);

// 부작용 해소
memberA.getHomeAddress().setCity("newCity");
```

<img width="755" alt="Untitled (6)" src="https://github.com/hgene0929/JPA/assets/90823532/66d5cf75-f1d9-4c6d-ab4e-565d643f2da5">
</br></br>

**객체 타입의 한계**

- 항상 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다.
- 문제는 임베디드 타입처럼 직접 정의한 값 타입은 자바의 기본 타입이 아니라 객체 타입이다.
- 자바 기본 타입에 값을 대입하면 값을 복사한다.
- 객체 타입은 참조값을 직접 대입하는 것을 막을 방법이 없다.
- 객체의 공유 참조는 피할 수 없다.

```java
// 기본 타입
int a = 10;
int b = a; // 기본 타입은 값을 복사 b = 4;

// 객체 타입
Address a = new Address(“Old”);
Address b = a; // 객체 타입은 참조를 전달 b. setCity(“New”)
```
</br>

**불변 객체**

- 객체 타입을 수정할 수 없게 만들면 부작용을 원천 차단.
- 값 타입은 불변 객체(immutable object)로 설계해야 함.
- 불변 객체 : 생성 시점 이후 절대 값을 변경할 수 없는 객체.
- 생성자로만 값을 설정하고 수정자(setter)를 만들지 않으면 됨.

---

### 값 타입의 비교

값 타입 : 인스턴스가 달라도 그 안에 값이 같으면 같은 것으로 봐야 함.

- 동일성(identity) 비교 : 인스턴스의 참조값을 비교, == 사용.
- 동등성(equivalence) 비교 : 인스턴스의 값을 비교, equlas() 사용.

값 타입은 a.equlas(b) 를 사용해서 동등성 비교를 해야함.

값 타입의 equals() 메소드를 적절하게 재정의(주로 모든 필드 사용).

### 값 타입 컬렉션

<img width="528" alt="Untitled (7)" src="https://github.com/hgene0929/JPA/assets/90823532/240f8c5a-565c-46ce-bd51-607540e536dc">

값 타입을 하나 이상 저장할 때 사용.

@ElementCollection, @CollectionTable 사용.

데이터베이스는 컬렉션을 같은 테이블에 저장할 수 없다.

컬렉션을 저장하기 위한 별도의 테이블이 필요함.

```java
@ElementCollection
@CollectionTable(
	name = "favorite_food",
	// 아래의 컬럼을 외래키로 지정
	joinColumns = @JoinColumn(name = "member_id")
)
@Column(name = "food_name")
private Set<String> favoriteFoods = new HashSet<>();

@ElementCollection
@CollectionTable(
	name = "address_histories",
	joinColumns = @JoinColumn(name = "member_id")
)
private List<Address> addressHistories = new ArrayList<>();

// 사용
Member member = new Member();
member.setUsername("member");
member.setHomeAddress(new Address("city", "street", "zipcode"));

member.getFavoriteFoods().add("치킨");
member.getFavoriteFoods().add("족발");
member.getFavoriteFoods().add("피자");

member.getAddressHistories().add(new Address("old1", "old1", "old1"));
member.getAddressHistories().add(new Address("old2", "old2", "old2"));

// Member 엔티티 한개만을 영속화하는 것만으로 내부의 컬렉션 테이블의 데이터가 영속화되어 반영됨
manager.persist(member);

manager.flush();
manager.clear();

// 엔티티 내부의 컬렉션 객체는 지연로딩 방식으로 조회됨
Member findMember = manager.find(Member.class, member.getId());

// 값 타입을 불변객체 방식으로 안전하게 업데이트
Address oldAddress = findMember.getHomeAddress();
findMember.setHomeAddress(new Address("newCity", oldAddress.getStreet(), oldAddress.getZipcode()));

// String 타입의 컬렉션은 값을 아예 지우고 새롭게 추가하는 것이 업데이트 개념
// 컬렉션의 값만 변경되어도 엔티티의 실제 데이터까지 변경됨
findMember.getFavoriteFoods().remove("치킨");
findMember.getFavoriteFoods().add("한식");

// 컬렉션 클래스의 remove() 메소드는 기본적으로 동등(equals)비교를 기준으로 값을 찾아서 삭제시킴
// 따라서 사용하고자 하는 Address 클래스의 equals() 메소드를 제대로 재정의해주어야 함
findMember.getAddressHistories().remove(new Address("old1", "old1", "old1"));
```

```java
// equals() 재정의
// 이때 내부의 값은 getter()를 통해서 호출되어야 프록시 객체 사용시에도 값이 호출된다
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Address address = (Address)obj;
    return Objects.equals(getCity(), address.getCity()) &&
    Objects.equals(getStreet(), address.getStreet()) &&
    Objects.equals(getZipcode(), address.getZipcode());
}

@Override
public int hashCode() {
    return Objects.hash(getCity(), getStreet(), getZipcode());
}
```
</br></br>
**값 타입 컬렉션 사용**

값 타입 저장, 조회(지연 로딩 전략 사용할 것), 수정.

> 참고 : 값 타입 컬렉션은 영속성 전이(cascade) + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있다.

</br>

**값 타입 컬렉션의 제약사항**

값 타입은 엔티티와 다르게 식별자 개념이 없음.

값은 변경하면 추적이 어려움.

값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장.

값 타입 컬렉션을 맵핑하는 테이블은 모든 컬럼을 묶어서 기본키를 구성해야 함 → null X, 중복 저장 X.
</br></br>

**값 타입 컬렉션 대안**

실무에서는 상황에 따라 값 타입 컬렉션 대신에 일대다 관계를 고려.

일대다 관계를 위한 엔티티를 만들고, 여기에서 값 타입 사용.

영속성 전이(cascade) + 고아 객체 제거를 사용해서 값 타입 컬렉션 처럼 사용.

Ex. AddressEntity.

```java
@Entity
public class AddressEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 값 타입을 해당 값 타입의 엔티티로 래핑
	private Address address;
}

// 일대다 단방향 맵핑으로 연관관계를 준다음,
// cascade, orphanRemoval 기능을 모두 주어 생명주기 관리
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "member_id")
private List<AddressEntity> addressHistories = new ArrayList<>();
```
