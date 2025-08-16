package com.sky.skybackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.skybackend.domain.dto.CommentDTO;
import com.sky.skybackend.domain.pojo.Comment;
import com.sky.skybackend.domain.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {
    CommentVO comment(CommentDTO commentDTO);

    List<CommentVO> getComments(Integer videoId, Integer userId);
}
