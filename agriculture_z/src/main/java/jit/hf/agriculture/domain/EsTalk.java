package jit.hf.agriculture.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: zj
 * Description:话题的文本库
 **/


@Document(indexName = "talk", type = "talk")//indexName是文本库的名字，自己定义
@XmlRootElement // MediaType 转为 XML
public class EsTalk implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id  // 主键
    private Long id;//这里不必用自增策略

    private String theme;//话题

    private String tags;  // 标签  以 , 隔开

    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)// 以下，不做全文检索字段
    private String author;//话题发布者
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)
    private String author_picture;//话题作者头像
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)
    private Date uptime;//发布话题时间
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)
    private Integer collections=0;//关注量
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)
    private Integer clicks=0;//浏览量
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)
    private Integer comments=0;//评论量，并初始化

    protected EsTalk() {  // JPA 的规范要求无参构造函数;protected防止直接调用
    }

    public EsTalk(Talk talk){ //新建
        this.id = talk.getId();
        this.theme = talk.getTheme();
        this.tags = talk.getTags();
        this.author = talk.getAuthor();
        this.author_picture = talk.getAuthor_picture();
        this.uptime = talk.getUptime();
        this.collections = talk.getCollections();
        this.clicks = talk.getClicks();
        this.comments = talk.getComments();
    }

    public void update(Talk talk){ //更新
        this.id = talk.getId();
        this.theme = talk.getTheme();
        this.tags = talk.getTags();
        this.author = talk.getAuthor();
        this.author_picture = talk.getAuthor_picture();
        this.uptime = talk.getUptime();
        this.collections = talk.getCollections();
        this.clicks = talk.getClicks();
        this.comments = talk.getComments();
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
}
