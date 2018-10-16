package jit.hf.agriculture.controller;

import jit.hf.agriculture.Repository.*;
import jit.hf.agriculture.Service.*;
import jit.hf.agriculture.domain.*;
import jit.hf.agriculture.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static jit.hf.agriculture.Service.UserDataServiceImpl.dateTime;
import static jit.hf.agriculture.util.MD5Util.encode;

/**
 * Author: jit.hf
 * Description:f
 * Date: Created in 上午10:02 18-5-31
 **/

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
public class AdminController {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    VideoService videoService;
    @Autowired
    ChoiceService choiceService;
    @Autowired
    DocRepository docRepository;
    @Autowired
    DocService docService;
    @Autowired
    TalkRepository talkRepository;
    @Autowired
    EsTalkRepository esTalkRepository;
    @Autowired
    TalkService talkService;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDataRepository userDataRepository;
    @Autowired
    ChoiceRepository choiceRepository;

    @Value("${upload.filepath}")
    private String filepath;

    @Value("${redirect.url}")
    private String redirectUrl;

    @Value("${jwt.header}")
    private String tokenHeader;

    //获取用户列表 test
    @GetMapping("/admin/getAllUser")
    public Object getAllUser() {
        List<User> userList = userService.getUsers();
        List<UserUtil> userUtils = new ArrayList<>();
        for (User user : userList) {
            UserUtil userUtil = new UserUtil(user);
            userUtils.add(userUtil);
        }
        return new Reponse(true, "获取所有用户列表", userUtils);
    }

    //获取用户列表 && 分页获取
    @GetMapping("/admin/getUserPage")
    public Object searchHotVideo(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex) {
        Pageable pageable = new PageRequest(pageIndex, 10);
        List<User> users = userService.getUsersPage(pageable);
        List<UserUtil> userUtils = new ArrayList<>();
        if (users.size() == 0) {
            return new Reponse(false, "没有更多用户了");
        } else {
            for (User user : users) {
                UserUtil userUtil = new UserUtil(user);
                userUtils.add(userUtil);
            }
            return new Reponse(true, "分页获取用户列表，第" + pageIndex + "页", userUtils);
        }
    }

    //根据Id查询单个用户详细信息
    @GetMapping("/admin/getUser")
    public Object getUser(@RequestParam(value = "userId") Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return new Reponse(false, "查询失败，找不到此用户");
        }

