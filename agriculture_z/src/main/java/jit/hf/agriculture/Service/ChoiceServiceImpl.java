package jit.hf.agriculture.Service;

import jit.hf.agriculture.Repository.ChoiceRepository;
import jit.hf.agriculture.Repository.UserRepository;
import jit.hf.agriculture.domain.Choice;
import jit.hf.agriculture.domain.User;
import jit.hf.agriculture.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午3:21 18-5-8
 **/
@Service
public class ChoiceServiceImpl implements ChoiceService {

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private UserRepository userRepository;
    private int[] results = new int[5];

    @Override
    public Choice saveOrUpdate(Choice choice) {
        return choiceRepository.save(choice);
    }

    @Override
    public Object deleteChoice(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户

        Choice choice = choiceRepository.findOneById(id);
        if (choice.getUploadUsername() != null && choice.getUploadUsername().equals(username)) {
            String wrongUserId = choice.getWrongUserId();
            if (wrongUserId != null && wrongUserId.length() != 5) {
                String[] as = wrongUserId.split(",");
                for (int i = 1; i < as.length; i++) {
                    User user = userRepository.findOneById(Long.valueOf(as[i]));
                    List<Choice> choices = user.getChoices();
                    if (choices.contains(choice)) {
                        choices.remove(choice);
                        user.setChoices(choices);
                        userRepository.save(user);
                    }
                }
            }
            choiceRepository.delete(id);
            return null;
        }
        return "没有删除此试题的权限";
    }

    @Override
    public Choice getChoiceById(Long id) {
        return choiceRepository.findOneById(id);
    }

