package jit.hf.agriculture.domain;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Author: jit.hf
 * Description:点赞
 * Date: Created in 下午3:08 18-4-18
 **/
@Entity // 实体
public class Vote {

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false) // 映射为字段，值不能为空
    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    private Timestamp createTime;

    protected Vote() { //构造器
    }

    public Vote(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    @Override
    public String toString() { //便于打印消息
        return String.format( "Vote[id='%d', user='%s', createTime='%s']",
                id, user, createTime );

    }
}