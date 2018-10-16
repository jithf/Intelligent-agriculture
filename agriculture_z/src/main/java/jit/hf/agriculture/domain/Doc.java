package jit.hf.agriculture.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: zj
 * Description:文件实体类
 */
@Entity
public class Doc implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id;

    private String title;

    private String avatar;//文档存放地址

    private String author;//文档上传者

    private String author_picture;//文档上传者头像

    private Date uptime;//上传文档时间

    private String description;//文档描述简介

    private Integer downloads=0;//下载量

    private Long fileSize;


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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Doc() { // JPA 的规范要求无参构造函数；设为 protected 防止直接使用

    }
    public Doc(String title, String description) {//构造器，将类具体化
        this.title = title;
        this.description = description;
    }

    @Override
    public String toString() { //便于打印消息
        return String.format("Doc[id=%d, title='%s', avatar='%s'," +
                        "author='%s',author_picture='%s',description='%s'，" +
                        "uptime='%s',downloads='%d']",
                id, title, avatar,
                author,author_picture,description,
                uptime,downloads);
    }

}
