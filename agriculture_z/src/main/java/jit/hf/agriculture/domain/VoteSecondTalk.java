package jit.hf.agriculture.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Author: zj
 * Description:二级评论的点赞类
 */
@Entity
public class VoteSecondTalk {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 唯一标识

    private String author;//点赞者

    private String author_picture;//点赞者的头像

    private Date uptime;//点赞的时间

    public VoteSecondTalk() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor_picture() {
        return author_picture;
    }

    public void setAuthor_picture(String author_picture) {
        this.author_picture = author_picture;
    }

    public Date getUptime() {
        return uptime;
    }

    public void setUptime(Date uptime) {
        this.uptime = uptime;
    }

    @Override
    public String toString() { //便于打印消息
        return String.format( "VoteTalk[id='%d', author='%s', " +
                        "author_picture='%s',uptime='%s']",
                id, author,
                author_picture,uptime);

    }

}
