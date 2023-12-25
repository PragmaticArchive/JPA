# 섹션 2. JPA 시작하기
## Hello JPA - 프로젝트 생성
**H2 데이터베이스 설치와 실행**
- 가볍다
- 웹용 쿼리 툴 제공
- MySQL, Oracle 데이터베이스 시뮬레이션 기능
- 시퀀스, AUTO INCREMENT 기능 지원

### JPA 설정하기 - persistence.xml
- JPA 설정 파일
- **/META-INF/persistence.xml** 위치
- persistence-unit name으로 이름 지정
- javax.persistence로 시작: JPA 표준 속성
- hibernate로 시작 : 하이버네이트 전용 속성

### META-INF/persistence.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>  
<persistence version="2.2"  
             xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">  
    <persistence-unit name="hello">  
        <properties>            <!-- 필수 속성 -->  
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>  
            <property name="javax.persistence.jdbc.user" value="sa"/>  
            <property name="javax.persistence.jdbc.password" value=""/>  
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>  
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>  
            <!-- 옵션 -->  
            <property name="hibernate.show_sql" value="true"/>  
            <property name="hibernate.format_sql" value="true"/>  
            <property name="hibernate.use_sql_comments" value="true"/>  
            <!--<property name="hibernate.hbm2ddl.auto" value="create" />-->  
        </properties>  
    </persistence-unit></persistence>
```

- javax로 시작하는 옵션은 jpa가 표준이기 때문
- hivernate로 시작하는 옵션은 하이버네이트 전용

#### 데이터베이스 방언
- JPA는 특정 데이터베이스에 종속되지 않는다.
- MySQL, ORACLE 데이터 베이스의 명령어가 조금씩 다르듯이
	- 방언 : SQL 표준을 지키지 않는 특정 데이터베이스만의 고유한 기능
- `<property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>`
	- "내가 알아서 DB에 맞게 쓸게" 라는 뜻
- hibernate.dialect 프로퍼티에 사용하고 있는 DB에 맞는 값을 넣어주면 사용할 수 있다.

## Hello JPA - 어플리케이션 개발
### jpa 구동 방식
- Persistence -(1. 설정 정보 조회)-> META-INF/persistence.xml
- Persistence -(2. 생성)-> EntityManagerFactory -(3. 생성)-> 여러 EntityManager

### 실습 - 회원 저장
##### 회원 테이블 & Entity
```java
package hellojpa;  
  
import javax.persistence.Entity;  
import javax.persistence.Id;  
import javax.persistence.Table;  
  
@Entity  
// @Table(name = "실제 테이블 이름")  
public class Member {  
  
    @Id  
    private Long id;  
    //@Column(name = "실제 콜럼 이름")  
    private String name;  
  
    public Long getId() {  
        return id;  
    }  
  
    public void setId(Long id) {  
        this.id = id;  
    }  
  
    public String getName() {  
        return name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }  
}
```
##### 회원 등록
```java
package hellojpa;  
  
import javax.persistence.*;  
  
public class JpaMain {  
    public static void main(String[] args) {  
        EntityManagerFactory emf  = Persistence.createEntityManagerFactory("hello");  
  
        EntityManager em = emf.createEntityManager();  
  
        EntityTransaction tx = em.getTransaction();  
        tx.begin();  
  
        try {  
            Member member = new Member();  
            member.setId(2L);  
            member.setName("HelloB");  
  
            em.persist(member);  
  
            tx.commit();  
        } catch (Exception e) {  
            tx.rollback();  
        } finally {  
            em.close();  
        }  
  
        emf.close();  
    }  
}
```
##### 회원 수정
```java
try {  
    Member findMember = em.find(Member.class, 1L);  
    findMember.setName("HelloJPA");
    // 값이 바뀌면 하나하나 검사하여 자동으로 수정하여 업데이트 쿼리를 날린다.
  
    tx.commit();  
} catch (Exception e) {  
    tx.rollback();  
} finally {  
    em.close();  
}
```
##### 회원 삭제
```java
try {  
    Member findMember = em.find(Member.class, 1L);  
    em.remove(findMember);  
      
    tx.commit();  
} catch (Exception e) {  
    tx.rollback();  
} finally {  
    em.close();  
}
```
### 주의
- 엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유
- 엔티티 매니저는 쓰레드간에 공유 X (사용하고 버려야 한다.)
- **JPA의 모든 데이터 변경은 트랜잭션 안에서 실행**

### JPQL 소개
- 가장 단순한 조회 방법
	- EntityManager.find()
	- 객체 그래프 탐색 (a.getB().getC())
```java
List<Member> result = em.createQuery("select m from Member as m", Member.class)  
        .setFirstResult(0)
        .setMaxResults(10)
        .getResultList();
for (Member member : result) {
    System.out.println("member.name = " + member.getName());
}
```
- 객체 지향 쿼리 언어
	- JPQL은 엔티티 객체를 대상으로 쿼리
	- SQL은 데이터베이스 테이블을 대상으로 쿼리
- SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
- 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
- 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
- **애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요**