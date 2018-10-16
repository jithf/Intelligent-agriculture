package jit.hf.agriculture.controller;

import jit.hf.agriculture.Repository.*;
import jit.hf.agriculture.Service.TalkService;
import jit.hf.agriculture.Service.UserDataService;
import jit.hf.agriculture.Service.UserService;
import jit.hf.agriculture.domain.*;
import jit.hf.agriculture.vo.Reponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.OneToMany;
import java.io.*;
import java.util.*;

/**
 * Author: zj
 * Description:任务广场模块功能实现
 */
@RestController
public class TalkController {
    @Autowired
    UserService userService;

    @Autowired
    TalkService talkService;

    @Autowired
    TalkRepository talkRepository;

    @Autowired
    CommentTalkRepository commentTalkRepository;

    @Autowired
    CommentSecondTalkRepository commentSecondTalkRepository;

    @Autowired
    AttentionTalkRepository attentionTalkRepository;

    @Autowired
    VoteTalkRepository voteTalkRepository;

    @Autowired
    VoteSecondTalkRepository voteSecondTalkRepository;

    @Autowired
    EsTalkRepository esTalkRepository;

    @Autowired
    UserDataService userDataService;

    @Value( "${upload.filepath}" )
    private String filepath;

    @Value( "${nginx.url}" )
    private String fileUrl;

    /**
     * 发布话题(web端),配合富文本使用
     * @param theme
     * @param tags
     * @return
     */
    @PostMapping("/postTalk")
    public Object postTalk(@RequestParam("theme") String theme,
                               @RequestParam("description") String description,
                               @RequestParam("tags") String tags,
                                @RequestParam("talkValues") Integer talkValues) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername( author );
        String author_picture = user.getAvater();

        //该用户消耗积分
        Integer num =user.getUserTalkValues()-talkValues;
        if (num < 0) {
            return new Reponse( false,"积分不够！！！，请充值");
        }
        user.setUserTalkValues( num );
        userService.saveOrUpdate( user );

