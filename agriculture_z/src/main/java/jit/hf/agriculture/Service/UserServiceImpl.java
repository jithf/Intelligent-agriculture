package jit.hf.agriculture.Service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import jit.hf.agriculture.Repository.UserInformationRepository;
import jit.hf.agriculture.Repository.UserRepository;
import jit.hf.agriculture.domain.User;
import jit.hf.agriculture.domain.UserInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static jit.hf.agriculture.util.AliyunMessageUtil.sendSms;
import static jit.hf.agriculture.util.MD5Util.encode;


/**
 * Author: jit.hf
 * Description:用户服务接口实现
 * Date: Created in 上午12:21 18-3-26
 **/

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Transactional
    @Override
    public User saveOrUpdate(User user) {
        User user1=userRepository.save(user);
        UserInformation userInfo=new UserInformation(user);
        userInformationRepository.save(userInfo);
        return user1;
    }

    @Transactional
    @Override
    public User registerUser(User user) {
        User user1=userRepository.save(user);
        UserInformation userInfo=new UserInformation(user);
        userInformationRepository.save(userInfo);
        return user1;
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userInformationRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findOneById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findOneByUsername(username);
    }

    @Transactional
    @Override
    public String sendMsg(User user) {

        String phoneNumber = user.getUsername();
        String randomNum = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String jsonContent = "{\"code\":\"" + randomNum + "\"}";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phoneNumber", phoneNumber);
        paramMap.put("msgSign", "Agriculture农");
        paramMap.put("templateCode", "SMS_129405139");
        paramMap.put("jsonContent", jsonContent);
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = sendSms(paramMap);
       } catch (com.aliyuncs.exceptions.ClientException e) {
            e.printStackTrace();
        }
        if(!(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK"))) {
            if(sendSmsResponse.getCode() == null) {
                //这里可以抛出自定义异常
            }
            if(!sendSmsResponse.getCode().equals("OK")) {
                //这里可以抛出自定义异常
            }
        }

        user.setVerityCode(encode(randomNum));  //验证码加密
        if(userRepository.findOneByUsername(user.getUsername())!=null) {
            User user1=userRepository.findOneByUsername(user.getUsername());
            user.setId(user1.getId());
        }
        userRepository.save(user);
        return encode(randomNum);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersPage(Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    @Override
    public List<User> searchUser(String key) {
        return userRepository.findDistinctByUsernameContainingOrNicknameContaining(key,key);
    }

    @Override
    public List<User> searchUserPage(String key, Pageable pageable) {
        return userRepository.findDistinctByUsernameContainingOrNicknameContaining(key,key,pageable).getContent();
    }

    @Override
    public List<User> getBadUser(Pageable pageable) {
        return userRepository.findAllByAuditNot("审核通过",pageable).getContent();
    }

}
