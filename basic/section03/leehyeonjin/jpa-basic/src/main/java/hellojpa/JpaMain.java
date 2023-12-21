package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

public class JpaMain {
	public static void main(String[] args) {
		// 가장 먼저 애플리케이션에 로드된 Persistence 클래스를 통해 EntityManagerFactory 생성
		// 애플리케이션 로딩시점에 딱 하나만 생성가능
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("hello");

		// 생성된 EntityManagerFactory로부터 EntityManager 객체 생성
		// DB 커넥션을 얻억서 쿼리를 생성하고 날리는 작업을 할 때마다 생성해야 함
		EntityManager manager = factory.createEntityManager();

		// EntityManager 을 통해 필요한 동작 시작
		EntityTransaction transaction = manager.getTransaction(); // 트랜잭션 객체 추출
		transaction.begin(); // 트랜잭션 시작

		try {
			// 객체를 생성한 상태(비영속)
			Member member = new Member();
			member.setId(100L);
			member.setName("회원1");

			// 객체를 저장한 상턔(영속) -> EntityManager.persistContext에 의해 해당 객체과 관리되기 시작
			// 1차 캐시에 저장됨
			manager.persist(member);

			// 회원 엔티티를 영속성 컨텍스트에서 분리(준영속)
			manager.detach(member);

			// 객체를 삭제한 상태(삭제)
			manager.remove(member);

			// 1차 캐시에서 조회(1차 캐시에 없다면 DB 조회)
			Member findMemberByCache = manager.find(Member.class, 100L);
			Member findMemberByDB = manager.find(Member.class, 1L);

			// 영속 엔티티의 동일성 보장
			Member a = manager.find(Member.class, 2L);
			Member b = manager.find(Member.class, 2L);
			System.out.println(a == b); // 동일성 비교 true

			// 쓰기 지연
			Member memberA = new Member();
			member.setId(101L);
			member.setName("회원A");
			Member memberB = new Member();
			member.setId(102L);
			member.setName("회원B");

			// 이 시점에는 DB에 등록하지 않고 1차 캐시에만 저장
			manager.persist(memberA);
			manager.persist(memberB);

			// 더티체킹
			Member updateMember = manager.find(Member.class, 100L);
			updateMember.setName("updateName");

			// 트랜잭션이 커밋되는 바로 이 시점에 DB에 엔티티 등록
			transaction.commit(); // 현대 트랜잭션의 동작 DB에 반영

			// 영속성 컨텍스트를 플러시 하는 방법
			manager.flush(); // 직접 호출(플러시 수동 호출)
			transaction.commit(); // (플러시 자동 호출)
			// JPQL 쿼리 실행(플러시 자동 호출)

			// 플러시 모드 옵션
			manager.setFlushMode(FlushModeType.AUTO); // 커밋이나 쿼리를 실행할 때 플러시(default)
			manager.setFlushMode(FlushModeType.COMMIT); // 커밋할 때만 플러시

			// 준영속 상태로 만드는 방법
			manager.detach(member); // 특정 엔티티만 준영속 상태로 전환
			manager.close(); // 영속성 컨텍스트를 완전히 초기화
			manager.close(); // 영속성 컨텍스트를 종료
		} catch (Exception e) {
			transaction.rollback(); // 로직에서 문제가 발생한 경우, 트랜잭션 롤백
		} finally {
			// 앞서 생성한 EntityManager의 동작이 완료되어 반환
			manager.close();
		}
		// EntityManager 을 통해 필요한 동작 끝

		// 애플리케이션 동작이 완전히 완료된 경우 EntityManagerFactory 까지 닫음
		factory.close();
	}
}
