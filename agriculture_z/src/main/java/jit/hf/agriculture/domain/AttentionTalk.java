package jit.hf.agriculture.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Author: zj
 * Description:话题关注类（关注者）
 */
@Entity
public class AttentionTalk {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 唯一标识

    private String author;

    private String author_picture;

    public AttentionTalk() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

}
