package jit.hf.agriculture.Service;

import jit.hf.agriculture.domain.User;
import org.springframework.web.client.RestTemplate;

/**
 * Author: zj
 * 提供一个登录的API，这个API应该是可以匿名访问的，注册本项目另外在MainController写了，这里注册接口不用
 */
public interface AuthService {
    User register(User userToAdd);
    String login(String username, String password);
    String refresh(String oldToken);//刷新token，个人感觉很鸡肋，oldToken不能失效
}
