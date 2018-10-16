package jit.hf.agriculture.vo;

import jit.hf.agriculture.domain.Video;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 上午9:48 18-6-1
 **/
public class VideoUtil implements Serializable {

    private Long id;//主键，唯一标识

    private String title;//视频名称

    private String avater;//视频存放地址

    private String author;//视频作者

    private String author_picture;//视频作者头像

    private String picture;//视频截图

    private String description;//视频描述

    private Timestamp uptime;//上传时间

    private Integer clicks=0;//播放量，初始化

    private Integer likes=0;//点赞量

    private Integer comments=0;//评论量

    private Integer collections=0; //收藏量

    private boolean examination=false;//审核状态

    private String tags;  // 标签  以 , 隔开

    public VideoUtil() {

    }

    public VideoUtil(Video video) {
        this.id=video.getId();
        this.author=video.getAuthor();
        this.author_picture=video.getAuthor_picture();
        this.tags=video.getTags();
        this.clicks=video.getClicks();
        this.avater=video.getAvater();
        this.description=video.getDescription();
        this.examination=video.getExamination();
        this.collections=video.getCollections();
        this.title=video.getTitle();
        this.uptime=video.getUptime();
        this.likes=video.getLikes();
        this.picture=video.getPicture();
        this.comments=video.getComments();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvater() {
        return avater;
    }

    public void setAvater(String avater) {
        this.avater = avater;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getUptime() {
        return uptime;
    }

    public void setUptime(Timestamp uptime) {
        this.uptime = uptime;
    }

    public Integer getClicks() {
        return clicks;
    }

    public void setClicks(Integer clicks) {
        this.clicks = clicks;
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

    public Integer getCollections() {
        return collections;
    }

    public void setCollections(Integer collections) {
        this.collections = collections;
    }

    public boolean isExamination() {
        return examination;
    }

    public void setExamination(boolean examination) {
        this.examination = examination;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

}

