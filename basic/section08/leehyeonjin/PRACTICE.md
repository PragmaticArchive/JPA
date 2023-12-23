# 실전예제(연관관계 관리)

---

### 글로벌 페치 전략 설정

1. 모든 연관관계를 지연 로딩으로.
2. @ManyToOne, @OneToOne은 기본이 즉시 로딩이므로 지연로딩으로 변경.

---

### 영속성 전이 설정

1. Order → Delivery를 영속성 전이 ALL 설정.
2. Order → OrderItem을 영속성 전이 ALL 설정.

---

### 엔티티 변경

[**상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Category.java)

[**주문 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Order.java)

[**주문상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/OrderItem.java)

[**배송 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Delivery.java)
