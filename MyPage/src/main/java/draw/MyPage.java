package draw;

import javax.persistence.*;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="MyPage_table")
public class MyPage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private String userId;
        private String grade;
        private Integer win;
        private Integer loss;
        private Integer itemNo;
        private Date drawDate;
        private Boolean drawResult;
        private String drawStatus;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
       

        public String getGrade() {
            return grade;
        }
        public void setGrade(String grade) {
            this.grade = grade;
        }
        
        public Integer getWin() {
            return win;
        }

        public void setWin(Integer win) {
            this.win = win;
        }
        public Integer getLoss() {
            return loss;
        }

        public void setLoss(Integer loss) {
            this.loss = loss;
        }
        public Integer getItemNo() {
            return itemNo;
        }

        public void setItemNo(Integer itemNo) {
            this.itemNo = itemNo;
        }
        public Date getDrawDate() {
            return drawDate;
        }

        public void setDrawDate(Date drawDate) {
            this.drawDate = drawDate;
        }
        public Boolean getDrawResult() {
            return drawResult;
        }

        public void setDrawResult(Boolean drawResult) {
            this.drawResult = drawResult;
        }

        public String getDrawStatus() {
            return this.drawStatus;
        }
        public void setDrawStatus(String drawStatus) {
            this.drawStatus = drawStatus;
        }
}