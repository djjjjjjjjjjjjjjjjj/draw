package draw;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Entity
@Table(name="Auth_table")
public class Auth {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String regid;
    private String regName;
    private String regPassword;
    private Integer itemNo;
    private String regDate;
	private Long orderRequestId ;
    private String orderRequestName;
    private String userId;
    private String userName;
    private String userPassword;
    
    // @PostPersist
    // public void onPostPersist(){
    @PrePersist
    public void onPrePersist(){
    

        String userId = "dj@sk.com";
        String userName = "dj";
        String userPassword = "1234";
        boolean authResult = false ;

        if( userId.equals( getUserId() ) && userPassword.equals( getUserPassword() ) ){
            authResult = true ;
        }

        System.out.println("\nAuthentication.Auth.39\n###############################################");
        System.out.println("authResult : " + authResult) ;
        System.out.println("orderRequestId : " + getOrderRequestId()) ;
        System.out.println("requestName : " + getOrderRequestName()) ;
        System.out.println("###############################################\n");

        if( authResult == false ){
            AuthCancelled authCancelled = new AuthCancelled();
            BeanUtils.copyProperties(this, authCancelled);
            authCancelled.publish();

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }
    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }
    public String getRegPassword() {
        return regPassword;
    }

    public void setRegPassword(String regPassword) {
        this.regPassword = regPassword;
    }
    public Integer getItemNo() {
        return itemNo;
    }

    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }
    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return this.userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

    public Long getOrderRequestId() {
		return this.orderRequestId;
	}
	public void setOrderRequestId(Long orderRequestId) {
		this.orderRequestId = orderRequestId;
	}

    public String getOrderRequestName() {
		return this.orderRequestName;
	}
	public void setOrderRequestName(String orderRequestName) {
		this.orderRequestName = orderRequestName;
	}


   


}