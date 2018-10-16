package jit.hf.agriculture.Service;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
//import com.webapp.entity.Media;
import jit.hf.agriculture.domain.Comment;
import jit.hf.agriculture.domain.User;
import jit.hf.agriculture.domain.Video;
import jit.hf.agriculture.domain.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Author: zj
 */
public interface VideoService {
    //新增、编辑、保存视频
    Video saveOrUpdate(Video video);
    //根据视频标题获取视频
    Video getVideoByTitle(String title);
    //根据视频作者获取视频
    List<Video> getVideoByAuthor(String author);
    //找出表中所有视频记录并排序
    Page<Video> findAll(Pageable pageable);

    //上传文件类型判断
    boolean checkFileType(String fileName);

    //转码视频类型判断(flv)
    boolean checkMediaType(String fileEnd);

    // 上传视频文件类型判断(AVI)
    boolean checkAVIType(String fileEnd);

    //获取文件扩展名
    String getFileExt(String fileName);


    //依据原始文件名生成新文件名  UUID 唯一标识
    String getName(String fileName);

    //获取文件大小，返回kb.mb
    String getSize(File file);

    /**
     * 视频转码
     * @param ffmpegPath    转码工具的存放路径
     * @param upFilePath    用于指定要转换格式的文件,要截图的视频源文件
     * @param codcFilePath  格式转换后的的文件保存路径
     * @param mediaPicPath   截图保存路径
     * @return
     * @throws Exception
     */
     boolean executeCodecs(String ffmpegPath,String upFilePath,
                                 String codcFilePath, String mediaPicPath);

    //发表评论
    Comment createComments(Long videoId, String commentContent, User user);

    //回复评论
    Comment replayComments(Long commentId,String commentContent,User user);
    //根据Id查询视频
    Video queryVideoById(Long id);

    //点击量递增
    void clicksIncrease(Long videId);

    //获取某一评论信息
    public Comment getCommentById(Long commentId);

    //删除评论
    public void removeComment(Long videoId,Long commentId);

    //获取某一点赞信息××
    Vote getVoteById(Long id);

    //删除点赞××
    void removeVote(Long videoId, Long voteId);

    //发表点赞××
    Video createVote(Long videoId,User user);

    //判断是否已点赞
    boolean hasVote(Long videoId,User user);

    //查找点赞id
    Vote searchVote(Long videoId,User user);

    //删除视频
    Object deleteVideo(Long videoId);

    //收藏视频
    Object collectVideo(Long videoId);

    //获取所有收藏的视频
    Object getVideoCollection();

    //删除收藏视频
    Object deleteVideoCollection(Long videoId);

    //判断该用户是否收藏过该视频
    Boolean isVideoCollected(Long videoId);

    Object deleteVideoCollectionAdmin(Long videoId,User user);

    //管理员删除视频
    Object deleteVideoAdmin(Long videoId);

//    /**
//     * 保存文件
//     * @param video
//     * @return
//     * @throws Exception
//     */
//    public boolean saveVideo(Video video)throws Exception;
//
//    /**
//     * 查询本地库中所有记录的数目
//     * @return
//     * @throws Exception
//     */
//    public int getAllVideoCount()throws Exception;
//
//    /**
//     * 带分页的查询
//     * @param firstResult
//     * @param maxResult
//     * @return
//     */
//    public List<Video> queryALlVideo(Long firstResult, Long maxResult)throws Exception;
//
//    /**
//     * 根据Id查询视频
//     * @param id
//     * @return
//     * @throws Exception
//     */
//    public Video queryVideoById(Long id)throws Exception;

}
