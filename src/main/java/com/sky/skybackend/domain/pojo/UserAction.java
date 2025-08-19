package com.sky.skybackend.domain.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.skybackend.Enum.ActionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_action")
public class UserAction {
    private Integer id;
    private Integer userId;
    private Integer videoId;
    private Integer type;  // 1-观看，2-点赞，3-收藏，4-分享
    private LocalDateTime createTime;
    public UserAction() {}

    public UserAction(Integer userId, Integer videoId, Integer type) {
        this.userId = userId;
        this.videoId = videoId;
        this.type = type;
        this.createTime = LocalDateTime.now();
    }
}
