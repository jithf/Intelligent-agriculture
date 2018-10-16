package jit.hf.agriculture.controller;

import jit.hf.agriculture.Repository.VideoRepository;
import jit.hf.agriculture.Service.EsVideoService;
import jit.hf.agriculture.Service.UserDataService;
import jit.hf.agriculture.Service.UserService;
import jit.hf.agriculture.Service.VideoService;
import jit.hf.agriculture.domain.*;
import jit.hf.agriculture.vo.Reponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.OneToOne;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Author: zj
 * Description:视频上传与下载模块
 * Date: Created in 下午1:39 18-3-26
 **/


@RestController
public class VideoController {

    // 文件最大500M
    private static long upload_maxsize = 500 * 1024 * 1024;

    @Autowired
    VideoService videoService;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    EsVideoService esVideoService;
    @Autowired
    UserService userService;
    @Autowired
    UserDataService userDataService;
    @Value( "${upload.filepath}" )
    private String filepath;

    //String filepath = "./src/main/webapp/";//视频地址的存放目录，根据不同电脑服务器，要自行修改！！！

    //用户视频上传接口。文件上传到本地（也就是nginx服务器，因为目录相同），附带转码功能
    @PostMapping("/videoUpload")
    public Object videoUpload(@RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam("tags") String tags) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        //User user=userService.getUserByUsername(username);
        Video video = new Video();
        String fileName = file.getOriginalFilename().toString();//获取视频文件名

        final SimpleDateFormat sDateFormate = new SimpleDateFormat("yyyymmddHHmmss");  //设置时间格式
        String nowTimeStr = sDateFormate.format(new Date()); // 当前时间
        fileName=fileName.substring(0,fileName.indexOf("."))+nowTimeStr+fileName.substring(fileName.lastIndexOf("."));

        boolean bflag = false;
        if (file.getSize() != 0 && !file.isEmpty()) {//如果视频文件非空
            bflag = true;
            // 判断文件大小
            if (file.getSize() <= upload_maxsize) {//判断文件大小是否超载
                bflag = true;
                if (videoService.checkFileType( fileName )) {// 文件类型判断
                    bflag = true;
                } else {
                    bflag = false;
                    System.out.println( "文件类型不允许" );
                }
            } else {
                bflag = false;
                System.out.println( "文件大小超范围" );
            }
        } else {
            bflag = false;
            System.out.println( "文件为空" );
        }