    //在指定范围随机获取n个数
    public int[] randomCommon(int min, int max, int n, String type) {
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while (count < n) {
            int num = (int) ((Math.random() * (max - min)) + min - 0.2);
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                Choice choice = choiceRepository.findOneById((long) num);
                //if(choiceRepository.findOneById((long) num)!=null && choiceRepository.findOneById((long) num).getType().equals(type)) {
                if (choice != null && choice.getType().equals(type)) {
                    result[count] = num;
                    count++;
                }
            }
        }
        return result;
    }

    @Override
    public List<Choice> getChoices(String type) {
        int max = (int) choiceRepository.count();
        int[] result = randomCommon(1, max, 5, type);
        List<Choice> choices = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Choice choice = choiceRepository.findOneById((long) result[i]);
            choices.add(choice);
        }
        return choices;
    }

    @Override
    public Choice getChoice(String type, int i) {
        int max = (int) choiceRepository.count();
        if (i == 0) {
            results = randomCommon(1, max, 5, type);
        }
        Choice choice = choiceRepository.findOneById((long) results[i]);
//        for(int j=0;j<5;j++)
//        System.out.println(results[j]);
        return choice;
    }

    @Override
    public Object gradeChoice(Long firstId, String answer1, Long secondId, String answer2, Long thirdId, String answer3, Long forthId, String answer4, Long fifthId, String answer5) {
        Long[] choiceId = {firstId, secondId, thirdId, forthId, fifthId};
        String[] answers = {answer1, answer2, answer3, answer4, answer5};
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userRepository.findOneByUsername(username);
        List<Choice> choices = user.getChoices();
        boolean[] tf = new boolean[5];
        int score = 0;
        for (int i = 0; i < 5; i++) {
            if (choiceRepository.findOneById(choiceId[i]).getAnswer().equals(answers[i])) {
                tf[i] = true;
                score = score + 20;
                if (!choices.contains(choiceRepository.findOneById(choiceId[i]))) {
                    Choice choice = choiceRepository.findOneById(choiceId[i]);
                    choices.add(choice);

                    String wrongUserId = choice.getWrongUserId();
                    choice.setWrongUserId(wrongUserId + user.getId() + ",");
                    choiceRepository.save(choice);
                }
            } else {
                tf[i] = false;
            }
        }
        user.setChoices(choices);
        userRepository.save(user);
        return new Result(tf[0], tf[1], tf[2], tf[3], tf[4], String.valueOf(score));
    }

    @Override
    public Object addWrongChoice(Long firstId, String answer1, Long secondId, String answer2, Long thirdId, String answer3, Long forthId, String answer4, Long fifthId, String answer5) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userRepository.findOneByUsername(username);
        List<Choice> choices = user.getChoices();
        Long[] choiceId = {firstId, secondId, thirdId, forthId, fifthId};
        String[] answers = {answer1, answer2, answer3, answer4, answer5};
        int j = 0;
        for (int i = 0; i < 5; i++) {
            if (answers[i].equals("1") && !choices.contains(choiceRepository.findOneById(choiceId[i]))) {
                Choice choice = choiceRepository.findOneById(choiceId[i]);
                choices.add(choice);
                j++;

                String wrongUserId = choice.getWrongUserId();
                choice.setWrongUserId(wrongUserId + "," + user.getId());
                //System.out.println("###"+choice.getWrongUserId());
                choiceRepository.save(choice);
            }
        }
        user.setChoices(choices);
        userRepository.save(user);
        return j;
    }

    @Override
    public Object getWrong() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userRepository.findOneByUsername(username);
        return user.getChoices();
    }

    @Override
    public Object deleteWrong(Long choiceId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userRepository.findOneByUsername(username);
        List<Choice> choices = user.getChoices();
        if (choices.contains(choiceRepository.findOneById(choiceId))) {
            choices.remove(choiceRepository.findOneById(choiceId));
            userRepository.save(user);

            Choice choice = choiceRepository.findOneById(choiceId);
            String wrongUserId = choice.getWrongUserId();
            String[] as = wrongUserId.split(",");
            String rs = "wrong";
            for (int i = 1; i < as.length; i++) {
                if (!as[i].equals(user.getId().toString())) {
                    rs = rs + "," + as[i];
                }
            }
            choice.setWrongUserId(rs);
            choiceRepository.save(choice);
            return null;
        }
        return "该用户未有此错题记录";
    }

    @Override
    public Object getAllChoices() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        return choiceRepository.findByUploadUsernameEquals(username);
    }

    @Override
    public Object changeChoice(Choice choice, Long choiceId) {
        if (choiceRepository.findOneById(choiceId).getUploadUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            choice.setId(choiceId);
            choice.setUploadUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            choiceRepository.save(choice);
            return null;
        }

        return "权限不够,没有修改该试题的权限";
    }

    //获取所有试题
    @Override
    public List<Choice> getChoicesAll() {
        return choiceRepository.findAll();
    }

    //获取所有试题 && 分页
    @Override
    public List<Choice> getChoicePage(Pageable pageable) {
        return choiceRepository.findAll(pageable).getContent();
    }

    @Override
    public List<Choice> searchChoice(String key) {
        return choiceRepository.findDistinctByTitleContainingOrTypeContaining(key, key);
    }

    @Override
    public List<Choice> searchChoicePage(String key, Pageable pageable) {
        return choiceRepository.findDistinctByTitleContainingOrTypeContaining(key,key,pageable).getContent();
    }

    @Override
    public Object deleteChoiceAdmin(Long id) {
        Choice choice = choiceRepository.findOneById(id);
        String wrongUserId = choice.getWrongUserId();
        if (wrongUserId != null && wrongUserId.length() != 5) {
            String[] as = wrongUserId.split(",");
            for (int i = 1; i < as.length; i++) {
                User user = userRepository.findOneById(Long.valueOf(as[i]));
                List<Choice> choices = user.getChoices();
                choices.size();
                if (choices.contains(choice)) {
                    choices.remove(choice);
                    user.setChoices(choices);
                    userRepository.save(user);
                }
            }
        }
        choiceRepository.delete(id);
        return null;
    }

    @Override
    public Object deleteWrongAdmin(Long choiceId, User user) {
        List<Choice> choices = user.getChoices();
        Choice choice = choiceRepository.findOneById(choiceId);
        String wrongUserId = choice.getWrongUserId();
        String[] as = wrongUserId.split(",");
        String rs = "wrong";
        for (int i = 1; i < as.length; i++) {
            if (!as[i].equals(user.getId().toString())) {
                rs = rs + "," + as[i];
            }
        }
        choice.setWrongUserId(rs);
        choiceRepository.save(choice);
        return null;
    }

}
