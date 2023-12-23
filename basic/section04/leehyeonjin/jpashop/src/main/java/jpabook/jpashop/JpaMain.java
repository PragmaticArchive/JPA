package jpabook.jpashop;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

public class JpaMain {
	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			// 현재 코드 상태로 연관관계의 테이블의 데이터를 조회하려면?
			// 현재 엔티티는 서로 연관관계의 테이블의 PK를 테이블의 필드로 저장하고 있다.
			// 따라서 조회조건이 되는 테이블을 조회한 다음 -> 해당 테이블에 존재하는 최종 조회할 테이블의 PK로 필요한 엔티티를 다시 조회해와야 한다.
			// 이러한 코드는 1건의 데이터 조회를 위해 2번의 조회 쿼리를 실행해야 한다 => 비효율적
			Order order = em.find(Order.class, 1L);
			Long memberId = order.getMemberId();

			Member member = em.find(Member.class, memberId);

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			em.close();
		}

		emf.close();
	}
}
