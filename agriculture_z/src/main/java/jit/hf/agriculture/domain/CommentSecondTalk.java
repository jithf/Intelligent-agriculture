package jit.hf.agriculture.domain;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
        * Author: zj
        * Description:话题讨论类（回复评论，二级评论）
        */
@Entity
public class CommentSecondTalk {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 唯一标识

    @Size(min=2, max=5000)
    @Column(nullable = false) // 映射为字段，值不能为空
    private String content;//内容

    private String author;

    private String author_picture;

    private Date uptime;

    private Integer likes=0;//点赞量，并初始化

    //点赞（感谢）
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "sys_commentSecondTalk_voteSecondTalk",
            joinColumns = @JoinColumn(
                    name = "commentSecondTalk_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "voteSecondTalk_id", referencedColumnName = "id")
    )
    private List<VoteSecondTalk> voteSecondTalk;  //点赞

    public CommentSecondTalk() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public List<VoteSecondTalk> getVoteSecondTalk() {
        return voteSecondTalk;
    }

    public void setVoteSecondTalk(List<VoteSecondTalk> voteSecondTalk) {
        this.voteSecondTalk = voteSecondTalk;
    }

    @Override
    public String toString() { //便于打印消息
        return String.format( "CommentSecondTalk[id=%d, content='%s'" +
                        "author='%s',author_picture='%s'" +
                        "uptime='%s',likes='%d'" +
                        "voteSecondTalk='%s']",
                id, content,
                author, author_picture,
                uptime, likes,
                voteSecondTalk
        );
    }
}
