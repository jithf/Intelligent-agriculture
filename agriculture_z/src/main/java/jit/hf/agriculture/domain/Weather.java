package jit.hf.agriculture.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Author: zj
 * Description:天气中农业信息类
 */
@Entity
public class Weather implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id;

    private Double ph1;//测ph值，传感器1    pha_new

    private Double ph2;//传感器2    ph2

    private Double ph3;//传感器3    ph7

    private Double ph4;//传感器4    ph8

    private Double ph5;//传感器5    ph9

    private Double do1;//测溶解氧，传感器1    do1

    private Double do2;//传感器2    do3

    private Double do3;//传感器3    do4_new

    private Double do4;//传感器4    do5

    private Double do5;//传感器5    do6

    private Double temp1;//测水温，传感器1    temp2

    private Double temp2;//传感器2    temp3

    private Double temp3;//传感器3    temp4

    private Double temp4;//传感器4    temp5

    private Double temp5;//传感器5    temp6

    private String upTime;//获取数据的时间 time

    public Weather() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPh1() {
        return ph1;
    }

    public void setPh1(Double ph1) {
        this.ph1 = ph1;
    }

    public Double getPh2() {
        return ph2;
    }

    public void setPh2(Double ph2) {
        this.ph2 = ph2;
    }

    public Double getPh3() {
        return ph3;
    }

    public void setPh3(Double ph3) {
        this.ph3 = ph3;
    }

    public Double getPh4() {
        return ph4;
    }

    public void setPh4(Double ph4) {
        this.ph4 = ph4;
    }

    public Double getPh5() {
        return ph5;
    }

    public void setPh5(Double ph5) {
        this.ph5 = ph5;
    }

    public Double getDo1() {
        return do1;
    }

    public void setDo1(Double do1) {
        this.do1 = do1;
    }

    public Double getDo2() {
        return do2;
    }

    public void setDo2(Double do2) {
        this.do2 = do2;
    }

    public Double getDo3() {
        return do3;
    }

    public void setDo3(Double do3) {
        this.do3 = do3;
    }

    public Double getDo4() {
        return do4;
    }

    public void setDo4(Double do4) {
        this.do4 = do4;
    }

    public Double getDo5() {
        return do5;
    }

    public void setDo5(Double do5) {
        this.do5 = do5;
    }

    public Double getTemp1() {
        return temp1;
    }

    public void setTemp1(Double temp1) {
        this.temp1 = temp1;
    }

    public Double getTemp2() {
        return temp2;
    }

    public void setTemp2(Double temp2) {
        this.temp2 = temp2;
    }

    public Double getTemp3() {
        return temp3;
    }

    public void setTemp3(Double temp3) {
        this.temp3 = temp3;
    }

    public Double getTemp4() {
        return temp4;
    }

    public void setTemp4(Double temp4) {
        this.temp4 = temp4;
    }

    public Double getTemp5() {
        return temp5;
    }

    public void setTemp5(Double temp5) {
        this.temp5 = temp5;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }
}
