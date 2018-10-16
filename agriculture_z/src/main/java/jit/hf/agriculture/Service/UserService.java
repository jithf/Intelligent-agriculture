package jit.hf.agriculture.Service;

import jit.hf.agriculture.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Author: jit.hf
 * Description:用户服务接口
 * Date: Created in 上午12:14 18-3-26
 **/
public interface UserService {
    //新增、编辑、保存用户
    User saveOrUpdate(User user);
    //注册用户
    User registerUser(User user);
    //删除用户
    void deleteUser(Long id);
    //根据ID获取用户
    User getUserById(Long id);
    //根据用户名获取用户
    User getUserByUsername(String username);
    //发送验证码
    String sendMsg(User user);
    //获取所有用户
    List<User> getUsers();
    //获取所有用户 && 分页获取
    List<User> getUsersPage(Pageable pageable);
    //搜索用户
    List<User> searchUser(String key);
    //搜索用户 && 分页
    List<User> searchUserPage(String key,Pageable pageable);
    //搜索审核未通过的用户 && 分页
    List<User> getBadUser(Pageable pageable);
}
