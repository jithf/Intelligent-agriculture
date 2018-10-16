package jit.hf.agriculture.domain;

import jit.hf.agriculture.domain.User;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;


/**
 * Author: jit.hf
 * Description:评论实体类
 * Date: Created in 下午3:13 18-4-17
 **/
@Entity // 实体
public class Comment {

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @NotEmpty(message = "评论内容不能为空")
    @Size(min=1, max=500)
    @Column(nullable = false) // 映射为字段，值不能为空
    private String content;

    @Column(name = "video_id")
    private Long videoId;

    @Column(name = "pid",nullable = false)
    private Long pid=0L;  //父评论，如果不设置，默认为0

    @Column(name = "ppid",nullable = false)
    private Long ppid=0L;  //祖评论，如果不设置，默认为0

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name="user")
    private UserInformation userInfo;

    @Column(nullable = false) // 映射为字段，值不能为空
    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    private Timestamp createTime;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="replayComment",referencedColumnName = "id")
    @OrderBy("id ASC")
    private List<Comment> replayCommentList;

    @JoinColumn(name = "replay_user")
    @OneToOne(fetch = FetchType.EAGER)
    private UserInformation replayUser;

    protected Comment() {
    }

    public Comment(User user, String content) {
        this.content = content;
        UserInformation userInformation=new UserInformation(user);
        this.userInfo=userInformation;
    }

    public Comment(User user,Long pid,String content) {
        UserInformation userInformation=new UserInformation(user);
        this.userInfo=userInformation;
        this.pid=pid;
        this.content=content;
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

    public UserInformation getUserInfo() {
        return userInfo;
    }

    public void setUserInfo (UserInformation userInfo) {
        this.userInfo = userInfo;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public List<Comment> getReplayCommentList() {
        return replayCommentList;
    }

    public void setReplayCommentList(List<Comment> replayCommentList) {
        this.replayCommentList = replayCommentList;
    }

    public UserInformation getReplayUser() {
        return replayUser;
    }

    public void setReplayUser(UserInformation replayUser) {
        this.replayUser = replayUser;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    //添加评论
    public void addReplayComment(Comment comment) {
        this.replayCommentList.add(comment);
    }


    public Long getPpid() {
        return ppid;
    }

    public void setPpid(Long ppid) {
        this.ppid = ppid;
    }
    @Override
    public String toString() { //便于打印消息
        return String.format( "Comment[id='%d', content='%s', user='%s',createTime='%s']",
                id, content,userInfo,createTime );

    }

}
