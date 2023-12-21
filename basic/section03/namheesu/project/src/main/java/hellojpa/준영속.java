package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class 준영속 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션 불러오기
        tx.begin(); // 트랙잭션 시작
        try {
            Member member = em.find(Member.class, 1L);
            member.setName("detached");

            // 영속성 컨텍스트에서 member를 제거(준영속)
            // update문 생성X
            em.detach(member);

            // 영속성 컨텍스트 전체를 비움
            em.clear();

            //  영속성 컨텍스트를 닫음
            em.close();

            tx.commit(); // 트랜잭션 커밋(정보 반영)

        } catch (Exception e) {
            tx.rollback(); // 트랜잭션 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}
