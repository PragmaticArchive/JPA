package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class 지연등록 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션 불러오기
        tx.begin(); // 트랙잭션 시작
        try {
            Member member1 = new Member(200L, "member1");
            Member member2 = new Member(201L, "member2");

            // 이때 DB에 저장하지 않고 영속성 컨텍스트에 쌓아놓음
            em.persist(member1);
            em.persist(member2);

            System.out.println("==============");

            // commit() -> 영속성 컨텍스트 flush -> DB에 commit됨
            tx.commit(); // 트랜잭션 커밋(정보 반영)

        } catch (Exception e) {
            tx.rollback(); // 트랜잭션 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}
