
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


## CQRS
draw 서비스로 CUD를 담당하고 R은 viewpage용 myPage에서 조회

MyPageViewHandler.java
```java
@Service
public class MyPageViewHandler {

    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenRaffleRequested_then_UPDATE(@Payload RaffleRequested raffleRequested) {
        try {
            if (!raffleRequested.validate()) return;
            System.out.println("\nMyPage.MyPageViewHandler.24\n##############################################");
            System.out.println("UPDATE when RaffleRequested");
            System.out.println("##############################################\n");
            System.out.println("\nMyPage.MyPageViewHandler.27\n##### listener RaffleRequested : " + raffleRequested.toJson() + "\n");   

            MyPage myPage = myPageRepository.findByItemNo( raffleRequested.getItemNo() );
            if( myPage == null ) myPage = new MyPage();            

            myPage.setUserId( raffleRequested.getUserId() );
            myPage.setItemNo( raffleRequested.getItemNo() );
            myPage.setDrawDate( raffleRequested.getRaffleDate() );
            myPage.setDrawResult( raffleRequested.getWin() );

            if (raffleRequested.getWin() == true ) {
                myPage.setWin( 1 );
                myPage.setLoss( 0 );
            }
            else{
                myPage.setWin( 0 );
                myPage.setLoss( 1 );
            }
            myPage.setDrawStatus("Raffle Completed");
            myPageRepository.save(myPage);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenRaffleDropped_then_UPDATE(@Payload RaffleDropped raffleDropped) {
        try {
            if (!raffleDropped.validate()) return;
            System.out.println("\nMyPage.MyPageViewHandler.50\n##############################################");
            System.out.println("UPDATE when raffleDropped");
            System.out.println("##############################################\n");
            System.out.println("\nMyPage.MyPageViewHandler.53\n##### listener raffleDropped : " + raffleDropped.toJson() + "\n");   
                        
            // 요청 취소의 경우 ItemNo가 존재하는 경우에만 상태변경
            MyPage myPage = myPageRepository.findByItemNo( raffleDropped.getItemNo() );
            if( myPage != null ){  
                myPage.setUserId( raffleDropped.getUserId() );
                myPage.setItemNo( raffleDropped.getItemNo() );
                myPage.setDrawDate( raffleDropped.getRaffleDate() );
                myPage.setDrawResult( raffleDropped.getWin() );
                if (raffleDropped.getWin() == true ) {
                    myPage.setWin( 1 );
                    myPage.setLoss( 0 );
                }
                else{
                    myPage.setWin( 0 );
                    myPage.setLoss( 1 );
                }
                myPage.setDrawStatus("Raffle Dropped");
                myPageRepository.save(myPage);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}

```
MyPage 

