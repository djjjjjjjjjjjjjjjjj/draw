package draw;

public class RaffleDropped extends AbstractEvent {

    private Long id;
    private Integer itemNo;
    private Date raffleDate;
    private String userId;
    private Boolean win;
    private Integer drawid;

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
    public Date getRaffleDate() {
        return raffleDate;
    }

    public void setRaffleDate(Date raffleDate) {
        this.raffleDate = raffleDate;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Boolean getWin() {
        return win;
    }

    public void setWin(Boolean win) {
        this.win = win;
    }
    public Integer getDrawid() {
        return drawid;
    }

    public void setDrawid(Integer drawid) {
        this.drawid = drawid;
    }
}