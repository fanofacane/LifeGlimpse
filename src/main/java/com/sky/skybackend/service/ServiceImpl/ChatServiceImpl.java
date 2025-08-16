package com.sky.skybackend.service.ServiceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.skybackend.domain.pojo.Message;
import com.sky.skybackend.domain.pojo.Result;
import com.sky.skybackend.domain.vo.ChatUserVO;
import com.sky.skybackend.mapper.ChatMapper;
import com.sky.skybackend.mapper.FollowMapper;
import com.sky.skybackend.service.ChatService;
import com.sky.skybackend.utils.CurrentHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Message> implements ChatService {
    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    private FollowMapper followMapper;
    @Override
    public List<ChatUserVO> getChatUserList(Integer targetId) {
        Integer currentId = CurrentHolder.getCurrentId();
        if (currentId == null) return List.of();
        List<Integer> followingIds = followMapper.getFollowIdList(currentId);
        List<Integer> chatHistoryIds=chatMapper.getChatHistoryIds(currentId);
        //私信id
        chatHistoryIds.add(targetId);
        //stream流去重
        List<Integer> chatUserIdList= Stream.concat(followingIds.stream(),chatHistoryIds.stream()).distinct().collect(Collectors.toList());
        if (chatUserIdList.isEmpty()) return List.of();
        // 3. 一次性获取所有聊天用户及其详细信息
        return chatMapper.getChatUsersWithFullDetails(chatUserIdList, currentId);
    }

    @Override
    public List<Message> getChatMessageList(Integer targetId) {
        Integer currentId = CurrentHolder.getCurrentId();
        if (currentId == null) return List.of();
        List<Message> messageList = chatMapper.getChatMessageList(currentId, targetId);
        if (messageList == null) return List.of();
        return messageList;
    }

    @Override
    public Result clearUnread(Integer id) {
        chatMapper.clearUnread(id,CurrentHolder.getCurrentId());
        return null;
    }
}
