package jit.hf.agriculture.vo;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午8:33 18-6-3
 **/
public class DataUtil {
    private Long total;
    private Integer aquaculture;
    private Integer cultivation;
    private Integer grow;
    private Long others;

    public DataUtil() {

    }

    public Integer getAquaculture() {
        return aquaculture;
    }

    public void setAquaculture(Integer aquaculture) {
        this.aquaculture = aquaculture;
    }

    public Integer getCultivation() {
        return cultivation;
    }

    public void setCultivation(Integer cultivation) {
        this.cultivation = cultivation;
    }

    public Integer getGrow() {
        return grow;
    }

    public void setGrow(Integer grow) {
        this.grow = grow;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getOthers() {
        return others;
    }

    public void setOthers(Long others) {
        this.others = others;
    }
}
