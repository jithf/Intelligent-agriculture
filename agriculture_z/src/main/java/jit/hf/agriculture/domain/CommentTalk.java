package jit.hf.agriculture.domain;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * Author: zj
 * Description:话题讨论类（一级评论）
 */
@Entity
public class CommentTalk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 评论的唯一标识

    @Size(min=2, max=5000)
    @Column(nullable = false) // 映射为字段，值不能为空
    private String content;//内容

    private String author;

    private String author_picture;

    private Date uptime;

    private Integer collections=0;//收藏量

    private Integer likes=0;//点赞量，并初始化

    private Integer comments=0;//评论量，并初始化

    private boolean acceptance=false; //是否已采纳

    //点赞（感谢）
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "sys_commentTalk_voteTalk",
            joinColumns = @JoinColumn(
                    name = "commentTalk_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "voteTalk_id", referencedColumnName = "id")
    )
    private List<VoteTalk> voteTalk;  //点赞

    //二级评论

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "sys_commentTalk_commentSecondTalk",
            joinColumns = @JoinColumn(
                    name = "commentTalk_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "commentSecondTalk_id", referencedColumnName = "id")
    )
    private List<CommentSecondTalk> commentSecondTalk;  //

    public CommentTalk() {

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

    public Integer getCollections() {
        return collections;
    }

    public void setCollections(Integer collection) {
        this.collections = collections;
    }

    public Integer getGratitudes() {
        return likes;
    }

    public void setGratitudes(Integer gratitudes) {
        this.likes = gratitudes;
    }

    public List<VoteTalk> getVoteTalk() {
        return voteTalk;
    }

    public void setVoteTalk(List<VoteTalk> voteTalk) {
        this.voteTalk = voteTalk;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public List<CommentSecondTalk> getCommentSecondTalk() {
        return commentSecondTalk;
    }

    public void setCommentSecondTalk(List<CommentSecondTalk> commentSecondTalk) {
        this.commentSecondTalk = commentSecondTalk;
    }

    public boolean getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(boolean acceptance) {
        this.acceptance = acceptance;
    }
    //    @Override
//    public String toString() { //便于打印消息
//        return String.format( "CommentTalk[id=%d, content='%s'" +
//                        "author='%s',author_picture='%s'" +
//                        "uptime='%s',collections='%d',likes='%d'" +
//                        "voteTalk='%s',comments='%s']",
//                id, content,
//                author, author_picture,
//                uptime, collections, likes,
//                voteTalk,comments
//        );
//    }

}


