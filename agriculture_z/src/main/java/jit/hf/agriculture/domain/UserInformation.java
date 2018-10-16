package jit.hf.agriculture.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 上午11:20 18-4-25
 **/
@Entity
public class UserInformation {

    @Id
    private Long userId;

    private String username;

    @JsonSerialize(include= JsonSerialize.Inclusion.NON_EMPTY)
    private String nickname;

    @JsonSerialize(include= JsonSerialize.Inclusion.NON_EMPTY)
    private String avater;

    protected UserInformation() {

    }

    public UserInformation(User user) {
        this.userId=user.getId();
        this.username=user.getUsername();
        this.nickname=user.getNickname();
        this.avater=user.getAvater();

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvater() {
        return avater;
    }

    public void setAvater(String avater) {
        this.avater = avater;
    }

}
