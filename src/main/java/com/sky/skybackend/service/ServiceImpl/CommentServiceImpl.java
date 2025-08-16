package com.sky.skybackend.service.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.sky.skybackend.domain.dto.CommentDTO;
import com.sky.skybackend.domain.pojo.Comment;
import com.sky.skybackend.domain.pojo.UserAction;
import com.sky.skybackend.domain.vo.CommentVO;
import com.sky.skybackend.mapper.CommentMapper;
import com.sky.skybackend.mapper.UserActionMapper;
import com.sky.skybackend.mapper.UserMapper;
import com.sky.skybackend.mapper.VideoMapper;
import com.sky.skybackend.service.CommentService;
import com.sky.skybackend.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserActionMapper userActionMapper;
    @Override
    public CommentVO comment(CommentDTO commentDTO) {
        Comment comment = BeanUtil.copyProperties(commentDTO, Comment.class);
        if (comment.getParentId()!=null) commentMapper.incrementCount(comment.getParentId());
        videoMapper.increCommentCount(comment.getVideoId());
        save(comment);
        return getCompleteComment(comment);
    }

    @Override
    public List<CommentVO> getComments(Integer videoId, Integer userId) {
        List<CommentVO> topComments = commentMapper.getTopLevelComments(videoId);
        topComments.forEach(comment ->{
            Map<String, String> nicknameAvatar = userMapper.getNicknameAvatar(comment.getUserId());
            comment.setNickName(nicknameAvatar.get("nick_name"));
            comment.setAvatar(nicknameAvatar.get("avatar"));
            comment.setIsLike(userActionMapper.checkLike(comment.getId(), userId));
            comment.setReplies(getNestedReplies(comment.getId()));
        });
        return topComments;
    }

    private CommentVO getCompleteComment(Comment comment) {
        CommentVO commentVO = BeanUtil.copyProperties(comment, CommentVO.class);
        Map<String, String> nicknameAvatar = userMapper.getNicknameAvatar(comment.getUserId());
        commentVO.setNickName(nicknameAvatar.get("nick_name"));
        commentVO.setAvatar(nicknameAvatar.get("avatar"));
        commentVO.setCreateTime(LocalDateTime.now());
        return commentVO;
    }
    // 递归获取嵌套回复
    private List<CommentVO> getNestedReplies(Integer parentId) {
        List<CommentVO> replies = commentMapper.getRepliesByParentId(parentId);
        replies.forEach(reply ->{
            Map<String, String> nicknameAvatar = userMapper.getNicknameAvatar(reply.getUserId());
            reply.setNickName(nicknameAvatar.get("nick_name"));
            reply.setAvatar(nicknameAvatar.get("avatar"));
            reply.setReplies(getNestedReplies(reply.getId()));
        });
        return replies;
    }
}
