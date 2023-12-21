package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class update {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션 불러오기
        tx.begin(); // 트랙잭션 시작
        try {
            // update문
            // 별도의 em을 호출하지 않아도 됨
            Member member = em.find(Member.class, 1L);
            member.setName("HelloJPA");

            tx.commit(); // 트랜잭션 커밋(정보 반영)

        } catch (Exception e) {
            tx.rollback(); // 트랜잭션 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}