        if (bflag) {//若上传的文件符合以上条件,则保存文件至服务器中
            try {
                BufferedOutputStream out = new BufferedOutputStream(
                        //C:\IDEA_mode_project\agriculture\src\main
                        new FileOutputStream( new File( filepath + fileName ) ) );//保存源文件到目录下
                // 获得文件扩展名
                String fileEnd = videoService.getFileExt(fileName);
                //System.out.println( fileEnd );
                // 新的文件名   给新文件命名
                String newFileName = videoService.getName(fileName);
                //System.out.println( newFileName );
                out.write( file.getBytes() );
                out.flush();
                out.close();
                String filename = filepath + fileName;
                video.setAvater( filename );
                video.setTitle( title );
                video.setAuthor( author );
                video.setAuthor_picture( userService.getUserByUsername( author ).getAvater() );
                video.setDescription( description );
                video.setTags(tags);

                // 转码Avi
                if (videoService.checkMediaType(fileEnd)) {//如果视频文件类型可以转码
                    // 设置转换为flv格式后文件的保存路径
                    String codcFilePath =filepath + newFileName + ".flv";
                    // 获取配置的转换工具（ffmpeg.exe）的存放路径
                    //String ffmpegPath = ".\\src\\main\\tools\\ffmpeg.exe";

                    String ffmpegPath = "/usr/local/bin/ffmpeg";
                    //设置截图的存放路径
                    String mediaPicPath =filepath + newFileName +".jpg";
                    boolean flag = videoService.executeCodecs(ffmpegPath, filename, codcFilePath,mediaPicPath); //该条语句是进行视频转码操作
                    //video.setAvater(codcFilePath); //记录视频保存路径
                    video.setAvater( filename );//记录未转码的视频保存路径
                    video.setPicture( mediaPicPath); //记录视频截图保存路径
                    if (flag) {
                        videoService.saveOrUpdate(video);//修改视频信息，并保存至数据库
                        System.out.println( "success!!!" );
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return new Reponse(false,"上传失败," + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                return new Reponse(false,"上传失败," + e.getMessage());
            }

            return new Reponse(true,"上传成功",video);//若上传正确，返回视频信息

        } else {
            return new Reponse(false,"上传失败,文件类型不符或大小过大");
        }
    }

    //播放视频
    @GetMapping("/playVideo")
    public Object playVideo(@RequestParam("videoId") Long id) {
        Video video=videoService.queryVideoById(id);
        String username = video.getAuthor();
        User user = userService.getUserByUsername( username );
        video.setAuthor_picture(user.getAvater());
        if (video == null) {
            return new Reponse(false,"播放视频失败，找不到该视频");}
        else {
            userDataService.editVideoClicks();
            videoService.clicksIncrease(id);
            return new Reponse(true,"播放视频成功",video);}
    }

    //添加视频评论
    @PostMapping("/addVideoComment")
    public Object addVideoComment(@RequestParam("videoId") Long videoId,
                                  @RequestParam("comment") String comment) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment resultComment;
        try {
           resultComment=videoService.createComments(videoId, comment,user);
        } catch (Exception e) {
            return new Reponse(false, "videoId不存在"+e.getMessage());
        }
        userDataService.editVideoComments();
        return new Reponse(true, "评论成功", resultComment);
    }

    //回复视频评论
    @PostMapping("/replayVideoComment")
    public Object replayVideoComment(@RequestParam("commentId") Long commentId,
                                  @RequestParam("replayComment") String replayComment) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment resultComment;
        try {
            resultComment=videoService.replayComments(commentId,replayComment,user);
        } catch (Exception e) {
            return new Reponse(false, "commentId不存在"+e.getMessage());
        }
        userDataService.editVideoComments();
        return new Reponse(true, "回复评论成功", resultComment);
    }

    //获取评论列表
    @GetMapping("/getComment")
    public Object listComments(@RequestParam(value="videoId") Long videoId) {
        Video video = videoService.queryVideoById(videoId);
        List<Comment> commentList = video.getComment();
        return new Reponse(true,"获取评论列表",commentList);
    }

    //删除评论
    @DeleteMapping("/deleteVideoComment")
    public Object deleteVideoComment(@RequestParam("commentId") String commentIds,
                                     @RequestParam("videoId") String videoIds) {

        Long videoId=Long.valueOf(videoIds);
        Long commentId=Long.valueOf(commentIds);
        boolean isOwner = false;
        UserInformation user = videoService.getCommentById(commentId).getUserInfo();

        // 判断操作用户是否是博客的所有者
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }

        if (!isOwner) {
            return new Reponse(false, "没有操作权限");
        }

        try {
            videoService.removeComment(videoId,commentId);
        } catch (Exception e) {
            return new Reponse(false, e.getMessage());
        }

