package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션 불러오기
        tx.begin(); // 트랙잭션 시작
        try {
//            insert 문
//            Member member = new Member();
//            member.setId(2L);
//            member.setName("HelloB");
//
//            em.persist(member);

//            select문
//            Member member = em.find(Member.class, 1L);
//            System.out.println("member.getId() = " + member.getId());
//            System.out.println("member.getName() = " + member.getName());

//            delete문
//            em.remove(member);

//            update문 별도의 em 호출하지 않아도 됨
//            Member member = em.find(Member.class, 1L);
//            member.setName("HelloJPA");

//          JPQL : 엔티티 객체를 대상으로 쿼리, SQL : 데이터베이스 테이블을 대상으로 쿼리
            List<Member> result = em.createQuery("select m from Member m", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10) // pageNation
                    .getResultList();

            for (Member member : result) {
                System.out.println("member.getName() = " + member.getName());
            }
            tx.commit(); // 트랜잭션 커밋(정보 반영)

        } catch (Exception e) {
            tx.rollback(); // 트랜잭션 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}
