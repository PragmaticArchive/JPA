package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class flush {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션 불러오기
        tx.begin(); // 트랙잭션 시작
        try {
            Member member = new Member(3L, "Flush");
            em.persist(member);

            // commit전에 flush가 직접 호출(1차캐시는 그대로 있음)
            // flush : 영속성 컨텍스트의 변경내용을 디비에 동기화
            // 플러시를 할 경우, 쓰기 지연 sql 저장소에 있는 쿼리문이 날아가고 DB에 반영됨
            em.flush();

            System.out.println("==========");
            tx.commit(); // 트랜잭션 커밋(정보 반영)

        } catch (Exception e) {
            tx.rollback(); // 트랜잭션 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}
