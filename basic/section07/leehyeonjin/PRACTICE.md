# 실전예제(상속관계 맵핑)

---

### 요구사항 추가

1. 상품의 종류는 음반, 도서, 영화가 있고 이후 더 확장 가능.
2. 모든 데이터는 등록일과 수정일이 필수다.

**도메인 모델**

<img width="667" alt="Untitled" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/fa94dffc-75f5-46d6-a942-e3451c116151">

**테이블 설계**

<img width="687" alt="Untitled (1)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/e6508f2a-c303-4ccf-802e-ea0623c2afb6">

**도메인 모델 상세**

<img width="659" alt="Untitled (2)" src="https://github.com/PragmaticArchive/Algorithm/assets/90823532/4f90fdd8-8838-4f55-94ff-71dedd69225b">

---

### 엔티티 추가 및 변경

[**상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Item.java)

[**앨범상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Album.java)

[**영화상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Movie.java)

[**도서상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Book.java)

[**공통속성** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/BaseEntity.java)

[**회원 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Member.java)

[**주문 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Order.java)

[**주문상품 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/OrderItem.java)

[**배송 엔티티** 소스코드](jpashop/src/main/java/jpabook/jpashop/domain/Delivery.java)
