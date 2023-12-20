package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class _1차캐시_같은객체조회_영속엔티티_동일성_보장 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션 불러오기
        tx.begin(); // 트랙잭션 시작
        try {

            // member1을 찾일때만 sql문 생성
            Member member1 = em.find(Member.class, 101L);
            Member member2= em.find(Member.class, 101L);

            // 자바의 컬레션에서 꺼내온 객체처럼 같은 컨텍스트에서 꺼내오면 동일객체로 판단
            System.out.println(member1==member2);
            tx.commit(); // 트랜잭션 커밋(정보 반영)

        } catch (Exception e) {
            tx.rollback(); // 트랜잭션 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}