![image](https://user-images.githubusercontent.com/87048583/131695481-69824863-183e-469c-a5ba-74d912f70c2e.png)

draw->raffle 

![image](https://user-images.githubusercontent.com/87048583/131695641-eb0459b2-7dac-40a8-966f-11a194c7d54b.png)
![image](https://user-images.githubusercontent.com/87048583/131696524-f7f80fed-4766-449d-85c3-3d6da84fbbc9.png)

mypage

![image](https://user-images.githubusercontent.com/87048583/131695692-a4cc9b58-1c0e-4f93-91a5-9d87e7328d95.png)

## 폴리글랏 퍼시스턴스


Draw 서비스만 간단한 mariaDB로 구현함.

pom.xml
```
<dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-data-jpa</artifactId>
      </dependency>
      <dependency>
          <groupId>org.mariadb.jdbc</groupId>
          <artifactId>mariadb-java-client</artifactId>
      </dependency>
```

application.yml
```
datasource:
    url: jdbc:mariadb://localhost:3306/tutorial
    driver-class-name: org.mariadb.jdbc.Driver
    username: root
    password: 1234
```

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

### Gateway
#### Gateway 기능이 정상적으로 수행되는지 확인하기 위하여 Gateway를 통하여 요청서비스를 호출한다
```
root@siege:/# http gateway:8080/draws itemNo="1111" price="100000" drawDate="2021-08-28" size=275 drawId="01" drawName="NIKE jordan 1" userId="dj14"
```
![image](https://user-images.githubusercontent.com/87048583/131790033-a5c47655-22ce-49b0-b1f4-3ec4fb645da0.png)

요청 처리결과를 통하여 Gateway 기능이 정상적으로 수행되었음을 확인할 수 있다.
요청이 정상적으로 처리되지 않는 경우
요청시 파라미터로 전송된 id 값을 기준으로 기 저장된 요청 데이터를 삭제한다.

![image](https://user-images.githubusercontent.com/87048583/131791221-1de8e4ca-837e-484d-96e9-5de750ea585c.png)
![image](https://user-images.githubusercontent.com/87048583/131791291-40f79f73-fc26-4cc2-8a09-c323dcd8ef85.png)
![image](https://user-images.githubusercontent.com/87048583/131791324-444244d2-9c05-4014-9b64-46f82bf81c2d.png)

Gateway 테스트시 존재하지 않는 응모요청코드에  시도하였으며 요청이 정상적으로 처리되지 못한 관계로 기 저장된 데이터가 삭제 처리 된다.


### 동기식 호출 (운영)

#### 동기식 호출인 관계로 인증시스템 장애시 서비스를 처리할 수 없다. 

1) 인증 서비스 임시로 삭제한다. 

```
root@labs-1621740876:/home/project/draw# kubectl delete service auth
service "auth" deleted
```
![image](https://user-images.githubusercontent.com/87048583/131793237-f12213d6-dde3-498b-b111-c8359ca1e5e0.png)

2) 요청 처리결과를 확인한다.

```
root@siege:/# http order:8080/orders itemNo="1112" price="10000" size=275 userId="dj@sk.com" userPassword="1234""
```
![image](https://user-images.githubusercontent.com/87048583/131793457-369f82cc-21f9-4a78-a6c6-76a111d94a05.png)


3) 인증서비스 재기동 한다. 

```
root@labs-1621740876:/home/project/draw# kubectl expose deploy auth --type="LoadBalancer" --port=8080
service/auth exposed
```
![image](https://user-images.githubusercontent.com/87048583/131793534-3bcb4a38-ddfd-4406-ad99-eba5d81c879f.png)

4) 요청처리 결과를 확인한다. 

```
root@siege:/# http order:8080/orders itemNo="1114" price="10000" size=275 userId="dj@sk.com" userPassword="1234"
```

![image](https://user-images.githubusercontent.com/87048583/131814095-e07e916f-fd20-4072-aa29-f58c68a5ed22.png)

#### 테스트를 통하여 인증 서비스가 기동되지 않은 상태에서는 업무 요청이 실패함을 확인 할 수 있음.

### Zero-downtime deploy (Readiness Probe) 무정지 재배포

```
root@siege:/#  siege -v -c100 -t90S -r10 --content-type "application/json" 'http://order:8080/orders POST {"itemNo":"1131","price":"100000","size":"275 ","userId":"dj14","userPassword":"1234"}'
( 동시사용자 100명, 90초간 진행 )
```
![image](https://user-images.githubusercontent.com/87048583/131841608-2ab940c9-1cc6-4b9e-ac93-f72d6746cd93.png)

1. 부하테스트중 추가 생성한 Terminal 에서 readiness 설정되지 않은 버젼으로 재배포 한다.
```
root@labs-1621740876:/home/project/draw# kubectl apply -f order-redeploy.yaml
```
![image](https://user-images.githubusercontent.com/87048583/131841542-c7e86352-e6ae-487a-9caf-12501cf2aa3d.png)


2. 부하테스트중 추가 생성한 Terminal 에서 readiness 설정된 버젼으로 재배포 한다.
```
root@labs-1621740876:/home/project/draw# kubectl apply -f order-deploy.yaml
```
![image](https://user-images.githubusercontent.com/87048583/131842391-705fb451-7396-4d0f-9934-eb22877eb73f.png)

![image](https://user-images.githubusercontent.com/87048583/131842358-0b643e54-ee74-4d60-8494-e8f04e12fe41.png)

부하테스트 결과

![image](https://user-images.githubusercontent.com/87048583/131842474-b2858d96-4b61-43de-a571-64daf2610fb1.png)

중단없이 완료됨

### Persistence Volume

#### Persistence Volume 을 생성한다. 

```
root@labs-1621740876:/home/project/draw# kubectl get pv
```
![image](https://user-images.githubusercontent.com/87048583/131848914-50db3379-645c-4468-a005-83b724edb68c.png)

#### Persistence Volume Claim 을 생성한다. 

```
root@labs-1621740876:/home/project/draw# kubectl get pvc
```
![image](https://user-images.githubusercontent.com/87048583/131849001-5df0d23b-08e7-4858-a1e7-064b36a548df.png)


#### Pod 로 접속하여 파일시스템 정보를 확인한다. 

```
root@labs-1621740876:/home/project/draw# kubectl get pod
```
![image](https://user-images.githubusercontent.com/87048583/131851919-7995a30f-97aa-47f7-acc2-4e1fc9d1bc31.png)


```
root@labs-1621740876:/home/project/draw# kubectl exec -it draw-7f76f46697-xwz64 -- /bin/sh
```
![image](https://user-images.githubusercontent.com/87048583/131852005-4df6f1bc-9a07-43ae-a3a0-d4a4d8a2dd1a.png)

#### 생성된 Persistence Volume 은 Mount 되지 않은 상태임을 확인한다. 


#### Persistenct Volume 이 Mount 되도록 yaml 설정파일을 변경한다. 

#### draw-deploy-vol.yaml

```
    spec:
      containers:
        - name: draw
          image: 052937454741.dkr.ecr.ap-southeast-2.amazonaws.com/draw
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
...
          volumeMounts:
          - mountPath: "/mnt/aws"
            name: volume
...
      volumes:
        - name: volume
          persistentVolumeClaim:
            claimName: aws-efs
```

#### 변경된 yaml 파일로 서비스 재배포 한다. 

```
root@labs-1621740876:/home/project/draw# kubectl apply -f  draw-deploy-vol.yaml
```
![image](https://user-images.githubusercontent.com/87048583/131855394-c14d701d-55fd-417d-8c35-1493b04aeb26.png)

#### Pod 로 접속하여 파일시스템 정보를 확인한다. 

```
root@labs-1621740876:/home/project/draw# kubectl exec -it draw-648bcdbd5d-q6ckw -- /bin/sh
```
![image](https://user-images.githubusercontent.com/87048583/131855639-66f67be2-cad1-4cb7-a9ff-e386610d4751.png)

#### 생성된 Persistence Volume 이 pod 내 정상 mount 되었음을 확인할 수 있다. 


### 오토스케일 아웃

앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에
이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다.

결제서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다.

설정은 CPU 사용량이 50프로를 넘어서면 replica 를 10개까지 늘려준다
```
root@labs-1621740876:/home/project/draw# kubectl autoscale deployment draw --cpu-percent=50 --min=1 --max=10
```
![image](https://user-images.githubusercontent.com/87048583/131863699-88214261-0b41-4c38-b315-65300d111824.png)
![image](https://user-images.githubusercontent.com/87048583/131863712-b0ea798d-2069-4ab0-b3be-4e4ed5212365.png)

#### 부하 테스트 진행
```
root@siege:/# siege -v -c100 -t90S -r10 --content-type "application/json" 'http://draw:8080/draws POST {"itemNo":"2222","price":"199000","drawDatw":"2021-09-02","size":275,"drawId":"1","drawName":"NikeXSoctt jordan,"userId":"dj80@sk.com"}' ( 동시사용자 100명, 90초간 진행 )
```
![image](https://user-images.githubusercontent.com/87048583/131863908-c413d0f2-6a22-4cf5-9e33-a48605225916.png)

Terminal 을 추가하여 오토스케일링 현황을 모니터링 한다. ( watch kubectl get pod )
부하 테스트 진행전
![image](https://user-images.githubusercontent.com/87048583/131863993-70c85b39-bc9f-48d9-9729-77371ed1ec62.png)
부하 테스트 진행 후
![image](https://user-images.githubusercontent.com/87048583/131864015-cf8d408a-b6e5-4a11-82b7-8487168e16d7.png)
오토 스케일링이 정상적으로 수행되었음을 확인할 수 있다.


### 서킷 브레이킹

1. Spring FeignClient + Hystrix 옵션을 사용하여 구현
2. 요청-인증시 Request/Response 로 연동하여 구현이 되어있으며 요청이 과도할 경우 CB를 통하여 장애격리
3. Hystrix 를 설정: 요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면
  CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정

application.yml
```
feign:
  hystrix:
    enabled: true

hystrix:
  command:
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610
 ```     
4. 인증 서비스의 임의 부하 처리
Auth.java (Entity)
```java
    @PrePersist
    public void onPrePersist(){  

        ...
        
        try {
	    // 인증 데이터 저장 전 처리 시간을 400ms ~ 620ms 강제 지연
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```
5. 부하테스터 seige 툴을 통한 서킷 브레이커 동작 확인
```
root@siege:/#  siege -v -c100 -t90S -r10 --content-type "application/json" 'http://order:8080/orders POST {"itemNo":"1131","price":"100000","size":"275 ","userId":"dj@sk.com","userPassword":"1234"}' ( 동시사용자 100명, 90초간 진행 )
```
![image](https://user-images.githubusercontent.com/87048583/131934042-899b5ee4-12f6-4236-9a9d-a25475b4bd49.png)

![image](https://user-images.githubusercontent.com/87048583/131933941-9ceea64c-685e-4f8b-80bd-1feb656ff814.png)


운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌. 동적 Scale out (replica의 자동적 추가,HPA) 을 통하여 시스템을 확장 해주는 후속처리가 필요.

### Liveness Prove
 Liveness Command probe
/tmp/healthy 파일이 존재하는지 확인

exec-liveness.yaml
```
apiVersion: v1
kind: Pod
metadata:
  labels:
    test: liveness
  name: liveness-exec
spec:
  containers:
  - name: liveness
    image: k8s.gcr.io/busybox
    args:
    - /bin/sh
    - -c
    - touch /tmp/healthy; sleep 30; rm -rf /tmp/healthy; sleep 600
    livenessProbe:
      exec:
        command:
        - cat
        - /tmp/healthy
      initialDelaySeconds: 5
      periodSeconds: 5
```

모든 서비스 delete

![image](https://user-images.githubusercontent.com/87048583/131937128-ca919b44-beb2-4c88-95a3-af917509e547.png)

exec-liveness.yaml 적용

![image](https://user-images.githubusercontent.com/87048583/131937213-e8b2772b-3c57-4352-b479-d4864f783edb.png)

pod 상태 확인 

컨테이너가 Running 상태로 보이나, Liveness Probe 실패로 재시작

![image](https://user-images.githubusercontent.com/87048583/131937281-e600be0e-a8c5-4270-ab23-a0e37df21ab4.png)

kubectl describe로 실패 메시지 확인

![image](https://user-images.githubusercontent.com/87048583/131937348-484690b7-9c7f-4d33-bd06-32a624f8da0a.png)
![image](https://user-images.githubusercontent.com/87048583/131937381-f202ac02-1728-4132-80f4-f4991a69745a.png)

재시작됨 확인

![image](https://user-images.githubusercontent.com/87048583/131937422-dc493e17-155e-416a-9e34-42e60dd6fafd.png)

