package com.sky.skybackend.controller;


import com.sky.skybackend.Enum.Constant;
import com.sky.skybackend.domain.dto.CommentDTO;
import com.sky.skybackend.domain.dto.VideoDTO;
import com.sky.skybackend.domain.pojo.Result;
import com.sky.skybackend.domain.pojo.UserAction;
import com.sky.skybackend.domain.pojo.Video;
import com.sky.skybackend.domain.vo.CommentVO;
import com.sky.skybackend.service.CommentService;
import com.sky.skybackend.service.ServiceImpl.SlidingWindowTagScorerService;
import com.sky.skybackend.service.VideoService;
import com.sky.skybackend.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
        return Result.success(url);
    }
    @PostMapping("/public")
    public Result publicVideo(@RequestBody VideoDTO videoDTO) {
            videoService.insert(videoDTO);
        return Result.success("发布成功");
    }
    @PostMapping("/action")
    public Result action(@RequestBody UserAction userAction) {
        slidingWindowTagScorerService.recordUserAction(userAction);
        return Result.success("用户行为记录成功");
    }
    @PostMapping("/comment")
    public Result comment(@RequestBody CommentDTO commentDTO) {
        CommentVO vos=commentService.comment(commentDTO);
        return Result.success(vos);
    }
    @PostMapping("/getComments")
    public Result getComments(@RequestParam Integer videoId, @RequestParam(required = false) Integer userId) {
        return Result.success(commentService.getComments(videoId, userId));
    }
    @GetMapping("/recommend")
    public Result getRecommendVideos(@RequestParam(required = false) Integer userId,
                          @RequestParam(required = false) Integer cursor,
                          @RequestParam(defaultValue = Constant.DEFAULT_VIDEOS_COUNT) int size) {
        return Result.success(videoService.queryMoreVideo(userId, cursor, size));
    }
    @GetMapping("/match")
    public Result getMatchVideo(@RequestParam String key,@RequestParam(required = false) Integer userId) {
        return Result.success(videoService.getMatchVideo(key,userId));
    }
    @GetMapping("/getVideoByActionType")
    public Result getVideoByActionType(@RequestParam Integer userId,@RequestParam int type) {
        return Result.success(videoService.getVideoByActionType(userId,type));
    }
    @GetMapping("/dynamics")
    public Result getInboxVideos(@RequestParam(defaultValue = Constant.DEFAULT_VIDEOS_COUNT) Integer count,
                                  @RequestParam(required = false) Long lastTimestamp){
        return Result.success(videoService.getInboxVideos(count,lastTimestamp));
    }
}
