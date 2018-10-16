package jit.hf.agriculture.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: zj
 * Description:任务广场的话题实体类
 */
@Entity
public class Talk implements Serializable {

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id;//主键，唯一标识

    private String theme;//话题

    private String description;//话题描述

    private String tags;  // 标签  以 , 隔开

    private String author;//话题发布者

    private String author_picture;//话题作者头像

    private Date uptime;//发布话题时间

    private String pictureName;//图片名称

    private String pictureUrl;//图片地址

    private Integer talkValues=50;//本话题价值的积分值,默认50


    private Integer collections=0;//关注量

    private Integer clicks=0;//浏览量

    private Integer comments=0;//评论量，并初始化

    private Integer acceptationNumbers=1;//用来判断，话题发布者是否已采纳过建议

    @OneToMany(cascade = {CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinTable(
            name = "sys_talk_commentTalk",
            joinColumns = @JoinColumn(
                    name = "talk_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "commentTalk_id",referencedColumnName = "id"
            )
    )
    private List<CommentTalk> commentTalk;//评论

    @OneToMany(cascade = {CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinTable(
            name = "sys_talk_attentionTalk",
            joinColumns = @JoinColumn(
                    name = "talk_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "attentionTalk_id",referencedColumnName = "id"
            )
    )
    private List<AttentionTalk> attentionTalks;//关注


    public Talk() { //new Talk 时，构造函数
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getDiscussions() {
        return comments;
    }

    public void setDiscussions(Integer discussions) {
        this.comments = discussions;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<CommentTalk> getCommentTalk() {
        return commentTalk;
    }

    public void setCommentTalk(List<CommentTalk> commentTalk) {
        this.commentTalk = commentTalk;
    }

    public Integer getCollections() {
        return collections;
    }

    public void setCollections(Integer collections) {
        this.collections = collections;
    }

    public Integer getClicks() {
        return clicks;
    }

    public void setClicks(Integer clicks) {
        this.clicks = clicks;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getTalkValues() {
        return talkValues;
    }

    public void setTalkValues(Integer talkValues) {
        this.talkValues = talkValues;
    }

    public Integer getAcceptationNumbers() {
        return acceptationNumbers;
    }

    public void setAcceptationNumbers(Integer acceptationNumbers) {
        this.acceptationNumbers = acceptationNumbers;
    }

    public List<AttentionTalk> getAttentionTalks() {
        return attentionTalks;
    }

    public void setAttentionTalks(List<AttentionTalk> attentionTalks) {
        this.attentionTalks = attentionTalks;
    }

    //    @Override
//    public String toString() { //便于打印消息
//        return String.format("Talk[id=%d, theme='%s'"+
//                        "author='%s',author_picture='%s'" +
//                        "uptime='%s',collections='%d',comments='%d'," +
//                       "tags='%s',commentTalk='%s']",
//                id,theme,
//                author,author_picture,
//                uptime,collections,comments,
//                tags, commentTalk
//                );
//    }

}
