package jpabook.jpashop.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "category")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	// N:M 연관관계 맵핑
	// 이때 다대다 관계를 테이블에 저장하기 위해 중간테이블을 생성
	// 중간테이블에는 연관관계에 참여하는 엔티티의 PK들을 FK로 지정
	@ManyToMany
	@JoinTable(
		name = "category_item",
		joinColumns = @JoinColumn(name = "category_id"),
		inverseJoinColumns = @JoinColumn(name = "item_id")
	)
	private List<Item> items = new ArrayList<>();

	// 상위 카테고리
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	// 하위 카테고리
	@OneToMany(mappedBy = "parent")
	private List<Category> children = new ArrayList<>();
}
