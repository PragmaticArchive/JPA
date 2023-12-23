package jpabook.jpashop.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "member")
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String name;

	private String city;

	private String street;

	private String zipcode;

	// 회원 : 주문 = 일 : 다 관계의 연관관계를 양방향으로 맵핑
	// 이때, FK는 "다" 측의 주문이 가지므로 주문이 연관관계의 주인
	// 따라서 여기에는 mappedBy로 자기 주인 명시할 것
	@OneToMany(mappedBy = "member")
	private List<Order> orders = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	// 양방향 연관관계 맵핑에 따라 편의메소드 추가
	public void addOrder(Order order) {
		this.orders.add(order);
		order.setMember(this);
	}
}