        Talk talk = new Talk();
        talk.setTheme(theme);
        talk.setDescription( description );
        talk.setAuthor( author );
        talk.setAuthor_picture( author_picture );
        talk.setTags( tags );
        talk.setTalkValues( talkValues );
        talk.setUptime( new Date() );
        Talk talkResult=talkService.saveOrUpdateQuestion( talk );//保存talk
        //接下来将talk传到文本库中，以供搜索功能使用（文本库新增记录）
        EsTalk esTalk = new EsTalk(talkResult);
        esTalkRepository.save( esTalk );
        return  new Reponse( true,"发布问题成功！", talk );
    }

    /**
     * 发布话题&&&安卓端&&&多图片上传
     * @param theme
     * @param description
     * @param tags
     * @param talkValues
     * @return
     */
    @PostMapping("/postTalk_android")
    public Object postTalkAndroid(@RequestParam("theme") String theme,
                           @RequestParam("description") String description,
                           @RequestParam("tags") String tags,
                           @RequestParam("talkValues") Integer talkValues,
                                  @RequestParam("file") MultipartFile files[] ){
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername( author );
        String author_picture = user.getAvater();

        StringBuilder picture_url = new StringBuilder();
        StringBuilder picture_name = new StringBuilder();

        for (MultipartFile file:files) {
            try {
                String fileName = file.getOriginalFilename();
                System.out.println( fileName );

                String uniqueFileName = getName( fileName );//生成独特的文件名
                System.out.println( uniqueFileName );

                BufferedOutputStream out = new BufferedOutputStream(
                        //C:\IDEA_mode_project\agriculture\src\main
                        new FileOutputStream( new File( filepath + uniqueFileName + ".jpg" ) ) );//保存图片到目录下,建立保存文件的输入流
                out.write( file.getBytes() );
                out.flush();
                out.close();

                String picturePath = fileUrl + uniqueFileName + ".jpg";
                System.out.println( picturePath );
                picture_url.append( "#" ).append( picturePath );//字符串拼接
                picture_name.append( "#" ).append( fileName );
            } catch (IOException e) {
                e.printStackTrace();
                return new Reponse(false,"上传失败," + e.getMessage());
                //return "上传失败," + e.getMessage();  //文件IO错误
            }
        }
        System.out.println( picture_url.toString() );
        System.out.println( picture_name.toString() );

        //该用户消耗积分
        Integer num =user.getUserTalkValues()-talkValues;
        if (num < 0) {
            return new Reponse( false,"积分不够！！！，请充值");
        }
        user.setUserTalkValues( num );
        userService.saveOrUpdate( user );

        Talk talk = new Talk();
        talk.setTheme(theme);
        talk.setDescription( description );
        talk.setAuthor( author );
        talk.setAuthor_picture( author_picture );
        talk.setTags( tags );
        talk.setTalkValues( talkValues );
        talk.setUptime( new Date() );
        talk.setPictureName( picture_name.toString() );
        talk.setPictureUrl( picture_url.toString() );
        Talk talkResult=talkService.saveOrUpdateQuestion( talk );//保存talk
        //接下来将talk传到文本库中，以供搜索功能使用（文本库新增记录）
        EsTalk esTalk = new EsTalk(talkResult);
        esTalkRepository.save( esTalk );
        return  new Reponse( true,"发布问题成功！", talk );

    }

    /**
     * 依据原始文件名生成新文件名
     * UUID：全局唯一标识符，由一个十六位的数字组成,由三部分组成：当前日期和时间、时钟序列、全局唯一的IEEE机器识别号
     *
     * @return string
     */
    public String getName(String fileName) {
        Random random = new Random();
        return "" + random.nextInt(10000) + System.currentTimeMillis();
        //return UUID.randomUUID().toString() + "_" + fileName;
    }

    /**
     * 获取话题列表
     * @return
     */
    @GetMapping("/showTalk")
    public Object showTalk() {
        List<Talk> talkList = talkRepository.findAll();
        return new Reponse(true,"获取话题列表",talkList);
    }

    /**
     * 判断话题，当前用户是否已关注
     * @param id
     * @return
     */
    @GetMapping("/isAttentionTalk")
    public Object isAttentionTalk(@RequestParam("talkId") Long id) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername( author );
        String author_picture = user.getAvater();

        Talk talk =talkRepository.findOneById( id );
        List<AttentionTalk> attentionTalkList=talk.getAttentionTalks();

        AttentionTalk attentionTalk = attentionTalkRepository.findOneByAuthor( author );

        boolean state =attentionTalkList.contains( attentionTalk );//包含相同的元素

        if(state) {
            return new Reponse( true,"对于一级评论，用户已关注",talk);
        }
        else {

            return new Reponse( false,",对于一级评论，用户没关注",talk);
        }
    }


    /**
     * 关注 话题
     * @return
     */
    @GetMapping("/payAttentionTalk")
    public Object payAttentionTalk(@RequestParam("talkId") Long id) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername( author );
        String author_picture = user.getAvater();

        Talk talk = talkRepository.findOneById( id );

        List<AttentionTalk>attentionTalkList = talk.getAttentionTalks();
        AttentionTalk attentionTalk1 =attentionTalkRepository.findOneByAuthor( author);
        boolean state = attentionTalkList.contains( attentionTalk1 );
        if(state) {
            return new Reponse( false,"已关注");
        }

        AttentionTalk attentionTalk = new AttentionTalk();
        attentionTalk.setAuthor( author );
        attentionTalk.setAuthor_picture( author_picture );
        AttentionTalk result = attentionTalkRepository.save( attentionTalk );

        List<AttentionTalk> attentionTalkResult = talk.getAttentionTalks();
        attentionTalkResult.add( result );
        talk.setAttentionTalks( attentionTalkResult );
        talk.setCollections( talk.getCollections()+1 );
        talkService.saveOrUpdateQuestion( talk );
        return new Reponse( true,"关注成功",talk);
    }
    /**
     * 取消关注
     * @param talkId
     * @return
     */
    @DeleteMapping("/deleteAttentionTalk")
    public Object deleteAttentionTalk(@RequestParam("talkId") Long talkId) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        Talk talk = talkRepository.findOneById( talkId );
        List<AttentionTalk>attentionTalkList1= talk.getAttentionTalks();
        AttentionTalk attentionTalk1 = attentionTalkRepository.findOneByAuthor( author );
        boolean state = attentionTalkList1.contains( attentionTalk1 );
        if(!state) {
            return new Reponse( false,"用户还未关注！" );
        }
        AttentionTalk attentionTalk = attentionTalkRepository.findOneByAuthor( author );

        List<AttentionTalk> attentionTalkList = talk.getAttentionTalks();
        talk.setCollections( talk.getCollections()-1 );
        talkRepository.save( talk );
        attentionTalkList.remove( attentionTalk );//先删除外键关联

        attentionTalkRepository.delete( attentionTalk );//再删除外键关联对应的记录

        return new Reponse( true,"取消关注成功！",talk );
    }

    /**
     * 写回答（一级评论）
     * @return
     */
    @PostMapping("/addCommentTalk")
    public Object addCommentTalk(@RequestParam("content") String content,
                                 @RequestParam("talkId") Long talkId) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername( author );
        String author_picture = user.getAvater();

        Talk talk = talkRepository.findOneById( talkId );

        CommentTalk commentTalk = new CommentTalk();
        commentTalk.setContent( content );
        commentTalk.setAuthor( author );
        commentTalk.setAuthor_picture( author_picture );
        commentTalk.setUptime( new Date(  ) );
        //commentTalk.setComments( commentTalk.getComments()+1);
        CommentTalk result =commentTalkRepository.save( commentTalk );


        List<CommentTalk> commentTalkResult = talk.getCommentTalk();

        commentTalkResult.add( result );
        talk.setCommentTalk( commentTalkResult );
        talk.setComments( talk.getComments()+1);

        talkService.saveOrUpdateQuestion( talk );
        userDataService.editTalkData();

        return new Reponse( true,"评论成功！！！",talk);
    }

    /**
     * 写二级评论
     * @param content
     * @param commentId
     * @return
     */
    @PostMapping("/addCommentSecondTalk")
    public Object addCommentSecondTalk(@RequestParam("content") String content,
                                 @RequestParam("commentId") Long commentId) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername( author );
        String author_picture = user.getAvater();

        CommentTalk commentTalk = commentTalkRepository.findOneById(commentId);

        CommentSecondTalk commentSecondTalk = new CommentSecondTalk();
        commentSecondTalk.setContent( content );
        commentSecondTalk.setAuthor( author );
        commentSecondTalk.setAuthor_picture(author_picture);
        commentSecondTalk.setUptime( new Date(  ) );
        CommentSecondTalk result =commentSecondTalkRepository.save( commentSecondTalk );

        List<CommentSecondTalk> commentSecondTalkResult = commentTalk.getCommentSecondTalk();

        commentSecondTalkResult.add( result );
        commentTalk.setCommentSecondTalk( commentSecondTalkResult );
        commentTalk.setComments( commentTalk.getComments()+1 );
        commentTalkRepository.save( commentTalk );
        userDataService.editTalkData();
        return new Reponse( true,"发表二级评论成功！！！",commentTalk);
    }

    /**
     * 对于一级评论，判断是否当前用户点过赞
     * @param commentTalkId
     * @return
     */
    @GetMapping("/isVoteTalk")
    public Object isVoteTalk(@RequestParam("commentTalkId") Long commentTalkId) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername( author );
        String author_picture = user.getAvater();

        CommentTalk commentTalk = commentTalkRepository.findOneById( commentTalkId );
        List<VoteTalk> voteTalkList = commentTalk.getVoteTalk();
        VoteTalk voteTalk =voteTalkRepository.findOneByAuthor(author);
        boolean state = voteTalkList.contains( voteTalk );

        if(!state) {
            return new Reponse( false,",对于一级评论，用户没点赞",commentTalk);
        }
        else {
            return new Reponse( true,"对于一级评论，用户点过赞了",commentTalk );
        }
    }
    /**
     * 对于二级评论，判断是否当前用户点过赞
     * @param commentSecondTalkId
     * @return
     */
    @GetMapping("/isVoteSecondTalk")
    public Object isVoteSecondTalk(@RequestParam("commentSecondTalkId") Long commentSecondTalkId) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername( author );
        String author_picture = user.getAvater();

        CommentSecondTalk commentSecondTalk = commentSecondTalkRepository.findOneById( commentSecondTalkId );
        List<VoteSecondTalk>voteSecondTalkList =commentSecondTalk.getVoteSecondTalk();
        VoteSecondTalk voteSecondTalk = voteSecondTalkRepository.findOneByAuthor(author);
        boolean state = voteSecondTalkList.contains( voteSecondTalk );

        if(!state) {
            return new Reponse( false,",对于二级评论，用户没点赞",commentSecondTalk);
        }
        else {
            return new Reponse( true,"对于二级评论，用户点过赞了",commentSecondTalk );
        }

    }

    /**
     * 对于一级评论，点赞
     * @param commentTalkId
     * @return
     */
    @GetMapping("/addVoteTalk")
    public Object addVoteTalk(@RequestParam("commentTalkId") Long commentTalkId) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername( author );
        String author_picture = user.getAvater();

        CommentTalk commentTalk= commentTalkRepository.findOneById( commentTalkId );
        List<VoteTalk>voteTalkList =commentTalk.getVoteTalk();
        VoteTalk voteTalk1 =voteTalkRepository.findOneByAuthor( author );
        boolean state = voteTalkList.contains( voteTalk1 );

        if (state) {
            return new Reponse( false,"对于一级评论,已经点过赞了" );
        }

        VoteTalk voteTalk = new VoteTalk();
        voteTalk.setAuthor( author );
        voteTalk.setAuthor_picture( author_picture );
        voteTalk.setUptime( new Date(  ) );
        VoteTalk result =voteTalkRepository.save( voteTalk );

        List<VoteTalk> voteTalkResult =commentTalk.getVoteTalk();
        voteTalkResult.add( result );
        commentTalk.setVoteTalk( voteTalkResult );
        commentTalk.setLikes( commentTalk.getLikes()+1 );
        commentTalkRepository.save( commentTalk );
        return new Reponse( true,"对于一级评论，点赞成功",commentTalk );
    }

    /**
     * 对于一级评论，取消点赞
     * @param commentTalkId
     * @return
     */
    @DeleteMapping("/deleteVoteTalk")
    public Object deleteVoteTalk(@RequestParam("commentTalkId") Long commentTalkId) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        CommentTalk commentTalk= commentTalkRepository.findOneById( commentTalkId );
        List<VoteTalk> voteTalkList1 = commentTalk.getVoteTalk();
        VoteTalk voteTalk1 = voteTalkRepository.findOneByAuthor( author );
        boolean state =voteTalkList1.contains( voteTalk1 );

        if(!state) {
            return new Reponse( false,"对于一级评论，还未点赞" );
        }

        VoteTalk voteTalk = voteTalkRepository.findOneByAuthor( author );

        List<VoteTalk> voteTalkList =commentTalk.getVoteTalk();
        commentTalk.setLikes( commentTalk.getLikes()-1 );
        commentTalkRepository.save( commentTalk );
        voteTalkList.remove( voteTalk );

        voteTalkRepository.delete(voteTalk);
        return new Reponse( true,"对于一级评论,取消点赞成功！" ,commentTalk);
    }

    /**
     * 对于二级评论，点赞
     * @param commentSecondTalkId
     * @return
     */
    @GetMapping("/addVoteSecondTalk")
    public Object addVoteSecondTalk(@RequestParam("commentSecondTalkId") Long commentSecondTalkId) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername( author );
        String author_picture = user.getAvater();

        CommentSecondTalk commentSecondTalk= commentSecondTalkRepository.findOneById( commentSecondTalkId );
        List<VoteSecondTalk> voteSecondTalkList = commentSecondTalk.getVoteSecondTalk();
        VoteSecondTalk voteSecondTalk1 = voteSecondTalkRepository.findOneByAuthor( author );
        boolean state = voteSecondTalkList.contains( voteSecondTalk1 );

        if (state) {
            return new Reponse( false,"对于二级评论,已经点过赞了" );
        }

        VoteSecondTalk voteSecondTalk = new VoteSecondTalk();
        voteSecondTalk.setAuthor( author );
        voteSecondTalk.setAuthor_picture( author_picture );
        voteSecondTalk.setUptime( new Date(  ) );
        VoteSecondTalk result =voteSecondTalkRepository.save( voteSecondTalk );

        List<VoteSecondTalk> voteSecondTalkResult =commentSecondTalk.getVoteSecondTalk();
        voteSecondTalkResult.add( result );
        commentSecondTalk.setVoteSecondTalk( voteSecondTalkResult );
        commentSecondTalk.setLikes( commentSecondTalk.getLikes()+1 );
        commentSecondTalkRepository.save( commentSecondTalk );
        return new Reponse( true,"对于二级评论，点赞成功",commentSecondTalk );
    }
    /**
     * 对于二级评论，取消点赞
     * @param commentSecondTalkId
     * @return
     */
    @DeleteMapping("/deleteVoteSecondTalk")
    public Object deleteVoteSecondTalk(@RequestParam("commentSecondTalkId") Long commentSecondTalkId) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        CommentSecondTalk commentSecondTalk= commentSecondTalkRepository.findOneById( commentSecondTalkId );
        List<VoteSecondTalk> voteSecondTalkList1 = commentSecondTalk.getVoteSecondTalk();
        VoteSecondTalk voteSecondTalk1 =voteSecondTalkRepository.findOneByAuthor( author );
        boolean state = voteSecondTalkList1.contains( voteSecondTalk1 );
        if(!state) {
            return new Reponse( false,"对于二级评论，还未点赞" );
        }

        VoteSecondTalk voteSecondTalk = voteSecondTalkRepository.findOneByAuthor( author );

        List<VoteSecondTalk> voteSecondTalkList =commentSecondTalk.getVoteSecondTalk();
        commentSecondTalk.setLikes( commentSecondTalk.getLikes()-1 );

        voteSecondTalkList.remove( voteSecondTalk );

        voteSecondTalkRepository.delete(voteSecondTalk);
        return new Reponse( true,"对于二级评论,取消点赞成功！" ,commentSecondTalk);
    }

    /**
     * 获取话题的最热排序
     * @return
     */
    @GetMapping("/sortHotTalk")
    public Object sortHotTalk() {
        Sort sort = new Sort(Sort.Direction.DESC, "clicks");
        List<Talk> list = talkRepository.findAll( sort );
        return new Reponse( true,"获取话题的最热排序成功" ,list);
    }

    /**
     * 获取话题的最新排序
     * @return
     */
    @GetMapping("/sortNewTalk")
    public Object sortNewTalk() {
        Sort sort = new Sort(Sort.Direction.DESC, "uptime");
        List<Talk> list = talkRepository.findAll( sort );
        return new Reponse( true,"获取话题的最新排序成功" ,list);
    }

    /**
     * 获取指定话题的一级评论的最热排序
     * @return
     */
    @GetMapping("/sortHotCommentTalk")
    public Object sortHotCommentTalk(@RequestParam("TalkId") Long id) {
        //Sort sort = new Sort(Sort.Direction.DESC, "likes");
        Talk talk = talkRepository.findOneById( id );
        List<CommentTalk> commentTalkList = talk.getCommentTalk();
        commentTalkList.sort( (o1, o2) -> {
            if (o1.getLikes() < o2.getLikes()) { //降序排序
                return 1;//调这个排序，规则
            }
            if (o1.getLikes() == o2.getLikes()) {
                return 0;
            }
            return -1;//  >  是升序
        } );
        return new Reponse( true,"获取指定话题的一级评论的最热排序成功",commentTalkList);
    }
    /**
     * 获取指定话题的一级评论的最新排序
     * @return
     */
    @GetMapping("/sortNewCommentTalk")
    public Object sortNewCommentTalk(@RequestParam("TalkId") Long id) {
        Talk talk = talkRepository.findOneById( id );
        List<CommentTalk> commentTalkList = talk.getCommentTalk();
        commentTalkList.sort( (o1, o2) -> {
            if (o1.getUptime().getTime() < o2.getUptime().getTime()) { //降序排序
                return 1;//调这个排序，规则
            }
            if (o1.getLikes() == o2.getLikes()) {
                return 0;
            }
            return -1;//  >  是升序
        } );
        return new Reponse( true,"获取指定话题的一级评论的最新排序成功",commentTalkList);
    }

    /**
     * 搜索所有符合条件的话题
     * @param keyWord
     * @return
     */
    @GetMapping("/searchTalkAll")
    public Object searchTalkAll(@RequestParam(value = "keyword",required=false,defaultValue = "") String keyWord) {
        List<EsTalk> esTalkList =esTalkRepository.findDistinctEsTalkByThemeContainingOrTagsContaining( keyWord,keyWord );
        if(esTalkList.isEmpty()) {
            return new Reponse( false,"没有更多话题了" );
        }

        return new Reponse( true,"搜索所有符合条件的话题成功",esTalkList );
    }

    /**
     * 更新文本库,将数据库冗余数据，更新到文本库（但效率感人）
     * @return
     */
    @GetMapping("/updateEsTalk")
    public Object updateEsTalk() {
        List<Talk> talkList = talkRepository.findAll();
        for (Talk talk:talkList) {
            Long id =talk.getId();
            EsTalk esTalk = esTalkRepository.findOneById( id );
            if (esTalk == null) {
                EsTalk esTalk1 = new EsTalk( talk );
                esTalkRepository.save( esTalk1 );
            }
            else {
                esTalk.update( talk );
                esTalkRepository.save( esTalk );
            }
        }
        Iterable<EsTalk> esTalkList =esTalkRepository.findAll();
        return  new Reponse( true,"更新文本库",esTalkList);
    }

    //分页，获取话题列表
    @GetMapping("/showTalkPage")
    public Object showTalkPage(@RequestParam(value = "pageIndex",required = false,defaultValue = "0") int pageIndex) {
        Pageable pageable=new PageRequest(pageIndex,4);
        List<Talk> talkList = talkRepository.findAll(pageable).getContent();//getContent()获取page实体中的内容

        if(talkList.size()==0) {
            return new Reponse(false,"没有更多视频了");
        } else {
            return new Reponse( true, "分页，获取话题列表成功！", talkList );
        }
    }

    //分页，获取话题最热列表
    @GetMapping("/showTalkPageHot")
    public Object showTalkPageHot(@RequestParam(value = "pageIndex",required = false,defaultValue = "0") int pageIndex) {

        Sort sort = new Sort( Sort.Direction.DESC,"clicks");
        Pageable pageable=new PageRequest(pageIndex,4,sort);

        List<Talk> talkList = talkRepository.findAll(pageable).getContent();
        if(talkList.size()==0) {
            return new Reponse(false,"没有更多话题了");
        } else {
            return new Reponse( true, "分页，获取话题最热列表成功！", talkList );
        }
    }

    //分页，获取话题最新列表
    @GetMapping("/showTalkPageNew")
    public Object showTalkPageNew(@RequestParam(value = "pageIndex",required = false,defaultValue = "0") int pageIndex) {

        Sort sort = new Sort( Sort.Direction.DESC,"uptime");
        Pageable pageable=new PageRequest(pageIndex,4,sort);

        List<Talk> talkList = talkRepository.findAll(pageable).getContent();
        if(talkList.size()==0) {
            return new Reponse(false,"没有更多话题了");
        } else {
            return new Reponse( true, "分页，获取话题最新列表成功！", talkList );
        }
    }

    /**
     * 话题发布者 采纳 回答（一级评论）
     * @param talkId
     * @param commentTalkId
     * @return
     */
    @GetMapping("/acceptTalk")
    public Object acceptTalk(@RequestParam("talkId" ) Long talkId,
            @RequestParam("commentTalkId") Long commentTalkId) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户

        Talk talk = talkRepository.findOneById( talkId );
        String author1 = talk.getAuthor();//获取话题发布者
        if(!author.equals(author1)) { //判断 当前登录用户是否是该话题发布者
            return new Reponse( false,"sorry,你不是该话题的发布者，没有权限采纳" );
        }

        Integer acceptationNum = talk.getAcceptationNumbers();
        if (acceptationNum ==0) { //判断 该话题发布者是否已经采纳过意见
            return new Reponse( false,"sorry，您已经采纳过意见，该操作不可逆" );
        }

        CommentTalk commentTalk = commentTalkRepository.findOneById( commentTalkId );
        commentTalk.setAcceptance( true );
        commentTalkRepository.save( commentTalk );

        talk.setAcceptationNumbers( 0 );
        talkRepository.save( talk );

        String answer_author = commentTalk.getAuthor();
        User user = userService.getUserByUsername( answer_author );
        user.setUserTalkValues( user.getUserTalkValues()+talk.getTalkValues() );
        userService.saveOrUpdate( user );

        return new Reponse( true,"采纳意见成功",commentTalk);

    }

    /**
     * 置顶操作
     */
    @GetMapping("/talkTopComment")
    public Object talkTopComment(@RequestParam("talkId" ) Long talkId) {
        Talk talk = talkRepository.findOneById( talkId );
        List<CommentTalk> commentTalkList = talk.getCommentTalk();
        for (CommentTalk commentTalk : commentTalkList) {
            if (commentTalk.getAcceptance() == true) {
                return new Reponse( true, "置顶成功！！！", commentTalk );
            }

        }
        return new Reponse( false,"发布者还未采纳！！！");
    }

}
