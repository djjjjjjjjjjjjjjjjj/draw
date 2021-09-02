package draw;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Raffle_table")
public class Raffle {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Integer itemNo;
    private Date raffleDate;
    private String userId;
    private Boolean win;

    @PostPersist
    public void onPostPersist(){
        RaffleRequested raffleRequested = new RaffleRequested();
        BeanUtils.copyProperties(this, raffleRequested);
        raffleRequested.publishAfterCommit();

        RaffleDropped raffleDropped = new RaffleDropped();
        BeanUtils.copyProperties(this, raffleDropped);
        raffleDropped.publishAfterCommit();

        ResultConfirmed resultConfirmed = new ResultConfirmed();
        BeanUtils.copyProperties(this, resultConfirmed);
        resultConfirmed.publishAfterCommit();

    }

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




}