        return new Reponse(true, "查询成功", user);
    }

    //根据Username查询用户
    @GetMapping("/admin/getUserByUsername")
    public Object getUserByUsername(@RequestParam(value = "username") String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return new Reponse(false, "查询失败，找不到此用户");
        }

        return new Reponse(true, "查询成功", user);
    }

    //搜索用户 根据用户名或昵称
    @GetMapping("/admin/searchUser")
    public Object searchUser(@RequestParam(value = "key") String key) {
        List<User> users = userService.searchUser(key);
        List<UserUtil> userUtils = new ArrayList<>();
        if (users.size() == 0) {
            return new Reponse(false, "找不到更多相关用户了");
        } else {
            for (User user : users) {
                UserUtil userUtil = new UserUtil(user);
                userUtils.add(userUtil);
            }
        }
        return new Reponse(true, "查询成功", userUtils);
    }

    //搜索用户 根据用户名或昵称  && 分页
    @GetMapping("/admin/searchUserPage")
    public Object searchUserPage(@RequestParam(value = "key") String key,
                                 @RequestParam(value = "pageIndex", required = false, defaultValue = "0") Integer pageIndex) {
        Pageable pageable = new PageRequest(pageIndex, 10);
        List<User> users = userService.searchUserPage(key,pageable);
        List<UserUtil> userUtils = new ArrayList<>();
        if (users.size() == 0) {
            return new Reponse(false, "找不到更多相关用户");
        } else {
            for (User user : users) {
                UserUtil userUtil = new UserUtil(user);
                userUtils.add(userUtil);
            }
        }
        return new Reponse(true, "查询成功，第" + pageIndex + "页。", userUtils);
    }

    //搜索权限申请未通过的用户 && 分页
    @GetMapping("/admin/getBadUser")
    public Object getBadUser(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") Integer pageIndex) {
        Pageable pageable = new PageRequest(pageIndex, 10);
        List<User> users = userService.getBadUser(pageable);
        List<UserUtil> userUtils = new ArrayList<>();
        if (users.size() == 0) {
            return new Reponse(false, "找不到更多权限申请未通过的用户");
        } else {
            for (User user : users) {
                UserUtil userUtil = new UserUtil(user);
                userUtils.add(userUtil);
            }
        }
        return new Reponse(true, "查询权限申请未通过的用户成功，第" + pageIndex + "页。", userUtils);
    }

    //增添用户
    @PostMapping("/admin/addUser")
    public Object addUser(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("permission") String permission,
                          @RequestParam(value = "nickname", required = false, defaultValue = "热心网友") String nickname) {
        User user = new User(username, password);

        User user1 = userService.getUserByUsername(user.getUsername());
        if (user1 != null) {
            return new Reponse(false, "添加失败，用户名已经注册过了");
        }

        SysRole sysRole = roleService.getOneByName(permission);//通过权限名查找表中权限
        List<SysRole> roles = new ArrayList<>(); //因为我在关联表时候定义的是L..类型
        roles.add(sysRole);//所以只能通过add来添加权限
        user.setRoles(roles); //user信息添加完毕，即通过user表来更新关系表

        user.setNickname(nickname);
        user.setPassword(encode(user.getPassword()));  //密码加密
        user.setLastPasswordResetDate(new Date());
        User re = userService.saveOrUpdate(user);
        return new Reponse(true, "添加用户成功", new UserUtil(re));
    }

    //删除用户
    @DeleteMapping("/admin/deleteUser")
    public Object deleteUser(@RequestParam("userId") Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return new Reponse(false, "删除失败，找不到此用户");
        }

        //删除此用户的错题记录
        List<Choice> choiceList = user.getChoices();
        for (Choice choice : choiceList) {
            choiceService.deleteWrongAdmin(choice.getId(), user);
        }

        //删除该用户收藏视频的记录
        String videos = user.getVideos();
        String[] vs = videos.split(",");
        for (int i = 1; i < vs.length; i++) {
            videoService.deleteVideoCollectionAdmin(Long.valueOf(vs[i]), user);
        }

        userService.deleteUser(userId);
        return new Reponse(true, "删除用户成功");
    }

    //修改用户
    @PostMapping("/admin/changeUser")
    public Object changeUser(@RequestParam(value = "userId") Long userId,
                             @RequestParam(value = "nickname", required = false, defaultValue = "null") String nickname,
                             @RequestParam(value = "roles", required = false, defaultValue = "null") String roles,
                             @RequestParam(value = "userTalkValues", required = false, defaultValue = "null") String userTalkValues,
                             @RequestParam(value = "avater", required = false) MultipartFile file) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return new Reponse(false, "修改失败，找不到此用户");
        }

        if (!nickname.equals("null")) {
            user.setNickname(nickname);
        }

        if (!roles.equals("null")) {
            user.setPermission(roles);
            user.setAudit("审核通过");
            SysRole sysRole = roleService.getOneByName(roles);//通过权限名查找表中权限
            List<SysRole> roleList = new ArrayList<>(); //因为我在关联表时候定义的是L..类型
            roleList.add(sysRole);//所以只能通过add来添加权限
            user.setRoles(roleList); //user信息添加完毕，即通过user表来更新关系表
        }

        if (!userTalkValues.equals("null")) {
            user.setUserTalkValues(Integer.valueOf(userTalkValues));
        }

        if (file != null) {
            if (!file.isEmpty()) { //文件不是空文件
                try {
                    BufferedOutputStream out = new BufferedOutputStream(
                            //C:\IDEA_mode_project\agriculture\src\main
                            new FileOutputStream(new File(filepath + user.getUsername() + ".jpg")));//保存图片到目录下,建立保存文件的输入流
                    out.write(file.getBytes());
                    out.flush();
                    out.close();
                    String filename = filepath + user.getUsername() + ".jpg";
                    user.setAvater(filename); //设置头像路径
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return new Reponse(false, "修改头像失败," + e.getMessage());
                    //return "上传失败," + e.getMessage();  //文件路径错误
                } catch (IOException e) {
                    e.printStackTrace();
                    return new Reponse(false, "修改头像失败," + e.getMessage());
                    //return "上传失败," + e.getMessage();  //文件IO错误
                }
            }
        }

        User re = userService.saveOrUpdate(user);
        return new Reponse(true, "修改用户信息成功", new UserUtil(re));
    }

    //管理员审核权限修改 && 审核通过
    @PostMapping("/admin/checkPermission")
    public Object adminCheckPermission(@RequestParam("userId") Long userId) {
        User user = userService.getUserById(userId);
        if(user==null) {
            return new Reponse(false, "失败，找不到此用户");
        }

        SysRole sysRole = roleService.getOneByName(user.getPermission());//通过权限名查找表中权限
        List<SysRole> roles = new ArrayList<>(); //因为我在关联表时候定义的是L..类型
        roles.add(sysRole);//所以只能通过add来添加权限
        user.setRoles(roles); //user信息添加完毕，即通过user表来更新关系表
        user.setAudit("审核通过");
        User re = userService.saveOrUpdate(user);
        return new Reponse(true, "审核通过，修改权限成功", new UserUtil(re));
    }

    //管理员审核权限修改 && 审核不通过
    @PostMapping("/admin/checkFalse")
    public Object adminCheckFalse(@RequestParam("userId") Long userId) {
        User user = userService.getUserById(userId);
        if(user==null) {
            return new Reponse(false, "失败，找不到此用户");
        }
        user.setAudit("审核不通过");
        User re = userService.saveOrUpdate(user);
        return new Reponse(true, "审核不通过，不予修改权限", new UserUtil(re));
    }

    //获取题库试题列表
    @GetMapping("/admin/getAllChoice")
    public Object getAllChoice() {
        List<Choice> choiceList = choiceService.getChoicesAll();
        for (Choice choice : choiceList) {
            if (choice.getWrongUserId().length() > 6)
                choice.setWrongUserId(choice.getWrongUserId().substring(6));
            else
                choice.setWrongUserId("暂无用户错误试题记录");
        }
        return new Reponse(true, "获取所有题库试题列表", choiceList);
    }

    //获取试题列表 && 分页获取
    @GetMapping("/admin/getChoicePage")
    public Object getChoicePage(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex) {
        Pageable pageable = new PageRequest(pageIndex, 10);
        List<Choice> choiceList = choiceService.getChoicePage(pageable);
        if (choiceList.size() == 0) {
            return new Reponse(false, "没有更多试题了");
        } else {
            for (Choice choice : choiceList) {
                if (choice.getWrongUserId().length() > 6)
                    choice.setWrongUserId(choice.getWrongUserId().substring(6));
                else
                    choice.setWrongUserId("暂无用户错误试题记录");
            }
            return new Reponse(true, "分页获取所有题库试题列表，第" + pageIndex + "页", choiceList);
        }
    }

    //根据id查询试题详细信息
    @GetMapping("/admin/getChoiceById")
    public Object getChoiceById(@RequestParam("choiceId") Long choiceId) {
        Choice choice = choiceService.getChoiceById(choiceId);
        if (choice == null)
            return new Reponse(false, "未找到相关试题");

        return new Reponse(true, "搜索相关试题", choice);
    }

    //搜索试题
    @GetMapping("/admin/searchChoice")
    public Object searchChoice(@RequestParam("key") String key) {
        List<Choice> choiceList = choiceService.searchChoice(key);
        if (choiceList.size() == 0)
            return new Reponse(false, "未找到相关试题");
        for (Choice choice : choiceList) {
            if (choice.getWrongUserId().length() > 6)
                choice.setWrongUserId(choice.getWrongUserId().substring(6));
            else
                choice.setWrongUserId("暂无用户错误试题记录");
        }
        return new Reponse(true, "搜索相关试题", choiceList);
    }

    //搜索试题 依据标题和类型 && 分页
    @GetMapping("/admin/searchChoicePage")
    public Object searchChoicePage(@RequestParam("key") String key,
                               @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex) {
        Pageable pageable = new PageRequest(pageIndex, 10);
        List<Choice> choiceList = choiceService.searchChoicePage(key,pageable);
        if (choiceList.size() == 0)
            return new Reponse(false, "未找到更多相关试题");
        for (Choice choice : choiceList) {
            if (choice.getWrongUserId().length() > 6)
                choice.setWrongUserId(choice.getWrongUserId().substring(6));
            else
                choice.setWrongUserId("暂无用户错误试题记录");
        }
        return new Reponse(true, "搜索相关试题，第" + pageIndex + "页", choiceList);
    }

    //添加试题
    @PostMapping("/admin/addChoice")
    public Object addChoice(@RequestParam("title") String title,
                            @RequestParam("A") String A,
                            @RequestParam("B") String B,
                            @RequestParam("C") String C,
                            @RequestParam("answer") String answer,
                            @RequestParam("analysis") String analysis,
                            @RequestParam("type") String type) {
        Choice choice = new Choice(title, A, B, C, answer, analysis, type);
        choice.setUploadUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        choiceService.saveOrUpdate(choice);
        choice.setWrongUserId("暂无用户错误试题记录");
        return new Reponse(true, "试题添加成功", choice);
    }

    //删除试题
    @DeleteMapping("/admin/deleteChoice")
    public Object deleteChoice(@RequestParam("choiceId") Long id) {
        Choice choice = choiceService.getChoiceById(id);
        if (choice == null)
            return new Reponse(false, "试题删除失败,未找到此试题");
        choiceService.deleteChoiceAdmin(id);
        return new Reponse(true, "试题删除成功");
    }

    //修改试题
    @PostMapping("/admin/changeChoice")
    public Object changeChoice(@RequestParam(value = "choiceId") Long choiceId,
                               @RequestParam(value = "title", required = false, defaultValue = "null") String title,
                               @RequestParam(value = "A", required = false, defaultValue = "null") String A,
                               @RequestParam(value = "B", required = false, defaultValue = "null") String B,
                               @RequestParam(value = "C", required = false, defaultValue = "null") String C,
                               @RequestParam(value = "answer", required = false, defaultValue = "null") String answer,
                               @RequestParam(value = "analysis", required = false, defaultValue = "null") String analysis,
                               @RequestParam(value = "type", required = false, defaultValue = "null") String type) {
        Choice choice = choiceService.getChoiceById(choiceId);
        if (choice == null) {
            return new Reponse(false, "未找到此试题");
        }
        if (!title.equals("null")) {
            choice.setTitle(title);
        }
        if (!A.equals("null")) {
            choice.setTitle(A);
        }
        if (!B.equals("null")) {
            choice.setTitle(B);
        }
        if (!C.equals("null")) {
            choice.setTitle(C);
        }
        if (!analysis.equals("null")) {
            choice.setTitle(analysis);
        }
        if (!answer.equals("null")) {
            choice.setTitle(answer);
        }
        if (!type.equals("null")) {
            choice.setTitle(type);
        }

        Choice rs = choiceService.saveOrUpdate(choice);

        if (rs.getWrongUserId().length() > 6)
            rs.setWrongUserId(choice.getWrongUserId().substring(6));
        else
            rs.setWrongUserId("暂无用户错误试题记录");
        return new Reponse(true, "试题修改成功", rs);
    }

    //获取文件列表
    @RequestMapping(value = "/admin/getFileList", method = RequestMethod.GET)
    public Object getFileList() {
        List<Doc> list = docRepository.findAll();
        return new Reponse(true, "获取文件列表成功", list);
    }

    //获取文件列表 && 分页获取
    @GetMapping("/admin/getDocPage")
    public Object getDocPage(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex) {
        Sort sort = new Sort(Sort.Direction.DESC, "uptime");
        Pageable pageable = new PageRequest(pageIndex, 10, sort);
        List<Doc> docList = docService.getDocPage(pageable);
        if (docList.size() == 0) {
            return new Reponse(false, "没有更多文档了");
        }
        return new Reponse(true, "分页获取所有文件列表，第" + pageIndex + "页", docList);
    }

    //根据id查询文档详细信息
    @GetMapping("/admin/getDocById")
    public Object getDocById(@RequestParam("docId") Long docId) {
        Doc doc=docService.getDocById(docId);
        if(doc==null) {
            return new Reponse(false, "未找到相关文件");
        }
        return new Reponse(true, "根据id查询文档详细信息", doc);
    }

    //搜索文件 根据标题和描述
    @GetMapping("/admin/searchDoc")
    public Object searchDoc(@RequestParam("key") String key) {
        List<Doc> docList = docService.searchDoc(key);
        if (docList.size() == 0)
            return new Reponse(false, "未找到相关文件");
        return new Reponse(true, "搜索相关文件", docList);
    }

    //搜索文件 根据标题和描述 && 分页
    @GetMapping("/admin/searchDocPage")
    public Object searchDocPage(@RequestParam("key") String key,
                                @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex)  {
        Sort sort = new Sort(Sort.Direction.DESC, "uptime");
        Pageable pageable = new PageRequest(pageIndex, 10, sort);
        List<Doc> docList = docService.searchDocPage(key,pageable);
        if (docList.size() == 0)
            return new Reponse(false, "未找到更多相关文件");
        return new Reponse(true, "搜索相关文件，第" + pageIndex + "页", docList);
    }

    //在线预览
    @GetMapping(value = "/admin/previewOnline")
    public Object previewOnline(@RequestParam(value = "docId") Long docId, HttpServletResponse response) throws IOException {
        String key = "367400494";//永中云域名key

        Doc doc = docRepository.findOneById(docId);
        String filename = doc.getAvatar().split("/")[4];
        String url = String.format("http://dcsapi.com/" +
                "?k=" + key +
                "&url=http://112.74.53.186/" + filename);
        System.out.println(url);
        response.sendRedirect(url);
        return null;
    }

    //新增文件（上传文件）
    @PostMapping("/admin/uploadDoc")
    public Object uploadFile(@RequestParam("title") String title,
                           @RequestParam("description") String description,
                           @RequestParam("file") MultipartFile file ,
                           HttpServletRequest httpServletRequest) throws IOException {

        String base=Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String basePath=base.substring(0,base.indexOf("/target"))+"/src/main/mysql/";
        String fileName=file.getOriginalFilename();
        File fileTemp=new File(basePath+fileName);
        file.transferTo(fileTemp);
        FileSystemResource resource = new FileSystemResource(fileTemp);

        String url = redirectUrl+"/docUpload";

        MultiValueMap<String,Object> params = new LinkedMultiValueMap<>();
        params.add("file", resource);
        params.add("title",title);
        params.add("description",description);

        HttpHeaders requestHeaders = new HttpHeaders();
        String authHeader = httpServletRequest.getHeader(this.tokenHeader);
        requestHeaders.add("Authorization", authHeader);
        MediaType type = MediaType.parseMediaType("multipart/form-data; charset=UTF-8");
        requestHeaders.setContentType(type);

        HttpEntity<MultiValueMap<String,Object>> httpEntity=new HttpEntity<>(params,requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        String result=restTemplate.postForObject(url,httpEntity,String.class);//POST请求 token（授权成功，会自动发送这个请求）
        if(!fileTemp.delete())
            return new Reponse(false,"删除缓存文件出错了");

        if(!result.split("\"")[2].equals(":true,")) {
            return new Reponse(false,result.split("\"")[5]);
        }
        String id=result.split("\"")[10];
        System.out.println(Long.valueOf(id.substring(1,id.indexOf(","))));
        Doc re=docRepository.findOneById(Long.valueOf(id.substring(1,id.indexOf(","))));
        return new Reponse(true,result.split("\"")[5],re);
    }

    //删除文件
    @DeleteMapping("/admin/deleteDoc")
    public Object deleteDoc(@RequestParam("docId") Long docId) {
        Doc doc=docRepository.findOneById(docId);
        if(doc==null)
            return new Reponse(false, "删除文件失败,找不到此文件");
        try {
            File file = new File(docRepository.findOneById(docId).getAvatar());
            if (file.delete()) {
                docRepository.delete(docId);
                return new Reponse(true, "成功删除文件");
            } else {
                return new Reponse(false, "删除文件失败");
            }
        } catch (Exception e) {
            return new Reponse(false, "删除文件失败:" + e.getMessage());
        }
    }

    //修改文件
    @PostMapping("/admin/changeDoc")
    public Object changeDoc(@RequestParam("docId") Long docId,
                            @RequestParam(value = "title", required = false, defaultValue = "null") String title,
                            @RequestParam(value = "description", required = false, defaultValue = "null") String description) {
        Doc doc = docRepository.findOneById(docId);
        if (doc == null)
            return new Reponse(false, "修改失败，未找到此文件");
        doc.setTitle(title);
        doc.setDescription(description);
        Doc re = docRepository.save(doc);
        return new Reponse(true, "修改文件成功", re);
    }

    //获取话题列表
    @GetMapping("/admin/showTalk")
    public Object showTalk() {
        List<Talk> talkList = talkRepository.findAll();
        List<TalkUtil> talkUtilList = new ArrayList<>();
        for (Talk talk : talkList) {
            talkUtilList.add(new TalkUtil(talk));
        }
        return new Reponse(true, "获取话题列表", talkUtilList);
    }

    //获取话题列表 && 分页
    @GetMapping("/admin/showTalkPage")
    public Object showTalkPage(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex) {
        Sort sort = new Sort(Sort.Direction.DESC, "uptime");
        Pageable pageable = new PageRequest(pageIndex, 10, sort);
        List<Talk> talkList = talkRepository.findAll(pageable).getContent();
        List<TalkUtil> talkUtilList = new ArrayList<>();
        if (talkList.size() == 0) {
            return new Reponse(false, "没有更多话题了");
        } else {
            for (Talk talk : talkList) {
                talkUtilList.add(new TalkUtil(talk));
            }
            return new Reponse(true, "分页，获取话题最新列表成功！", talkUtilList);
        }
    }

    //依据Id获取话题详细信息
    @GetMapping("/admin/getTalk")
    public Object getTalk(@RequestParam(value = "talkId") Long talkId) {
        Talk talk = talkRepository.findOneById(talkId);
        if (talk == null)
            return new Reponse(false, "查找此话题失败，找不到此话题");
        return new Reponse(true, "查找此话题详细信息成功", talk);
    }

    //搜索话题 && 分页
    @GetMapping("/admin/searchTalkPage")
    public Object searchTalkPage(@RequestParam(value = "key") String key,
                             @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex) {
        Sort sort = new Sort(Sort.Direction.DESC, "uptime");
        Pageable pageable = new PageRequest(pageIndex, 10, sort);
        List<EsTalk> esTalkList = talkRepository.findDistinctEsTalkByThemeContainingOrTagsContaining(key, key,pageable).getContent();
        List<TalkUtil> talkUtilList = new ArrayList<>();
        if (esTalkList.isEmpty()) {
            return new Reponse(false, "没有找到更多相关话题");
        }
        for (EsTalk esTalk : esTalkList) {
            Talk talk = talkRepository.findOneById(esTalk.getId());
            TalkUtil talkUtil = new TalkUtil(talk);
            talkUtilList.add(talkUtil);
        }
        return new Reponse(true, "搜索相关话题成功,第"+pageIndex+"页。", talkUtilList);
    }

    //搜索话题
    @GetMapping("/admin/searchTalk")
    public Object searchTalk(@RequestParam(value = "key") String key) {
        List<EsTalk> esTalkList = talkRepository.findDistinctEsTalkByThemeContainingOrTagsContaining(key, key);
        List<TalkUtil> talkUtilList = new ArrayList<>();
        if (esTalkList.isEmpty()) {
            return new Reponse(false, "没有找到相关话题");
        }
        for (EsTalk esTalk : esTalkList) {
            Talk talk = talkRepository.findOneById(esTalk.getId());
            TalkUtil talkUtil = new TalkUtil(talk);
            talkUtilList.add(talkUtil);
        }
        return new Reponse(true, "搜索相关话题成功", talkUtilList);
    }

    //添加话题
    @PostMapping("/admin/addTalk")
    public Object addTalk(@RequestParam("theme") String theme,
                          @RequestParam("description") String description,
                          @RequestParam("tags") String tags,
                          @RequestParam("talkValues") Integer talkValues) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername(author);
        String author_picture = user.getAvater();

        Talk talk = new Talk();
        talk.setTheme(theme);
        talk.setDescription(description);
        talk.setAuthor(author);
        talk.setAuthor_picture(author_picture);
        talk.setTags(tags);
        talk.setTalkValues(talkValues);
        talk.setUptime(new Date());
        Talk talkResult = talkService.saveOrUpdateQuestion(talk);//保存talk
        //接下来将talk传到文本库中，以供搜索功能使用（文本库新增记录）
        EsTalk esTalk = new EsTalk(talkResult);
        esTalkRepository.save(esTalk);
        return new Reponse(true, "添加话题成功！", new TalkUtil(talkResult));
    }

    //删除话题
    @DeleteMapping("/admin/deleteTalk")
    public Object deleteTalk(@RequestParam("talkId") Long talkId) {
        Talk talk = talkRepository.findOneById(talkId);
        if (talk == null)
            return new Reponse(false, "找不到此话题");
        talkRepository.delete(talk);
        EsTalk esTalk = esTalkRepository.findOneById(talkId);
        if (esTalk != null)
            esTalkRepository.delete(esTalk);
        return new Reponse(true, "删除此话题成功");
    }

    //修改话题
    @PostMapping("/admin/changeTalk")
    public Object changeTalk(@RequestParam(value = "talkId") Long talkId,
                             @RequestParam(value = "theme", required = false, defaultValue = "null") String theme,
                             @RequestParam(value = "description", required = false, defaultValue = "null") String description,
                             @RequestParam(value = "tags", required = false, defaultValue = "null") String tags,
                             @RequestParam(value = "talkValues", required = false, defaultValue = "null") String talkValues) {
        Talk talk = talkRepository.findOneById(talkId);
        if (talk == null)
            return new Reponse(false, "找不到此话题");

        if (!theme.equals("null"))
            talk.setTheme(theme);
        if (!description.equals("null"))
            talk.setDescription(description);
        if (!tags.equals("null"))
            talk.setTags(tags);
        if (!talkValues.equals("null"))
            talk.setTalkValues(Integer.valueOf(talkValues));

        Talk talkResult = talkService.saveOrUpdateQuestion(talk);//保存talk
        //接下来将talk传到文本库中，以供搜索功能使用（文本库新增记录）
        EsTalk esTalk = esTalkRepository.findOneById(talkId);
        esTalkRepository.delete(esTalk);
        EsTalk rsTalk = new EsTalk(talkResult);
        esTalkRepository.save(rsTalk);
        return new Reponse(true, "修改话题成功！", new TalkUtil(talkResult));
    }

    //获取视频列表
    @GetMapping("/admin/getAllVideo")
    public Object getVideoCollection() {
        List<Video> videoList = videoRepository.findAll();
        List<VideoUtil> videoUtilList = new ArrayList<>();
        for (Video video : videoList) {
            VideoUtil videoUtil = new VideoUtil(video);
            videoUtilList.add(videoUtil);
        }
        return new Reponse(true, "获取所有视频列表", videoUtilList);
    }

    //获取视频列表 && 分页
    @GetMapping("/admin/getVideoPage")
    public Object getVideoPage(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex) {
        Sort sort = new Sort(Sort.Direction.DESC, "uptime");
        Pageable pageable = new PageRequest(pageIndex, 10, sort);
        List<Video> videoList = videoRepository.findAll(pageable).getContent();
        List<VideoUtil> videoUtilList = new ArrayList<>();
        if (videoList.size() == 0) {
            return new Reponse(false, "没有更多视频了");
        } else {
            for (Video video : videoList) {
                VideoUtil videoUtil = new VideoUtil(video);
                videoUtilList.add(videoUtil);
            }
            return new Reponse(true, "分页获取视频列表，第" + pageIndex + "页", videoUtilList);
        }
    }

    //依据Id查找视频详细信息
    @GetMapping("/admin/getVideo")
    public Object getVideoAdmin(@RequestParam(value = "videoId") Long videoId) {
        Video video = videoRepository.findOneById(videoId);
        if (video == null)
            return new Reponse(false, "查找失败，找不到此视频记录");
        return new Reponse(true, "查找视频详细信息成功", video);
    }

    //搜索视频 && 依据标题和标签 && 分页
    @GetMapping("/admin/searchVideoPage")
    public Object searchVideoPage(@RequestParam(value = "key") String key,
                                  @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex) {
        Sort sort = new Sort(Sort.Direction.DESC, "uptime");
        Pageable pageable = new PageRequest(pageIndex, 10, sort);
        List<Video> videoList = videoRepository.findDistinctByTitleContainingOrTagsContaining(key, key,pageable).getContent();
        List<VideoUtil> videoUtilList = new ArrayList<>();
        if (videoList.size() == 0)
            return new Reponse(false, "找不到更多相关视频记录");
        for (Video video : videoList) {
            VideoUtil videoUtil = new VideoUtil(video);
            videoUtilList.add(videoUtil);
        }
        return new Reponse(true, "搜索视频信息成功，第" + pageIndex + "页", videoUtilList);
    }

    //搜索视频 && 依据标题和标签
    @GetMapping("/admin/searchVideo")
    public Object searchVideo(@RequestParam(value = "key") String key) {
        List<Video> videoList = videoRepository.findDistinctByTitleContainingOrTagsContaining(key, key);
        List<VideoUtil> videoUtilList = new ArrayList<>();
        if (videoList.size() == 0)
            return new Reponse(false, "找不到更多相关视频记录");
        for (Video video : videoList) {
            VideoUtil videoUtil = new VideoUtil(video);
            videoUtilList.add(videoUtil);
        }
        return new Reponse(true, "搜索视频信息成功", videoUtilList);
    }

    //查找审核未通过的视频  && 分页
    @GetMapping("/admin/getBadVideo")
    public Object getBadVideo(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex) {
        Sort sort = new Sort(Sort.Direction.DESC, "uptime");
        Pageable pageable = new PageRequest(pageIndex, 10, sort);
        List<Video> videoList = videoRepository.findAllByExaminationFalse(pageable).getContent();
        List<VideoUtil> videoUtilList = new ArrayList<>();
        if (videoList.size() == 0)
            return new Reponse(false, "找不到更多相关视频记录");
        for (Video video : videoList) {
            VideoUtil videoUtil = new VideoUtil(video);
            videoUtilList.add(videoUtil);
        }
        return new Reponse(true, "查找审核未通过的视频成功，第" + pageIndex + "页", videoUtilList);
    }

    //添加视频(上传视频)
    //调用VideoController中的/videoUpload接口
    @PostMapping("/admin/uploadVideo")
    public Object uploadVideo(@RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam("tags") String tags) {
        String test=tags+"/"+title+"/"+description;
        file.isEmpty();
        return new Reponse(true,"请调用'/videoUpload'接口,just kidding!",test);
    }

    //删除视频
    @DeleteMapping("/admin/deleteVideo")
    public Object deleteVideo(@RequestParam("videoId") Long videoId) {
        Object object = videoService.deleteVideoAdmin(videoId);
        if (object == null) {
            return new Reponse(true, "删除视频成功");
        }
        return new Reponse(false, "删除视频失败 "+object);
    }

    //修改视频
    @PostMapping("/admin/changeVideo")
    public Object changeVideo(@RequestParam("videoId") Long videoId,
                              @RequestParam(value = "title",required = false,defaultValue = "null") String title,
                              @RequestParam(value = "description",required = false,defaultValue = "null") String description,
                              @RequestParam(value = "tags",required = false,defaultValue = "null") String tags) {

        Video video=videoRepository.findOneById(videoId);
        if (video == null)
            return new Reponse(false, "视频修改失败，未找到此视频");

        if(!tags.equals("null"))
            video.setTags(tags);
        if(!description.equals("null"))
            video.setDescription(description);
        if(!title.equals("null"))
            video.setTitle(title);

        Video rs=videoService.saveOrUpdate(video);
        return new Reponse(true,"视频修改成功",new VideoUtil(rs));
    }

    //视频审核通过
    @PostMapping("/admin/checkVideo")
    public Object checkVideo(@RequestParam("videoId") Long videoId) {
        Video video = videoService.queryVideoById(videoId);
        if (video == null)
            return new Reponse(false, "视频审核不通过，未找到此视频");
        video.setExamination(true);
        videoService.saveOrUpdate(video);
        return new Reponse(true, "视频审核通过", new VideoUtil(video));
    }

    //视频审核不通过
    @PostMapping("/admin/checkVideoFalse")
    public Object checkVideoFalse(@RequestParam("videoId") Long videoId) {
        Video video = videoService.queryVideoById(videoId);
        if (video == null)
            return new Reponse(false, "视频审核不通过，未找到此视频");
        video.setExamination(false);
        videoService.saveOrUpdate(video);
        return new Reponse(true, "视频审核不通过", new VideoUtil(video));
    }

    //有关用户的统计数据
    @GetMapping("/admin/userData")
    public Object userNumber() {
        UserDataUtil userDataUtil=new UserDataUtil();
        userDataUtil.setTotal(userRepository.count());
        userDataUtil.setROLE_ADMIN(userRepository.countDistinctByPermissionEquals("ROLE_ADMIN"));
        userDataUtil.setROLE_USER(userRepository.countDistinctByPermissionEquals("ROLE_USER"));
        return new Reponse(true,"获取有关用户的统计数据",userDataUtil);
    }

    //有关试题的统计数据
    @GetMapping("/admin/choiceData")
    public Object choiceData() {
        DataUtil dataUtil=new DataUtil();
        Long choiceNumber=choiceRepository.count() ;
        Integer a=choiceRepository.countDistinctByTypeContaining("水产");
        Integer b=choiceRepository.countDistinctByTypeContaining("养殖");
        Integer c=choiceRepository.countDistinctByTypeContaining("农耕");
        long d=choiceNumber-a-b-c;
        dataUtil.setTotal(choiceNumber);
        dataUtil.setAquaculture(a);
        dataUtil.setCultivation(b);
        dataUtil.setGrow(c);
        dataUtil.setOthers(d);
        return new Reponse(true,"获取有关试题的统计数据",dataUtil);
    }

    //有关文件的统计数据
    @GetMapping("/admin/docData")
    @ResponseBody
    public Object docData() {
        DataUtil dataUtil=new DataUtil();
        Long docNumber=docRepository.count();
        Integer a=docRepository.countDistinctByTitleContaining("水产");
        Integer b=docRepository.countDistinctByTitleContaining("养殖");
        Integer c=docRepository.countDistinctByTitleContaining("农耕");
        long d=docNumber-a-b-c;
        dataUtil.setTotal(docNumber);
        dataUtil.setAquaculture(a);
        dataUtil.setCultivation(b);
        dataUtil.setGrow(c);
        dataUtil.setOthers(d);
        return new Reponse(true,"获取有关文件的统计数据",dataUtil);
    }
    //有关话题的统计数据
    @GetMapping("/admin/talkData")
    @ResponseBody
    public Object talkData() {
        DataUtil dataUtil=new DataUtil();
        Long talkNumber=talkRepository.count() ;
        Integer a=talkRepository.countDistinctByTagsContaining("水产");
        Integer b=talkRepository.countDistinctByTagsContaining("养殖");
        Integer c=talkRepository.countDistinctByTagsContaining("农耕");
        long d=talkNumber-a-b-c;
        dataUtil.setTotal(talkNumber);
        dataUtil.setAquaculture(a);
        dataUtil.setCultivation(b);
        dataUtil.setGrow(c);
        dataUtil.setOthers(d);
        return new Reponse(true,"获取有关话题的统计数据",dataUtil);
    }
    //有关视频的统计数据
    @GetMapping("/admin/videoData")
    @ResponseBody
    public Object videoData() {
        DataUtil dataUtil=new DataUtil();
        Long videoNumber=videoRepository.count() ;
        Integer c=videoRepository.countDistinctByTagsContaining("农耕");
        Integer a=videoRepository.countDistinctByTagsContaining("水产");
        Integer b=videoRepository.countDistinctByTagsContaining("养殖");
        long d=videoNumber-a-b-c;
        dataUtil.setTotal(videoNumber);
        dataUtil.setAquaculture(b);
        dataUtil.setCultivation(a);
        dataUtil.setGrow(c);
        dataUtil.setOthers(d);
        return new Reponse(true,"获取有关视频的统计数据",dataUtil);
    }

    //获取过去一个月内的用户数据
    @GetMapping("/admin/data")
    public Object data() {
        List<UserData> userDataList=userDataRepository.findAll();
        return new Reponse(true,"获取过去一个月内的用户数据",userDataList);
    }
}
