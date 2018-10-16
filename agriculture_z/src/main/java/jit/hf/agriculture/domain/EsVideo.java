package jit.hf.agriculture.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 上午10:27 18-4-23
 **/
@Document(indexName = "video", type = "video")
@XmlRootElement // MediaType 转为 XML
public class EsVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id  // 主键
    private Long id;

    private String title;

    private String description;

    private String author;

    private String tags;

    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)
    private Long videoId; // video 的 id
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)  // 不做全文检索字段
    private String avater;
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)  // 不做全文检索字段
    private String picture;
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)  // 不做全文检索字段
    private Timestamp uptime;
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)  // 不做全文检索字段
    private Integer clicks = 0; // 访问量、阅读量
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)  // 不做全文检索字段
    private Integer comments= 0;  // 评论量
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)  // 不做全文检索字段
    private Integer likes = 0;  // 点赞量
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)  // 不做全文检索字段
    private Boolean examination;  // 标签

    protected EsVideo() {  // JPA 的规范要求无参构造函数；设为 protected 防止直接使用
    }

    public EsVideo(String title,String description,String author,String tags) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.tags=tags;
    }

    public EsVideo(Long videoId, String title, String description, String author, String tags, String avater,String picture,Timestamp uptime,
                  Integer clicks,Integer comments, Integer likes,Boolean examination) {
        this.id=videoId;
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.author = author;
        this.tags = tags;
        this.avater = avater;
        this.picture = picture;
        this.uptime = uptime;
        this.clicks = clicks;
        this.comments = comments;
        this.likes = likes;
        this.examination = examination;
    }

    public EsVideo(Video video){
        this.id=video.getId();
        this.videoId = video.getId();
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.author = video.getAuthor();
        this.tags = video.getTags();
        this.avater = video.getAvater();
        this.picture = video.getPicture();
        this.uptime = video.getUptime();
        this.clicks = video.getClicks();
        this.comments = video.getComments();
        this.likes = video.getLikes();
        this.examination = video.getExamination();
    }

    public void update(Video video){
        this.id=video.getId();
        this.videoId = video.getId();
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.author = video.getAuthor();
        this.tags = video.getTags();
        this.avater = video.getAvater();
        this.picture = video.getPicture();
        this.uptime = video.getUptime();
        this.clicks = video.getClicks();
        this.comments = video.getComments();
        this.likes = video.getLikes();
        this.examination = video.getExamination();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAvater() {
        return avater;
    }

    public void setAvater(String avater) {
        this.avater = avater;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Boolean getExamination() {
        return examination;
    }

    public void setExamination(Boolean examination) {
        this.examination = examination;
    }

    @Override
    public String toString() {
        return String.format(
                "esVideo[id=%d, title='%s', description='%s']",
                videoId, title, description);
    }
}