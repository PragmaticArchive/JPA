package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
	public static void main(String[] args) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("hello");

		EntityManager manager = factory.createEntityManager();

		EntityTransaction transaction = manager.getTransaction();
		transaction.begin();

		try {
			// ID(PK) 값 직접 할당
			Member member1 = new Member();
			member1.setId(1L);
			member1.setUsername("A");

			manager.persist(member1);

			// ID(PK) 값 할당 방식 IDENTITY
			Member member2 = new Member();
			member2.setUsername("B");

			manager.persist(member2);

			// ID(PK) 값 할당 방식 SEQUENCE
			Member member3 = new Member();
			member3.setUsername("C");

			manager.persist(member3);

			// ID(PK) 값 할당 방식 AUTO
			Member member4 = new Member();
			member4.setUsername("D");

			manager.persist(member4);

			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
		} finally {
			manager.close();
		}

		factory.close();
	}
}
