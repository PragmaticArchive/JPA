# 실전예제(요구사항 분석, 도메인 설계, 엔티티 생성)

---

### 1. 요구사항 분석

1. 회원은 상품을 주문할 수 있다.
2. 주문시 여러 종류의 상품을 선택할 수 있다.

**기능 목록**

- 회원 기능 : 회원 등록, 회원 조회.
- 상품 기능 : 상품 등록, 상품 조회, 상품 수정.
- 주문 기능 : 상품 주문, 주문내역 조회, 주문 취소.

---

### 2. 도메인 모델 분석

회원 - 주문 관계 : 회원은 여러 번 주문할 수 있다(일대다).

주문 - 상품 관계 :

- 주문할 때 여러 상품을 선택가능하다.
- 반대로 같은 상품도 여러번 주문 가능하다.
- 다대다 → 권장 X ⇒ 주문상품 이라는 모델을 만들어서 일대다, 다대일 관계로 풀어냄.

<img width="599" alt="Untitled" src="https://github.com/hgene0929/JPA/assets/90823532/ea4f1c66-ab31-49fe-8e0d-546e1158b96b">

**테이블 설계 / 엔티티 설계와 맵핑**

<img width="623" alt="Untitled (1)" src="https://github.com/hgene0929/JPA/assets/90823532/69a766ef-0aeb-4cce-8b5b-f524bc1788a2">

<img width="649" alt="Untitled (2)" src="https://github.com/hgene0929/JPA/assets/90823532/6686d7b8-84fd-4979-8fd9-efb1f6c72216">

---

### 3. 엔티티 생성

[**회원 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Member.java)

[**상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Item.java)

[**주문 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Order.java)

[**주문상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/OrderItem.java)

---

데이터 중심 설계의 문제점

- 현재 방식은 객체 설계를 테이블 설계에 맞춘 방식 → 테이블의 외래키를 객체에 그대로 가져옴.

```java
@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	// 테이블 설계 방식대로 객체지향코드(Java)를 맞추어 코드를 작성함
	@Column(name = "member_id")
	private Long memberId;

	...

}
```

- 이러한 코드 방식으로는 객체 그래프 탐색( . 연산자를 통한 연관관계 엔티티 객체 참조) 불가능.
- 이에 따라 참조(사실상 연관관계가 아님)가 없으므로, UML도 잘못됨.

[전체 코드 보기](./jpashop/src/main/java/jpabook/jpashop/JpaMain.java)
```java
// 현재 코드 상태로 연관관계의 테이블의 데이터를 조회하려면?
// 현재 엔티티는 서로 연관관계의 테이블의 PK를 테이블의 필드로 저장하고 있다.
// 따라서 조회조건이 되는 테이블을 조회한 다음 -> 해당 테이블에 존재하는 최종 조회할 테이블의 PK로 필요한 엔티티를 다시 조회해와야 한다.
// 이러한 코드는 1건의 데이터 조회를 위해 2번의 조회 쿼리를 실행해야 한다 => 비효율적
Order order = em.find(Order.class, 1L);
Long memberId = order.getMemberId();

Member member = em.find(Member.class, memberId);
```
