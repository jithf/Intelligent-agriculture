package jit.hf.agriculture.vo;

import jit.hf.agriculture.domain.SysRole;
import jit.hf.agriculture.domain.User;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 上午11:21 18-5-31
 **/
public class UserUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id; // 用户的唯一标识

    //@Column(nullable = false, length = 20, unique = true) 用户名唯一在发送验证码时，判断
    private String username; // 用户账号，用户登录时的唯一标识

    private String nickname="热心网友";//昵称

    private String avater="./src/main/webapp/default.jpg";//头像信息(头像的地址)

    private Integer userTalkValues=500;//积分值

    private List<SysRole> roles;

    private String permission; //申请变更的权限

    private String audit="审核通过";

    public UserUtil() { // JPA 的规范要求无参构造函数；设为 protected 防止直接使用
    }

    public UserUtil(User user) {//构造器，将类具体化
        this.username = user.getUsername();
        this.id=user.getId();
        this.avater=user.getAvater();
        this.nickname=user.getNickname();
        this.roles=user.getRoles();
        this.userTalkValues=user.getUserTalkValues();
        this.permission=user.getPermission();
        this.audit=user.getAudit();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvater() {
        return avater;
    }

    public void setAvater(String avater) {
        this.avater = avater;
    }

    public Integer getUserTalkValues() {
        return userTalkValues;
    }

    public void setUserTalkValues(Integer userTalkValues) {
        this.userTalkValues = userTalkValues;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getAudit() {
        return audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }
}
