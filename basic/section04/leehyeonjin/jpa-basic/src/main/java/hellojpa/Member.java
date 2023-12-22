package hellojpa;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

@Entity
// TABLE 전략으로 PK값을 할당하는 경우 생성해야하는 테이블 생성
@TableGenerator(
	name = "MEMBER_SEQ_GENERATOR",
	table = "MY_SEQUENCES",
	pkColumnValue = "MEMBER_SEQ", allocationSize = 1
)
// SEQUENCE 전략으로 PK값을 할당하는 경우, 기본적으로 Sequence 객체는 hibernate가 제공
// 그러나 만약 테이블별로 다른 sequence 객체를 사용하고 싶다면 GenerateSequence 어노테이션을 통해 테이블마다 객체를 생성해서 맵핑해줄 것
@SequenceGenerator(
	name = "MEMBER_SEQ_GENERATOR",
	sequenceName = "MEMBER_SEQ", // 맵핑할 DB의 시퀀스 이름
	initialValue = 1, allocationSize = 1
)
public class Member {

	@Id
	// GeneratedValue 없이 Id 어노테이션만 지정 -> 해당 필드가 PK임만을 명시하고, 그 값은 직접 할당
	// Id가 DB 방언에 맞춰서 자동 할당
	// @GeneratedValue(strategy = GenerationType.AUTO)
	// Id가 MySQL 기준 AUTO_INCREMENT 방식으로 할당됨
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	// Id가 Table 테이블을 직접 생성하여 해당 테이블에서 값을 가져와서 할당
	// @GeneratedValue(strategy = GenerationType.TABLE)
	// Id가 Sequence 객체를 생성하여 해당 객체에서 값을 가져와서 할당
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	// DB의 name이라는 컬럼은 생성은 가능하지만(insertable의 기본값이 true이기 때문), 수정은 불가능
	// @Column(name = "name", updatable = false)
	// DDL 생성시 DB의 name이라는 컬럼의 제약조건으로 not null이 추가됨
	// @Column(name = "name", nullable = false)
	// DDL 생성시 name이라는 컬럼에 유니크 제약조건이 추가됨(잘 사용하지 X)
	// @Column(name = "name", unique = true)
	// DDL 생성시 컬럼에 대한 정의를 직접 SQL문을 통해 하고싶은 경우
	// @Column(name = "name", columnDefinition = "varchar(100) default 'EMPTY")
	@Column(name = "name")
	private String username;

	// 아주 큰 숫자의 소수점 등을 사용할 떄 사용
	// @Column(precision = 19, scale = 4)
	// private BigDecimal num;

	private Integer age;

	@Enumerated(EnumType.STRING) // Java에는 Enum 타입이 있으나, DB에는 없기 때문에 Enumerated 어노테이션을 활용해서 맵핑
	private RoleType roleType;

	// 날짜를 표현하는 맵핑 정보(DATE, TIME, TIMESTAMP)
	// Java 8 이후로 LocalDate, LocalDateTime 타입이 추가되면서 필요없어짐
	// @Temporal(TemporalType.TIMESTAMP)
	private LocalDate createdDate;

	// @Temporal(TemporalType.TIMESTAMP)
	private LocalDate lastModifiedDate;

	@Lob // varchar 크기를 넘어서는 데이터 저장에 필요
	private String description;

	@Transient
	private int temp;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public RoleType getRoleType() {
		return roleType;
	}

	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDate getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDate lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}
}
