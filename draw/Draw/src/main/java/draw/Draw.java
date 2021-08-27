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

    @PostPersist
    public void onPostPersist(){
        DrawRequested drawRequested = new DrawRequested();
        BeanUtils.copyProperties(this, drawRequested);
        drawRequested.publishAfterCommit();

        DrawChecked drawChecked = new DrawChecked();
        BeanUtils.copyProperties(this, drawChecked);
        drawChecked.publishAfterCommit();

    }
    @PostRemove
    public void onPostRemove(){
        DrawCanceled drawCanceled = new DrawCanceled();
        BeanUtils.copyProperties(this, drawCanceled);
        drawCanceled.publishAfterCommit();

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




}