package draw;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="MyPage_table")
public class MyPage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private String name;
        private String addr;
        private String grade;
        private Integer win;
        private Integer loss;
        private Integer drawId;
        private Date drawDate;
        private Boolean drawResult;


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
        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
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
        public Integer getDrawId() {
            return drawId;
        }

        public void setDrawId(Integer drawId) {
            this.drawId = drawId;
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

}