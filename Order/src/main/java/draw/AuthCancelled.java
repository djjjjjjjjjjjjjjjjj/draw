package draw;

public class AuthCancelled extends AbstractEvent {

    private Long id;
    private String regid;
    private String regName;
    private String regPassword;
    private String itemNo;
    private String regDate;
	private Long orderRequestId ;
    private String orderRequestName;
    private String userId;
    private String userName;
    private String userPassword;

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
    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
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