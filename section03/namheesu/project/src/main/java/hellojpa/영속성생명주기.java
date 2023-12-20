package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class 영속성생명주기 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션 불러오기
        tx.begin(); // 트랙잭션 시작
        try {
            // 영속성 컨텍스트 생명주기

            // 객체를 생성한 상태(비영속)
            Member member = new Member();
            member.setId(100L);
            member.setName("HELLOJPA");

            System.out.println("=====Before======");

            // 객체를 저장한 상태(영속)
            em.persist(member);

            System.out.println("=====After======");

            // 회원 엔티티를 영속성 컨텍스트에서 분리(준영속)
            // em.detach(member);

            // 객체를 삭제한 상태(삭제)
            // em.remove(member);

            tx.commit(); // 트랜잭션 커밋(정보 반영)

        } catch (Exception e) {
            tx.rollback(); // 트랜잭션 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}
