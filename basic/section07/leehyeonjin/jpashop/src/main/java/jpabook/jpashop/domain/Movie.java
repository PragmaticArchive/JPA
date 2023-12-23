package jpabook.jpashop.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "movie")
@DiscriminatorValue("movie") // Item 테이블에서 이를 상속받는 자식을 구분하기 위한 컬럼에 movie 타입이라고 저장됨
public class Movie extends Item {

	private String director;

	private String actor;

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}
}
