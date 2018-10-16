package jit.hf.agriculture.Service;

import jit.hf.agriculture.Repository.CommentRepository;
import jit.hf.agriculture.Repository.UserRepository;
import jit.hf.agriculture.Repository.VideoRepository;
import jit.hf.agriculture.Repository.VoteRepository;
import jit.hf.agriculture.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Author: zj
 */
@Service
public class VideoServiceImpl implements VideoService {

    // 文件允许格式
    private static String[] allowFiles = {".rar", ".doc", ".docx", ".zip", ".pdf", ".txt", ".swf", ".xlsx", ".gif",
            ".png", ".jpg", ".jpeg", ".bmp", ".xls", ".mp4", ".flv", ".ppt", ".avi", ".mpg", ".wmv", ".3gp", ".mov",
            ".asf", ".asx", ".vob", ".wmv9", ".rm", ".rmvb"};
    // 允许转码的视频格式（ffmpeg）
    private static String[] allowFLV = {".avi", ".mpg", ".wmv", ".3gp", ".mp4", ".mov", ".asf", ".asx", ".vob"};
    // 允许的视频转码格式(mencoder)
    private static String[] allowAVI = {".wmv9", ".rm", ".rmvb"};

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private EsVideoService esVideoService;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public Video saveOrUpdate(Video video) {
        EsVideo esVideo = null;
        Video returnVideo = videoRepository.save(video);
        if (returnVideo.getExamination()) {
            if (esVideoService.getEsVideoByVideoId(returnVideo.getId()) == null) {
                esVideo = new EsVideo(returnVideo);
            } else {
                esVideo = esVideoService.getEsVideoByVideoId(returnVideo.getId());
                esVideo.update(returnVideo);
            }
            esVideoService.saveOrUpdateEsVideo(esVideo);
        } else if (esVideoService.getEsVideoByVideoId(returnVideo.getId()) != null) {
            esVideo = esVideoService.getEsVideoByVideoId(returnVideo.getId());
            esVideoService.removeEsVideo(esVideo.getId());
        }
        return returnVideo;
    }

    @Override
    public Video getVideoByTitle(String title) {
        return videoRepository.findOneByTitle(title);
    }

    @Override
    public List<Video> getVideoByAuthor(String author) {
        return videoRepository.findOneByAuthor(author);
    }

    @Override
    public Page<Video> findAll(Pageable pageable) {
        return videoRepository.findAllByExaminationTrue(pageable);
    }

