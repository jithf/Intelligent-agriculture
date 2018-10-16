package jit.hf.agriculture.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午3:00 18-5-8
 **/
@Entity
public class Choice implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @Column(name = "title")
    private String title;

    @Column(name = "A")
    private String A;

    @Column(name = "B")
    private String B;

    @Column(name = "C")
    private String C;

    @Column(name = "answer")
    private String answer;

    @Column(name = "analysis")
    private String analysis;

    @Column(name = "type")
    private String type;

    @Column
    private String wrongUserId="wrong";

    @Column
    private String uploadUsername="初始题目";

    protected Choice() {}

    public Choice(String title,String A,String B,String C,String answer,String analysis,String type) {
        this.title=title;
        this.A=A;
        this.B=B;
        this.C=C;
        this.answer=answer;
        this.analysis=analysis;
        this.type=type;
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

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWrongUserId() {
        return wrongUserId;
    }

    public void setWrongUserId(String wrongUserId) {
        this.wrongUserId = wrongUserId;
    }

    public String getUploadUsername() {
        return uploadUsername;
    }

    public void setUploadUsername(String uploadUsername) {
        this.uploadUsername = uploadUsername;
    }

    @Override
    public String toString() { //便于打印消息
        return String.format( "Choice[id='%d', title='%s', A='%s', B='%s', C='%s', answer='%s']",
                id, title,A,B,C,answer);
    }

}
