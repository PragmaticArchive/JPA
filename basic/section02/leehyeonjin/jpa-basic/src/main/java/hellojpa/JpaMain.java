package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
	public static void main(String[] args) {
		// 가장 먼저 애플리케이션에 로드된 Persistence 클래스를 통해 EntityManagerFactory 생성
		// 애플리케이션 로딩시점에 딱 하나만 생성가능
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("hello");

		// 생성된 EntityManagerFactory로부터 EntityManager 객체 생성
		// DB 커넥션을 얻억서 쿼리를 생성하고 날리는 작업을 할 때마다 생성해야 함
		EntityManager manager = factory.createEntityManager();

		// EntityManager 을 통해 필요한 동작 시작
		EntityTransaction transaction = manager.getTransaction(); // 트랜잭션 객체 추출
		transaction.begin(); // 트랜잭션 시작

		try {
			Member member = manager.createQuery("select m from Member m where m.id = 1", Member.class)
				.getSingleResult();// JPQL을 활용하여 복잡한 SQL 생성
			System.out.println("member.id = " + member.getId());
			System.out.println("member.name = " + member.getName());

			transaction.commit(); // 현대 트랜잭션의 동작 DB에 반영
		} catch (Exception e) {
			transaction.rollback(); // 로직에서 문제가 발생한 경우, 트랜잭션 롤백
		} finally {
			// 앞서 생성한 EntityManager의 동작이 완료되어 반환
			manager.close();
		}
		// EntityManager 을 통해 필요한 동작 끝

		// 애플리케이션 동작이 완전히 완료된 경우 EntityManagerFactory 까지 닫음
		factory.close();
	}
}