    //上传文件类型判断
    public boolean checkFileType(String fileName) {
        Iterator<String> type = Arrays.asList(allowFiles).iterator();
        while (type.hasNext()) {
            String ext = type.next();
            if (fileName.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    //转码视频类型判断(flv)
    public boolean checkMediaType(String fileEnd) {
        Iterator<String> type = Arrays.asList(allowFLV).iterator();
        while (type.hasNext()) {
            String ext = type.next();
            if (fileEnd.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    // 上传视频文件类型判断(AVI)
    public boolean checkAVIType(String fileEnd) {
        Iterator<String> type = Arrays.asList(allowAVI).iterator();
        while (type.hasNext()) {
            String ext = type.next();
            if (fileEnd.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    // 获取文件扩展名
    public String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
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

    //获取文件大小，返回kb.mb
    public String getSize(File file) {
        String size = "";
        long fileLength = file.length();
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileLength < 1024) {
            size = df.format((double) fileLength) + "BT";
        } else if (fileLength < 1048576) {
            size = df.format((double) fileLength / 1024) + "KB";
        } else if (fileLength < 1073741824) {
            size = df.format((double) fileLength / 1048576) + "MB";
        } else {
            size = df.format((double) fileLength / 1073741824) + "GB";
        }
        return size;
    }

    /**
     * 视频转码
     *
     * @param ffmpegPath   转码工具的存放路径
     * @param upFilePath   用于指定要转换格式的文件,要截图的视频源文件
     * @param codcFilePath 格式转换后的的文件保存路径
     * @param mediaPicPath 截图保存路径
     * @return
     * @throws Exception
     */
    public boolean executeCodecs(String ffmpegPath, String upFilePath, String codcFilePath,
                                 String mediaPicPath) {
        // 创建一个List集合来保存转换视频文件为flv格式的命令
        List<String> convert = new ArrayList<String>();
        convert.add(ffmpegPath); // 添加转换工具路径
        convert.add("-i"); // 添加参数＂-i＂，该参数指定要转换的文件
        convert.add(upFilePath); // 添加要转换格式的视频文件的路径
        convert.add("-qscale");     //指定转换的质量
        convert.add("6");
        convert.add("-ab");        //设置音频码率
        convert.add("64");
        convert.add("-ac");        //设置声道数
        convert.add("2");
        convert.add("-ar");        //设置声音的采样频率
        convert.add("22050");
        convert.add("-r");        //设置帧频
        convert.add("24");
        convert.add("-y"); // 添加参数＂-y＂，该参数指定将覆盖已存在的文件
        convert.add(codcFilePath);

        // 创建一个List集合来保存从视频中截取图片的命令
        List<String> cutpic = new ArrayList<String>();
        cutpic.add(ffmpegPath);
        cutpic.add("-i");
        cutpic.add(upFilePath); // 同上（指定的文件即可以是转换为flv格式之前的文件，也可以是转换的flv文件）
        cutpic.add("-y");
        cutpic.add("-f");
        cutpic.add("image2");
        cutpic.add("-ss"); // 添加参数＂-ss＂，该参数指定截取的起始时间
        cutpic.add("1"); // 添加起始时间为第1秒
        cutpic.add("-t"); // 添加参数＂-t＂，该参数指定持续时间
        cutpic.add("0.001"); // 添加持续时间为1毫秒
        cutpic.add("-s"); // 添加参数＂-s＂，该参数指定截取的图片大小
        cutpic.add("800*280"); // 添加截取的图片大小为350*240
        cutpic.add(mediaPicPath); // 添加截取的图片的保存路径

        boolean mark = true;
        ProcessBuilder builder = new ProcessBuilder();
        try {
            //本项目视频现在不转码，故下面屏蔽掉
//            builder.command(convert);
//            builder.redirectErrorStream(true);
//            builder.start();
            builder.command(cutpic);
            builder.redirectErrorStream(true);
            // 如果此属性为 true，则任何由通过此对象的 start() 方法启动的后续子进程生成的错误输出都将与标准输出合并，
            //因此两者均可使用 Process.getInputStream() 方法读取。这使得关联错误消息和相应的输出变得更容易
            builder.start();
        } catch (Exception e) {
            mark = false;
            System.out.println(e);
            e.printStackTrace();
        }
        return mark;
    }

    //发表评论
    @Override
    public Comment createComments(Long videoId, String commentContent, User user) {
        Video video = videoRepository.findOneById(videoId);
        System.out.println(user.getUsername());
        Comment comment = new Comment(user, commentContent);
        comment.setVideoId(videoId);
        Comment resultComment = commentRepository.save(comment);
        video.addComment(resultComment);
        //更新评论量
        video.setComments(commentRepository.countByVideoId(videoId));
        saveOrUpdate(video);
        return resultComment;
    }

    //回复评论
    @Override
    public Comment replayComments(Long commentId, String commentContent, User user) {
        Comment comment = commentRepository.findOneById(commentId);  //被回复评论
        Comment replayComment = new Comment(user, commentId, commentContent); //回复评论
        if (replayComment.getPid() != 0L) {
            replayComment.setReplayUser(comment.getUserInfo());
            replayComment.setVideoId(comment.getVideoId());
        }

        if (comment.getPpid() != 0L) {
            replayComment.setPpid(comment.getPpid());
        } else {
            replayComment.setPpid(commentId);
        }

        Comment C = commentRepository.findOneById(replayComment.getPpid());
        C.getReplayCommentList().add(replayComment);
        commentRepository.save(C);
        commentRepository.save(replayComment);
        Video video = videoRepository.findOneById(comment.getVideoId());
        //更新评论量
        video.setComments(commentRepository.countByVideoId(comment.getVideoId()));
        saveOrUpdate(video);
        videoRepository.save(video);
        return replayComment;
    }

    //根据Id查询video
    @Override
    public Video queryVideoById(Long id) {
        return videoRepository.findOneById(id);
    }

    //点击量递增
    @Override
    public void clicksIncrease(Long videoId) {
        Video video = videoRepository.findOneById(videoId);
        video.setClicks(video.getClicks() + 1);
        saveOrUpdate(video);
        videoRepository.save(video);
    }

    //获取某一评论具体信息
    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findOneById(commentId);
    }

    //删除评论
    @Override
    @Transactional
    public void removeComment(Long videoId, Long commentId) {
        //先删除子评论
        Comment comment = commentRepository.findOneById(commentId);
        List<Comment> commentList = comment.getReplayCommentList();
        if (commentList != null && commentList.size() != 0) {
            for (int i = 0; i < commentList.size(); i++) {
                Comment recomment = commentList.get(i);
                commentRepository.delete(recomment.getId());
            }
        }
        //再删除该评论
        commentRepository.delete(commentId);
        Video video = videoRepository.findOneById(videoId);
        video.removeComment(commentId);
        //更新评论量
        video.setComments(commentRepository.countByVideoId(videoId));
        saveOrUpdate(video);
        videoRepository.save(video);
    }

    //获取点赞信息
    @Override
    public Vote getVoteById(Long id) {
        return voteRepository.findOne(id);
    }

    //发表点赞
    @Override
    public Video createVote(Long videoId, User user) {
        Video video = videoRepository.findOne(videoId);
        Vote vote = new Vote(user);
        boolean isExist = video.addVote(vote);
        if (isExist) {
            throw new IllegalArgumentException("该用户已经点过赞了");
        }
        saveOrUpdate(video);
        return videoRepository.save(video);
    }

    //删除点赞
    @Override
    public void removeVote(Long videoId, Long voteId) {
        Video video = videoRepository.findOne(videoId);
        video.removeVote(voteId);
        saveOrUpdate(video);
        videoRepository.save(video);
        voteRepository.delete(voteId);
    }

    //判断是否已点赞
    @Override
    public boolean hasVote(Long videoId, User user) {
        Video video = videoRepository.findOne(videoId);
        Vote vote = new Vote(user);
        boolean isExist = video.elseVote(vote);
        return isExist;
    }

    //查找点赞id
    @Override
    public Vote searchVote(Long videoId, User user) {
        Video video = videoRepository.findOne(videoId);
        Vote vote = new Vote(user);
        Vote vote1 = video.selectVote(vote);
        return vote1;
    }

    //删除视频
    @Override
    public Object deleteVideo(Long videoId) {
        Video video = videoRepository.findOneById(videoId);
        if (video.getAuthor().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            if (esVideoService.getEsVideoByVideoId(video.getId()) != null) {
                EsVideo esVideo = esVideoService.getEsVideoByVideoId(videoId);
                esVideoService.removeEsVideo(esVideo.getId());
            }
            String collector = video.getCollectors();
            if (collector != null && collector.length() != 9) {
                String[] as = collector.split(",");
                for (int i = 1; i < as.length; i++) {
                    User user = userRepository.findOneById(Long.valueOf(as[i]));
                    String a = user.getVideos();
                    String[] ras = a.split(",");
                    String rs = "collectionVideos";
                    String result = "x";
                    for (int j = 1; j < ras.length; j++) {
                        if (!Long.valueOf(as[j]).equals(videoId)) {
                            rs = rs + "," + as[j];
                        }
                    }
                    userRepository.save(user);
                }
            }
            videoRepository.delete(video.getId());
            return null;
        }

        return "权限不够，没有删除该视频的权限";
    }

    //收藏视频
    @Override
    public Object collectVideo(Long videoId) {
        Video video = videoRepository.findOneById(videoId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userRepository.findOneByUsername(username);

        boolean flag = true;
        String a = user.getVideos();
        String[] as = a.split(",");

        for (int i = 1; i < as.length; i++) {
            if (Long.valueOf(as[i]).equals(videoId)) {
                flag = false;
                return "该用户已收藏过该视频";
            }
        }

        if (flag) {
            user.setVideos(a + "," + videoId);
            String collectors = video.getCollectors();
            video.setCollectors(collectors + "," + user.getId());
            video.setCollections(video.getCollections() + 1);
            saveOrUpdate(video);
            videoRepository.save(video);
            userRepository.save(user);
        }
        return null;
    }


    //获取所有收藏的视频
    @Override
    public Object getVideoCollection() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userRepository.findOneByUsername(username);

        String a = user.getVideos();
        String[] as = a.split(",");
        List<Video> videos = new ArrayList<>();
        for (int i = 1; i < as.length; i++) {
            videos.add(videoRepository.findOneById(Long.valueOf(as[i])));
        }
        return videos;
    }

    //删除收藏视频
    @Override
    public Object deleteVideoCollection(Long videoId) {
        Video video = videoRepository.findOneById(videoId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userRepository.findOneByUsername(username);

        int i = 1;
        String a = user.getVideos();
        String[] as = a.split(",");
        String rs = "collectionVideos";

        for (i = 1; i < as.length; i++) {
            if (!Long.valueOf(as[i]).equals(videoId)) {
                rs = rs + "," + as[i];
            }
        }

        String[] s = rs.split(",");
        if (s.length == as.length) {
            return "该用户未收藏过该视频";
        }

        String collectors = video.getCollectors();
        String[] cs = collectors.split(",");
        String rcs = "collector";
        for (i = 1; i < cs.length; i++) {
            if (!cs[i].equals(user.getId().toString())) {
                rcs = rcs + "," + cs[i];
            }
        }

        user.setVideos(rs);
        video.setCollectors(rcs);
        video.setCollections(video.getCollections() - 1);
        saveOrUpdate(video);
        userRepository.save(user);
        videoRepository.save(video);
        return null;
    }

    @Override
    public Boolean isVideoCollected(Long videoId) {
        Video video = videoRepository.findOneById(videoId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userRepository.findOneByUsername(username);
        String a = user.getVideos();
        String[] as = a.split(",");
        String rs = "collectionVideos";

        for (int i = 1; i < as.length; i++) {
            if (Long.valueOf(as[i]).equals(videoId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object deleteVideoCollectionAdmin(Long videoId, User user) {
        Video video = videoRepository.findOneById(videoId);
        int i = 1;
        String collectors = video.getCollectors();
        String[] cs = collectors.split(",");
        String rcs = "collector";
        for (i = 1; i < cs.length; i++) {
            if (!cs[i].equals(user.getId().toString())) {
                rcs = rcs + "," + cs[i];
            }
        }
        video.setCollectors(rcs);
        video.setCollections(video.getCollections() - 1);
        saveOrUpdate(video);
        videoRepository.save(video);
        return null;
    }

    @Override
    public Object deleteVideoAdmin(Long videoId) {
        Video video = videoRepository.findOneById(videoId);
        if (video != null) {
            if (esVideoService.getEsVideoByVideoId(video.getId()) != null) {
                EsVideo esVideo = esVideoService.getEsVideoByVideoId(videoId);
                esVideoService.removeEsVideo(esVideo.getId());
            }

            String collector = video.getCollectors();
            if (collector != null && collector.length() != 9) {
                String[] as = collector.split(",");
                for (int i = 1; i < as.length; i++) {
                    User user = userRepository.findOneById(Long.valueOf(as[i]));
                    String a = user.getVideos();
                    String[] ras = a.split(",");
                    String rs = "collectionVideos";
                    for (int j = 1; j < ras.length; j++) {
                        if (!Long.valueOf(as[j]).equals(videoId)) {
                            rs = rs + "," + as[j];
                        }
                    }
                    userRepository.save(user);
                }
            }

            File file = new File(video.getAvater());
            file.delete();
            File file1= new File(video.getPicture());
            file1.delete();
            videoRepository.delete(video.getId());
            return null;

        }
        return "未找到此视频";
    }

//    @Override
//    public boolean saveVideo(Video video) throws Exception {
//        return false;
//    }
//
//    @Override
//    public int getAllVideoCount() throws Exception {
//        return 0;
//    }
//
//    @Override
//    public List<Video> queryALlVideo(Long firstResult, Long maxResult) throws Exception {
//        return null;
//    }
//
//    @Override
//    public Video queryVideoById(Long id) throws Exception {
//        return null;
//    }
}
