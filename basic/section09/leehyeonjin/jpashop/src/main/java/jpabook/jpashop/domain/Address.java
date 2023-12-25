package jpabook.jpashop.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

	// 상세한 값에 대한 제약사항을 공통으로 사용가능
	@Column(length = 10)
	private String city;

	private String street;

	private String zipcode;

	// 의미있는 비즈니스 메소드 생성
	public String fullAddress() {
		return getCity() + " " + getStreet() + " " + getZipcode();
	}

	// setter는 생성하지 않고 생성자로 값을 복사할 수만 있도록(불변객체) 하여 안정성을 높인다
	public Address() {
	}

	public Address(String city, String street, String zipcode) {
		this.city = city;
		this.street = street;
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public String getStreet() {
		return street;
	}

	public String getZipcode() {
		return zipcode;
	}

	// equals(), hashCode()를 재정의하여 컬렉션 사용시 동등성 비교가 올바르게 이루어지도록 한다
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
}
