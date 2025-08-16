package com.sky.skybackend.domain.vo;

import com.sky.skybackend.domain.pojo.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class VideoVO extends Video {
    private String nickName;
    private String avatar;
    private Boolean isCollect = false;
    private Boolean isFollow = false;
    private Boolean isLike = false;
}
