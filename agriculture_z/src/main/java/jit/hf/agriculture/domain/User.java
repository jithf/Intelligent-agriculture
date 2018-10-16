package jit.hf.agriculture.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Date;
/**
 * Author: jit.hf ,zj
 * Description: User 实体类
 * Date:Created in 下午7:06 18-3-22
 **/

@Entity // 实体
public class User implements Serializable,UserDetails {

    private static final long serialVersionUID = 1L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    //@Column(nullable = false, length = 20, unique = true) 用户名唯一在发送验证码时，判断
    private String username; // 用户账号，用户登录时的唯一标识

    @Size(max = 100)
    @Column(length = 100)
    private String password; // 登录时密码

    private String verityCode; //验证码

    private String nickname="热心网友";//昵称

    private String avater="./src/main/webapp/default.jpg";//头像信息(头像的地址)

    private Date lastPasswordResetDate;//最新一次恢复token值的时间

    private Integer userTalkValues=500;//积分值

    private String permission; //申请变更的权限

    private String audit="审核通过";

    @ManyToMany(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER) //这里一定要立即加载，不然会报错：org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role。。。。
    // 因为在获取一方的时候一方是获取到了，但是再获取多方的时候session已经关闭了，这时候会获取不到多方信息，因此报错。
    @JoinTable(
            name = "sys_user_role",
            joinColumns = @JoinColumn(
                    name = "user_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",referencedColumnName = "id"
            )
    )
    private List<SysRole> roles;

    @ManyToMany(cascade = {CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinTable(
            name = "wrongChoice",
            joinColumns = @JoinColumn(
                    name = "user_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "choice_id",referencedColumnName = "id"
            )
    )
    private List<Choice> choices;


    private String videos="collectionVideos";

    public User() { // JPA 的规范要求无参构造函数；设为 protected 防止直接使用
    }

    public User(String username, String password) {//构造器，将类具体化
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVerityCode() {
        return verityCode;
    }

    public void setVerityCode(String verityCode) {
        this.verityCode = verityCode;
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

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
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
        for(SysRole sysRole:roles) {
            this.permission=sysRole.getName();
            break;
        }
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public String getVideos() {
        return videos;
    }

    public void setVideos(String videos) {
        this.videos = videos;
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

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auths = new ArrayList<>();
        List<SysRole> roles = this.getRoles();
        for (SysRole role : roles) {
            auths.add(new SimpleGrantedAuthority(role.getName()));
        }
        return auths;
    }

    @Override
    public String toString() { //便于打印消息
        return String.format("User[id=%d, username='%s', password='%s'," +
                        "nickname='%s'，avatar='%s'，lastPasswordResetDate='%s'，github='%s']",
                id, username, password, nickname, avater,lastPasswordResetDate);
    }

}

