package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class 엔티티수정_변경감지 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션 불러오기
        tx.begin(); // 트랙잭션 시작
        try {
            Member findMember = em.find(Member.class, 200L);
            // 자동 update쿼리 반영
            findMember.setName("zzzz");

            // flush() -> 1차캐시(id, 엔티티, 스냅샷)에서 엔티티와 스냅샷 비교
            // 스냅샷은 읽어온 당시의 객체상태
            // 스냅샷과 다른 엔티티는 지연저장소에 update문 생성
            // flush() -> 쿼리문 날리기 -> DB에 commit
            tx.commit(); // 트랜잭션 커밋(정보 반영)

        } catch (Exception e) {
            tx.rollback(); // 트랜잭션 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}
