package jit.hf.agriculture.vo;

import jit.hf.agriculture.domain.Talk;

import java.util.Date;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午11:31 18-5-31
 **/
public class TalkUtil {
    private Long id;//主键，唯一标识

    private String theme;//话题

    private String description;//话题描述

    private String tags;  // 标签  以 , 隔开

    private String author;//话题发布者

    private String author_picture;//话题作者头像

    private Date uptime;//发布话题时间

    private Integer collections=0;//关注量

    private Integer clicks=0;//浏览量

    private Integer comments=0;//评论量，并初始化

    private Integer talkValues=50;//本话题价值的积分值,默认50

    private Integer acceptationNumbers=1;//用来判断，话题发布者是否已采纳过建议

    public TalkUtil() { //new Talk 时，构造函数
    }

    public TalkUtil(Talk talk) {
        this.id=talk.getId();
        this.acceptationNumbers=talk.getAcceptationNumbers();
        this.author=talk.getAuthor();
        this.author_picture=talk.getAuthor_picture();
        this.clicks=talk.getClicks();
        this.collections=talk.getCollections();
        this.comments = talk.getComments();
        this.description=talk.getDescription();
        this.theme=talk.getTheme();
        this.tags=talk.getTags();
        this.talkValues=talk.getTalkValues();
        this.uptime=talk.getUptime();
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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
}
