package jit.hf.agriculture.Service;

import jit.hf.agriculture.domain.Choice;

import jit.hf.agriculture.domain.User;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午3:21 18-5-8
 **/
public interface ChoiceService {
    //新增、编辑、保存试题
    Choice saveOrUpdate(Choice choice);
    //删除试题
    Object deleteChoice(Long id);
    //根据ID获取试题
    Choice getChoiceById(Long id);
    //随机抽取5题
    List<Choice> getChoices(String type);
    //在线评分
    Object gradeChoice(Long firstId,String answer1,Long secondId,String answer2,Long thirdId,String answer3,Long forthId,String answer4,Long fifthId,String answer5);
    //随机抽取5题,每次只返回5题
    Choice getChoice(String type,int i);
    //添加错题
    Object addWrongChoice(Long firstId,String answer1,Long secondId,String answer2,Long thirdId,String answer3,Long forthId,String answer4,Long fifthId,String answer5);
    //获取所有错题
    Object getWrong();
    //删除错题
    Object deleteWrong(Long choiceId);
    //获取用户上传的所有题目
    Object getAllChoices();
    //修改题目
    Object changeChoice(Choice choice,Long choiceId);
    //获取所有试题
    List<Choice> getChoicesAll();
    //获取所有试题 && 分页
    List<Choice> getChoicePage(Pageable pageable);
    //搜索试题
    List<Choice> searchChoice(String key);
    //搜索试题 && 分页
    List<Choice> searchChoicePage(String key,Pageable pageable);
    //管理员删除试题
    Object deleteChoiceAdmin(Long id);
    //管理员删除错题
    Object deleteWrongAdmin(Long choiceId,User user);
}
