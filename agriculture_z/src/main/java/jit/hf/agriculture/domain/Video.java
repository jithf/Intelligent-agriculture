package jit.hf.agriculture.domain;



import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
/**
 * Author: zj
 * Description:视频类
 */
@Entity
public class Video implements Serializable {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id;//主键，唯一标识

    private String title;//视频名称

    private String avater;//视频存放地址

    private String author;//视频作者

    private String author_picture;//视频作者头像

    private String picture;//视频截图

    private String description;//视频描述

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    private Timestamp uptime;//上传时间

    private Integer clicks=0;//播放量，初始化

    private Integer likes=0;//点赞量

    private Integer comments=0;//评论量

    private Integer collections=0; //收藏量

    @Column(name = "examination")
    private boolean examination=false;//审核状态

    @Column(name="tags")
    private String tags;  // 标签  以 , 隔开

    //点赞
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "video_vote", joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "vote_id", referencedColumnName = "id"))
    private List<Vote> votes;  //点赞

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "video_comment", joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id"))
    @OrderBy("id DESC")
    @Where(clause = "pid=0")
    private List<Comment> comment=new ArrayList<Comment>();//评论

    @Column
    private String collectors="collector";

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

    public void setDescription(String descript) {
        this.description = descript;
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

    public boolean getExamination() {
        return examination;
    }

    public void setExamination(boolean examination) {
        this.examination = examination;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
        this.likes = this.votes.size();//点赞量
    }

    public String getCollectors() {
        return collectors;
    }

    public void setCollectors(String collectors) {
        this.collectors = collectors;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }


    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getCollections() {
        return collections;
    }

    public void setCollections(Integer collections) {
        this.collections = collections;
    }

    //点赞
    public boolean addVote(Vote vote) {
        boolean isExist = false;
        // 判断重复
        for (int index=0; index < this.votes.size(); index ++ ) {
            if (this.votes.get(index).getUser().getId() == vote.getUser().getId()) {
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            this.votes.add(vote);
            this.likes = this.votes.size();
        }

        return isExist;
    }

    //取消点赞
    public void removeVote(Long voteId) {
        for (int index=0; index < this.votes.size(); index ++ ) {
            if (this.votes.get(index).getId() == voteId) {
                this.votes.remove(index);
                break;
            }
        }

        this.likes = this.votes.size();
    }

    //判断是否已点赞
    public boolean elseVote(Vote vote) {
        boolean isExist = false;
        // 判断重复
        for (int index=0; index < this.votes.size(); index ++ ) {
            if (this.votes.get(index).getUser().getId() == vote.getUser().getId()) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    //查找点赞
    public Vote selectVote(Vote vote) {
        for (int index=0; index < this.votes.size(); index ++ ) {
            if (this.votes.get(index).getUser().getId() == vote.getUser().getId()) {
                return this.votes.get(index);
            }
        }
        return null;
    }

    //添加评论
    public void addComment(Comment comment) {
        this.comment.add(comment);
        this.comments = this.comment.size();
    }

    //删除评论
    public void removeComment(Long commentId) {
        for (int index=0; index < this.comment.size(); index ++ ) {
            if (comment.get(index).getId() == commentId) {
                this.comment.remove(index);
                break;
            }
        }
        this.comments = this.comment.size();
    }


    public Video() { // JPA 的规范要求无参构造函数；设为 protected 防止直接使用
    }

    public Video(String title,String avater, String description, String author) {//构造器，将类具体化
        this.title = title;
        this.avater= avater;
        this.description = description;
        this.author = author;
    }

    @Override
    public String toString() { //便于打印消息
        return String.format("Video[id=%d, title='%s', avater='%s'," +
                        "author='%s',author_picture='%s',picture='%s',description='%s'，" +
                        "uptime='%s',clicks='%d',likes='%d'," +
                        "comments='%d'，examination='%s',votes='%s'，comment='%s']",
                id, title, avater,
                author,author_picture ,picture, description,
                uptime,clicks,likes,
                comments,examination,votes,comment);
    }


}
