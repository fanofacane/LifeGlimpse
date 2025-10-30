package com.sky.skybackend.controller;


import com.sky.skybackend.Enum.Constant;
import com.sky.skybackend.domain.dto.CommentDTO;
import com.sky.skybackend.domain.dto.VideoDTO;
import com.sky.skybackend.domain.pojo.Result;
import com.sky.skybackend.domain.pojo.UserAction;
import com.sky.skybackend.domain.vo.CommentVO;
import com.sky.skybackend.service.CommentService;
import com.sky.skybackend.service.ServiceImpl.SlidingWindowTagScorerService;
import com.sky.skybackend.service.VideoService;
import com.sky.skybackend.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController {
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private VideoService videoService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private SlidingWindowTagScorerService slidingWindowTagScorerService;
    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws Exception {
        //MinIO对象存储
        String url = minioUtil.uploadFile("origin",file);
        System.out.println("文件路径"+url);
        return Result.success(url);
    }

    /**
     * 发布作品
     * @param videoDTO 视频内容
     * @return
     */
    @PostMapping("/public")
    public Result publicVideo(@RequestBody VideoDTO videoDTO) {
        videoService.insert(videoDTO);
        return Result.success("发布成功");
    }

    /**
     * 记录点赞收藏等行为
     * @param userAction 用户行为
     * @return
     */
    @PostMapping("/action")
    public Result action(@RequestBody UserAction userAction) {
        slidingWindowTagScorerService.recordUserAction(userAction);
        return Result.success("用户行为记录成功");
    }

    /**
     * 发布评论
     * @param commentDTO 评论内容
     * @return List<CommentVO>
     */
    @PostMapping("/comment")
    public Result comment(@RequestBody CommentDTO commentDTO) {
        CommentVO vos=commentService.comment(commentDTO);
        return Result.success(vos);
    }

    /**
     * 获取评论
     * @param videoId 视频id
     * @param userId 用户id
     * @return List<CommentVO>
     */
    @GetMapping("/getComments")
    public Result getComments(@RequestParam Integer videoId, @RequestParam(required = false) Integer userId) {
        return Result.success(commentService.getComments(videoId, userId));
    }

    /**
     * 获取推荐视频
     * @param userId 用户id
     * @param cursor 游标
     * @param size 每页数量
     * @return List<VideoVO>
     */
    @GetMapping("/recommend")
    public Result getRecommendVideos(@RequestParam(required = false) Integer userId,
                          @RequestParam(required = false) Integer cursor,
                          @RequestParam(defaultValue = Constant.DEFAULT_VIDEOS_COUNT) int size) {
        return Result.success(videoService.queryMoreVideo(userId, cursor, size));
    }

    /**
     * 模糊查询
     * @param key 关键字
     * @param userId 用户id
     * @return List<VideoVO>
     */
    @GetMapping("/match")
    public Result getMatchVideo(@RequestParam String key,@RequestParam(required = false) Integer userId) {
        return Result.success(videoService.getMatchVideo(key,userId));
    }

    /**
     * 获取点赞、收藏等视频列表
     * @param userId 用户id
     * @param type 类型
     * @return List<VideoVO>
     */
    @GetMapping("/getVideoByActionType")
    public Result getVideoByActionType(@RequestParam Integer userId,@RequestParam int type) {
        return Result.success(videoService.getVideoByActionType(userId,type));
    }
    @GetMapping("/getVideoByUserId")
    public Result getVideoByUserId(@RequestParam Integer userId) {
        return Result.success(videoService.getVideoByUserId(userId));
    }

    /**
     * 获取好友动态视频
     * @param count  数量
     * @param lastTimestamp 游标时间戳
     * @return List<VideoVO>
     */
    @GetMapping("/dynamics")
    public Result getInboxVideos(@RequestParam(defaultValue = Constant.DEFAULT_VIDEOS_COUNT) Integer count,
                                  @RequestParam(required = false) Long lastTimestamp){
        return Result.success(videoService.getInboxVideos(count,lastTimestamp));
    }
    /**
     * 获取热度排行榜
     * @return List<HotVideo>
     */
    @GetMapping("/getHotVideos")
    public Result getHotRank(){
        return Result.success(videoService.getHotRank());
    }
    /**
     * 获取热度视频
     * @return List<HotVideo>
     */
    @GetMapping("/getHotVideo")
    public Result getHotVideo(){
        return Result.success(videoService.getHotVideos());
    }
}
