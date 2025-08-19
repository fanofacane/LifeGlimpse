package com.sky.skybackend.controller;

import com.sky.skybackend.domain.pojo.Result;
import com.sky.skybackend.domain.pojo.Video;
import com.sky.skybackend.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private ChatService chatService;

    /**
     * 获取聊天列表
     * @param targetId 用户ID
     * @return List<ChatUserVO>
     */
    @GetMapping("/userList")
    public Result getChatUserList(@RequestParam(required = false) Integer targetId){
        return Result.success(chatService.getChatUserList(targetId));
    }

    /**
     * 获取聊天记录
     * @param targetId 用户ID
     * @return List<message>
     */
    @GetMapping("/chatRecord/{targetId}")
    public Result getChatMessageList(@PathVariable Integer targetId){
        return Result.success(chatService.getChatMessageList(targetId));
    }

    /**
     * 清空未读
     * @param id 用户ID
     * @return
     */
    @PostMapping("/clearUnread/{id}")
    public Result clearUnread(@PathVariable Integer id){
        return chatService.clearUnread(id);
    }

    /**
     * AI对话
     * @param msg 消息
     * @param userId 用户ID
     * @return string
     */
    @GetMapping(value = "/AIService",produces = "text/html;charset=utf-8")
    public String chatAI(@RequestParam String msg,@RequestParam Integer userId){
        return chatClient.prompt()
                .user(msg)
                .advisors(a-> a.param(chatMemory.CONVERSATION_ID,userId))
                .call()
                .content();
    }

}
