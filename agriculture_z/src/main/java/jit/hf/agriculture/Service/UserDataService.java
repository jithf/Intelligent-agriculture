package jit.hf.agriculture.Service;

import jit.hf.agriculture.domain.User;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午5:27 18-6-3
 **/
public interface UserDataService {
    //记录User登录数据
    void editUserData(User user);
    //记录文件下载量
    void editDocData();
    //记录话题评论评论量
    void editTalkData();
    //记录试题测试量
    void editChoiceData();
    //记录视频播放量
    void editVideoClicks();
    //记录视频评论量
    void editVideoComments();
}
