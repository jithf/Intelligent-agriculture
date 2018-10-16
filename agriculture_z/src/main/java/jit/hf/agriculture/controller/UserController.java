package jit.hf.agriculture.controller;

import io.swagger.annotations.*;
import jit.hf.agriculture.Service.RoleService;
import jit.hf.agriculture.Service.UserService;
import jit.hf.agriculture.domain.SysRole;
import jit.hf.agriculture.domain.User;
import jit.hf.agriculture.vo.Reponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: zj
 * Description:用户个人中心模块功能实现
 */

@RestController//还有一个额外的作用，将return返回值封装成json格式
@Api("UserController相关api")
public class UserController {

    //自动注入 获取bean
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleservice;
    @Value( "${upload.filepath}" )
    private String filepath;

    //String filepath = "./src/main/webapp/";//头像地址的存放目录，根据不同电脑服务器，要自行修改！！！

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    //获取个人信息(只需一开始用于点开个人中心，调用即可,之后不必调用)
    @GetMapping("/getPersonalInformation")
    public Object getPersonalInformation() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
       //String username = JwtTokenUtil
        User user1=userService.getUserByUsername(username);
        logger.info( "获取个人信息成功" );
        //logger.debug( "叭叭叭吧" );
        //logger.error( "不不不不不" );
        return new Reponse(true,"获取个人信息成功",user1);
    }

    //修改昵称
    @ApiOperation(value="修改昵称", notes="")
    @PostMapping("/personChange")
    public Object personChange(@RequestParam("nickname") String nickname) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
         User user1=userService.getUserByUsername(username);
         user1.setNickname(nickname);
         userService.saveOrUpdate(user1) ;
         return new Reponse(true,"修改成功",user1);
     }

    //上传头像
    @PostMapping("/headImg")
    public Object uploadHeadimg(@RequestParam("file") MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        System.out.println( username );
        User user = userService.getUserByUsername(username);
        if (!file.isEmpty()) { //文件不是空文件
            try {
                BufferedOutputStream out = new BufferedOutputStream(
                        //C:\IDEA_mode_project\agriculture\src\main
                        new FileOutputStream(new File(filepath + username + ".jpg")));//保存图片到目录下,建立保存文件的输入流
                out.write(file.getBytes());
                out.flush();
                out.close();
                String filename = filepath + username + ".jpg";
                user.setAvater(filename); //设置头像路径
                userService.saveOrUpdate(user);//修改用户信息
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return new Reponse(false,"上传失败," + e.getMessage());
                //return "上传失败," + e.getMessage();  //文件路径错误
            } catch (IOException e) {
                e.printStackTrace();
                return new Reponse(false,"上传失败," + e.getMessage());
                //return "上传失败," + e.getMessage();  //文件IO错误
            }
            return new Reponse(true,"上传头像成功",user);//返回用户信息
        } else {
            return new Reponse(false,"上传失败，因为文件是空的");
        }
    }

    //个人中心&&&修改权限，仅安卓端！
    @PostMapping("/changePermission")
    public Object changePermission(@RequestParam("permission") String permission) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername(username);
//        SysRole sysRole =roleservice.getOneByName(permission);//通过权限名查找表中权限
//        List<SysRole> roles = new ArrayList<>(); //因为我在关联表时候定义的是L..类型
//        roles.add(sysRole);//所以只能通过add来添加权限
//        user.setRoles(roles); //user信息添加完毕，即通过user表来更新关系表

        user.setAudit("审核中");
        user.setPermission(permission);
        userService.saveOrUpdate(user);
        return new Reponse(true, "修改权限申请发送成功",user);
    }

    //管理员界面 &&& 修改权限
    @PostMapping("/adminChangePermission")
    public Object adminChangePermission(@RequestParam("permission") String permission,
                                        @RequestParam("username") String username) {
        User user = userService.getUserByUsername( username );
        SysRole sysRole =roleservice.getOneByName(permission);//通过权限名查找表中权限
        List<SysRole> roles = new ArrayList<>(); //因为我在关联表时候定义的是L..类型
        roles.add(sysRole);//所以只能通过add来添加权限
        user.setRoles(roles); //user信息添加完毕，即通过user表来更新关系表
        userService.saveOrUpdate(user);
        return new Reponse(true, "审核通过，修改权限成功",user);
    }

}