        return new Reponse(true, "删除评论成功", null);
    }

    //用户视频获取 &&& 最热排序 &&& 视频已通过审核 &&& 每次返回4条数据
    @GetMapping("/sortHot")
    public Object sortHot(@RequestParam(value = "pageIndex",required = false,defaultValue = "0")int pageIndex) {
        Sort sort = new Sort(Sort.Direction.DESC, "clicks");
        Pageable pageable = new PageRequest(pageIndex, 4, sort);
        List<Video> videos = videoService.findAll(pageable).getContent();
        if (videos.size() == 0) {
            return new Reponse(false, "没有更多视频了 Hot");
        } else {
            return new Reponse(true, "搜索视频 Hot", videos);
        }
    }

     //用户视频获取 &&& 最新排序 &&&视频已通过审核 &&& 每次返回4条数据
    @GetMapping("/sortLatest")
    public Object sortLatest(@RequestParam(value = "pageIndex",required = false,defaultValue = "0")int pageIndex) {
            Sort sort = new Sort(Sort.Direction.DESC, "uptime");
            Pageable pageable = new PageRequest(pageIndex, 4, sort);
            List<Video> videos = videoService.findAll(pageable).getContent();
            if (videos.size() == 0) {
                return new Reponse(false, "没有更多视频了 NEW");
            } else {
                return new Reponse(true, "搜索视频 NEW", videos);
            }
        }

    //用户界面 &&& 获取当前用户上传视频的记录（包括通过未审核和通过审核）
    @GetMapping("/personalVideo")
    public Object personalVideo() {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        List<Video> list = videoService.getVideoByAuthor( author );
        return new Reponse(true,
                " 获取当前用户上传视频的记录成功！！！", list);
    }

    //管理员界面 &&& 视频列表的获取，为视频审核功能做铺垫
        @GetMapping("/adminGetVideo")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public Object adminGetVideo() {
        List<Video> list = videoRepository.findAll();
            return new Reponse(true,"管理员界面 &&& 获取所有视频列表成功",list);
        }
    //管理员界面 &&& 审核通过
        @PostMapping("/adminAudit_s")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public Object adminAudit_s(@RequestParam("videoId") Long videoId) {
        Video video = videoService.queryVideoById(videoId);
        video.setExamination(true);
        videoService.saveOrUpdate( video );
            return new Reponse(true,"审核通过",video);
        }
    //管理员界面 &&& 审核不通过，驳回视频
        @PostMapping("/adminAudit_f")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public Object adminAudit_f(@RequestParam("videoId") Long videoId) {
        Video video = videoService.queryVideoById(videoId);
        video.setExamination(false);
        videoService.saveOrUpdate( video );
        return new Reponse(true,"审核不通过",video);
    }
    //判断该用户是否对该视频已经点赞
    @GetMapping("/isVote")
    public Object isVote(@RequestParam("videoId") Long videoId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isExist=videoService.hasVote(videoId,user);
        if(isExist) {
            Vote vote=videoService.searchVote(videoId,user);
            return new Reponse(true,"已经点赞",vote);
        }else{
            return new Reponse(false,"未点赞");
        }
    }

    //发表点赞
    @PostMapping("/addVote")
    // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public Object createVote(@RequestParam("videoId") Long videoId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            videoService.createVote(videoId,user);
        } catch (IllegalArgumentException e) {
            return new Reponse(false, e.getMessage());
        } catch (Exception e) {
            return new Reponse(false, e.getMessage());
        }
        Vote vote=videoService.searchVote(videoId,user);
        //return new Reponse(true, "点赞成功",vote);

        return new Reponse(true, "点赞成功", vote);
    }

    //删除点赞
    @DeleteMapping("/deleteVote")
    // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public Object delete(@RequestParam("videoId") Long videoId,
                         @RequestParam("voteId") Long voteId) {

        boolean isOwner = false;
        User user = videoService.getVoteById(voteId).getUser();

        // 判断操作用户是否是点赞的所有者
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }

        if (!isOwner) {
            return new Reponse(false, "没有操作权限");
        }

        try {
            videoService.removeVote(videoId, voteId);
        } catch (Exception e) {
            return new Reponse(false, e.getMessage());
        }

        return new Reponse(true, "取消点赞成功", null);
    }

    //搜索（以最新顺序排序前4）
    @GetMapping("/searchNew4Video")
    public Object searchNewVideo(@RequestParam(value = "keyword",required=false,defaultValue = "") String keyWord) {
        Pageable pageable=new PageRequest(0,4);
        List<EsVideo> videos = esVideoService.listNewestEsVideo(keyWord,pageable).getContent();
        if (videos.size()==0) {
            return new Reponse(false,"没有更多视频了 New");
        } else {
            return new Reponse(true, "搜索视频 New", videos);
        }
    }

    //搜索（以最热顺序排序前4）
    @GetMapping("/searchHot4Video")
    public Object searchHotVideo(@RequestParam(value = "keyword",required=false,defaultValue = "") String keyWord) {
        Pageable pageable=new PageRequest(0,4);
        List<EsVideo> videos = esVideoService.listHotestEsVideo(keyWord,pageable).getContent();
        if (videos.size()==0) {
            return new Reponse(false,"没有更多视频了 Hot");
        } else {
            return new Reponse(true, "搜索视频 Hot", videos);
        }
    }

    //搜索（以最新顺序分页搜索，每次返回4条数据）
    @GetMapping("/searchNewVideo")
    public Object searchNewVideo(@RequestParam(value = "keyword",required=false,defaultValue = "") String keyWord,
                                 @RequestParam(value = "pageIndex",required = false,defaultValue = "0") int pageIndex) {
        Pageable pageable=new PageRequest(pageIndex,4);
        List<EsVideo> videos = esVideoService.listNewestEsVideo(keyWord,pageable).getContent();
        if (videos.size()==0) {
            return new Reponse(false,"没有更多视频了 New");
        } else {
            return new Reponse(true, "搜索视频 New", videos);
        }
    }

    //搜索（以最热顺序分页搜索，每次返回4条数据）
    @GetMapping("/searchHotVideo")
    public Object searchHotVideo(@RequestParam(value = "keyword",required=false,defaultValue = "") String keyWord,
                                 @RequestParam(value = "pageIndex",required = false,defaultValue = "0") int pageIndex) {
        Pageable pageable=new PageRequest(pageIndex,4);
        List<EsVideo> videos = esVideoService.listHotestEsVideo(keyWord,pageable).getContent();
        if (videos.size()==0) {
            return new Reponse(false,"没有更多视频了 Hot");
        } else {
            return new Reponse(true, "搜索视频 Hot", videos);
        }
    }

    //搜索（以最新顺序分页搜索，返回所有数据）
    @GetMapping("/searchNewVideoAll")
    public Object searchNewVideoAll(@RequestParam(value = "keyword",required=false,defaultValue = "") String keyWord) {
        int pagesize=0;
        Pageable pageable=new PageRequest(pagesize,20);
        List<EsVideo> videos=null;
        List<EsVideo> allVideo=new ArrayList<EsVideo>();
        do {
            videos = esVideoService.listNewestEsVideo(keyWord, pageable).getContent();
            allVideo.addAll(videos);
            pageable=new PageRequest(pagesize++,20);
        }while (videos.size()>=20);
        if(allVideo.size()==0) {
            return new Reponse(false,"没有更多视频了 New");
        } else {
            return new Reponse(true, "搜索所有视频 New", allVideo);
        }
    }

    //搜索（以最热顺序分页搜索，返回所有数据）
    @GetMapping("/searchHotVideoAll")
    public Object searchHOTVideoAll(@RequestParam(value = "keyword",required=false,defaultValue = "") String keyWord) {
        int pagesize=0;
        Pageable pageable=new PageRequest(pagesize,20);
        List<EsVideo> videos=null;
        List<EsVideo> allVideo=new ArrayList<EsVideo>();
        do {
            videos = esVideoService.listHotestEsVideo(keyWord, pageable).getContent();
            allVideo.addAll(videos);
            pageable=new PageRequest(pagesize++,20);
        }while (videos.size()>=20);

        if(allVideo.size()==0) {
            return new Reponse(false,"没有更多视频了 Hot");
        } else {
            return new Reponse(true, "搜索所有视频 Hot", allVideo);
        }
    }

    //收藏视频
    @PostMapping("/collectVideo")
    public Object addWrongChoice(@RequestParam("videoId") Long videoId) {
        Object result=videoService.collectVideo(videoId);
        if (result!=null){
            return new Reponse(false, "收藏视频失败",result);
        }
        return new Reponse(true, "成功收藏该视频",result);
    }

    //获取所有收藏的视频
    @GetMapping("/getVideoCollection")
    public Object getVideoCollection() {
        return new Reponse(true,"获取所有收藏的视频",videoService.getVideoCollection());
    }

    //删除收藏的视频
    @DeleteMapping("/deleteVideoCollection")
    public Object deleteWrong(@RequestParam("videoId") Long videoId) {
        Object result=videoService.deleteVideoCollection(videoId);
        if (result!=null){
            return new Reponse(false, "删除收藏视频失败",result);
        }
        return new Reponse(true, "成功删除该收藏视频",result);
    }

    //判断该用户是否收藏过该视频
    @GetMapping("/isVideoCollected")
    public Object isVideoCollected(@RequestParam("videoId") Long videoId) {
        if(videoService.isVideoCollected(videoId)) {
            return new Reponse(true,"该用户已收藏该视频");
        }

        return new Reponse(false,"该用户未收藏该视频");
    }

    //分类（以最新顺序，每次返回4条数据）
    @GetMapping("/classifyNew")
    public Object classifyNewVideo(@RequestParam(value = "keyword",required=false,defaultValue = "") String keyWord,
                                 @RequestParam(value = "pageIndex",required = false,defaultValue = "0") int pageIndex) {
        Pageable pageable=new PageRequest(pageIndex,4);
        List<EsVideo> videos = esVideoService.classNewestEsVideo(keyWord,pageable).getContent();
        if (videos.size()==0) {
            return new Reponse(false,"没有更多视频了 New");
        } else {
            return new Reponse(true, "分类视频 New", videos);
        }
    }

    //分类（以最热顺序，每次返回4条数据）
    @GetMapping("/classifyHot")
    public Object classifyHotVideo(@RequestParam(value = "keyword",required=false,defaultValue = "") String keyWord,
                                 @RequestParam(value = "pageIndex",required = false,defaultValue = "0") int pageIndex) {
        Pageable pageable=new PageRequest(pageIndex,4);
        List<EsVideo> videos = esVideoService.classHotestEsVideo(keyWord,pageable).getContent();
        if (videos.size()==0) {
            return new Reponse(false,"没有更多视频了 Hot");
        } else {
            return new Reponse(true, "分类视频 Hot", videos);
        }
    }

    //分类（以最新顺序，返回所有数据）
    @GetMapping("/classifyNewAll")
    public Object classifyNewVideoAll(@RequestParam(value = "keyword",required=false,defaultValue = "") String keyWord) {
        int pagesize=0;
        Pageable pageable=new PageRequest(pagesize,20);
        List<EsVideo> videos=null;
        List<EsVideo> allVideo=new ArrayList<EsVideo>();
        do {
            videos = esVideoService.classNewestEsVideo(keyWord, pageable).getContent();
            allVideo.addAll(videos);
            pageable=new PageRequest(pagesize++,20);
        }while (videos.size()>=20);
        if(allVideo.size()==0) {
            return new Reponse(false,"没有更多视频了 New");
        } else {
            return new Reponse(true, "分类 所有视频 New", allVideo);
        }
    }

    //搜索（以最热顺序分页搜索，返回所有数据）
    @GetMapping("/classifyHotAll")
    public Object classHOTVideoAll(@RequestParam(value = "keyword",required=false,defaultValue = "") String keyWord) {
        int pagesize=0;
        Pageable pageable=new PageRequest(pagesize,20);
        List<EsVideo> videos=null;
        List<EsVideo> allVideo=new ArrayList<EsVideo>();
        do {
            videos = esVideoService.classHotestEsVideo(keyWord, pageable).getContent();
            allVideo.addAll(videos);
            pageable=new PageRequest(pagesize++,20);
        }while (videos.size()>=20);

        if(allVideo.size()==0) {
            return new Reponse(false,"没有更多视频了 Hot");
        } else {
            return new Reponse(true, "分类 所有视频 Hot", allVideo);
        }
    }

//    //删除视频
//    @DeleteMapping("/deleteVideo")
//    public Object deleteVideo(@RequestParam("videoId") Long videoId) {
//        Object object=videoService.deleteVideo(videoId);
//        if(object==null) {
//            return new Reponse(true, "删除视频");
//        }
//        return new Reponse(false,"删除视频失败",object);
//    }
}









