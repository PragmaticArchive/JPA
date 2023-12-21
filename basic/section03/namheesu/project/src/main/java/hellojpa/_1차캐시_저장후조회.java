package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class _1차캐시_저장후조회 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션 불러오기
        tx.begin(); // 트랙잭션 시작
        try {

            Member member = new Member();
            member.setId(101L);
            member.setName("HELLOJPA");

            // 위에서 저장한 멤버객체는 1차캐시에 저장되어있기 때문에
            // select문이 실행되지않음
            Member member1 = em.find(Member.class, 101L);
            System.out.println("member1.getId() = " + member1.getId());
            System.out.println("member1.getName() = " + member1.getName());

            tx.commit(); // 트랜잭션 커밋(정보 반영)

        } catch (Exception e) {
            tx.rollback(); // 트랜잭션 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}
