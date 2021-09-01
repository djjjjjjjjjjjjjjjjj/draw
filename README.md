
**![image](https://user-images.githubusercontent.com/87048583/130795100-b322940a-0cc7-4a61-a912-cd7177500c9c.png)![image](https://user-images.githubusercontent.com/87048583/130794977-8f194589-8d9b-438f-9b71-363a468bb656.png)**

# The Draw 

본 예제는 MSA/DDD/Event Storming/EDA 를 포괄하는 분석/설계/구현/운영 전단계를 커버하도록 구성한 예제입니다.
이는 클라우드 네이티브 애플리케이션의 개발에 요구되는 체크포인트들을 통과하기 위한 예시 답안을 포함합니다.
- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW


# Table of contents

- [ 응모추첨시스템 ](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
  - [신규 개발 조직의 추가](#신규-개발-조직의-추가)

# 서비스 시나리오

NIKE THE DRAW 시스템 커버하기

기능적 요구사항
1. 고객이 Draw를 신청한다.
2. 고객이 Draw를 취소한다.
3. 고객이 Draw 결과를 확인한다.
4. 고객이 주문을 한다. 주문시 인증을 한다. 인증 성공시만 주문 가능.
5. 고객이 Draw 신청을 취소할 수 있다
6. 고객이 Draw신청을 취소하면 당첨에서도 제외시킨다.
7. 고객이 당첨결과를 확인한다.
8. 고객이 주문을 취소한다. 주문 취소시 당첨에서도 취소시킨다.


비기능적 요구사항
1. 트랜잭션
    1.  주문건시 인증 과정을 거치며 인증 시패 시 서비스 이용이 불가하다 Sync 호출 
    2.  고객이 요청한 업무 처리가 실패한 경우 요청 내역을 삭제한다 (Correlation)
1. 장애격리
    1. 주문 기능이 수행되지 않더라도 Draw신청은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    1. Draw시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
1. 성능
    1. 고객이 자주 Draw 결과를 확인할 수 있어야 한다  CQRS
    1. Draw상태가 바뀔때마다 SMS 등으로 알림을 줄 수 있어야 한다  Event driven


# 체크포인트

- 분석 설계


  - 이벤트스토밍: 
    - 스티커 색상별 객체의 의미를 제대로 이해하여 헥사고날 아키텍처와의 연계 설계에 적절히 반영하고 있는가?
    - 각 도메인 이벤트가 의미있는 수준으로 정의되었는가?
    - 어그리게잇: Command와 Event 들을 ACID 트랜잭션 단위의 Aggregate 로 제대로 묶었는가?
    - 기능적 요구사항과 비기능적 요구사항을 누락 없이 반영하였는가?    

  - 서브 도메인, 바운디드 컨텍스트 분리
    - 팀별 KPI 와 관심사, 상이한 배포주기 등에 따른  Sub-domain 이나 Bounded Context 를 적절히 분리하였고 그 분리 기준의 합리성이 충분히 설명되는가?
      - 적어도 3개 이상 서비스 분리
    - 폴리글랏 설계: 각 마이크로 서비스들의 구현 목표와 기능 특성에 따른 각자의 기술 Stack 과 저장소 구조를 다양하게 채택하여 설계하였는가?
    - 서비스 시나리오 중 ACID 트랜잭션이 크리티컬한 Use 케이스에 대하여 무리하게 서비스가 과다하게 조밀히 분리되지 않았는가?
  - 컨텍스트 매핑 / 이벤트 드리븐 아키텍처 
    - 업무 중요성과  도메인간 서열을 구분할 수 있는가? (Core, Supporting, General Domain)
    - Request-Response 방식과 이벤트 드리븐 방식을 구분하여 설계할 수 있는가?
    - 장애격리: 서포팅 서비스를 제거 하여도 기존 서비스에 영향이 없도록 설계하였는가?
    - 신규 서비스를 추가 하였을때 기존 서비스의 데이터베이스에 영향이 없도록 설계(열려있는 아키택처)할 수 있는가?
    - 이벤트와 폴리시를 연결하기 위한 Correlation-key 연결을 제대로 설계하였는가?

  - 헥사고날 아키텍처
    - 설계 결과에 따른 헥사고날 아키텍처 다이어그램을 제대로 그렸는가?
    
- 구현
  - [DDD] 분석단계에서의 스티커별 색상과 헥사고날 아키텍처에 따라 구현체가 매핑되게 개발되었는가?
    - Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 데이터 접근 어댑터를 개발하였는가
    - [헥사고날 아키텍처] REST Inbound adaptor 이외에 gRPC 등의 Inbound Adaptor 를 추가함에 있어서 도메인 모델의 손상을 주지 않고 새로운 프로토콜에 기존 구현체를 적응시킬 수 있는가?
    - 분석단계에서의 유비쿼터스 랭귀지 (업무현장에서 쓰는 용어) 를 사용하여 소스코드가 서술되었는가?
  - Request-Response 방식의 서비스 중심 아키텍처 구현
    - 마이크로 서비스간 Request-Response 호출에 있어 대상 서비스를 어떠한 방식으로 찾아서 호출 하였는가? (Service Discovery, REST, FeignClient)
    - 서킷브레이커를 통하여  장애를 격리시킬 수 있는가?
  - 이벤트 드리븐 아키텍처의 구현
    - 카프카를 이용하여 PubSub 으로 하나 이상의 서비스가 연동되었는가?
    - Correlation-key:  각 이벤트 건 (메시지)가 어떠한 폴리시를 처리할때 어떤 건에 연결된 처리건인지를 구별하기 위한 Correlation-key 연결을 제대로 구현 하였는가?
    - Message Consumer 마이크로서비스가 장애상황에서 수신받지 못했던 기존 이벤트들을 다시 수신받아 처리하는가?
    - Scaling-out: Message Consumer 마이크로서비스의 Replica 를 추가했을때 중복없이 이벤트를 수신할 수 있는가
    - CQRS: Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능한가?

  - 폴리글랏 플로그래밍
    - 각 마이크로 서비스들이 하나이상의 각자의 기술 Stack 으로 구성되었는가?
    - 각 마이크로 서비스들이 각자의 저장소 구조를 자율적으로 채택하고 각자의 저장소 유형 (RDB, NoSQL, File System 등)을 선택하여 구현하였는가?
  - API 게이트웨이
    - API GW를 통하여 마이크로 서비스들의 집입점을 통일할 수 있는가?
    - 게이트웨이와 인증서버(OAuth), JWT 토큰 인증을 통하여 마이크로서비스들을 보호할 수 있는가?
- 운영
  - SLA 준수
    - 셀프힐링: Liveness Probe 를 통하여 어떠한 서비스의 health 상태가 지속적으로 저하됨에 따라 어떠한 임계치에서 pod 가 재생되는 것을 증명할 수 있는가?
    - 서킷브레이커, 레이트리밋 등을 통한 장애격리와 성능효율을 높힐 수 있는가?
    - 오토스케일러 (HPA) 를 설정하여 확장적 운영이 가능한가?
    - 모니터링, 앨럿팅: 
  - 무정지 운영 CI/CD (10)
    - Readiness Probe 의 설정과 Rolling update을 통하여 신규 버전이 완전히 서비스를 받을 수 있는 상태일때 신규버전의 서비스로 전환됨을 siege 등으로 증명 
    - Contract Test :  자동화된 경계 테스트를 통하여 구현 오류나 API 계약위반를 미리 차단 가능한가?


# 분석/설계


## AS-IS 조직 (Horizontally-Aligned)
  ![image](https://user-images.githubusercontent.com/87048583/130798280-2ba0b3c3-c3bf-4303-b311-6421aee55706.png)

## TO-BE 조직 (Vertically-Aligned)
  ![image](https://user-images.githubusercontent.com/87048583/130798354-01db7437-6d30-44ed-9d42-aed4db9a9833.png)


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과: http://labs.msaez.io/#/storming/T1YZ69ggZUbBACuupTUSDFTVC872/49d1ec0b9c0c606e75abe005e9eb8ba6

### 이벤트 도출
![image](https://user-images.githubusercontent.com/87048583/131518889-6507170e-7932-443d-8e79-aa7df21e44fa.png)

### 부적격 이벤트 탈락
![image](https://user-images.githubusercontent.com/87048583/131518961-08ffd9c8-072a-4187-81b8-d095a6550480.png)

### 액터, 커맨드 부착하여 읽기 좋게
![image](https://user-images.githubusercontent.com/87048583/131519243-a869d260-6352-4326-854d-23f42e8ad893.png)

### 어그리게잇으로 묶기
![image](https://user-images.githubusercontent.com/87048583/131519456-02336333-9eb2-4f8e-aac3-5b39d5517d86.png)

### 바운디드 컨텍스트로 묶기
![image](https://user-images.githubusercontent.com/87048583/131523034-ec9cef60-0565-4182-9fbb-3337c244ac9e.png)

![image](https://user-images.githubusercontent.com/87048583/131523077-f9e3f351-1750-4dbd-a751-ae37292a8ac3.png)

![image](https://user-images.githubusercontent.com/87048583/131523108-72a848f0-d7e0-4510-babd-1db0e2ab337a.png)

![image](https://user-images.githubusercontent.com/87048583/131523168-85cfe3cc-859c-4b2c-b905-c9fdd5938455.png)

![image](https://user-images.githubusercontent.com/87048583/131523209-752c7f9b-25d2-4272-92e9-34dc8b03ac2e.png)

![image](https://user-images.githubusercontent.com/87048583/131523265-daf38a53-030f-4c28-8616-db44eb806cbd.png)

        - 인증 실패한 주문건은 아예 거래가 성립되지 않아야 한다  Sync 호출 
        - 주문 기능이 수행되지 않더라도 Draw신청은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
        - Draw시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
        - 고객이 자주 Draw 결과를 확인할 수 있어야 한다  CQRS
        - Draw상태가 바뀔때마다 카톡 등으로 알림을 줄 수 있어야 한다  Event driven

## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://user-images.githubusercontent.com/87048583/131671677-fc1b4d22-8cb2-4da3-83da-43ec20495ecd.png)


# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트와 파이선으로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd draw
mvn spring-boot:run

cd raffle
mvn spring-boot:run 

cd order
mvn spring-boot:run  

cd authentication
mvn spring-boot:run  

cd Mypage
mvn spring-boot:run

cd gateway
mvn spring-boot:run
```

## DDD 의 적용

1. 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다.

## Draw.java
```java
package draw;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Draw_table")
public class Draw {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Integer itemNo;
    private Double price;
    private Date drawDate;
    private Integer size;
    private String userId;
    private Integer drawId;
    private String drawName;
    private boolean win;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getItemNo() {
        return itemNo;
    }

    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    public Date getDrawDate() {
        return drawDate;
    }

    public void setDrawDate(Date drawDate) {
        this.drawDate = drawDate;
    }
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Integer getDrawId() {
        return drawId;
    }

    public void setDrawId(Integer drawId) {
        this.drawId = drawId;
    }
    public String getDrawName() {
        return drawName;
    }

    public void setDrawName(String drawName) {
        this.drawName = drawName;
    }


    public boolean getWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

}
```
2.Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다

## DrawRepository.java


```java
package draw;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="draws", path="draws")
public interface DrawRepository extends PagingAndSortingRepository<Draw, Long>{


}

```
3.적용 후 REST API 의 테스트
 draw 서비스의 요청처리
```
http localhost:8080/draws itemNo="1111" price="100000" drawDate="2021-08-28" size=275 drawId="001" drawName="NIKE jordan 1" userId="dj14"
```
![image](https://user-images.githubusercontent.com/87048583/131528359-b9a26571-5fcb-4d75-82ab-65e8afe654ea.png)


+모든 요청은 request 에서 처리하는 관계로 다른 마이크로시스템에 접속하지 않는다.
요청상태 확인
```
http http://request:8080/draws/1
```
![image](https://user-images.githubusercontent.com/87048583/131528461-c62cf09b-10de-41a0-97a8-a0965eca3545.png)


## 폴리글랏 퍼시스턴스


고객에게 메세지 전송은 전통적인 RDB로 개발 하기로 하고 구현이 간단한 sqlite로 구현함.

pom.xml
```
sqlite 사용을 위해 sqlite용 jdbc dependency 추가
	<dependency>
      		<groupId>org.xerial</groupId>
      		<artifactId>sqlite-jdbc</artifactId>
	</dependency>
쿼리로 작업하기 위해 mybatis dependency 추가
	<dependency>
    		<groupId>org.hibernate</groupId>
		<artifactId>hibernate-core</artifactId>
	</dependency>
    	<dependency>
        	<groupId>org.mybatis.spring.boot</groupId>
        	<artifactId>mybatis-spring-boot-starter</artifactId>
        	<version>1.3.2</version>
    	</dependency>
```

application.yml
was 기동시 자동으로 sqlite 연결 설정 프로젝트 폴드에 자동으로 sqlitesample.db 생성됨
  datasource:
    url: jdbc:sqlite:sqlitesample.db 
    driver-class-name: org.sqlite.JDBC
    username: admin 
    password: admin


SendMsgVO.java
mybatis 조회 결과를 VO 형태로 받기 위한 VO 설정
```java
@Data
public class SendMsgVO {
	private Long id; 
	private String phone; 
	private String message; 
	
	public SendMsgVO() {
	}
	
	public SendMsgVO(String pphone, String pmessage) {
		this.phone = pphone;
		this.message = pmessage;
	}
}

KakaoDAO.java
Mapper.xml 파일과 연결하기 위한 용도로 함수 생성
@Mapper
public interface  KakaoDAO {
	void insertmsg(SendMsgVO vo) throws Exception;;
	List<SendMsgVO> selectmsg() throws Exception;;
}
```

## 폴리글랏 프로그래밍

구현의 편의를 위해 Java 버전도 16 을 사용.

KakaoTakMapper.xml
``` 
KakaoDAO.java 생성된 함수와 동일한 package 경로와 함수명으로 select, insert 함수 생성

<mapper namespace="com.example.demo.table.KakaoDAO">
    <select id="selectmsg"  resultType="com.example.demo.table.SendMsgVO">
    <![CDATA[
        select id, phone, message from send_msg order by 1 desc LIMIT 5
    ]]>
    </select>
    
    <insert id="insertmsg" parameterType="com.example.demo.table.SendMsgVO" >
    <![CDATA[
    	INSERT INTO send_msg VALUES 
		( (select max(id)+1 from send_msg) , 
		#{phone} , #{message}  )
	]]>
	</insert>
</mapper>
```
  
KafkaService.java
카프카에서 수신 받은 메세지를 sqlite에 저장

```java  
@Service
@Transactional
public class KafkaService {
	
	//저장을 위해 위에서 만든 DAO 파일 선언
	@Autowired
	KakaoDAO kakaoDAO;
	
        //수신 받을 topic 선언
	@KafkaListener(topics = "onlinebank", groupId="kakaotalk")
	public void getKafka(String message) {
		
		System.out.println( "kakaotalk getKafka START " );
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, String> map = (Map<String, String>)objectMapper.readValue( message, Map.class);
			System.out.println( "kafka recerve data : " + map);
			
			//수신 받은 메세지에서 job 에 kakaotalk 이라는 글자가 존재 하면 sqlite 저장
			if ( map.get("job")!= null && map.get("job").indexOf("kakaotalk") >=0 ) {
				
				SendMsgVO vo = new SendMsgVO(); 
				vo.setPhone(  map.get( "phone").toString() );
	    		vo.setMessage( map.get( "message").toString() );
	    		kakaoDAO.insertmsg(vo);
			} else {
				System.out.println( "kafka Skip ");
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
```
  
Dockerfile 
Java 16 버전 사용을 위해 image도 openjdk16 을 사용함.

FROM khipu/openjdk16-alpine
COPY target/*SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xmx400M","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.profiles.active=docker"]



## 동기식 호출 구현
분석단계에서의 조건 중 하나로 주문(order)->인증(auth) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로

처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를

이용하여 호출하도록 한다.

결제서비스를 호출하기 위하여 FeignClient 를 이용하여 Service 대행 인터페이스 구현

AuthService.java

```java
@FeignClient(name="auths", url="http://auth:8080")
public interface AuthService {
    @RequestMapping(method= RequestMethod.GET, path="/auths")
    public void requestAuth(@RequestBody Auth auth);

}
```
요청을 받은 직후(@PostPersist) 인증을 요청하도록 처리
Ordert.java
```java
@PostPersist
    public void onPostPersist(){
        OrderRequested orderRequested = new OrderRequested();
        BeanUtils.copyProperties(this, orderRequested);
        orderRequested.publishAfterCommit();

        draw.external.Auth auth = new draw.external.Auth();
        BeanUtils.copyProperties(this, auth);
        auth.setOrderRequestId( getId() );
        // mappings goes here
        OrderApplication.applicationContext.getBean(draw.external.AuthService.class).requestAuth(auth);
       
 
    }
```

![image](https://user-images.githubusercontent.com/87048583/131611825-955848f6-2968-4e9e-91f2-e0835fbe6cdb.png)
![image](https://user-images.githubusercontent.com/87048583/131611850-76059c79-3e02-402b-9458-8b7b3a6fa6f3.png)
![image](https://user-images.githubusercontent.com/87048583/131611872-2ad413ba-afe7-4c7e-94b8-891b5cc96aca.png)



## 비동기식 호출
인증성공 후 주문 시스템으로 이를 알려주는 행위는 동기식이 아니라 비 동기식으로 처리하여

주문 처리를 위하여 인증 기능이 블로킹 되지 않아도록 처리한다.

이를 위하여 로 인증완료 되었다는 도메인 이벤트를 카프카로 송출한다(Publish)
Auth.java
```java
 @PrePersist
    public void onPrePersist(){

        String userId = "dj@sk.com";
        String userName = "dj";
        String userPassword = "1234";
        boolean authResult = false ;

        if( userId.equals( getUserId() ) && userName.equals( getUserName() ) && userPassword.equals( getUserPassword() ) ){
            authResult = true ;
        }

        System.out.println("\nBankAuthentication.Auth.39\n###############################################");
        System.out.println("authResult : " + authResult) ;
        System.out.println("orderRequestId : " + getOrderRequestId()) ;
        System.out.println("requestName : " + getOrderRequestName()) ;
        System.out.println("###############################################\n");
	
	 // 실패 이벤트 카프카 송출
        if( authResult == false ){
            AuthCancelled authCancelled = new AuthCancelled();
            BeanUtils.copyProperties(this, authCancelled);
            authCancelled.publish();
	
	 // 성공 이벤트 카프카 송출
        }else{
            AuthCertified authCertified = new AuthCertified();
            BeanUtils.copyProperties(this, authCertified);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    authCertified.publish();
                }
            });

            //try {
            //    Thread.currentThread().sleep((long) (400 + Math.random() * 220));
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}

        }

    }
```
주문 서비스에서는 인증 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다
PolicyHandler.java

```java
@Service
public class PolicyHandler{
    @Autowired OrderRepository orderRepository;
    //인증 실패
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCancelled_CancelAuthentication(@Payload AuthCancelled authCancelled){

        if(!authCancelled.validate()) return;    
        orderRepository.deleteById( authCancelled.getOrderRequestId() );
        System.out.println("\nBankRequest.PolicyHandler.20\n##############################################" + 
                           "\n" + authCancelled.getOrderRequestName() + " Order Cancelled" + 
                           "\nAuthentication Failed" + 
                           "\n##############################################\n" );
        System.out.println("Order PolicyHandler.24\n##### listener Cancel Authentication : " + authCancelled.toJson() + "\n");


    }

    // 인증 성공시 정책 수신(비동기)
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCertified_OrderComplate(@Payload AuthCertified authCertified){
        if(!authCertified.validate()) return;


        Order order = orderRepository.findByItemNo( authCertified.getItemNo() );
        order.setItemNo(authCertified.getItemNo());
        order.setPrice(159000.0);
        order.setSize(280);
        order.setUserId(authCertified.getUserId());
        orderRepository.save(order);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
```
![image](https://user-images.githubusercontent.com/87048583/131611917-f0badd89-fc45-47bc-9982-81c181998058.png)
![image](https://user-images.githubusercontent.com/87048583/131611943-ef89d8ed-cf8f-4c71-9083-b70025a2a16a.png)
![image](https://user-images.githubusercontent.com/87048583/131611973-e0d7f9ca-6c00-4f6c-bd11-fbc7195bd535.png)
![image](https://user-images.githubusercontent.com/87048583/131611998-734fff9a-11d8-4f24-a427-f1349d658da7.png)


# 운영

## CI/CD 설정


각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 GCP를 사용하였으며, pipeline build script 는 각 프로젝트 폴더 이하에 cloudbuild.yml 에 포함되었다.


## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 단말앱(app)-->결제(pay) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 결제 요청이 과도할 경우 CB 를 통하여 장애격리.

- Hystrix 를 설정:  요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정
```
# application.yml

hystrix:
  command:
    # 전역설정
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610

```

- 피호출 서비스(결제:pay) 의 임의 부하 처리 - 400 밀리에서 증감 220 밀리 정도 왔다갔다 하게
```
# (pay) 결제이력.java (Entity)

    @PrePersist
    public void onPrePersist(){  //결제이력을 저장한 후 적당한 시간 끌기

        ...
        
        try {
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```

* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명
- 60초 동안 실시

```
$ siege -c100 -t60S -r10 --content-type "application/json" 'http://localhost:8081/orders POST {"item": "chicken"}'

** SIEGE 4.0.5
** Preparing 100 concurrent users for battle.
The server is now under siege...

HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.73 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.75 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.77 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.97 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.81 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.87 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.12 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.16 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.17 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.26 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.25 secs:     207 bytes ==> POST http://localhost:8081/orders

* 요청이 과도하여 CB를 동작함 요청을 차단

HTTP/1.1 500     1.29 secs:     248 bytes ==> POST http://localhost:8081/orders   
HTTP/1.1 500     1.24 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.23 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.42 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     2.08 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.29 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.24 secs:     248 bytes ==> POST http://localhost:8081/orders

* 요청을 어느정도 돌려보내고나니, 기존에 밀린 일들이 처리되었고, 회로를 닫아 요청을 다시 받기 시작

HTTP/1.1 201     1.46 secs:     207 bytes ==> POST http://localhost:8081/orders  
HTTP/1.1 201     1.33 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.36 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.63 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.65 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.69 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.71 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.71 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.74 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.76 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.79 secs:     207 bytes ==> POST http://localhost:8081/orders

* 다시 요청이 쌓이기 시작하여 건당 처리시간이 610 밀리를 살짝 넘기기 시작 => 회로 열기 => 요청 실패처리

HTTP/1.1 500     1.93 secs:     248 bytes ==> POST http://localhost:8081/orders    
HTTP/1.1 500     1.92 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.93 secs:     248 bytes ==> POST http://localhost:8081/orders

* 생각보다 빨리 상태 호전됨 - (건당 (쓰레드당) 처리시간이 610 밀리 미만으로 회복) => 요청 수락

HTTP/1.1 201     2.24 secs:     207 bytes ==> POST http://localhost:8081/orders  
HTTP/1.1 201     2.32 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.16 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.19 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.19 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.19 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.21 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.29 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.30 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.38 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.59 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.61 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.62 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.64 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.01 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.27 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.33 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.45 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.52 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.57 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.69 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.69 secs:     207 bytes ==> POST http://localhost:8081/orders

* 이후 이러한 패턴이 계속 반복되면서 시스템은 도미노 현상이나 자원 소모의 폭주 없이 잘 운영됨


HTTP/1.1 500     4.76 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.23 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.76 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.74 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.82 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.82 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.84 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.66 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     5.03 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.22 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.19 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.18 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.69 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.65 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     5.13 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.84 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.25 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.25 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.80 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.87 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.33 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.86 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.96 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.34 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.04 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.50 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.95 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.54 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.65 secs:     207 bytes ==> POST http://localhost:8081/orders


:
:

Transactions:		        1025 hits
Availability:		       63.55 %
Elapsed time:		       59.78 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02
Successful transactions:        1025
Failed transactions:	         588
Longest transaction:	        9.20
Shortest transaction:	        0.00

```
- 운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌. 하지만, 63.55% 가 성공하였고, 46%가 실패했다는 것은 고객 사용성에 있어 좋지 않기 때문에 Retry 설정과 동적 Scale out (replica의 자동적 추가,HPA) 을 통하여 시스템을 확장 해주는 후속처리가 필요.

- Retry 의 설정 (istio)
- Availability 가 높아진 것을 확인 (siege)

### 오토스케일 아웃
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 


- 결제서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deploy pay --min=1 --max=10 --cpu-percent=15
```
- CB 에서 했던 방식대로 워크로드를 2분 동안 걸어준다.
```
siege -c100 -t120S -r10 --content-type "application/json" 'http://localhost:8081/orders POST {"item": "chicken"}'
```
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
kubectl get deploy pay -w
```
- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다:
```
NAME    DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
pay     1         1         1            1           17s
pay     1         2         1            1           45s
pay     1         4         1            1           1m
:
```
- siege 의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다. 
```
Transactions:		        5078 hits
Availability:		       92.45 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02
```


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c100 -t120S -r10 --content-type "application/json" 'http://localhost:8081/orders POST {"item": "chicken"}'

** SIEGE 4.0.5
** Preparing 100 concurrent users for battle.
The server is now under siege...

HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
:

```

- 새버전으로의 배포 시작
```
kubectl set image ...
```

- seige 의 화면으로 넘어가서 Availability 가 100% 미만으로 떨어졌는지 확인
```
Transactions:		        3078 hits
Availability:		       70.45 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02

```
배포기간중 Availability 가 평소 100%에서 70% 대로 떨어지는 것을 확인. 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 것이기 때문. 이를 막기위해 Readiness Probe 를 설정함:

```
# deployment.yaml 의 readiness probe 의 설정:


kubectl apply -f kubernetes/deployment.yaml
```

- 동일한 시나리오로 재배포 한 후 Availability 확인:
```
Transactions:		        3078 hits
Availability:		       100 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02

```

배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.


