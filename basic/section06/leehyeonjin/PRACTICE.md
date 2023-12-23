# 실전예제(다양한 연관관계 맵핑)

---

### 배송, 카테고리 엔티티 추가

- 주문과 배송은 1 : 1(@OneToOne).
- 상품과 카테고리는 N : M(@ManyToMany).

<img width="531" alt="Untitled" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/9392c846-62bb-4c11-a0d0-6de459d2e173">

**배송, 카테고리 추가 ERD**

<img width="610" alt="Untitled (1)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/cfc0e23f-2002-4114-b97d-bd55b7f540cf">

**배송, 카테고리 추가 엔티티 상세**

<img width="540" alt="Untitled (2)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/09c54b1a-ea86-4ea4-acce-c77c7f32792c">

---

### 엔티티 추가 및 변경

[**배송 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Delivery.java)

[**주문 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Order.java)

[**상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Item.java)

[**카테고리 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Category.java)

---

### N : M 관계는 1:N, N:1 관계로

- 테이블의 N:M 관계는 중간 테이블을 이용해서 1:N, N:1.
- 실전에서는 중간 테이블이 단순하지 않다.
- @ManyToMany는 제약이 많음 : 필드 추가 불가, 엔티티 테이블 불일치.
- 실전에서는 가급적 @ManyToMany 사용하지 말 것.

[@ManyToMany 사용 지양의 상세 이유 바로가기](./README.md)

---

### @JoinColumn

- 외래키를 맵핑할 때 사용.

| 속성 | 설명 | 기본값 |
| --- | --- | --- |
| name | 맵핑할 외래키 이름 | 필드명 + __ + 참조테이블 PK명 |
| referenceColumnName | 외래키가 참조하는 대상 테이블의 컬럼명 | 참조하는 테이블의 PK명 |
| foreignKey(DDL) | 외래키 제약조건 직접 지정
(이 속성은 테이블 생성시에만 사용) |  |
| unique</br>nullable</br>insertable</br>updatable</br>columnDefinition</br>table | @Column 속성과 동일 |  |

---

### @ManyToOne 주요속성

**다대일 관계 맵핑**

| 속성 | 설명 | 기본값 |
| --- | --- | --- |
| optional | false로 설정하면 연관된 엔티티가 항상 있어야 함 | TRUE |
| fetch | 글로벌 페치 전략 설정 | @ManyToOne = FetchType.EAGER
@OneToMany = FetchType.Lazy |
| cascade | 영속성 전이 기능 사용 |  |
| targetEntity | 연관된 엔티티의 타입 정보 설정</br>이 기능은 거의 사용하지 않음</br>컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있음 |  |
</br>

**일대다 관계 맵핑**

| 속성 | 설명 | 기본값 |
| --- | --- | --- |
| mappedBy | 연관관계의 주인 필드를 설정 | TRUE |
| fetch | 글로벌 페치 전략 설정 | @ManyToOne = FetchType.EAGER
@OneToMany = FetchType.Lazy |
| cascade | 영속성 전이 기능 사용 |  |
| targetEntity | 연관된 엔티티의 타입 정보 설정</br>이 기능은 거의 사용하지 않음</br>컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있음 |  |
