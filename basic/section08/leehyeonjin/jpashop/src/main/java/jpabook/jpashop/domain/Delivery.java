package jpabook.jpashop.domain;

import static javax.persistence.FetchType.*;

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

	@OneToOne(mappedBy = "delivery", fetch = LAZY)
	private Order order;

	private String city;

	private String street;

	private String zipcode;

	private DeliveryStatus status;
}
