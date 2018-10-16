package jit.hf.agriculture.controller;

import jit.hf.agriculture.Repository.ChoiceRepository;
import jit.hf.agriculture.Service.ChoiceService;
import jit.hf.agriculture.Service.UserDataService;
import jit.hf.agriculture.domain.Choice;
import jit.hf.agriculture.vo.Reponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午3:51 18-5-8
 **/
@RestController
public class ChoiceController {

    @Autowired
    private ChoiceService choiceService;
    @Autowired
    private UserDataService userDataService;

    //添加试题
    @PostMapping("/addChoice")
    public Object addChoice(@RequestParam("title") String title,
                            @RequestParam("A") String A,
                            @RequestParam("B") String B,
                            @RequestParam("C") String C,
                            @RequestParam("answer") String answer,
                            @RequestParam("analysis") String analysis,
                            @RequestParam("type") String type) {
        Choice choice = new Choice(title,A,B,C,answer,analysis,type);
        choice.setUploadUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        choiceService.saveOrUpdate(choice);
        return new Reponse(true,"试题添加成功");
    }

    //删除试题
    @DeleteMapping("/deleteChoice")
    public Object deleteChoice(@RequestParam("choiceId") Long id) {
        Object rs=choiceService.deleteChoice(id);
        if(rs==null) {
            return new Reponse(true, "试题删除成功");
        }
        return new Reponse(false,"试题删除失败",rs);
    }

    //随机获取5道试题
    @GetMapping("/getChoices")
    public Object getChoices(@RequestParam("type") String type) {
        userDataService.editChoiceData();
        return new Reponse(true,"获取5道试题",choiceService.getChoices(type));
    }

    //随机获取5道试题,每次返回1道试题
    @GetMapping("/test")
    public Object getChoice(@RequestParam("type") String type,
                            @RequestParam("i") int i) {
        if(i==0)
        userDataService.editChoiceData();
        return new Reponse(true,"获取1道试题",choiceService.getChoice(type,i));
    }

    //在线评分
    @GetMapping("/grade")
    public Object grade(@RequestParam("firstId") Long firstId,
                        @RequestParam("answer1") String answer1,
                        @RequestParam("secondId") Long secondId,
                        @RequestParam("answer2") String answer2,
                        @RequestParam("thirdId") Long thirdId,
                        @RequestParam("answer3") String answer3,
                        @RequestParam("forthId") Long forthId,
                        @RequestParam("answer4") String answer4,
                        @RequestParam("fifthId") Long fifthId,
                        @RequestParam("answer5") String answer5) {
        return new Reponse(true,"获取5道试题",choiceService.gradeChoice(firstId,answer1,secondId,answer2,thirdId,answer3,forthId,answer4,fifthId,answer5));
    }

    //添加错题
    @PostMapping("/addWrong")
    public Object addWrongChoice(@RequestParam("firstId") Long firstId,
                        @RequestParam("answer1") String answer1,
                        @RequestParam("secondId") Long secondId,
                        @RequestParam("answer2") String answer2,
                        @RequestParam("thirdId") Long thirdId,
                        @RequestParam("answer3") String answer3,
                        @RequestParam("forthId") Long forthId,
                        @RequestParam("answer4") String answer4,
                        @RequestParam("fifthId") Long fifthId,
                        @RequestParam("answer5") String answer5) {
        return new Reponse(true,"添加错误试题",choiceService.addWrongChoice(firstId,answer1,secondId,answer2,thirdId,answer3,forthId,answer4,fifthId,answer5));
    }

    //获取所有试题
    @GetMapping("/getWrong")
    public Object getWrongChoice() {
        return new Reponse(true,"获取所有错题",choiceService.getWrong());
    }

    //删除试题
    @DeleteMapping("/deleteWrong")
    public Object deleteWrong(@RequestParam("choiceId") Long choiceId) {
        Object s=choiceService.deleteWrong(choiceId);
        if(s == null ) {
            return new Reponse(true, "删除错题",s);
        }
        return new Reponse(false,"删除错题失败",s);
    }

    //获取用户上传的所有试题
    @GetMapping("/getAllChoices")
    public Object getAllChoices() {
        return new Reponse(true,"获取用户上传的所有试题",choiceService.getAllChoices());
    }

    //修改试题
    @PostMapping("/changeChoice")
    public Object changesChoice(@RequestParam("choiceId") Long choiceId,
                            @RequestParam("title") String title,
                            @RequestParam("A") String A,
                            @RequestParam("B") String B,
                            @RequestParam("C") String C,
                            @RequestParam("answer") String answer,
                            @RequestParam("analysis") String analysis,
                            @RequestParam("type") String type) {
        Choice choice = new Choice(title,A,B,C,answer,analysis,type);
        Object object=choiceService.changeChoice(choice,choiceId);
        if(object==null) {
            return new Reponse(true, "试题修改成功");
        }
        return new Reponse(false,"试题修改失败",object);
    }
}
