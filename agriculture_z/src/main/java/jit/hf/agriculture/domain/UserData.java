package jit.hf.agriculture.domain;

import javax.persistence.*;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午4:55 18-6-3
 **/
@Entity
public class UserData {

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 唯一标识

    @Column(unique = true)
    private String date; //建立时间

    private Integer numbers=0; //每日登录的用户数量

    private String users="users";

    private Integer docDownLoad=0;

    private Integer videoClicks=0;

    private Integer videoComments=0;

    private Integer talkComments=0;

    private Integer choiceClicks=0;

    public UserData() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumbers() {
        return numbers;
    }

    public void setNumbers(Integer numbers) {
        this.numbers = numbers;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getDocDownLoad() {
        return docDownLoad;
    }

    public void setDocDownLoad(Integer docdownLoads) {
        this.docDownLoad = docdownLoads;
    }

    public Integer getVideoClicks() {
        return videoClicks;
    }

    public void setVideoClicks(Integer videoClicks) {
        this.videoClicks = videoClicks;
    }

    public Integer getVideoComments() {
        return videoComments;
    }

    public void setVideoComments(Integer videoComments) {
        this.videoComments = videoComments;
    }

    public Integer getTalkComments() {
        return talkComments;
    }

    public void setTalkComments(Integer talkComments) {
        this.talkComments = talkComments;
    }

    public Integer getChoiceClicks() {
        return choiceClicks;
    }

    public void setChoiceClicks(Integer choiceClicks) {
        this.choiceClicks = choiceClicks;
    }
}
