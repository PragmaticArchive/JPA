package jpabook.jpashop.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "delivery")
public class Delivery extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 1:1 양방향 맵핑(연관관계의 주인은 order)
	@OneToOne(mappedBy = "delivery")
	private Order order;

	private String city;

	private String street;

	private String zipcode;

	private DeliveryStatus status;
}
