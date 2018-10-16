package jit.hf.agriculture.vo;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午8:49 18-6-3
 **/
public class UserDataUtil {

    private Long total;
    private Integer ROLE_ADMIN;
    private Integer ROLE_USER;


    public UserDataUtil() {

    }

    public Integer getROLE_ADMIN() {
        return ROLE_ADMIN;
    }

    public void setROLE_ADMIN(Integer ROLE_ADMIN) {
        this.ROLE_ADMIN = ROLE_ADMIN;
    }

    public Integer getROLE_USER() {
        return ROLE_USER;
    }

    public void setROLE_USER(Integer ROLE_USER) {
        this.ROLE_USER = ROLE_USER;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
