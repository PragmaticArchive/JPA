package hellojpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity // 해당 클래스가 엔티티임을 알림
public class Member {

	@Id // 해당 필드가 이 엔티티의 PK임을 알림
	private Long id;
	private String name;

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
}
