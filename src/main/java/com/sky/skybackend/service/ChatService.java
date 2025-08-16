package com.sky.skybackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.skybackend.domain.pojo.Message;
import com.sky.skybackend.domain.pojo.Result;
import com.sky.skybackend.domain.vo.ChatUserVO;

import java.util.List;

public interface ChatService extends IService<Message> {
    List<ChatUserVO> getChatUserList(Integer targetId);

    List<Message> getChatMessageList(Integer targetId);

    Result clearUnread(Integer id);
}
