# 실전예제(연관관계 맵핑 시작)

---

### 테이블 중심 엔티티 설계 → 객체 중심 엔티티 설계

1. 서로 연관관계를 가지는 엔티티 간에 객체 지향 적으로 참조를 사용하도록 코드 변경.
2. Member 엔티티에 Order 엔티티를 양방향 연관관계 맵핑 처리.

> 💡 **양방향 연관관계의 필요성?**
>
> 사실상 로직으로만 보았을 때, 회원 엔티티가 주문에 대한 목록을 컬렉션으로 참조값을 가지는 것은 상당히 부자연스러움.</br>
> (실제 운영 서비스였다면 필요없었을 양방향 연관관계지만, 실습이기 때문에 추가).

<img width="570" alt="Untitled (2)" src="https://github.com/hgene0929/JPA/assets/90823532/58ee759f-4285-4773-9dd5-30d87bd08158">

---

### 엔티티 변경

[**회원 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Member.java)

[**상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Item.java)

[**주문 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Order.java)

[**주문상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/OrderItem.java)
