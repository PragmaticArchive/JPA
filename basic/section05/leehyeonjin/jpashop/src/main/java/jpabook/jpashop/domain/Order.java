package jpabook.jpashop.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	// 변경 전 테이블 중심 코드
	// @Column(name = "member_id")
	// private Long memberId;

	// 변경 후 객체 지향 코드
	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(name = "order_date")
	private LocalDateTime orderDate; // Java의 카멜표기법으로 표시해도, spring boot 사용시 알아서 DB의 관례대로 언더스코어 표기법으로 DB에 저장

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	// 주문 : 주문상품 = 일 : 다 관계의 연관관계를 양방향으로 맵핑
	// 이때, FK는 "다" 측의 주문이 가지므로 주문이 연관관계의 주인
	// 따라서 여기에는 mappedBy로 자기 주인 명시할 것
	@OneToMany(mappedBy = "order")
	private List<OrderItem> orderItems = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public void addOrderItem(OrderItem orderItem) {
		this.orderItems.add(orderItem);
		orderItem.setOrder(this);
	}
}
